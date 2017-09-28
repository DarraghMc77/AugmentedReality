package com.example.darragh.augmentedreality;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ProAndroidAR2Activity extends Activity {
    SurfaceView cameraPreview;
    SurfaceHolder previewHolder;
    Camera camera;
    boolean inPreview;

    final static String TAG = "PAAR";
    SensorManager sensorManager;
    int orientationSensor;
    float headingAngle;
    float pitchAngle;
    float rollAngle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inPreview = false;
        cameraPreview = (SurfaceView)findViewById(R.id.cameraPreview);
        previewHolder = cameraPreview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        orientationSensor = Sensor.TYPE_ORIENTATION;
        sensorManager.registerListener(sensorEventListener,
        sensorManager.getDefaultSensor(orientationSensor),
        SensorManager.SENSOR_DELAY_NORMAL);
    }

    final SensorEventListener sensorEventListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                headingAngle = sensorEvent.values[0];
                pitchAngle = sensorEvent.values[1];
                rollAngle = sensorEvent.values[2];
                Log.d(TAG, "Heading: " + String.valueOf(headingAngle));
                Log.d(TAG, "Pitch: " + String.valueOf(pitchAngle));
                Log.d(TAG, "Roll: " + String.valueOf(rollAngle));
            }
        }
        public void onAccuracyChanged (Sensor senor, int accuracy) {
            //Not used
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        camera=Camera.open();
    }

    @Override
    public void onPause() {
        if (inPreview) {
            camera.stopPreview();
        }
        camera.release();
        camera=null;
        inPreview=false;
        super.onPause();
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return (result);
    }


    SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
            }
            catch (Throwable t) {
                Log.e("ProAndroidAR2Activity", "Exception in setPreviewDisplay()", t);
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int
                height) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = getBestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                camera.setParameters(parameters);
                camera.startPreview();
                inPreview = true;
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // not used
        }

    };

}