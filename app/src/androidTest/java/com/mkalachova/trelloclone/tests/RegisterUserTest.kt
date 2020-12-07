package com.mkalachova.trelloclone.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.mkalachova.trelloclone.activities.SplashActivity
import com.mkalachova.trelloclone.robots.home
import com.mkalachova.trelloclone.robots.join
import com.mkalachova.trelloclone.robots.signUp
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class RegisterUserTest : BaseTest() {

    private val validEmail = "ann@email.com"
    private val password = "temptemp"
    private val name = "Ann"

    @get: Rule
    var activityTestRule = ActivityScenarioRule(SplashActivity::class.java)

    @get: Rule
    var chain = RuleChain.outerRule(clearPreferencesRule)
        .around(clearFilesRule)
        .around(activityTestRule)

    @Test
    fun verifyUserRegistration() {
        join {
            waitForIntroScreen()
            tapSignUpBtn()
        }
        signUp {
            enterName(name)
            enterEmail(validEmail)
            enterPassword(password)
            tapSignUpButton()
        }
        home {
            emptyBoardsMessageIsDisplayed()
        }
    }
}