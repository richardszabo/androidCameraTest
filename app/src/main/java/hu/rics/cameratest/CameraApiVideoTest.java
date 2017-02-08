package hu.rics.cameratest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import hu.rics.cameratest.video.MediaRecorderWrapper;

/**
 * Created by rics on 2017.02.08..
 */

public class CameraApiVideoTest extends Activity implements View.OnClickListener{
    MediaRecorderWrapper mediaRecorderWrapper;
    Button cheese;
    String ext = ".mp4";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        createSurface();
    }

    void createSurface() {
        setContentView(R.layout.camera);
        cheese = (Button) findViewById(R.id.cheese);
        cheese.setText("Start");
        mediaRecorderWrapper = new MediaRecorderWrapper(this,R.id.camera_preview);
        cheese.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mediaRecorderWrapper.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaRecorderWrapper.stopPreview();
    }

    @Override
    public void onClick(View v) {
        if( mediaRecorderWrapper.isRecording() ) {
            mediaRecorderWrapper.stopRecording();
            cheese.setText("Start");
            finish();
        } else {
            mediaRecorderWrapper.startRecording(getIntent().getStringExtra("filename") + ext);
            cheese.setText("Stop");
        }
    }

}
