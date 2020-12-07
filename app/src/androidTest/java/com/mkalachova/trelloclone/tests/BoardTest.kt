package com.mkalachova.trelloclone.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.mkalachova.trelloclone.activities.SplashActivity
import com.mkalachova.trelloclone.robots.createBoard
import com.mkalachova.trelloclone.robots.home
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class BoardTest : BaseTest() {

    private val email = "fred@email.com"
    private val password = "temptemp"
    private val boardName = "Camping"
    private val creator = "Fred"

    @get: Rule
    var activityTestRule = ActivityScenarioRule(SplashActivity::class.java)

    @get: Rule
    var chain = RuleChain.outerRule(clearPreferencesRule)
        .around(clearFilesRule)
        .around(activityTestRule)

    @Test
    fun verifyCreateBoard() {
        skipSignIn(email, password)

        home {
            openCreateBoard()
        }
        createBoard {
            enterBoardName(boardName)
            tapCreateButton()
        }
        home {
            boardIsDisplayed(boardName, creator)
        }
    }
}