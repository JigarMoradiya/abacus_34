package com.jigar.me.ui.view.home.screens.abacus_practice

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jigar.me.utils.LanguageManager
import java.util.Locale

/**
 * The page titles and descriptions of Abacus Practice come from the server in English ("Mental Arithmetic 12", "Big Friend (+3)").
 * Each pattern of them ("mental arithmetic #") has a translated string in [PracticePatterns]; the numbers are put back in. A text with
 * no pattern (new on the server, or only signs and digits like "+5") is shown as the server sent it, and English is always the server text.
 * New server patterns are found with tools/l10n/practice_patterns.py --check.
 */
object PracticeText {
    class Match(@StringRes val res: Int, val numbers: List<String>)

    private val spaces = Regex("\\s+")
    private val numberRun = Regex("\\d+")

    fun patternOf(text: String): String = numberRun.replace(spaces.replace(text.trim(), " "), "#").lowercase(Locale.ROOT)

    /** The translated string and the numbers of [text] in order, or null when the server text has no pattern. */
    fun match(text: String?): Match? {
        if (text.isNullOrBlank()) return null
        val res = PracticePatterns.map[patternOf(text)] ?: return null
        return Match(res, numberRun.findAll(text).map { it.value }.toList())
    }
}

/** [serverText] in the app language when it is a known pattern, else unchanged. */
@Composable
fun practiceText(serverText: String): String {
    if (LanguageManager.currentTag() == "en") return serverText
    val match = PracticeText.match(serverText) ?: return serverText
    return stringResource(match.res, *match.numbers.toTypedArray())
}
