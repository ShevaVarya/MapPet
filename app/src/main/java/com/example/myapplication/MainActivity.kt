package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.Style

import com.mapbox.mapboxsdk.maps.MapView


class MainActivity : AppCompatActivity() {
    private var mapView: MapView? = null

    private val locationOne = LatLng(55.932475, 37.870141);
    private val locationTwo = LatLng(55.559958, 37.341934);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        setContentView(R.layout.activity_basic_simple_mapview)

        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->

            mapboxMap.setStyle(Style.OUTDOORS) {

                val latLngBounds = LatLngBounds.Builder()
                    .include(locationOne)
                    .include(locationTwo)
                    .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10));

            }
        }

    }



    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

}
