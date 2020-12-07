package com.mkalachova.trelloclone.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.mkalachova.trelloclone.activities.SplashActivity
import com.mkalachova.trelloclone.robots.*
import org.junit.Ignore
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
    private val board1 = "Christmas Party"

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

    @Test
    fun verifyCreateListWithTask() {
        val listName = "Food"
        val cardName = "Turkey"

        skipSignIn(email, password)

        home {
            selectBoard("Christmas party")
        }
        board {
            addList(listName)
            listIsDisplayed(listName)
            addCard(listName, cardName)

            cardIsDisplayed(cardName, listName)
        }
    }

    @Test
    fun verifyDeleteTaskList() {
        skipSignIn(email, password)

        home {
            selectBoard("Thanksgiving")
        }
        board {
            deleteTaskList("Food")

            alertPopUpHasCorrectMessage("Food")
            acceptDeletePopup()

            listDoesNotExist("Food")
        }
    }

    @Test
    fun verifyAddUserToBoard() {
        val memberEmail = "jess@email.com"
        val memberName = "Jessica"
        skipSignIn(email, password)

        home {
            selectBoard("Christmas party")
        }
        board {
            tapToOpenMembers()

        }
        members {
            openSearchMember()
            enterMemberEmail(memberEmail)
            tapAddMember()

            memberIsDisplayed(memberName, memberEmail)
        }
    }

    @Test
    fun verifyAssignedBoardIsDisplayed() {
        val email = "kate@email.com"
        skipSignIn(email, password)

        home {
            boardIsDisplayed("Bucket list", "Fred")
        }
    }

    @Test
    fun verifyAssignTaskToUser() {
        val assignedName = "Jessica"
        val assignedEmail = "jess@email.com"
        val listName = "Food"
        val cardName = "Turkey"

        skipSignIn(email, password)

        home {
            selectBoard("Christmas party")
        }
        board {
            tapToOpenCardDetails(listName, cardName)
        }
        cardDetails {
            tapToOpenMembersList()
            tapToAssignMember(assignedName, assignedEmail)

            assignedMemberIsDisplayed(assignedEmail)

            tapUpdateButton()
        }
        board {
            assignedMemberIsDisplayed(assignedEmail)
        }
    }
}