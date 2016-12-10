/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.rics.cameratest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static hu.rics.cameratest.PhotoIntentTest.REQUEST_IMAGE_CAPTURE;
import static hu.rics.cameratest.R.id.imageIntentPublicButton;
import static hu.rics.cameratest.R.id.imageIntentLocaleButton;
import static hu.rics.cameratest.R.layout.main;

/**
 *
 * @author rics
 */
public class CameraTest extends Activity {

    static final String TAG = "CameraTest";
    EditText imageLocationTextField;
    String defaultName = "CameraTest.jpg";
    File sdcardLocation;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 123;
    boolean hasRights = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(main);
        sdcardLocation = Environment.getExternalStorageDirectory();
        imageLocationTextField = (EditText) findViewById(R.id.imageLocation);
        File imageLocation = new File(sdcardLocation, defaultName);
        imageLocationTextField.setText(imageLocation.getAbsolutePath());
        if( requestPermission() ) {
            hasRights = true;
        }

        View.OnClickListener cameraApiListener = new View.OnClickListener() {

            public void onClick(View v) {
            if( hasRights ) {
                Intent intent = new Intent(CameraTest.this, CameraApiTest.class);
                intent.putExtra("filename", imageLocationTextField.getText().toString());
                startActivity(intent);
            }
            }
        };

        final Button cameraApiButton = (Button) findViewById(R.id.cameraApiButton);
        cameraApiButton.setOnClickListener(cameraApiListener);

        View.OnClickListener imageIntentListener = new View.OnClickListener() {

            public void onClick(View v) {
            if( hasRights ) {
                //Log.i(TAG,"id:" + v.getId());
                Intent intent = new Intent(CameraTest.this, PhotoIntentTest.class);
                intent.putExtra("isPublic", v.getId() == R.id.imageIntentPublicButton || v.getId() == R.id.videoIntentPublicButton);
                intent.putExtra("isVideo", v.getId() == R.id.videoIntentPublicButton || v.getId() == R.id.videoIntentLocaleButton);
                startActivity(intent);
            }
            }
        };
        final Button imageIntentPublicButton = (Button) findViewById(R.id.imageIntentPublicButton);
        imageIntentPublicButton.setOnClickListener(imageIntentListener);

        final Button imageIntentLocaleButton = (Button) findViewById(R.id.imageIntentLocaleButton);
        imageIntentLocaleButton.setOnClickListener(imageIntentListener);

        final Button videoIntentPublicButton = (Button) findViewById(R.id.videoIntentPublicButton);
        videoIntentPublicButton.setOnClickListener(imageIntentListener);

        final Button videoIntentLocaleButton = (Button) findViewById(R.id.videoIntentLocaleButton);
        videoIntentLocaleButton.setOnClickListener(imageIntentListener);
    }

    public void showMessage(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    boolean requestPermission() {
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
                                ActivityCompat.requestPermissions(CameraTest.this,permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return false;
            }
            ActivityCompat.requestPermissions(this,permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
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
                    hasRights = true;
                } else {
                    // Permission Denied
                    Toast.makeText(CameraTest.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
