package com.example.hackverse

import android.os.Parcel
import android.os.Parcelable

data class GlobalTeamModel (
    var Name: String? = null,
    var TeamId: String? = null,
    var LeaderId: String? = null,
    var LeaderName: String? = null,
    var EventId :String?=null,
    var EventName :String?=null,
    var EventTheme :String?=null,

    var CandidateEmailId: String?=null,
    var CandidateName: String?=null
)