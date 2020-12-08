package com.mkalachova.trelloclone.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.mkalachova.trelloclone.activities.SplashActivity
import com.mkalachova.trelloclone.robots.home
import com.mkalachova.trelloclone.robots.join
import com.mkalachova.trelloclone.robots.signUp
import com.mkalachova.trelloclone.utils.TestData
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class RegisterUserTest : BaseTest() {

    val time = System.currentTimeMillis()
    val name = "Fred$time"
    val email = "fred$time@email.com"
    private val password = "temptemp"

    @get: Rule
    var activityTestRule = ActivityScenarioRule(SplashActivity::class.java)

    @get: Rule
    var chain = RuleChain.outerRule(clearPreferencesRule)
        .around(clearFilesRule)
        .around(activityTestRule)

    @After
    fun cleanUp() {
        TestData().deleteUsers()
    }

    @Test
    fun testUserRegistration() {
        join {
            waitForIntroScreen()
            tapSignUpBtn()
        }
        signUp {
            enterName(name)
            enterEmail(email)
            enterPassword(password)
            tapSignUpButton()
        }
        home {
            emptyBoardsMessageIsDisplayed()
        }
    }
}