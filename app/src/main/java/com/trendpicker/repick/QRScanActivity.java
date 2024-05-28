package com.trendpicker.repick;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);  // 바코드 형식 설정
        integrator.setPrompt("QR Code를 스캔합니다!");  // 스캔 화면에 표시되는 메시지 설정
        integrator.setCameraId(0);  // 후면 카메라 사용
        integrator.setBeepEnabled(false);    //(false);  // 스캔 소리 사용 여부
        integrator.setOrientationLocked(true);  // (false);

        integrator.initiateScan();  // 스캔 시작
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                setResult(Activity.RESULT_CANCELED);
            } else {
                // 스캔된 코드를 처리합니다.
                String scannedCode = result.getContents();
                Intent intent = new Intent();
                intent.putExtra("scannedCode", scannedCode);
                setResult(Activity.RESULT_OK, intent);
            }
            finish();
        }
    }
}