package com.jigar.me.ui.view.login.data

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jigar.me.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Replacement for the legacy GoogleSignInClient flow.
 *
 * Uses the Jetpack Credential Manager to obtain a Google id-token, then signs
 * the user in with Firebase. Returns the email + idToken needed for our
 * /social-login backend call.
 *
 * Order of attempts (so the very first tap shows an account picker, not a
 * "no credential available" error):
 *   1. GetGoogleIdOption — tries to use a previously-authorized account
 *      silently (one-tap style). If none is cached we get NoCredentialException.
 *   2. Fallback to GetSignInWithGoogleOption — always renders the
 *      "Sign in with Google" bottom-sheet account chooser.
 */
@Singleton
class GoogleSignInHelper @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {
    sealed class Outcome {
        data class Success(val email: String?, val idToken: String) : Outcome()
        object Cancelled : Outcome()
        data class Failure(val throwable: Throwable) : Outcome()
    }

    suspend fun signIn(activityContext: Context): Outcome {
        val credentialManager = CredentialManager.create(activityContext)
        val webClientId = appContext.getString(R.string.web_client_id)

        val authorizedOption: CredentialOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(true)
            .setAutoSelectEnabled(true)
            .build()

        val pickerOption: CredentialOption = GetSignInWithGoogleOption
            .Builder(webClientId)
            .build()

        return try {
            val response = try {
                credentialManager.getCredential(
                    activityContext,
                    GetCredentialRequest.Builder().addCredentialOption(authorizedOption).build()
                )
            } catch (_: NoCredentialException) {
                // First-time sign-in: nothing cached yet. Show the account picker.
                credentialManager.getCredential(
                    activityContext,
                    GetCredentialRequest.Builder().addCredentialOption(pickerOption).build()
                )
            }
            handleResponse(response)
        } catch (_: GetCredentialCancellationException) {
            Outcome.Cancelled
        } catch (e: NoCredentialException) {
            Outcome.Failure(e)
        } catch (e: GoogleIdTokenParsingException) {
            Outcome.Failure(e)
        } catch (e: Exception) {
            Outcome.Failure(e)
        }
    }

    private suspend fun handleResponse(response: GetCredentialResponse): Outcome {
        val credential = response.credential
        if (credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return Outcome.Failure(IllegalStateException("Unexpected credential type: ${credential.type}"))
        }
        val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
        val firebaseCred = GoogleAuthProvider.getCredential(googleCred.idToken, null)
        val firebaseUser = FirebaseAuth.getInstance()
            .signInWithCredential(firebaseCred).await().user
        return Outcome.Success(
            email = firebaseUser?.email ?: googleCred.id,
            idToken = googleCred.idToken
        )
    }

    suspend fun signOut() {
        runCatching {
            CredentialManager.create(appContext)
                .clearCredentialState(ClearCredentialStateRequest())
        }
        runCatching { FirebaseAuth.getInstance().signOut() }
    }
}
