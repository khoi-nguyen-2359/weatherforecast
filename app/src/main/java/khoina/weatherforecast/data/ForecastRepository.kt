package khoina.weatherforecast.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.room.Room
import khoina.weatherforecast.ForecastModel
import khoina.weatherforecast.data.retrofit.ForecastApi
import khoina.weatherforecast.data.room.ForecastDao
import khoina.weatherforecast.data.room.ForecastDatabase
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForecastRepository @Inject constructor(
    private val forecastApi: ForecastApi,
    private val forecastDao: ForecastDao
) {
    private val entityMapper = ForecastEntityMapper()

    fun getForecastList(place: String, cnt: Int) = liveData<Resource<List<ForecastModel>>>(Dispatchers.IO) {
        val hasFreshData = forecastDao.hasFreshData(place, cnt, System.currentTimeMillis() / 1000 - TIME_OUT_SEC)

        if (hasFreshData) {
            Log.d("khoina", "cache hit")

            emitSource(
                forecastDao.getForecastList(place).map {
                    Resource.Success(it.map(entityMapper::mapModel))
                }
            )
        } else {
            Log.d("khoina", "cache miss")

            try {
                emitSource(
                    forecastDao.getForecastList(place).map {
                        Resource.Loading(
                            it.map(
                                entityMapper::mapModel
                            )
                        )
                    }
                )

                val response = forecastApi.getDailyForecast(place, cnt)
                val records = response.list.map { entityMapper.mapRecord(place, it) }
                forecastDao.replaceAllForecast(place, records)
                Log.d("khoina", "cache records = ${records.size}")

                emitSource(
                    forecastDao.getForecastList(place).map {
                        Resource.Success(
                            it.map(
                                entityMapper::mapModel
                            )
                        )
                    }
                )
            } catch (ex: Exception) {
                emitSource(forecastDao.getForecastList(place).map {
                    Resource.Error<List<ForecastModel>>(
                        ex
                    )
                })
            }
        }
    }

    companion object {
        const val TIME_OUT_SEC = 5
    }
}