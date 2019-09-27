package com.renard.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.Toast;

import com.renard.common.Interface.CallBackListener;
import com.renard.common.config.TypeConfig;
import com.renard.common.parse.channel.Channel;
import com.renard.common.utils.log.LogUtils;
import com.renard.paysdkss.ConfigUtils;
import com.renard.paysdkss.PayManager;
import com.renard.paysdkss.bean.CreateBackBean;
import com.renard.paysdkss.bean.CreateOrderBean;
import com.renard.paysdkss.bean.LoginBean;
import com.renard.paysdkss.bean.UploadBean;
import com.renard.paysdkss.listener.CreatOrderInterface;
import com.renard.paysdkss.listener.LoginInterface;
import com.renard.paysdkss.ui.PayActivity;
import com.xiyuan.sdk.impl.MainActivity;
import com.xxx.sdk.XConfig;
import com.xxx.sdk.XPlatform;
import com.xxx.sdk.data.GameUserData;

import com.xxx.sdk.data.PayOrder;
import com.xxx.sdk.listener.ILogoutListener;
import com.xxx.sdk.listener.ISDKExitListener;
import com.xxx.sdk.listener.ISDKListener;
import com.xxx.sdk.listener.ISDKLoginListener;
import com.xxx.sdk.listener.ISDKPayListener;
import com.xxx.sdk.service.SdkManager;


import java.util.HashMap;

public class SykjSDK extends Channel {
    private final String TAG = getClass().getSimpleName();
    private CallBackListener loginCallBackListeners;
    private CallBackListener payCallBackListener;
    private CallBackListener switchAccountCallBackLister;
    private static String uids;

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
                return true;

            case TypeConfig.FUNC_LOGOUT:
                return true;

            case TypeConfig.FUNC_SHOW_FLOATWINDOW:
                return false;

            case TypeConfig.FUNC_DISMISS_FLOATWINDOW:
                return false;

