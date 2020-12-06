package com.mkalachova.trelloclone.robots

import androidx.test.espresso.matcher.ViewMatchers
import com.mkalachova.trelloclone.R

fun profile(profileFunction: ProfileRobot.() -> Unit) = ProfileRobot().apply { profileFunction() }

class ProfileRobot : BaseRobot() {
    private val nameInputViewMatcher = ViewMatchers.withId(R.id.et_name)
    private val updateBtnMatcher = ViewMatchers.withId(R.id.btn_update)

    fun changeName(name: String) {
        clearTextField(nameInputViewMatcher)
        enterText(nameInputViewMatcher, name)
    }

    fun tapUpdateButton() = tapBy(updateBtnMatcher)
}