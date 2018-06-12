package com.w10group.hertzdictionary.util

import com.w10group.hertzdictionary.model.InquireResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface InquireWordService {
    @GET("#en/zh-CN/{word}")
    fun inquire(@Path("word") word: String): Observable<InquireResponse>
}