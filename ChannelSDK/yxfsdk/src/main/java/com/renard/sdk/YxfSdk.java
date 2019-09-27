package com.renard.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.game.sdk.util.ToastUtils;
import com.renard.common.Interface.CallBackListener;
import com.renard.common.cache.SharePreferencesCache;
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
import com.tencent.mm.opensdk.utils.Log;
import com.yaoyue.release.ICallback;
import com.yaoyue.release.YYReleaseSDK;
import com.yaoyue.release.inter.OnKeyPress;
import com.yaoyue.release.model.GameInfo;
import com.yaoyue.release.model.GamePayInfo;
import com.yaoyue.release.model.UserInfoModel;
import com.yaoyue.release.service.InitService;
import com.yaoyue.release.util.SDKUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.game.sdk.util.NewSign.sign;

public class YxfSdk extends Channel {
    private final String TAG = getClass().getSimpleName();
    private GameInfo gameInfo;
    private CallBackListener loginCallBackListeners;
    private CallBackListener initCallBackListener;
    private CallBackListener payCallBackListener;
    private CallBackListener switchAccountCallBackLister;
    private CallBackListener logoutCallBackLister;
    private Context GameContext;

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
        //初始化发行SDK验证判断
        PayManager.getInit("250", "585","d32b4f01cf53784f94e055a9707403df");
        YYReleaseSDK.getInstance().sdkInit((Activity) context, callback);
        this.initCallBackListener = initCallBackListener;
    }

    @Override
    public void login(Context context, HashMap<String, Object> loginMap, CallBackListener loginCallBackListener) {
        LogUtils.d(TAG, getClass().getSimpleName() + " login");
        if (ConfigUtils.isIssueLogin()) {
            LoginBean loginBean = new LoginBean();
            PayManager.getLogin((Activity) context, loginBean);
        } else {
            YYReleaseSDK.getInstance().sdkLogin((Activity) context, callback);
        }
        this.loginCallBackListeners = loginCallBackListener;
    }

    @Override
    public void switchAccount(Context context, CallBackListener changeAccountCallBackLister) {
        this.switchAccountCallBackLister = changeAccountCallBackLister;
    }

    @Override
    public void logout(Context context, CallBackListener logoutCallBackLister) {
//        GameInfo gameInfo = new GameInfo(); //角色id
//        gameInfo.setRoleId((String) dataMap.get("roleId"));
//        gameInfo.setRoleLevel((String) dataMap.get("roleLevel"));//角色等级
//        gameInfo.setRoleName((String) dataMap.get("roleName")); //角色名称
//        gameInfo.setZoneId((String) dataMap.get("serverId"));//setZoneId 和setServerId 传的值应该相同
//        gameInfo.setServerId((String) dataMap.get("serverId"));//区服id
//        gameInfo.setZoneName((String) dataMap.get("serverName"));//区服名
//        gameInfo.setVipLevel((String) dataMap.get("roleVipLevel"));  //vip等级
//        YYReleaseSDK.getInstance().createRole((Activity) context, gameInfo, callback);
        if (gameInfo == null) {
            ToastUtils.showToast(context, "请先上传信息");
        } else {
            YYReleaseSDK.getInstance().onSdkLogOut((Activity) context, gameInfo, callback);
            this.logoutCallBackLister = logoutCallBackLister;
        }

    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
        gameInfo = new GameInfo(); //角色id
        gameInfo.setRoleId((String) dataMap.get("roleId"));
        gameInfo.setRoleLevel((String) dataMap.get("roleLevel"));//角色等级
        gameInfo.setRoleName((String) dataMap.get("roleName")); //角色名称
        gameInfo.setZoneId((String) dataMap.get("serverId"));//setZoneId 和setServerId 传的值应该相同
        gameInfo.setServerId((String) dataMap.get("serverId"));//区服id
        gameInfo.setZoneName((String) dataMap.get("serverName"));//区服名
        gameInfo.setVipLevel((String) dataMap.get("roleVipLevel"));  //vip等级
        if ((Boolean) dataMap.get("create")) {
            //创建角色
            YYReleaseSDK.getInstance().createRole((Activity) context, gameInfo, callback);
            //  SharePreferencesCache.putObject();
            //进入游戏
            GameContext = context;
        } else {
            //角色升级
            YYReleaseSDK.getInstance().levelUpdate((Activity) context, gameInfo);
        }
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

    @Override
    public void pay(final Context context, final HashMap<String, Object> payMap, CallBackListener payCallBackListener) {
        if (gameInfo == null) {
            ToastUtils.showToast(context, "请先上传信息");
        } else {
            //传递到平台
            CreateOrderBean createOrderBean = new CreateOrderBean();
            createOrderBean.setCpOrderId((String) payMap.get("gorder"));
            createOrderBean.setGoodsId((String) payMap.get("productID"));
            createOrderBean.setGoodsName((String) payMap.get("productName"));
            createOrderBean.setMoney(String.valueOf(payMap.get("money")));
            createOrderBean.setRole((String) payMap.get("roleName"));
            createOrderBean.setServer((String) payMap.get("serverName"));
            createOrderBean.setExt(String.valueOf(payMap.get("extraInfo")));
            // PayManager.getCreateOrder((Activity)context,createOrderBean);
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
                        //所有参数不能为空，否则报错
                        GamePayInfo payInfo = new GamePayInfo();
                        payInfo.setExtInfo("cusompay");//自定义参数
                        payInfo.setMoney(String.valueOf(Integer.parseInt(String.valueOf(payMap.get("money"))) * 100));//支付金额（只支持定额支付，以分为单位，值不能少于100分）
                        payInfo.setNotifyUrl("http://issue.hjygame.com/sdk/gamepay/aid/250/gid/585/");//支付回调地址
                        payInfo.setCpOrderId((String) payMap.get("gorder"));//游戏生成的订单编号
                        payInfo.setProductCount(Integer.parseInt(String.valueOf(payMap.get("buyNum"))));//商品数量
                        payInfo.setProductId((String) payMap.get("productID"));  //购买物品的id
                        payInfo.setProductName((String) payMap.get("productName")); //购买物品的名称
                        String appId = SDKUtils.getAppId(context);
                        String sign_key = "c7bk93ymhhgrk84akg42jhq7qugg5rrg";//这个可以在对接群中的参数中
//        sign(String appId, String uid, String cpOrderId, String roleId, String zoneId, String amount,String signKey)
                        String sign = sign(appId, InitService.mUserInfoModel.id, (String) payMap.get("gorder"), gameInfo.getRoleId(), gameInfo.getServerId(), payInfo.getMoney(), sign_key);
                        payInfo.setSign(sign);
                        YYReleaseSDK.getInstance().doPay((Activity) context, gameInfo, payInfo, callback);
                    }
                });
            }
            this.payCallBackListener = payCallBackListener;
        }
    }

    @Override
    public void exit(Context context, CallBackListener exitCallBackLister) {
        // YYReleaseSDK.getInstance().onSdkExit((Activity) context, gameInfo, callback);
        channelNotExitDialog(exitCallBackLister);
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        YYReleaseSDK.getInstance().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(context, requestCode, resultCode, data);
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

    private ICallback callback = new ICallback() {
        @Override
        public void paySuccess(String orderId) {
            //   writeLog("支付成功 orderId = " + orderId);
            payOnSuccess(payCallBackListener);
        }

        @Override
        public void onError(int type, String message) {
            switch (type) {
                case ICallback.INIT:
                    //    writeLog("初始化失败 " + message);

                    break;
                case ICallback.LOGIN:
                    //   writeLog("登陆失败 " + message);
                    loginOnFail(message,loginCallBackListeners);
                    break;
                case ICallback.CREATE_ROLE:
                    //   writeLog("创建角色失败 " + message);
                    break;
                case ICallback.UPLOAD_GAME_INFO:
                    //      writeLog("更新角色信息失败 " + message);
                    break;
                case ICallback.PAY:
                    //       writeLog("支付失败 " + message);
                    payOnFailure(payCallBackListener);
                    break;
                default:
                    //      writeLog("onError type = " + type + "  , message = " + message);
                    break;
            }
        }

        @Override
        public void logoutSuccess() {
            //      writeLog("登出成功");
            //      login();
            logoutOnSuccess(logoutCallBackLister);
        }


        @Override
        public void initSuccess() {
            //     init_suc = true;
            Log.e("ssss", "初始化成功");
            //     writeLog("初始化成功");
            initOnSuccess(initCallBackListener);
//            login();
        }

        @Override
        public void loginSuccess(UserInfoModel userInfoModel) {
            LoginBean loginBean = new LoginBean();
            loginBean.setToken(userInfoModel.sessionId);
            loginBean.setUid(userInfoModel.pid);

            PayManager.getActivityLogin1(userInfoModel.id,userInfoModel.sessionId,userInfoModel.pid, new LoginInterface() {
                @Override
                public void setUID(String uid) {
                    loginOnSuccess(uid, loginCallBackListeners);
                }

                @Override
                public void Closed(String msg) {
                    loginOnFail(msg,loginCallBackListeners);
                }

            });
            // userInfoModel.id 在5.0 已经废弃,需要通过 sessionId从服务器获取

            //     writeLog("登陆成功,  " +   " sessionId = " + userInfoModel.sessionId);
            //登陆验证 token的有效时间为2分钟
            //对userId进行判断，如果userId发生改变，重新回到选服界面
//            if (userId == null || userId == userInfoModel.id) {
//                userId = userInfoModel.id;
//                //进入游戏
//            } else {
//                //清除角色信息，回到选服界面
//                userId = userInfoModel.id;
//            }

        }

        @Override
        public void setGameInfoSuccess(String loginTime) {
            //  writeLog("调用进入游戏 setGameInfo Success  loginTime =" + loginTime);
        }

        @Override
        public void exitSuccess() {
            //finish();
        }

        @Override
        public void createRoleSuccess() {
            //   writeLog("创建角色成功");
            //设置角色
            YYReleaseSDK.getInstance().setGameInfo((Activity) GameContext, gameInfo, true, callback);
        }

    };

    public static String sign(String appId, String uid, String cpOrderId, String roleId, String zoneId, String amount, String signKey) {
        Map<String, Object> signParam = new HashMap<>();
        signParam.put("appId", appId);
        signParam.put("uid", uid);
        signParam.put("cpOrderId", cpOrderId);
        signParam.put("roleId", roleId);
        signParam.put("zoneId", zoneId);
        signParam.put("amount", amount);
        return signByMD5(signParam, signKey);
    }

    public static String signByMD5(Map<String, Object> params, String secretKey) {
        if (null == params || params.isEmpty()) throw new RuntimeException("params can't be empty");
        Map<String, Object> result = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        result.putAll(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
//        logger.info(sb.substring(0,sb.length()-1).concat(secretKey));
        return md5(sb.substring(0, sb.length() - 1).concat(secretKey));
    }

    private static String md5(String s) {
        MessageDigest sMd5MessageDigest = null;
        try {
            sMd5MessageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        }
        StringBuilder sStringBuilder = new StringBuilder();
        sMd5MessageDigest.reset();
        sMd5MessageDigest.update(s.getBytes());

        byte digest[] = sMd5MessageDigest.digest();

        sStringBuilder.setLength(0);
        for (int i = 0; i < digest.length; i++) {
            final int b = digest[i] & 255;
            if (b < 16) {
                sStringBuilder.append('0');
            }
            sStringBuilder.append(Integer.toHexString(b));
        }
        return sStringBuilder.toString();
    }

    @Override
    public void onCreate(Context context, Bundle savedInstanceState) {
        super.onCreate(context, savedInstanceState);
        YYReleaseSDK.getInstance().onCreate((Activity) context);
    }

    @Override
    public void onPause(Context context) {
        super.onPause(context);
        YYReleaseSDK.getInstance().onSdkPause((Activity) context);
    }

    @Override
    public void onStop(Context context) {
        super.onStop(context);
        YYReleaseSDK.getInstance().onSdkStop((Activity) context);
    }

    @Override
    public void onResume(Context context) {
        super.onResume(context);
        YYReleaseSDK.getInstance().onSdkResume((Activity) context);
    }

    @Override
    public void onRestart(Context context) {
        super.onRestart(context);
        YYReleaseSDK.getInstance().onRestart((Activity) context);
    }

    @Override
    public void onNewIntent(Context context, Intent intent) {
        super.onNewIntent(context, intent);
        YYReleaseSDK.getInstance().onNewIntent(intent);
    }

    @Override
    public void onDestroy(Context context) {
        super.onDestroy(context);
        YYReleaseSDK.getInstance().onSdkDestory((Activity) context);
    }
}
