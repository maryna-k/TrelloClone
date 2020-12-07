package com.mkalachova.trelloclone.robots

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.mkalachova.trelloclone.utils.getText
import org.hamcrest.Matcher
import java.util.concurrent.atomic.AtomicReference

open class BaseRobot {

    fun sleep(timeout: Long = 2000) = Thread.sleep(timeout)

    fun tapBy(matcher: Matcher<View>) = onView(matcher).perform(click())

    fun displayed(matcher: Matcher<View>) = onView(matcher).check(matches(isDisplayed()))

    fun doesntExist(matcher: Matcher<View>) = onView(matcher).check(doesNotExist())

    fun clearTextField(matcher: Matcher<View>) = onView(matcher).perform(clearText())

    fun enterText(matcher: Matcher<View>, text: String) =  onView(matcher)
        .perform(typeText(text))
        .perform(closeSoftKeyboard())

    fun getElementText(elementMatcher: Matcher<View>): String {
        val textReference: AtomicReference<String> = AtomicReference()
        onView(elementMatcher).perform(getText(textReference))
        return textReference.toString()
    }

    fun scrollToElement(matcher: Matcher<View>) = onView(matcher).perform(scrollTo())

    fun closeKeyboard(matcher: Matcher<View>) = onView(matcher).perform(closeSoftKeyboard())

}