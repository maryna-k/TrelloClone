package com.mkalachova.trelloclone.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SelectedMembers(
    val id: String = "",
    val image: String = "",
    val email: String = ""
) : Parcelable {
}