package com.demo.library.net.okhttp;

public class HttpConstants {

    public final static String USER_AGENT = "OkHttp-Demo";

    /** 默认错误信息 */
    public final static String DEFAULT_ERROR = "error";


    /** 请求失败 : 默认 code : 1000 */
    public final static int FAILURE_CODE_DEFAULT = 1000;

    /** 请求失败 : 无网络 code : 1001 */
    public final static int FAILURE_CODE_NETWORK_NO = 1001;

    /** 请求失败 : 数据格式错误 code : 1002 */
    public final static int FAILURE_CODE_PARSE_ERR = 1002;


    /** 默认连接超时时间:30s */
    public final static int DEFAULT_CONNECT_TIME_OUT = 30;

    /** 默认读取超时时间:30s */
    public final static int DEFAULT_READ_TIME_OUT = 30;

    /** 默认写超时时间:30s */
    public final static int DEFAULT_WRITE_TIME_OUT = 30;


}
