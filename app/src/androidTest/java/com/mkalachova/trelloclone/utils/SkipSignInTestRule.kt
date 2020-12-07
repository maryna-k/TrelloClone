package com.mkalachova.trelloclone.utils

import com.google.firebase.auth.FirebaseAuth
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class SkipSignInTestRule(val email: String, val password: String) : TestWatcher() {

    override fun starting(description: Description?) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        //sleep for the duration of SplashActivity
        Thread.sleep(2000)
        super.starting(description)
    }
}