            default:
                return false;
        }
    }

    @Override
    public void init(final Context context, HashMap<String, Object> initMap, final CallBackListener initCallBackListener) {
        LogUtils.d(TAG, getClass().getSimpleName() + " init");
        String appId = "16", appKey = "CMK0WjDSEDYa";
        int screenOrientation = XConfig.ORIENTATION_LANDSCAPE;
        XConfig cfg = new XConfig(appId, appKey, screenOrientation);
        XPlatform.getInstance().init((Activity) context, cfg, new ISDKListener() {
            @Override
            public void onFailed(int code) {
                Log.e("init", "init sdk fail.   error code: " + code);
            }

            @Override
            public void onSuccess() {
                Log.d("init", "init sdk suc");
                //初始化成功
                initOnSuccess(initCallBackListener);
            }
        }, new ILogoutListener() {

            @Override
            public void onLogout() {
                Log.d("init", "logout success");
                logoutOnSuccess(loginCallBackListeners);
            }
        });
        PayManager.getInit("314", "729", "e9d0933bc19b9a28229320ab01eb40e7");
    }

    @Override
    public void login(final Context context, HashMap<String, Object> loginMap, CallBackListener loginCallBackListener) {
        LogUtils.d(TAG, getClass().getSimpleName() + " login");
        if (ConfigUtils.isIssueLogin()) {
            LoginBean loginBean = new LoginBean();
            PayManager.getLogin((Activity) context, loginBean);
        } else {
            XPlatform.getInstance().login(new ISDKLoginListener() {
                @Override
                public void onFailed(int code, String msg) {
                    LogUtils.e("SDK", "login fail. error code: " + code);
//                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String id, String accessToken, String time) {
                    LogUtils.d("SDK", "login suc:id=" + id + ";token=" + accessToken+"/"+time);
//                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                    PayManager.getActivityLogin1(id,time,accessToken, new LoginInterface() {
                    PayManager.getActivityLogin1(time,accessToken,id, new LoginInterface() {
                        @Override
                        public void setUID(String uid) {
                            uids = uid;
                            Log.e(TAG, "uid:" + uid.toString());
                            loginOnSuccess(uid, loginCallBackListeners);
                        }

                        @Override
                        public void Closed(String msg) {
                            loginOnFail(msg, loginCallBackListeners);
                        }
                    });
                }
            });

        }
        this.loginCallBackListeners = loginCallBackListener;
    }

    @Override
    public void switchAccount(final Context context, CallBackListener changeAccountCallBackLister) {
        this.switchAccountCallBackLister = changeAccountCallBackLister;
    }

    @Override
    public void logout(final Context context, final CallBackListener logoutCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " logout");
        XPlatform.getInstance().logout();
    }

    @Override
    public void pay(final Context context, final HashMap<String, Object> payMap, final CallBackListener payCallBackListener) {
        LogUtils.d(TAG, getClass().getSimpleName() + " pay");
        //传递到平台
        CreateOrderBean createOrderBean = new CreateOrderBean();
        createOrderBean.setCpOrderId((String) payMap.get("gorder"));
        createOrderBean.setGoodsId("1");
        createOrderBean.setGoodsName((String) payMap.get("productName"));
        createOrderBean.setMoney(String.valueOf(Integer.parseInt(String.valueOf(payMap.get("money")))/100));
        createOrderBean.setRole((String) payMap.get("roleName"));
        createOrderBean.setServer((String) payMap.get("serverName"));
        createOrderBean.setExt(String.valueOf(payMap.get("extraInfo")));
        //  PayManager.getCreateOrder((Activity)context,createOrderBean);
        if (ConfigUtils.isIssuePay()) {
            Intent intent = new Intent(context, PayActivity.class);
            intent.putExtra("creatorder", createOrderBean);
            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, 1001);
        } else {
            LogUtils.d(TAG, getClass().getSimpleName() + " payCreate");
            PayManager.getActivityCreateOrder(createOrderBean, new CreatOrderInterface() {
                @Override
                public void CreatOrder(CreateBackBean createBackBean) {

                }

                @Override
                public void PayOrder() {
                    LogUtils.d(TAG, getClass().getSimpleName() + " paytrue");
                    PayOrder data = new PayOrder();
                    data.setPrice(Integer.parseInt(String.valueOf(payMap.get("money"))));
                    data.setPayNotifyUrl("http://issue.hjygame.com/sdk/gamepay/aid/314/gid/729/");
                    data.setProductID("1");
                    data.setProductName((String) payMap.get("productName"));
                    data.setProductDesc((String) payMap.get("productDesc"));
                    data.setRoleID((String) payMap.get("roleID"));
                    data.setRoleLevel((String) payMap.get("roleLevel"));
                    data.setServerID((String) payMap.get("serverID"));
                    data.setRoleName((String) payMap.get("roleName"));
                    data.setServerName((String) payMap.get("serverName"));
                    data.setVip((String) payMap.get("roleVipLevel"));
                    data.setExtra((String) payMap.get("gorder"));
                    XPlatform.getInstance().pay((Activity) context, data, new ISDKPayListener() {
                        @Override
                        public void onSuccess(String s) {
                            // 支付成功
                            payOnSuccess(payCallBackListener);
                        }

                        @Override
                        public void onFailed(int i) {
                            // 支付失败
                            payOnFailure(payCallBackListener);
                        }
                    });
//                    NSPayInfo info = new NSPayInfo();
//                    info.gameName = "攻城三国";//游戏名称
//                    info.productId = (String) payMap.get("productID");//商品ID
//                    info.productName = (String) payMap.get("productName");//商品名称
//                    info.productDesc = (String) payMap.get("productDesc");//商品描述
//                    info.price = Integer.parseInt(String.valueOf(payMap.get("money")))*100 ;// 金额（单位为分）
//                    info.coinNum = Integer.parseInt(payMap.get("coinNum").toString());
//                    info.serverId = Integer.parseInt((String) payMap.get("serverID"));//服务器id
//                    info.serverName = (String) payMap.get("serverName");//服务器名称
//                    info.uid = uids;
//                    info.roleId = (String) payMap.get("roleID");//游戏的角色id
//                    info.roleName = (String) payMap.get("roleName");
//                    info.roleLevel = Integer.parseInt((String) payMap.get("roleLevel"));//角色等级
//                    info.giftId = " ";//选填
//                    info.privateField = (String) payMap.get("gorder");//选填 订单透传字段
//                    info.ratio = 10;
//                    info.buyNum = Integer.parseInt(payMap.get("buyNum").toString());
                }
            });
        }
        this.payCallBackListener = payCallBackListener;

    }

    @Override
    public void exit(final Context context, CallBackListener exitCallBackLister) {
//        channelNotExitDialog(exitCallBackLister);
        XPlatform.getInstance().exit((Activity) context, new ISDKExitListener() {
            @Override
            public void onCancel() {
                //用户取消， 继续游戏，不用处理
                Log.d("SDK", "cancel exit");
            }

            @Override
            public void onExit() {
                //用户点击确认退出游戏， 直接杀掉进程即可
                ((Activity) context).finish();
                System.exit(0);
            }
        });
    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
//        NSRoleInfo roleinfo = new NSRoleInfo();
//        roleinfo.roleId = (String) dataMap.get("roleId");//角色ID
//        roleinfo.uid = uids;//用户ID
//        roleinfo.roleName = (String) dataMap.get("roleName");//角色名称
//        roleinfo.roleCtime=(String) dataMap.get("roleCreateTime");//角色创建时间（单位：秒，10位数）（必填）
//        roleinfo.roleLevel = (String) dataMap.get(" roleLevel ");//角色等级
//        roleinfo.roleLevelMtime= "";//角色等级变化时间（单位：秒，10位数）
//        roleinfo.zoneId = (String) dataMap.get("serverId");//区ID
//        roleinfo.zoneName = (String) dataMap.get("serverName");//区名称
//        roleinfo.dataType = "2";//数据类型：1：进入游戏；2：创建角色；3：角色升级；4：游戏退出
//        roleinfo.ext = "";
//        if ((Boolean)dataMap.get("create")) {
//            NSdk.getInstance().submitGameInfo((Activity) context, roleinfo);
//        }else {
//            NSdk.getInstance().submitGameInfo((Activity) context, roleinfo);
//        }
        GameUserData data = new GameUserData();
//        创建角色时，opType为1；
//        进入游戏时，opType为2；
//        等级提升时，opType为3；
//        退出游戏时，opType为4
        if ((Boolean) dataMap.get("create")) {
            data.setOpType(GameUserData.OP_CREATE_ROLE);
        } else {
            data.setOpType(GameUserData.OP_ENTER_GAME);
        }
        data.setRoleID((String) dataMap.get("roleId"));
        data.setRoleName((String) dataMap.get("roleName"));
        data.setServerID((String) dataMap.get("serverId"));
        data.setServerName((String) dataMap.get("serverName"));
        data.setRoleLevel((String) dataMap.get(" roleLevel "));
        data.setVip((String) dataMap.get(" roleVipLevel "));

        XPlatform.getInstance().submitGameData((Activity) context, data, new ISDKListener() {

            @Override
            public void onSuccess() {
                Log.d("login", "submit game data success");
            }

            @Override
            public void onFailed(int code) {
                Log.d("login", "submit game data fail:" + code);
            }
        });

        //传递到平台
        UploadBean uploadBean = new UploadBean();
        uploadBean.setIsCreateRole(String.valueOf(dataMap.get("create")));
        uploadBean.setGameId("585");
        uploadBean.setServerId((String) dataMap.get("serverId"));
        uploadBean.setServerName((String) dataMap.get("serverName"));
        uploadBean.setUserRoleId((String) dataMap.get("roleId"));
        uploadBean.setUserRoleName((String) dataMap.get("roleName"));
        uploadBean.setVipLevel((String) dataMap.get("roleVipLevel"));
        PayManager.getUpload(uploadBean);

    }

    /******************必接sdk生命周期 start***********************/
    @Override
    public void onResume(Context context) {
        super.onResume(context);
        XPlatform.getInstance().showFloatWindow(); //显示悬浮窗
    }

    @Override
    public void onPause(Context context) {
        super.onPause(context);
        XPlatform.getInstance().hideFloatWindow(); //隐藏悬浮窗
    }

    @Override
    public void onStart(Context context) {
        // TODO Auto-generated method stub
        super.onStart(context);
//        NSdk.getInstance().onStart((Activity) context);
    }

    @Override
    public void onStop(Context context) {
        super.onStop(context);
//        NSdk.getInstance().onStop((Activity) context);
    }

    @Override
    public void onDestroy(Context context) {
        super.onDestroy(context);
        XPlatform.getInstance().destroy();
    }

    @Override
    public void onConfigurationChanged(Context context, Configuration newConfig) {
        super.onConfigurationChanged(context, newConfig);
//        NSdk.getInstance().onConfigurationChanged((Activity) context, newConfig);
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(context, requestCode, resultCode, data);
//        NSdk.getInstance().onActivityResult((Activity) context, requestCode, resultCode, data);
        if (requestCode == 1000) {   //登录1000，300   支付1001，400
            if (resultCode == 300) {
                if (data.getStringExtra("uid") != null) {
                    String uid = data.getStringExtra("uid");
                    loginOnSuccess(uid, loginCallBackListeners);
                } else {
                    loginOnFail("登录失败", loginCallBackListeners);
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
    public void onRestart(Context context) {
        super.onRestart(context);
//        NSdk.getInstance().onRestart((Activity) context);
    }

    @Override
    public void onNewIntent(Context context, Intent intent) {
        super.onNewIntent(context, intent);
//        NSdk.getInstance().onNewIntent((Activity) context, intent);
    }
    /******************必接sdk生命周期 end***********************/
    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(context, requestCode, permissions, grantResults);
        XPlatform.getInstance().onRequestPermissionsResult(SdkManager.getInstance().getActivity(),requestCode,permissions,grantResults);
    }
}
