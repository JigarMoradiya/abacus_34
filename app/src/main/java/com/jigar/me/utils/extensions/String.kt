package com.jigar.me.utils.extensions


fun String.sumToIntList(): List<Int> {
    val regex = Regex("[-+]?\\d+")
    return regex.findAll(this)
        .map { it.value.toInt() }
        .toList()
}

