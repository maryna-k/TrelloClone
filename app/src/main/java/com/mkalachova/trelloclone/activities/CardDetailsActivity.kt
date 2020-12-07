package com.mkalachova.trelloclone.activities

import com.mkalachova.trelloclone.adapters.CardMemberAdapter
import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.mkalachova.trelloclone.R
import com.mkalachova.trelloclone.dialogs.LabelColorListDialog
import com.mkalachova.trelloclone.dialogs.MembersListDialog
import com.mkalachova.trelloclone.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_card_details.*
import com.mkalachova.trelloclone.models.*
import com.mkalachova.trelloclone.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private lateinit var boardDetails: Board
    private var taskListPosition = -1
    private var cardPosition = -1
    private lateinit var cardTitle: String
    private var selectedColor: String = ""
    private lateinit var membersDetailList: ArrayList<User>
    private var selectedDueDateMilliseconds: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        getIntentData()
        setupActionBar()

        et_name_card_details.setText(cardTitle)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)

        selectedColor = boardDetails.taskList[taskListPosition].cards[cardPosition].labelColor
        if (selectedColor.isNotEmpty()) {
            setColor()
        }

        btn_update_card_details.setOnClickListener {
            if (et_name_card_details.text.toString().isNotEmpty()) {
                updateCardDetails()
            } else {
                Toast.makeText(this, "Please enter a card name", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        tv_select_label_color.setOnClickListener {
            labelColorsListDialog()
        }

        tv_select_members.setOnClickListener {
            membersListDialog()
        }

        setupSelectedMembersList()
        selectedDueDateMilliseconds =
            boardDetails.taskList[taskListPosition].cards[cardPosition].dueDate
        if(selectedDueDateMilliseconds > 0) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(selectedDueDateMilliseconds))
            tv_select_due_date.text = selectedDate
        }

        tv_select_due_date.setOnClickListener {
            showDataPicker()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_card_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
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
        when (item.itemId) {
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
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            boardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            taskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            cardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            membersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    private fun updateCardDetails() {
        cardTitle = et_name_card_details.text.toString()
        val card = Card(
            cardTitle,
            boardDetails.taskList[taskListPosition].cards[cardPosition].createdBy,
            boardDetails.taskList[taskListPosition].cards[cardPosition].assignedTo,
            selectedColor,
            selectedDueDateMilliseconds
        )

        val taskList: ArrayList<Task> = boardDetails.taskList
        taskList.removeAt(taskList.size - 1)

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
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card, cardName
            )
        )
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
        val listDialog = object : LabelColorListDialog(
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

    private fun membersListDialog() {
        val cardAssignedMembersList = boardDetails.taskList[taskListPosition]
            .cards[cardPosition].assignedTo
        if (cardAssignedMembersList.size > 0) {
            for (i in membersDetailList.indices) {
                for (j in cardAssignedMembersList) {
                    if (membersDetailList[i].id == j) {
                        membersDetailList[i].selected = true
                    }
                }
            }
        } else {
            for (i in membersDetailList.indices) {
                membersDetailList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(
            this,
            membersDetailList,
            resources.getString(R.string.str_select_member)
        ){
            override fun onItemSelected(user: User, action: String) {
                if(action == Constants.SELECT) {
                    if(!boardDetails.taskList[taskListPosition]
                            .cards[cardPosition].assignedTo.contains(user.id)) {
                        boardDetails.taskList[taskListPosition]
                            .cards[cardPosition].assignedTo.add(user.id)
                    }
                } else {
                    boardDetails.taskList[taskListPosition]
                        .cards[cardPosition].assignedTo.remove(user.id)

                    for (i in membersDetailList.indices) {
                        if(membersDetailList[i].id == user.id) {
                            membersDetailList[i].selected = false
                        }
                    }
                }
                setupSelectedMembersList()
            }
        }
        listDialog.show()
    }

    private fun setupSelectedMembersList() {
        val cardAssignedMembersList = boardDetails.taskList[taskListPosition]
            .cards[cardPosition].assignedTo
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for (i in membersDetailList.indices) {
            for (j in cardAssignedMembersList) {
                if (membersDetailList[i].id == j) {
                    val selectedMember = SelectedMembers(
                        membersDetailList[i].id,
                        membersDetailList[i].image,
                        membersDetailList[i].email
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if(selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMembers("", ""))
            tv_select_members.visibility = View.GONE
            rv_selected_members_list.visibility = View.VISIBLE

            rv_selected_members_list.layoutManager = GridLayoutManager(this, 6)
            val adapter = CardMemberAdapter(this, selectedMembersList, true)
            rv_selected_members_list.adapter = adapter
            adapter.setOnClickListener(
                object: CardMemberAdapter.OnClickListener {
                    override fun onClick() {
                        membersListDialog()
                    }
                }
            )
        } else {
            tv_select_members.visibility = View.VISIBLE
            rv_selected_members_list.visibility = View.GONE
        }
    }

    private fun showDataPicker() {
        val c = Calendar.getInstance()
        val year =
            c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            { view, year, monthOfYear, dayOfMonth ->
                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"

                val sMonthOfYear =
                    if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"

                tv_select_due_date.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)
                selectedDueDateMilliseconds = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show()
    }
}