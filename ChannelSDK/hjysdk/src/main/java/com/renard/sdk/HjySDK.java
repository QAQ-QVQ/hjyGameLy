package com.renard.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.renard.common.Interface.CallBackListener;
import com.renard.common.config.TypeConfig;
import com.renard.common.parse.channel.Channel;
import com.renard.common.utils.KLog;
import com.renard.common.utils.log.LogUtils;
import com.renard.hjyGameSs.SDKInitInfo;
import com.renard.hjyGameSs.SDKManager;
import com.renard.hjyGameSs.bean.PayBean;
import com.renard.hjyGameSs.bean.PlayerBean;
import com.renard.hjyGameSs.listener.CallbackListener;
import com.renard.hjyGameSs.utils.ConfigUtils;
import com.renard.hjyGameSs.utils.GetJsonDataUtil;
import com.renard.hjyGameSs.utils.GsonUtils;
import com.renard.hjyGameSs.utils.MD5Util;
import com.renard.paysdkss.PayManager;
import com.renard.paysdkss.bean.CreateBackBean;
import com.renard.paysdkss.bean.CreateOrderBean;
import com.renard.paysdkss.bean.LoginBean;
import com.renard.paysdkss.bean.UploadBean;
import com.renard.paysdkss.listener.CreatOrderInterface;
import com.renard.paysdkss.listener.LoginInterface;
import com.renard.paysdkss.ui.PayActivity;

import java.util.HashMap;

/**
 * Created by Riven_rabbit on 2019/4/25
 *
 * @author Lemon酱
 */
public class HjySDK extends Channel {

    private final String TAG = getClass().getSimpleName();
    public String useruid;
    public String username;
    private CallBackListener loginCallBackListener;
    private CallBackListener payCallBackListener;
    private CallBackListener switchAccountCallBackLister;
    private CallbackListener callbackListener;
    SDKInitInfo sdkInitInfo;


    @Override
    protected void initChannel() {
        LogUtils.d(TAG, getClass().getSimpleName() + " has init");
    }

    @Override
    public String getChannelID() {
        return null;
    }

    @Override
    public boolean isSupport(int FuncType) {
        switch (FuncType) {

            case TypeConfig.FUNC_SWITCHACCOUNT:
                return false;

            case TypeConfig.FUNC_LOGOUT:
                return true;

            case TypeConfig.FUNC_SHOW_FLOATWINDOW:
                return true;

            case TypeConfig.FUNC_DISMISS_FLOATWINDOW:
                return false;

            default:
                return false;
        }
    }

    @Override
    public void init(final Context context, HashMap<String, Object> initMap, CallBackListener
            initCallBackListener) {
        LogUtils.d(TAG, getClass().getSimpleName() + " init");
        final String json = GetJsonDataUtil.getJson(context, "Parameter_config.json");
        //欢聚游SDK初始化
        sdkInitInfo = new SDKInitInfo();
        ConfigUtils.setHjyRequestedOrientation(Boolean.parseBoolean(GsonUtils.getValue(json, "config_bool")));
        callbackListener = new CallbackListener() {
            @Override
            public void onInitSDK(int statusCode, Object obj) {
                KLog.e("init", obj);
//              萌侠q传  5eed02976d663b07a311d6b4033b4909
//                d32b4f01cf53784f94e055a9707403df
                PayManager.getInit(GsonUtils.getValue(json, "config_aid"),
                        GsonUtils.getValue(json, "config_gid"),
                        GsonUtils.getValue(json, "config_key"));
            }

            @Override
            public void onLogin(int statusCode, Object obj, String uid, String name) {
                LogUtils.e("login", obj);
                if (statusCode == 200) {
                    useruid = uid;
                    username = name;
                    LoginBean loginBean = new LoginBean();
                    loginBean.setToken("");
                    loginBean.setUid(uid);
                    PayManager.getActivityLogin1(loginBean, new LoginInterface() {
                        @Override
                        public void setUID(String uid) {
                            loginOnSuccess(uid, loginCallBackListener);
                            LogUtils.i("login", "logintrue" + uid);
                        }

                        @Override
                        public void Closed(String msg) {
                            loginOnFail(msg, loginCallBackListener);
                        }

                    });
                }
            }

            @Override
            public void onUploadInformation(int statusCode, Object obj) {
                KLog.e("upload", obj);
            }

            @Override
            public void onLogout(int statusCode) {
                //切换账号
                LogUtils.i("loginout", "loginout");
            //    SDKManager.logout(context);
//                SDKManager.destroy();//销毁悬浮图标
                logoutOnSuccess(switchAccountCallBackLister);
//                logoutOnSuccess(switchAccountCallBackLister);
            }

            @Override
            public void onRegister(int statusCode, Object obj) {
                KLog.e("register", obj);
            }

            @Override
            public void onPay(int statusCode) {
                //200成功
                if (statusCode == 200) {
                    payOnSuccess(payCallBackListener);
                } else {
                    payOnFailure(payCallBackListener);
                }
            }

            @Override
            public void onExit(int statusCode) {
                //退出
            }
        };
        SDKManager.init((Activity) context, sdkInitInfo, callbackListener);
        initOnSuccess(initCallBackListener);
    }

