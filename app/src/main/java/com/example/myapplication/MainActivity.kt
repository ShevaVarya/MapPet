package com.example.myapplication

import android.widget.TextView;
import android.widget.Toast;

import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.stop
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*

import com.mapbox.mapboxsdk.style.expressions.Expression.get
import com.mapbox.mapboxsdk.style.expressions.Expression.literal
import com.mapbox.mapboxsdk.style.expressions.Expression.match
import com.mapbox.mapboxsdk.style.expressions.Expression.stop
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage


class MainActivity : AppCompatActivity(), OnMapReadyCallback, MapboxMap.OnMapClickListener {
    private var mapView: MapView? = null
    private val SOURCE_ID = "SOURCE_ID";
    private val ICON_ID = "ICON_ID";
    private val LAYER_ID = "LAYER_ID";
    private val ICON_PROPERTY = "ICON_PROPERTY"
    private var mapboxMap: MapboxMap? = null

    private val locationOne = LatLng(55.932475, 37.870141)
    private val locationTwo = LatLng(55.559958, 37.341934)
    var dataPoint = mutableListOf<List<Double>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        setContentView(R.layout.activity_basic_simple_mapview)

        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        try {
            var jsonObject = JSONObject(JSONDataFromAssets("geoLoc.json"))
            var features = jsonObject.getJSONArray("features")

            for (i in 0 until features.length()) {
                var coordinatesPoint = ArrayList<Double>()
                var geoData: JSONObject = features.getJSONObject(i)
                var x = geoData.getJSONObject("geometry").getJSONArray("coordinates")
                coordinatesPoint.add(x[0].toString().toDouble())
                coordinatesPoint.add(x[1].toString().toDouble())
                dataPoint.add(coordinatesPoint)
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
       for (i in 0 .. dataPoint.size-1) {
           symbolLayerIconFeatureList.add(
               Feature.fromGeometry(
                   Point.fromLngLat(dataPoint[i][1], dataPoint[i][0])
               )
           )
       }

        mapboxMap.setStyle(
            Style.Builder().fromUri("mapbox://styles/shevavarya/ckqc1lfvv1uxr17nww8mwmrj6")
                .withImage(ICON_ID, BitmapFactory.decodeResource(
                    this.getResources(), R.drawable.mapbox_marker_icon_default
                ))
                .withSource(GeoJsonSource(SOURCE_ID,
                    FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
            .withLayer(SymbolLayer(LAYER_ID, SOURCE_ID)
            .withProperties(iconImage(match(
                get(ICON_PROPERTY), literal(ICON_ID),
                stop(ICON_ID, ICON_ID))),
                iconAllowOverlap(true),
                iconAnchor(Property.ICON_ANCHOR_BOTTOM))
        ),  Style.OnStyleLoaded() {
                val latLngBounds = LatLngBounds.Builder()
                    .include(locationOne)
                    .include(locationTwo)
                    .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 10));

                this.mapboxMap = mapboxMap;

                mapboxMap.addOnMapClickListener(this);

                Toast.makeText(this, R.string.tap_on_marker_instruction,
                    Toast.LENGTH_SHORT).show();
        })
    }

    override fun onMapClick(@NonNull point: LatLng): Boolean{
        return handleClickIcon(mapboxMap?.getProjection()!!.toScreenLocation(point))
    }
    private fun handleClickIcon(screenPoint: PointF): Boolean{
        var features: MutableList<Feature> = mapboxMap?.queryRenderedFeatures(screenPoint, LAYER_ID) as MutableList<Feature>
        if (!features.isEmpty()){
            var featureInfoTextView: TextView = findViewById(R.id.feature_info);
            featureInfoTextView.setText(features.get(0).toJson());
            return true;
        } else {
            return false;
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
