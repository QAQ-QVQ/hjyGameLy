package com.renard.sdk;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.mchsdk.open.AnnounceTimeCallBack;
import com.mchsdk.open.GPExitResult;
import com.mchsdk.open.GPUserResult;
import com.mchsdk.open.IGPExitObsv;
import com.mchsdk.open.IGPUserObsv;
import com.mchsdk.open.LogoutCallback;
import com.mchsdk.open.MCApiFactory;
import com.mchsdk.open.OrderInfo;
import com.mchsdk.open.PayCallback;
import com.mchsdk.open.ToastUtil;
import com.mchsdk.open.UploadRoleCallBack;
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

import java.util.HashMap;

public class YnSdk extends Channel {
    private final String TAG = getClass().getSimpleName();
    private Context ContextLoginIn;
    private HashMap<String, Object> loginMaps;
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
    public void init(Context context, HashMap<String, Object> initMap, CallBackListener initCallBackListener) {
        LogUtils.d(TAG, getClass().getSimpleName() + " init");
        MCApiFactory.getMCApi().setParams("0", // 渠道id
                "自然注册",// 渠道名称
                "223", // 游戏id(编号)
                "攻城三国(安卓版)",// 游戏名称
                "96F553028D2E860E1", // 游戏appid
                "FRoYAB0WDwg=",// 访问秘钥： (SDK访问服务器时的加密key)
                "http://sy.1n.cn/sdk.php/");// 联运SDK服务器地址

        // 2.初始化方法
        MCApiFactory.getMCApi().init(context, true);
        //初始化发行SDK验证判断
        PayManager.getInit("247","585","d32b4f01cf53784f94e055a9707403df");
        initOnSuccess(initCallBackListener);
        //3.注册用户注销回调
        MCApiFactory.getMCApi().initLogoutCallback(logoutCallback);
    }

    @Override
    public void login(final Context context, final HashMap<String, Object> loginMap, CallBackListener loginCallBackListener) {
      //  LogUtils.d(TAG, getClass().getSimpleName() + " login");
        if (ConfigUtils.isIssueLogin()){
            LoginBean loginBean=new LoginBean();
            PayManager.getLogin((Activity)context,loginBean);
        }else {
            MCApiFactory.getMCApi().startlogin((Activity) context, new IGPUserObsv() {
                @Override
                public void onFinish(GPUserResult gpUserResult) {
                    switch (gpUserResult.getmErrCode()) {
                        case GPUserResult.USER_RESULT_LOGIN_FAIL:
                            Toast.makeText(context, "登录失败", Toast.LENGTH_LONG).show();
                            Log.w(TAG, "登录失败");
                            break;
                        case GPUserResult.USER_RESULT_LOGIN_SUCC:
                            Toast.makeText(context, "登录成功", Toast.LENGTH_LONG).show();
                            MCApiFactory.getMCApi().startFloating(context);
                            String uid = gpUserResult.getAccountNo();// 登录用户id
                            String token = gpUserResult.getToken();
                            LoginBean loginBean=new LoginBean();
                            loginBean.setToken(token);
                            loginBean.setUid(uid);
                            PayManager.getActivityLogin1(loginBean, new LoginInterface() {
                                @Override
                                public void setUID(String uid) {
                                    Toast.makeText(context, "uid:"+uid, Toast.LENGTH_SHORT).show();
                                    loginOnSuccess(uid,loginCallBackListeners);
                                }

                                @Override
                                public void Closed(String msg) {
                                    loginOnFail(msg,loginCallBackListeners);
                                }
                            });
                            ContextLoginIn = context;
                            loginMaps = loginMap;
                            MCApiFactory.getMCApi().initAnnounceTimeCallBack(announceTimeCallBack);//设置防沉迷回调
                            break;
                    }
                }
            });
            this.loginCallBackListeners = loginCallBackListener;
        }
    }

