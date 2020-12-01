package models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Card (
    val name: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    val labelColor: String = ""
): Parcelable