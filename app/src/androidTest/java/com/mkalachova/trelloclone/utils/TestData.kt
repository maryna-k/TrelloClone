package com.mkalachova.trelloclone.utils

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.mkalachova.trelloclone.firebase.FirebaseAuthClass
import com.mkalachova.trelloclone.firebase.FirestoreClass
import com.mkalachova.trelloclone.models.Board
import com.mkalachova.trelloclone.models.User

class TestData {
    private val password = "temptemp"
    private val createdUsers: ArrayList<User> = ArrayList()

    fun createUserWithEmailAndPassword(name: String, email: String, password: String, signOut: Boolean = true) {
            FirebaseAuthClass.authInstance
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val user = User(firebaseUser.uid, name, email)
                        createdUsers.add(user)
                        Log.i("createUserWithEmailAndPassword",
                            "Success. UID: ${firebaseUser.uid}")
                        registerUserInFirebase(user, signOut)
                    } else {
                        Log.e("createUserWithEmailAndPassword", "Failure. ${task.exception}")
                    }
                }
    }

    fun registerUserInFirebase(user: User, signOut: Boolean) {
            FirestoreClass.firestoreInstance.collection(Constants.USERS)
                .document(FirestoreClass.getCurrentUserId())
                .set(user, SetOptions.merge())
                .addOnSuccessListener {
                    if(signOut) {
                        FirebaseAuthClass.authInstance.signOut()
                    }
                    Log.i("registerUserInFirebase", "Success.")
                }
                .addOnFailureListener { e ->
                    Log.e("registerUserInFirebase", "Failure. $e")
                }
    }

    fun createBoard(name: String, creatorEmail: String, assignedEmail: String? = null) {
            val assignedUsersArrayList: ArrayList<String> = ArrayList()
            val createdBy = getUserByEmail(creatorEmail)!!
            assignedUsersArrayList.add(createdBy.id)
            if(assignedEmail != null) {
                val assignedUser = getUserByEmail(assignedEmail)!!
                assignedUsersArrayList.add(assignedUser.id)
            }
            val boardInfo = Board(name, "", createdBy.name, assignedUsersArrayList)
            FirestoreClass.firestoreInstance.collection(Constants.BOARDS)
                .document()
                .set(boardInfo, SetOptions.merge())
                .addOnSuccessListener {
                    Log.i("createBoard", "Success.")
                }
                .addOnFailureListener { e ->
                    Log.e("createBoard", "Failure. $e")
                }
    }

    private fun getUserByEmail(email: String): User? {
        for(user in createdUsers) {
            if(user.email == email) {
                return user
            }
        }
        return null
    }

    fun deleteUsers() {
        deleteCurrentUser()
        if(createdUsers.size > 1) {
            for(user in createdUsers) {
                FirebaseAuthClass.authInstance.signInWithEmailAndPassword(user.email, password)
                deleteCurrentUser()
            }
        }
    }

    private fun deleteCurrentUser() {
        FirebaseAuthClass.authInstance.currentUser!!.delete()
        FirebaseAuthClass.authInstance.signOut()
    }
}