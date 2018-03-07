package com.mybintray.app;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.rabbit.permission.RabbitKnife;
import com.rabbit.permission.Rpermission;
import com.rabbit.permission.RpermissionResult;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paizhao();
            }
        });
    }

    @Rpermission(permission ={ Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void paizhao() {
        RabbitKnife.initPromissions(this);
    }

    @RpermissionResult(permissionResult = true, requestCode = 111)
    public void toDoThing(){
        Toast.makeText(MainActivity.this,"执行aaa",Toast.LENGTH_LONG).show();
    }

}
