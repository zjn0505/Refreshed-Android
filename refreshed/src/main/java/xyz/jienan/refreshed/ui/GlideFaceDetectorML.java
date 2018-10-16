package xyz.jienan.refreshed.ui;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;

class GlideFaceDetectorML {

    private static volatile FirebaseVisionFaceDetector faceDetector;

    private static void initDetector() {
        if (faceDetector == null) {
            synchronized ((GlideFaceDetectorML.class)) {
                if (faceDetector == null) {
                    faceDetector = FirebaseVision.getInstance()
                            .getVisionFaceDetector();
                }
            }
        }
    }

    static FirebaseVisionFaceDetector getFaceDetector() {
        initDetector();
        return faceDetector;
    }
}