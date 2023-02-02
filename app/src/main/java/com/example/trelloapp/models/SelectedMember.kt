package com.example.trelloapp.models

import android.os.Parcel
import android.os.Parcelable

data class SelectedMember(
    val name : String = "",
    val image : String = ""
) : Parcelable{

    constructor(parcel: Parcel) : this (
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flag : Int) = with(dest) {
        writeString(name)
        writeString(image)
    }

    companion object CREATOR : Parcelable.Creator<SelectedMember> {
        override fun createFromParcel(parcel: Parcel): SelectedMember {
            return SelectedMember(parcel)
        }

        override fun newArray(size: Int): Array<SelectedMember?> {
            return arrayOfNulls(size)
        }
    }

}
