package com.trendpicker.repick;
// 222
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
import android.media.AudioAttributes;
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
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.buzzvil.booster.external.BuzzBooster;
import com.buzzvil.booster.external.BuzzBoosterConfig;
import com.buzzvil.booster.external.BuzzBoosterJavaScriptInterface;
import com.buzzvil.buzzad.benefit.BuzzAdBenefit;
import com.buzzvil.buzzad.benefit.BuzzAdBenefitConfig;
import com.buzzvil.buzzad.benefit.core.ad.AdError;
import com.buzzvil.buzzad.benefit.core.js.BuzzAdBenefitJavascriptInterface;
import com.buzzvil.buzzad.benefit.presentation.feed.BuzzAdFeed;
import com.buzzvil.buzzad.benefit.presentation.feed.FeedConfig;
import com.buzzvil.buzzad.benefit.presentation.interstitial.BuzzAdInterstitial;
import com.buzzvil.buzzad.benefit.presentation.interstitial.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.tnkfactory.ad.TNK_POINT_EFFECT_TYPE;
import com.tnkfactory.ad.TnkAdConfig;
import com.tnkfactory.ad.TnkOfferwall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

//    // for google admob(일반 전면)
//    private InterstitialAd mInterstitialAd;

    // for google admob(rewarded 전면)
    private RewardedAd mRewardedAd;

    // for tnkad
    private TnkOfferwall offerwall;
    private String m_user_id;


    BackPressCloseHandler backPressCloseHandler;
    public ValueCallback<Uri> filePathCallbackNormal;
    public ValueCallback<Uri[]> filePathCallbackLollipop;
    public final static int FILECHOOSER_NORMAL_REQ_CODE = 2001;
    public final static int FILECHOOSER_LOLLIPOP_REQ_CODE = 2002;
    public final static int QRSCANNER_REQ_CODE = 1093;

    BottomSheetDialog bottomSheetDialog;
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


    //------------------------------------------------------------------//
    private void checkCameraPermission(PermissionListener permissionListener, String okMsg, String rejectMsg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            TedPermission.create()
                    .setGotoSettingButton(true)
                    .setPermissionListener(permissionListener)
                    .setRationaleMessage(okMsg)
                    .setDeniedMessage(rejectMsg)
                    .setPermissions(// Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_AUDIO)
                    .check();
        } else {

            TedPermission.create()
                    .setGotoSettingButton(true)
                    .setPermissionListener(permissionListener)
                    .setRationaleMessage(okMsg)
                    .setDeniedMessage(rejectMsg)
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check();
        }

    }
    //------------------------------------------------------------------//
    private void openCustomTab(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        // 상단 바의 색상을 설정합니다.
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.white));

        // 로고 설정
//        Bitmap closeButtonIcon = BitmapFactory.decodeResource(getResources(), R.drawable.sp_logo);
//        builder.setCloseButtonIcon(closeButtonIcon);

        // 애니메이션 설정
        builder.setStartAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out);
        builder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    public void processQRCode(String code) {
        // String data = "http://m.dhlottery.co.kr/?v=1111q050911314044q062730313944q012835384044q061422313443q0105181940411221084601";
        webView.evaluateJavascript("javascript:getQrScan('" + code + "')",null);

        Toast.makeText(this , "QR Code 스캔 완료!", Toast.LENGTH_LONG).show();
    }

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


        String appVersion = "?appVersion="+ getPlayStoreAppVersion(context).trim();

        // FeedConfig 설정
        final FeedConfig feedConfig = new FeedConfig.Builder("485364925359311")
                .build();

        final BuzzAdBenefitConfig buzzAdBenefitConfig = new BuzzAdBenefitConfig.Builder(getApplicationContext())
                .setDefaultFeedConfig(feedConfig)
                .build();

        // 버스부스터
        final BuzzBoosterConfig buzzBoosterConfig = new BuzzBoosterConfig("136614165053620");
        BuzzBooster.init(this, buzzBoosterConfig);

        BuzzAdBenefit.init(getApplicationContext(), buzzAdBenefitConfig);
        BuzzAdBenefit.init(this, buzzAdBenefitConfig);
        setContentView(R.layout.activity_main);

        buzzAdInterstitial = new BuzzAdInterstitial.Builder("499428234083850").buildBottomSheet();

        // 구글 adMob 초기화
        MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                loadGoogleRewardedAd();
            }
        });


        setupFcmNotificationChannel();

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

        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5 Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.96 Mobile Safari/537.36");

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

                checkCameraPermission(permissionListener, "사진 및 파일을 저장하기 위하여 접근 권한이 필요합니다.", "[설정] > [권한] 에서 권한을 허용할 수 있습니다.");
            }
        });

        webView.addJavascriptInterface(new Bridge(), Props.bridge_name);
        webView.addJavascriptInterface( new BuzzBoosterJavaScriptInterface(this),"buzzBoosterJS");

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


    private void setupFcmNotificationChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            //--- setup start for push sound -------------------------//
            Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fcm);
            AudioAttributes soundAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)  // USAGE_NOTIFICATION   USAGE_NOTIFICATION_RINGTONE USAGE_ALARM
                    .build();
            //--- setup end   for push sound -------------------------//


            final String channelId = "001";
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelName_marketing = "REPICK 알림";
            NotificationChannel channel_marketing = new NotificationChannel(channelId, channelName_marketing, NotificationManager.IMPORTANCE_HIGH);

            //--- setup start for push sound -------------------------//
            channel_marketing.setSound(soundUri, soundAttributes);
            //--- setup end   for push sound -------------------------//

            notificationManager.createNotificationChannel(channel_marketing);
        }
    }

    // 구글 adMob 초기화

    private void loadGoogleRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, getString(R.string.interstitial_add_unit),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        // Handle the error.
                        mRewardedAd = null;
                        Toast.makeText(MainActivity.this, "Ad Load Fail!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        super.onAdLoaded(ad);
                        mRewardedAd = ad;

                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d("RewardedAd", "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d("RewardedAd", "Ad dismissed fullscreen content.");
                                mRewardedAd = null;
                                loadGoogleRewardedAd();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                                // Called when ad fails to show.
                                Log.e("RewardedAd", "Ad failed to show fullscreen content.");
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d("RewardedAd", "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d("RewardedAd", "Ad showed fullscreen content.");
                            }
                        });
                        // Toast.makeText(MainActivity.this, "Ad Load OK!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        mInterstitialAd = null;
