/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.rics.cameratest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import hu.rics.permissionhandler.PermissionHandler;

/**
 *
 * @author rics
 */
public class CameraTest extends Activity {

    public static final String TAG = "CameraTest";
    EditText imageLocationTextField;
    File sdcardLocation;
    PermissionHandler permissionHandler;
    String permissions[] = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO

    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);
        sdcardLocation = Environment.getExternalStorageDirectory();
        imageLocationTextField = (EditText) findViewById(R.id.imageLocation);
        File imageLocation = new File(sdcardLocation, getString(R.string.DEFAULT_FILENAME));
        imageLocationTextField.setText(imageLocation.getAbsolutePath());
        permissionHandler = new PermissionHandler(this);
        permissionHandler.requestPermission(permissions);


        View.OnClickListener cameraApiImageListener = new View.OnClickListener() {

            public void onClick(View v) {
            if( permissionHandler.hasRights() ) {
                Intent intent = new Intent(CameraTest.this, CameraApiTest.class);
                intent.putExtra(getString(R.string.INTENT_PARAM_FILENAME), imageLocationTextField.getText().toString());
                startActivity(intent);
            } else {
                showMessage("Some rights are missing.");
            }
            }
        };

        final Button cameraApiImageButton = (Button) findViewById(R.id.cameraApiImageButton);
        cameraApiImageButton.setOnClickListener(cameraApiImageListener);

        View.OnClickListener cameraApiVideoListener = new View.OnClickListener() {

            public void onClick(View v) {
                if( permissionHandler.hasRights() ) {
                    Intent intent = new Intent(CameraTest.this, CameraApiVideoTest.class);
                    intent.putExtra(getString(R.string.INTENT_PARAM_FILENAME), imageLocationTextField.getText().toString());
                    startActivity(intent);
                } else {
                    showMessage("Some rights are missing.");
                }
            }
        };

        final Button cameraApiVideoButton = (Button) findViewById(R.id.cameraApiVideoButton);
        cameraApiVideoButton.setOnClickListener(cameraApiVideoListener);

        View.OnClickListener imageIntentListener = new View.OnClickListener() {

            public void onClick(View v) {
            if( permissionHandler.hasRights() ) {
                Intent intent = new Intent(CameraTest.this, PhotoIntentTest.class);
                intent.putExtra("isPublic", v.getId() == R.id.imageIntentPublicButton || v.getId() == R.id.videoIntentPublicButton);
                intent.putExtra("isVideo", v.getId() == R.id.videoIntentPublicButton || v.getId() == R.id.videoIntentLocaleButton);
                startActivity(intent);
            } else {
                showMessage("Some rights are missing.");
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionHandler.onRequestPermissionsResult( requestCode, permissions, grantResults);
    }

    public void showMessage(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}
