package com.renard.hjysdkapi.api;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;


import com.renard.common.Interface.CallBackListener;
import com.renard.common.config.TypeConfig;
import com.renard.common.google.gson.Gson;
import com.renard.common.google.gson.JsonObject;
import com.renard.common.parse.project.Project;
import com.renard.common.parse.project.ProjectManager;
import com.renard.common.utils.ErrorCode;
import com.renard.common.utils.ObjectUtils;
import com.renard.common.utils.log.LogUtils;
import com.renard.hjysdkapi.bean.info.account.AccountEventResultInfo;
import com.renard.hjysdkapi.bean.info.ConfigInfo;
import com.renard.hjysdkapi.bean.info.account.PlayerInfo;
import com.renard.account.bean.AccountBean;
import com.renard.account.bean.AccountCallBackBean;
import com.renard.hjysdkapi.bean.info.pay.GameRoleParams;
import com.renard.hjysdkapi.bean.info.pay.PayParams;
import com.renard.hjysdkapi.listener.AccountCallBackLister;
import com.renard.hjysdkapi.listener.ExitCallBackLister;
import com.renard.hjysdkapi.listener.InitCallBackLister;
import com.renard.hjysdkapi.listener.PurchaseCallBackListener;
import com.renard.purchase.PurchaseResult;

import java.util.HashMap;

/**
 * Created by Riven_rabbit on 2019/4/24
 *
 * @author Lemon酱
 */
public class SDKAPI {


    private final String TAG = getClass().getSimpleName();

    private volatile static SDKAPI INSTANCE;

    private SDKAPI() {
    }

    public static SDKAPI getInstance() {
        if (INSTANCE == null) {
            synchronized (SDKAPI.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SDKAPI();
                }
            }
        }
        return INSTANCE;
    }

    //    private Project project = ProjectManager.getInstance().getProject("custom_project"); //获取实际的项目对象
