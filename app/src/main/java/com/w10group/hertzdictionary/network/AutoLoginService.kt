package com.w10group.hertzdictionary.network

import com.w10group.hertzdictionary.model.User
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.lang.ref.WeakReference

/**
 * Created by Administrator on 2018/2/6 0006.
 * 自动登录用的Retrofit接口
 */

object AutoLoginService {

    private var reference: WeakReference<IAutoLoginService>?

    init {
        reference = null
    }

    fun get(): IAutoLoginService =
            if (reference == null || reference!!.get() == null) {
                reference = WeakReference(NetworkUtil.create())
                reference!!.get()!!
            } else {
                reference!!.get()!!
            }

    interface IAutoLoginService {
        @FormUrlEncoded
        @POST("autologin")
        fun login(@Field("user_id") userId: String, @Field("user_password") password: String): Observable<User>
    }

}