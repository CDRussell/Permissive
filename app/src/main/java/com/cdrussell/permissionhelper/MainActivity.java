package com.cdrussell.permissionhelper;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cdrussell.permissive.Permissive;

public class MainActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int REQUEST_CODE_PERMISSION_LOCATION = 10;

    private Permissive permissive;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // specify required permissions
        permissive = new Permissive(PERMISSIONS);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performActionRequiringPermission();
            }
        });
    }

    private void performActionRequiringPermission() {
        if (!permissive.needsPermission(this)) {
            accessLocation();
        } else {
            // permissions not yet granted
            permissive.requestPermissionWithRationale(this, REQUEST_CODE_PERMISSION_LOCATION, R.string.rationale_for_permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CODE_PERMISSION_LOCATION) {
            Snackbar.make(toolbar, "Unhandled permission request code " + requestCode, Snackbar.LENGTH_INDEFINITE).show();
            return;
        }

        if (permissive.allPermissionsGranted(grantResults)) {
            accessLocation();
        } else {
            Snackbar snackbar = Snackbar.make(toolbar, R.string.permissive_permission_required, Snackbar.LENGTH_LONG);
            if (!permissive.isRationaleRequired(this)) {
                snackbar.setAction(R.string.permissive_settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = Permissive.buildAppSettingsIntent(MainActivity.this);
                        startActivity(intent);
                    }
                });
            }
            snackbar.show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void accessLocation() {
        // you have the required permission by this time
        Snackbar.make(toolbar, "All required permissions granted", Snackbar.LENGTH_INDEFINITE).show();
    }

}