    @Override
    public void login(Context context, HashMap<String, Object> loginMap, CallBackListener
            loginCallBackListener) {
        LogUtils.i(TAG, getClass().getSimpleName() + " login");
        this.loginCallBackListener = loginCallBackListener;
        SDKManager.showLoginUI(context);
    }

    @Override
    public void switchAccount(Context context, CallBackListener switchAccountCallBackLister) {
        LogUtils.i(TAG, getClass().getSimpleName() + " switchAccount");
        this.switchAccountCallBackLister = switchAccountCallBackLister;
    }

    @Override
    public void logout(Context context, CallBackListener logoutCallBackLister) {
        LogUtils.i(TAG, getClass().getSimpleName() + " logout");
//
        SDKManager.logout(context);
//////
//        SDKManager.destroy();//销毁悬浮图标
//        logoutOnSuccess(logoutCallBackLister);
    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
        String Strsign = "gameId=" + ConfigUtils.getGameId() + "&gameRoleGender=no" + "&gameRoleMoney=1" + "&gameRolePower=" + 5497112 + "&partyId=" + 1 + "&partyName=" + 666 + "&roleCreateTime="
                + dataMap.get("roleCreateTime") + "&serverId=" + dataMap.get("serverId") + "&serverName=" + dataMap.get("serverName") + "&uid=" + useruid + "&userRoleBalance=" + dataMap.get("roleBalance")
                + "&userRoleId=" + dataMap.get("roleId") + "&userRoleLevel=" + dataMap.get("roleLevel") + "&userRoleName=" + dataMap.get("roleName") + "&username=" + username + "&vipLevel=" + dataMap.get("roleVipLevel") + "&key=" + ConfigUtils.getGameKey();
        String sign = MD5Util.getMD5(Strsign);
        PlayerBean player = new PlayerBean(dataMap.get("create").toString(), ConfigUtils.getGameId(), dataMap.get("roleCreateTime").toString(), useruid, username, dataMap.get("serverId").toString(), dataMap.get("serverName").toString(),
                dataMap.get("roleId").toString(), dataMap.get("roleName").toString(), dataMap.get("roleBalance").toString(), dataMap.get("roleVipLevel").toString(), dataMap.get("roleLevel").toString(), dataMap.get("roleBalance").toString(), "1",
                "666", "no", "5497112", sign, "1", "1", "1", "1");
        SDKManager.Upload(player);
        //传递到平台
        UploadBean uploadBean = new UploadBean();
        uploadBean.setIsCreateRole(String.valueOf(dataMap.get("create")));
        uploadBean.setGameId("699");
        uploadBean.setServerId((String) dataMap.get("serverId"));
        uploadBean.setServerName((String) dataMap.get("serverName"));
        uploadBean.setUserRoleId((String) dataMap.get("roleId"));
        uploadBean.setUserRoleName((String) dataMap.get("roleName"));
        uploadBean.setVipLevel((String) dataMap.get("roleVipLevel"));
        PayManager.getUpload(uploadBean);
    }

