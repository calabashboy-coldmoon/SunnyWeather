package com.sunnyweather.android.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// 为了使用PlaceService接口，创建一个Retrofit构建器
object ServiceCreator {
    private const val BASE_URL = "https://api.caiyunapp.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(sreviceClass: Class<T>): T = retrofit.create(sreviceClass)

    inline fun <reified T> create(): T = create(T::class.java)
}