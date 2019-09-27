package com.renard.sdk;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.android.game.paymanager.PayListener;
import com.android.game.paymanager.ReqPayManager;
import com.android.tcyw.login.LoginListener;
import com.android.tcyw.login.TcManager;
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

public class TcSdk extends Channel {
    private final String TAG = getClass().getSimpleName();
    private CallBackListener loginCallBackListeners;
    private CallBackListener payCallBackListener;
    private CallBackListener switchAccountCallBackLister;
    private TcManager cmanager;

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
//        // 2.初始化方法
//        初始化接口，gameId游戏id, channelId为渠道号, packageId 包的id, signkey为签名秘钥, trackingkey为热云key(不需可不填)
        cmanager = TcManager.getInstance(context, 1182, 1, 4000001, "Kjgr9hOts6JjonC0opJebaoIy6377SahBQ9bxo4q", " ");

        //初始化发行SDK验证判断
        PayManager.getInit("168", "585", "d32b4f01cf53784f94e055a9707403df");
        initOnSuccess(initCallBackListener);
    }

    @Override
    public void login(final Context context, final HashMap<String, Object> loginMap, CallBackListener loginCallBackListener) {
        LogUtils.d(TAG, getClass().getSimpleName() + " login");
        if (ConfigUtils.isIssueLogin()) {
            LoginBean loginBean = new LoginBean();
            PayManager.getLogin((Activity) context, loginBean);
        } else {
            cmanager.show(2, new LoginListener() {
                @Override
                public void login_msg(int i, String s, String s1) {
//                    i为sessionid,s为登录账号
                    if (i != 0) {
                        String uid = String.valueOf(i);// 登录用户id
                        String token = s;
                        LoginBean loginBean = new LoginBean();
                        loginBean.setToken(token);
                        loginBean.setUid(uid);
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
                    }
                }
            });
            this.loginCallBackListeners = loginCallBackListener;
        }
    }

    @Override
    public void switchAccount(Context context, CallBackListener switchAccountCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " switchAccount");
        this.switchAccountCallBackLister = switchAccountCallBackLister;
    }

    @Override
    public void logout(Context context, CallBackListener logoutCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " logout");
        //    MCApiFactory.getMCApi().loginout((Activity) context);
        //this.loginCallBackListeners = logoutCallBackLister;
    }

    @Override
    public void pay(final Context context, final HashMap<String, Object> payMap, final CallBackListener payCallBackListener) {
        //传递到平台
        CreateOrderBean createOrderBean = new CreateOrderBean();
        createOrderBean.setCpOrderId((String) payMap.get("gorder"));
        createOrderBean.setGoodsId((String) payMap.get("productID"));
        createOrderBean.setGoodsName((String) payMap.get("productName"));
        createOrderBean.setMoney(String.valueOf(payMap.get("money")));
        createOrderBean.setRole((String) payMap.get("roleName"));
        createOrderBean.setServer((String) payMap.get("serverName"));
        createOrderBean.setExt(String.valueOf(payMap.get("extraInfo")));
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
                    //AppId微信支付需要的AppID(ps:现在支付为null)，
                    // mcode 需要生产订单的金额编号，
                    // sum对应的金额数，
                    // gcid游戏订单编号，
                    // gname游戏角色名称，
                    // glv游戏角色等级，
                    // grid游戏角色id，
                    // order_type订单类型,
                    // extend_params透传参数(注:订单类型与透传参数不为必填)
                    ReqPayManager.getInstance(context).sendReq(
                            "com.renard.hjygamewd",
                            String.valueOf(payMap.get("money")),
                            Double.parseDouble(String.valueOf(payMap.get("money"))),
                            (String) payMap.get("gorder"),
                            (String) payMap.get("roleName"),
                            Integer.parseInt(String.valueOf(payMap.get("roleLevel"))),
                            (String) payMap.get("roleID"),
                            " ",
                            (String) payMap.get("extraInfo"), new PayListener() {
                                @Override
                                public void payResult(int i, String s) {
                                    if (i == 1) {
                                        Toast.makeText(context, "支付成功", Toast.LENGTH_LONG).show();
                                        payOnSuccess(payCallBackListener);
                                    } else {
                                        Toast.makeText(context, "支付失败", Toast.LENGTH_LONG).show();
                                        payOnFailure(payCallBackListener);
                                    }
                                }
                            });
                }
            });
        }
        this.payCallBackListener = payCallBackListener;
    }


    @Override
    public void exit(final Context context, CallBackListener exitCallBackLister) {
        LogUtils.d(TAG, getClass().getSimpleName() + " exit");
        channelNotExitDialog(exitCallBackLister);
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
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
    public void onResume(Context context) {
        super.onResume(context);

    }

    @Override
    public void onPause(Context context) {
        super.onPause(context);

    }

    @Override
    public void onStop(Context context) {
        super.onStop(context);

    }
}