    @Override
    public void switchAccount(Context context, CallBackListener switchAccountCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " switchAccount");
        this.switchAccountCallBackLister=switchAccountCallBackLister;
    }

    @Override
    public void logout(Context context, CallBackListener logoutCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " logout");
        MCApiFactory.getMCApi().loginout((Activity) context);
        //this.loginCallBackListeners = logoutCallBackLister;
    }

    @Override
    public void pay(final Context context, final HashMap<String, Object> payMap, CallBackListener payCallBackListener) {
        //传递到平台
        CreateOrderBean createOrderBean=new CreateOrderBean();
        createOrderBean.setCpOrderId((String) payMap.get("gorder"));
        createOrderBean.setGoodsId((String) payMap.get("productID"));
        createOrderBean.setGoodsName((String) payMap.get("productName"));
        createOrderBean.setMoney(String.valueOf(payMap.get("money")));
        createOrderBean.setRole((String) payMap.get("roleName"));
        createOrderBean.setServer((String) payMap.get("serverName"));
        createOrderBean.setExt(String.valueOf(payMap.get("extraInfo")));
//        PayManager.getCreateOrder((Activity)context,createOrderBean);
        if (ConfigUtils.isIssuePay()) {
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
                    OrderInfo order = new OrderInfo();
                    order.setProductName((String) payMap.get("productName"));// 物品名称
                    order.setProductDesc((String) payMap.get("productDesc")); // 物品描述
                    order.setAmount(Integer.parseInt(String.valueOf(payMap.get("money"))) * 100);//商品价格（单位分）
                    order.setExtendInfo((String) payMap.get("gorder")); // 用于确认交易给玩家发送商品
                    order.setGameServerId((String) payMap.get("serverID"));//游戏服务器ID
//        order.setGameServerName((String) payMap.get("serverName"));//游戏服务器名称
//        order.setGameUserId((String) payMap.get("roleID"));//角色ID
//        order.setGameUserName((String) payMap.get("roleName"));//角色名称
                    MCApiFactory.getMCApi().pay(context, order, payCallback);
                }
            });
        }
        this.payCallBackListener=payCallBackListener;
    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
        if ((Boolean)dataMap.get("create")){
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
//            MCApiFactory.getMCApi().setGameRole(userGameInfo);
            //调用上传角色方法
            MCApiFactory.getMCApi().uploadRole(context, "223", dataMap.get("serverId").toString(), dataMap.get("serverName").toString(),
                    dataMap.get("roleName").toString(), dataMap.get("roleLevel").toString(), uploadRoleCallBack);
        }else {
            MCApiFactory.getMCApi().uploadRole(context, "223", dataMap.get("serverId").toString(), dataMap.get("serverName").toString(),
                    dataMap.get("roleName").toString(), dataMap.get("roleLevel").toString(), uploadRoleCallBack);
        }
        //传递到平台
        UploadBean uploadBean=new UploadBean();
        uploadBean.setIsCreateRole(String.valueOf(dataMap.get("create")));
        uploadBean.setGameId("223");
        uploadBean.setServerId((String) dataMap.get("serverId"));
        uploadBean.setServerName((String) dataMap.get("serverName"));
        uploadBean.setUserRoleId((String) dataMap.get("roleId"));
        uploadBean.setUserRoleName((String) dataMap.get("roleName"));
        uploadBean.setVipLevel((String) dataMap.get("roleVipLevel"));
        PayManager.getUpload(uploadBean);
    }
    @Override
    public void exit(final Context context, CallBackListener exitCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " exit");
        channelNotExitDialog(exitCallBackLister);
        MCApiFactory.getMCApi().exitDialog(context, new IGPExitObsv() {
            @Override
            public void onExitFinish(GPExitResult exitResult) {
                switch (exitResult.mResultCode) {
                    case GPExitResult.GPSDKExitResultCodeError:
                        Log.e(TAG, "退出回调:调用退出弹框失败");
                        break;
                    case GPExitResult.GPSDKExitResultCodeExitGame:
                        Log.e(TAG, "退出回调:退出方法回调");
                        // 关闭悬浮窗
                        MCApiFactory.getMCApi().stopFloating(context);
                        // 下面是退出逻辑,解决退出之后微信还在的问题
                        Intent MyIntent = new Intent(Intent.ACTION_MAIN);
                        MyIntent.addCategory(Intent.CATEGORY_HOME);
                        context.startActivity(MyIntent);
//                        switchAccountOnSuccess(switchAccountCallBackLister);
//                    // 你自己的退出逻辑，退出程序
//                    finish();
//                    System.exit(0);
                        break;
                }
            }
        });
    }
    /**
     * 上传角色回调
     * 1：成功 ，其他为失败
     */
    private UploadRoleCallBack uploadRoleCallBack = new UploadRoleCallBack() {
        @Override
        public void onUploadComplete(String result) {
          //  Log.i(TAG, "上传角色回调: " + result);
            if ("1".equals(result)) {
               // ToastUtil.show(, "上传角色成功");
                Log.i(TAG, "上传角色成功");
            }else {
                Log.i(TAG, "上传角色失败");
            }

        }
    };
    /**
     * 防沉迷时间回掉
     */
    private AnnounceTimeCallBack announceTimeCallBack = new AnnounceTimeCallBack() {
        @Override
        public void callback(String result) {
            Log.i(TAG, "result: " + result);
            if (TextUtils.isEmpty(result)) {
                return;
            }
            if ("1".equals(result)) {
                //第一次到时通知
                Log.i(TAG, "第一次到时通知");
            }
            if ("2".equals(result)) {
                //第二次到时通知,玩家进入疲劳游戏时间
                Log.i(TAG, "第二次到时通知");
            }
            if ("3".equals(result)) {
                Log.e(TAG, "时间到，停止计时。");
            }

        }
    };
    /**
     * 支付结果回调
     */
    private PayCallback payCallback = new PayCallback() {
        @Override
        public void callback(String errorcode) {
            if (TextUtils.isEmpty(errorcode)) {
                return;
            }
            Log.w(TAG, errorcode);
            if ("0".equals(errorcode)) {
                Log.i( TAG,"支付成功");
                payOnSuccess(payCallBackListener);
            } else if ("1".equals(errorcode)) {
                //可以当做支付成功
                Log.i( TAG,"支付宝支付正在确认");
            } else if ("2".equals(errorcode)) {
                Log.i( TAG,"支付宝支付未获取到支付结果");
            } else {
                Log.i( TAG,"支付失败");
                payOnFailure(payCallBackListener);
            }
        }
    };

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
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

    /**
     * 注销回调
     */
    private LogoutCallback logoutCallback = new LogoutCallback() {
        @Override
        public void logoutResult(String result) {
            if (TextUtils.isEmpty(result)) {
                return;
            }
            if ("1".equals(result)) {
            //    Log.e("ss", "注销成功");
                logoutOnSuccess(loginCallBackListeners);
                MCApiFactory.getMCApi().stopFloating(ContextLoginIn);
                login(ContextLoginIn,loginMaps,loginCallBackListeners);
                //调用登录接口
            } else {
            //    Log.e("ss", "注销失败");
                logoutOnFail("注销失败",loginCallBackListeners);
            }

        }
    };

    @Override
    public void onResume(Context context) {
        super.onResume(context);
        MCApiFactory.getMCApi().onResume(context);  //开始计时，请求后台设置的悬浮球开关，图标等信息
    }

    @Override
    public void onPause(Context context) {
        super.onPause(context);
        MCApiFactory.getMCApi().stopFloating(context); //隐藏悬浮球
    }

    @Override
    public void onStop(Context context) {
        super.onStop(context);
        MCApiFactory.getMCApi().onStop(context);
    }
}
