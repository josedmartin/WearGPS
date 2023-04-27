/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.wearosmap;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.wear.ambient.AmbientModeSupport;
import androidx.wear.ambient.AmbientModeSupport.AmbientCallback;
import androidx.wear.widget.SwipeDismissFrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, AmbientModeSupport.AmbientCallbackProvider,
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private SupportMapFragment mapFragment;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.map_activity);

        // activar ambient mode support
        AmbientModeSupport.AmbientController controller = AmbientModeSupport.attach(this);
        // [END maps_wear_os_ambient_mode_support]
        Log.d(MapActivity.class.getSimpleName(), "Is ambient enabled: " + controller.isAmbient());

        // Quitar pantalla
        final SwipeDismissFrameLayout mapFrameLayout = (SwipeDismissFrameLayout) findViewById(
                R.id.map_container);
        mapFrameLayout.addCallback(new SwipeDismissFrameLayout.Callback() {
            @Override
            public void onDismissed(SwipeDismissFrameLayout layout) {
                onBackPressed();
            }
        });


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private GoogleMap map;

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37, -5), 8));

        enableMyLocation();
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        // 1. Comprobar si los permisosn han sido aceptados
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            return;
        }

        // 2. Perdir permisos
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Si los permisos de ubicación no se han otorgado, solicitarlos al usuario
            EasyPermissions.requestPermissions(
                    this,
                    "Se requieren permisos de ubicación para continuar",
                    LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Hace que la camara se mueva a la posición del usuario.
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (EasyPermissions.somePermissionPermanentlyDenied(this, Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))) {
            // Al menos uno de los permisos de ubicación se ha denegado permanentemente
            // Mostrar un diálogo al usuario para que lo dirija a la configuración de la aplicación y permita los permisos
            new AppSettingsDialog.Builder(this)
                    .setTitle("Permisos denegados")
                    .setRationale("Para utilizar esta función se necesitan permisos de ubicación")
                    .setPositiveButton("Configuración")
                    .setNegativeButton("Cancelar")
                    .setRequestCode(LOCATION_PERMISSION_REQUEST_CODE)
                    .build()
                    .show();
        } else if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Ambos permisos de ubicación se han otorgado
            enableMyLocation();
        } else {
            // Los permisos de ubicación no se han otorgado, solicitarlos al usuario
            EasyPermissions.requestPermissions(
                    this,
                    "Se requieren permisos de ubicación para continuar",
                    LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            );
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        new AppSettingsDialog.Builder(this)
                .setTitle("Permisos denegados")
                .setRationale("Para utilizar esta función se necesitan permisos de ubicación")
                .setPositiveButton("Configuración")
                .setNegativeButton("Cancelar")
                .setRequestCode(LOCATION_PERMISSION_REQUEST_CODE)
                .build()
                .show();
    }

    @Override
    public AmbientCallback getAmbientCallback() {
        return new AmbientCallback() {
            @Override
            public void onEnterAmbient(Bundle ambientDetails) {
                super.onEnterAmbient(ambientDetails);
                mapFragment.onEnterAmbient(ambientDetails);
            }

            @Override
            public void onExitAmbient() {
                super.onExitAmbient();
                mapFragment.onExitAmbient();
            }
        };
    }
}