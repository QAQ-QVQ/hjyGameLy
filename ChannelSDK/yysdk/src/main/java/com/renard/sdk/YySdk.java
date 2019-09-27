package com.renard.sdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.quicksdk.QuickSDK;
import com.quicksdk.Sdk;
import com.quicksdk.User;
import com.quicksdk.entity.GameRoleInfo;
import com.quicksdk.entity.OrderInfo;
import com.quicksdk.entity.UserInfo;
import com.quicksdk.notifier.ExitNotifier;
import com.quicksdk.notifier.InitNotifier;
import com.quicksdk.notifier.LoginNotifier;
import com.quicksdk.notifier.LogoutNotifier;
import com.quicksdk.notifier.PayNotifier;
import com.quicksdk.notifier.SwitchAccountNotifier;
import com.renard.common.Interface.CallBackListener;
import com.renard.common.config.TypeConfig;
import com.renard.common.parse.channel.Channel;
import com.renard.common.utils.GsonUtils;
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
import com.tencent.mm.opensdk.utils.Log;

import java.util.HashMap;


/**
 * CREATED BY DY ON 2019/7/22.
 * TIME BY 9:20.
 **/
public class YySdk extends Channel {
    private final String TAG = getClass().getSimpleName();
    private CallBackListener loginCallBackListeners;
    private CallBackListener initCallBackListener;
    private CallBackListener payCallBackListener;
    private CallBackListener switchAccountCallBackLister;
    private GameRoleInfo roleInfo;
    private String json;
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
    public void init(Context context, HashMap<String, Object> initMap, final CallBackListener initCallBackListener) {
// 设置横竖屏，游戏横屏为true，游戏竖屏为false(必接)
        QuickSDK.getInstance().setIsLandScape(true);
        json = GetJsonDataUtil.getJson(context, "Parameter_config.json");
        // 当targetVer大于23时 需要动态申请读写等权限 具体权限 具体而定
        try {
            // check权限
            if ((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                // 没有 ， 申请权限 权限数组
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                // 有 则执行初始化
                // 设置通知，用于监听初始化，登录，注销，支付及退出功能的返回值(必接)
                initQkNotifiers();
                // 请将下面语句中的第二与第三个参数，替换成QuickSDK后台申请的productCode和productKey值，目前的值仅作为示例
                //85381403454468718194677695149668 45108092971169245966158711474129 66899515669147188088314200939195
                //10734868 64054973
                Sdk.getInstance().init((Activity) context, GsonUtils.getValue(json, "productCode"), GsonUtils.getValue(json, "productKey"));
            }
        } catch (Exception e) {
            // 异常 继续申请
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        //585 676
        //636e2ae74563351449cc8ac9450361ef d32b4f01cf53784f94e055a9707403df
        PayManager.getInit(GsonUtils.getValue(json, "config_aid"), GsonUtils.getValue(json, "config_gid"), GsonUtils.getValue(json, "config_key"));
        this.initCallBackListener = initCallBackListener;
    }

    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(context, requestCode, permissions, grantResults);
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            // 没有 ， 申请权限 权限数组
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void login(Context context, HashMap<String, Object> loginMap, CallBackListener loginCallBackListener) {

        if (ConfigUtils.isIssueLogin()) {
            LoginBean loginBean = new LoginBean();
            PayManager.getLogin((Activity) context, loginBean);
        } else {

            com.quicksdk.User.getInstance().login((Activity) context);
        }
        this.loginCallBackListeners = loginCallBackListener;
    }

    @Override
    public void switchAccount(Context context, CallBackListener changeAccountCallBackLister) {
        this.switchAccountCallBackLister = changeAccountCallBackLister;
    }

    @Override
    public void logout(Context context, CallBackListener logoutCallBackLister) {
        com.quicksdk.User.getInstance().logout((Activity) context);
        this.loginCallBackListeners = logoutCallBackLister;
    }

    @Override
    public void pay(final Context context, final HashMap<String, Object> payMap, CallBackListener payCallBackListener) {
        //传递到平台
        CreateOrderBean createOrderBean = new CreateOrderBean();
        createOrderBean.setCpOrderId((String) payMap.get("gorder"));
        createOrderBean.setGoodsId((String) payMap.get("productID"));
        createOrderBean.setGoodsName((String) payMap.get("productName"));
        createOrderBean.setMoney(String.valueOf(payMap.get("money")));
        createOrderBean.setRole((String) payMap.get("roleName"));
        createOrderBean.setServer((String) payMap.get("serverName"));
        createOrderBean.setExt(String.valueOf(payMap.get("extraInfo")));
        if (com.renard.paysdkss.ConfigUtils.isIssuePay()) {
            Intent intent = new Intent(context, PayActivity.class);
            intent.putExtra("creatorder", createOrderBean);
            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, 1001);
        } else {
//            LogUtils.d(TAG,"loginpay");
//            OrderInfo orderInfo = new OrderInfo();
//            orderInfo.setCpOrderID((String) payMap.get("gorder"));// 游戏订单号
//            orderInfo.setGoodsName((String) payMap.get("productName"));// 产品名称
////                    // orderInfo.setGoodsName("月卡");
//            orderInfo.setCount(Integer.parseInt(String.valueOf(payMap.get("buyNum"))));// 购买数量，如购买"10元宝"则传10
////                    // orderInfo.setCount(1);// 购买数量，如购买"月卡"则传1
//            orderInfo.setAmount(Integer.parseInt(String.valueOf(payMap.get("money")))); // 总金额（单位为元）
//            orderInfo.setGoodsID((String) payMap.get("productID")); // 产品ID，用来识别购买的产品
//            orderInfo.setExtrasParams((String) payMap.get("extraInfo")); // 透传参数
//            LogUtils.d(TAG,"login"+orderInfo.getGoodsName()+roleInfo.getGameRoleName());
//            com.quicksdk.Payment.getInstance().pay((Activity) context, orderInfo, roleInfo);

            PayManager.getActivityCreateOrder(createOrderBean, new CreatOrderInterface() {
                @Override
                public void CreatOrder(CreateBackBean createBackBean) {

                }

                @Override
                public void PayOrder() {
                    LogUtils.d(TAG,"loginpaytrue");
                    OrderInfo orderInfo = new OrderInfo();
                    orderInfo.setCpOrderID((String) payMap.get("gorder"));// 游戏订单号
                    orderInfo.setGoodsName((String) payMap.get("productName"));// 产品名称
//                    // orderInfo.setGoodsName("月卡");
                    orderInfo.setCount(Integer.parseInt(String.valueOf(payMap.get("buyNum"))));// 购买数量，如购买"10元宝"则传10
//                    // orderInfo.setCount(1);// 购买数量，如购买"月卡"则传1
                    orderInfo.setAmount(Integer.parseInt(String.valueOf(payMap.get("money")))); // 总金额（单位为元）
                    orderInfo.setGoodsID((String) payMap.get("productID")); // 产品ID，用来识别购买的产品
                    orderInfo.setExtrasParams((String) payMap.get("extraInfo")); // 透传参数
                    LogUtils.d(TAG,"login"+orderInfo.getGoodsName()+roleInfo.getGameRoleName());
                    com.quicksdk.Payment.getInstance().pay((Activity) context, orderInfo, roleInfo);
                }
            });
        }
        this.payCallBackListener = payCallBackListener;
    }

    @Override
    public void exit(Context context, CallBackListener exitCallBackLister) {
        channelNotExitDialog(exitCallBackLister);
    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
        LogUtils.d(TAG,"loginupdate");
        roleInfo = new GameRoleInfo();
        roleInfo.setServerID(String.valueOf(dataMap.get("serverID")));// 服务器ID
        roleInfo.setServerName(String.valueOf(dataMap.get("serverName")));// 服务器名称
        roleInfo.setGameRoleName(String.valueOf(dataMap.get("roleName")));// 角色名称
        roleInfo.setGameRoleID(String.valueOf(dataMap.get("roleID")));// 角色ID
        roleInfo.setGameUserLevel(String.valueOf(dataMap.get("roleLevel")));// 等级
        roleInfo.setVipLevel(String.valueOf(dataMap.get("roleVipLevel"))); // 设置当前用户vip等级，必须为整型字符串
        roleInfo.setRoleCreateTime(" ");
        roleInfo.setGameBalance(" ");// 角色现有金额
        roleInfo.setPartyName(" ");
        if ((Boolean) dataMap.get("create")) {
            User.getInstance().setGameRoleInfo((Activity) context, roleInfo, true);
        } else {
            User.getInstance().setGameRoleInfo((Activity) context, roleInfo, false);
        }

        //传递到平台
        UploadBean uploadBean = new UploadBean();
        uploadBean.setIsCreateRole(String.valueOf(dataMap.get("create")));
        uploadBean.setGameId(GsonUtils.getValue(json, "config_gid"));
        uploadBean.setServerId((String) dataMap.get("serverId"));
        uploadBean.setServerName((String) dataMap.get("serverName"));
        uploadBean.setUserRoleId((String) dataMap.get("roleId"));
        uploadBean.setUserRoleName((String) dataMap.get("roleName"));
        uploadBean.setVipLevel((String) dataMap.get("roleVipLevel"));
        PayManager.getUpload(uploadBean);
    }

    /**
     * 设置通知，用于监听初始化，登录，注销，支付及退出功能的返回值
     */
    private void initQkNotifiers() {
        QuickSDK.getInstance()
                // 1.设置初始化通知(必接)
                .setInitNotifier(new InitNotifier() {

                    @Override
                    public void onSuccess() {
                        //            infoTv.setText("初始化成功");
                        initOnSuccess(initCallBackListener);
                        LogUtils.d(TAG,"loginssinit");
                    }

                    @Override
                    public void onFailed(String message, String trace) {
                        //        infoTv.setText("初始化失败:" + message);
                    }
                })
                // 2.设置登录通知(必接)
                .setLoginNotifier(new LoginNotifier() {

                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        if (userInfo != null) {
                            LogUtils.d(TAG,"loginsslogin");
                            //                infoTv.setText("登陆成功" + "\n\r" + "UserID:  " + userInfo.getUID() + "\n\r" + "UserName:  " + userInfo.getUserName() + "\n\r"
                            //                           + "Token:  " + userInfo.getToken());
                            LoginBean loginBean = new LoginBean();
                            loginBean.setToken(userInfo.getToken());
                            loginBean.setUid(userInfo.getUID());
                            PayManager.getActivityLogin1(loginBean, new LoginInterface() {
                                @Override
                                public void setUID(String uid) {
                                    loginOnSuccess(uid, loginCallBackListeners);
                                }

                                @Override
                                public void Closed(String msg) {
                                    loginOnFail(msg,loginCallBackListeners);
                                }
                            });
                            // 登录成功之后，进入游戏时，需要向渠道提交用户信息
                            //           setUserInfo();
                        }
                    }

                    @Override
                    public void onCancel() {
                        //   infoTv.setText("取消登陆");
                        loginOnCancel("取消登陆", loginCallBackListeners);
                    }

                    @Override
                    public void onFailed(final String message, String trace) {
                        //  infoTv.setText("登陆失败:" + message);
                        loginOnFail(message, loginCallBackListeners);
                        LogUtils.d(TAG,"loginss3");
                    }

                })
                // 3.设置注销通知(必接)
                .setLogoutNotifier(new LogoutNotifier() {

                    @Override
                    public void onSuccess() {
                        //  infoTv.setText("注销成功");
                        logoutOnSuccess(loginCallBackListeners);
                    }

                    @Override
                    public void onFailed(String message, String trace) {
                        //     infoTv.setText("注销失败:" + message);
                        loginOnFail(message, loginCallBackListeners);
                    }
                })
                // 4.设置切换账号通知(必接)
                .setSwitchAccountNotifier(new SwitchAccountNotifier() {

                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        if (userInfo != null) {
                            //      infoTv.setText("切换账号成功" + "\n\r" + "UserID:  " + userInfo.getUID() + "\n\r" + "UserName:  " + userInfo.getUserName() + "\n\r"
                            //                 + "Token:  " + userInfo.getToken());
                        }
                    }

                    @Override
                    public void onFailed(String message, String trace) {
                        //  infoTv.setText("切换账号失败:" + message);
                    }

                    @Override
                    public void onCancel() {
                        //  infoTv.setText("取消切换账号");
                    }
                })
                // 5.设置支付通知(必接)
                .setPayNotifier(new PayNotifier() {

                    @Override
                    public void onSuccess(String sdkOrderID, String cpOrderID, String extrasParams) {
                        //   infoTv.setText("支付成功，sdkOrderID:" + sdkOrderID + ",cpOrderID:" + cpOrderID);
                        payOnSuccess(payCallBackListener);
                        LogUtils.d(TAG,"loginss4");
                    }

                    @Override
                    public void onCancel(String cpOrderID) {
                        //   infoTv.setText("支付取消，cpOrderID:" + cpOrderID);
                        payOnFailure(payCallBackListener);
                    }

                    @Override
                    public void onFailed(String cpOrderID, String message, String trace) {
                        //     infoTv.setText("支付失败:" + "pay failed,cpOrderID:" + cpOrderID + ",message:" + message);
                        payOnFailure(payCallBackListener);
                    }
                })
                // 6.设置退出通知(必接)
                .setExitNotifier(new ExitNotifier() {

                    @Override
                    public void onSuccess() {
                        // 进行游戏本身的退出操作，下面的finish()只是示例
                        //        finish();
                    }

                    @Override
                    public void onFailed(String message, String trace) {
                        //     infoTv.setText("退出失败：" + message);
                    }
                });
    }

    /******************必接sdk生命周期 start***********************/
    @Override
    public void onResume(Context context) {
        super.onResume(context);
        com.quicksdk.Sdk.getInstance().onResume((Activity) context);
    }

    @Override
    public void onCreate(Context context, Bundle savedInstanceState) {
        super.onCreate(context, savedInstanceState);
        com.quicksdk.Sdk.getInstance().onCreate((Activity) context);
    }

    @Override
    public void onPause(Context context) {
        super.onPause(context);
        com.quicksdk.Sdk.getInstance().onPause((Activity) context);
    }

    @Override
    public void onStart(Context context) {
        super.onStart(context);
        com.quicksdk.Sdk.getInstance().onStart((Activity) context);
    }

    @Override
    public void onStop(Context context) {
        super.onStop(context);
        com.quicksdk.Sdk.getInstance().onStop((Activity) context);
    }

    @Override
    public void onDestroy(Context context) {
        super.onDestroy(context);
        com.quicksdk.Sdk.getInstance().onDestroy((Activity) context);
    }


    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(context, requestCode, resultCode, data);
        com.quicksdk.Sdk.getInstance().onActivityResult((Activity) context, requestCode, resultCode, data);
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
        com.quicksdk.Sdk.getInstance().onRestart((Activity) context);
    }

    @Override
    public void onNewIntent(Context context, Intent intent) {
        super.onNewIntent(context, intent);
        com.quicksdk.Sdk.getInstance().onNewIntent(intent);
    }
    /******************必接sdk生命周期 end***********************/
}
