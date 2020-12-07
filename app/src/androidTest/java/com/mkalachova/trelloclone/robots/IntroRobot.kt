package com.mkalachova.trelloclone.robots

import androidx.test.espresso.matcher.ViewMatchers.withId
import com.mkalachova.trelloclone.R

fun join(joinFunction: IntroRobot.() -> Unit) = IntroRobot().apply { joinFunction() }

class IntroRobot : BaseRobot() {
    private val signInBtn = withId(R.id.btn_sign_in_intro)
    private val signUpBtn = withId(R.id.btn_sign_up_intro)

    fun waitForIntroScreen() = sleep(4000)

    fun tapSignInBtn() = tapBy(signInBtn)

    fun tapSignUpBtn() = tapBy(signUpBtn)
}