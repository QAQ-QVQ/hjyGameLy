package com.renard.common.parse.channel;

import com.renard.common.Interface.CallBackListener;
import com.renard.common.config.TypeConfig;
import com.renard.common.utils.ErrorCode;

import java.util.HashMap;

/**
 * Created by Riven_rabbit on 2019/4/24
 *
 * @author Lemon酱
 */
public class ChannelListenerImpl {

    private HashMap<String,Object> loginAuthData;

    /**
     * 初始化成功
     */
    public void initOnSuccess(CallBackListener callBackListener){
        if (callBackListener != null){
            callBackListener.onSuccess(null);
        }
    }


    /**
     * 登录成功
     */
    public void loginOnSuccess(String oathUrl,CallBackListener callBackListener){
        if (loginAuthData == null){
            loginAuthData = new HashMap<>();
        }
        loginAuthData.put(Channel.PARAMS_OAUTH_URL,oathUrl); //授权的参数信息
        loginAuthData.put(Channel.PARAMS_OAUTH_TYPE, TypeConfig.LOGIN); //授权的事件类型
        if (callBackListener != null){
            callBackListener.onSuccess(loginAuthData);
        }
    }
    /**
     * 登录成功
     */
    public void loginOnSuccess(String oathUrl,String token,CallBackListener callBackListener){
        if (loginAuthData == null){
            loginAuthData = new HashMap<>();
        }
        loginAuthData.put(Channel.PARAMS_OAUTH_URL,oathUrl); //授权的参数信息
        loginAuthData.put(Channel.PARAMS_OAUTH_TYPE, TypeConfig.LOGIN); //授权的事件类型
        loginAuthData.put(Channel.PARAMS_OAUTH_TOKEN,token);//token
        if (callBackListener != null){
            callBackListener.onSuccess(loginAuthData);
        }
    }

    /**
     * 登录失败
     */
    public void loginOnFail(String errorMessage, CallBackListener callBackListener){
        if (callBackListener != null){
            callBackListener.onFailure(ErrorCode.CHANNEL_LOGIN_FAIL,errorMessage);
        }
    }

    /**
     * 登录取消
     */
    public void loginOnCancel(String errorMessage, CallBackListener callBackListener){
        if (callBackListener != null){
            callBackListener.onFailure(ErrorCode.CHANNEL_LOGIN_CANCEL,errorMessage);
        }
    }


    /**
     * 切换账号成功
     */
    public void switchAccountOnSuccess(CallBackListener callBackListener){
        if (loginAuthData == null){
            loginAuthData = new HashMap<>();
        }
        loginAuthData.put(Channel.PARAMS_OAUTH_TYPE, TypeConfig.SWITCHACCOUNT); //授权的事件类型
        if (callBackListener != null){
            callBackListener.onSuccess(loginAuthData);
        }
    }

    /**
     * 切换账号成功
     */
    public void switchAccountOnFail(String errorMessage, CallBackListener callBackListener){
        if (callBackListener != null){
            callBackListener.onFailure(ErrorCode.CHANNEL_SWITCH_ACCOUNT_FAIL,errorMessage);
        }
    }


    /**
     * 切换账号成功
     */
    public void switchAccountOnCancel(String errorMessage, CallBackListener callBackListener){
        if (callBackListener != null){
            callBackListener.onFailure(ErrorCode.CHANNEL_SWITCH_ACCOUNT_CANCEL,errorMessage);
        }
    }

    /**
     * 注销成功
     */
    public void logoutOnSuccess(CallBackListener callBackListener){
        if (loginAuthData == null){
            loginAuthData = new HashMap<>();
        }
        loginAuthData.put(Channel.PARAMS_OAUTH_TYPE, TypeConfig.LOGOUT); //授权的事件类型
        if (callBackListener != null){
            callBackListener.onSuccess(loginAuthData);
        }
    }

    /**
     * 注销失败
     */
    public void logoutOnFail(String errorMessage, CallBackListener callBackListener){
        if (callBackListener != null){
            callBackListener.onFailure(ErrorCode.CHANNEL_LOGOUT_FAIL,errorMessage);
        }
    }

    /**
     * 注销取消
     */
    public void logoutOnCancel(String errorMessage, CallBackListener callBackListener){
        if (callBackListener != null){
            callBackListener.onFailure(ErrorCode.CHANNEL_LOGOUT_CANCEL,errorMessage);
        }
    }


    /**
     * 支付成功
     * @param callBackListener
     */
    public void payOnSuccess(CallBackListener callBackListener){

        if (callBackListener != null){
            callBackListener.onSuccess(null);
        }
    }
    /**
     * 支付失败
     * @param callBackListener
     */
    public void payOnFailure(CallBackListener callBackListener){

        if (callBackListener != null){
            callBackListener.onFailure(201,"支付失败");
        }
    }
    /**
     * 当渠道不能正确返回支付结果时，返回该字段
     * @param callBackListener
     */
    public void payOnComplete(CallBackListener callBackListener){
        if (callBackListener != null){
            callBackListener.onFailure(ErrorCode.NO_PAY_RESULT,"pay complete");
        }
    }

    /**
     * 统一渠道不存在退出框
     */
    public void channelNotExitDialog(CallBackListener callBackListener){
        if (callBackListener != null){
            callBackListener.onFailure(ErrorCode.NO_EXIT_DIALOG,"channel not exitDialog");
        }
    }


    /**
     * 统一失败回调
     */
    public void OnFailure(String errorMessage, CallBackListener callBackListener){
        if (callBackListener != null){
            callBackListener.onFailure(ErrorCode.FAILURE,errorMessage);
        }
    }

    /**
     * 统一取消回调
     */
    public void  OnCancel(CallBackListener callBackListener){
        if (callBackListener != null){
            callBackListener.onFailure(ErrorCode.CANCEL,"cancel");
        }
    }
}
