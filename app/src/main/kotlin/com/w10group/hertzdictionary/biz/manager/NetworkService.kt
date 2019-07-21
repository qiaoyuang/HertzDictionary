package com.w10group.hertzdictionary.biz.manager

import com.w10group.hertzdictionary.biz.data.InquireResult
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Created by Administrator on 2018/6/26.
 * Retrofit网络接口服务
 */

interface NetworkService {

    @GET
    suspend fun getImageURLByCoroutinesAsync(@Url url: String): ResponseBody

    @GET("translate_a/single")
    suspend fun inquireWordByCoroutinesAsync(@Query("q") word: String,
                                     @Query("dj") dj: Int = 1,
                                     @Query("client") client: String = "gtx",
                                     @Query("sl") source: String = "en",
                                     @Query("tl") result: String = "zh-CN",
                                     @Query("ie") characterSet: String = "UTF-8",
                                     @Query("dt") param1: String = "t",
                                     @Query("dt") param2: String = "at",
                                     @Query("dt") param6: String = "rw",
                                     @Query("dt") param7: String = "bd",
                                     @Query("dt") param8: String = "rm"): InquireResult

}