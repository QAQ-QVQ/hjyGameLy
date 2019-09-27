package com.renard.sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.renard.common.Interface.CallBackListener;
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
import com.tygrm.sdk.TYRT;
import com.tygrm.sdk.bean.TYRLoginBean;
import com.tygrm.sdk.bean.TYRPayBean;
import com.tygrm.sdk.bean.TYRPayGameParams;
import com.tygrm.sdk.bean.TYRUploadInfo;
import com.tygrm.sdk.cb.ITYRBackCallBack;
import com.tygrm.sdk.cb.TYRSDKListener;
import com.tygrm.sdk.constan.TYRCode;
import com.tygrm.sdk.constan.UserUploadType;
import com.tygrm.sdk.core.TYRSDK;

import java.util.HashMap;


/**
 * Created by Riven_rabbit on 2019/4/27
 *
 * @author Lemon酱
 */
public class TyySDK extends Channel {
    private final String TAG = getClass().getSimpleName();

    private CallBackListener loginCallBackListener;
    private CallBackListener payCallBackListener;
    private CallBackListener switchAccountCallBackLister;

    @Override
    public void onCreate(final Context context, Bundle savedInstanceState) {
        super.onCreate(context, savedInstanceState);
    }

    @Override
    public void onResume(Context context) {
        TYRSDK.getInstance().onResume((Activity) context);
        super.onResume(context);
    }

    @Override
    public void onPause(Context context) {
        TYRSDK.getInstance().onPause((Activity) context);
        super.onPause(context);
    }

    @Override
    public void onStop(Context context) {
        TYRSDK.getInstance().onStop((Activity) context);
        super.onStop(context);
    }

    @Override
    public void onStart(Context context) {
        TYRSDK.getInstance().onCreate((Activity) context);
        TYRSDK.getInstance().onStart((Activity) context);
        super.onStart(context);
    }

    @Override
    protected void initChannel() {

    }

    @Override
    public String getChannelID() {
        return null;
    }

    @Override
    public boolean isSupport(int FuncType) {
        return false;
    }

    @Override
    public void init(final Context context, HashMap<String, Object> initMap, final CallBackListener initCallBackListener) {
        TYRSDK.getInstance().TYRInit((Activity) context, new TYRSDKListener() {
            @Override
            public void onSwitchAccount(TYRLoginBean data ) {
                //data 如果为null,则说明sdk调用了注销,此时游戏应该清空登录信息,并重新唤起登录
//data 如果不为null,则说明sdk内部调用了注销,并重新登录成功,需返回游戏区服界面，重新登录认证，重新加载新用户对应的角色数据
                LogUtils.i("loginout", "loginout");
                switchAccountOnSuccess(switchAccountCallBackLister);
            }
            @Override
            public void onInitChange(int code, String msg) {
                switch (code) {
                    case TYRCode.CODE_INIT_SUCCESS:
                      //  TYRT.show(context, "初始化成功" + msg);
                        break;
                    case TYRCode.CODE_INIT_FAIL:
                    //    TYRT.show(context, "初始化失败" + msg);
                        break;
                }
            }
            @Override
            public void onLoginChange(int code, TYRLoginBean tyrLoginBean) {
                switch (code) {
                    case TYRCode.CODE_LOGIN_SUCCESS:
                        TYRT.show(context, "登录成功");
                        LoginBean loginBean=new LoginBean();
                        loginBean.setToken(tyrLoginBean.getToken());
                        loginBean.setUid(tyrLoginBean.getsID());
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
                        break;
                    case TYRCode.CODE_LOGIN_FAIL:
                        //注意:失败时 data 可能是 null
                        TYRT.show(context, "登录失败" );
                        break;
                }
            }
            @Override
            public void onPayResult(int code, TYRPayBean tyrPayBean) {
                String s = "";
                if (tyrPayBean != null) {
                    s = tyrPayBean.getPayID() + "          " + tyrPayBean.getMoney();
                }
                switch (code) {
                    case TYRCode.CODE_PAY_SUCCESS:
                        TYRT.show(context, "支付成功" + s);
                        payOnSuccess(payCallBackListener);
                        break;
                    case TYRCode.CODE_PAY_FAIL:
                        String msg = tyrPayBean.getMsg();
                        TYRT.show(context, "支付失败"+msg );
                        payOnFailure(payCallBackListener);
                        break;
                    case TYRCode.CODE_PAY_CANCEL:
                        TYRT.show(context, "支付取消");
                        payOnFailure(payCallBackListener);
                        break;
                    case TYRCode.CODE_PAY_UNKNOWN:
                        TYRT.show(context, "未知错误");
                        payOnFailure(payCallBackListener);
                        break;
                }
            }
            @Override
            public void onLogout() {
                //SDK 主动登出触发该回调，游戏调用 logout()接口不会收到该回调
                //用户登出回调（需要收到该回调需要返回游戏登录界面，并调用 login 接口，打开 SDK 登录界面）

            }
        });
        //初始化发行SDK验证判断

        PayManager.getInit("97","729","e9d0933bc19b9a28229320ab01eb40e7");
    }

