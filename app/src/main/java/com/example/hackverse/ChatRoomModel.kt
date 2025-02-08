package com.example.hackverse

import android.os.Parcel
import android.os.Parcelable

data class ChatRoomModel (
    var SenderId: String? = null,
    var SenderName:String? = null,

    var Message: String? = null,
    var CommitTime: String? = null,
    var CommitDate: String? = null,

    var SuccedingMessageDate :String?=null,
    var SuccedingMessageSenderId:String?=null,

    var UserType :String?=null
)