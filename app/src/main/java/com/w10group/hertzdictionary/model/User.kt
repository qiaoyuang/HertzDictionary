package com.w10group.hertzdictionary.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Administrator on 2018/1/31 0031.
 * 用户实体类
 */
data class User(@SerializedName("user_id") val userID: String = "",
                @SerializedName("user_token") val token: String = "",
                @SerializedName("user_name") val username: String = "",
                @SerializedName("user_avatar_id") val avatarID: String= "",
                @SerializedName("user_phone_number") val phoneNumber: String = "",
                @SerializedName("word_list") val wordList: List<Int>? = null,
                val password: String? = null)