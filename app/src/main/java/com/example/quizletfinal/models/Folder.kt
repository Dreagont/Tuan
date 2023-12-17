package com.example.quizletfinal.models

import android.os.Parcel
import android.os.Parcelable

data class Folder(
    val id: String?,
    val title: String?,
    val description: String?,
    val topics: Map<String, Topic>? = mapOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        readMap(parcel)
    ) {
    }

    constructor() : this("", "", "", mapOf())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        writeMap(parcel, topics ?: emptyMap())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Folder> {

        override fun createFromParcel(parcel: Parcel): Folder {
            return Folder(parcel)
        }

        override fun newArray(size: Int): Array<Folder?> {
            return arrayOfNulls(size)
        }

        fun readMap(parcel: Parcel): Map<String, Topic>? {
            val size = parcel.readInt()
            if (size == -1) return null

            val map = mutableMapOf<String, Topic>()

            for (i in 0 until size) {
                val key = parcel.readString()
                val value = parcel.readParcelable<Topic>(Topic::class.java.classLoader)
                if (key != null && value != null) {
                    map[key] = value
                }
            }
            return map
        }

        fun writeMap(parcel: Parcel, map: Map<String, Topic>?) {
            if (map == null) {
                parcel.writeInt(-1)
                return
            }

            parcel.writeInt(map.size)

            for ((key, value) in map) {
                parcel.writeString(key)
                parcel.writeParcelable(value, 0)
            }
        }
    }
}
