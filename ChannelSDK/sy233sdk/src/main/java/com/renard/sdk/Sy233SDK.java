package com.renard.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.sy233.sdk.SYAPI;
import cn.sy233.sdk.sdkcallback.ILoginCallback;
import cn.sy233.sdk.sdkcallback.IPayResultCallback;

/**
 * Created by Riven_rabbit on 2019/5/7
 *
 * @author Lemon酱
 */
public class Sy233SDK extends Channel {

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
        //SDK初始化
        SYAPI.init((Activity) context,"988572857B487A98","100000");
        PayManager.getInit("78","585","d32b4f01cf53784f94e055a9707403df");
        initOnSuccess(initCallBackListener);
    }

    @Override
    public void login(final Context context, HashMap<String, Object> loginMap, CallBackListener
            loginCallBackListener) {
        LogUtils.d(TAG, getClass().getSimpleName() + " login");
        if (ConfigUtils.isIssueLogin()){
            LoginBean loginBean=new LoginBean();
            PayManager.getLogin((Activity)context,loginBean);
        }else {
            SYAPI.login((Activity) context, new ILoginCallback() {
                @Override
                public void loginSuccess(String s, boolean b, String s1) {
                    LoginBean loginBean=new LoginBean();
                    loginBean.setToken(s);
                    loginBean.setUid(s);
                    PayManager.getActivityLogin1(loginBean, new LoginInterface() {
                        @Override
                        public void setUID(String uid) {
                            loginOnSuccess(uid,loginCallBackListeners);
                        }

                        @Override
                        public void Closed(String msg) {
                            loginOnFail(msg,loginCallBackListeners);
                        }
                    });
                }

                @Override
                public void loginError(int i, String s) {

                }

                @Override
                public void onLogout() {
                    switchAccountOnSuccess(switchAccountCallBackLister);
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
        SYAPI.logout((Activity) context);
    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
        SYAPI.setRoleData((Activity) context, (String) dataMap.get("roleId"), (String) dataMap.get("roleName"), (String) dataMap.get("roleLevel"), (String) dataMap.get("serverId"), (String) dataMap.get("serverName"));
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
                    SYAPI.pay((Activity)context,(String) payMap.get("gorder"), Integer.parseInt(String.valueOf(payMap.get("money")))*100, (String) payMap.get("productID"), (String) payMap.get("productName"), new IPayResultCallback() {
                        @Override
                        public void payResult(int code, String msg) {
                            if(code==0){
                                Toast.makeText(context,"支付成功",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(context,"支付失败"+msg,Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            });
        }
        this.payCallBackListener=payCallBackListener;
    }

    @Override
    public void onResume(Context context) {
        super.onResume(context);
        SYAPI.showFloat((Activity) context);
    }

    @Override
    public void onPause(Context context) {
        super.onPause(context);
        SYAPI.hideFloat((Activity) context);
    }

    @Override
    public void exit(Context context, CallBackListener exitCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " exit");
        channelNotExitDialog(exitCallBackLister);
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

    }
}
