package com.mkalachova.trelloclone.utils

import android.view.View
import android.widget.TextView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.Matcher
import java.util.concurrent.atomic.AtomicReference

fun getText(textReference: AtomicReference<String>): ViewAction = GetTextAction(textReference)

class GetTextAction(var textReference: AtomicReference<String>) : ViewAction {
    override fun getDescription(): String = "Get Text of a view"

    override fun getConstraints(): Matcher<View> = isA(View::class.java)

    override fun perform(uiController: UiController?, view: View?) {
        val actualText = (view as TextView).text.toString()
        textReference.set(actualText)
    }
}