package com.example.mapapplicationv2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.GeoJson;
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

import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.CustomLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.RasterLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class MainActivity extends AppCompatActivity {
    private static final int ACTIVITY_CHOOSE_FILE = 666;
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    public String JsonString = "{\n" +
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
        updateMap(mapView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.chooseFile :
                Log.d("checkMenu","ChooseFile");
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("*/*");
                Intent intent = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
                return true;
            case R.id.addPoint:
                Log.d("checkMenu","Point");

                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull MapboxMap mapboxMap) {

                        SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, mapboxMap.getStyle());
                        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                            @Override
                            public boolean onMapClick(@NonNull LatLng point) {
                               // GeoJsonSource symbolSource = new GeoJsonSource("symbolsFromJson",FeatureCollection.fromFeatures(featurePoints));
                                //Toast.makeText(MainActivity.this, String.format("User clicked at: %s", point.toString()), Toast.LENGTH_LONG).show();
                                //Source symbolSource = mapboxMap.getStyle().getSource("symbolsFromJson");
                                symbolManager.create(new SymbolOptions().withLatLng(new LatLng(point.getLatitude(),point.getLongitude())).withIconImage(ICON_ID));
                                List <Feature> j = FeatureCollection.fromJson(JsonString).features();
                                j.add(Feature.fromGeometry(Point.fromLngLat(point.getLatitude(),point.getLongitude())));
                                JsonString = FeatureCollection.fromFeatures(j).toJson();
                                Log.d("here",JsonString);

                                return true;
                            }
                        });
                    }
                });
                return true;
            case R.id.addLine:
                Log.d("checkMenu","Line");
                return true;
            case R.id.addPolygon:
                Log.d("checkMenu","Polygon");
                return true;
            case R.id.getFileInPhoneSpace:
                Log.d("ChooseFile",Environment.getExternalStorageDirectory().toString());
                boolean is = isStoragePermissionGranted();
                        // и производим непосредственно запись
                try {
                    //File myFile = new File( "storage/Download/test4.json" );
                    File myFile = new File( "sdcard/Download/test6.json" );
                    boolean created = myFile.createNewFile();
                    if (created) {
                        Log.d("ChooseFile","created");
                        Toast.makeText(this, R.string.write_done, Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d("ChooseFile","not created");
                    }
                    // Создается файл, если он не был создан
                    FileOutputStream outputStream = new FileOutputStream(myFile);   // После чего создаем поток для записи
                    outputStream.write(JsonString.getBytes());
                    Log.d("JsonString",JsonString);
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*
                 * Вызов сообщения Toast не относится к теме.
                 * Просто для удобства визуального контроля исполнения метода в приложении
                 */

//                try {
//
//                    if (is) {
//                        String FILENAME = "hello_file", mystring = "hello world!";
//                        boolean newFile = new File(Environment.getExternalStorageDirectory(), FILENAME).createNewFile();
//                        FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
//                        fos.write(mystring.getBytes());
//                        fos.flush();
//                        fos.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_CHOOSE_FILE) {
            if (resultCode != RESULT_OK || data == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri uri = data.getData();
            if (uri == null) {
                return;
            }
            //String fileName = getFileName(uri);
            String fileContent = getFileContent(uri);
            Log.e("File content: ", fileContent); // Содержимое файла
           // Log.e("File name: ", fileName); // Имя файла
            //GCjsonString = fileContent;
            JsonString = fileContent;
            mapView = (MapView) findViewById(R.id.mapView);
            updateMap(mapView);



        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public String getFileContent(Uri contentUri) {
        try {
            InputStream in = getContentResolver().openInputStream(contentUri);
            if (in != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                return total.toString();
            } else {
                Log.e("TAG", "Input stream is null");
            }
        } catch (Exception e) {
            Log.e("TAG", "Error while reading file by uri", e);
        }
        return "Could not read content!";
    }
    private void updateMap (MapView mapView){
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
//                        try {
//                            InputStream is = getAssets().open("geojsontest.json");
//
//                            int size = is.available();
//
//                            byte[] buffer = new byte[size];
//
//                            is.read(buffer);
//
//                            is.close();
//
//                            GCjsonString  = new String(buffer, StandardCharsets.UTF_8);
////                            FileWriter file = new FileWriter("geojsontest.json");
////                            file.write(GCjsonString);
////                            file.flush();
////                            file.close();
//                        } catch (IOException ex){
//                            ex.printStackTrace();
//                        }



                             FeatureCollection FC = FeatureCollection.fromJson(JsonString);
//                                GeometryCollection GC = GeometryCollection.fromJson(JsonString);
                                List <Feature> listFeatures = FC.features();
//                                List <Geometry> listGeometry = GC.geometries();
                                List <Feature> featurePoints = new ArrayList<>();
                                List <Feature> featureLines = new ArrayList<>();
                                List <Feature> featureFills = new ArrayList<>();


//                                for (Geometry geometry:
//                                        listGeometry) {
//                                    switch (geometry.type()) {
//                                        case "Point":
//                                            featurePoints.add(Feature.fromGeometry(geometry));
//                                            break;
//                                        case "LineString":
//                                            featureLines.add(Feature.fromGeometry(geometry));
//                                            break;
//                                        case "Polygon":
//                                            featureFills.add(Feature.fromGeometry(geometry));
//                                            break;
//                                        case "MultiPolygon":
//                                            for(Polygon pl:
//                                                    ((MultiPolygon) geometry).polygons()) {
//                                                featureFills.add(Feature.fromGeometry(((Geometry) pl)));
//                                            }
//                                            break;
//                                        case "MultiLineString":
//                                            for(LineString line:
//                                                    ((MultiLineString) geometry).lineStrings()) {
//                                                featureLines.add(Feature.fromGeometry(((Geometry) line)));
//                                            }
//                                            break;
//                                        case "MultiPoint":
//                                            for(Point pt:
//                                                    ((MultiPoint) geometry).coordinates()) {
//                                                featurePoints.add(Feature.fromGeometry(((Geometry) pt)));
//                                            }
//                                            break;
//                                    }
//                                }

                        fillLayers(listFeatures,featurePoints,featureLines,featureFills);
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
                                LineLayer lineLayer = new LineLayer("lineLayer","linesFromJson").withProperties(
                                        PropertyFactory.lineColor(Color.parseColor("#3c9a58")),
                                        PropertyFactory.lineWidth(1f),
                                        PropertyFactory.lineBlur(0f),
                                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                        PropertyFactory.lineOpacity(1f)

                                );
                                FillLayer fillLayer = new FillLayer("fillLayer","fillFromJson").withProperties(
                                        PropertyFactory.fillColor(Color.parseColor("#f18484")),
                                        PropertyFactory.fillOpacity(0.4f),
                                        PropertyFactory.fillOutlineColor(Color.parseColor("#e93f3f"))
                                );

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
    private void fillLayers(List <Feature> listFeatures,List <Feature>  featurePoints,List <Feature> featureLines,List <Feature> featureFills){
        for (Feature feature:
                listFeatures) {
            switch (feature.geometry().type()) {
                case "Point":
                    featurePoints.add(feature);
                    break;
                case "LineString":
                    featureLines.add(feature);
                    break;
                case "Polygon":
                    featureFills.add(feature);
                    break;
                case "MultiPoint":
                    for(Point pt:
                        ((MultiPoint) feature.geometry()).coordinates()) {
                     featurePoints.add(Feature.fromGeometry(((Geometry) pt)));
                     }
                    break;
                case "MultiLineString":
                    for(LineString line:
                         ((MultiLineString) feature.geometry()).lineStrings()) {
                     featureLines.add(Feature.fromGeometry(((Geometry) line)));
                    }
                    break;
                case "MultiPolygon":
                    for(Polygon pl:
                         ((MultiPolygon) feature.geometry()).polygons()) {
                    featureFills.add(Feature.fromGeometry(((Geometry) pl)));
                    }
                    break;
                case "GeometryCollection":
                    for (Geometry geometry:
                            ((GeometryCollection) feature.geometry()).geometries()) {
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
                    break;
            }
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("dont care","Permission is granted");
                return true;
            } else {

                Log.v("dont care","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("dont care","Permission is granted");
            return true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}