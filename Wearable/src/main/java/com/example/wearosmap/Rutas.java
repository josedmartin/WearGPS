package com.example.wearosmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.wear.widget.SwipeDismissFrameLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Rutas extends AppCompatActivity implements OnMapReadyCallback {

    private String destination;
    private SupportMapFragment mapFragment;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //Activamos el quitar pantalla
        final SwipeDismissFrameLayout mapFrameLayout = (SwipeDismissFrameLayout) findViewById(
                R.id.map_container);
        mapFrameLayout.addCallback(new SwipeDismissFrameLayout.Callback() {
            @Override
            public void onDismissed(SwipeDismissFrameLayout layout) {
                onBackPressed();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }
    private GoogleMap map;

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        enableMyLocation();
        showRoute();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showRoute() {
        // Verificar si se tienen los permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Se tienen los permisos de ubicación, obtener la ubicación actual del usuario
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double currentLatitude = location.getLatitude();
                    double currentLongitude = location.getLongitude();
                    LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);

                    // Mover mapa hacia la ubicación del usuario
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));

                    // Obtener el destino
                    Intent intent = getIntent();
                    if (intent != null && intent.hasExtra("destination")) {
                        destination = intent.getStringExtra("destination");
                    }
                    Log.i("destination", destination);

                    // Pasar a coordenadas
                    LatLng coordenadasDestino = obtenerCoordenadas(destination);

                    //Añadimos marcador
                    map.addMarker(new MarkerOptions().position(coordenadasDestino).title("destination"));

                    // Construir la URL de la solicitud de ruta a la API
                    String apiKey = "AIzaSyDZf6KNEm24s_hRW_6KCbd7mBE26ybdDwQ";
                    String requestUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                            "origin=" + currentLatitude + "," + currentLongitude +
                            "&destination=" + coordenadasDestino.latitude + "," + coordenadasDestino.longitude +
                            "&key=" + apiKey;

                    // Realizar la solicitud a la API de OpenRouteService en un hilo separado
                    new FetchDirectionsTask().execute(requestUrl );

                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // No se tienen los permisos de ubicación, solicitarlos al usuario
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //Función para obtener las coordenadas del destino
    public LatLng obtenerCoordenadas(String destination) {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> addresses;
        LatLng latLng = null;

        try {
            addresses = geocoder.getFromLocationName(destination, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return latLng;
    }
    public class FetchDirectionsTask  extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        //Funcion que hace el calculo de las rutas
        private List<LatLng> decodePolyline(String encodedPolyline) {
            List<LatLng> polyline = new ArrayList<>();
            int index = 0;
            int latitude = 0;
            int longitude = 0;

            while (index < encodedPolyline.length()) {
                int b;
                int shift = 0;
                int result = 0;

                do {
                    b = encodedPolyline.charAt(index++) - 63;
                    result |= (b & 0x1F) << shift;
                    shift += 5;
                } while (b >= 0x20);

                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                latitude += dlat;

                shift = 0;
                result = 0;

                do {
                    b = encodedPolyline.charAt(index++) - 63;
                    result |= (b & 0x1F) << shift;
                    shift += 5;
                } while (b >= 0x20);

                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                longitude += dlng;

                LatLng point = new LatLng((double) latitude / 1E5, (double) longitude / 1E5);
                polyline.add(point);
            }

            return polyline;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    // Parsear la respuesta JSON
                    JSONObject response = new JSONObject(result);
                    String status = response.getString("status");

                    if (status.equals("OK")) {
                        // Obtener la duración estimada de la ruta
                        JSONArray routes = response.getJSONArray("routes");
                        JSONObject route = routes.getJSONObject(0);
                        JSONArray legs = route.getJSONArray("legs");
                        JSONObject leg = legs.getJSONObject(0);
                        JSONObject duration = leg.getJSONObject("duration");
                        String durationText = duration.getString("text");

                        // Obtener los puntos de la ruta
                        JSONObject polyline = route.getJSONObject("overview_polyline");
                        String encodedPolyline = polyline.getString("points");
                        List<LatLng> decodedPolyline = decodePolyline(encodedPolyline);

                        // Dibujar la ruta en el mapa
                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.addAll(decodedPolyline);
                        polylineOptions.color(Color.BLUE);
                        polylineOptions.width(10);
                        map.addPolyline(polylineOptions);

                        // Mostrar la duración estimada al usuario
                        Toast.makeText(Rutas.this, "Duración: " + durationText, Toast.LENGTH_SHORT).show();
                    } else {
                        // Mostrar un mensaje de error si la solicitud no tuvo éxito
                        Toast.makeText(Rutas.this, "Error al obtener la ruta", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}