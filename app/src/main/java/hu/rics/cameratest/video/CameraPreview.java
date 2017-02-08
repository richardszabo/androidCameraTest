package hu.rics.cameratest.video;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import hu.rics.cameratest.CameraTest;

/**
 * Created by rics on 2017.02.08..
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Camera camera;
    private boolean previewIsRunning;

    public CameraPreview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder = getHolder();
        holder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera c) {
        camera = c;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera.setPreviewDisplay(surfaceHolder);
            Log.d(CameraTest.TAG,"surfacecreated");
        } catch (Exception e) {
            Log.e(CameraTest.TAG,e.getMessage(),e);
            // Camera is not available (in use or does not exist)
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        startPreview();
        Log.d(CameraTest.TAG,"surfacechanged---------------------");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopPreview();
        camera.release();
        camera = null;
        Log.d(CameraTest.TAG,"surfacedestroyed");
    }

    // safe call to start the preview
    // if this is called in onResume, the surface might not have been created yet
    // so check that the camera has been set up too.
    public void startPreview() {
        if (!previewIsRunning && (camera != null)) {
            camera.startPreview();
            previewIsRunning = true;
        }
    }

    // same for stopping the preview
    public void stopPreview() {
        if (previewIsRunning && (camera != null)) {
            camera.stopPreview();
            previewIsRunning = false;
        }
    }

}
