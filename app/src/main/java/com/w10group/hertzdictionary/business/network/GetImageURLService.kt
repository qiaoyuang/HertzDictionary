package com.w10group.hertzdictionary.business.network

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface GetImageURLService {
    @GET
    fun get(@Url url: String): Observable<ResponseBody>
}