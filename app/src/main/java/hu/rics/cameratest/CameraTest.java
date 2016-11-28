/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.rics.cameratest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import static hu.rics.cameratest.PhotoIntentTest.REQUEST_IMAGE_CAPTURE;
import static hu.rics.cameratest.R.layout.main;

/**
 *
 * @author rics
 */
public class CameraTest extends Activity {

    static final String TAG = "CameraTest";
    CameraApiTest cameraApiTest;
    PhotoIntentTest photoIntentTest;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(main);
        cameraApiTest = new CameraApiTest(this);
        final Button imageIntentButton = (Button) findViewById(R.id.imageIntentButton);
        imageIntentButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(CameraTest.this, PhotoIntentTest.class);
                startActivity(intent);
            }
        });

        //photoIntentTest = new PhotoIntentTest(this);
    }

    public void showMessage(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}
