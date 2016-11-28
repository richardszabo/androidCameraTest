package hu.rics.cameratest;

import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import static hu.rics.cameratest.R.id.cameraApiButton;

/**
 * Created by rics on 2016.11.28..
 */

public class PhotoIntentTest {

    final CameraTest parent;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public PhotoIntentTest(final CameraTest parent) {
        this.parent = parent;
        final Button imageIntentButton = (Button) parent.findViewById(R.id.imageIntentButton);
        imageIntentButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(parent.getPackageManager()) != null) {
            parent.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

}
