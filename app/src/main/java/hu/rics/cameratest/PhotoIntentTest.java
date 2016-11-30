package hu.rics.cameratest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * Created by rics on 2016.11.28..
 */

public class PhotoIntentTest extends Activity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    boolean isPublic;
    boolean isVideo;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        isPublic = getIntent().getBooleanExtra("isPublic",false);
        Log.i(CameraTest.TAG,"isPublic:" + isPublic);
        isVideo = getIntent().getBooleanExtra("isVideo",false);
        Log.i(CameraTest.TAG,"isVideo:" + isVideo);
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takeCameraIntent = new Intent(isVideo ? MediaStore.ACTION_VIDEO_CAPTURE : MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takeCameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(CameraTest.TAG,"Photo file cannot be created:" + ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "hu.rics.cameratest.fileprovider",
                        photoFile);
                // the following block is necessary otherwise intent cannot save photo
                // solution taken from here: http://stackoverflow.com/a/18332000/21047
                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(takeCameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                takeCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takeCameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = (isVideo ? "MPEG_" : "JPEG_") + timeStamp + "_";
        String dirName = isVideo ? Environment.DIRECTORY_MOVIES : Environment.DIRECTORY_PICTURES;
        File storageDir = isPublic ? getExternalStoragePublicDirectory(dirName) :
                                     getExternalFilesDir(dirName);
        File image = File.createTempFile(
                imageFileName,
                isVideo ? ".mp4" : ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i(CameraTest.TAG,"requestCode:" + requestCode + " resultCode:" + resultCode);
    }
}
