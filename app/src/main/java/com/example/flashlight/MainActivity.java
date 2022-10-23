package com.example.flashlight;

import android.Manifest;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {
    private ImageButton imageButton;
    boolean state;
    private Camera mCamera;
    private Camera.Parameters parameters;
    private CameraManager cameraManager;
    private String mCameraId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton = findViewById(R.id.imageButton);

        Dexter.withContext(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                        mCameraId = cameraManager.getCameraIdList()[0];
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }else {
                    mCamera = Camera.open();
                    parameters = mCamera.getParameters();
                }
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(MainActivity.this, "Camera permission required.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permissionRequest, PermissionToken permissionToken) {

            }
        }).check();

        imageButton.setOnClickListener(view -> {
            if (state){
                turnOffFlashLight();
                state = false;
            }else {
                turnOnFlashLight();
                state = true;
            }
        });
    }

    public void turnOnFlashLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(mCameraId, true);
                imageButton.setImageResource(R.drawable.ic_flash_on);
            }else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Sorry, Their device does not have flashlight.", Toast.LENGTH_SHORT).show();
        }
    }

    public void turnOffFlashLight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(mCameraId, false);
                imageButton.setImageResource(R.drawable.ic_flash_off);
            }else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                mCamera.stopPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}