package com.example.quizletfinal.models
import android.os.Parcel
import android.os.Parcelable

data class Topic(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val visibility: String = "",
    val folderId: String? = null,
    val cards: Map<String, Card> = mapOf()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        emptyMap() // You might need to handle parceling for Map<String, Card>
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(visibility)
        parcel.writeString(folderId)
        // You might need to handle parceling for Map<String, Card>
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Topic> {
        override fun createFromParcel(parcel: Parcel): Topic {
            return Topic(parcel)
        }

        override fun newArray(size: Int): Array<Topic?> {
            return arrayOfNulls(size)
        }
    }
}
