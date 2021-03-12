package com.core.base.usecases

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import timber.log.Timber

class LocationManager {
    internal var lm: LocationManager? = null
    internal lateinit var locationResult: LocationResult
    private var gps_enabled = false
    private var network_enabled = false

    internal var locationListenerGps: LocationListener = object : LocationListener {
        @SuppressLint("MissingPermission")
        override fun onLocationChanged(location: Location) {
            locationResult.gotLocation(location)
            lm?.removeUpdates(this)
            lm?.removeUpdates(locationListenerNetwork)
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    internal var locationListenerNetwork: LocationListener = object : LocationListener {

        @SuppressLint("MissingPermission")
        override fun onLocationChanged(location: Location) {
            locationResult.gotLocation(location)
            lm?.removeUpdates(this)
            lm?.removeUpdates(locationListenerGps)
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    fun getLocation(
        fragment: Fragment,
        result: LocationResult,
        context: Activity,
        onSuccess: () -> Unit = {},
        onCancel: () -> Unit = {},
        onFailPermission: () -> Unit = {},
        onFailure: (Throwable) -> Unit = {}
    ): Boolean {
        //I use LocationResult callback class to pass location value from LocationManager to user code.
        locationResult = result
        if (lm == null)
            lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        //exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = lm!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            network_enabled = lm!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        //don't start listeners if no provider is enabled
        if (!gps_enabled && !network_enabled) {
            val googleApiClient = GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build()
            googleApiClient.connect()

            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val builder = LocationSettingsRequest.Builder().addLocationRequest(
                locationRequest
            )
            builder.setAlwaysShow(true)

            LocationServices.getSettingsClient(context)
                .checkLocationSettings(builder.build()).addOnSuccessListener {
                    val states = it.locationSettingsStates
                    if (states.isLocationPresent) {
                        //Do something
                    }

                    getLocation(fragment)
                    onSuccess.invoke()
                }.addOnCanceledListener {
                    onCancel.invoke()
                }.addOnFailureListener {
                    if (it is ResolvableApiException) {
                        try {
                            // Handle result in onActivityResult()
                            it.startResolutionForResult(
                                context,
                                RESOLUTION_REQUEST
                            )
                        } catch (sendEx: IntentSender.SendIntentException) {
                            onFailure.invoke(it)
                        }
                    }
                    onFailure.invoke(it)
                }

            return false
        }

        getLocation(fragment, onFailPermission)

        return true
    }

    private fun getLocation(fragment: Fragment, onFailPermission: () -> Unit = {}) {
        if (checkPermission(fragment)) {
            if (gps_enabled)
                lm!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListenerGps
                )
            if (network_enabled)
                lm!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0f,
                    locationListenerNetwork
                )

            getLastLocation(fragment)
        } else {
            onFailPermission.invoke()
        }
    }

    private fun getLastLocation(context: Fragment) {
        lm!!.removeUpdates(locationListenerGps)
        lm!!.removeUpdates(locationListenerNetwork)

        var net_loc: Location? = null
        var gps_loc: Location? = null

        checkPermission(context)

        if (gps_enabled)
            gps_loc = lm!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (network_enabled)
            net_loc = lm!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        //if there are both values use the latest one
        if (gps_loc != null && net_loc != null) {
            if (gps_loc.getTime() > net_loc.getTime())
                locationResult.gotLocation(gps_loc)
            else
                locationResult.gotLocation(net_loc)
            return
        }

        if (gps_loc != null) {
            locationResult.gotLocation(gps_loc)
            return
        }
        if (net_loc != null) {
            locationResult.gotLocation(net_loc)
            return
        }
        locationResult.gotLocation(null)
    }

    fun checkPermission(context: Fragment): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context.context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                context.context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) run {
            context.requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERM_REQUEST
            )

            return false
        }

        return true
    }

    companion object {
        const val LOCATION_PERM_REQUEST: Int = 300
        const val RESOLUTION_REQUEST = 1414
    }

    abstract class LocationResult {
        abstract fun gotLocation(location: Location?)
    }
}