package com.w10group.hertzdictionary.ui

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.w10group.hertzdictionary.R
import com.w10group.hertzdictionary.model.appUser
import com.w10group.hertzdictionary.network.AutoLoginService
import com.w10group.hertzdictionary.network.LoginService
import com.w10group.hertzdictionary.network.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar

/**
 * Created by Administrator on 2018/2/6 0006.
 * 登录类
 */
class Login(private val mContext: Context, private val mView: View) {

    private val mSharedPreferences = mContext.getSharedPreferences("UserFile", Context.MODE_PRIVATE)

    private val mProgressDialog = mContext.progressDialog(message = "正在登录中......", title = "请稍等")

    private var mPhoneNumber = ""
    private var mPassword = ""

    private lateinit var etPhoneNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var buttonLogin: Button

    fun start() {
        mProgressDialog.setCancelable(false)
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

    //自动登录的网络连接函数
    private fun autoLogin(userId: String) {
        val password = mSharedPreferences.getString("user_password", "")
        NetworkUtil.create<AutoLoginService>().login(userId, password)
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
                        onError = {
                            it.printStackTrace()
                            login()
                        },
                        onComplete = { snackbar(mView, "登录成功") }
                )
    }

    //登录的网络连接函数
    private fun startLogin() {
        NetworkUtil.create<LoginService>().login(mPhoneNumber, mPassword)
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
                                mContext.alert(title = "登录失败", message = "登录失败，请重试。").show()
                            }
                        },
                        onError = {
                            it.printStackTrace()
                            mContext.alert(title = "登录失败", message = "登录失败，请重试。").show()
                        },
                        onComplete = { snackbar(mView, "登录成功") }
                )
    }

    private fun validate() {
        fun action(message: String) {
            mProgressDialog.dismiss()
            mContext.alert(title = "登录失败", message = message).show()
            return
        }

        if (!(NetworkUtil.checkNetwork(mContext)))
            action("手机无网络，请检查网络链接")

        mPhoneNumber = etPhoneNumber.text.toString().trim()
        if (mPhoneNumber.isBlank())
            action("手机号不能为空")

        mPassword = etPassword.text.toString().trim()
        if (mPassword.isBlank())
            action("密码不能为空")

        if (mPassword.length < 8 || mPassword.length > 16)
            action("密码长度必须位于8至16位之间")

        startLogin()
    }


    //创建登录对话框的UI布局
    private fun createLoginUI(): View = AnkoContext.create(mContext).apply {
        verticalLayout {
            isClickable = true
            backgroundColorResource = R.color.deepWhite

            textView("登录") {
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

            buttonLogin = button("登录") {
                elevation = dip(4).toFloat()
                translationZ = dip(4).toFloat()
                textColorResource = android.R.color.white
                textSize = sp(16).toFloat()
                backgroundColorResource = R.color.blue1
                setOnClickListener {
                    mProgressDialog.show()
                    validate()
                }
            }.lparams(wrapContent, wrapContent) {
                gravity = Gravity.HORIZONTAL_GRAVITY_MASK
                marginStart = dip(16)
                marginEnd = dip(16)
                topMargin = dip(64)
            }

        }
    }.view

}