package khoina.weatherforecast

import khoina.weatherforecast.data.entity.DayTempEntity
import khoina.weatherforecast.data.entity.ForecastEntity
import khoina.weatherforecast.data.ForecastEntityMapper
import khoina.weatherforecast.data.entity.WeatherEntity
import org.junit.Assert
import org.junit.Test

class RecordMappingUnitTest {
    @Test
    fun recordMapping_isCorrectPlace() {
        val mapper = ForecastEntityMapper()
        val place = "Saigon"
        val entity = ForecastEntity(
            1588219524L,
            DayTempEntity(0f),
            0,
            0,
            listOf()
        )
        val record = mapper.mapRecord(place, entity)
        Assert.assertEquals(place, record.place)
    }

    @Test
    fun recordMapping_isCorrectEmptyPlace() {
        val mapper = ForecastEntityMapper()
        val place = ""
        val entity = ForecastEntity(
            1588219524L,
            DayTempEntity(0f),
            0,
            0,
            listOf()
        )
        val record = mapper.mapRecord(place, entity)
        Assert.assertEquals(place, record.place)
    }

    @Test
    fun recordMapping_isCorrectArbitraryPlace() {
        val mapper = ForecastEntityMapper()
        val place = "!@)(NN  "
        val entity = ForecastEntity(
            1588219524L,
            DayTempEntity(0f),
            0,
            0,
            listOf()
        )
        val record = mapper.mapRecord(place, entity)
        Assert.assertEquals(place, record.place)
    }

    @Test
    fun recordMapping_isCorrectDateConversion() {
        val mapper = ForecastEntityMapper()
        val dateInSec = 1588219524L
        val entity = ForecastEntity(
            dateInSec,
            DayTempEntity(0f),
            0,
            0,
            listOf()
        )
        val record = mapper.mapRecord("", entity)
        Assert.assertEquals(entity.dt, dateInSec)
        Assert.assertEquals(record.date, dateInSec)
    }

    @Test
    fun recordMapping_isCorrectAveTempCalculation() {
        val mapper = ForecastEntityMapper()
        val dayTemp = 27f
        val entity = ForecastEntity(
            1588219524L,
            DayTempEntity(dayTemp),
            0,
            0,
            listOf()
        )
        val record = mapper.mapRecord("", entity)
        Assert.assertEquals(entity.temp.day, dayTemp)
        Assert.assertEquals(record.aveTemp, dayTemp)
    }

    @Test
    fun recordMapping_isCorrectNegativeAveTempCalculation() {
        val mapper = ForecastEntityMapper()
        val dayTemp = -15f
        val entity = ForecastEntity(
            1588219524L,
            DayTempEntity(dayTemp),
            0,
            0,
            listOf()
        )
        val record = mapper.mapRecord("", entity)
        Assert.assertEquals(entity.temp.day, dayTemp)
        Assert.assertEquals(record.aveTemp, dayTemp)
    }

    @Test
    fun recordMapping_isCorrectArbitraryAveTempCalculation() {
        val mapper = ForecastEntityMapper()
        val dayTemp = -20012f
        val entity = ForecastEntity(
            1588219524L,
            DayTempEntity(dayTemp),
            0,
            0,
            listOf()
        )
        val record = mapper.mapRecord("", entity)
        Assert.assertEquals(entity.temp.day, dayTemp)
        Assert.assertEquals(record.aveTemp, dayTemp)
    }

    @Test
    fun recordMapping_isCorrectPressure() {
        val mapper = ForecastEntityMapper()
        val pressure = 1001
        val entity = ForecastEntity(
            1588219524L,
            DayTempEntity(0f),
            pressure,
            0,
            listOf()
        )
        val record = mapper.mapRecord("", entity)
        Assert.assertEquals(entity.pressure, pressure)
        Assert.assertEquals(record.pressure, pressure)
    }

    @Test
    fun recordMapping_isCorrectHumidity() {
        val mapper = ForecastEntityMapper()
        val humidity = 60
        val entity = ForecastEntity(
            1588219524L,
            DayTempEntity(0f),
            0,
            humidity,
            listOf()
        )
        val record = mapper.mapRecord("", entity)
        Assert.assertEquals(entity.humidity, humidity)
        Assert.assertEquals(record.humidity, humidity)
    }

    @Test
    fun recordMapping_isCorrectWeatherDescription() {
        val mapper = ForecastEntityMapper()
        val weatherDescription = "light rain"
        val entity = ForecastEntity(
            1588219524L,
            DayTempEntity(0f),
            0,
            0,
            listOf(
                WeatherEntity(
                    weatherDescription
                )
            )
        )
        val record = mapper.mapRecord("", entity)
        Assert.assertEquals(record.description, weatherDescription)
    }

    @Test
    fun recordMapping_isCorrectEmptyWeatherDescription() {
        val mapper = ForecastEntityMapper()
        val entity = ForecastEntity(
            1588219524L,
            DayTempEntity(0f),
            0,
            0,
            listOf()
        )
        val record = mapper.mapRecord("", entity)
        Assert.assertEquals(record.description, "")
    }
}