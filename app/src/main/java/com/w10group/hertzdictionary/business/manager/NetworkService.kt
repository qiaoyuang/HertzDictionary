package com.w10group.hertzdictionary.business.manager

import com.w10group.hertzdictionary.business.bean.InquireResult
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface NetworkService {

    @GET
    fun getImageURL(@Url url: String): Observable<ResponseBody>

    @GET("translate_a/single")
    fun inquireWord(@Query("q") word: String,
                    @Query("dj") dj: Int = 1,
                    @Query("client") client: String = "gtx",
                    @Query("sl") source: String = "en",
                    @Query("tl") result: String = "zh-CN",
                    @Query("ie") characterSet: String = "UTF-8",
                    @Query("dt") param1: String = "t",
                    @Query("dt") param2: String = "at",
                    @Query("dt") param6: String = "rw",
                    @Query("dt") param7: String = "bd",
                    @Query("dt") param8: String = "rm"): Observable<InquireResult>

}