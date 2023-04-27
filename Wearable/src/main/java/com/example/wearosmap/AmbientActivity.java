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

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.wear.ambient.AmbientModeSupport;
import androidx.wear.ambient.AmbientModeSupport.AmbientCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class AmbientActivity extends AppCompatActivity implements
    AmbientModeSupport.AmbientCallbackProvider {
    private SupportMapFragment mapFragment;
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.map_activity);

        // Enable ambient support
        AmbientModeSupport.AmbientController controller = AmbientModeSupport.attach(this);
        Log.d(AmbientActivity.class.getSimpleName(), "Is ambient enabled: " + controller.isAmbient());

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
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
