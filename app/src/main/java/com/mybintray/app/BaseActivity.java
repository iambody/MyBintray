package com.mybintray.app;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.rabbit.permission.RabbitKnife;

/**
 * desc  ${DESC}
 * author wangyongkui
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            RabbitKnife.initPromissionsResult(this, grantResults[0] == PackageManager.PERMISSION_GRANTED, 111);
        }
    }

}
