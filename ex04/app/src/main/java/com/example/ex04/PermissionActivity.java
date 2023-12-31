package com.example.ex04;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.ArrayList;

public class PermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        
        if(checkAndRequestPermission()){ //permission이 다 되어있으면 MainActivity로 이동
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    //권한을 확인하고, 권한이 부여되어 있지 않으면 권한을 요청한다.
    private boolean checkAndRequestPermission() {
        String[] permissions = {
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
        };
        ArrayList<String> permissionNeedes = new ArrayList<>();
        for(String permission:permissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionNeedes.add(permission); //권한이 부여되어있지 않으면 permission을 하나씩 더한다.
            }
        }
        if(!permissionNeedes.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionNeedes.toArray(new String[permissionNeedes.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;
        for(int i = 0; i < permissions.length; i++){
            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                isAllGranted = false;
            }
        }
        if(isAllGranted){
            Intent intent = new Intent(PermissionActivity.this, MainActivity.class);
            startActivity(intent);
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("알림")
                    .setMessage("권한 설정이 필요합니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }
}