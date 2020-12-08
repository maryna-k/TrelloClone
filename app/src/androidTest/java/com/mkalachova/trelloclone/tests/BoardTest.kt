package com.mkalachova.trelloclone.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.mkalachova.trelloclone.activities.SplashActivity
import com.mkalachova.trelloclone.robots.*
import com.mkalachova.trelloclone.utils.TestData
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class BoardTest : BaseTest() {

    companion object {
        private val time = System.currentTimeMillis()
        private val userName1 = "Fred$time"
        private val email1 = "fred$time@email.com"

        private val userName2 = "Jess$time"
        private val email2 = "jess$time@email.com"

        private val password = "temptemp"
        private val boardNameNoSetup = "Camping"
        private val boardNameNoMembers = "Christmas Party$time"
        private val boardNameWithMember = "Bucketlist$time"

        private val data = TestData()

        @BeforeClass
        @JvmStatic
        fun dataSetup() {
            runBlocking {
                data.createUserWithEmailAndPassword(userName1, email1, password, false)
                data.createUserWithEmailAndPassword(userName2, email2, password, false)
                Thread.sleep(5000)
                data.createBoard(boardNameNoMembers, email1)
                data.createBoard(boardNameWithMember, email1, email2)
            }
        }
    }

    @get: Rule
    var activityTestRule = ActivityScenarioRule(SplashActivity::class.java)

    @get: Rule
    var chain = RuleChain.outerRule(clearPreferencesRule)
        .around(clearFilesRule)
        .around(activityTestRule)

    @Test
    fun testCreateBoard() {
        skipSignIn(email1, password)

        home {
            sleep(3000)
            openCreateBoard()
        }
        createBoard {
            enterBoardName(boardNameNoSetup)
            tapCreateButton()
        }
        home {
            boardIsDisplayed(boardNameNoSetup, userName1)
        }
    }

    @Test
    fun testAddUserToBoard() {
        skipSignIn(email1, password)

        home {
            sleep()
            selectBoard(boardNameNoMembers)
        }
        board {
            tapToOpenMembers()
        }
        members {
            openSearchMember()
            enterMemberEmail(email2)
            tapAddMember()

            memberIsDisplayed(email2, userName2)
        }
    }

    @Test
    fun testAssignedBoardIsDisplayed() {
        skipSignIn(email2, password)

        home {
            boardIsDisplayed(boardNameWithMember, userName1)
        }
    }
}