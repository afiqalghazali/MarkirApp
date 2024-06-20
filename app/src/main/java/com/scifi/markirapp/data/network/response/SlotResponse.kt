package com.scifi.markirapp.data.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SlotResponse(

	@field:SerializedName("num")
	val num: String? = null,

	@field:SerializedName("column")
	val column: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("row")
	val row: Int? = null,

	@field:SerializedName("floor")
	val floor: Int? = null,

	@field:SerializedName("occupied")
	val occupied: Int? = null,
) : Parcelable
