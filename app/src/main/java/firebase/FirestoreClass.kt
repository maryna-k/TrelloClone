package firebase

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mkalachova.trelloclone.activities.MainActivity
import com.mkalachova.trelloclone.activities.SignInActivity
import com.mkalachova.trelloclone.activities.SignUpActivity
import models.User
import timber.log.Timber
import utils.Constants

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                e ->
                Timber.e(activity.javaClass.simpleName, "Error writing to Firestore: %s", e)
            }
    }

    fun singInUser(activity: Activity) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)

                if(loggedInUser != null) {
                    when (activity) {
                        is SignInActivity -> {
                            activity.signInSuccesss(loggedInUser)
                        }
                        is MainActivity -> {
                            activity.updateNavigationUserDetails(loggedInUser)
                        }
                    }
                }
            }.addOnFailureListener {
                    e ->
                when(activity) {
                    is SignInActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Timber.e(activity.javaClass.simpleName, "Error writing to Firestore: %s", e)
            }
    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }
}