//
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//                loadInterstitialAd();
//            }
//        });
//    }

    // 시작하기: QR Code Scan
    public void startNativeQrScan() {
//        Toast.makeText(MainActivity.this, "Start QR Code Scan!", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, QRScanActivity.class);
//        startActivityForResult(intent, QRSCANNER_REQ_CODE);



        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
                // Toast.makeText(MainActivity.this, "Start QR Code Scan!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, QRScanActivity.class);
                startActivityForResult(intent, QRSCANNER_REQ_CODE);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        checkCameraPermission(permissionListener, "사진 및 동영상 녹화를 위하여 접근 권한이 필요합니다.", "[설정] > [권한] 에서 권한을 허용할 수 있습니다.");
    }


//    public void startAdMob() {
//        if (mInterstitialAd != null) {
//            mInterstitialAd.show(MainActivity.this);
//        } else {
//            Toast.makeText(MainActivity.this, "초기화 중, 잠시 후 다시 시도해 주세요!", Toast.LENGTH_LONG).show();
//        }
//    }

    // tnkAdMob
    // javascript -> TNKAD 시작 처리
    private void startTnkAd(String user_id) {

        if (m_user_id != null && offerwall != null) {
            if (m_user_id.equals(user_id)) {
                // Toast.makeText(MainActivity.this, "2 번째 실행", Toast.LENGTH_SHORT).show();
                offerwall.startOfferwallActivity(MainActivity.this);
                return;
            }
        }

        m_user_id = user_id;
        offerwall = new TnkOfferwall(this);

        Runnable rn = () -> {
            // 고유 아이디는 매체사에서 유저 식별을 위한 고유값을 사용하셔야 하며
            // 이 예제에서는 google adid를 사용 합니다.
            // backgroud thread 처리 필요
            //---AdvertisingIdInfo adInfo = AdvertisingIdInfo.requestIdInfo(MainActivity.this);
            //---String id = adInfo.getId();

            // 2) 유저 식별값 설정
            //-----offerwall.setUserName(id);
            offerwall.setUserName(user_id);
            // 3) COPPA 설정 (https://www.ftc.gov/business-guidance/privacy-security/childrens-privacy)
            offerwall.setCOPPA(false);
            // 4) 포인트 금액 앞에 아이콘, 뒤에 재화 단위 출력 여부를 설정합니다.
            TnkAdConfig.INSTANCE.setPointEffectType(TNK_POINT_EFFECT_TYPE.UNIT);

            offerwall.getEarnPoint(point -> {
                runOnUiThread(() -> {
                    // Toast.makeText(MainActivity.this, String.format("받을 수 있는 포인트 : %d p", point), Toast.LENGTH_SHORT).show();
                    offerwall.startOfferwallActivity(MainActivity.this);
                });
                return null;
            });
        };
        new Thread(rn).start();

    }

    public void startAdMob() {

        if (mRewardedAd == null) {
            Toast.makeText(MainActivity.this, "초기화 중, 잠시 후 다시 시도해 주세요!", Toast.LENGTH_LONG).show();
            return;
        }

        if (mRewardedAd != null) {
            runOnUiThread(() -> {
                        Activity activityContext = MainActivity.this;
                        mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                // Handle the reward.
                                // Log.d("RewardedAd", "The user earned the reward.");
                                // int rewardAmount = rewardItem.getAmount();
                                // String rewardType = rewardItem.getType();
                                webView.evaluateJavascript("javascript:isAdComplete()", null);
                            }
                        });
                    }
            );
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
        webView.removeJavascriptInterface("buzzBoosterJS");
        webView=null;
        super.onDestroy();
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
        if (webView.canGoBack() && !webView.getUrl().contains(base_url)) {
//        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            Log.d("URL ", webView.getUrl());
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("앱을 종료하시겠습니까?")
//                    .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
//                    .setPositiveButton("종료", (dialog, which) -> finish()).setCancelable(true).create().show();

//            Intent intent = new Intent(this, BackPopupActivity.class);
//            intent.putExtra("data", "Test Popup");
//            startActivityForResult(intent, 1);
            // setContentView(R.layout.activity_backpopup);
            final View view = getLayoutInflater().inflate(R.layout.activity_backpopup, null);

            bottomSheetDialog = new BottomSheetDialog(this);
            // bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
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

            webView.addJavascriptInterface(new Bridge(), Props.bridge_name);


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
            //---webView.loadUrl("https://trendpicker1.cafe24.com/mypage/backpopup.php"); // 원하는 URL로 변경
            webView.loadUrl("https://trendpicker1.cafe24.com/mypage/backpopup.php"); // 원하는 URL로 변경
            // bottomSheetDialog.setCancelable(false);   다른곳눌렀을때 취소
            bottomSheetDialog.setContentView(webView);

            ViewGroup.LayoutParams layoutParams = webView.getLayoutParams();
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            layoutParams.height = (int) (screenHeight * 0.60); // 화면 높이의 60%
            webView.setLayoutParams(layoutParams);

            bottomSheetDialog.show();

            //bottomSheetDialog.onBackPressed();

        }
    }

    class Bridge {

        // tnkad 시작하기
        @JavascriptInterface
        public void tnkAdMob(String user_id) {
            startTnkAd(user_id);
        }

        // 구글 애드몹 시작하기
        @JavascriptInterface
        public void playAdMob() {
            startAdMob();
        }

        @JavascriptInterface
        public void startQrScan() {
            startNativeQrScan();
        }

        @JavascriptInterface
        public void popupCancel() {
            // 버튼 클릭 시 실행할 작업을 처리하는 메소드
            // 이 부분에서 원하는 동작을 수행하면 됩니다.
            //super.dismiss();
            bottomSheetDialog.onBackPressed();
        }

        @JavascriptInterface
        public void popupClick() {
            // 버튼 클릭 시 실행할 작업을 처리하는 메소드
            // 이 부분에서 원하는 동작을 수행하면 됩니다.
            finish();
        }

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

        checkCameraPermission(permissionListener, "사진 및 파일을 업로드하기 위하여 접근 권한이 필요합니다.", "[설정] > [권한] 에서 권한을 허용할 수 있습니다.");

//        TedPermission.create()
//                .setGotoSettingButton(true)
//                .setPermissionListener(permissionListener)
//                .setRationaleMessage("사진 및 파일을 업로드하기 위하여 접근 권한이 필요합니다.")
//                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
//                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
//                .check();
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

    // 액티비티가 종료될 때 결과를 받고 파일을 전송할 때 사용
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case QRSCANNER_REQ_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String scannedCode = data.getStringExtra("scannedCode");
                    processQRCode(scannedCode);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // Toast.makeText(this,"스캔을 취소하였습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
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

                    checkCameraPermission(permissionListener, "사진 및 파일을 업로드하기 위하여 접근 권한이 필요합니다.", "[설정] > [권한] 에서 권한을 허용할 수 있습니다.");
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

            // 구글 로그인 URL 감지
//            if (url.startsWith("https://accounts.google.com/")) {
//                openCustomTab(url);
//                return true;
//            }

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
                    || uri.getHost().contains("google")
                    || uri.getHost().contains("googleapis")
                    || uri.getHost().contains("youtube")
                    || uri.getHost().contains("gmail")
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

            // pincrux 폼전송시 외부 브라우저로 전송, 웹뷰는 현재 페이지로 전송
            if (url.contains("pincrux.shop")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);

                webView.loadUrl("https://trendpicker1.cafe24.com/numpick/receive-api.php");

                return true;
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
                    || uri.getHost().contains("google")
                    || uri.getHost().contains("googleapis")
                    || uri.getHost().contains("youtube")
                    || uri.getHost().contains("gmail")
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