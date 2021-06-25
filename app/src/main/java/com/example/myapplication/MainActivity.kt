package com.example.myapplication

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import android.graphics.BitmapFactory
import android.util.Log


import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import java.util.ArrayList
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage
import java.net.URI
import java.net.URISyntaxException


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mapView: MapView? = null
    private val SOURCE_ID = "SOURCE_ID";
    private val ICON_ID = "ICON_ID";
    private val LAYER_ID = "LAYER_ID";

    private val locationOne = LatLng(55.932475, 37.870141)
    private val locationTwo = LatLng(55.559958, 37.341934)
    var dataPoint = mutableMapOf<Int, List<Double>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        setContentView(R.layout.activity_basic_simple_mapview)

        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

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

    override fun onMapReady(@NonNull mapboxMap: MapboxMap) {
        val symbolLayerIconFeatureList: MutableList<Feature> = ArrayList()
        symbolLayerIconFeatureList.add(
            Feature.fromGeometry(
                Point.fromLngLat(37.759903, 55.618775)
            )
        )
        symbolLayerIconFeatureList.add(
            Feature.fromGeometry(
                Point.fromLngLat(-54.14164, -33.981818)
            )
        )
        symbolLayerIconFeatureList.add(
            Feature.fromGeometry(
                Point.fromLngLat(-56.990533, -30.583266)
            )
        )

        mapboxMap.setStyle(
            Style.Builder().fromUri("mapbox://styles/shevavarya/ckqc1lfvv1uxr17nww8mwmrj6")
                .withImage(ICON_ID, BitmapFactory.decodeResource(
                    this.getResources(), R.drawable.mapbox_marker_icon_default
                ))
                .withSource(GeoJsonSource(SOURCE_ID,
                    FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
            .withLayer(SymbolLayer(LAYER_ID, SOURCE_ID)
            .withProperties(
                iconImage(ICON_ID),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
            )
        ),  Style.OnStyleLoaded() {
                val latLngBounds = LatLngBounds.Builder()
                    .include(locationOne)
                    .include(locationTwo)
                    .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10));
        })
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
