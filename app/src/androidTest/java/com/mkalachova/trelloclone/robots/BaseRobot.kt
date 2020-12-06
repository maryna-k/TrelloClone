package com.mkalachova.trelloclone.robots

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher

open class BaseRobot {

    fun sleep(timeout: Long = 2000) = Thread.sleep(timeout)

    fun tapBy(matcher: Matcher<View>) = onView(matcher).perform(click())

    fun displayed(matcher: Matcher<View>) = onView(matcher).check(matches(isDisplayed()))

    fun enterText(matcher: Matcher<View>, text: String) =  onView(matcher)
        .perform(typeText(text))
        .perform(closeSoftKeyboard())

}