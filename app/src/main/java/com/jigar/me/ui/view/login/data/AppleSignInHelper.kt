package com.jigar.me.ui.view.login.data

import android.app.Activity
import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWebException
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppleSignInHelper @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {
    sealed class Outcome {
        data class Success(val email: String?, val idToken: String, val appleUserId: String?) : Outcome()
        object Cancelled : Outcome()
        data class Failure(val throwable: Throwable) : Outcome()
    }

    suspend fun signIn(activityContext: Context): Outcome {
        return try {
            val provider = OAuthProvider.newBuilder("apple.com")
                .setScopes(listOf("email", "name"))
                .build()

            val result = FirebaseAuth.getInstance()
                .startActivityForSignInWithProvider(activityContext as Activity, provider)
                .await()

            // Apple identity token is in OAuthCredential — this matches what iOS sends to the backend
            val idToken = (result.credential as? OAuthCredential)?.idToken
                ?: return Outcome.Failure(IllegalStateException("Apple identity token missing"))

            // Extract the Apple user ID from the `sub` claim of the identity token JWT
            val appleUserId = extractSubFromJwt(idToken)

            Outcome.Success(email = result.user?.email, idToken = idToken, appleUserId = appleUserId)
        } catch (e: FirebaseAuthWebException) {
            if (e.errorCode == "ERROR_WEB_CANCELLED") Outcome.Cancelled
            else Outcome.Failure(e)
        } catch (e: Exception) {
            Outcome.Failure(e)
        }
    }

    private fun extractSubFromJwt(token: String): String? {
        return try {
            val payload = token.split(".").getOrNull(1) ?: return null
            val decoded = String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING))
            JSONObject(decoded).optString("sub").takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            null
        }
    }
}
