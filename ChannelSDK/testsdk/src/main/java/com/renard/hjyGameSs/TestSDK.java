package com.renard.hjyGameSs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Riven_rabbit on 2019/5/6
 *
 * @author Lemon酱
 */
public class TestSDK extends Channel {

    private final String TAG = getClass().getSimpleName();
    private CallBackListener payCallBackListener;
    private CallBackListener loginCallBackListener;
    @Override
    protected void initChannel() {
        LogUtils.d(TAG, getClass().getSimpleName() + " has init");
    }

    @Override
    public String getChannelID() {
        return "1";
    }

    @Override
    public boolean isSupport(int FuncType) {

        switch (FuncType){
            case TypeConfig.FUNC_SWITCHACCOUNT:
                return true;

            case TypeConfig.FUNC_LOGOUT:
                return true;

            case TypeConfig.FUNC_SHOW_FLOATWINDOW:
                return true;

            case TypeConfig.FUNC_DISMISS_FLOATWINDOW:
                return true;

            default:
                return false;
        }
    }

    @Override
    public void init(Context context, HashMap<String, Object> initMap, CallBackListener initCallBackListener) {
        LogUtils.d(TAG,getClass().getSimpleName() + " init");
        final String json = GetJsonDataUtil.getJson(context, "Parameter_config.json");
//        PayManager.getInit("13","648","721085624a6d4fdb7fbda838f80a26c1");
        PayManager.getInit(GsonUtils.getValue(json, "config_aid"),
                GsonUtils.getValue(json, "config_gid"),
                GsonUtils.getValue(json, "config_key"));
        initOnSuccess(initCallBackListener);
    }

    @Override
    public void login(Context context, HashMap<String, Object> loginMap, CallBackListener loginCallBackListener) {
        LogUtils.d(TAG,getClass().getSimpleName() + " login");
        showLoginView(context);
        this.loginCallBackListener=loginCallBackListener;
    }

    @Override
    public void switchAccount(final Context context, final CallBackListener changeAccountCallBackLister) {
        LogUtils.d(TAG,getClass().getSimpleName() + " switchAccount");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("是否切换账号?");
        builder.setTitle("切换账号");
        builder.setPositiveButton("切换账号",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        switchAccountOnSuccess(changeAccountCallBackLister);
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        switchAccountOnCancel("channel switchAccount cancel",changeAccountCallBackLister);
                    }
                });
        builder.create().show();
    }

    @Override
    public void logout(Context context, final CallBackListener logoutCallBackLister) {
        LogUtils.d(TAG,getClass().getSimpleName() + " logout");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("是否注销账号?");
        builder.setTitle("注销账号");
        builder.setPositiveButton("成功",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        logoutOnSuccess(logoutCallBackLister);
                    }
                });
        builder.setNegativeButton("失败",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        logoutOnFail("channel logout fail",logoutCallBackLister);
                    }
                });
        builder.create().show();

    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
        //传递到平台
        UploadBean uploadBean=new UploadBean();
        uploadBean.setIsCreateRole(String.valueOf(dataMap.get("create")));
        uploadBean.setGameId("648");
        uploadBean.setServerId((String) dataMap.get("serverId"));
        uploadBean.setServerName((String) dataMap.get("serverName"));
        uploadBean.setUserRoleId((String) dataMap.get("roleId"));
        uploadBean.setUserRoleName((String) dataMap.get("roleName"));
        uploadBean.setVipLevel((String) dataMap.get("roleVipLevel"));
        PayManager.getUpload(uploadBean);
    }

    @Override
    public void pay(final Context context, final HashMap<String, Object> payMap, final CallBackListener payCallBackListener) {
        LogUtils.d(TAG,getClass().getSimpleName() + " pay");
        CreateOrderBean createOrderBean=new CreateOrderBean();
        createOrderBean.setCpOrderId((String) payMap.get("gorder"));
        createOrderBean.setGoodsId((String) payMap.get("productID"));
        createOrderBean.setGoodsName((String) payMap.get("productName"));
        createOrderBean.setMoney(String.valueOf(payMap.get("money")));
        createOrderBean.setRole((String) payMap.get("roleName"));
        createOrderBean.setServer((String) payMap.get("serverName"));
        createOrderBean.setExt(String.valueOf(payMap.get("extraInfo")));
       // PayManager.getCreateOrder((Activity)context,createOrderBean);
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
                }
            });
        }
        this.payCallBackListener=payCallBackListener;
//        String orderID = (String) payMap.get("orderId");
//        String productName = (String) payMap.get("productName");
//        String productDesc = (String) payMap.get("productDesc");
//        String money = String.valueOf(payMap.get("money"));
//        String productID = String.valueOf(payMap.get("productID"));
//        LogUtils.d(TAG,productID);
//
//        final HashMap<String,Object> paymap = new HashMap<>();
//        paymap.put("orderID",orderID);
//        paymap.put("productName",productName);
//        paymap.put("money",money);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        String message = "充值金额：" + money
//                + "\n商品名称：" + productName
//                + "\n商品数量：" + "1"
//                + "\n资费说明：" + productDesc;
//        builder.setMessage(message);
//        builder.setTitle("请确认充值信息");
//        builder.setPositiveButton("确定",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(final DialogInterface dialog, int index) {
//                        //传递到平台
//                        CreateOrderBean createOrderBean=new CreateOrderBean();
//                        createOrderBean.setCpOrderId((String) payMap.get("gorder"));
//                        createOrderBean.setGoodsId((String) payMap.get("productID"));
//                        createOrderBean.setGoodsName((String) payMap.get("productName"));
//                        createOrderBean.setMoney(String.valueOf(payMap.get("money")));
//                        createOrderBean.setRole((String) payMap.get("roleName"));
//                        createOrderBean.setServer((String) payMap.get("serverName"));
//                        PayManager.getCreateOrder(context,createOrderBean);
//                        payOnSuccess(payCallBackListener);
//                    }
//                });
//        builder.setNegativeButton("取消",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int index) {
//                        OnCancel(payCallBackListener);
//                    }
//                });
//        builder.create().show();

    }


    private void showLoginView(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("是否登录?");
        builder.setTitle("登录界面");
        builder.setPositiveButton("登录",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        LoginBean loginBean=new LoginBean();
                        loginBean.setUid("617");
                        loginBean.setToken("testtoken");
                        PayManager.getActivityLogin1(loginBean, new LoginInterface() {
                            @Override
                            public void setUID(String uid) {
                                loginOnSuccess(uid,loginCallBackListener);
                            }

                            @Override
                            public void Closed(String msg) {
                                loginOnFail(msg,loginCallBackListener);
                            }
                        });
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        loginOnFail("channel login fail",loginCallBackListener);
                    }
                });
        builder.create().show();

    }

    @Override
    public void exit(Context context, CallBackListener exitCallBackLister) {
        LogUtils.d(TAG,getClass().getSimpleName() + " exit");
        channelNotExitDialog(exitCallBackLister);
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(context, requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode==400){
                if (data.getIntExtra("code",201)==200){
                    payOnSuccess(payCallBackListener);
                }else {
                    payOnFailure(payCallBackListener);
                }
            }
        }
    }
}