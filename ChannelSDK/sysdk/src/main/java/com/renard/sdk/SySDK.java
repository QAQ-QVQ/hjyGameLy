package com.renard.sdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.sy.nsdk.NSdk;
import com.sy.nsdk.NSdkListener;
import com.sy.nsdk.NSdkStatusCode;
import com.sy.nsdk.bean.NSAppInfo;
import com.sy.nsdk.bean.NSLoginResult;
import com.sy.nsdk.bean.NSPayInfo;
import com.sy.nsdk.bean.NSRoleInfo;
import com.sy.nsdk.exception.NSdkException;

import java.util.HashMap;

public class SySDK extends Channel {
    private final String TAG = getClass().getSimpleName();
    private CallBackListener loginCallBackListeners;
    private CallBackListener payCallBackListener;
    private CallBackListener switchAccountCallBackLister;
    private  static String uids;

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
        NSAppInfo appinfo = new NSAppInfo();
//        10000 4df4ba90e19389458a4fe9
//        10093 2df4b924393c537752c4e7
        appinfo.appId = "10093";// 填写自己游戏appId(由sdk的商务提供)
        appinfo.appKey = "2df4b924393c537752c4e7";// 填写自己游戏的appKey(由sdk的商务提供)
        try {
            // check权限
            if ((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                // 没有 ， 申请权限 权限数组
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
                // 有 则执行初始化
                NSdk.getInstance().init((Activity) context, appinfo, new NSdkListener<String>() {
                    @Override
                    public void callback(int i, String s) {
                        if (i == NSdkStatusCode.INIT_SUCCESS) {
                            //初始化成功
                            initOnSuccess(initCallBackListener);
                            NSdk.getInstance().getChannel();
                            NSdk.getInstance().getSdkVersion();
                            NSdk.getInstance().getExt((Activity) context);
                            //切换账号
                            NSdk.getInstance().setAccountSwitchListener(new NSdkListener<String>() {
                                @Override
                                public void callback(int code, String resp) {
                                    if (code == NSdkStatusCode.SWITCH_SUCCESS) {
                                        // todo清空游戏信息，(切勿重复调用登陆接口)
//                                    uid = "";
//                                    sid = "";
//                                    uidtv.setText("");
//                                    sidtv.setText("");
//                                    resulttv.setText("");
                                        Toast.makeText(context, "切换成功!", Toast.LENGTH_SHORT).show();
                                        NSdk.getInstance().hideToolBar((Activity) context);
                                        switchAccountOnSuccess(switchAccountCallBackLister);
                                    }else if (code ==NSdkStatusCode.SWITCH_FAILURE) {
                                        //切换或注销失败
                                        switchAccountOnFail("切换失败",switchAccountCallBackLister);
                                    }
                                }
                            });
                        } else if(i == NSdkStatusCode.INIT_FAILURE) {
                            //初始化失败
                        }
                    }
                });

        } catch (Exception e) {
            e.printStackTrace();
            // 异常 继续申请
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        PayManager.getInit("258","585","d32b4f01cf53784f94e055a9707403df");
    }

    @Override
    public void login(final Context context, HashMap<String, Object> loginMap, CallBackListener loginCallBackListener) {
        if (ConfigUtils.isIssueLogin()){
            LoginBean loginBean=new LoginBean();
            PayManager.getLogin((Activity)context,loginBean);
        }else {
            try {
                Log.i(TAG, "login sdk");
                NSdk.getInstance().login((Activity) context, new NSdkListener<NSLoginResult>() {

                    @Override
                    public void callback(int code, NSLoginResult response) {
                        Toast.makeText(context,"[NSDK_LOGIN]"+response.msg, Toast.LENGTH_LONG).show();
                        switch (code) {
                            case NSdkStatusCode.LOGIN_SUCCESS:
                                //登陆成功
//                            sid = response.sid;
//                            uidtv.setText(uid);
//                            sidtv.setText(sid);
                                Log.i(TAG, response.msg + ":sid=" + response.sid
                                        + "uid=" + response.uid);
                                //    isLogined = true;
                               // LoginBean loginBean=new LoginBean();
                             //   loginBean.setToken(response.sid);
                              //  loginBean.setUid(response.uid);
                                PayManager.getActivityLogin("10093", NSdk.getInstance().getChannel(), response.uid, response.sid, NSdk.getInstance().getExt((Activity) context), NSdk.getInstance().getSdkVersion(), new LoginInterface() {
                                    @Override
                                    public void setUID(String uid) {
                                        uids = uid;
                                        Log.e(TAG,"uid:"+uid.toString());
                                        loginOnSuccess(uid, loginCallBackListeners);
                                    }

                                    @Override
                                    public void Closed(String msg) {
                                        loginOnFail(msg,loginCallBackListeners);
                                    }
                                });

                                NSdk.getInstance().showToolBar((Activity) context);
                                break;
                            case NSdkStatusCode.LOGIN_CANCLE:
                                Log.i(TAG, response.msg);
                                break;
                            case NSdkStatusCode.LOGIN_OTHER:
                                Log.i(TAG, response.msg);
                                break;
                            case NSdkStatusCode.INIT_FAILURE:
                                Log.i(TAG, response.msg);
                                break;
                            default:
                                break;
                        }
                    }
                });
            } catch (NSdkException e) {
                e.printStackTrace();
            }

        }
        this.loginCallBackListeners = loginCallBackListener;
    }

    @Override
    public void switchAccount(final Context context, CallBackListener changeAccountCallBackLister) {
        this.switchAccountCallBackLister=changeAccountCallBackLister;
    }

    @Override
    public void logout(final Context context, final CallBackListener logoutCallBackLister) {
      //  NSdk.getInstance().logout((Activity) context);
        NSdk.getInstance().accountSwitch((Activity) context);
    }

    @Override
    public void pay(final Context context, final HashMap<String, Object> payMap, final CallBackListener payCallBackListener) {
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
                public void PayOrder()  {
                    NSPayInfo info = new NSPayInfo();
                    info.gameName = "攻城三国";//游戏名称
                    info.productId = (String) payMap.get("productID");//商品ID
                    info.productName = (String) payMap.get("productName");//商品名称
                    info.productDesc = (String) payMap.get("productDesc");//商品描述
                    info.price = Integer.parseInt(String.valueOf(payMap.get("money")))*100 ;// 金额（单位为分）
                    info.coinNum = Integer.parseInt(payMap.get("coinNum").toString());
                    info.serverId = Integer.parseInt((String) payMap.get("serverID"));//服务器id
                    info.serverName = (String) payMap.get("serverName");//服务器名称
                    info.uid = uids;
                    info.roleId = (String) payMap.get("roleID");//游戏的角色id
                    info.roleName = (String) payMap.get("roleName");
                    info.roleLevel = Integer.parseInt((String) payMap.get("roleLevel"));//角色等级
                    info.giftId = " ";//选填
                    info.privateField = (String) payMap.get("gorder");//选填 订单透传字段
                    info.ratio = 10;
                    info.buyNum = Integer.parseInt(payMap.get("buyNum").toString());
                    try {
                        NSdk.getInstance().pay((Activity) context, info, new NSdkListener<String>() {
                            @Override
                            public void callback(int code, String response) {
                                Log.i(TAG, "code=" + code + ", response=" + response);
                                switch (code) {
                                    case NSdkStatusCode.PAY_SUCCESS:
                                        // 支付成功
                                        payOnSuccess(payCallBackListener);
                                        break;
                                    case NSdkStatusCode.PAY_FAILURE:
                                        // 支付失败
                                        payOnFailure(payCallBackListener);
                                        break;
                                    case NSdkStatusCode.PAY_CANCLE:
                                        // 取消支付
                                        break;
                                    case NSdkStatusCode.PAY_PAYING:
                                        // 正在支付
                                        break;
                                    case NSdkStatusCode.PAY_NOCALLBACK:
                                        // 有些渠道支付完成之后客户端是没有回调的
                                        break;
                                    case NSdkStatusCode.PAY_OTHER:
                                    default:
                                        // 其他支付错误回调
                                        break;
                                }
                            }
                        });
                    } catch (NSdkException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        this.payCallBackListener=payCallBackListener;

    }

    @Override
    public void exit(Context context, CallBackListener exitCallBackLister) {
        channelNotExitDialog(exitCallBackLister);
    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
        NSRoleInfo roleinfo = new NSRoleInfo();
        roleinfo.roleId = (String) dataMap.get("roleId");//角色ID
        roleinfo.uid = uids;//用户ID
        roleinfo.roleName = (String) dataMap.get("roleName");//角色名称
        roleinfo.roleCtime=(String) dataMap.get("roleCreateTime");//角色创建时间（单位：秒，10位数）（必填）
        roleinfo.roleLevel = (String) dataMap.get(" roleLevel ");//角色等级
        roleinfo.roleLevelMtime= "";//角色等级变化时间（单位：秒，10位数）
        roleinfo.zoneId = (String) dataMap.get("serverId");//区ID
        roleinfo.zoneName = (String) dataMap.get("serverName");//区名称
        roleinfo.dataType = "2";//数据类型：1：进入游戏；2：创建角色；3：角色升级；4：游戏退出
        roleinfo.ext = "";
        if ((Boolean)dataMap.get("create")) {
            NSdk.getInstance().submitGameInfo((Activity) context, roleinfo);
        }else {
            NSdk.getInstance().submitGameInfo((Activity) context, roleinfo);
        }

        //传递到平台
        UploadBean uploadBean=new UploadBean();
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
        NSdk.getInstance().onResume((Activity) context);
    }

    @Override
    public void onPause(Context context) {
        super.onPause(context);
        NSdk.getInstance().onPause((Activity) context);
    }

    @Override
    public void onStart(Context context) {
        // TODO Auto-generated method stub
        super.onStart(context);
        NSdk.getInstance().onStart((Activity) context);
    }
    @Override
    public void onStop(Context context) {
        super.onStop(context);
        NSdk.getInstance().onStop((Activity) context);
    }

    @Override
    public void onDestroy(Context context) {
        super.onDestroy(context);
        NSdk.getInstance().onDestroy((Activity) context);
    }

    @Override
    public void onConfigurationChanged(Context context,Configuration newConfig) {
        super.onConfigurationChanged(context,newConfig);
        NSdk.getInstance().onConfigurationChanged((Activity) context, newConfig);
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(context,requestCode, resultCode, data);
        NSdk.getInstance().onActivityResult((Activity) context, requestCode, resultCode, data);
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
    public void onRestart(Context context) {
        super.onRestart(context);
        NSdk.getInstance().onRestart((Activity) context);
    }

    @Override
    public void onNewIntent(Context context, Intent intent) {
        super.onNewIntent(context,intent);
        NSdk.getInstance().onNewIntent((Activity) context, intent);
    }
    /******************必接sdk生命周期 end***********************/
}
