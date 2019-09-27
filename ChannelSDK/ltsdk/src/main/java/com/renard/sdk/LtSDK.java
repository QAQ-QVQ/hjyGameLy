package com.renard.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ju6game.app.Ju6Manager;
import com.ju6game.app.Listener.BaseUiListener;
import com.ju6game.app.Listener.CallbackListener;
import com.ju6game.app.OrderInfo;
import com.ju6game.app.ui.MyToast;
import com.ju6game.app.utils.ConfigUtils;
import com.ju6game.app.utils.StoreUtils;
import com.renard.common.Interface.CallBackListener;
import com.renard.common.config.TypeConfig;
import com.renard.common.parse.channel.Channel;
import com.renard.common.utils.GsonUtils;
import com.renard.common.utils.log.LogUtils;
import com.renard.paysdkss.PayManager;
import com.renard.paysdkss.bean.CreateBackBean;
import com.renard.paysdkss.bean.CreateOrderBean;
import com.renard.paysdkss.bean.LoginBean;
import com.renard.paysdkss.bean.UploadBean;
import com.renard.paysdkss.listener.CreatOrderInterface;
import com.renard.paysdkss.listener.LoginInterface;
import com.renard.paysdkss.ui.PayActivity;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * CREATED BY DY ON 2019/8/13.
 * TIME BY 13:41.
 **/
public class LtSDK extends Channel {
    private final String TAG = getClass().getSimpleName();
    CallbackListener mListener;
    ConfigUtils initInfo;
    private CallBackListener loginCallBackListeners;
    private CallBackListener payCallBackListener;
    private CallBackListener switchAccountCallBackLister;


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
       // View.inflate(context,R.layout.loginsdk_dialog_loading,null);
        mListener=new CallbackListener() {
            @Override
            public void onInitSDK(int statusCode, Object obj) {
                if(obj!=null)
                 //   tv_content.setText("初始化成功" + obj.toString());
                    initOnSuccess(initCallBackListener);
                if(statusCode==0){
                    //去获取token，如果为空表示客户端为未登录状态
                    if(Ju6Manager.getToken(context)==null){
                        Log.i("CallbackListener", "logout");
                        Ju6Manager.logout(context);
                    }else{
                        //客户端可以拿到token，去获取uid
                        Log.i("CallbackListener", "getUid");
                        Ju6Manager.getUid(context);
                    }
                }
            }

            @Override
            public void onLogin(int statusCode, Object obj) {
                if(statusCode==0) {
                    Ju6Manager.getUid(context);//登陆后去获取uid
                }else {
                    MyToast.show(context,"登录失败");
                    loginOnFail(obj.toString(),loginCallBackListeners);
                }
            //    tv_content.setText("登陆成功");

            }

            @Override
            public void onCreateRole(int statusCode, Object obj) {
                if(statusCode == 0) {
            //        tv_content.setText("创建角色成功"+obj.toString());
                }
            }

            @Override
            public void onLogout(int stausCode) {
                if(stausCode==0) {
                    //          tv_content.setText("注销成功");
//                    Ju6Manager.removeFloatView();
                    logoutOnSuccess(loginCallBackListeners);
                }
            }

            @Override
            public void onGetUid(int stausCode, Object obj) {
                if(obj==null){
                    //获取不到uid，需要重新登陆
                    Ju6Manager.logout(context);
                }else {
                    //获取到了uid,游戏端就可以拿去校验，如果游戏端服务器校验失败，再调用登陆界面即可
                    final String token= Ju6Manager.getToken(context);
                    String uid=(String)obj;
                    String modelid = ConfigUtils.getUniqueID();
                    String model = Build.MODEL;

                    LoginBean loginBean=new LoginBean();
                    loginBean.setToken(token);
                    loginBean.setUid(uid);
                    PayManager.getActivityLogin1(loginBean, new LoginInterface() {
                        @Override
                        public void setUID(String uid) {
                            loginOnSuccess(uid,loginCallBackListeners);
//                            Ju6Manager.showFloatView(context,  0,  0);
                        }
                        @Override
                        public void Closed(String msg) {
                            loginOnFail(msg,loginCallBackListeners);
                        }
                    });
                }
            }

            @Override
            public void onRegister(int stausCode, Object obj) {
       //         tv_content.setText("注册"+obj.toString());
            }

            @Override
            public void onPay(int statusCode) {
                if(statusCode == 0){
                    payOnSuccess(payCallBackListener);
                }else if (statusCode == 1){
                    payOnFailure(payCallBackListener);
                }
            }

            @Override
            public void onExit(int statusCode) {
                if(statusCode == 0){
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        };

        //初始化
        initInfo=new ConfigUtils();
        initInfo.setAppId("nThsbTJscYF7aHTD");
        initInfo.setAppKey("WzSXGDmExrbFBJW2zkFAQQ7ZfE6mz4yR");
        initInfo.setServerUrl("https://v1.j6yx.com/");
        initInfo.setQqAppId("101549949");
        initInfo.setWxAppId("wx7d62759edfecb1c3");
        initInfo.setWxSecret("661f3a533e76ef312d9e1471f1e1c047");
        initInfo.setPromoteId("0");
        Ju6Manager.init(context,mListener);
        Ju6Manager.setDebugMode(true);
        //初始化发行SDK验证判断
        PayManager.getInit("86","720","fff342f1b7e649c70d98487f8bd83592");
    }

    @Override
    public void login(Context context, HashMap<String, Object> loginMap, CallBackListener loginCallBackListener) {
        LogUtils.d(TAG, getClass().getSimpleName() + " login");
        if (com.renard.paysdkss.ConfigUtils.isIssueLogin()){
            LoginBean loginBean=new LoginBean();
            PayManager.getLogin((Activity)context,loginBean);
            LogUtils.d(TAG, getClass().getSimpleName() + " login1");
        }else {
            LogUtils.d(TAG, getClass().getSimpleName() + " login2");
            Ju6Manager.logout(context);
        }
        this.loginCallBackListeners = loginCallBackListener;
    }

    @Override
    public void switchAccount(Context context, CallBackListener changeAccountCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " switchAccount");
        this.switchAccountCallBackLister = changeAccountCallBackLister;
    }

