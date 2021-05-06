package com.example.mapapplicationv2;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.GeometryCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.MultiLineString;
import com.mapbox.geojson.MultiPoint;
import com.mapbox.geojson.MultiPolygon;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import com.mapbox.mapboxsdk.style.layers.CustomLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.RasterLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class MainActivity extends AppCompatActivity {
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private String JsonString = "{\n" +
            "       \"type\": \"FeatureCollection\",\n" +
            "       \"features\": [{\n" +
            "           \"type\": \"Feature\",\n" +
            "           \"geometry\": {\n" +
            "               \"type\": \"Point\",\n" +
            "               \"coordinates\": [102.0, 0.5]\n" +
            "           },\n" +
            "           \"properties\": {\n" +
            "               \"prop0\": \"value0\"\n" +
            "           }\n" +
            "       }, {\n" +
            "           \"type\": \"Feature\",\n" +
            "           \"geometry\": {\n" +
            "               \"type\": \"LineString\",\n" +
            "               \"coordinates\": [\n" +
            "                   [102.0, 0.0],\n" +
            "                   [103.0, 1.0],\n" +
            "                   [104.0, 0.0],\n" +
            "                   [105.0, 1.0]\n" +
            "               ]\n" +
            "           },\n" +
            "           \"properties\": {\n" +
            "               \"prop0\": \"value0\",\n" +
            "               \"prop1\": 0.0\n" +
            "           }\n" +
            "       }, {\n" +
            "           \"type\": \"Feature\",\n" +
            "           \"geometry\": {\n" +
            "               \"type\": \"Polygon\",\n" +
            "               \"coordinates\": [\n" +
            "                   [\n" +
            "                       [100.0, 0.0],\n" +
            "                       [101.0, 0.0],\n" +
            "                       [101.0, 1.0],\n" +
            "                       [100.0, 1.0],\n" +
            "                       [100.0, 0.0]\n" +
            "                   ]\n" +
            "               ]\n" +
            "           },\n" +
            "           \"properties\": {\n" +
            "               \"prop0\": \"value0\",\n" +
            "               \"prop1\": {\n" +
            "                   \"this\": \"that\"\n" +
            "               }\n" +
            "           }\n" +
            "       }]\n" +
            "   }";
    private String GCjsonString = "{ \"type\": \"GeometryCollection\",\n" +
            "  \"geometries\": [\n" +
            "    { \"type\": \"Point\",\n" +
            "      \"coordinates\": [100.0, 0.0]\n" +
            "      },\n" +
            "    { \"type\": \"Point\",\n" +
            "      \"coordinates\": [104.0, 2.0]\n" +
            "      },\n" +
            "    { \"type\": \"LineString\",\n" +
            "      \"coordinates\": [ [101.0, 0.0], [102.0, 1.0] ]\n" +
            "      },\n" +
            "    { \"type\": \"LineString\",\n" +
            "      \"coordinates\": [ [102.0, 0.0], [103.0, 0.0] ]\n" +
            "      },\n" +
            "    { \"type\": \"Polygon\",\n" +
            "      \"coordinates\": [ \n" +
            "\t\t[ \n" +
            "\t\t\t[102.0, 2.0],\n" +
            "\t\t \t[103.0, 2.0],\n" +
            "    \t\t\t[103.0, 3.0],\n" +
            "    \t\t\t[102.0, 3.0],\n" +
            "    \t\t\t[102.0, 2.0]\n" +
            "\t \t]\n" +
            "\t ]\n" +
            "      },\n" +
            "\t{ \"type\": \"MultiPolygon\",\n" +
            "   \t\t \"coordinates\": [\n" +
            "   \t\t [[[102.0, 2.0], [103.0, 2.0], [103.0, 3.0], [102.0, 3.0], [102.0, 2.0]]],\n" +
            "   \t\t [[[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]],\n" +
            "   \t\t  [[100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2]]]\n" +
            "  \t\t  ]\n" +
            " \t }\n" +
            "  ]\n" +
            "}";
    private MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
                symbolLayerIconFeatureList.add(Feature.fromGeometry(
                        Point.fromLngLat(58, 53.172)));
                symbolLayerIconFeatureList.add(Feature.fromGeometry(
                        Point.fromLngLat(56.850, 53.223)));
                symbolLayerIconFeatureList.add(Feature.fromGeometry(
                        Point.fromLngLat(56.870, 53.220)));

              // FeatureCollection featureCollectionFromJson = FeatureCollection.fromJson(JsonString);
               // Polygon polygonFromOuterInner = Polygon.fromOuterInner(outerLineStringObject,innerLineStringObject);

//                Feature pointFeature = Feature.fromGeometry(Point.fromLngLat(LONGITUDE, LATITUDE));
//
//                Feature multiPointFeature = Feature.fromGeometry(MultiPoint.fromLngLats(listOfPoints));
//
//                FeatureCollection featureCollectionFromSingleFeature = FeatureCollection.fromFeature(pointFeature);

                mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/streets-v11")

                    // Add the SymbolLayer icon image to the map style
                                .withImage(ICON_ID, BitmapFactory.decodeResource(
                                        MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default))

                    // Adding a GeoJson source for the SymbolLayer icons.
                                .withSource(new GeoJsonSource(SOURCE_ID,
                                        FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
//                              .withSource(new GeoJsonSource(SOURCE_ID,
//                                       FeatureCollection.fromJson(JsonString)))

                    // Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
                    // marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
                    // the coordinate point. This is offset is not always needed and is dependent on the image
                    // that you use for the SymbolLayer icon.
                                .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                                        .withProperties(
                                                iconImage(ICON_ID),
                                                iconAllowOverlap(true),
                                                iconIgnorePlacement(true)
                                        )
                                )
                        , new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        try {
                            InputStream is = getAssets().open("geojsontest.json");

                            int size = is.available();

                            byte[] buffer = new byte[size];

                            is.read(buffer);

                            is.close();

                            GCjsonString  = new String(buffer, "UTF-8");
//                            FileWriter file = new FileWriter("geojsontest.json");
//                            file.write(GCjsonString);
//                            file.flush();
//                            file.close();
                        } catch (IOException ex){
                            ex.printStackTrace();
                        }



                        //                       FeatureCollection FC = FeatureCollection.fromJson(JsonString);
                        GeometryCollection GC = GeometryCollection.fromJson(GCjsonString);
//                        style.addSource(new GeoJsonSource("FCsource",FC));
//                        style.addLayer(new LineLayer("newLayer","FCsource"));
//                        List <Feature> listFeatures = FC.features();
                        List <Geometry> listGeometry = GC.geometries();
                        List <Feature> featurePoints = new ArrayList<>();
                        List <Feature> featureLines = new ArrayList<>();
                        List <Feature> featureFills = new ArrayList<>();


                        for (Geometry geometry:
                                listGeometry) {
                            switch (geometry.type()) {
                                case "Point":
                                    featurePoints.add(Feature.fromGeometry(geometry));
                                    break;
                                case "LineString":
                                    featureLines.add(Feature.fromGeometry(geometry));
                                    break;
                                case "Polygon":
                                    featureFills.add(Feature.fromGeometry(geometry));
                                    break;
                                case "MultiPolygon":
                                    for(Polygon pl:
                                            ((MultiPolygon) geometry).polygons()) {
                                        featureFills.add(Feature.fromGeometry(((Geometry) pl)));
                                    }
                                    break;
                                case "MultiLineString":
                                    for(LineString line:
                                            ((MultiLineString) geometry).lineStrings()) {
                                        featureLines.add(Feature.fromGeometry(((Geometry) line)));
                                    }
                                    break;
                                case "MultiPoint":
                                    for(Point pt:
                                            ((MultiPoint) geometry).coordinates()) {
                                        featurePoints.add(Feature.fromGeometry(((Geometry) pt)));
                                    }
                                    break;
                            }
                        }


//                        for (Feature feature:
//                             listFeatures) {
//                            switch (feature.geometry().type()) {
//                                case "Point":
//                                    featurePoints.add(feature);
//                                    break;
//                                case "LineString":
//                                    featureLines.add(feature);
//                                    break;
//                                case "Polygon":
//                                    featureFills.add(feature);
//                                    break;
//                            }
//                        }

                        GeoJsonSource symbolSource = new GeoJsonSource("symbolsFromJson",FeatureCollection.fromFeatures(featurePoints));
                        GeoJsonSource lineSource = new GeoJsonSource("linesFromJson",FeatureCollection.fromFeatures(featureLines));
                        GeoJsonSource fillSource = new GeoJsonSource("fillFromJson",FeatureCollection.fromFeatures(featureFills));

                        style.addSource(symbolSource);
                        style.addSource(lineSource );
                        style.addSource(fillSource);

                        SymbolLayer pointLayer = new SymbolLayer("symbolLayer","symbolsFromJson").withProperties(
                                                iconImage(ICON_ID),
                                                iconAllowOverlap(true),
                                                iconIgnorePlacement(true)
                                        );
                        LineLayer lineLayer = new LineLayer("lineLayer","linesFromJson");
                        FillLayer fillLayer = new FillLayer("fillLayer","fillFromJson");

                        style.addLayer(pointLayer);
                        style.addLayer(lineLayer);
                        style.addLayer(fillLayer);

                        if (featurePoints.size() != 0) {
                            Point pointOfLove = ((Point) featurePoints.get(0).geometry());
                            CameraPosition position = new CameraPosition.Builder()
                                    .target(new LatLng(pointOfLove.latitude(), pointOfLove.longitude()))
                                    .zoom(5)
                                    .build();
                            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
                        }



//                        // Create a list to store our line coordinates.
//                        List <Point> routeCoordinates = new ArrayList<Point>();
//                        routeCoordinates.add(Point.fromLngLat(55, 37));
//                        routeCoordinates.add(Point.fromLngLat(56, 53));

//
//                        // Create the LineString from the list of coordinates and then make a GeoJSON FeatureCollection so that you can add the line to our map as a layer.
//
//
//                        LineString lineString = LineString.fromLngLats(routeCoordinates);
//
//                        Feature feature = Feature.fromGeometry(lineString);
//
//                        GeoJsonSource geoJsonSource = new GeoJsonSource(SOURCE_ID, feature);
//                        style.addSource(geoJsonSource);
                    }
                });



            }
        });
    }
}