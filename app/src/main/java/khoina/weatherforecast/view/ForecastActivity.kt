package khoina.weatherforecast.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import khoina.weatherforecast.MainApp
import khoina.weatherforecast.*
import khoina.weatherforecast.ForecastModel
import khoina.weatherforecast.data.Resource
import khoina.weatherforecast.manager.AppConfigManager
import kotlinx.android.synthetic.main.activity_weather_forecast.*
import java.util.Map
import javax.inject.Inject

class ForecastActivity: AppCompatActivity(R.layout.activity_weather_forecast) {

    private val forecastAdapter = ForecastAdapter()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appConfigManager: AppConfigManager

    private val cacheSettingsMap = listOf(
        0L to "No cache",
        3L to "3 seconds",
        7L to "7 seconds"
    )

    private val forecastListViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ForecastListViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as MainApp).getAppComponent().inject(this)

        setupViews()
        setupObservers()
    }

    private fun setupObservers() {
        forecastListViewModel.getLiveForecastData().observe(this, forecastListObserver)
    }

    private fun setupViews() {
        rcvWeatherForecast.adapter = forecastAdapter
        val dividerDrawable = resources.getDrawable(R.drawable.list_vertical_divider)
        rcvWeatherForecast.addItemDecoration(VerticalDividerDecoration(dividerDrawable))

        btGetWeather.setOnClickListener {
            onGetWeatherForecast()
        }
        etPlace.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    onGetWeatherForecast()
                    true
                }
                else -> false
            }
        }

        btSettings.setOnClickListener {
            onClickSettings()
        }
    }

    private fun onClickSettings() {
        val items = cacheSettingsMap.map { it.second }.toTypedArray()
        val selected = cacheSettingsMap.map { it.first }.indexOf(appConfigManager.getCacheDuration())
        AlertDialog.Builder(this)
            .setTitle(R.string.alert_cache_settings_title)
            .setSingleChoiceItems(items, selected) { dlg, which ->
                appConfigManager.setCacheDuration(cacheSettingsMap[which].first)
                dlg.dismiss()
            }
            .show()
    }

    private fun onGetWeatherForecast() {
        val place = etPlace.text.toString().trim()
        forecastListViewModel.getForecastList(place)
    }

    private val forecastListObserver = Observer<Resource<List<ForecastModel>>> { resource ->
        Log.d("khoina", "resource count = ${resource.data?.size}")

        if (resource is Resource.Loading)
            pbLoading.visibility = View.VISIBLE
        else
            pbLoading.visibility = View.INVISIBLE

        if (resource is Resource.Error) {
            tvError.text = resource.exception.message
        } else {
            tvError.text = ""
        }

        resource.data ?. let {
            forecastAdapter.submitData(it)
        }
    }

}