package com.mkalachova.trelloclone.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.mkalachova.trelloclone.activities.SplashActivity
import com.mkalachova.trelloclone.robots.*
import com.mkalachova.trelloclone.utils.TestData
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class TaskTest : BaseTest() {

    companion object {
        private val time = System.currentTimeMillis()
        private val userName1 = "Fred$time"
        private val email1 = "fred$time@email.com"

        private val userName2 = "Jess$time"
        private val email2 = "jess$time@email.com"

        private val password = "temptemp"
        private val boardNameWithMember = "Bucketlist$time"
        private val boardNameNoMember = "Travel$time"

        val listName = "Food"
        val cardName = "Turkey"

        private val data = TestData()

        @BeforeClass
        @JvmStatic
        fun dataSetup() {
            runBlocking {
                data.createUserWithEmailAndPassword(userName1, email1, password, false)
                data.createUserWithEmailAndPassword(userName2, email2, password, false)
                Thread.sleep(5000)
                data.createBoard(boardNameWithMember, email1, email2)
                data.createBoard(boardNameNoMember, email1)
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
    fun testCreateListWithTask() {
        skipSignIn(email1, password)

        home {
            selectBoard(boardNameNoMember)
        }
        board {
            addList(listName)
            listIsDisplayed(listName)
            addCard(listName, cardName)

            cardIsDisplayed(cardName, listName)
        }
    }

    @Test
    fun testDeleteTaskList() {
        skipSignIn(email1, password)

        home {
            selectBoard(boardNameNoMember)
        }
        board {
            addList(listName)
            addCard(listName, cardName)

            deleteTaskList(listName)

            alertPopUpHasCorrectMessage(listName)
            acceptDeletePopup()

            listDoesNotExist(listName)
        }
    }

    @Test
    fun testAddDueDate() {
        skipSignIn(email1, password)

        home {
            selectBoard(boardNameNoMember)
        }
        board {
            addList(listName)
            addCard(listName, cardName)
            tapToOpenCardDetails(listName, cardName)
        }
        cardDetails {
            tapSelectDate()
            selectDate(10, 10, 2030)
            tapOkOnCalendar()
            tapUpdateButton()
        }
        board {
            tapToOpenCardDetails(listName, cardName)
        }
        cardDetails {
            assertEquals(getDisplayedDueDate(), "10/10/2030")
        }
    }

    @Test
    fun testAssignTaskToUser() {

        skipSignIn(email1, password)

        home {
            selectBoard(boardNameNoMember)
        }
        board {
            addList(listName)
            addCard(listName, cardName)
            tapToOpenMembers()
        }
        members {
            openSearchMember()
            enterMemberEmail(email2)
            tapAddMember()
            tapReturn()
        }
         board {
            tapToOpenCardDetails(listName, cardName)
        }
        cardDetails {
            tapToOpenMembersList()
            tapToAssignMember(userName2, email2)

            assignedMemberIsDisplayed(email2)

            tapUpdateButton()
        }
        board {
            assignedMemberIsDisplayed(email2)
        }
    }
}