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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by rics on 2016.11.28.
 */

public class CameraApiTest extends Activity implements SurfaceHolder.Callback, View.OnClickListener {

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    boolean previewRunning;
    static final int FOTO_MODE = 0;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        createSurface();
    }

    void createSurface() {
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
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0)
        {
            Log.i(CameraTest.TAG,"ROTATION_O");
            camera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90)
        {
            Log.i(CameraTest.TAG,"ROTATION_9O");
       }

        if(display.getRotation() == Surface.ROTATION_180)
        {
            Log.i(CameraTest.TAG,"ROTATION_18O");
        }

        if(display.getRotation() == Surface.ROTATION_270)
        {
            Log.i(CameraTest.TAG,"ROTATION_27O");
            camera.setDisplayOrientation(180);
        }
        parameters.setPreviewSize(width, height);
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
