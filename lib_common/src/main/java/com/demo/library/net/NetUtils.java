package com.demo.library.net;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class NetUtils {

    private static final String TAG = NetUtils.class.getSimpleName();

    private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");// 4.0模拟器屏蔽掉该权限

    /**
     * 没有网络连接
     */
    public static final int NETWORK_TYPE_DISCONNECT = -1;

    /**
     * mobile连接
     */
    public static final int NETWORK_TYPE_MOBILE = 0;

    /**
     * wifi连接
     */
    public static final int NETWORK_TYPE_WIFI = 1;

    /**
     * 其他网络
     */
    public static final int NETWORK_TYPE_OTHER = 2;


    public static String PROXY_IP;

    public static int PROXY_PORT;

    private NetUtils() {
    }

    /**
     * 判断网络类型
     *
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        checkContext(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return NETWORK_TYPE_DISCONNECT;
        }

        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return NETWORK_TYPE_DISCONNECT;
        }

        if (ni.isConnected() && ni.isAvailable()) {
            if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_TYPE_WIFI;
            } else if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
                return NETWORK_TYPE_MOBILE;
            } else {
                return NETWORK_TYPE_OTHER;
            }
        } else {
            return NETWORK_TYPE_DISCONNECT;
        }
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        return getNetworkType(context) != NETWORK_TYPE_DISCONNECT;
    }

    /**
     * 网络是否可用
     *
     * @param context
     * @param isShowToast 没有网络时是否有toast提示
     * @return
     */
    public static boolean isNetworkAvailable(Context context, boolean isShowToast) {
        boolean result = isNetworkAvailable(context);

        if (!result && isShowToast) {
            Toast.makeText(context, "network not available", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    /**
     * 判断wifi是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiAvailable(Context context) {
        return getNetworkType(context) == NETWORK_TYPE_WIFI;
    }

    /**
     * 判断mobile是否可用
     *
     * @param context
     * @return
     */
    public static boolean isMobileAvailable(Context context) {
        return getNetworkType(context) == NETWORK_TYPE_MOBILE;
    }

    /**
     * Whether is fast mobile network
     *
     * @param context
     * @return
     */
    public static boolean isFastMobileNetwork(Context context) {
        checkContext(context);

        boolean result = false;

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return result;
        }

        switch (tm.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN: // 2G
                result = false;
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP: // 3G
                result = true;
                break;
            case TelephonyManager.NETWORK_TYPE_LTE: // 4G
                result = true;
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                result = false;
                break;
            default:
                result = false;
                break;
        }

        return result;
    }

    /**
     * 获取网络类型：2G、3G、4G、UNKNOWN
     *
     * @param context
     * @return
     */
    public static String getNetworkTypeString(Context context) {
        checkContext(context);

        String type = "UNKNOWN";

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected() && ni.isAvailable()) {
            switch (ni.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            type = "2G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            type = "3G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            type = "4G";
                            break;
                        default:
                            break;
                    }

                    break;
                case ConnectivityManager.TYPE_WIFI:
                    type = "WiFi";
                    break;
                default:
                    break;
            }
        }

        return type;
    }

    /**
     * 获取手机IMSI
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        checkContext(context);

        String imsi = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                imsi = tm.getSubscriberId();

                if (TextUtils.isEmpty(imsi)) {
                    imsi = "";
                }
            }
        } catch (Throwable e) {
            Log.e(TAG, "error:", e);

            imsi = "";
        }

        return imsi;
    }

    /**
     * 获取SIM卡运营商的名称
     *
     * @param context
     * @return
     */
    public static String getSIMProviderName(Context context) {
        String imsi = getIMSI(context);

        String name = "";
        if (!TextUtils.isEmpty(imsi)) {
            if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
                name = "中国移动";
            } else if (imsi.startsWith("46001")) {
                name = "中国联通";
            } else if (imsi.startsWith("46003")) {
                name = "中国电信";
            }
        }

        return name;
    }

    /**
     * 获取代理的ip 和 代理端口
     *
     * @param context
     */
    public static void readAPN(Context context) {
        checkContext(context);

        Cursor cursor = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            PROXY_IP = cursor.getString(cursor.getColumnIndex("proxy"));
            PROXY_PORT = cursor.getInt(cursor.getColumnIndex("port"));
        }

        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    /**
     * 检测context是否为空
     *
     * @param context
     */
    private static void checkContext(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
    }

}
