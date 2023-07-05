package com.trendpicker.repick;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static com.trendpicker.repick.Props.AlertCustom;
import static com.trendpicker.repick.Props.MainBackDoublePressOut;
import static com.trendpicker.repick.Props.MainBackDoublePressOut_GuideString;
import static com.trendpicker.repick.Props.USER_AGENT;
import static com.trendpicker.repick.Props.app_proj_name;
import static com.trendpicker.repick.Props.base_url;
import static com.trendpicker.repick.Props.fcm_data_click_action;
import static com.trendpicker.repick.Props.fcm_permission_success;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.buzzvil.buzzad.benefit.BuzzAdBenefit;
import com.buzzvil.buzzad.benefit.BuzzAdBenefitConfig;
import com.buzzvil.buzzad.benefit.core.ad.AdError;
import com.buzzvil.buzzad.benefit.core.js.BuzzAdBenefitJavascriptInterface;
import com.buzzvil.buzzad.benefit.presentation.feed.BuzzAdFeed;
import com.buzzvil.buzzad.benefit.presentation.feed.FeedConfig;
import com.buzzvil.buzzad.benefit.presentation.interstitial.BuzzAdInterstitial;
import com.buzzvil.buzzad.benefit.presentation.interstitial.InterstitialAdListener;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    BackPressCloseHandler backPressCloseHandler;
    public ValueCallback<Uri> filePathCallbackNormal;
    public ValueCallback<Uri[]> filePathCallbackLollipop;
    public final static int FILECHOOSER_NORMAL_REQ_CODE = 2001;
    public final static int FILECHOOSER_LOLLIPOP_REQ_CODE = 2002;


    private boolean isNews = false;
    String[] exceptUrl = new String[]{
            "https://trendpicker1.cafe24.com/numpick/cash.php",
            "https://trendpicker1.cafe24.com/shop/buy-ok.php",
            "https://trendpicker1.cafe24.com/numpick/withdrawal-history2.php",
            "https://trendpicker1.cafe24.com/numpick/news.php",
            "https://trendpicker1.cafe24.com/numpick/receive-main.php",
            "https://trendpicker1.cafe24.com/mypage/order-cancle.php",
            "https://trendpicker1.cafe24.com/mypage/point.php",
            "https://trendpicker1.cafe24.com/numpick/withdrawal-history.php",
            "https://trendpicker1.cafe24.com/mypage/my-prize.php",
            "https://trendpicker1.cafe24.com/numpick/news.php",
            "https://trendpicker1.cafe24.com/shop/shop.php",
            "https://trendpicker1.cafe24.com/mypage/my-withdrawal.php",
            "https://trendpicker1.cafe24.com/numpick/ad.php",
            "https://trendpicker1.cafe24.com/numpick/scan.php",
            "https://trendpicker1.cafe24.com/mypage/mypage.php",
            "https://trendpicker1.cafe24.com/mypage/order-list.php",
            "https://trendpicker1.cafe24.com/numpick/scan-ok.php"};

    private Uri cameraImageUri = null;
    public final String baseUrl = base_url;
    public WebView webView;
    MAPP mapp;
    DownloadOBJ downloadOBJ = null;

    static class DownloadOBJ {
        String url;
        String content;
        String mime;

        public DownloadOBJ(String url, String content, String mime) {
            this.url = url;
            this.content = content;
            this.mime = mime;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setMime(String mime) {
            this.mime = mime;
        }

        public String getUrl() {
            return url;
        }

        public String getContent() {
            return content;
        }

        public String getMime() {
            return mime;
        }
    }

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


    BuzzAdInterstitial buzzAdInterstitial = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String appVersion = getPlayStoreAppVersion(context).trim();

        // FeedConfig 설정
        final FeedConfig feedConfig = new FeedConfig.Builder("485364925359311")
                .build();

        final BuzzAdBenefitConfig buzzAdBenefitConfig = new BuzzAdBenefitConfig.Builder(getApplicationContext())
                .setDefaultFeedConfig(feedConfig)
                .build();

        BuzzAdBenefit.init(getApplicationContext(), buzzAdBenefitConfig);
        BuzzAdBenefit.init(this, buzzAdBenefitConfig);
        setContentView(R.layout.activity_main);

        buzzAdInterstitial = new BuzzAdInterstitial.Builder("499428234083850").buildBottomSheet();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "001";
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelName_marketing = "REPICK 알림";
            NotificationChannel channel_marketing = null;
            channel_marketing = new NotificationChannel(channelId, channelName_marketing, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel_marketing);
        }
        askNotificationPermission();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed : ", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d("test", "token : " + token);
                });

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        webView = findViewById(R.id.webview);
        final BuzzAdBenefitJavascriptInterface javascriptInterface = new BuzzAdBenefitJavascriptInterface(webView);
        webView.getSettings().setJavaScriptEnabled(true); // JS를 사용하여 광고를 로드하기 때문에 필수임
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 롤리팝부터 Mixed Content 에러 막기 위함
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.addJavascriptInterface(javascriptInterface, BuzzAdBenefitJavascriptInterface.INTERFACE_NAME);

        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mapp = (MAPP) getApplication();
        backPressCloseHandler = new BackPressCloseHandler(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new BaseChrome(this));
        webView.setWebViewClient(new BaseWebClient());
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
//        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setTextZoom(100);
        webView.setWebContentsDebuggingEnabled(true);

        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.clearCache(true);
        webView.clearHistory();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        // 권한 요청 성공
                        if (url.startsWith("data:")) {
                            String path = parseBase64(url);
                            Bitmap bitmap = changeBitmap(path);
                            try {
                                saveImage(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        }

                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
                        String cookies = CookieManager.getInstance().getCookie(url);

                        String name = filename.substring(0, filename.lastIndexOf('.') - 1);
                        String ext = contentDisposition.substring(contentDisposition.lastIndexOf("."));
                        filename = name + ext;

                        request.addRequestHeader("cookie", cookies);
                        request.addRequestHeader("User-Agent", userAgent);
                        request.setDescription("Downloading file..");
                        request.setTitle(filename);
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

                        DownloadManager dManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                        dManager.enqueue(request);

                        Toast.makeText(getApplicationContext(), "다운로드중 입니다...", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        // 권한 요청 실패
                    }
                };

                TedPermission.create()
                        .setGotoSettingButton(true)
                        .setPermissionListener(permissionListener)
                        .setRationaleMessage("사진 및 파일을 저장하기 위하여 접근 권한이 필요합니다.")
                        .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
            }
        });

        webView.addJavascriptInterface(new Bridge(), Props.bridge_name);

        if (!USER_AGENT.isEmpty()) {
            String userAgent = webView.getSettings().getUserAgentString();
            webView.getSettings().setUserAgentString(userAgent + USER_AGENT + " ");
        }
        webView.loadUrl(baseUrl+appVersion);

        if (getIntent().hasExtra(fcm_data_click_action)){
            String action = getIntent().getStringExtra(fcm_data_click_action);
            webView.loadUrl(action);
        }

    }
    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
