package com.renard.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.mchsdk.open.AnnounceTimeCallBack;
import com.mchsdk.open.GPExitResult;
import com.mchsdk.open.GPUserResult;
import com.mchsdk.open.IGPExitObsv;
import com.mchsdk.open.IGPUserObsv;
import com.mchsdk.open.IGPlogoffObsv;
import com.mchsdk.open.MCApiFactory;
import com.mchsdk.open.OrderInfo;
import com.mchsdk.open.PayCallback;
import com.mchsdk.open.UserGameInfo;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by Riven_rabbit on 2019/5/7
 *
 * @author Lemon酱
 */
public class XtSDK extends Channel {

    private final String TAG = getClass().getSimpleName();


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
    public void init(Context context, HashMap<String, Object> initMap, CallBackListener
            initCallBackListener) {
        LogUtils.d(TAG, getClass().getSimpleName() + " init");
//        //欢聚游SDK初始化
//        MCApiFactory.getMCApi().setParams("0", // 渠道id
//                "自然注册",// 渠道名称
//                "4044", // 游戏id(编号)
//                "攻城三国",// 游戏名称
//                "61EB166F47A439454"// 游戏appid
//                , "D18RQgIKUQIHGFZUAwhGBFdVAw4= ",// 访问秘钥： (SDK访问服务器时的加密key)
//                "http://www.jmsdk.net/sdk.php/");// 联运SDK服务器地址
        //SDK初始化
        String json=getJson(context,"Parameter_config.json");
        MCApiFactory.getMCApi().setParams("0", // 渠道id
                "自然注册",// 渠道名称
                GsonUtils.getValue(json,"config_id"), // 游戏id(编号)
                GsonUtils.getValue(json,"config_name"),// 游戏名称
                GsonUtils.getValue(json,"config_appid"),// 游戏appid
                GsonUtils.getValue(json,"config_appkey"),// 访问秘钥： (SDK访问服务器时的加密key)
                "http://www.jmsdk.net/sdk.php/");// 联运SDK服务器地址
        //SDK初始化
     //   String json=getJson(context,"Parameter_config.json");
//        MCApiFactory.getMCApi().setParams("0", // 渠道id
//                "自然注册",// 渠道名称
//              "5267", // 游戏id(编号)
//                "萌侠Q传星耀版-BT",// 游戏名称
//                "C9508A07C1EEB96A6",// 游戏appid
//                "UhcQFBsGBAkJWQ1UAAJNWAQLHAA",// 访问秘钥： (SDK访问服务器时的加密key)
//                "http://www.jmsdk.net/sdk.php/");// 联运SDK服务器地址

        // 初始化方法
        MCApiFactory.getMCApi().init(context, true);//开启debug模式输出SDK日志
        //初始化发行SDK验证判断
        PayManager.getInit(  GsonUtils.getValue(json,"config_aid"),
                GsonUtils.getValue(json,"config_gid"),
                GsonUtils.getValue(json,"config_key"));

        initOnSuccess(initCallBackListener);
    }


