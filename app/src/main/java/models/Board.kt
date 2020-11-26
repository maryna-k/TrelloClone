package models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Board(
    val name: String = "",
    val image: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentId: String = "",
    var taskList: ArrayList<Task> = ArrayList()
) : Parcelable