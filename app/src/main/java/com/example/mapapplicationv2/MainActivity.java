package com.example.mapapplicationv2;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.mapbox.mapboxsdk.plugins.annotation.FillManager;
import com.mapbox.mapboxsdk.plugins.annotation.FillOptions;
import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class MainActivity extends AppCompatActivity {
    private static final int ACTIVITY_CHOOSE_FILE = 666;
    private static final String ICON_ID = "ICON_ID";
    private String JsonString = "";
    private SymbolManager symbolManager;
    private LineManager lineManager;
    private FillManager fillManager;
    private List <Point> LinetoAdd = new ArrayList<Point>();
    private List <LatLng> PolygontoAdd = new ArrayList<LatLng>();
    private List <LatLng> LinetoAddLatLng = new ArrayList<LatLng>();
    private final MapboxMap.OnMapClickListener pointListener = new MapboxMap.OnMapClickListener() {
        @Override
        public boolean onMapClick(@NonNull LatLng point) {
            symbolManager.create(new SymbolOptions().withLatLng(new LatLng(point.getLatitude(),point.getLongitude())).withIconImage(ICON_ID));
            List<Feature> j = new ArrayList();
            if (!JsonString.isEmpty()) {
                j = FeatureCollection.fromJson(JsonString).features();
            }
            Log.d("Point",point.toString());
            Point po = Point.fromLngLat(point.getLatitude(),point.getLongitude());
            Log.d("PointObject", po.toString());
            Feature newPoint = Feature.fromGeometry(Point.fromLngLat(point.getLongitude(),point.getLatitude()));
            //newPoint.addStringProperty("prop0","value0");
            j.add(newPoint);
            JsonString = FeatureCollection.fromFeatures(j).toJson();
            Log.d("here",JsonString);
            Log.d("string",FeatureCollection.fromFeatures(j).toString());
            return true;

        }
    };
    private final MapboxMap.OnMapClickListener  lineListener = new MapboxMap.OnMapClickListener() {
        @Override
        public boolean onMapClick(@NonNull LatLng point) {
            if (LinetoAdd.isEmpty()){
                LinetoAdd.add(Point.fromLngLat(point.getLongitude(),point.getLatitude()));
                LinetoAddLatLng.add(new LatLng(point.getLatitude(),point.getLongitude()));
                return true;
            } else {
                LinetoAdd.add(Point.fromLngLat(point.getLongitude(),point.getLatitude()));
                LinetoAddLatLng.add(new LatLng(point.getLatitude(),point.getLongitude()));
                lineManager.create(new LineOptions().withLatLngs(LinetoAddLatLng)
                        .withLineColor("black")
                        .withLineWidth(1f)
                        .withLineOpacity(1f)
                        .withLineJoin(Property.LINE_JOIN_ROUND));
                Log.d("An",lineManager.getAnnotations().toString());
                List<Feature> j = new ArrayList();
                if (!JsonString.isEmpty()) {
                    j = FeatureCollection.fromJson(JsonString).features();
                }
                Log.d("Point", point.toString());
                Feature newLine = Feature.fromGeometry(LineString.fromLngLats(LinetoAdd));
                j.add(newLine);
                JsonString = FeatureCollection.fromFeatures(j).toJson();
                Log.d("here", JsonString);
                Log.d("string", FeatureCollection.fromFeatures(j).toString());
                LinetoAdd.clear();
                LinetoAddLatLng.clear();
                return true;
            }
        }
    };

    private final MapboxMap.OnMapClickListener  fillListener = new MapboxMap.OnMapClickListener() {
        @Override
        public boolean onMapClick(@NonNull LatLng point) {
                PolygontoAdd.add(new LatLng(point.getLatitude(),point.getLongitude()));
                return true;
        }
    };
    private final MapboxMap.OnMapLongClickListener fillListenerLong = new MapboxMap.OnMapLongClickListener() {
        @Override
        public boolean onMapLongClick(@NonNull LatLng point) {
            if ( PolygontoAdd.isEmpty() ){
                return true;
            }
            List<List<LatLng>> latLngs = new ArrayList<>();
            latLngs.add(PolygontoAdd);
            fillManager.create(new FillOptions()
                    .withLatLngs(latLngs)
                    .withFillColor("#f18484")
                    .withFillOpacity(0.4f)
                    .withFillOutlineColor("#e93f3f"));



            List<Feature> j = new ArrayList();
            if (!JsonString.isEmpty()) {
                j = FeatureCollection.fromJson(JsonString).features();
            }
            List <Point> p = new ArrayList<>();
            for (LatLng latLng:PolygontoAdd){
                p.add(Point.fromLngLat(latLng.getLongitude(),latLng.getLatitude()));
            }
            List<List<Point>> points = new ArrayList<>();
            points.add(p);
            Feature newPolygon = Feature.fromGeometry(Polygon.fromLngLats(points));
            j.add(newPolygon);
            JsonString = FeatureCollection.fromFeatures(j).toJson();
            Log.d("here", JsonString);
            Log.d("string", FeatureCollection.fromFeatures(j).toString());
            PolygontoAdd.clear();
            return true;
        }
    };

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
                        if (symbolManager == null){
                            symbolManager = new SymbolManager(mapView, mapboxMap, mapboxMap.getStyle());
                        }
                        if (lineListener != null) {
                            mapboxMap.removeOnMapClickListener(lineListener);
                        }
                        if (fillListener != null) {
                            mapboxMap.removeOnMapClickListener(fillListener);
                        }
                        if (fillListenerLong != null) {
                            mapboxMap.removeOnMapLongClickListener(fillListenerLong);
                        }

                        mapboxMap.addOnMapClickListener(pointListener);
                    }
                });
                return true;
            case R.id.addLine:
                Log.d("checkMenu","Line");
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull MapboxMap mapboxMap) {
                        if (lineManager == null){
                            lineManager = new LineManager(mapView, mapboxMap, mapboxMap.getStyle());
                        }

                        if (pointListener!= null) {
                            mapboxMap.removeOnMapClickListener(pointListener);
                        }
                        if (fillListener != null) {
                            mapboxMap.removeOnMapClickListener(fillListener);
                        }

                        if (fillListenerLong != null) {
                            mapboxMap.removeOnMapLongClickListener(fillListenerLong);
                        }

                        mapboxMap.addOnMapClickListener(lineListener);
                    }
                });
                return true;
            case R.id.addPolygon:
                Log.d("checkMenu","Polygon");
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull MapboxMap mapboxMap) {
                        if (fillManager == null){
                            fillManager = new FillManager(mapView, mapboxMap, mapboxMap.getStyle());
                        }

                        if (pointListener!= null) {
                            mapboxMap.removeOnMapClickListener(pointListener);
                        }
                        if (lineListener != null) {
                            mapboxMap.removeOnMapClickListener(lineListener);
                        }
                        mapboxMap.addOnMapClickListener(fillListener);
                        mapboxMap.addOnMapLongClickListener(fillListenerLong);
                    }
                });
                return true;
            case R.id.getFileInPhoneSpace:
                Log.d("ChooseFile",Environment.getExternalStorageDirectory().toString());
                        // и производим непосредственно запись
                try {
                    //File myFile = new File( "storage/Download/test4.json" );
                    //File externalAppDir = new File(this.getApplicationContext().getFilesDir()+ "/" + "mapapplicationv2");
                    //File externalAppDir = new File(Environment.getExternalStorageDirectory() + "/" + "mapapplicationv2");
                    File externalAppDir = new File("sdcard/Download");
                    //Log.d("MKDIRPATH", this.getApplicationContext().getFilesDir()+ "/" + "mapapplicationv2");
                    if (!externalAppDir.exists()) {
                        if(externalAppDir.mkdirs()) {
                            Log.d("MKDIR", "Created");
                        }else{
                            Log.d("MKDIRPATH", this.getApplicationContext().getFilesDir()+"/"+ getPackageName());
                        }
                    }

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyy-hhmmss.SSS");
                    File myFile = new File(externalAppDir, "mapObject"+simpleDateFormat.format( new Date() )+".json");
                    boolean created = myFile.createNewFile();
                    if (created) {
                        Log.d("ChooseFile","created");
                        Toast.makeText(this, "mapObject"+simpleDateFormat.format( new Date() )+".json", Toast.LENGTH_SHORT).show();
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
                mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/streets-v11")
                                .withImage(ICON_ID, BitmapFactory.decodeResource(
                                            MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default))
                        , new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {
                            if (symbolManager != null){
                                symbolManager.deleteAll();
                            }

                            if (lineManager != null){
                                lineManager.deleteAll();
                            }

                            if (fillManager != null){
                                fillManager.deleteAll();
                            }


                        if(JsonString.isEmpty()){
                            return;
                        }

                        if(!isJSONValid(JsonString)){
                            //Должно быть сообщение об ошибке
                            JsonString = "";
                            return;
                        }


                             FeatureCollection FC = FeatureCollection.fromJson(JsonString);
                        if(!FC.type().equals("FeatureCollection")){
                            JsonString = "";
                            return;
                        }

                                List <Feature> listFeatures = FC.features();
                                List <Feature> featurePoints = new ArrayList<>();
                                List <Feature> featureLines = new ArrayList<>();
                                List <Feature> featureFills = new ArrayList<>();



                        if (listFeatures != null) {
                            fillLayers(listFeatures,featurePoints,featureLines,featureFills);
                        }
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
                                        PropertyFactory.lineColor(Color.parseColor("black")),
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
                                            .zoom(10)
                                            .build();
                                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
                                }

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
                     featurePoints.add(Feature.fromGeometry(pt));
                     }
                    break;
                case "MultiLineString":
                    for(LineString line:
                         ((MultiLineString) feature.geometry()).lineStrings()) {
                     featureLines.add(Feature.fromGeometry(line));
                    }
                    break;
                case "MultiPolygon":
                    for(Polygon pl:
                         ((MultiPolygon) feature.geometry()).polygons()) {
                    featureFills.add(Feature.fromGeometry(pl));
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
                                    featureFills.add(Feature.fromGeometry(pl));
                                }
                                break;
                            case "MultiLineString":
                                for(LineString line:
                                        ((MultiLineString) geometry).lineStrings()) {
                                    featureLines.add(Feature.fromGeometry(line));
                                }
                                break;
                            case "MultiPoint":
                                for(Point pt:
                                        ((MultiPoint) geometry).coordinates()) {
                                    featurePoints.add(Feature.fromGeometry(pt));
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
    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
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
        if (lineManager != null) {
            lineManager.onDestroy();
        }
        if (fillManager != null) {
            fillManager.onDestroy();
        }
        if (symbolManager != null) {
            symbolManager.onDestroy();
        }
        mapView.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        mapView.onSaveInstanceState(outState);
    }
}