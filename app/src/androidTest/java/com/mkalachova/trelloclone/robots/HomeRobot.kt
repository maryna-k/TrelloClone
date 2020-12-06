package com.mkalachova.trelloclone.robots

import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.mkalachova.trelloclone.R
import org.hamcrest.CoreMatchers.containsString

fun home(homeFunction: HomeRobot.() -> Unit) = HomeRobot().apply { homeFunction() }

class HomeRobot : BaseRobot() {
    private val boardsRecyclerViewMatcher = withId(R.id.rv_boards_list)
    private val drawerToggleMatcher = withClassName(containsString("AppCompatImageButton"))

    fun isBoardsListDisplayed() = displayed(boardsRecyclerViewMatcher)

    fun openDrawer() = tapBy(drawerToggleMatcher)

}