/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.rics.cameratest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rics
 */
public class CameraTest extends Activity implements SurfaceHolder.Callback, View.OnClickListener {

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    boolean previewRunning;
    static final int FOTO_MODE = 0;
    private static final String TAG = "CameraTest";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.e(TAG, "onCreate");
        showMessage("Hello toast 1!");

        saveDataToSDFile("t.txt","hello".getBytes());

        setContentView(R.layout.main);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                /*
                // somehow the application hangs if it is in the code
                getWindow().setFormat(PixelFormat.TRANSLUCENT);
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
                Log.e(TAG, "onClick");
                setContentView(R.layout.camera);
                final Button cheese = (Button) findViewById(R.id.cheese);
                surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
                cheese.setOnClickListener(CameraTest.this);
                surfaceHolder = surfaceView.getHolder();
                surfaceHolder.addCallback(CameraTest.this);
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
        });
    }

    public void surfaceCreated(SurfaceHolder sh) {
        Log.e(TAG, "surfaceCreated");
        camera = Camera.open();
    }

    public void surfaceChanged(SurfaceHolder holder, int i, int width, int height) {
        Log.e(TAG, "surfaceChanged1");
        if (previewRunning) {
            camera.stopPreview();
        }

        Camera.Parameters p = camera.getParameters();
        p.setPreviewSize(width, height);
        //p.setPreviewFormat(PixelFormat.JPEG);
        camera.setParameters(p);

        Log.e(TAG, "surfaceChanged2");
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "surfaceChanged3");
        camera.startPreview();
        previewRunning = true;
    }

    public void surfaceDestroyed(SurfaceHolder sh) {
        camera.stopPreview();
        previewRunning = false;
        camera.release();
    }

    public void onClick(View v) {
        Log.e(TAG, "onClick2");
        showMessage("Hello toast 2!");
        saveDataToSDFile("t2.txt","hello".getBytes());
        camera.takePicture(null, null, mPictureCallback);
    }

    public void showMessage(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    public void saveDataToSDFile(String filename, byte[] data) {
        FileOutputStream fOut = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                File sdcard = Environment.getExternalStorageDirectory();
                File file = new File(sdcard.getAbsolutePath(), filename);
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
            Log.e(TAG, "onPictureTaken");
            showMessage("Hello toast 3!");

            if (imageData != null) {

                Intent mIntent = new Intent();

                saveDataToSDFile("CameraTest2.jpg",imageData);

                setResult(FOTO_MODE, mIntent);
                finish();
                //camera.startPreview();
            }
        }
    };

}
