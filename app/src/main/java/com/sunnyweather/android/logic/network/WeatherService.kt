package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.DailyResponse
import com.sunnyweather.android.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


// 用于访问天气信息API的Retrofit接口
interface WeatherService {

    // @GET注解来声明要访问的API接口，并且我们还使用了@Path注解来向请求接口中动态传入经纬度的坐标。这两个方法的返回值分别被
    // 声明成了Call<RealtimeResponse>和Call<DailyResponse>，对应了刚刚定义好的两个数据模型类。
    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String):
            Call<RealtimeResponse>

    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String):
            Call<DailyResponse>
}