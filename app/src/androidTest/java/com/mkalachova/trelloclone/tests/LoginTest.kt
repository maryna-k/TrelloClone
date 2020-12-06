package com.mkalachova.trelloclone.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.mkalachova.trelloclone.activities.SplashActivity
import com.mkalachova.trelloclone.robots.home
import com.mkalachova.trelloclone.robots.join
import com.mkalachova.trelloclone.robots.signIn
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class LoginTest : BaseTest() {

    val email = "fred@email.com"
    val password = "temptemp"

    @get: Rule
    var mActivityTestRule = ActivityScenarioRule(SplashActivity::class.java)

    @get: Rule
    var chain = RuleChain.outerRule(clearPreferencesRule)
        .around(clearFilesRule)
        .around(mActivityTestRule)

    @Test
    fun verifyLogin() {

        join {
            sleep(3000)
            tapSignInBtn()
        }
        signIn {
            enterEmail(email)
            enterPassword(password)
            tapSignInButton()
        }
        home {
            isBoardsListDisplayed()
        }
    }
}