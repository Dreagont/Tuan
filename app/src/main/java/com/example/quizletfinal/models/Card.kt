package com.example.quizletfinal.models

import android.os.Parcel
import android.os.Parcelable

data class Card(val english: String, val vietnamese: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    constructor() : this("", "")


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(english)
        parcel.writeString(vietnamese)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Card> {
        override fun createFromParcel(parcel: Parcel): Card {
            return Card(parcel)
        }

        override fun newArray(size: Int): Array<Card?> {
            return arrayOfNulls(size)
        }
    }
}
public var extendedCardList = listOf(
    Card("Sun", "Mặt trời"),
    Card("Moon", "Mặt trăng"),
    Card("Stars", "Ngôi sao"),
    Card("Sky", "Bầu trời"),
    Card("Cloud", "Đám mây"),
    Card("Rain", "Mưa"),
    Card("Wind", "Gió"),
    Card("Ocean", "Đại dương"),
    Card("Mountain", "Núi"),
    Card("Beach", "Bãi biển"),
    Card("Forest", "Rừng"),
    Card("Flower", "Hoa"),
    Card("Color", "Màu sắc"),
    Card("Art", "Nghệ thuật"),
    Card("Dance", "Nhảy múa"),
    Card("Friendship", "Tình bạn"),
    Card("Health", "Sức khỏe"),
    Card("Technology", "Công nghệ"),
    Card("Science", "Khoa học"),
    Card("History", "Lịch sử"),
    Card("Book", "Sách"),
    Card("Music", "Âm nhạc"),
    Card("Movie", "Phim"),
    Card("Food", "Thức ăn"),
    Card("Travel", "Du lịch"),
    Card("Adventure", "Phiêu lưu"),
    Card("Love", "Tình yêu"),
    Card("Happiness", "Hạnh phúc"),
    Card("Peace", "Hòa bình"),
    Card("Family", "Gia đình"),
    Card("Pet", "Thú cưng"),
    Card("Home", "Nhà cửa"),
    Card("Garden", "Vườn"),
    Card("Planet", "Hành tinh"),
    Card("Universe", "Vũ trụ"),
    Card("Galaxy", "Dải Ngân hà"),
    Card("Time", "Thời gian"),
    Card("Space", "Không gian"),
    Card("Dream", "Giấc mơ"),
    Card("Adventure", "Phiêu lưu"),
    Card("Magic", "Phép thuật"),
    Card("Wisdom", "Sự khôn ngoan"),
    Card("Courage", "Can đảm"),
    Card("Hope", "Hi vọng"),
    Card("Laughter", "Tiếng cười"),
    Card("Inspiration", "Inspirasi"),
    Card("Knowledge", "Kiến thức"),
    Card("Education", "Giáo dục"),
    Card("Discovery", "Khám phá"),
    Card("Innovation", "Đổi mới"),
    Card("Dream", "Giấc mơ"),
    Card("Purpose", "Mục đích"),
    Card("Journey", "Hành trình"),
    Card("Challenge", "Thách thức"),
    Card("Achievement", "Thành tựu"),
    Card("Balance", "Cân bằng"),
    Card("Harmony", "Hài hòa"),
    Card("Gratitude", "Lòng biết ơn"),
    Card("Mindfulness", "Tâm lý chánh niệm"),
    Card("Resilience", "Sự kiên cường"),
    Card("Renewal", "Sự làm mới"),
    Card("Freedom", "Tự do"),
    Card("Equality", "Bình đẳng"),
    Card("Justice", "Công bằng"),
    Card("Peace", "Hòa bình"),
    Card("Nature", "Thiên nhiên"),
    Card("Environment", "Môi trường"),
    Card("Adventure", "Phiêu lưu"),
    Card("Discovery", "Khám phá"),
    Card("Innovation", "Đổi mới"),
    Card("Dream", "Giấc mơ"),
    Card("Purpose", "Mục đích"),
    Card("Journey", "Hành trình"),
    Card("Challenge", "Thách thức"),
    Card("Achievement", "Thành tựu"),
    Card("Balance", "Cân bằng"),
    Card("Harmony", "Hài hòa"),
    Card("Gratitude", "Lòng biết ơn"),
    Card("Mindfulness", "Tâm lý chánh niệm"),
    Card("Resilience", "Sự kiên cường"),
    Card("Renewal", "Sự làm mới"),
    Card("Freedom", "Tự do"),
    Card("Equality", "Bình đẳng"),
    Card("Justice", "Công bằng"),
    Card("Peace", "Hòa bình"),
    Card("Nature", "Thiên nhiên"),
    Card("Environment", "Môi trường")
)

