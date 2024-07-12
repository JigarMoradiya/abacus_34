package com.jigar.me.ui.view.dashboard.fragments.number_puzzles

import java.util.*

class Cards(private val n: Int, private val m: Int) {
    private var board: Array<IntArray>
    val newCards: Unit
        get() {
            board = Array(n) { IntArray(m) }
            var boardX: Int
            var boardY: Int
            val total = n * m - 1
            for (i in 0 until total) {
                board[i % n][i / m] = 0
            }
            for (i in 0 until n * m) {
                boardX = (Math.random() * n).toInt()
                boardY = (Math.random() * m).toInt()
                while (board[boardX][boardY] != 0) {
                    boardX = (Math.random() * n).toInt()
                    boardY = (Math.random() * m).toInt()
                }
                board[boardX][boardY] = i
            }
        }
    val newCardsRow2Column8: Unit
        get() {
            board = Array(n) { IntArray(m) }
            var boardX: Int
            var boardY: Int
            val total = n * m - 1
            for (i in 0 until total) {
                var mTemp = 1
                if (i % m == 0) {
                    mTemp = 0
                }
                board[i % n][mTemp] = 0
            }
            for (i in 0 until n * m) {
                boardX = (Math.random() * n).toInt()
                boardY = (Math.random() * m).toInt()
                while (board[boardX][boardY] != 0) {
                    boardX = (Math.random() * n).toInt()
                    boardY = (Math.random() * m).toInt()
                }
                board[boardX][boardY] = i
            }
        }

    val newCardsLevel3: Unit
        get() {
            board = Array(n) { IntArray(m) }
            for (i in 0 until n) {
                for (j in 0 until m) {
                    board[i][j] = -1
                }
            }
            val dataList = ArrayList<String?>()
            for (i in 0..15) {
                dataList.add(i.toString() + "")
            }
            for (i in 0 until n) {
                for (j in 0 until m) {
                    if ((i != 0 || j != 2) && (i != 0 || j != 3) && (i != 4 || j != 0) && (i != 4 || j != 1)) {
                        Collections.shuffle(dataList)
                        board[i][j] = dataList[0]!!.toInt()
                        dataList.removeAt(0)
                    }
                }
            }

//            Log.e("jigar_getNewCards", "+++" + Gson().toJson(board))
        }
    private var result = false
    fun moveCards(boardX: Int, boardY: Int) {
        var X0 = -1
        var Y0 = -1
        for (i in 0 until n) for (j in 0 until m) if (board[i][j] == 0) {
            X0 = i
            Y0 = j
        }
        result = false
        if (X0 == boardX || Y0 == boardY) if (!(X0 == boardX && Y0 == boardY)) {
            if (X0 == boardX) if (Y0 < boardY) for (i in Y0 + 1..boardY) board[boardX][i - 1] =
                board[boardX][i] else for (i in Y0 downTo boardY + 1) board[boardX][i] =
                board[boardX][i - 1]
            if (Y0 == boardY) if (X0 < boardX) for (i in X0 + 1..boardX) board[i - 1][boardY] =
                board[i][boardY] else for (i in X0 downTo boardX + 1) board[i][boardY] =
                board[i - 1][boardY]
            board[boardX][boardY] = 0
            result = true
        }
    }

    fun resultMove(): Boolean {
        return result
    }

    fun finished(N: Int, M: Int): Boolean {
        var finish = false
        if (board[N - 1][M - 1] == 0) {
            var a = 0
            var b = 1
            for (i in 0 until N) for (j in 0 until M) {
                a++
                if (board[i][j] == a) b++
            }
            if (b == N * M) finish = true
        }
        return finish
    }

    fun getValueBoard(i: Int, j: Int): Int {
//        Log.e("jigar_getValueBoard", "::" + Gson().toJson(board))
        return board[i][j]
    }

    fun setValueBoard(i: Int, j: Int, value: Int) {
        board[i][j] = value
    }

    init {
        board = Array(n) { IntArray(m) }
    }
}
