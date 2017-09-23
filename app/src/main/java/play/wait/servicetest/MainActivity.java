package play.wait.servicetest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import play.wait.service.MyService;


public class MainActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    private void permissionGranted() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(MainActivity.this, PERMISSIONS[0])) {
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_service_nointerface);
        permissionGranted();
        Intent startIntent = new Intent(getApplicationContext(), MyService.class);
        startService(startIntent);


    }
}
