package com.mkalachova.trelloclone.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.mkalachova.trelloclone.activities.SplashActivity
import com.mkalachova.trelloclone.robots.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

class ProfileTest : BaseTest() {

    private val newName = "Jess"
    private val email = "jess@email.com"
    private val password = "temptemp"

    @get: Rule
    var activityTestRule = ActivityScenarioRule(SplashActivity::class.java)

    @get: Rule
    var chain = RuleChain.outerRule(clearPreferencesRule)
        .around(clearFilesRule)
        .around(activityTestRule)

    @Test
    fun verifyUpdateName() {
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