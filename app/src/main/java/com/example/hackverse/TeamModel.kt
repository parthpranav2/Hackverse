package com.example.hackverse

import android.os.Parcel
import android.os.Parcelable

data class TeamModel (
    var Name: String? = null,
    var PublicVisibility: Boolean=false,
    var Size: String? = null,
    var Leader: String? = null,
    var EventId :String?=null,
    var RegistrationCompleted: Boolean=false
)