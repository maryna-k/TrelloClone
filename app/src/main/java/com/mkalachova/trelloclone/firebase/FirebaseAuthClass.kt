package com.mkalachova.trelloclone.firebase

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthClass {

    var authInstance : FirebaseAuth = FirebaseAuth.getInstance()

    init {
        authInstance.useEmulator("10.0.2.2", 9099)
    }
}