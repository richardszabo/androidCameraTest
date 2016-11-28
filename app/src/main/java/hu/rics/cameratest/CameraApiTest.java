package hu.rics.cameratest;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rics on 2016.11.28..
 */

public class CameraApiTest implements SurfaceHolder.Callback, View.OnClickListener {

    final CameraTest parent;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;
    boolean previewRunning;
    EditText imageLocationTextField;
    String defaultName = "CameraTest.jpg";
    File sdcardLocation;
    static final int FOTO_MODE = 0;


    public CameraApiTest(final CameraTest parent) {
        this.parent = parent;
        sdcardLocation = Environment.getExternalStorageDirectory();
        imageLocationTextField = (EditText) parent.findViewById(R.id.imageLocation);
        File imageLocation = new File(sdcardLocation, defaultName);
        imageLocationTextField.setText(imageLocation.getAbsolutePath());
        final Button cameraApiButton = (Button) parent.findViewById(R.id.cameraApiButton);
        cameraApiButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                parent.setContentView(R.layout.camera);
                final Button cheese = (Button) parent.findViewById(R.id.cheese);
                surfaceView = (SurfaceView) parent.findViewById(R.id.surface_camera);
                cheese.setOnClickListener(CameraApiTest.this);
                surfaceHolder = surfaceView.getHolder();
                surfaceHolder.addCallback(CameraApiTest.this);
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

                parent.setResult(FOTO_MODE, mIntent);
                parent.finish();
                //camera.startPreview();
            }
        }
    };

}
