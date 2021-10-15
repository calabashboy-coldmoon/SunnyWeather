package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext


// 仓库层的统一封装入口
object Repository {

    // fire()函数是一个按照liveData()函数的参数接收标准定义的一个高阶函数。在fire()函数的内部会先调用一下liveData()函数，
    // 然后在liveData()函数的代码块中统一进行了try catch 处理，并在try语句中调用传入的Lambda表达式中的代码，
    // 最终获取Lambda 表达式的执行结果并调用emit()方法发射出去。
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(
                    realtimeResponse.result.realtime,
                    dailyResponse.result.daily
                )
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}


//    fun searchPlaces(query: String) = liveData(Dispatchers.IO){
//        val result =try{
//            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
//            if (placeResponse.Status == "ok"){
//                val places = placeResponse.places
//                Result.success(places)
//            } else{
//                Result.failure(RuntimeException("response status is ${placeResponse.Status}"))
//            }
//        } catch (e: Exception){
//            Result.failure<List<Place>>(e)
//        }
//        emit(result)
//    }
//
//
//    // 提供了一个refreshWeather()方法用来刷新天气信息
//    // 分别在两个async函数中发起网络请求，然后再分别调用它们的await()方法，就可以保证只有在两个网络请求都成功响应之后，才会进一步
//    // 执行程序。另外，由于async函数必须在协程作用域内才能调用，所以这里又使用coroutineScope函数创建了一个协程作用域。
//    fun refreshWeather(lng: String, lat: String) = liveData(Dispatchers.IO) {
//        val result = try {
//            coroutineScope {
//                val deferredRealtime = async {
//                    SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
//                }
//                val deferredDaily = async {
//                    SunnyWeatherNetwork.getDailyWeather(lng, lat)
//                }
//                val realtimeResponse = deferredRealtime.await()
//                val dailyResponse = deferredDaily.await()
//                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
//                    val weather = Weather(realtimeResponse.result.realtime,
//                        dailyResponse.result.daily)
//                    Result.success(weather)
//                } else {
//                    Result.failure(
//                        RuntimeException(
//                            "realtime response status is ${realtimeResponse.status}" +
//                                    "daily response status is ${dailyResponse.status}"
//                        )
//                    )
//                }
//            }
//        } catch (e: Exception) {
//            Result.failure<Weather>(e)
//        }
//        emit(result)
//    }
//}