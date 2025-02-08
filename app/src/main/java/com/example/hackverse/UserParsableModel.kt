package com.example.hackverse

import android.os.Parcel
import android.os.Parcelable

data class UserParsableModel(
    var empName: String? = null,
    var empUName: String? = null,
    var empPassword: String? = null,
    var empGender: String? = null,
    var empPin: String? = null,
    var empEmail : String? = null,

    var empRegistered : String? = "F",

): Parcelable {
    constructor(parcel: Parcel) : this(
        empName = parcel.readString() ?: "",
        empUName = parcel.readString() ?: "",
        empPassword = parcel.readString() ?: "",
        empGender = parcel.readString() ?: "",
        empPin = parcel.readString() ?: "",
        empEmail = parcel.readString() ?: "",

        empRegistered = parcel.readString() ?: "",
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(empName)
        parcel.writeString(empUName)
        parcel.writeString(empPassword)
        parcel.writeString(empGender)
        parcel.writeString(empPin)
        parcel.writeString(empEmail)
        parcel.writeString(empRegistered)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserParsableModel> {
        override fun createFromParcel(parcel: Parcel): UserParsableModel {
            return UserParsableModel(parcel)
        }

        override fun newArray(size: Int): Array<UserParsableModel?> {
            return arrayOfNulls(size)
        }
    }

}

