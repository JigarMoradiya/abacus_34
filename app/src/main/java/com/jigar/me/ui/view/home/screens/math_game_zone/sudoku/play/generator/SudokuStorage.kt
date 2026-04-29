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
        return try {
            gson.fromJson(json, SavedSudokuGame::class.java)
        } catch (t: Throwable) { null }
    }

    fun hasSavedGame(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.contains(KEY)
    }

    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit { remove(KEY) }
    }
}