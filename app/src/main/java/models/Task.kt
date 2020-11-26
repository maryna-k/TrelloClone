package models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task (
    var title: String = "",
    var createdBy: String = ""
): Parcelable
