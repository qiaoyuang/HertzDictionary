package com.w10group.hertzdictionary.util

import com.w10group.hertzdictionary.model.InquireResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface InquireWordService {
    @GET("translate_a/single")
    fun inquire(@Query("q") word: String,
                @Query("dt") param: String = "t",
                @Query("client") client: String = "gtx",
                @Query("sl") source: String = "en",
                @Query("tl") result: String = "zh-CN",
                @Query("dj") dj: Int = 1): Observable<InquireResult>
}