//    private Project project = ProjectManager.getInstance().getProject("juhe_project"); //获取实际的项目对象
    private Project project = ProjectManager.getInstance().getProject("project"); //获取实际的项目对象

    /**-----------------------------------生命周期接口--------------------------------------**/

    public void onCreate(Activity activity, Bundle savedInstanceState) {
        project.onCreate(activity,savedInstanceState);
    }

    public void onStart(Activity activity) {
        project.onStart(activity);
    }

    public void onResume(Activity activity) {
        project.onResume(activity);
    }

    public void onPause(Activity activity) {
        project.onPause(activity);
    }

    public void onStop(Activity activity) {
        project.onStop(activity);
    }

    public void onRestart(Activity activity) {
        project.onRestart(activity);
    }

    public void onDestroy(Activity activity) {
        project.onDestroy(activity);
    }

    public void onNewIntent(Activity activity,Intent intent){
        project.onNewIntent(activity,intent);
    }

    public void onConfigurationChanged(Activity activity,Configuration configuration){
        project.onConfigurationChanged(activity,configuration);
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        project.onActivityResult(activity,requestCode,resultCode,data);
    }

    public void onRequestPermissionsResult(Activity activity,int requestCode, String[] permissions,int[] grantResults) {
        project.onRequestPermissionsResult(activity,requestCode,permissions,grantResults);
    }


    /**-----------------------------------SDK接口--------------------------------------**/


    /**
     * 初始化接口
     * @param activity 游戏activity
     * @param configInfo 游戏参数对象
     * @param accountCallBackLister 账号回调监听
     * @param initCallBackListener 初始化回调监听
     */
    public void init(final Activity activity, ConfigInfo configInfo, AccountCallBackLister accountCallBackLister,
                     final InitCallBackLister initCallBackListener){

        //检测是否已设置配置游戏必须参数
//        if (TextUtils.isEmpty(configInfo.gameid) || TextUtils.isEmpty(configInfo.gamekey)|| TextUtils.isEmpty(configInfo.paykey)){
//            Toast.makeText(activity,"param one of GameInfoSetting is null, please checks first",Toast.LENGTH_SHORT).show();
//            return;
//        }

        //检测是否已设置登录监听
        if (accountCallBackLister == null){
            Toast.makeText(activity,"AccountCallBackLister is null, please setAccountCallBackLister first",Toast.LENGTH_SHORT).show();
            return;
        }
        //设置账号监听回调
        mAccountCallCallBackLister = accountCallBackLister;

        project.setAccountCallBackLister(SDKAccountCallBackLister);
        project.init(activity,"","", new CallBackListener() {

            @Override
            public void onSuccess(Object o) {
                initCallBackListener.onSuccess();
            }

            @Override
            public void onFailure(int code, String msg) {
                initCallBackListener.onFailure(code,msg);
            }
        });
    }

    /***********************************************************************************************
     *
     *
     *  设置Account回调接口,避免渠道有些渠道登录是从浮窗或个人中心等入口接口回调回来，没法正确回调给CP
     *
     *
     **********************************************************************************************/
    private AccountCallBackLister mAccountCallCallBackLister;

    private void accountCallBack(int type, int statusCode, String msg, AccountBean loginInfo){

        AccountEventResultInfo accountResult = new AccountEventResultInfo();
        accountResult.setEventType(type);
        accountResult.setStatusCode(statusCode);
        accountResult.setMsg(msg);
        if (loginInfo != null){

            PlayerInfo playerInfo = new PlayerInfo();
            playerInfo.setPlayerId(loginInfo.getUserID());
            playerInfo.setToken(loginInfo.getUserToken());
            accountResult.setPlayerInfo(playerInfo);

        }else {
            accountResult.setPlayerInfo(new PlayerInfo());
        }

        Gson gson = new Gson();
        String jsonStr = gson.toJson(accountResult);
        if (mAccountCallCallBackLister != null){
            mAccountCallCallBackLister.onAccountEventCallBack(jsonStr);
        }
    }

    private void loginEventCallBack(int code, AccountBean loginInfo, String msg){

        if (code == ErrorCode.SUCCESS){
            accountCallBack(AccountCallBackLister.LOGIN_SUCCESS, ErrorCode.SUCCESS ,msg, loginInfo);

        }else if (code == ErrorCode.CANCEL){
            accountCallBack(AccountCallBackLister.LOGIN_CANCEL, code, msg, null);

        }else if (code == ErrorCode.CHANNEL_LOGIN_CLOSE){
            accountCallBack(AccountCallBackLister.LOGIN_FAILURE, code, msg, null);

        } else {
            accountCallBack(AccountCallBackLister.LOGIN_FAILURE, code, msg, null);
        }
    }

    private void switchEventCallBack(int code, AccountBean loginInfo, String msg){

        if (code == ErrorCode.SUCCESS){
            accountCallBack(AccountCallBackLister.SWITCH_ACCOUNT_SUCCESS, ErrorCode.SUCCESS, msg,loginInfo);

        }else if (code == ErrorCode.CANCEL){
            accountCallBack(AccountCallBackLister.SWITCH_ACCOUNT_CANCEL, code, msg, null);

        } else {
            accountCallBack(AccountCallBackLister.SWITCH_ACCOUNT_FAILURE,code, msg, null);
        }
    }

    private void logoutEventCallBack(int code, String msg){

        if (code == ErrorCode.SUCCESS){
            accountCallBack(AccountCallBackLister.LOGOUT_SUCCESS, ErrorCode.SUCCESS, msg, null);

        }else if (code == ErrorCode.CANCEL){
            accountCallBack(AccountCallBackLister.LOGOUT_CANCEL, code, msg, null);

        } else {
            accountCallBack(AccountCallBackLister.LOGOUT_FAILURE, code, msg , null);
        }
    }

    /**
     * 将Project层的结果回调到这里
     */
    private CallBackListener SDKAccountCallBackLister = new CallBackListener<AccountCallBackBean>() {

        /**
         * 事件结果都回调到这里来,包括失败，取消的
         */
        @Override
        public void onSuccess(AccountCallBackBean callBackBean) {

            int event = callBackBean.getEvent();
            int code = callBackBean.getErrorCode();
            AccountBean loginInfo = callBackBean.getAccountBean();
            String msg = callBackBean.getMsg();
            switch (event){
                case TypeConfig.LOGIN:
                    loginEventCallBack(code,loginInfo,msg);
                    break;

                case TypeConfig.SWITCHACCOUNT:
                    switchEventCallBack(code,loginInfo,msg);
                    break;

                case TypeConfig.LOGOUT:
                    logoutEventCallBack(code,msg);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onFailure(int code, String msg) {
            //不会走到这里来
        }
    };


    /**
     * 账号登录
     * @param activity 游戏activity
     */
    public void login(Activity activity){
        project.login(activity, null);
    }


    /**
     * 切换账号
     * @param activity 游戏activity
     */
    public void switchAccount(Activity activity){
        project.switchAccount(activity);
    }

    /**
     * 账号注销
     * @param activity 游戏activity
     */
    public void logout(Activity activity){
        project.logout(activity);
    }


    /**
     * 上报数据信息
     * @param activity 游戏activity
     * @param gameRoleParams 游戏参数实体
     */
    public void submitRoleInfo(Activity activity, GameRoleParams gameRoleParams){

        //将实体对象装换为Map集合，方便后续自动扩展
        HashMap<String,Object> reportData = ObjectUtils.objectToMap(gameRoleParams);
        LogUtils.d(TAG,reportData.toString());
        project.reportData(activity,reportData);
    }


    /**
     * 购买
     * @param activity 游戏activity
     * @param params 购买参数
     * @param purchaseCallBackListener 购买回调
     */
    public void pay(Activity activity, PayParams params, final PurchaseCallBackListener purchaseCallBackListener){

        //将实体对象装换为Map集合，方便后续自动扩展
        HashMap<String,Object> payParams = ObjectUtils.objectToMap(params);
        project.pay(activity, payParams, new CallBackListener<PurchaseResult>() {

            @Override
            public void onSuccess(PurchaseResult purchaseResult) {

                int type = purchaseResult.status;
                if (type == PurchaseResult.OrderState){ //创建订单成功回调

                    JsonObject object = (JsonObject) purchaseResult.message;
                    String orderID = object.get("orderId").getAsString();
                    purchaseCallBackListener.onOrderId(orderID);
                }

                if(type == PurchaseResult.PurchaseState){//支付成功回调
                    purchaseCallBackListener.onSuccess();
                }

            }

            @Override
            public void onFailure(int code, String msg) {

                if (purchaseCallBackListener != null){
                    if (code == ErrorCode.CANCEL){
                        purchaseCallBackListener.onCancel();

                    }else if (code == ErrorCode.NO_PAY_RESULT){

                        purchaseCallBackListener.onComplete();

                    }else {

                        purchaseCallBackListener.onFailure(code,msg);
                    }
                }
            }
        });

    }

    /**
     * 退出接口
     * @param activity 游戏activity
     * @param exitCallBackLister 退出回调
     */
    public void exit(Activity activity, final ExitCallBackLister exitCallBackLister){

        project.exit(activity, new CallBackListener() {

            @Override
            public void onSuccess(Object object) {

                //存在渠道退出框,并且点击退出成功
                exitCallBackLister.onExitDialogSuccess();
            }

            @Override
            public void onFailure(int code, String msg) {

                if (code == ErrorCode.CANCEL){
                    exitCallBackLister.onExitDialogCancel();

                }else if (code == ErrorCode.NO_EXIT_DIALOG){
                    exitCallBackLister.onNotExitDialog();//不存在渠道退出框

                } else{ //其他错误的时候,默认是没有回调框的

                    exitCallBackLister.onNotExitDialog();//不存在渠道退出框
                }

            }
        });

    }

    /**
     * 显示浮窗
     * @param activity
     */
    public void showFloatWindow(Activity activity){
        project.showFloatView(activity);
    }

    /**
     * 隐藏浮窗
     */
    public void dismissFloatView(Activity activity){
        project.dismissFloatView(activity);
    }




    public String getChannelId(){
        return project.getChannelID();
    }


}
