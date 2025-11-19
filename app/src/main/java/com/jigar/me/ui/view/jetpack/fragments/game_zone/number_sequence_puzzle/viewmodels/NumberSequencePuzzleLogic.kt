package com.jigar.me.ui.view.jetpack.fragments.game_zone.number_sequence_puzzle.viewmodels


fun generateSolvableGrid(gridSize: Int, scrambleMoves: Int = 200): List<List<Int?>> {
    val grid = goalGrid(gridSize).map { it.toMutableList() }
    var empty = gridSize - 1 to gridSize - 1

    repeat(scrambleMoves) {
        val neighbors = listOfNotNull(
            (empty.first - 1).takeIf { it >= 0 }?.let { it to empty.second },
            (empty.first + 1).takeIf { it < gridSize }?.let { it to empty.second },
            (empty.second - 1).takeIf { it >= 0 }?.let { empty.first to it },
            (empty.second + 1).takeIf { it < gridSize }?.let { empty.first to it }
        )
        val pick = neighbors.random()
        grid[empty.first][empty.second] = grid[pick.first][pick.second]
        grid[pick.first][pick.second] = null
        empty = pick
    }

    return grid
}

fun goalGrid(gridSize: Int): List<List<Int?>> {
    var num = 1
    return List(gridSize) { r ->
        List(gridSize) { c ->
            if (r == gridSize - 1 && c == gridSize - 1) null else num++
        }
    }
}

fun moveTile(
    tiles: List<List<Int?>>,
    row: Int,
    col: Int
): Pair<List<List<Int?>>, Boolean> {
    val grid = tiles.map { it.toMutableList() }
    val emptyPos = findEmpty(grid) ?: return tiles to false
    val (er, ec) = emptyPos
    var moved = false

    if (row == er) {
        val dir = if (col < ec) 1 else -1
        var current = ec
        while (current != col) {
            val next = current - dir
            grid[er][current] = grid[er][next]
            current = next
            moved = true
        }
        grid[row][col] = null
    } else if (col == ec) {
        val dir = if (row < er) 1 else -1
        var current = er
        while (current != row) {
            val next = current - dir
            grid[current][ec] = grid[next][ec]
            current = next
            moved = true
        }
        grid[row][col] = null
    }

    return grid to moved
}

fun findEmpty(grid: List<List<Int?>>): Pair<Int, Int>? {
    for (r in grid.indices)
        for (c in grid[r].indices)
            if (grid[r][c] == null) return r to c
    return null
}

fun checkSolved(grid: List<List<Int?>>): Boolean {
    val flat = grid.flatten()
    val correct = (1 until grid.size * grid.size).map { it } + listOf(null)
    return flat == correct
}