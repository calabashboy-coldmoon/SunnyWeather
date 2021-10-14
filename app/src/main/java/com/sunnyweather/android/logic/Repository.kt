package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers


// 仓库层的统一封装入口
object Repository {
    fun searchPlaces(query: String) = liveData(Dispatchers.IO){
        val result =try{
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.Status == "ok"){
                val places = placeResponse.places
                Result.success(places)
            } else{
                Result.failure(RuntimeException("response status is ${placeResponse.Status}"))
            }
        } catch (e: Exception){
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }
}