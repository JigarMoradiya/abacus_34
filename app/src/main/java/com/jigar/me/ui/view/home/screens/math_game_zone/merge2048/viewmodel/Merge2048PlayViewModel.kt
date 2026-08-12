package com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.jetpack.utils.AudioPlayerManager
import com.jigar.me.ui.view.home.common_ui.enums.CommonDifficulty4
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048Config
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048Direction
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048Tile
import com.jigar.me.ui.view.home.screens.math_game_zone.merge2048.components.Merge2048UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import javax.inject.Inject

const val KEY_MERGE2048_DIFF = "merge2048_diff"

@HiltViewModel
class Merge2048PlayViewModel @Inject constructor(
    private val prefManager: AppPreferencesHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val difficulty: CommonDifficulty4 =
        savedStateHandle.get<String>(KEY_MERGE2048_DIFF)?.let { CommonDifficulty4.fromName(it) }
            ?: CommonDifficulty4.easy
    val config: Merge2048Config = Merge2048Config.forDifficulty(difficulty)
    val size: Int get() = config.size
    val target: Int get() = config.target

    private val _uiState = MutableStateFlow(Merge2048UiState(size = config.size, target = config.target))
    val uiState: StateFlow<Merge2048UiState> = _uiState

    private var nextId = 0L
    private var isAnimating = false
    private var reachedGoal = false
    private var spawnBase = 2      // lowest tile value that spawns; rises as targets are reached

    private val bestScoreKey get() = "merge2048Best_${difficulty.name}"
    val bestScore: Int get() = prefManager.getCustomParamInt(bestScoreKey, 0)
    val highestTile: Int get() = _uiState.value.tiles.maxOfOrNull { it.value } ?: 0

    fun start() {
        nextId = 0
        isAnimating = false
        reachedGoal = false
        spawnBase = 2
        _uiState.value = Merge2048UiState(size = config.size, target = config.target)
        spawnTile(); spawnTile()
    }

    private fun spawnValue(): Int = if (Random.nextInt(10) == 0) spawnBase * 2 else spawnBase

    fun dismissMilestone() { _uiState.update { it.copy(milestoneValue = null) } }

    fun move(dir: Merge2048Direction) {
        val s = _uiState.value
        if (s.isGameOver || isAnimating || s.milestoneValue != null) return

        val grid = Array(size) { arrayOfNulls<Merge2048Tile>(size) }
        for (t in s.tiles) grid[t.row][t.col] = t

        val updated = HashMap<Long, Merge2048Tile>()
        for (t in s.tiles) updated[t.id] = t.copy(isNew = false, justMerged = false)

        val absorbedIds = ArrayList<Long>()
        val doubles = HashMap<Long, Int>()
        var gained = 0
        var moved = false

        for (line in lines(dir)) {
            val lineTiles = line.mapNotNull { grid[it.first][it.second] }
            data class Seq(val survivor: Merge2048Tile, val absorbed: Merge2048Tile?, val mergedValue: Int)
            val seq = ArrayList<Seq>()
            var i = 0
            while (i < lineTiles.size) {
                if (i + 1 < lineTiles.size && lineTiles[i].value == lineTiles[i + 1].value) {
                    seq.add(Seq(lineTiles[i], lineTiles[i + 1], lineTiles[i].value * 2)); i += 2
                } else {
                    seq.add(Seq(lineTiles[i], null, lineTiles[i].value)); i += 1
                }
            }
            seq.forEachIndexed { idx, sq ->
                val (r, c) = line[idx]
                if (sq.survivor.row != r || sq.survivor.col != c) moved = true
                updated[sq.survivor.id] = updated[sq.survivor.id]!!.copy(row = r, col = c)
                if (sq.absorbed != null) {
                    updated[sq.absorbed.id] = updated[sq.absorbed.id]!!.copy(row = r, col = c)
                    absorbedIds.add(sq.absorbed.id)
                    doubles[sq.survivor.id] = sq.mergedValue
                    gained += sq.mergedValue
                    moved = true
                }
            }
        }

        if (!moved) return

        // Phase 1 — slide everything to final cells.
        isAnimating = true
        _uiState.update { st -> st.copy(tiles = st.tiles.map { updated[it.id] ?: it }) }

        // Phase 2 — drop absorbed, double survivors, spawn, check.
        viewModelScope.launch {
            delay(130)
            var next = _uiState.value.tiles.filter { it.id !in absorbedIds }
            next = next.map { if (doubles.containsKey(it.id)) it.copy(value = doubles[it.id]!!, justMerged = true) else it }

            // Reaching the target advances it, wipes the lowest tier, and raises spawns.
            var newTarget = _uiState.value.target
            var milestone: Int? = null
            while (next.any { it.value >= newTarget }) {
                reachedGoal = true
                milestone = newTarget
                newTarget *= 2
                // Wipe the lowest tier actually present on the board (not just the
                // spawn tier) and raise what spawns next, so the board keeps climbing.
                val lowest = next.minOf { it.value }
                next = next.filter { it.value != lowest }
                spawnBase = lowest * 2
            }

            val cell = randomEmptyCell(next)
            if (cell != null) {
                next = next + Merge2048Tile(nextId++, spawnValue(), cell.first, cell.second, isNew = true)
            }
            if (gained > 0) AudioPlayerManager.playSoundMergePop() else AudioPlayerManager.playSoundSwip()   // merge vs plain slide
            _uiState.update { it.copy(tiles = next, score = it.score + gained, target = newTarget, milestoneValue = milestone) }

            // Game ends only when no move is possible; otherwise persist progress.
            if (!movesAvailable(next)) endGame() else saveGame()
            isAnimating = false
        }
    }

    private fun lines(dir: Merge2048Direction): List<List<Pair<Int, Int>>> {
        val res = ArrayList<List<Pair<Int, Int>>>()
        when (dir) {
            Merge2048Direction.LEFT -> for (r in 0 until size) res.add((0 until size).map { r to it })
            Merge2048Direction.RIGHT -> for (r in 0 until size) res.add((0 until size).reversed().map { r to it })
            Merge2048Direction.UP -> for (c in 0 until size) res.add((0 until size).map { it to c })
            Merge2048Direction.DOWN -> for (c in 0 until size) res.add((0 until size).reversed().map { it to c })
        }
        return res
    }

    private fun randomEmptyCell(tiles: List<Merge2048Tile>): Pair<Int, Int>? {
        val occ = BooleanArray(size * size)
        for (t in tiles) occ[t.row * size + t.col] = true
        val free = (0 until size * size).filter { !occ[it] }
        val pick = free.randomOrNull() ?: return null
        return pick / size to pick % size
    }

    private fun spawnTile() {
        val cell = randomEmptyCell(_uiState.value.tiles) ?: return
        _uiState.update {
            it.copy(tiles = it.tiles + Merge2048Tile(nextId++, spawnValue(), cell.first, cell.second, isNew = true))
        }
    }

    private fun movesAvailable(tiles: List<Merge2048Tile>): Boolean {
        val g = Array(size) { IntArray(size) }
        for (t in tiles) g[t.row][t.col] = t.value
        for (r in 0 until size) for (c in 0 until size) {
            if (g[r][c] == 0) return true
            if (c + 1 < size && g[r][c] == g[r][c + 1]) return true
            if (r + 1 < size && g[r][c] == g[r + 1][c]) return true
        }
        return false
    }

    private fun endGame() {
        saveBestIfHigher()
        clearSave()
        _uiState.update { it.copy(isGameOver = true, didWin = reachedGoal, milestoneValue = null) }
    }

    // 2048 only "ends" when the board is completely stuck — unlike the
    // timed games, most kids just hit Back mid-game with a great score
    // still on screen. Bank it as best whenever we leave, not only then.
    private fun saveBestIfHigher() {
        val finalScore = _uiState.value.score
        if (finalScore > bestScore) prefManager.setCustomParamInt(bestScoreKey, finalScore)
    }

    override fun onCleared() {
        saveBestIfHigher()
    }

    // MARK: - Save / resume (per difficulty)

    private val gridKey get() = "merge2048Grid_${difficulty.name}"
    private val saveScoreKey get() = "merge2048SaveScore_${difficulty.name}"
    private val saveTargetKey get() = "merge2048SaveTarget_${difficulty.name}"
    private val saveBaseKey get() = "merge2048SaveBase_${difficulty.name}"
    private val saveReachedKey get() = "merge2048SaveReached_${difficulty.name}"

    fun hasSavedGame(): Boolean {
        val g = prefManager.getCustomParam(gridKey, "")
        return g.isNotEmpty() && g.split(",").any { (it.toIntOrNull() ?: 0) != 0 }
    }

    fun saveGame() {
        val st = _uiState.value
        if (st.isGameOver) return
        val grid = IntArray(size * size)
        for (t in st.tiles) grid[t.row * size + t.col] = t.value
        prefManager.setCustomParam(gridKey, grid.joinToString(","))
        prefManager.setCustomParamInt(saveScoreKey, st.score)
        prefManager.setCustomParamInt(saveTargetKey, st.target)
        prefManager.setCustomParamInt(saveBaseKey, spawnBase)
        prefManager.setCustomParamBoolean(saveReachedKey, reachedGoal)
    }

    fun clearSave() {
        prefManager.setCustomParam(gridKey, "")
    }

    fun resumeSavedGame() {
        val vals = prefManager.getCustomParam(gridKey, "").split(",").mapNotNull { it.toIntOrNull() }
        if (vals.size != size * size) { start(); return }
        spawnBase = prefManager.getCustomParamInt(saveBaseKey, 2).let { if (it > 0) it else 2 }
        reachedGoal = prefManager.getCustomParamBoolean(saveReachedKey, false)
        val savedTarget = prefManager.getCustomParamInt(saveTargetKey, config.target).let { if (it > 0) it else config.target }
        nextId = 0
        val loaded = ArrayList<Merge2048Tile>()
        for (i in vals.indices) if (vals[i] != 0) loaded.add(Merge2048Tile(nextId++, vals[i], i / size, i % size))
        isAnimating = false
        _uiState.value = Merge2048UiState(
            size = size, tiles = loaded,
            score = prefManager.getCustomParamInt(saveScoreKey, 0), target = savedTarget
        )
        if (!movesAvailable(loaded)) endGame()
    }
}