    public static String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    @Override
    public void login(final Context context, HashMap<String, Object> loginMap, final CallBackListener loginCallBackListener) {
        LogUtils.d(TAG, getClass().getSimpleName() + " login");
        if (ConfigUtils.isIssueLogin()){
            LoginBean loginBean=new LoginBean();
            PayManager.getLogin((Activity)context,loginBean);
            LogUtils.d(TAG, getClass().getSimpleName() + " login1");
        }else {
            LogUtils.d(TAG, getClass().getSimpleName() + " login2");
            MCApiFactory.getMCApi().startlogin(context, new IGPUserObsv() {
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
                            final String token = gpUserResult.getToken();
                            LoginBean loginBean=new LoginBean();
                            loginBean.setToken(token);
                            loginBean.setUid(uid);
                            PayManager.getActivityLogin1(loginBean, new LoginInterface() {
                                @Override
                                public void setUID(String uid) {
//                                    loginOnSuccess(uid,loginCallBackListeners);
                                    loginOnSuccess(uid,token,loginCallBackListeners);
                                }

                                @Override
                                public void Closed(String msg) {
                                    loginOnFail(msg,loginCallBackListeners);
                                }
                            });
                            MCApiFactory.getMCApi().initAnnounceTimeCallBack(announceTimeCallBack);//设置防沉迷回调
                            break;
                    }
                }
            });
        }
        this.loginCallBackListeners = loginCallBackListener;
    }

    @Override
    public void switchAccount(Context context, CallBackListener switchAccountCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " switchAccount");
        this.switchAccountCallBackLister=switchAccountCallBackLister;
    }

    @Override
    public void logout(Context context, CallBackListener logoutCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " logout");
        MCApiFactory.getMCApi().loginout(context);
    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
        if ((Boolean)dataMap.get("create")){
            UserGameInfo userGameInfo = new UserGameInfo();
            userGameInfo.setGameServerId((String) dataMap.get("serverId"));//游戏服务器ID
            userGameInfo.setGameServerName((String) dataMap.get("serverName"));//游戏服务器名称
            userGameInfo.setGameUserId((String) dataMap.get("roleId"));//角色ID
            userGameInfo.setGameUserName((String) dataMap.get("roleName"));//角色名称
            userGameInfo.setGameUserLevel((String) dataMap.get("roleLevel"));//角色等级
            userGameInfo.setGameUserVipLevel((String) dataMap.get("roleVipLevel"));//角色VIP等级
            userGameInfo.setGameUserGold((String) dataMap.get("roleBalance"));//角色剩余金币
            userGameInfo.setGameUserDiamond((String) dataMap.get("roleBalance"));//角色剩余钻石
            userGameInfo.setGameUserProfession("无职业");//角色职业
            MCApiFactory.getMCApi().setGameRole(userGameInfo);
        }else {
            UserGameInfo userGameInfo = new UserGameInfo();
            userGameInfo.setGameServerId((String) dataMap.get("serverId"));//游戏服务器ID
            userGameInfo.setGameServerName((String) dataMap.get("serverName"));//游戏服务器名称
            userGameInfo.setGameUserId((String) dataMap.get("roleId"));//角色ID
            userGameInfo.setGameUserName((String) dataMap.get("roleName"));//角色名称
            userGameInfo.setGameUserLevel((String) dataMap.get("roleLevel"));//角色等级
            userGameInfo.setGameUserVipLevel((String) dataMap.get("roleVipLevel"));//角色VIP等级
            userGameInfo.setGameUserGold((String) dataMap.get("roleBalance"));//角色剩余金币
            userGameInfo.setGameUserDiamond((String) dataMap.get("roleBalance"));//角色剩余钻石
            userGameInfo.setGameUserProfession("无职业");//角色职业
            MCApiFactory.getMCApi().uploadGameRole(userGameInfo);
        }
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
    public void pay(final Context context, final HashMap<String, Object> payMap, CallBackListener
            payCallBackListener) {
        //传递到平台
        CreateOrderBean createOrderBean=new CreateOrderBean();
        createOrderBean.setCpOrderId((String) payMap.get("gorder"));
        createOrderBean.setGoodsId((String) payMap.get("productID"));
        createOrderBean.setGoodsName((String) payMap.get("productName"));
        createOrderBean.setMoney(String.valueOf(payMap.get("money")));
        createOrderBean.setRole((String) payMap.get("roleName"));
        createOrderBean.setServer((String) payMap.get("serverName"));
        createOrderBean.setExt(String.valueOf(payMap.get("extraInfo")));
      //  PayManager.getCreateOrder((Activity)context,createOrderBean);
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
                    OrderInfo order = new OrderInfo();
                    order.setProductName((String) payMap.get("productName"));// 物品名称
                    order.setProductDesc((String) payMap.get("productDesc")); // 物品描述
                    order.setAmount(Integer.parseInt(String.valueOf(payMap.get("money")))*100);//商品价格（单位分）
                    order.setExtendInfo((String) payMap.get("gorder")); // 用于确认交易给玩家发送商品
                    order.setGameServerId((String) payMap.get("serverID"));//游戏服务器ID
                    order.setGameServerName((String) payMap.get("serverName"));//游戏服务器名称
                    order.setGameUserId((String) payMap.get("roleID"));//角色ID
                    order.setGameUserName((String) payMap.get("roleName"));//角色名称
                    MCApiFactory.getMCApi().pay(context, order, payCallback);
                }
            });
        }
        this.payCallBackListener=payCallBackListener;
    }

    @Override
    public void exit(final Context context, CallBackListener exitCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " exit");
        //channelNotExitDialog(exitCallBackLister);
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
                      //  ((Activity)context).finish();
                   // System.exit(0);
                        break;
            }
            }
        });
    }

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

    @Override
    public void onDestroy(Context context) {
            MCApiFactory.getMCApi().stopFloating(context);
    }
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
}
