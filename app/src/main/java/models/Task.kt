package models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task (
    var title: String = "",
    val createdBy: String = "",
    var cards: ArrayList<Card> = ArrayList()
): Parcelable
