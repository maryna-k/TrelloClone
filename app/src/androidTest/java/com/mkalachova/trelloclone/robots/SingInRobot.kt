package com.mkalachova.trelloclone.robots

import androidx.test.espresso.matcher.ViewMatchers.withId
import com.mkalachova.trelloclone.R


fun signIn(signInFunction: SingInRobot.() -> Unit) = SingInRobot().apply { signInFunction() }

class SingInRobot : BaseRobot() {
    private val emailField = withId(R.id.et_email_signin)
    private val passwordField = withId(R.id.et_password_signin)
    private val signInBtn = withId(R.id.btn_sign_in)

    fun enterEmail(email: String) = enterText(emailField, email)

    fun enterPassword(password: String) = enterText(passwordField, password)

    fun tapSignInButton() = tapBy(signInBtn)

}
