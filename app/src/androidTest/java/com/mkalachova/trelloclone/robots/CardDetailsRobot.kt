package com.mkalachova.trelloclone.robots

import android.widget.DatePicker
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.mkalachova.trelloclone.R
import org.hamcrest.CoreMatchers.allOf

fun cardDetails(cardDetailsFunction: CardDetailsRobot.() -> Unit) =
    CardDetailsRobot().apply { cardDetailsFunction() }

class CardDetailsRobot : BaseRobot() {
    private val openAssignDialogBtn = allOf(withId(R.id.iv_add_member), isDisplayed())
    private val selectedMembersRecyclerView = withId(R.id.rv_selected_members_list)
    private val assignedMember = withId(R.id.iv_selected_member_image)
    private val updateBtn = withId(R.id.btn_update_card_details)
    private val memberRecyclerView = withId(R.id.rvList)
    private val memberEmail = withId(R.id.tv_member_email)
    private val memberName = withId(R.id.tv_member_name)
    private val memberSelected = withId(R.id.iv_selected_member)
    private val selectDateBtn = withId(R.id.tv_select_due_date)
    private val calendarBtnOk = withText("OK")

    fun tapToOpenMembersList() = tapBy(openAssignDialogBtn)

    fun tapToAssignMember(name: String, email: String) {
        onView(memberRecyclerView)
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>
                (hasDescendant(withText(email)), scrollTo()))
        tapBy(allOf(withText(name), hasSibling(withText(email))))
    }

    fun assignedMemberIsDisplayed(email: String) =
        displayed(allOf(assignedMember, withContentDescription(email)))

    fun tapSelectDate() = tapBy(selectDateBtn)

    fun selectDate(day: Int, month: Int, year: Int) =
        onView(isAssignableFrom(DatePicker::class.java))
            .perform(PickerActions.setDate(year, month, day))

    fun tapOkOnCalendar() = tapBy(calendarBtnOk)

    fun getDisplayedDueDate() = getElementText(selectDateBtn)

    fun tapUpdateButton() {
        closeKeyboard(updateBtn)
        tapBy(updateBtn)
    }
}