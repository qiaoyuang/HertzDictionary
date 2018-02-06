package com.w10group.hertzdictionary

import android.content.Context
import android.content.SharedPreferences
import android.support.design.widget.Snackbar
import android.view.View
import com.w10group.hertzdictionary.model.appUser
import com.w10group.hertzdictionary.network.AutoLoginService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Administrator on 2018/2/6 0006.
 * 登录类
 */
class Login(private val mContext: Context, private val mView: View) {

    private val mSharedPreferences = mContext.getSharedPreferences("UserFile", Context.MODE_PRIVATE)

    fun start() {
        val userId = mSharedPreferences.getString("user_id", "")
        if (userId.isBlank()) {

        } else {
            autoLogin(userId)
        }
    }

    private fun login() {

    }

    private fun autoLogin(userId: String) {
        val password = mSharedPreferences.getString("user_password", "")
        AutoLoginService.get().login(userId, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            if (it.token != "0") {
                                appUser = it
                                val editor = mSharedPreferences.edit()
                                editor.putString("user_name", it.username)
                                editor.putString("user_avatar_id", it.avatarID)
                                if (!it.phoneNumber.isBlank()) {
                                    editor.putString("user_phone", it.phoneNumber)
                                }
                                editor.apply()
                            } else {
                                login()
                            }
                        },
                        onError = { it.printStackTrace() },
                        onComplete = { Snackbar.make(mView, "登录成功", Snackbar.LENGTH_SHORT) }
                )
    }

}