    @Override
    public void pay(final Context context, final HashMap<String, Object> payMap, CallBackListener payCallBackListener) {
        LogUtils.i(TAG, getClass().getSimpleName() + " pay");
        //过一遍发行
        CreateOrderBean createOrderBean = new CreateOrderBean();
        createOrderBean.setCpOrderId((String) payMap.get("gorder"));
        createOrderBean.setGoodsId((String) payMap.get("productID"));
//        createOrderBean.setGoodsId("1");
        createOrderBean.setGoodsName((String) payMap.get("productName"));
        createOrderBean.setMoney(String.valueOf(Integer.parseInt(String.valueOf(payMap.get("money")))/100));
        createOrderBean.setRole((String) payMap.get("roleName"));
        createOrderBean.setServer((String) payMap.get("serverName"));
        createOrderBean.setExt(String.valueOf(payMap.get("extraInfo")));

        if (com.renard.paysdkss.ConfigUtils.isIssuePay()) {
            LogUtils.i(TAG, "creatorder:");
            Intent intent = new Intent(context, PayActivity.class);
            intent.putExtra("creatorder", createOrderBean);
            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, 1001);
        } else {
            LogUtils.i(TAG, getClass().getSimpleName() + " paytrue");
//            PayBean payBean = new PayBean(ConfigUtils.getGameId(),
//                    (String) payMap.get("serverID"), (String) payMap.get("roleID"),
//                    (String) payMap.get("productID"), (String) payMap.get("productName"),
//                String.valueOf(Integer.parseInt(String.valueOf(payMap.get("money")))/100), (String) payMap.get("gorder"));
//            SDKManager.showPayUI((Activity) context, payBean);
            PayManager.getActivityCreateOrder(createOrderBean, new CreatOrderInterface() {
                @Override
                public void CreatOrder(CreateBackBean createBackBean) {

                }

                @Override
                public void PayOrder() {
                    LogUtils.i(TAG, "paymap:" + payMap.toString());
                    Log.e("configkey",ConfigUtils.getGameId()+"/"+ConfigUtils.getPayKey()+"/"+ConfigUtils.getGameKey()+"/"+String.valueOf(Integer.parseInt(String.valueOf(payMap.get("money")))));
                    PayBean payBean = new PayBean(ConfigUtils.getGameId(),
                            (String) payMap.get("serverID"), (String) payMap.get("roleID"),
                            (String) payMap.get("productID"), (String) payMap.get("productName"),
                            String.valueOf(Integer.parseInt(String.valueOf(payMap.get("money")))/100), (String) payMap.get("gorder"));
                    SDKManager.showPayUI((Activity) context, payBean);
                }
            });
        }
        this.payCallBackListener = payCallBackListener;
    }

    @Override
    public void exit(Context context, CallBackListener exitCallBackLister) {
        LogUtils.i(TAG, getClass().getSimpleName() + " exit");
        channelNotExitDialog(exitCallBackLister);
    }

    @Override
    public void onDestroy(Context context) {
        SDKManager.destroy();//销毁悬浮图标
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(context, requestCode, resultCode, data);
        if (requestCode == 1000) {   //登录1000，300   支付1001，400
            if (resultCode == 300) {
                if (data.getStringExtra("uid") != null) {
                    String uid = data.getStringExtra("uid");
                    loginOnSuccess(uid, loginCallBackListener);
                } else {
                    loginOnFail("登录失败", loginCallBackListener);
                }
            }
        }
        if (requestCode == 1001) {   //登录1000，300   支付1001，400
            if (resultCode == 400) {
                if (data.getIntExtra("code", 201) == 200) {
                    payOnSuccess(payCallBackListener);
                } else {
                    payOnFailure(payCallBackListener);
                }
            }
        }
    }

    @Override
    public void onResume(Context context) {
        super.onResume(context);

    }
}
