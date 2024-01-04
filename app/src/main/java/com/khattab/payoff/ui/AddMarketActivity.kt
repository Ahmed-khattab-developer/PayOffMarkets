package com.khattab.payoff.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.khattab.payoff.R
import com.khattab.payoff.data.model.City
import com.khattab.payoff.data.model.Market
import com.khattab.payoff.databinding.ActivityAddMarketBinding
import com.khattab.payoff.utils.appUtils.NoInternetDialog
import com.khattab.payoff.viewModel.ConnectionViewModel
import com.khattab.payoff.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class AddMarketActivity : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback {

    private var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_1 = 1
    private var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_2 = 2

    private var myMap: GoogleMap? = null
    private var currentLocation: Location? = null
    private lateinit var fusedLocationProvideClint: FusedLocationProviderClient
    private var locationManager: LocationManager? = null

    private lateinit var dataBinding: ActivityAddMarketBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val connectionViewModel: ConnectionViewModel by viewModels()
    private var noInternetDialog: NoInternetDialog? = null

    private var cities = ArrayList<City>()
    private var cityArrays = ArrayList<String>()
    private var districtArrays = ArrayList<String>()
    private var city: String? = null
    private var district: String? = null
    private var cityPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_market)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        mainViewModel.getCitiesData()
        noInternetDialog = NoInternetDialog(this)
        noInternetDialog!!.setCancelable(false)
        noInternetDialog!!.window?.setBackgroundDrawable(
            ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent))
        )
        connectionViewModel.isOnline.observe(this) { isOnline ->
            if (isOnline) {
                noInternetDialog!!.hide()
            } else {
                noInternetDialog!!.show()
            }
        }

        dataBinding.citySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    city = cities[position].city
                    district = cities[position].district[0]
                    cityPosition = position

                    districtArrays.clear()
                    for (district in cities[position].district) {
                        districtArrays.add(district)
                    }
                    dataBinding.districtSpinner.item = districtArrays as List<Any>?
                    dataBinding.districtSpinner.setSelection(0)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            }

        dataBinding.districtSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    district = cities[cityPosition!!].district[position]
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {}
            }

        dataBinding.addButton.setOnClickListener(this)


        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this@AddMarketActivity)

        fusedLocationProvideClint = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(10000)
        locationRequest.setFastestInterval(10000 / 2)

        val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
        locationSettingsRequestBuilder.addLocationRequest(locationRequest)
        locationSettingsRequestBuilder.setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build())
        task.addOnSuccessListener(this) {
            getLastLocation()
        }

        task.addOnFailureListener(this) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        this@AddMarketActivity, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_1
                    )
                } catch (sendIntentException: SendIntentException) {
                    sendIntentException.printStackTrace()
                }
            }
        }
        getLocationPermission()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                mainViewModel.cities.collect {
                    if (it.isLoading) {
                        dataBinding.progressBar.visibility = View.VISIBLE
                    }
                    if (it.error.isNotBlank()) {
                        dataBinding.progressBar.visibility = View.GONE
                        Toast.makeText(this@AddMarketActivity, it.error, Toast.LENGTH_LONG).show()
                        Log.e("aaaaaaaaaaaaaaaa", it.error)
                    }
                    it.data?.let {
                        dataBinding.progressBar.visibility = View.GONE

                        cities = it

                        for (city in it) {
                            cityArrays.add(city.city)
                        }

                        for (district in it[0].district) {
                            districtArrays.add(district)
                        }

                        dataBinding.citySpinner.item = cityArrays as List<Any>?
                        dataBinding.citySpinner.setSelection(0)
                        dataBinding.districtSpinner.item = districtArrays as List<Any>?
                        dataBinding.districtSpinner.setSelection(0)

                        city = cities[0].city
                        district = cities[0].district[0]
                        cityPosition = 0
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                mainViewModel.market.collect {
                    if (it.isLoading) {
                        dataBinding.progressBar.visibility = View.VISIBLE
                    }
                    if (it.error.isNotBlank()) {
                        dataBinding.progressBar.visibility = View.GONE
                        Toast.makeText(this@AddMarketActivity, it.error, Toast.LENGTH_LONG).show()
                        Log.e("aaaaaaaaaaaaaaaa", it.error)
                    }
                    it.data?.let {
                        dataBinding.progressBar.visibility = View.GONE
                        Toast.makeText(this@AddMarketActivity, "successfully", Toast.LENGTH_LONG)
                            .show()
                        finish()
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        if (v == dataBinding.addButton) {
            val marketName = dataBinding.marketNameET.text.toString()
            val marketNumber = dataBinding.marketNumberET.text.toString()
            val marketStreet = dataBinding.marketStreetET.text.toString()
            val marketDescription = dataBinding.marketDescriptionET.text.toString()
            if (marketName == "") {
                Toast.makeText(this, getString(R.string.add_market_name), Toast.LENGTH_LONG)
                    .show()
            } else if (marketNumber == "") {
                Toast.makeText(this, getString(R.string.add_market_number), Toast.LENGTH_LONG)
                    .show()
            } else if (city == null) {
                Toast.makeText(this, getString(R.string.add_market_city), Toast.LENGTH_LONG)
                    .show()
            } else if (district == null) {
                Toast.makeText(this, getString(R.string.add_market_district), Toast.LENGTH_LONG)
                    .show()
            } else if (marketStreet == "") {
                Toast.makeText(this, getString(R.string.add_market_street), Toast.LENGTH_LONG)
                    .show()
            } else if (marketDescription == "") {
                Toast.makeText(this, getString(R.string.add_market_description), Toast.LENGTH_LONG)
                    .show()
            } else if (currentLocation == null) {
                Toast.makeText(
                    this, getString(R.string.add_market_location_on_map), Toast.LENGTH_LONG
                ).show()
            } else {
                val id: String = FirebaseFirestore.getInstance().collection("market").document().id
                val geoPoint = GeoPoint(currentLocation!!.latitude, currentLocation!!.longitude)

                val date = Calendar.getInstance().time

                val market = Market(
                    id, marketName, marketNumber, city!!, district!!, marketStreet,
                    marketDescription, geoPoint, date, false
                )
                mainViewModel.addMarket(market, id)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        myMap!!.isMyLocationEnabled = true
        myMap!!.uiSettings.isMyLocationButtonEnabled = true;
        myMap!!.uiSettings.isZoomGesturesEnabled = false
        myMap!!.uiSettings.isZoomControlsEnabled = false
        myMap!!.uiSettings.isScrollGesturesEnabled = false
        myMap!!.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = false
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val task: Task<Location> = fusedLocationProvideClint.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location

                val sydney = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
                myMap!!.addMarker(MarkerOptions().position(sydney).title("current location"))
                myMap!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom
                        (LatLng(currentLocation!!.latitude, currentLocation!!.longitude), 12.0f)
                )
            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_1 -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                    }

                    Activity.RESULT_CANCELED -> {
                    }
                }
                getLastLocation()
            }
        }
    }

    private fun getLocationPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION,
            ) -> {
            }

            else -> {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_2
                )
            }
        }
        getLastLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_2 -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }

            else -> Toast.makeText(this, "permission denied", Toast.LENGTH_LONG)
                .show()
        }
        getLastLocation()
    }
}