package com.example.hackverse

import android.os.Parcel
import android.os.Parcelable

data class EventModel(
    var evName: String? = null,
    var evTargetAudiance: String? = null,
    var evMode: String? = null,
    var evssd: String? = null,
    var evsed: String? = null,
    var evTheme: String? = null,
    var evId: String? = null,
    var evMaxTeamSize: String? = null,
    var evsst: String? = null,
    var evset: String? = null,
    var evDescription: String? = null,
    var evVenue: String? = null,
    var evPosterURL: String? = null
):Parcelable{
    constructor(parcel: Parcel) : this(
        evName = parcel.readString() ?: "",
        evTargetAudiance = parcel.readString() ?: "",
        evMode = parcel.readString() ?: "",
        evssd = parcel.readString() ?: "",
        evsed = parcel.readString() ?: "",
        evTheme = parcel.readString() ?: "",
        evId = parcel.readString() ?: "",
        evMaxTeamSize = parcel.readString() ?: "",
        evsst = parcel.readString() ?: "",
        evset = parcel.readString() ?: "",
        evDescription = parcel.readString() ?: "",
        evVenue = parcel.readString() ?: "",
        evPosterURL = parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(evName)
        parcel.writeString(evTargetAudiance)
        parcel.writeString(evMode)
        parcel.writeString(evssd)
        parcel.writeString(evsed)
        parcel.writeString(evTheme)
        parcel.writeString(evId)
        parcel.writeString(evMaxTeamSize)
        parcel.writeString(evsst)
        parcel.writeString(evset)
        parcel.writeString(evDescription)
        parcel.writeString(evVenue)
        parcel.writeString(evPosterURL)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventModel> {
        override fun createFromParcel(parcel: Parcel): EventModel {
            return EventModel(parcel)
        }

        override fun newArray(size: Int): Array<EventModel?> {
            return arrayOfNulls(size)
        }
    }

}