    @Override
    public void login(Context context, HashMap<String, Object> loginMap, CallBackListener loginCallBackListener) {
        if (ConfigUtils.isIssueLogin()){
            LoginBean loginBean=new LoginBean();
            PayManager.getLogin((Activity)context,loginBean);
        }else {
            TYRSDK.getInstance().login();
        }
        this.loginCallBackListener = loginCallBackListener;
    }

    @Override
    public void switchAccount(Context context, CallBackListener switchAccountCallBackLister) {
        this.switchAccountCallBackLister=switchAccountCallBackLister;
    }

    @Override
    public void logout(Context context, CallBackListener logoutCallBackLister) {
        TYRSDK.getInstance().setGameLogOut(null);
    }

    @Override
    public void reportData(Context context, HashMap<String, Object> dataMap) {
        TYRUploadInfo info = new TYRUploadInfo();
        info.setRoleid((String) dataMap.get("roleId"));              //角色id
        info.setRolename((String) dataMap.get("roleName"));           //角色名称
        info.setRolelevel((String) dataMap.get("roleLevel"));           //角色等级
        info.setZoneid((String) dataMap.get("serverId"));             //区服id
        info.setZonename((String) dataMap.get("serverName"));       //区服名
        info.setBalance((String) dataMap.get("roleBalance"));           //帐户余额
        info.setVip((String) dataMap.get("roleVipLevel"));                  //vip等级
        info.setPartyname("无帮派");        //帮派信息  如果没有帮派信息 则传入-->无帮派
        info.setAttach("原始数据");         //cp传入android 端的所有数据 如果是json 应该转义
//        UserUploadType.UNDEFINED,//未定义
//                JOIN_SERVER, //进入服务器
//                CREATE_ROLE, //创建角色
//                UPGRADE, //角色升级
//                FACTION, //加入公会 或者帮派
//                EXIT,        //退出游戏
//                OTHER       //其他情况
        info.setType(UserUploadType.CREATE_ROLE);
        TYRSDK.getInstance().reportedRoleInfo(info);
        //传递到平台
        UploadBean uploadBean=new UploadBean();
        uploadBean.setIsCreateRole(String.valueOf(dataMap.get("create")));
        uploadBean.setGameId("676");
        uploadBean.setServerId((String) dataMap.get("serverId"));
        uploadBean.setServerName((String) dataMap.get("serverName"));
        uploadBean.setUserRoleId((String) dataMap.get("roleId"));
        uploadBean.setUserRoleName((String) dataMap.get("roleName"));
        uploadBean.setVipLevel((String) dataMap.get("roleVipLevel"));
        PayManager.getUpload(uploadBean);
    }
    @Override
    public void pay(Context context, final HashMap<String, Object> payMap, CallBackListener payCallBackListener) {

        //传递到平台
        CreateOrderBean createOrderBean=new CreateOrderBean();
        createOrderBean.setCpOrderId((String) payMap.get("gorder"));
        createOrderBean.setGoodsId((String) payMap.get("productID"));
        createOrderBean.setGoodsName((String) payMap.get("productName"));
        createOrderBean.setMoney(String.valueOf(Integer.parseInt(String.valueOf(payMap.get("money")))/100));
        createOrderBean.setRole((String) payMap.get("roleName"));
        createOrderBean.setServer((String) payMap.get("serverName"));
        createOrderBean.setExt(String.valueOf(payMap.get("extraInfo")));

     //   PayManager.getCreateOrder((Activity)context,createOrderBean);
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
                    TYRPayGameParams params = new TYRPayGameParams();
                    params.setRoleid((String) payMap.get("roleID"));                //角色id
                    params.setCporder_sn((String) payMap.get("gorder"));  //游戏生成的订单编号
                    params.setAmount(Double.parseDouble(String.valueOf(payMap.get("money")))/100); //购买物品的总价   单位为元
                    params.setProduct_name((String) payMap.get("productName"));          //购买物品的名称
                    params.setBuyNum(String.valueOf(payMap.get("buyNum")));                    //购买的数量
                    params.setCoinNum(String.valueOf(payMap.get("coinNum")));               //当前玩家身上拥有的游戏币数量
                    params.setProduct_id((String) payMap.get("productID"));            //购买物品的id
                    params.setProduct_des((String) payMap.get("productDesc"));  //购买物品的描述
                    params.setRoleLevel((String) payMap.get("roleLevel"));               //玩家等级
                    params.setRolename((String) payMap.get("roleName"));              //玩家名字
                    params.setServerId((String) payMap.get("serverID"));           //服务器id
                    params.setServerName((String) payMap.get("serverName"));        //服务器名
                    params.setVip((String) payMap.get("roleVipLevel"));                     //角色vip等级
                    TYRSDK.getInstance().pay(params);
                }
            });
        }
        this.payCallBackListener=payCallBackListener;
    }

    @Override
    public void exit(final Context context, final CallBackListener exitCallBackLister) {
        TYRSDK.getInstance().setGameLogOut(null);
      //  TYRSDK.getInstance().exit((Activity) context);
        channelNotExitDialog(exitCallBackLister);
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("确定要退出吗").setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                exitCallBackLister.onSuccess(200);
//            }
//        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        }).show();

//        TYRSDK.getInstance().onKeyDown((Activity) context, new ITYRBackCallBack() {
//            @Override
//            public void onChannelExcit() {
//                //当玩家点击确认退出
//                System.exit(0);
//                TYRSDK.getInstance().setGameLogOut(null);
//            }
//            @Override
//            public void onGameExcit() {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("确定要退出吗").setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
////                       System.exit(0);
//                    }
//                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
//            }
//        });

    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        TYRSDK.getInstance().onActivityResult(requestCode, resultCode, data, (Activity) context);
        super.onActivityResult(context, requestCode, resultCode, data);
        if (requestCode == 1000) {   //登录1000，300   支付1001，400
            if (resultCode==300){
                if (data.getStringExtra("uid")!=null){
                    String uid=data.getStringExtra("uid");
                    loginOnSuccess(uid,loginCallBackListener);
                }else {
                    loginOnFail("登录失败",loginCallBackListener);
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
    public void onNewIntent(Context context, Intent intent) {
        TYRSDK.getInstance().onNewIntent(intent, (Activity) context);
        super.onNewIntent(context, intent);
    }

    @Override
    public void onDestroy(Context context) {
        TYRSDK.getInstance().onDestroy((Activity) context);
        super.onDestroy(context);
    }

    @Override
    public void onRestart(Context context) {
        TYRSDK.getInstance().onRestart((Activity) context);
        super.onRestart(context);
    }

    @Override
    public void onConfigurationChanged(Context context, Configuration configuration) {
        TYRSDK.getInstance().onConfigurationChanged(configuration, (Activity) context);
        super.onConfigurationChanged(context, configuration);
    }

    @Override
    public void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        TYRSDK.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults, (Activity) context);
    }
}
