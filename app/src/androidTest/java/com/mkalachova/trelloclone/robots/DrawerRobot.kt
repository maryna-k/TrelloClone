package com.mkalachova.trelloclone.robots

import androidx.test.espresso.matcher.ViewMatchers
import com.mkalachova.trelloclone.R

fun drawer(drawerFunction: DrawerRobot.() -> Unit) = DrawerRobot().apply { drawerFunction() }

class DrawerRobot : BaseRobot() {
    private val myProfileViewMatcher = ViewMatchers.withId(R.id.nav_my_profile)
    private val userNameViewMatcher = ViewMatchers.withId(R.id.tv_username)

    fun tapMyProfile() = tapBy(myProfileViewMatcher)

    fun getUserName() = getElementText(userNameViewMatcher)
}
