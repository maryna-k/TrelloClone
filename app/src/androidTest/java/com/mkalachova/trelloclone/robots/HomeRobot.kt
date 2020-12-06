package com.mkalachova.trelloclone.robots

import androidx.test.espresso.matcher.ViewMatchers.withId
import com.mkalachova.trelloclone.R

fun home(homeFunction: HomeRobot.() -> Unit) = HomeRobot().apply { homeFunction() }

class HomeRobot : BaseRobot() {
    private val boardsRecyclerViewMatcher = withId(R.id.rv_boards_list)

    fun isBoardsListDisplayed() = displayed(boardsRecyclerViewMatcher)
}