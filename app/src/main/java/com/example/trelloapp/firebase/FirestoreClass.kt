package com.example.trelloapp.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.trelloapp.activities.*
import com.example.trelloapp.activities.activities.MainActivity
import com.example.trelloapp.activities.activities.SignInActivity
import com.example.trelloapp.activities.activities.SignUpActivity
import com.example.trelloapp.models.Board
import com.example.trelloapp.models.Task
import com.example.trelloapp.models.User
import com.example.trelloapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class FirestoreClass : BaseActivity() {

    // create instance for firebase firestore
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity : SignUpActivity, userInfo : User)
    {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
    }


    fun signInUser(activity : Activity, readBoardList : Boolean = false)
    {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)!!
                if (loggedInUser != null)
                {
                    when(activity)
                    {
                        is SignInActivity -> {activity.signInSuccess(loggedInUser)}
                        is MainActivity -> {
                            activity.updateNavigationUserDetail(loggedInUser, readBoardList)
                        }
                        is MyProfileActivity -> {activity.setUserDataInUI(loggedInUser)}
                    }
                }
            }.addOnFailureListener { e->

                when(activity)
                {
                    is SignInActivity -> {activity.hideProgressDialog()}
                    is MainActivity -> {activity.hideProgressDialog()}
                    is MyProfileActivity -> {activity.hideProgressDialog()}
                }

                Log.e("SignInUser", "Error writing document", e)
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).update(userHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Data updated successfully!")

                when (activity)
                {
                    is MyProfileActivity -> {activity.updateProfileSuccess()}
                }

            }
    }

    fun createBoard(activity: Activity, board: Board) {
        mFireStore.collection(Constants.BOARD).document().set(board, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(activity, "Board created success", Toast.LENGTH_LONG).show()
                when (activity)
                {
                    is CreateBoardActivity -> {activity.boardCreatedSuccessfully()}
                }
            }.addOnFailureListener { exception ->
                when (activity)
                {
                    is CreateBoardActivity ->
                    {
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName, "Error while created a board")
                    }
                }
            }
    }

    fun getBoardList(activity : MainActivity)
    {
        mFireStore.collection(Constants.BOARD)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                val boardList : ArrayList<Board> = ArrayList()
                for (i in document.documents)
                {
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }
                activity.populateBoardListToUI(boardList)
            }
            .addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    fun getBoardDetail(activity : TaskListActivity, documentId : String)
    {
        mFireStore.collection(Constants.BOARD)
            .document(documentId).get().addOnSuccessListener {
                document ->
                val board = document.toObject(Board::class.java)!!
                board.documentId = documentId
                activity.boardDetails(board)
            }.addOnFailureListener {
                Log.e(activity.javaClass.simpleName, "Error getting board Details")
            }
    }

    fun deleteBoard(activity : TaskListActivity, documentId : String)
    {
        mFireStore.collection(Constants.BOARD)
            .document(documentId).delete().addOnSuccessListener {
                document ->
                Log.d(activity.javaClass.simpleName, "Board has been successfully deleted")
                activity.successfullyDeleteBoard()
            }.addOnFailureListener {
                hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error getting board Details")
            }
    }

    fun addUpdateTaskList(activity: Activity, board: Board)
    {
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList


        mFireStore.collection(Constants.BOARD).document(board.documentId).update(
            taskListHashMap).addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Task list added successfully")

            if (activity is TaskListActivity) {activity.addUpdateTaskListSuccess()}
            if (activity is CardDetailActivity) {activity.cardUpdatedSuccess()}
        }
            .addOnFailureListener {
                exception ->
                if (activity is CardDetailActivity) {activity.hideProgressDialog()}
                if (activity is TaskListActivity) {activity.hideProgressDialog()}
                Log.e(activity.javaClass.simpleName, "Failed to add task", exception)

            }
    }

    fun getAssignedMemberList(activity: Activity, assignedTo : ArrayList<String>)
    {
        mFireStore.collection(Constants.USERS).whereIn(Constants.ID, assignedTo).get()
            .addOnSuccessListener {
                document ->
                val users = ArrayList<User>()
                for (i in document.documents)
                {
                    val user = i.toObject(User::class.java)
                    users.add(user!!)
                }
                if (activity is MembersActivity) {activity.userDetail(users)}
                if (activity is TaskListActivity) {activity.getMemberDetail(users)}
            }.addOnFailureListener {
                exception ->
                Log.e(activity.javaClass.simpleName, "Error getting assigned member list")
            }
    }


    fun getMemberDetail(activity: MembersActivity, memberEmail: String)
    {
        mFireStore.collection(Constants.USERS).whereEqualTo(Constants.EMAIL, memberEmail).get()
            .addOnSuccessListener {
                document ->
                if (document.documents.size > 0)
                {
                    val member = document.documents[0].toObject(User::class.java)
                    activity.memberDetail(member!!)
                }
                else
                {
                    activity.hideProgressDialog()
                    showErrorSnackBar("User email not available")
                }
            }.addOnFailureListener {
                exception ->
                hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error getting user information", exception)
            }
    }

    fun assignUserBoard(activity: MembersActivity, board: Board, user: User)
    {
        val assignedToHashMap : HashMap<String, Any> = HashMap()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        mFireStore.collection(Constants.BOARD).document(board.documentId)
            .update(assignedToHashMap).addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Successfully assigned user")
                activity.successfullyAssignUser()
            }.addOnFailureListener {
                exception ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error: ", exception)
            }
    }

}