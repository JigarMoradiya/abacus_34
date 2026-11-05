package com.jigar.me.ui.view.home.common_ui.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class CommonDifficulty4 : Parcelable {
    easy, medium, hard, veryHard;

    val displayName: String
        get() = when (this) {
            easy -> "Easy"
            medium -> "Medium"
            hard -> "Hard"
            veryHard -> "Very Hard"
        }

    companion object {
        fun fromName(name: String?): CommonDifficulty4 {
            return entries.firstOrDefault(
                default = easy
            ) { it.name.equals(name, ignoreCase = true) }
        }
    }
}

inline fun <T> Iterable<T>.firstOrDefault(default: T, predicate: (T) -> Boolean): T {
    for (item in this) if (predicate(item)) return item
    return default
}