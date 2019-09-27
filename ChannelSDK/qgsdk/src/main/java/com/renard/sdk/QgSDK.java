package com.renard.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.kding.api.QiGuoApi;
import com.kding.api.QiGuoCallBack;
import com.renard.common.Interface.CallBackListener;
import com.renard.common.config.TypeConfig;
import com.renard.common.parse.channel.Channel;
import com.renard.common.utils.log.LogUtils;
import com.renard.paysdkss.ConfigUtils;
import com.renard.paysdkss.MD5Util;
import com.renard.paysdkss.PayManager;
import com.renard.paysdkss.bean.CreateBackBean;
import com.renard.paysdkss.bean.CreateOrderBean;
import com.renard.paysdkss.bean.LoginBean;
import com.renard.paysdkss.listener.CreatOrderInterface;
import com.renard.paysdkss.listener.LoginInterface;
import com.renard.paysdkss.ui.PayActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class QgSDK extends Channel {

    private final String TAG = getClass().getSimpleName();
    private CallBackListener loginCallBackListeners;
    private CallBackListener payCallBackListeners;

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
                return false;

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

        QiGuoApi.INSTANCE.initSdk((Activity) context, "20190517-HNTYYgcsg", new QiGuoCallBack() {

            @Override
            public void onSuccess() {
                Log.e("init ", " init  suc");
                Toast.makeText(context, "初始化成功", Toast.LENGTH_SHORT).show();
                initOnSuccess(initCallBackListener);
            }

            @Override
            public void onFailure(String errorMsg) {
                Log.e("init ", errorMsg);
            }

            @Override
            public void onCancel() {
                Log.e("init ", "取消");
            }
        });
        PayManager.getInit("248", "585","d32b4f01cf53784f94e055a9707403df");
    }

    @Override
    public void login(Context context, HashMap<String, Object> loginMap, CallBackListener loginCallBackListener) {
        LogUtils.d(TAG, getClass().getSimpleName() + " login");
        if (ConfigUtils.isIssueLogin()) {
            LoginBean loginBean = new LoginBean();
            PayManager.getLogin((Activity) context, loginBean);
        } else {
            QiGuoApi.INSTANCE.showLogin(new QiGuoCallBack() {
                @Override
                public void onSuccess() {
//                    String userId = QiGuoApi.INSTANCE.getUserId();
//                    Boolean isAuthenticated = QiGuoApi.INSTANCE.isAuthenticated();
                    String birthday = QiGuoApi.INSTANCE.getBirthday();
//                    Log.e("login ", " login  suc" + "   userId = " + userId);
//                    Log.e("login ", " login  suc" + "   isAuthenticated = " + isAuthenticated);
//                    Log.e("login ", " login  suc" + "   birthday = " + birthday);
                    //  loginLabel.setText("登陆成功，当前账号uid为 "+userId);

                    String uid = QiGuoApi.INSTANCE.getUserId();// 登录用户id

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm E");
                    Date dt = new Date();
                    String str_time = sdf.format(dt);

                    String md5 = MD5Util.getMD5(QiGuoApi.INSTANCE.getBirthday() + "20190517-HNTYYgcsg" + str_time);

                    PayManager.getActivityLogin(uid, birthday, "20190517-HNTYYgcsg", str_time, md5, new LoginInterface() {
                        @Override
                        public void setUID(String uid) {
                            loginOnSuccess(uid, loginCallBackListeners);
                        }

                        @Override
                        public void Closed(String msg) {
                            loginOnFail(msg,loginCallBackListeners);
                        }
                    });

//                    String token = "";
////                    LoginBean loginBean=new LoginBean();
////                    loginBean.setToken(token);
////                    loginBean.setUid(uid);
////                    PayManager.getActivityLogin1(loginBean, new LoginInterface() {
////                        @Override
////                        public void setUID(String uid) {
////                            loginOnSuccess(uid,loginCallBackListeners);
////                        }
////                    });

                }

                @Override
                public void onFailure(String errorMsg) {
                    Log.e("login ", errorMsg);
                    //   loginLabel.setText("登陆失败 "+errorMsg);
                }

                @Override
                public void onCancel() {
                    Log.e("login ", "登陆取消");
                    //   loginLabel.setText("登陆取消");
                }
            });
            this.loginCallBackListeners = loginCallBackListener;
        }
    }

    @Override
    public void switchAccount(Context context, CallBackListener changeAccountCallBackLister) {

    }

    @Override
    public void logout(Context context, CallBackListener logoutCallBackLister) {

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
                        Log.e("sssss", payMap.toString());
                        QiGuoApi.INSTANCE.pay((String) payMap.get("productName").toString(), (String) payMap.get("productDesc").toString(), (String) payMap.get("money").toString(), (String) payMap.get("gorder"), new QiGuoCallBack() {
                            @Override
                            public void onSuccess() {
                                Log.e("pay ", " pay  suc");
                                payOnSuccess(payCallBackListener);
                            }

                            @Override
                            public void onFailure(String errorMsg) {
                                Log.e("pay ", " pay  fail  " + errorMsg);
                                payOnFailure(payCallBackListener);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    }
            });
        }
        this.payCallBackListeners = payCallBackListener;
    }

    @Override
    public void exit(Context context, CallBackListener exitCallBackLister) {
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
                    payOnSuccess(payCallBackListeners);
                } else {
                    payOnFailure(payCallBackListeners);
                }
            }
        }
    }

    @Override
    public void onResume(Context context) {
        super.onResume(context);
        QiGuoApi.INSTANCE.onResume();
    }

    @Override
    public void onPause(Context context) {
        super.onPause(context);
        QiGuoApi.INSTANCE.onPause();
    }

}
