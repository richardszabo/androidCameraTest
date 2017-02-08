package hu.rics.cameratest;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.R.attr.id;

/**
 * Created by rics on 2016.11.28.
 */

public class CameraApiTest extends Activity implements SurfaceHolder.Callback, View.OnClickListener {

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    boolean previewRunning;
    static final int FOTO_MODE = 0;
    static final int CAMERA_ID = 0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        createSurface();
    }

    void createSurface() {
        setContentView(R.layout.camera);
        final Button cheese = (Button) findViewById(R.id.cheese);
        surfaceView = new SurfaceView(this);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(surfaceView);
        cheese.setOnClickListener(this);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder sh) {
        camera = Camera.open(CAMERA_ID);
    }

    public void surfaceChanged(SurfaceHolder holder, int i, int width, int height) {
        if (previewRunning) {
            camera.stopPreview();
        }

        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        int previewWidth = width;
        int previewHeight = height;
        if( previewSizes.size() > 0 ) {
            previewWidth = previewSizes.get(0).width;
            previewHeight = previewSizes.get(0).height;
        }
        setCameraDisplayOrientation(this,CAMERA_ID,camera);
        parameters.setPreviewSize(previewWidth, previewHeight);
        camera.setParameters(parameters);

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        camera.startPreview();
        previewRunning = true;
    }

    public void surfaceDestroyed(SurfaceHolder sh) {
        camera.stopPreview();
        previewRunning = false;
        camera.release();
    }

    // correct displayorientation for Ace 3 and SMT800 tab in all four direction
    // taken from here: http://stackoverflow.com/a/10218309/21047
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public void onClick(View v) {
        camera.takePicture(null, null, mPictureCallback);
    }

    public void saveDataToSDFile(String filename, byte[] data) {
        String ext = ".jpg";
        FileOutputStream fOut = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                File file = new File(filename + ext);
                fOut = new FileOutputStream(file);
                fOut.write(data);
                fOut.flush();
            }
        } catch (FileNotFoundException ex) {
            Log.e(CameraTest.TAG,ex.toString());
        } catch (IOException ex) {
            Log.e(CameraTest.TAG,ex.toString());
        } finally {
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (IOException ex) {
                    Log.e(CameraTest.TAG,ex.toString());
                }
            }
        }
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] imageData, Camera c) {

            if (imageData != null) {

                Intent mIntent = new Intent();

                saveDataToSDFile(getIntent().getStringExtra("filename"),imageData);

                setResult(FOTO_MODE, mIntent);
                finish();
                //camera.startPreview();
            }
        }
    };

}
