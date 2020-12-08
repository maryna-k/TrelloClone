package com.mkalachova.trelloclone.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.mkalachova.trelloclone.activities.SplashActivity
import com.mkalachova.trelloclone.robots.home
import com.mkalachova.trelloclone.robots.join
import com.mkalachova.trelloclone.robots.signIn
import com.mkalachova.trelloclone.utils.TestData
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import java.lang.System.currentTimeMillis

@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class LoginTest : BaseTest() {

    companion object {
        private val time = currentTimeMillis()
        private val name = "Fred$time"
        private val validEmail = "fred$time@email.com"
        private val password = "temptemp"
        private val data = TestData()

        @BeforeClass @JvmStatic
        fun dataSetup() {
            runBlocking {
                data.createUserWithEmailAndPassword(name, validEmail, password)
                Thread.sleep(5000)
                data.createBoard("Board", validEmail)
                Thread.sleep(5000)
            }
        }
    }

    @After
    fun cleanUp() {
        data.deleteUsers()
    }

    var activityTestRule = ActivityScenarioRule(SplashActivity::class.java)

    @get: Rule
    var chain = RuleChain.outerRule(clearPreferencesRule)
        .around(clearFilesRule)
        .around(activityTestRule)

    @Test
    fun testLogin() {
        join {
            waitForIntroScreen()
            tapSignInBtn()
        }
        signIn {
            enterEmail(validEmail)
            enterPassword(password)
            tapSignInButton()
        }
        home {
            isBoardsListDisplayed()
        }
    }
}