package com.jigar.me.utils

/**
 * Reads an enum from a navigation argument without throwing. Case and underscores are ignored ("easy", "EASY", "veryHard" and
 * "VERY_HARD" all find their constant), and anything unknown or missing gives [default]: a screen that is opened with an old or
 * hand-written route must open on a sensible setting, not close the app.
 */
inline fun <reified T : Enum<T>> enumFromArg(raw: String?, default: T): T {
    val key = raw?.replace("_", "") ?: return default
    return enumValues<T>().firstOrNull { it.name.replace("_", "").equals(key, ignoreCase = true) } ?: default
}
