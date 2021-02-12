package com.example.projmanagapp.models

import android.os.Parcel
import android.os.Parcelable

data class Boardmodel (

    val name:String = "",
    val image :String ="",
    val createdby :String= "",
    val assignedto : ArrayList<String> = ArrayList(),
    var documentid :String = "",
    val taskList :ArrayList<task> = ArrayList()

):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!, //
        parcel.readString()!!,
        parcel.createTypedArrayList(task.CREATOR)!! //

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)= with(parcel) {
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(createdby)
        parcel.writeStringList(assignedto) //
        parcel.writeString(documentid)
        parcel.writeTypedList(taskList) //
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Boardmodel> {
        override fun createFromParcel(parcel: Parcel): Boardmodel {
            return Boardmodel(parcel)
        }

        override fun newArray(size: Int): Array<Boardmodel?> {
            return arrayOfNulls(size)
        }
    }
}