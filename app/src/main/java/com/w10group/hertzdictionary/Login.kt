package com.w10group.hertzdictionary

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.w10group.hertzdictionary.model.appUser
import com.w10group.hertzdictionary.network.AutoLoginService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*

/**
 * Created by Administrator on 2018/2/6 0006.
 * 登录类
 */
class Login(private val mContext: Context, private val mView: View) {

    private val mSharedPreferences = mContext.getSharedPreferences("UserFile", Context.MODE_PRIVATE)

    private lateinit var etPhoneNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var buttonLogin: Button

    fun start() {
        val userId = mSharedPreferences.getString("user_id", "")
        if (userId.isBlank()) {
            login()
        } else {
            autoLogin(userId)
        }
    }

    private fun login() {
        val builder = AlertDialog.Builder(mContext, R.style.Dialog_Fullscreen)
        builder.setView(createLoginUI()).setCancelable(false).create()
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

    //创建登录对话框的UI布局
    private fun createLoginUI(): View = AnkoContext.create(mContext).apply {
        verticalLayout {
            isClickable = true
            backgroundColorResource = R.color.deepWhite

            textView {
                text = "登录"
                textColorResource = R.color.blue1
            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.HORIZONTAL_GRAVITY_MASK
                topMargin = dip(96)
                bottomMargin = dip(96)
            }

            etPhoneNumber = editText {
                val drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_phone_android_grey600_24dp)
                drawable?.let {
                    it.setBounds(0, 0, it.minimumWidth, it.minimumHeight)
                    setCompoundDrawables(it, null, null, null)
                }
                compoundDrawablePadding = dip(4)
                textSize = sp(16).toFloat()
                hint = "请输入手机号码"
            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.HORIZONTAL_GRAVITY_MASK
                marginStart = dip(16)
                marginEnd = dip(16)
            }

            etPassword = editText {
                val drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_lock_grey600_24dp)
                drawable?.let {
                    it.setBounds(0, 0, it.minimumWidth, it.minimumHeight)
                    setCompoundDrawables(it, null, null, null)
                }
                compoundDrawablePadding = dip(4)
                textSize = sp(16).toFloat()
                inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                hint = "请输入密码"
            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.HORIZONTAL_GRAVITY_MASK
                marginStart = dip(16)
                marginEnd = dip(16)
            }

            buttonLogin = button {
                elevation = dip(4).toFloat()
                translationZ = dip(4).toFloat()
                textColorResource = android.R.color.white
                textSize = sp(16).toFloat()
                backgroundColorResource = R.color.blue1
                text = "登录"
            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.HORIZONTAL_GRAVITY_MASK
                marginStart = dip(16)
                marginEnd = dip(16)
                topMargin = dip(64)
            }

        }
    }.view

}