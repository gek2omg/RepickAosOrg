package com.trendpicker.repick;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import com.buzzvil.buzzad.benefit.core.js.BuzzAdBenefitJavascriptInterface;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BackPopupActivity extends Activity {
    //public final String baseUrl = "https://trendpicker1.cafe24.com/numpick/receive-main.php";
    public final String baseUrl = "https://naver.com";
    private WebView webView; // WebView 객체 선언
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature( Window.FEATURE_NO_TITLE );

        setContentView(R.layout.activity_backpopup);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // WebView 생성채
        WebView webView = new WebView(this);
                final BuzzAdBenefitJavascriptInterface javascriptInterface = new BuzzAdBenefitJavascriptInterface(webView);
        webView.getSettings().setJavaScriptEnabled(true); // JS를 사용하여 광고를 로드하기 때문에 필수임
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 롤리팝부터 Mixed Content 에러 막기 위함
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.addJavascriptInterface(javascriptInterface, BuzzAdBenefitJavascriptInterface.INTERFACE_NAME);
        // WebView 객체가 null이 아닌 경우에만 작업 수행

        webView.addJavascriptInterface(javascriptInterface, "Android");

                webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
//        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setTextZoom(100);
        webView.setWebContentsDebuggingEnabled(true);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.clearCache(true);
        webView.clearHistory();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }


        // WebView 설정
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://trendpicker1.cafe24.com/mypage/backpopup.php"); // 원하는 URL로 변경
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setContentView(webView);
        bottomSheetDialog.show();



//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//        WebView webView = new WebView(this);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl("https://www.example.com"); // 원하는 URL로 변경
//
//        bottomSheetDialog.setContentView(webView);
//        bottomSheetDialog.show();
//
//        webView = findViewById(R.id.backWebview); // WebView 객체를 레이아웃과 연결
//
//        final BuzzAdBenefitJavascriptInterface javascriptInterface = new BuzzAdBenefitJavascriptInterface(webView);
//        webView.getSettings().setJavaScriptEnabled(true); // JS를 사용하여 광고를 로드하기 때문에 필수임
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            // 롤리팝부터 Mixed Content 에러 막기 위함
//            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
//        webView.addJavascriptInterface(javascriptInterface, BuzzAdBenefitJavascriptInterface.INTERFACE_NAME);
//        // WebView 객체가 null이 아닌 경우에만 작업 수행
//
//                webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.getSettings().setAllowFileAccess(true);
//        webView.getSettings().setAllowFileAccessFromFileURLs(true);
//        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
////        webView.getSettings().setBuiltInZoomControls(true);
//        webView.getSettings().setSupportZoom(true);
//        webView.getSettings().setDisplayZoomControls(false);
//        webView.getSettings().setTextZoom(100);
//        webView.setWebContentsDebuggingEnabled(true);
//        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webView.getSettings().setAllowContentAccess(true);
//        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
//        webView.clearCache(true);
//        webView.clearHistory();
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        }
//
//
//        // WebView 설정
//        webView.setWebViewClient(new WebViewClient());
//        webView.getSettings().setJavaScriptEnabled(true);
//
////        int parentHeight = webView.getParent().
////        int parentWidth = webView.getParent().getWidth();
////        int webViewWidth = (int) (parentWidth * 0.8);
////        int webViewHeight = (int) (parentHeight * 0.6);
//
////        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
////                RelativeLayout.LayoutParams., // width: 부모 레이아웃의 전체 크기
////                ViewGroup.LayoutParams.MATCH_PARENT // height: 부모 레이아웃의 전체 크기
////             //   webViewWidth, webViewHeight
////        );
//   //     webView.setLayoutParams(layoutParams);
//
//        webView.loadUrl(baseUrl); // 원하는 URL로 로드

    }


    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

}
