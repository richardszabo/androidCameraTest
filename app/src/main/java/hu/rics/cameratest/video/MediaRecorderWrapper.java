package hu.rics.cameratest.video;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.FrameLayout;

import java.io.IOException;

import hu.rics.cameratest.CameraTest;

/**
 * Created by rics on 2017.02.08..
 */

public class MediaRecorderWrapper {
    MediaRecorder mediaRecorder;
    Camera camera;
    CameraPreview cameraPreview;
    boolean isRecording;

    public MediaRecorderWrapper(Activity activity, int id) {
        cameraPreview = new CameraPreview(activity);
        FrameLayout preview = (FrameLayout) activity.findViewById(id);
        preview.addView(cameraPreview);
    }

    public void startPreview() {
        camera=Camera.open(); // attempt to get a Camera instance
        cameraPreview.setCamera(camera);
        cameraPreview.startPreview();
    }

    public void stopPreview() {
        cameraPreview.stopPreview();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
    }

    public void startRecording(String outputfileName) {
        if (prepareMediaRecorder(outputfileName)) {
            mediaRecorder.start();

            isRecording = true;
        } else {
            releaseMediaRecorder();
            isRecording = false;
        }
    }

    public void stopRecording() {
        // stop recording and release camera
        mediaRecorder.stop();  // stop the recording
        releaseMediaRecorder(); // release the MediaRecorder object
        camera.lock();         // take camera access back from MediaRecorder
        isRecording = false;
    }

    private boolean prepareMediaRecorder(String outputfileName) {

        mediaRecorder = new MediaRecorder();

        Log.d(CameraTest.TAG,"prep1");
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        Log.d(CameraTest.TAG,"prep2");
        //http://stackoverflow.com/a/16543157/21047
        Camera.Parameters parameters = camera.getParameters();
        Log.d(CameraTest.TAG,"prep3");
        parameters.setPreviewSize(profile.videoFrameWidth,profile.videoFrameHeight);
        Log.d(CameraTest.TAG,"prep4");
        parameters.setPreviewFormat(ImageFormat.YV12);
        Log.d(CameraTest.TAG,"prep5");
        camera.setParameters(parameters);
        Log.d(CameraTest.TAG,"prep6");

        camera.unlock();
        Log.d(CameraTest.TAG,"prep7");
        mediaRecorder.setCamera(camera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(profile);
        mediaRecorder.setOutputFile(outputfileName);

        mediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException | IOException e) {
            Log.e(CameraTest.TAG, "Exception preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        Log.d(CameraTest.TAG, "prepareMediaRecorder ready");
        return true;
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
        }
    }

    public boolean isRecording() {
        return isRecording;
    }
}
