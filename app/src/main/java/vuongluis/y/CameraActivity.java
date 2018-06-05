package vuongluis.y;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class CameraActivity extends AppCompatActivity {

    private ImageView imvTakeAPhoto;
    private Bitmap photo;

    private final int CAMERA_REQUEST = 2;
    private final int MY_PERMISSIONS_REQUEST_CODE = 1;
    private final String FAXAGE_IMAGE = "EzFaxing_";
    private final String FAXAGE_DESCRIPTION = "EzFaxing__";

    /**
     * Check permission for Android with version >= 6.0
     **/
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSIONS_REQUEST_CODE) {
            return;
        }
        boolean isGranted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }

        if (isGranted) {
            startApplication();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
        }
    }

    private void setPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CODE);
    }

    public void startApplication() {
        initComponents();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPermissions()) {
            startApplication();
        } else {
            setPermissions();
        }
    }

    private void initComponents() {
        imvTakeAPhoto = (ImageView) this.findViewById(R.id.imvTakeAPhoto);
        imvTakeAPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            setImageView(data);
            Uri selectedImageUri = data.getData();
            Log.i("TAG", "Uri: "+selectedImageUri);
            if (selectedImageUri != null) {
                // Get path of image from Gallery
                String selectedImagePath = getRealPathFromURI(selectedImageUri);
                Log.i("TAG", "PATH: " + selectedImagePath);
            } else {
                // Null and we need to insert new image
                Uri tempUri = getImageUri(getApplicationContext(), photo);
                File file = new File(getRealPathFromURI(tempUri));
                Log.i("TAG", "PATH: " + file.getAbsolutePath());
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, FAXAGE_IMAGE, FAXAGE_DESCRIPTION);
        return Uri.parse(path);
    }

    public void setImageView(Intent data) {
        photo = (Bitmap) data.getExtras().get("data");
        imvTakeAPhoto.setImageBitmap(photo);
    }
}