    @Override
    public void logout(Context context, CallBackListener logoutCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " logout");
        StoreUtils.clearUserInfo(context);
        Ju6Manager.getCallBackListener().onLogout(0);
        this.loginCallBackListeners = logoutCallBackLister;
    }

    @Override
    public void pay(final Context context, final HashMap<String, Object> payMap, CallBackListener payCallBackListener) {
        LogUtils.d(TAG, getClass().getSimpleName() + " pay");
        //传递到平台
        CreateOrderBean createOrderBean=new CreateOrderBean();
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
            PayManager.getActivityCreateOrder(createOrderBean, new CreatOrderInterface() {
                @Override
                public void CreatOrder(CreateBackBean createBackBean) {

                }

                @Override
                public void PayOrder() {
                    LogUtils.d(TAG, getClass().getSimpleName() + " paytrue");
//                    OrderInfo order = new OrderInfo();
//                    order.setProductName((String) payMap.get("productName"));// 物品名称
//                    order.setProductDesc((String) payMap.get("productDesc")); // 物品描述
//                    order.setAmount(Integer.parseInt(String.valueOf(payMap.get("money")))*100);//商品价格（单位分）
//                    order.setExtendInfo((String) payMap.get("gorder")); // 用于确认交易给玩家发送商品
//                    order.setGameServerId((String) payMap.get("serverID"));//游戏服务器ID
//                    order.setGameServerName((String) payMap.get("serverName"));//游戏服务器名称
//                    order.setGameUserId((String) payMap.get("roleID"));//角色ID
//                    order.setGameUserName((String) payMap.get("roleName"));//角色名称
//                    MCApiFactory.getMCApi().pay(context, order, payCallback);
                   // String no = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date());//暂时用时间生成
                    OrderInfo orderInfo = new OrderInfo();
                    orderInfo.put("money", String.valueOf(payMap.get("money")));
                    orderInfo.put("product_id", String.valueOf(payMap.get("productID")));
                    orderInfo.put("cporder", String.valueOf(payMap.get("gorder")));
                    orderInfo.put("server_id", String.valueOf(payMap.get("serverID")));
                    orderInfo.put("server_name", String.valueOf(payMap.get("serverName")));
                    orderInfo.put("role", String.valueOf(payMap.get("roleID")));
                    orderInfo.put("role_level", String.valueOf(payMap.get("roleLevel")));
                    orderInfo.put("fee_type", "CNY");
                    orderInfo.put("ext", String.valueOf(payMap.get("extraInfo")));
                    Ju6Manager.showPayUI(context, orderInfo);
                }
            });
        }
        this.payCallBackListener=payCallBackListener;
    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
        String roleID,roleName;
        if ((Boolean)dataMap.get("create")){
             roleID = String.valueOf(dataMap.get("roleId"));
             roleName = String.valueOf(dataMap.get("roleName"));
        }else {
//            UserGameInfo userGameInfo = new UserGameInfo();
//            userGameInfo.setGameServerId((String) dataMap.get("serverId"));//游戏服务器ID
//            userGameInfo.setGameServerName((String) dataMap.get("serverName"));//游戏服务器名称
//            userGameInfo.setGameUserId((String) dataMap.get("roleId"));//角色ID
//            userGameInfo.setGameUserName((String) dataMap.get("roleName"));//角色名称
//            userGameInfo.setGameUserLevel((String) dataMap.get("roleLevel"));//角色等级
//            userGameInfo.setGameUserVipLevel((String) dataMap.get("roleVipLevel"));//角色VIP等级
//            userGameInfo.setGameUserGold((String) dataMap.get("roleBalance"));//角色剩余金币
//            userGameInfo.setGameUserDiamond((String) dataMap.get("roleBalance"));//角色剩余钻石
//            userGameInfo.setGameUserProfession("无职业");//角色职业
//            MCApiFactory.getMCApi().uploadGameRole(userGameInfo);
            roleID = String.valueOf(dataMap.get("roleId"));
            roleName = String.valueOf(dataMap.get("roleName"));
        }
        Ju6Manager.createRole(context, roleID, roleName);

        //传递到平台
        UploadBean uploadBean=new UploadBean();
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
    public void exit(Context context, CallBackListener exitCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " exit");
        channelNotExitDialog(exitCallBackLister);
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
        }
        if (requestCode == 1000) {   //登录1000，300   支付1001，400
            if (resultCode==300){
                if (data.getStringExtra("uid")!=null){
                    String uid=data.getStringExtra("uid");
                    loginOnSuccess(uid,loginCallBackListeners);
                }else {
                    loginOnFail("登录失败",loginCallBackListeners);
                }
            }
        }
        if (requestCode == 1001) {   //登录1000，300   支付1001，400
            if (resultCode==400){
                if (data.getIntExtra("code",201)==200){
                    payOnSuccess(payCallBackListener);
                }else {
                    payOnFailure(payCallBackListener);
                }
            }
        }
    }

    @Override
    public void onDestroy(Context context) {
        super.onDestroy(context);
        Ju6Manager.destroy();
    }
}
