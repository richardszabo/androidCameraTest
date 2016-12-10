package hu.rics.cameratest;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // permission check (https://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en)
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, Manifest.permission.CAMERA)) {
            Log.i(CameraTest.TAG,"permissionsNeeded.add(\"Camera\");");
            permissionsNeeded.add("Camera");
        }
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.i(CameraTest.TAG,"permissionsNeeded.add(\"Storage\");");
            permissionsNeeded.add("External storage");
        }

        Log.i(CameraTest.TAG,"onCreate:" + permissionsList.size() + ":" + permissionsNeeded.size());
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(CameraApiTest.this,permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this,permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        createSurface();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,permission)) {
                return false;
            }
        }
        return true;
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

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++) {
                    Log.i(CameraTest.TAG,"i:" + i + ":" + permissions[i] + ":" + grantResults[i] );
                    perms.put(permissions[i], grantResults[i]);
                }
                // Check for ACCESS_FINE_LOCATION
                if( perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                    // All Permissions Granted
                    createSurface();
                } else {
                    // Permission Denied
                    Toast.makeText(CameraApiTest.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
