package com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.generator

import android.content.Context
import com.google.gson.Gson
import androidx.core.content.edit
import com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SavedSudokuGame

// ---------- Storage ----------
object SudokuStorage {
    private const val PREFS = "sudoku_prefs_v1"
    private const val KEY = "saved_sudoku"
    private val gson = Gson()

    fun save(context: Context, game: SavedSudokuGame) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY, gson.toJson(game)) }
    }

    fun load(context: Context): SavedSudokuGame? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY, null) ?: return null
        val game = try {
            gson.fromJson(json, SavedSudokuGame::class.java)
        } catch (t: Throwable) { null }
        // Gson leaves a field that is missing from an old save as null, whatever the Kotlin type says. A save without its puzzle
        // or board can never be resumed (tapping "continue" on it closed the app), so it is dropped.
        val usable = game != null && runCatching { game.puzzle.size.grid; game.puzzle.difficulty.name; game.board.size }.isSuccess
        if (!usable) {
            clear(context)
            return null
        }
        return game
    }

    /** True only for a save that can really be resumed (an old or damaged one is not offered). */
    fun hasSavedGame(context: Context): Boolean = load(context) != null

    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit { remove(KEY) }
    }
}