package com.trendpicker.repick;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    private OpenApi OpenApi = com.trendpicker.repick.OpenApi.getInstance();

    private String getPlayStoreAppVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String playStorePackageName = "com.trendpicker.repick"; // Play Store 패키지 이름
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(playStorePackageName, 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        String AppVersion = getPlayStoreAppVersion(context).trim();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("업데이트 알림")
                .setMessage("새로운버전으로 업데이트가 필요합니다!")
                .setPositiveButton("업데이트 하러가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 확인 버튼을 눌렀을 때의 동작
                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.naver.com")));
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.trendpicker.repick")));
                        //finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setTitle("인사말").setMessage("반갑습니다");
//
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                System.out.println("body.."+body);
                if(body.equals("false")){
                    System.out.println("bodyㄴㅇㄹㄴㅇㄹㄴㅇㄹㄴㅇㄹㄴㅇㄹㄴㅇㄹㄴㅇㄹㄴㅇㄹ");


                }else{
                    //Toast.makeText(getApplicationContext(), "버전확인 중입니다.", Toast.LENGTH_LONG).show();
                    Intent inte = new Intent(SplashActivity.this , MainActivity.class);
                    if (getIntent().getExtras() != null) {
                        inte.putExtras(getIntent().getExtras());
                    }
                    startActivity(inte);
                    finish();
                }
            }
        };


        new Handler().postDelayed(() -> {
//            Intent inte = new Intent(SplashActivity.this , IntroActivity.class);
//            if (getIntent().getExtras() != null) {
//                inte.putExtras(getIntent().getExtras());
//            }
//            startActivity(inte);
            OpenApi.requestWebServer(AppVersion, callback);
            //finish();

        },1500); // 1500
    }
}