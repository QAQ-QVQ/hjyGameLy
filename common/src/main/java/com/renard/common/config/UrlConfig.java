package com.renard.common.config;

import android.text.TextUtils;

import com.renard.common.cache.BaseCache;
import com.renard.common.utils.log.LogUtils;

/**
 * Created by Riven_rabbit on 2019/4/26
 *
 * @author Lemon酱
 */
public class UrlConfig {
    private static final String TAG = "UrlConfig";

    private static String Project_SDKUrl; //url

    //项目的基础ip域名地址
    private static String Project_BaseApi = "https://www.baidu.com/";


    public static String getSdkUrl(){
        return Project_SDKUrl;
    }

    public static void initUrl(){

        //通过配置读取域名
        String SDK_Base_Url = BaseCache.getInstance().getSdkUrl();

        if (!TextUtils.isEmpty(SDK_Base_Url)){//通过配置文件来修改域名
            Project_BaseApi = SDK_Base_Url;
        }

        LogUtils.debug_d(TAG,Project_SDKUrl);
    }

    /**
     *  预设设置修改当前网络的请求域名接口
     *  思考特殊的场景：
     *
     */
    public static String getReSetUrl(String sdk_type, String channelId){

        String tempUrl = "";
        return tempUrl; //返回临时的域名
    }
}
