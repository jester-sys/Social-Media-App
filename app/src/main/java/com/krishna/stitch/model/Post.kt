package com.krishna.stitch.model

data class Post(
    var postId: String? = null,
    var postImage: String? = null,
    var postedBy: String? = null,
    var postDescription: String? = null,
    var postedAt: Long? = null,
    val postLike: Int = 0,
    var commentCount:Int? =0,
    var hashtags: List<String> = emptyList()

)