//                    PackageManager.PERMISSION_GRANTED) {
//                // FCM SDK (and your app) can post notifications.
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                // TODO: display an educational UI explaining to the user the features that will be enabled
//                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
//                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
//                //       If the user selects "No thanks," allow the user to continue without notifications.
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
//
//            } else {
//                // Directly ask for the permission
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
//            }
//        }
    }
    public static String parseBase64(String base64) {

        try {
            Pattern pattern = Pattern.compile("((?<=base64,).*\\s*)",Pattern.DOTALL|Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(base64);
            if (matcher.find()) {
                return matcher.group().toString();
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }

    private Bitmap changeBitmap(String fileContent) {
        try {
            byte[] encodeByte = Base64.decode(fileContent, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void saveImage(Bitmap bitmap) throws IOException {
        boolean saved;
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "sample.jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + app_proj_name + "/image");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + app_proj_name + "/image";

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, "sample.jpg");
            fos = new FileOutputStream(image);

        }

        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
    }

    public static String getExtension(String fileStr){
        String fileExtension = fileStr.substring(fileStr.lastIndexOf(".")+1, fileStr.length());
        return TextUtils.isEmpty(fileExtension) ? null : fileExtension;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView=null;
    }

    private ValueCallback mFilePathCallback;

    void downloadFile(String url, String contentDisposition, String mimetype) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Download file...");
        try {
            String file_name = contentDisposition.split("filename=")[1].replaceAll("\"", "").replaceAll(";", "");
            Log.d("DOWNLdd", file_name);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file_name);
            request.setTitle(file_name);
        } catch (Exception e) {
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
        }
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(request);
        if (downloadOBJ != null)
            downloadOBJ = null;

        Toast.makeText(getApplicationContext(), "Downloading File...", Toast.LENGTH_LONG).show();
    }

    public class BackPressCloseHandler {

        private long backKeyPressedTime = 0;
        private Toast toast;
        private Activity activity;

        public BackPressCloseHandler(Activity context) {
            this.activity = context;
        }

        public void onBackPressed() {
            if (MainBackDoublePressOut) {
                if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                    backKeyPressedTime = System.currentTimeMillis();
                    showGuide();
                    return;
                }
                if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                    activity.finish();
                    toast.cancel();
                }
            } else {
                activity.finish();
            }
        }

        public void showGuide() {
            toast = Toast.makeText(activity, MainBackDoublePressOut_GuideString, Toast.LENGTH_SHORT);
            toast.show();
        }
    }




    @Override
    public void onBackPressed() {
        Log.d("onBack url" ,webView.getUrl());
        for (String except: exceptUrl) {
            if (webView.getUrl().startsWith(except)) {
                webView.evaluateJavascript("javascript:fnGoBack()",null);
                return;
            }
        }
        if (webView.canGoBack() && !webView.getUrl().equals(base_url)) {
//        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            Log.d("URL ", webView.getUrl());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("앱을 종료하시겠습니까?")
                    .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("종료", (dialog, which) -> finish()).setCancelable(true).create().show();
        }
    }

    class Bridge {
        @JavascriptInterface
        public void showInterstitial() {
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    buzzAdInterstitial.load(new InterstitialAdListener() {

                        @Override
                        public void onAdLoadFailed(@Nullable AdError adError) {
                        }

                        @Override
                        public void onAdLoaded() {
                            buzzAdInterstitial.show(MainActivity.this);
                        }
                    });
                }
            });
        }
        @JavascriptInterface
        public void showFeed() {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    new BuzzAdFeed.Builder().build().show(MainActivity.this);
                }
            });
        }
        @JavascriptInterface
        public void call(String number) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
            startActivity(intent);
        }

        @JavascriptInterface
        public void pop(String link) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            intent.setPackage("com.android.chrome");
            startActivity(intent);
        }

        @JavascriptInterface
        public void fcmToken() {
            String token = "";
            webView.evaluateJavascript("javascript:onToken("+token+")",null);
        }

        @JavascriptInterface
        public void getFcmInfo() {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    String token = task.getResult();
                    Log.d("test", "test token : " + token);
                    webView.evaluateJavascript("javascript:" + fcm_permission_success + "('" + token + "' , 'aos')",null);
                }
            });
        }
        @JavascriptInterface
        public void getADID() {
            try{
                String adid = AdvertisingIdClient.getAdvertisingIdInfo(MainActivity.this).getId();

//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("adid test");
//                builder.setMessage("adid : " + adid);
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                builder.setNegativeButton("Cancle", null);
//                builder.show();

                Log.d("adid" , "adid : " + adid);
                runOnUiThread(()->{
                    new Handler().postDelayed(()->{
                        webView.evaluateJavascript("javascript:onAdidReceived('" + adid + "' , 'aos')", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {

                                Log.d("adid" , "value : " + value);
                            }
                        });
                    },500);

                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //권한 획득 여부 확인
    @TargetApi(Build.VERSION_CODES.M)
    public void checkVerify() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                runCamera();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.create()
                .setGotoSettingButton(true)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("사진 및 파일을 업로드하기 위하여 접근 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    // 카메라 기능 구현
    private void runCamera() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File path = getFilesDir();
        File file = new File(path, "sample_" + System.currentTimeMillis() + ".png");
        // File 객체의 URI 를 얻는다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String strpa = getApplicationContext().getPackageName();
            cameraImageUri = FileProvider.getUriForFile(this, strpa + ".fileprovider", file);
        } else {
            cameraImageUri = Uri.fromFile(file);
        }
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);

        if (!isCapture) { // 선택팝업 카메라, 갤러리 둘다 띄우고 싶을 때
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            pickIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            String pickTitle = "사진 가져올 방법을 선택하세요.";
            Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);

            // 카메라 intent 포함시키기..
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{intentCamera});
            startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
        } else {// 바로 카메라 실행..
            startActivityForResult(intentCamera, FILECHOOSER_LOLLIPOP_REQ_CODE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //액티비티가 종료될 때 결과를 받고 파일을 전송할 때 사용
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case FILECHOOSER_NORMAL_REQ_CODE:
                if (resultCode == RESULT_OK) {
                    if (filePathCallbackNormal == null) return;
                    Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
                    //  onReceiveValue 로 파일을 전송한다.
                    filePathCallbackNormal.onReceiveValue(result);
                    filePathCallbackNormal = null;
                }
                break;
            case FILECHOOSER_LOLLIPOP_REQ_CODE:
                if (resultCode == RESULT_OK) {
                    if (filePathCallbackLollipop == null) return;
                    if (data == null)
                        data = new Intent();
                    if (data.getData() == null)
                        data.setData(cameraImageUri);

                    filePathCallbackLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                    filePathCallbackLollipop = null;
                } else {
                    if (filePathCallbackLollipop != null) {   //  resultCode에 RESULT_OK가 들어오지 않으면 null 처리하지 한다.(이렇게 하지 않으면 다음부터 input 태그를 클릭해도 반응하지 않음)
                        filePathCallbackLollipop.onReceiveValue(null);
                        filePathCallbackLollipop = null;
                    }

                    if (filePathCallbackNormal != null) {
                        filePathCallbackNormal.onReceiveValue(null);
                        filePathCallbackNormal = null;
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class BaseChrome extends WebChromeClient {
        private Activity mActivity = null;
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private FrameLayout mFullscreenContainer;
        private  final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);

        public BaseChrome(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        public void onPermissionRequest(final PermissionRequest request) {

            MainActivity.this.runOnUiThread(new Runnable() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {


                    PermissionListener permissionListener = new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            // 권한 요청 성공
                            request.grant(request.getResources());

                        }

                        @Override
                        public void onPermissionDenied(List<String> deniedPermissions) {
                            // 권한 요청 실패

                        }
                    };

                    TedPermission.create()
                            .setGotoSettingButton(true)
                            .setPermissionListener(permissionListener)
                            .setRationaleMessage("사진 및 파일을 업로드하기 위하여 접근 권한이 필요합니다.")
                            .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                            .check();
                }
            });
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            if (AlertCustom) {
                result.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("")
                        .setMessage(message)
                        .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                        .setCancelable(true)
                        .create().show();
                return true;
            } else
                return super.onJsAlert(view, url, message, result);
        }

        @SuppressLint("setJavaScriptEnabled")
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            // window.opener 시
            WebView mWebViewPop = new WebView(view.getContext());
            mWebViewPop.getSettings().setJavaScriptEnabled(true);

            Dialog dialog = new Dialog(view.getContext());
            dialog.setContentView(mWebViewPop);

            ViewGroup.LayoutParams param = dialog.getWindow().getAttributes();
            param.width = MATCH_PARENT;
            param.height = MATCH_PARENT;

            dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) param);
            dialog.show();

            mWebViewPop.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onCloseWindow(WebView window) {
                    window.destroy();
                    dialog.dismiss();

                }
            });
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebViewPop);
            resultMsg.sendToTarget();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            if (AlertCustom) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("")
                        .setMessage(message)
                        .setPositiveButton(getResources().getString(R.string.confirm), (dialog, which) -> {
                            result.confirm();
                            dialog.dismiss();
                        }).setNegativeButton("취소", (dialogInterface, i) -> {
                    result.cancel();
                    dialogInterface.dismiss();
                })
                        .setCancelable(false)
                        .create().show();
                return true;
            } else {
                return super.onJsAlert(view, url, message, result);
            }
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (filePathCallbackLollipop != null) {
                filePathCallbackLollipop.onReceiveValue(null);
                filePathCallbackLollipop = null;
            }
            filePathCallbackLollipop = filePathCallback;

            isCapture = fileChooserParams.isCaptureEnabled();

            checkVerify();
            return true;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                mOriginalOrientation = mActivity.getRequestedOrientation();
                FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
                mFullscreenContainer = new FullscreenHolder(mActivity);
                mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
                decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
                mCustomView = view;
                setFullscreen(true);
                mCustomViewCallback = callback;
            }
            super.onShowCustomView(view, callback);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
            this.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            }
            setFullscreen(false);
            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
            decor.removeView(mFullscreenContainer);
            mFullscreenContainer = null;
            mCustomView = null;
            mCustomViewCallback.onCustomViewHidden();
            mActivity.setRequestedOrientation(mOriginalOrientation);
        }

        private void setFullscreen(boolean enabled) {
            Window win = mActivity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            if (enabled) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
                if (mCustomView != null) {
                    mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }
            win.setAttributes(winParams);
        }

        private class FullscreenHolder extends FrameLayout {
            public FullscreenHolder(Context ctx) {
                super(ctx);
                setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.black));
            }
            @Override
            public boolean onTouchEvent(MotionEvent evt) {
                return true;
            }
        }
    }

    boolean isCapture;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("<TAG>>>" , "Location permission 111");
        if (requestCode == 2){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("<TAG>>>", "Location permission 222");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (downloadOBJ != null)
                        downloadFile(downloadOBJ.url,downloadOBJ.content,downloadOBJ.mime);
                }
            } else {
                Log.d("<TAG>>>", "Location Denied");
            }
        }

        if (requestCode == 1) {
            if (grantResults.length > 0) {
                boolean isDenied = true;
                for (int i = 0; i < grantResults.length; i++) {
                    isDenied &= grantResults[i] == PERMISSION_DENIED;
                }
                if (!isDenied) {
                    // 카메라, 저장소 중 하나라도 거부한다면 앱실행 불가 메세지 띄움
                    new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                            .setPositiveButton("종료", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            }).setNegativeButton("권한 설정", (dialog, which) -> {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                        startActivity(intent);
                    }).setCancelable(false).show();
//                    return;
                } else {
                    runCamera();
                }
            }
        }
    }

    class BaseWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("test", "shouldOverrideUrlLoading url 1 : " + url);
            final Uri uri;
            try {
                uri = Uri.parse(url);
            } catch (Exception e) {
                return false;
            }
            if (!URLUtil.isNetworkUrl(url) && !URLUtil.isJavaScriptUrl(url)) {

                if (uri.getScheme().equalsIgnoreCase("http://") || uri.getScheme().equalsIgnoreCase("https://")){
                    if (!((url.startsWith("https://trendpicker1.cafe24.com")
                            || (url.startsWith("http://trendpicker1.cafe24.com")))
                            || url.startsWith("http://quizmoa.kr")
                            || uri.getScheme().contains("kakao"))){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                    return false;
                } else if (url.contains("about:blank")){
                    return true;
                } else {
                    return startSchemeIntent(url);
                }
            }else if (!(uri.getHost().contains("trendpicker1")
                    || uri.getHost().contains("quizmoa")
                    || uri.getHost().contains("kakao")
                    || uri.getHost().contains("daum")
                    || uri.getHost().contains("naver")
                    || uri.getHost().contains("ex-")
                    || uri.getHost().contains("taptalk")
                    || uri.getHost().contains("postcode")
                    || uri.getHost().contains("pstatic")
                    || uri.getHost().contains("hyundaicard")
                    || url.startsWith("https://www.google.com/recaptcha/api2")
            )) {
                if (!isNews){
                    Log.d("test", "shouldOverrideUrlLoading testCase1 : ");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

            }else {
                if (url.startsWith("https://trendpicker1.cafe24.com/numpick/news")
                    || url.startsWith("http://trendpicker1.cafe24.com/numpick/news")){
                    isNews = true;
                }else{
                    isNews = false;
                }
            }
            return false;
        }

        @RequiresApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.d("test", "shouldOverrideUrlLoading url 2 : " + request.getUrl());

            String url = request.getUrl().toString();
            final Uri uri;
            try {
                uri = Uri.parse(url);
                Log.d("test", "shouldOverrideUrlLoading url 22 : " + uri.getHost());
            } catch (Exception e) {
                return false;
            }

            if (!URLUtil.isNetworkUrl(url) && !URLUtil.isJavaScriptUrl(url)) {

                if (uri.getScheme().equalsIgnoreCase("http://") || uri.getScheme().equalsIgnoreCase("https://")){

                    Log.d("test", "shouldOverrideUrlLoading testCase1 : ");
                    if (!((url.startsWith("https://trendpicker1.cafe24.com")
                            || (url.startsWith("http://trendpicker1.cafe24.com")))
                            || url.startsWith("http://quizmoa.kr")
                            || uri.getScheme().contains("kakao"))){
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }

                    return false;
                } else if (url.contains("about:blank")){
                    return true;
                } else {
                    return startSchemeIntent(url);
                }

            }else if (!(uri.getHost().contains("trendpicker1")
                    || uri.getHost().contains("quizmoa")
                    || uri.getHost().contains("kakao")
                    || uri.getHost().contains("daum")
                    || uri.getHost().contains("naver")
                    || uri.getHost().contains("ex-")
                    || uri.getHost().contains("taptalk")
                    || uri.getHost().contains("postcode")
                    || uri.getHost().contains("pstatic")
                    || uri.getHost().contains("hyundaicard")
                    || url.startsWith("https://www.google.com/recaptcha/api2")
            )) {
                if (!isNews){
                    Log.d("test", "shouldOverrideUrlLoading testCase1 : ");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

            }else {
                if (url.startsWith("https://trendpicker1.cafe24.com/numpick/news")
                        || url.startsWith("http://trendpicker1.cafe24.com/numpick/news")){
                    isNews = true;
                }else{
                    isNews = false;
                }
            }

            return super.shouldOverrideUrlLoading(view, request);
        }

        private boolean startSchemeIntent(String url) {
            final Intent schemeIntent;

            try {
                schemeIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            } catch (URISyntaxException e) {
                return false;
            }

            try {
                startActivity(schemeIntent);
                return true;
            } catch (ActivityNotFoundException e) {
                final String packageName = schemeIntent.getPackage();

                if (!TextUtils.isEmpty(packageName)) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    return true;
                } else if (url.startsWith("kftc-bankpay:")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.kftc.bankpay.android")));
                    return true;
                } else if (url.startsWith("newliiv:")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.kbstar.reboot")));
                    return true;
                } else if (url.startsWith("kb-bankpay:")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.kbstar.liivbank")));
                    return true;
                } else if (url.startsWith("nhb-bankpay:")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nhnent.payapp")));
                    return true;
                } else if (url.startsWith("mg-bankpay:")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=kr.co.kfcc.mobilebank")));
                    return true;
                } else if (url.startsWith("kn-bankpay:")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.knb.psb")));
                    return true;
                }

            }

            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            CookieManager.getInstance().setAcceptCookie(true);
            CookieManager.getInstance().acceptCookie();
            CookieManager.getInstance().flush();
        }

        public static final String INTENT_PROTOCOL_START = "intent:";
        public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
        public static final String INTENT_PROTOCOL_END = ";end;";
        public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";
    }
}
