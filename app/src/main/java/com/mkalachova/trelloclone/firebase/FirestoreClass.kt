package com.mkalachova.trelloclone.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions
import com.mkalachova.trelloclone.activities.*
import com.mkalachova.trelloclone.models.Board
import com.mkalachova.trelloclone.models.User
import com.mkalachova.trelloclone.utils.Constants
import com.mkalachova.trelloclone.utils.EspressoIdlingResource

object FirestoreClass {

    var firestoreInstance : FirebaseFirestore

    init {
        firestoreInstance = FirebaseFirestore.getInstance()
        if (!firestoreInstance.getFirestoreSettings().host.contains("10.0.2.2")) {
            firestoreInstance.useEmulator("10.0.2.2", 8080)
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build()
            firestoreInstance.firestoreSettings = settings
        }
    }

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        EspressoIdlingResource.increment()
        firestoreInstance.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
                EspressoIdlingResource.decrement()
            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error writing to Firestore: %s", e)
                EspressoIdlingResource.decrement()
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        EspressoIdlingResource.increment()
        firestoreInstance.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile Data updated successfully")
                when(activity) {
                    is MainActivity -> {
                        activity.tokenUpdateSuccess()
                    }
                    is MyProfileActivity -> {
                        activity.profileUpdateSuccess()
                    }
                }
                EspressoIdlingResource.decrement()
            }
            .addOnFailureListener { e ->
                when(activity) {
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error updating profile", e)
                EspressoIdlingResource.decrement()
            }
    }

    fun loadUserData(activity: Activity, readBoardsList: Boolean = false) {
        EspressoIdlingResource.increment()
        firestoreInstance.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)

                if(loggedInUser != null) {
                    when (activity) {
                        is SignInActivity -> {
                            activity.signInSuccesss()
                        }
                        is MainActivity -> {
                            activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                        }
                        is MyProfileActivity -> {
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }
                }
                EspressoIdlingResource.decrement()
            }
            .addOnFailureListener { e ->
                when(activity) {
                    is SignInActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error writing to Firestore: %s", e)
                EspressoIdlingResource.decrement()
            }
    }

    fun getCurrentUserId(): String {
        EspressoIdlingResource.increment()
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser != null) {
            currentUserId = currentUser.uid
        }
        EspressoIdlingResource.decrement()
        return currentUserId
    }

    fun getBoardsList(activity: MainActivity) {
        EspressoIdlingResource.increment()
        firestoreInstance.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()
                for(i in document.documents) {
                    var board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }

                activity.populateBoardsListToUI(boardList)
                activity.hideProgressDialog()
                EspressoIdlingResource.decrement()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board", e)
                EspressoIdlingResource.decrement()
            }
    }

    fun createBoard(activity: CreateBoardActivity, boardInfo: Board) {
        EspressoIdlingResource.increment()
        firestoreInstance.collection(Constants.BOARDS)
            .document()
            .set(boardInfo, SetOptions.merge())
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Board created successfully")
                Toast.makeText(activity, "Board created successfully", Toast.LENGTH_SHORT)
                    .show()
                activity.boardCreatedSuccessfully()
                EspressoIdlingResource.decrement()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error writing to Firestore: %s", e)
                EspressoIdlingResource.decrement()
            }
    }

    fun getBoardDetails(activity: TaskListActivity, boardDocumentId: String) {
        EspressoIdlingResource.increment()
        firestoreInstance.collection(Constants.BOARDS)
            .document(boardDocumentId)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                activity.boardDetails(board)
                EspressoIdlingResource.decrement()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board", e)
                EspressoIdlingResource.decrement()
            }
    }

    fun addUpdateTaskList(activity: Activity, board: Board) {
        EspressoIdlingResource.increment()
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        firestoreInstance.collection((Constants.BOARDS))
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Task updated successfully")

                if(activity is TaskListActivity) {
                    activity.addUpdateTaskListSuccess()
                } else if (activity is CardDetailsActivity) {
                    activity.addUpdateTaskListSuccess()
                }
                EspressoIdlingResource.decrement()
            }
            .addOnFailureListener { e ->
                if(activity is TaskListActivity) {
                    activity.hideProgressDialog()
                } else if (activity is CardDetailsActivity) {
                    activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "Error updating tasks: %s", e)
                EspressoIdlingResource.decrement()
            }
    }

    fun getAssignedMembersListDetails(activity: Activity, assignedTo: ArrayList<String>) {
        EspressoIdlingResource.increment()
        firestoreInstance.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())

                val usersList: ArrayList<User> = ArrayList()
                for (i in document.documents) {
                    val user = i.toObject(User::class.java)
                    if (user != null) {
                        usersList.add(user)
                    }
                    if(activity is MembersActivity) {
                        activity.setupMembersList((usersList))
                    } else if(activity is TaskListActivity) {
                        activity.boardMembersDetailsList(usersList)
                    }
                }
                EspressoIdlingResource.decrement()
            }
            .addOnFailureListener { e ->
                if(activity is MembersActivity) {
                    activity.hideProgressDialog()
                } else if(activity is TaskListActivity) {
                    activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "Error getting assigned members: %s", e)
                EspressoIdlingResource.decrement()
            }
    }

    fun getMemberDetails(activity: MembersActivity, email: String) {
        EspressoIdlingResource.increment()
        firestoreInstance.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                if(document.documents.size > 0) {
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                } else {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found")
                }
                EspressoIdlingResource.decrement()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error getting user by email: %s", e)
                EspressoIdlingResource.decrement()
            }
    }

    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User) {
        EspressoIdlingResource.increment()
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        firestoreInstance.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
                EspressoIdlingResource.decrement()
            }
            .addOnFailureListener {e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error updating board: %s", e)
                EspressoIdlingResource.decrement()
            }
    }
}