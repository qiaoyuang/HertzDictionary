package com.w10group.hertzdictionary.network

import com.w10group.hertzdictionary.model.User
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.lang.ref.WeakReference

/**
 * Created by Administrator on 2018/2/6 0006.
 * 登录用的Retrofit接口
 */

object LoginService {

    private var reference: WeakReference<ILoginService>?

    init {
        reference = null
    }

    fun get(): ILoginService =
            if (reference == null || reference!!.get() == null) {
                reference = WeakReference(NetUtil.create())
                reference!!.get()!!
            } else {
                reference!!.get()!!
            }

    interface ILoginService {
        @FormUrlEncoded
        @POST("login")
        fun login(@Field("user_phone") phone: String, @Field("user_password") password: String): Observable<User>
    }

}