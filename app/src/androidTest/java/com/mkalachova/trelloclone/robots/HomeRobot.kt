package com.mkalachova.trelloclone.robots

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.mkalachova.trelloclone.R
import com.mkalachova.trelloclone.utils.RecyclerViewHasChildMatcher.Companion.recyclerHasChild
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString

fun home(homeFunction: HomeRobot.() -> Unit) = HomeRobot().apply { homeFunction() }

class HomeRobot : BaseRobot() {
    private val boardsRecyclerViewMatcher = withId(R.id.rv_boards_list)
    private val drawerToggleMatcher = withClassName(containsString("AppCompatImageButton"))
    private val createBoardBtnMatcher = withId(R.id.fab_create_board)
    private val noBoardsMessageMatcher = withId(R.id.tv_no_boards_available)

    fun isBoardsListDisplayed() = displayed(boardsRecyclerViewMatcher)

    fun openDrawer() = tapBy(drawerToggleMatcher)

    fun openCreateBoard() = tapBy(createBoardBtnMatcher)

    fun emptyBoardsMessageIsDisplayed() = displayed(noBoardsMessageMatcher)

    fun boardIsDisplayed(boardName: String, creator: String) {
        onView(boardsRecyclerViewMatcher)
            .check(matches(recyclerHasChild(
                allOf(
                    hasDescendant(withText(boardName)),
                    hasDescendant(withText("Created by: $creator"))
                )
            )))
    }

}