package com.w10group.hertzdictionary.util

import com.w10group.hertzdictionary.bean.InquireResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface InquireWordService {
    @GET("translate_a/single")
    fun inquire(@Query("q") word: String,
                @Query("client") client: String = "gtx",
                @Query("sl") source: String = "en",
                @Query("tl") result: String = "zh-CN",
                @Query("ie") characterSet: String = "UTF-8",
                @Query("dj") dj: Int = 1,
                @Query("dt") param1: String = "t",
                @Query("dt") param2: String = "at",
                @Query("dt") param6: String = "rw",
                @Query("dt") param7: String = "bd",
                @Query("dt") param8: String = "rm"): Observable<InquireResult>
}