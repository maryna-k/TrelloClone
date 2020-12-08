package com.mkalachova.trelloclone.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.mkalachova.trelloclone.activities.SplashActivity
import com.mkalachova.trelloclone.robots.*
import com.mkalachova.trelloclone.utils.TestData
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.rules.RuleChain

class ProfileTest : BaseTest() {

    private val newName = "Jess"

    companion object {
        val time = System.currentTimeMillis()
        val name = "Fred$time"
        val email = "fred$time@email.com"
        val password = "temptemp"
        val data = TestData()

        @BeforeClass
        @JvmStatic
        fun dataSetup() {
            runBlocking {
                data.createUserWithEmailAndPassword(name, email, password)
                Thread.sleep(5000)
            }
        }
    }

    @After
    fun cleanUp() {
        data.deleteUsers()
    }

    @get: Rule
    var activityTestRule = ActivityScenarioRule(SplashActivity::class.java)

    @get: Rule
    var chain = RuleChain.outerRule(clearPreferencesRule)
        .around(clearFilesRule)
        .around(activityTestRule)

    @Test
    fun testUpdateName() {
        skipSignIn(email, password)

        home {
            openDrawer()
        }
        drawer {
            tapMyProfile()
        }
        profile {
            changeName(newName)
            tapUpdateButton()
        }
        home {
            openDrawer()
        }
        drawer {
            assertEquals(newName, getUserName())
        }
    }
}