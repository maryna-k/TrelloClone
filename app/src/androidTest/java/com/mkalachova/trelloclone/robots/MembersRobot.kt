package com.mkalachova.trelloclone.robots

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.mkalachova.trelloclone.R
import com.mkalachova.trelloclone.utils.RecyclerViewHasChildMatcher.Companion.recyclerHasChild
import org.hamcrest.CoreMatchers.allOf

fun members(membersFunction: MembersRobot.() -> Unit) = MembersRobot().apply { membersFunction() }

class MembersRobot : BaseRobot() {
    private val addMemberMenuBtn = withId(R.id.action_add_member)
    private val searchEmailField = withId(R.id.et_email_search_member)
    private val addBtn = withId(R.id.tv_add)
    private val membersRecyclerView = withId(R.id.rv_members_list)
    private val memberName = withId(R.id.tv_member_name)
    private val memberEmail = withId(R.id.tv_member_email)

    fun openSearchMember() = tapBy(addMemberMenuBtn)

    fun enterMemberEmail(email: String) {
        clearTextField(searchEmailField)
        enterText(searchEmailField, email)
    }

    fun tapAddMember() = tapBy(addBtn)

    fun memberIsDisplayed(name: String, email: String) {
        onView(membersRecyclerView)
            .check(matches(recyclerHasChild(
                allOf(
                    hasDescendant(allOf(memberName, withText(name))),
                    hasDescendant(allOf(memberEmail, withText(email)))
                )))
            )
    }

}
