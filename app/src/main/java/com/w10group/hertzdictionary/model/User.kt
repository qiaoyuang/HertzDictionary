package com.w10group.hertzdictionary.model

/**
 * Created by Administrator on 2018/1/31 0031.
 * 用户实体类
 */
data class User(val userID: String,
                val token: String,
                val username: String,
                val password: String,
                val avatarID: String,
                val phoneNumber: String,
                val wordList: List<Int>)

var appUser: User? = null