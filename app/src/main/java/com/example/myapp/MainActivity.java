package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    // 所需的全部权限
    static final String[] all_permissions = new String[]{
        Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CALL_LOG,Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.ANSWER_PHONE_CALLS,Manifest.permission.USE_SIP,
        Manifest.permission.PROCESS_OUTGOING_CALLS,Manifest.permission.READ_PHONE_NUMBERS
};
//    Manifest.permission.ADD_VOICEMAIL


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission(all_permissions);
//        requestPermission(Permission.Group.STORAGE);
//        requestPermission(Permission.Group.PHONE);
//        requestPermission(Permission.RECORD_AUDIO);
        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SecondActivity.class));
            }
        });
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限申请失败");
        builder.setMessage(R.string.string_help_text);
        // 拒绝, 退出应用
        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        builder.show();
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0){
            requestPermission(all_permissions);
        }
    }

    private void registerPhoneStateListener() {
        Intent intent = new Intent(this, PhoneListenService.class);
        startService(intent);
        Log.i("===","Service");

    }

    private void requestPermission(String... permissions) {
        AndPermission.with(this)
                .runtime()
                .permission(permissions)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //允许
                        Log.i("===p允许",permissions.get(0));
                        registerPhoneStateListener();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, permissions)) {
                            //拒绝-不再询问
                            Log.i("===不再询问",permissions.get(0));
                            showMissingPermissionDialog();
                        }else {
                            Log.i("===p拒绝",permissions.get(0));
                            requestPermission(all_permissions);
                        }
                    }
                })
                .start();
    }

}
