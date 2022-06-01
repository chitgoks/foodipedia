package org.lifesum.foodipedia.web

import org.lifesum.foodipedia.model.Data
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiInterface {

    @GET(".")
    fun getFood(@Header("Authorization") token: String, @Query("foodid") id: Int) : Call<Data>

    companion object {
        const val AUTHORIZATION = "23863708:465c0554fd00da006338c72e282e939fe6576a25fd00c776c0fbe898c47c9876"
        private const val BASE_URL = "https://api.lifesum.com/v2/foodipedia/codetest/"
        fun create() : ApiInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}