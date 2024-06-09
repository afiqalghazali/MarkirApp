package com.scifi.markirapp.data.model

data class FavoriteModel(
    val id: String? = null,
    val name: String? = null,
    val imageUrl: String = "",
    var isBookmarked: Boolean = false
) {
    // No-argument constructor required by Firebase
    constructor() : this(null, null, "", false)
}


