package com.mkalachova.trelloclone.tests

import androidx.test.espresso.IdlingRegistry
import com.mkalachova.trelloclone.firebase.FirebaseAuthClass
import com.mkalachova.trelloclone.utils.EspressoIdlingResource
import com.schibsted.spain.barista.rule.cleardata.ClearFilesRule
import com.schibsted.spain.barista.rule.cleardata.ClearPreferencesRule
import org.junit.After
import org.junit.Before
import org.junit.Rule

open class BaseTest {

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @get: Rule
    var clearPreferencesRule = ClearPreferencesRule()

    @get: Rule
    var clearFilesRule = ClearFilesRule()

    fun skipSignIn(email: String, password: String) {
        FirebaseAuthClass.authInstance.signInWithEmailAndPassword(email, password)
        //sleep for the duration of splash activity
        Thread.sleep(2000)
    }

}
