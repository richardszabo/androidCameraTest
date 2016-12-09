/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.rics.cameratest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(main);
        sdcardLocation = Environment.getExternalStorageDirectory();
        imageLocationTextField = (EditText) findViewById(R.id.imageLocation);
        File imageLocation = new File(sdcardLocation, defaultName);
        imageLocationTextField.setText(imageLocation.getAbsolutePath());

        View.OnClickListener cameraApiListener = new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(CameraTest.this, CameraApiTest.class);
                intent.putExtra("filename",imageLocationTextField.getText().toString());
                startActivity(intent);
            }
        };

        final Button cameraApiButton = (Button) findViewById(R.id.cameraApiButton);
        cameraApiButton.setOnClickListener(cameraApiListener);

        View.OnClickListener imageIntentListener = new View.OnClickListener() {

            public void onClick(View v) {
                //Log.i(TAG,"id:" + v.getId());
                Intent intent = new Intent(CameraTest.this, PhotoIntentTest.class);
                intent.putExtra("isPublic",v.getId() == R.id.imageIntentPublicButton || v.getId() == R.id.videoIntentPublicButton);
                intent.putExtra("isVideo",v.getId() == R.id.videoIntentPublicButton || v.getId() == R.id.videoIntentLocaleButton);
                startActivity(intent);
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
}
