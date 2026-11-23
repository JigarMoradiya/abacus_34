package com.jigar.me.utils

import androidx.annotation.StringRes
import com.jigar.me.MyApplication
import com.jigar.me.R
import com.jigar.me.data.model.MainAPIResponse
import java.io.IOException
import java.util.Locale

/** An error that already carries the text for the person (in the app language), made by [ApiErrors]. */
class ApiException(message: String) : Exception(message)

/**
 * Every error the person sees comes from our own strings, chosen here by the case. The server's English text is only used to tell the
 * cases apart (it is never shown): first the error code, then the HTTP status, then the known server sentences, then a generic text.
 * The server has no codes for most of its errors yet, so those are recognised by their sentence; an unknown sentence gets [R.string.err_generic].
 */
object ApiErrors {
    /** Codes the app itself puts on the answer it builds when there was no answer (see RemoteDataSource). */
    const val CODE_NETWORK = "CLIENT_NETWORK"
    const val CODE_SERVER = "CLIENT_SERVER"

    private val byCode = mapOf(
        CODE_NETWORK to R.string.no_internet,
        CODE_SERVER to R.string.err_server,
        "USER_NOT_FOUND" to R.string.err_account_not_found,
        "USER_NOT_VERIFIED" to R.string.err_not_verified,
        "ACCESS_DENIED" to R.string.err_no_access,
        "THIS_STUDENT_IS_ASSOCIATED_WITH_OTHER_ORDER" to R.string.err_plan_problem,
    )

    /** First match wins, so the more specific sentences come first. */
    private val bySentence: List<Pair<List<String>, Int>> = listOf(
        listOf("wrong otp", "invalid otp") to R.string.err_otp_wrong,
        listOf("token expire", "otp expire") to R.string.err_otp_expired,
        listOf("already verified") to R.string.err_already_verified,
        listOf("already exists") to R.string.err_email_exists,
        listOf("not verified", "verify your email") to R.string.err_not_verified,
        listOf("credentials") to R.string.err_wrong_credentials,
        listOf("email and password", "provide password") to R.string.err_enter_email_password,
        listOf("account is deleted", "user is not active") to R.string.err_account_inactive,
        listOf("does not exist", "not found", "invalid user") to R.string.err_account_not_found,
        listOf("access denied", "permission", "do not have access") to R.string.err_no_access,
        listOf("invalid plan", "already subscribed", "select plan", "select a plan", "payment is failed", "associated with other order") to R.string.err_plan_problem,
        listOf("only png", "png/jpg") to R.string.err_image_type,
    )

    @StringRes
    fun resFor(statusCode: Int?, errorCode: String?, serverSentence: String?): Int {
        errorCode?.let { byCode[it]?.let { res -> return res } }
        if (statusCode == 401) return R.string.err_session_expired
        val sentence = serverSentence.orEmpty().lowercase(Locale.ROOT)
        if (sentence.isNotEmpty()) bySentence.firstOrNull { (words, _) -> words.any { it in sentence } }?.let { return it.second }
        return if (statusCode != null && statusCode >= 500) R.string.err_server else R.string.err_generic
    }

    private fun words(@StringRes res: Int): String = MyApplication.getInstance().inAppLanguage().getString(res)

    fun text(statusCode: Int?, errorCode: String?, serverSentence: String?): String = words(resFor(statusCode, errorCode, serverSentence))

    /** The text for an answer the server sent with status ERROR (the server's own sentence is not shown). */
    fun text(response: MainAPIResponse?): String =
        text(response?.statusCode, response?.error?.error_code, response?.error?.message ?: response?.message)

    /** The text for anything that was thrown on the way (our [ApiException] already has it, a broken connection is "no internet"). */
    fun text(throwable: Throwable?): String = when (throwable) {
        is ApiException -> throwable.message ?: words(R.string.err_generic)
        is IOException -> words(R.string.no_internet)
        else -> words(R.string.err_generic)
    }

    /** Google / Apple sign-in did not work (their own message is in the phone's language but not ours). */
    fun signInFailed(): String = words(R.string.err_sign_in_failed)
}
