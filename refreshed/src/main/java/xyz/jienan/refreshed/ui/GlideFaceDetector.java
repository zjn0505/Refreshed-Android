package xyz.jienan.refreshed.ui;

import android.content.Context;

import com.google.android.gms.vision.face.FaceDetector;

/*
 * Copyright (C) 2016 Rohit Arya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class GlideFaceDetector {

    private static volatile FaceDetector faceDetector;
    private static Context mContext;

    public static Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("Initialize GlideFaceDetector by calling GlideFaceDetector.initialize(context).");
        }
        return mContext;
    }

    public static void initialize(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        mContext = context.getApplicationContext();
    }

    private static void initDetector() {
        if (faceDetector == null) {
            synchronized ((GlideFaceDetector.class)) {
                if (faceDetector == null) {
                    faceDetector = new
                            FaceDetector.Builder(getContext())
                            .setTrackingEnabled(false)
                            .build();
                }
            }
        }
    }

    static FaceDetector getFaceDetector() {
        initDetector();
        return faceDetector;
    }

    public static void releaseDetector() {
        if (faceDetector != null) {
            faceDetector.release();
            faceDetector = null;
        }
        mContext = null;
    }
}