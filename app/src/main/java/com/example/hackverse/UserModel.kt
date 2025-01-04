package com.example.hackverse

import android.os.Parcel
import android.os.Parcelable

data class UserModel(
    var empName: String? = null,
    var empUName: String? = null,
    var empPassword: String? = null,
    var empGender: String? = null,
    var empPin: String? = null,
    var empEmail: String? = null
)