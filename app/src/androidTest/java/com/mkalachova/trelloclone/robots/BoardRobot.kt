package com.mkalachova.trelloclone.robots

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.mkalachova.trelloclone.R
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString

fun board(boardFunction: BoardRobot.() -> Unit) = BoardRobot().apply { boardFunction() }

class BoardRobot : BaseRobot() {
    private val addListBtnMatcher = allOf(withId(R.id.tv_add_task_list), isDisplayed())
    private val taskListNameFieldMatcher = withId(R.id.et_task_list_name)
    private val doneListNameBtnMatcher = withId(R.id.ib_done_list_name)

    private val taskListRecyclerView = withId(R.id.rv_task_list)
    private val taskListTitleMatcher = withId(R.id.tv_task_list_title)
    private val addCardBtnMatcher = withId(R.id.tv_add_card)
    private val cardNameFieldMatcher = withId(R.id.et_card_name)
    private val doneCardNameBtnMatcher = withId(R.id.ib_done_card_name)
    private val cardTitleMatcher = withId(R.id.tv_card_name)
    private val deleteListBtn = withId(R.id.ib_delete_list)

    private val taskItemMatcher = withId(R.id.ll_task_item)

    private val alertYesBtnMatcher = withText("Yes")

    private val cardListRecyclerView = withId(R.id.rv_card_list)

    private val cardAssignedMembersRecyclerView = withId(R.id.rv_card_selected_members_list)
    private val assignedMember = withId(R.id.iv_selected_member_image)

    private val overflowMenuBtnMatcher = withClassName(containsString("OverflowMenuButton"))
    private val membersMenuMatcher = allOf(
        withClassName(containsString("MenuDropDownListView"))
    )

    fun tapAddList() {
        tapBy(addListBtnMatcher)
    }

    fun enterListName(name: String) {
        clearTextField(taskListNameFieldMatcher)
        enterText(taskListNameFieldMatcher, name)
    }

    fun enterCardName(listName: String, cardName: String) {
        clearTextField(buildCardNameEditFieldMatcher(listName))
        enterText(buildCardNameEditFieldMatcher(listName), cardName)
    }

    fun tapConfirmCreateList() = tapBy(doneListNameBtnMatcher)

    fun tapConfirmCreateCard(listName: String) = tapBy(buildAddCardDoneButtonMatcher(listName))

    fun tapAddCard(listName: String) {
        tapBy(buildAddCardMatcher(listName))
    }

    fun addList(listName: String) {
        tapAddList()
        enterListName(listName)
        tapConfirmCreateList()
    }

    fun addCard(listName: String, cardName: String) {
        tapAddCard(listName)
        enterCardName(listName, cardName)
        tapConfirmCreateCard(listName)
    }

    fun deleteTaskList(listName: String) {
        tapBy(buildDeleteListBtnMatcher(listName))
    }

    fun listDoesNotExist(listName: String) = doesntExist(buildListNameMatcher(listName))

    fun listIsDisplayed(listName: String) = displayed(buildListNameMatcher(listName))

    fun cardIsDisplayed(cardName: String, listName: String) =
        displayed(buildCardNameMatcher(cardName, listName))

    fun acceptDeletePopup() = tapBy(alertYesBtnMatcher)

    fun alertPopUpHasCorrectMessage(listName: String) = displayed(withText("Are you sure you want to delete $listName?"))

    fun tapToOpenMembers() {
        tapBy(overflowMenuBtnMatcher)
        tapBy(membersMenuMatcher)
    }

    fun tapToOpenCardDetails(listName: String, cardName: String) {
        onView(buildCardRecyclerViewMatcher(listName))
            .perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>
            (hasDescendant(withText(cardName)), scrollTo()))
        tapBy(withText(cardName))
    }

    fun assignedMemberIsDisplayed(email: String) =
        displayed(allOf(assignedMember, withContentDescription(email)))

    private fun isDescendantOfAList(listName: String) =
        isDescendantOfA(allOf(taskItemMatcher, hasDescendant(buildListNameMatcher(listName))))

    private fun buildListNameMatcher(name: String) = allOf(taskListTitleMatcher, withText(name))

    private fun buildCardNameMatcher(cardName: String, listName: String) =
        allOf(cardTitleMatcher, withText(cardName), isDescendantOfAList(listName))

    private fun buildAddCardMatcher(listName: String) =
        allOf(addCardBtnMatcher, isDescendantOfAList(listName))

    private fun buildDeleteListBtnMatcher(listName: String) =
        allOf(deleteListBtn, hasSibling(buildListNameMatcher(listName)))

    private fun buildCardNameEditFieldMatcher(listName: String) =
        allOf(cardNameFieldMatcher, isDescendantOfAList(listName))

    private fun buildAddCardDoneButtonMatcher(listName: String) =
        allOf(doneCardNameBtnMatcher, isDescendantOfAList(listName))

    private fun buildCardRecyclerViewMatcher(listName: String) =
        allOf(cardListRecyclerView, isDescendantOfAList(listName))

    private fun buildCardAssignedMembersRecyclerViewMatcher(listName: String, cardName: String) =
        allOf(
            cardAssignedMembersRecyclerView,
            isDescendantOfAList(listName),
            hasSibling(allOf(cardNameFieldMatcher, withText(cardName)))
        )
}