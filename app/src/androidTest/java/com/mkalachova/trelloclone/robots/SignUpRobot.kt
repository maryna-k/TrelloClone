package com.mkalachova.trelloclone.robots

import androidx.test.espresso.matcher.ViewMatchers
import com.mkalachova.trelloclone.R

fun signUp(signUpFunction: SignUpRobot.() -> Unit) = SignUpRobot().apply { signUpFunction() }

class SignUpRobot : BaseRobot() {
    private val nameField = ViewMatchers.withId(R.id.et_name)
    private val emailField = ViewMatchers.withId(R.id.et_email_signup)
    private val passwordField = ViewMatchers.withId(R.id.et_password_signup)
    private val signUpBtn = ViewMatchers.withId(R.id.btn_sign_up)

    fun enterName(name: String) = enterText(nameField, name)

    fun enterEmail(email: String) = enterText(emailField, email)

    fun enterPassword(password: String) = enterText(passwordField, password)

    fun tapSignUpButton() = tapBy(signUpBtn)

}
