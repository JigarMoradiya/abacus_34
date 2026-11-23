package com.jigar.me.ui.view.home.common_ui.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.annotation.StringRes
import com.jigar.me.R

@Parcelize
enum class CommonDifficulty4 : Parcelable {
    easy, medium, hard, veryHard;

    /** The name of the level, resolved when it is drawn so it follows the app language. */
    @get:StringRes
    val nameRes: Int
        get() = when (this) {
            easy -> R.string.g_diff_easy
            medium -> R.string.g_diff_medium
            hard -> R.string.g_diff_hard
            veryHard -> R.string.g_diff_very_hard
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