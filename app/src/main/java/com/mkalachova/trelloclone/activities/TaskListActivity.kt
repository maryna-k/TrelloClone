package com.mkalachova.trelloclone.activities

import android.os.Bundle
import com.mkalachova.trelloclone.R
import firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_task_list.*
import models.Board
import utils.Constants

class TaskListActivity : BaseActivity() {
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

    private fun setupActionBar(title: String) {
        setSupportActionBar(toolbar_task_list_activity)

        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = title
        }
        toolbar_task_list_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun boardDetails(board: Board) {
        hideProgressDialog()
        setupActionBar(board.name)
    }
}