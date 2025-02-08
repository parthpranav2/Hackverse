package com.example.hackverse

import android.os.Parcel
import android.os.Parcelable

data class TeamParsableModel(
    var tmName: String? = null,
    var tmevName: String? = null,
    var tmInviterEmail: String? = null,
    var tmevTheme: String? = null,

    var tmEventId: String? = null,
    var tmId: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        tmName = parcel.readString() ?: "",
        tmevName = parcel.readString() ?: "",
        tmInviterEmail = parcel.readString() ?: "",
        tmevTheme = parcel.readString() ?: "",

        tmEventId = parcel.readString() ?: "",
        tmId = parcel.readString() ?: "",
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tmName)
        parcel.writeString(tmevName)
        parcel.writeString(tmInviterEmail)
        parcel.writeString(tmevTheme)

        parcel.writeString(tmEventId)
        parcel.writeString(tmId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TeamParsableModel> {
        override fun createFromParcel(parcel: Parcel): TeamParsableModel {
            return TeamParsableModel(parcel)
        }

        override fun newArray(size: Int): Array<TeamParsableModel?> {
            return arrayOfNulls(size)
        }
    }

}

