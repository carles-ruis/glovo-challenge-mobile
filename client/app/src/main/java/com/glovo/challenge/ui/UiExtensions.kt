package com.glovo.challenge.ui

import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity

fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission)

fun AppCompatActivity.requestPermissionsCompat(permissionsArray: Array<String>, requestCode: Int) {
    ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
}