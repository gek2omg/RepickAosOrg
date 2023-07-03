package com.trendpicker.repick;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class WebViewAdInterface {

    private WebView mAppView;
    private Context mContext;

    private final String mCountryIso;
    private final String mDeviceModel;
    private final String mDeviceMake;
    private final String mDeviceOsVersion;
    private final String mAppVersion;
    private final String mMcc;
    private final String mMnc;

    static String mIfa;
    static Double mLatitude;
    static Double mLongitude;

    public WebViewAdInterface(Context activity, WebView view) {
        mAppView = view;
        mContext = activity;
        mDeviceModel = Build.MODEL;
        mDeviceMake = Build.BRAND;
        mDeviceOsVersion = Build.VERSION.RELEASE;

        final TelephonyManager telephonyManager =
                (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        mCountryIso = getCountryIso(mContext);

        String tempVersion = "";
        try {
            final String packageName = mContext.getPackageName();
            final PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
            tempVersion = packageInfo.versionName;
        } catch (Exception exception) {
            Log.d("TAG","Failed to retrieve PackageInfo#versionName.");
        }
        mAppVersion = tempVersion;

        String networkOperator = telephonyManager.getNetworkOperator();

        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA &&
                telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
            networkOperator = telephonyManager.getSimOperator();
        }
        mMcc = networkOperator == null ? "" : networkOperator.substring(0, Math.min(3, networkOperator.length()));
        mMnc = networkOperator == null ? "" : networkOperator.substring(Math.min(3, networkOperator.length()));

        if (TextUtils.isEmpty(mIfa)) {
            new Thread(mAdvertisingRunnable).start();
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(mLatitude == null || mLongitude == null) {

                LocationServices.getFusedLocationProviderClient(mContext).getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    mLatitude = location.getLatitude();
                                    mLongitude = location.getLongitude();
                                }
                            }
                        });
            }
        }
    }

    @JavascriptInterface
    public String getIfa() {
        if(!TextUtils.isEmpty(mIfa))
            return mIfa;
        return "";
    }

    @JavascriptInterface
    public boolean isCoppa() {
        return false;
    }

    @JavascriptInterface
    public boolean hasYob() {
        return false;
    }

    @JavascriptInterface
    public String getYob() {
        return null;
    }

    @JavascriptInterface
    public boolean hasGender() {
        return false;
    }

    @JavascriptInterface
    public String getGender() {
        return null;
    }

    @JavascriptInterface
    public boolean hasSegment() {
        return false;
    }

    @JavascriptInterface
    public Map<String, String> getSegment() {
        return null;
    }

    @JavascriptInterface
    public boolean hasMobileCountryCode()
    {
        return !TextUtils.isEmpty(mMcc);
    }

    @JavascriptInterface
    public String getMobileCountryCode()
    {
        return mMcc;
    }

    @JavascriptInterface
    public boolean hasMobileNetworkCode() {
        return !TextUtils.isEmpty(mMnc);
    }

    @JavascriptInterface
    public String getMobileNetworkCode() {
        return mMnc;
    }

    @JavascriptInterface
    public boolean hasCountryIso() {
        return !TextUtils.isEmpty(mCountryIso);
    }

    @JavascriptInterface
    public String getCountryIso() {
        return mCountryIso;
    }

    @JavascriptInterface
    public boolean hasDeviceModel() {
        return !TextUtils.isEmpty(mDeviceModel);
    }

    @JavascriptInterface
    public String getDeviceModel() {
        return mDeviceModel;
    }

    @JavascriptInterface
    public boolean hasDeviceMake() {
        return !TextUtils.isEmpty(mDeviceMake);
    }

    @JavascriptInterface
    public String getDeviceMake() {
        return mDeviceMake;
    }

    @JavascriptInterface
    public boolean hasOsVersion() {
        return !TextUtils.isEmpty(mDeviceOsVersion);
    }

    @JavascriptInterface
    public String getOsVersion() {
        return mDeviceOsVersion;
    }

    @JavascriptInterface
    public boolean hasAppVersion() {
        return !TextUtils.isEmpty(mAppVersion);
    }

    @JavascriptInterface
    public String getAppVersion() {
        return mAppVersion;
    }

    @JavascriptInterface
    public boolean hasGeo() {
        return mLatitude != null && mLongitude != null;
    }

    @JavascriptInterface
    public String getLat() {
        if(mLatitude != null)
            return Double.toString(mLatitude);

        return "";
    }

    @JavascriptInterface
    public String getLon() {
        if(mLongitude != null)
            return Double.toString(mLongitude);

        return "";
    }

    private static String getCountryIso(Context context) {
        String countryCode;

        // try to get country code from TelephonyManager service
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm != null) {
            // query first getSimCountryIso()
            countryCode = tm.getSimCountryIso();
            if (countryCode != null && countryCode.length() == 2)
                return countryCode.toLowerCase();

            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                // special case for CDMA Devices
                countryCode = getCDMACountryIso();
            } else {
                // for 3G devices (with SIM) query getNetworkCountryIso()
                countryCode = tm.getNetworkCountryIso();
            }

            if (countryCode != null && countryCode.length() == 2)
                return countryCode.toLowerCase();
        }

        // if network country not available (tablets maybe), get country code from Locale class
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            countryCode = context.getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            countryCode = context.getResources().getConfiguration().locale.getCountry();
        }

        if (countryCode != null && countryCode.length() == 2)
            return  countryCode.toLowerCase();

        // general fallback to "kr"
        return "kr";
    }

    private static String getCDMACountryIso() {
        try {
            // try to get country code from SystemProperties private class
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class);

            // get homeOperator that contain MCC + MNC
            String homeOperator = ((String) get.invoke(systemProperties,
                    "ro.cdma.home.operator.numeric"));

            // first 3 chars (MCC) from homeOperator represents the country code
            int mcc = Integer.parseInt(homeOperator.substring(0, 3));

            // mapping just countries that actually use CDMA networks
            switch (mcc) {
                case 330: return "PR";
                case 310:
                case 311:
                case 312:
                case 316: return "US";
                case 283: return "AM";
                case 460: return "CN";
                case 455: return "MO";
                case 414: return "MM";
                case 619: return "SL";
                case 450: return "KR";
                case 634: return "SD";
                case 434: return "UZ";
                case 232: return "AT";
                case 204: return "NL";
                case 262: return "DE";
                case 247: return "LV";
                case 255: return "UA";
            }
        } catch (ClassNotFoundException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        } catch (NullPointerException ignored) {
        }

        return null;
    }

    private Runnable mAdvertisingRunnable = new Runnable() {
        @Override
        public void run() {

            AdvertisingIdClient.Info adInfo = null;
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(mContext);
                mIfa = adInfo.getId();
            } catch (IOException e) {
                // Unrecoverable error connecting to Google Play services (e.g.,
                // the old version of the service doesn't support getting AdvertisingId).

            } catch (GooglePlayServicesRepairableException e) {
                // Encountered a recoverable error connecting to Google Play services.

            } catch (GooglePlayServicesNotAvailableException e) {
                // Google Play services is not available entirely.
            }
            final boolean isLAT = adInfo.isLimitAdTrackingEnabled();
        }
    };

    private String getAppVersionFromContext(Context context) {
        try {
            final String packageName = context.getPackageName();
            final PackageInfo packageInfo =
                    context.getPackageManager().getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        } catch (Exception exception) {
            Log.d("TAG","Failed to retrieve PackageInfo#versionName.");
            return "";
        }
    }
}
