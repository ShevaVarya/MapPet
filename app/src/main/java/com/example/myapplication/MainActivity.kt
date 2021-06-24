package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.GeoJson
import com.mapbox.geojson.gson.GeometryGeoJson
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.maps.MapView
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;


class MainActivity : AppCompatActivity() {
    private var mapView: MapView? = null

    private val locationOne = LatLng(55.932475, 37.870141)
    private val locationTwo = LatLng(55.559958, 37.341934)
    private var dataPoint = mutableMapOf<Int, ArrayList<Double>>()

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

        try {
            var idPoint: Int
            var jsonObject = JSONObject(JSONDataFromAssets("geoLoc.json"))
            var features = jsonObject.getJSONArray("features")

            for (i in 0 until features.length()) {
                var coordinatesPoint = ArrayList<Double>()
                var geoData: JSONObject = features.getJSONObject(i)
                idPoint = geoData.getString("id").toInt()
                var x = geoData.getJSONObject("geometry").getJSONArray("coordinates")
                coordinatesPoint.add(x[0].toString().toDouble())
                coordinatesPoint.add(x[1].toString().toDouble())
                dataPoint += idPoint to coordinatesPoint
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun JSONDataFromAssets(filename: String): String {
        var json: String? = null;
        try {
            var inputStream: InputStream = getAssets().open(filename)
            var sizeOfFile = inputStream.available()
            var bufferData = ByteArray(sizeOfFile)
            inputStream.read(bufferData)
            inputStream.close()
            json = String(bufferData)
        } catch (e: IOException) {
            e.printStackTrace()
            return null.toString()
        }
        return json;
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
