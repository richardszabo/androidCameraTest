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
import android.widget.EditText;
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
    File sdcardLocation;
    String defaultName = "CameraTest.jpg";
    EditText imageLocationTextField;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);
        sdcardLocation = Environment.getExternalStorageDirectory();
        imageLocationTextField = (EditText) findViewById(R.id.imageLocation);
        File imageLocation = new File(sdcardLocation,defaultName);
        imageLocationTextField.setText(imageLocation.getAbsolutePath());

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                /*
                // somehow the application hangs if it is in the code
                getWindow().setFormat(PixelFormat.TRANSLUCENT);
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
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
        camera = Camera.open();
    }

    public void surfaceChanged(SurfaceHolder holder, int i, int width, int height) {
        if (previewRunning) {
            camera.stopPreview();
        }

        Camera.Parameters p = camera.getParameters();
        p.setPreviewSize(width, height);
        //p.setPreviewFormat(PixelFormat.JPEG);
        camera.setParameters(p);

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

                saveDataToSDFile(imageLocationTextField.getText().toString(),imageData);

                setResult(FOTO_MODE, mIntent);
                finish();
                //camera.startPreview();
            }
        }
    };

}
