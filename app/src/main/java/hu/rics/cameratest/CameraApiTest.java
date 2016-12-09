package hu.rics.cameratest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rics on 2016.11.28.
 */

public class CameraApiTest extends Activity implements SurfaceHolder.Callback, View.OnClickListener {

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    boolean previewRunning;
    static final int FOTO_MODE = 0;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // permission check
        int hasCameraPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

        setContentView(R.layout.camera);
        final Button cheese = (Button) findViewById(R.id.cheese);
        surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        cheese.setOnClickListener(this);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder sh) {
        camera = Camera.open();
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
        parameters.setPreviewSize(previewWidth, previewHeight);
        //p.setPreviewFormat(PixelFormat.JPEG);
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

    public void onClick(View v) {
        camera.takePicture(null, null, mPictureCallback);
    }

    public void saveDataToSDFile(String filename, byte[] data) {
        FileOutputStream fOut = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                File file = new File(filename);
                fOut = new FileOutputStream(file);
                fOut.write(data);
                fOut.flush();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CameraTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CameraTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (IOException ex) {
                    Logger.getLogger(CameraTest.class.getName()).log(Level.SEVERE, null, ex);
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
