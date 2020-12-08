package com.mkalachova.trelloclone.robots

import androidx.test.espresso.matcher.ViewMatchers.withId
import com.mkalachova.trelloclone.R

fun createBoard(createBoardFunction: CreateBoardRobot.() -> Unit) = CreateBoardRobot().apply { createBoardFunction() }

class CreateBoardRobot : BaseRobot() {
    private val boardNameFieldMatcher = withId(R.id.et_board_name)
    private val createBtn = withId(R.id.btn_create)

    fun enterBoardName(name: String) {
        clearTextField(boardNameFieldMatcher)
        enterText(boardNameFieldMatcher, name)
    }

    fun tapCreateButton() = tapBy(createBtn)
}