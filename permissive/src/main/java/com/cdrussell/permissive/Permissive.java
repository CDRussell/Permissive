package com.cdrussell.permissive;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.CheckResult;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class Permissive {

    public static final String PACKAGE = "package";

    private String[] permissions;

    public Permissive(String... permissions) {
        this.permissions = permissions;
    }

    public boolean needsPermission(Context context) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PERMISSION_GRANTED) {
                return true;
            }
        }

        return false;
    }

    public boolean isRationaleRequired(Activity activity) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }

        return false;
    }

    public void requestPermissionWithRationale(final Activity activity, final int requestCode, @StringRes int rationaleStringId) {
        if (isRationaleRequired(activity)) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.permissive_permission_required)
                    .setMessage(rationaleStringId)
                    .setPositiveButton(R.string.permissive_next, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermission(activity, requestCode);
                        }
                    }).show();
        } else {
            requestPermission(activity, requestCode);
        }

    }

    public void requestPermission(Activity activity, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    @CheckResult
    public static Intent buildAppSettingsIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts(PACKAGE, context.getPackageName(), null);
        intent.setData(uri);
        return intent;
    }

    public boolean allPermissionsGranted(int[] grantResults) {
        if (grantResults.length == 0) {
            return false;
        }

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }
}
