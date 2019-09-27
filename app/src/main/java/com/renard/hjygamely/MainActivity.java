package com.renard.hjygamely;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.renard.common.utils.log.LogUtils;
import com.renard.hjysdkapi.api.SDKAPI;
import com.renard.hjysdkapi.bean.info.ConfigInfo;
import com.renard.hjysdkapi.bean.info.pay.GameRoleParams;
import com.renard.hjysdkapi.bean.info.pay.PayParams;
import com.renard.hjysdkapi.listener.AccountCallBackLister;
import com.renard.hjysdkapi.listener.ExitCallBackLister;
import com.renard.hjysdkapi.listener.InitCallBackLister;
import com.renard.hjysdkapi.listener.PurchaseCallBackListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class MainActivity extends Activity implements View.OnClickListener {
    private String TAG = getClass().getName();
    private EditText etPay,etReport;
    private TextView tvLog;
    private int pri = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initSDK();
    }

    private void initView() {
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_switchAccount).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
        findViewById(R.id.btn_upload).setOnClickListener(this);
        findViewById(R.id.btn_pay).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);

        etPay =  findViewById(R.id.et_inputPrice);
        tvLog =  findViewById(R.id.tvLog);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_switchAccount:
                switchAccount();
                break;
            case R.id.btn_logout:
                logout();
                break;
            case R.id.btn_pay:
                purchase();
                break;
            case R.id.btn_upload:
                uploadrole();
                break;
            case R.id.btn_exit:
                exitGame();
                break;
            default:
                break;
        }
    }


    /**
     * 初始化SDK
     */
    private void initSDK() {
        SDKAPI.getInstance().init(this, null,accountCallBackLister, new InitCallBackLister() {

            @Override
            public void onSuccess() {
//                showToast("初始化状态：初始化成功");
            }

            @Override
            public void onFailure(int code, String msg) {
                String message = "初始化状态"
                        +"\n错误码:\n" + code
                        +"\n错误信息:\n" + msg;
                tvLog.setText(message);
//                showToast(message);
            }
        });
    }


    /**
     * 账号登陆
     */
    private void login() {
        SDKAPI.getInstance().login(this);
    }

    /**
     * 切换账号
     */
    private void switchAccount(){
        SDKAPI.getInstance().switchAccount(this);
    }

    /**
     * 账号登出
     */
    private void logout() {
        SDKAPI.getInstance().logout(this);
    }


    private AccountCallBackLister accountCallBackLister = new AccountCallBackLister() {

        @Override
        public void onAccountEventCallBack(String jsonStr) {
            //    1000; //登录成功
            //    1001; //登录失败
            //    1002;  //登录取消
            //
            //    1003; //切换账号成功
            //    1004; //切换账号失败
            //    1005; //切换账号取消
            //
            //    1006; //注销账号成功
            //    1007; //注销账号失败
            //    1008; //注销账号取消
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                int code = jsonObject.getInt("eventType");
                LogUtils.e(TAG,jsonObject.toString());
                String json=jsonObject.getString("playerInfo");
                String uid;
                switch (code){
                    case 1000:
                        JSONObject jsonObject1 = new JSONObject(json);
                        uid=jsonObject1.getString("playerId");
                        break;
                    case 1003:

                        break;
                    case 1006:

                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    /**
     * 上传信息
     */
    private void uploadrole(){
        /**
         *  Update to create    判断当前是创建角色还是更新角色信息
         *  roleID              当前游戏内角色ID
         *  roleName            当前游戏内角色名称
         *  roleBalance         角色余额
         *  roleLevel           玩家等级
         *  roleCreateTime      创建时间
         *  roleVipLevel        Vip等级
         *  serverID            当前玩家所在的服务器ID
         *  serverName          当前玩家所在的服务器名称
         */
        GameRoleParams gameRoleParams=new GameRoleParams();
        gameRoleParams.setCreate(true);
        gameRoleParams.setRoleId("123");
        gameRoleParams.setRoleBalance("10");
        gameRoleParams.setRoleName("小明");
        gameRoleParams.setRoleLevel("1");
        gameRoleParams.setRoleCreateTime("1529550752");
        gameRoleParams.setRoleVipLevel("1");
        gameRoleParams.setServerId("1");
        gameRoleParams.setServerName("1");
        SDKAPI.getInstance().submitRoleInfo(this,gameRoleParams);
    }
    /**
     * 购买
     */
    private void purchase() {

        String pri_str = etPay.getText().toString();
        if (!isDecimal(pri_str) && !isInteger(pri_str)) {
            showToast("Please enter Correct values.");
            return;
        }

        try {
            pri = Integer.valueOf(pri_str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         *  productId			商品ID
         *  productName			商品名称
         *  productDesc			商品描述
         *
         *  money				总金额(以分为单位)
         *
         *  roleID              当前游戏内角色ID
         *  roleName            当前游戏内角色名称
         *  roleLevel           玩家等级
         *  roleVipLevel        VIP等级
         *  serverID            当前玩家所在的服务器ID
         *  serverName          当前玩家所在的服务器名称
         *
         *  notifyUrl           支付回调地址
         *  extraInfo			透传给 cp服务器的字段
         *  gorder              CP订单号
         *  buyNum              购买数量 没有默认1
         *  coinNum             单价
         */

        //商品信息
        PayParams payParams = new PayParams();
        payParams.setProductName("金币");
        payParams.setProductDesc("一金币等于十银币");
        payParams.setProductId("9000"); //SDK的商品ID

        //金额信息
        payParams.setMoney(pri);

        //玩家信息
        payParams.setRoleID("123");
        payParams.setRoleName("小明");
        payParams.setRoleLevel("15");
        payParams.setRoleVipLevel("3");
        payParams.setServerID("11");
        payParams.setServerName("ceshi");
        //支付配置信息
        payParams.setGorder(String.valueOf(System.currentTimeMillis()/1000));
        payParams.setExtraInfo("用于确认交易给玩家发送商品");
        payParams.setBuyNum(1);
        payParams.setCoinNum(100);
        SDKAPI.getInstance().pay(this, payParams,new PurchaseCallBackListener() {

            //创建SDK订单成功就返回，方便CP查询该笔订单状态
            @Override
            public void onOrderId(String orderId) {
                showToast(orderId);
            }

            @Override
            public void onSuccess() {
                String message = "支付成功";
                tvLog.setText(message);
//                showToast(message);
            }

            @Override
            public void onFailure(int code, String msg) {

                String message = "支付失败\n:\n错误:\n" + code;
                tvLog.setText(message);
//                showToast(message);

            }

            @Override
            public void onCancel() {
                String message = "支付取消\n:\n错误:\n" ;
                tvLog.setText(message);
//                showToast(message);
            }

            //支付完成，没有正确支付回调时，会返回该结果
            @Override
            public void onComplete() {
                String message = "支付完成\n" ;
                tvLog.setText(message);
                showToast(message);
            }

        });
    }



    //--------------------------------------------------退出接口---------------------------------------------

    private void exitGame(){

        SDKAPI.getInstance().exit(MainActivity.this,new ExitCallBackLister(){

            //存在退出框,并且点击退出成功
            @Override
            public void onExitDialogSuccess() {
                Log.d(TAG,"退出成功");
                SDKAPI.getInstance().onDestroy(MainActivity.this);
                System.exit(0);
            }

            //存在退出框,并且点击取消退出
            @Override
            public void onExitDialogCancel() {
                Log.d(TAG,"退出取消");
            }

            //不存在退出框，需要游戏自己实现退出框,然后调用SDK释放资源接口
            @Override
            public void onNotExitDialog() {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("退出游戏");
                builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SDKAPI.getInstance().onDestroy(MainActivity.this);
                        System.exit(0);
                    }
                });
                builder.show();

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                exitGame();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitGame();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // 浮点型判断 (Floating-point judgment)
    public static boolean isDecimal(String str) {
        if (str == null || "".equals(str))
            return false;
        java.util.regex.Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
        return pattern.matcher(str).matches();
    }

    // 整型判断 (Integer to determine)
    public static boolean isInteger(String str) {
        if (str == null)
            return false;
        Pattern pattern = Pattern.compile("[0-9]+");
        return pattern.matcher(str).matches();
    }

    //--------------------------------------------------生命周期接口---------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        SDKAPI.getInstance().onStart(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SDKAPI.getInstance().onResume(MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SDKAPI.getInstance().onPause(MainActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SDKAPI.getInstance().onStop(MainActivity.this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SDKAPI.getInstance().onRestart(MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SDKAPI.getInstance().onDestroy(MainActivity.this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SDKAPI.getInstance().onNewIntent(MainActivity.this,intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SDKAPI.getInstance().onActivityResult(MainActivity.this,requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SDKAPI.getInstance().onRequestPermissionsResult(MainActivity.this,requestCode, permissions, grantResults);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        SDKAPI.getInstance().onConfigurationChanged(MainActivity.this,configuration);
        super.onConfigurationChanged(configuration);
    }
}
