package com.w10group.hertzdictionary.network

import com.w10group.hertzdictionary.model.Word
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface GetWordListService {

    @GET("list/{token}/{list_id}")
    fun get(@Path("token") token: String, @Path("list_id") listId: Int): Observable<List<Word>>

}