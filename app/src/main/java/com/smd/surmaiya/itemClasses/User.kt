package com.smd.surmaiya.itemClasses


enum class UserType {
    ARTIST,
    CONSUMER,
    GUEST
}

data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var country: String = "",
    var password: String = "",
    var phone: String = "",
    var profilePictureUrl: String = "",
    var fcmToken: String = "",
    var userType: UserType = UserType.GUEST
) {
    constructor() : this("", "", "", "", "", "", "", "", UserType.GUEST)
}