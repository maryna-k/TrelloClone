package com.mkalachova.trelloclone.activities

import adapters.TaskListAdapter
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mkalachova.trelloclone.R
import firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_task_list.*
import models.Board
import models.Task
import utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var boardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        var boardDocumentId = ""

        if(intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, boardDocumentId)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_task_list_activity)

        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = boardDetails.name
        }
        toolbar_task_list_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun boardDetails(board: Board) {
        boardDetails = board

        setupActionBar()
        hideProgressDialog()

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        rv_task_list.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false)

        rv_task_list.setHasFixedSize(true)

        val adapter = TaskListAdapter(this, board.taskList)
        rv_task_list.adapter = adapter
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, boardDetails.documentId)
    }

    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, FirestoreClass().getCurrentUserId())
        boardDetails.taskList.add(0, task)
        boardDetails.taskList.removeAt(boardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this, boardDetails)
    }
}