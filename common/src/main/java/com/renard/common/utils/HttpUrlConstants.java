package com.renard.common.utils;

/**
 * Created by Riven_rabbit on 2019/1/18
 */
public class HttpUrlConstants {
    public static final String COOKIE_DATA = "cookieData";

    //  请求成功，响应成功(!--接口成功--!)
    public static final String NET_OK = "netOK";
    //cookieData

    //  请求成功，响应失败(!--接口失败--!)
    public static final String NET_ON_FAILURE = "netFaiure";

    //------------!!!------------

    //  没有网络链接(!--客户端没有联网--!)
    public static final String NET_NO_LINKING = "(￣▽￣)~*网络连接错误";

    //  后台服务器错误(!--服务器宕机--!)
    public static final String SERVER_ERROR = "(Ｔ▽Ｔ)服务器错误";
}
