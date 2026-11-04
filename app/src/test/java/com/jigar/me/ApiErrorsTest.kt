package com.jigar.me

import com.jigar.me.utils.ApiErrors
import org.junit.Assert.assertEquals
import org.junit.Test

class ApiErrorsTest {
    private fun case(status: Int?, code: String?, sentence: String?) = ApiErrors.resFor(status, code, sentence)

    @Test fun codesComeFirst() {
        assertEquals(R.string.no_internet, case(500, ApiErrors.CODE_NETWORK, "anything"))
        assertEquals(R.string.err_server, case(500, ApiErrors.CODE_SERVER, null))
        assertEquals(R.string.err_account_not_found, case(400, "USER_NOT_FOUND", "User not found"))
        assertEquals(R.string.err_not_verified, case(400, "USER_NOT_VERIFIED", "Your account is not verified"))
        assertEquals(R.string.err_no_access, case(400, "ACCESS_DENIED", "Access Denied."))
        assertEquals(R.string.err_plan_problem, case(400, "THIS_STUDENT_IS_ASSOCIATED_WITH_OTHER_ORDER", "x"))
    }

    @Test fun anExpiredSessionIsRecognisedByTheStatus() = assertEquals(R.string.err_session_expired, case(401, null, "Invalid token. Login and try again"))

    @Test fun knownServerSentencesGetTheirOwnText() {
        assertEquals(R.string.err_wrong_credentials, case(400, null, "Problem with your credentials please try again"))
        assertEquals(R.string.err_enter_email_password, case(400, null, "Email and password are required"))
        assertEquals(R.string.err_otp_wrong, case(400, null, "Wrong OTP, verify and try again"))
        assertEquals(R.string.err_otp_wrong, case(400, null, "Invalid otp"))
        assertEquals(R.string.err_otp_expired, case(400, null, "Token expired, try generating new OTP"))
        assertEquals(R.string.err_already_verified, case(400, null, "User already verified"))
        assertEquals(R.string.err_email_exists, case(400, null, "Email Already Exists"))
        assertEquals(R.string.err_account_not_found, case(400, null, "User does not exist"))
        assertEquals(R.string.err_account_inactive, case(400, null, "Your account is deleted"))
        assertEquals(R.string.err_plan_problem, case(400, null, "Invalid plan"))
        assertEquals(R.string.err_image_type, case(400, null, "Only png/jpg/jpeg files allowed."))
        assertEquals(R.string.err_no_access, case(400, null, "You Do not have permission to  this action."))
    }

    @Test fun theSentenceIsMatchedWhateverTheCase() = assertEquals(R.string.err_account_not_found, case(400, null, "EMAIL DOES NOT EXIST, PLEASE VERIFY"))

    @Test fun everythingElseIsGenericOrServer() {
        assertEquals(R.string.err_generic, case(400, null, "Some new sentence nobody has seen"))
        assertEquals(R.string.err_generic, case(null, null, null))
        assertEquals(R.string.err_generic, case(0, null, ""))
        assertEquals(R.string.err_server, case(503, null, "Bad gateway"))
        assertEquals(R.string.err_server, case(500, null, null))
    }
}
