package com.krishna.stitch.model

//data class User(
//    val name: String,
//    val lastName: String,
//    val email: String,
//    val password: String,
//    val gender: String,
//    val birthDate: String,
//    val username: String,
//    val profession: String,
//    val profilePhoto: String
//)
//{
//    constructor(
//        name: String,
//        lastName: String,
//        email: String,
//        password: String,
//        gender: String,
//        birthDate: String,
//        username: String,
//        profession: String
//    ) : this(
//        name,
//        lastName,
//        email,
//        password,
//        gender,
//        birthDate,
//        username,
//        profession,
//        ""
//    )
//}

data class User @JvmOverloads constructor(
    var name: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val username: String = "",
    val profession: String = "",
    var profilePhoto: String = "",
    val bio:String ="",
    var UserID: String? = null,
    val followerCount: Int? = 0,
    val followingCount: Int? = 0,
    val postCount : Int? = 0,
    var onlineStatus: Boolean = false,
    var hasNewMessage: Boolean = false,
    var latestMessageTimestamp: Long = 0,
    var latestMessage: String? = null,

)