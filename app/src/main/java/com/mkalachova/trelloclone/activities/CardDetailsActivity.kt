package com.mkalachova.trelloclone.activities

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.mkalachova.trelloclone.R
import dialogs.LabelColorListDialog
import firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_card_details.*
import models.Board
import models.Card
import models.Task
import utils.Constants

class CardDetailsActivity : BaseActivity() {

    private lateinit var boardDetails: Board
    private var taskListPosition = -1
    private var cardPosition = -1
    private lateinit var cardTitle: String
    private var selectedColor: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        getIntentData()
        setupActionBar()

        et_name_card_details.setText(cardTitle)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)

        selectedColor = boardDetails.taskList[taskListPosition].cards[cardPosition].labelColor
        if(selectedColor.isNotEmpty()) {
            setColor()
        }

        btn_update_card_details.setOnClickListener {
            if(et_name_card_details.text.toString().isNotEmpty()) {
                updateCardDetails()
            } else {
                Toast.makeText(this, "Please enter a card name", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        tv_select_label_color.setOnClickListener {
            labelColorsListDialog()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_card_details_activity)

        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            cardTitle = boardDetails.taskList[taskListPosition].cards[cardPosition].name
            actionBar.title = cardTitle
        }
        toolbar_card_details_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete_card -> {
                alertDialogForDeleteCard(cardTitle)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun getIntentData() {
        if(intent.hasExtra(Constants.BOARD_DETAIL)) {
            boardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            taskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            cardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
    }

    private fun updateCardDetails() {
        cardTitle = et_name_card_details.text.toString()
        val card = Card(
            cardTitle,
            boardDetails.taskList[taskListPosition].cards[cardPosition].createdBy,
            boardDetails.taskList[taskListPosition].cards[cardPosition].assignedTo,
            selectedColor
        )
        boardDetails.taskList[taskListPosition].cards[cardPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, boardDetails)
    }

    private fun deleteCard() {
        val cardsList: ArrayList<Card> = boardDetails.taskList[taskListPosition].cards
        cardsList.removeAt(cardPosition)
        val taskList: ArrayList<Task> = boardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[taskListPosition].cards = cardsList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, boardDetails)
    }

    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(resources.getString(
            R.string.confirmation_message_to_delete_card, cardName))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, which ->
            dialogInterface.dismiss()
            deleteCard()
        }
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun colorsList(): ArrayList<String> {
        var colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    private fun setColor() {
        tv_select_label_color.text = ""
        tv_select_label_color.setBackgroundColor(Color.parseColor(selectedColor))
    }

    private fun labelColorsListDialog() {
        val colorsList: ArrayList<String> = colorsList()
        val listDialog = object: LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            selectedColor
        ) {
            override fun onItemSelected(color: String) {
                selectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }
}