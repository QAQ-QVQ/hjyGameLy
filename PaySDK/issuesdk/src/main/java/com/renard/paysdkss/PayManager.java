package com.renard.paysdkss;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

import com.renard.alipay.AliPayPlugin;
import com.renard.alipay.pay.AliPayInterface;
import com.renard.common.utils.GsonUtils;
import com.renard.common.utils.HttpRequestUtil;
import com.renard.common.utils.URLString;
import com.renard.common.utils.log.LogUtils;
import com.renard.paysdkss.bean.CreateBackBean;
import com.renard.paysdkss.bean.CreateOrderBean;
import com.renard.paysdkss.bean.ExtraBean;
import com.renard.paysdkss.bean.GameBean;
import com.renard.paysdkss.bean.GameInitBean;
import com.renard.paysdkss.bean.LoginBean;
import com.renard.paysdkss.bean.PayBackBean;
import com.renard.paysdkss.bean.UploadBean;
import com.renard.paysdkss.listener.CreatOrderInterface;
import com.renard.paysdkss.listener.LoginInterface;
import com.renard.paysdkss.ui.LoginActivity;
import com.renard.paysdkss.ui.PayActivity;
import com.renard.wechat.utils.DialogWeb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//import com.renard.alipay.AliPayPlugin;
//import com.renard.alipay.pay.AliPayInterface;

/**
 * Created by Riven_rabbit on 2019/4/30
 *
 * @author Lemon酱
 */
public class PayManager {
    private static String uid;
    private static CreatOrderInterface CreatOrderInterface;
    private static LoginInterface LoginInterface;
    private static String aid, gid;
    private static String key;

    /**
     * 初始化
     */
    public static void getInit(String aids, String gids, String keys) {
        aid = aids;
        gid = gids;
        key = keys;
        HttpRequestUtil.okPostFormRequest(URLString.GameInit + "aid/" + aid + "/gid/" + gid + "/", "", null, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                GameInitBean initBean = GsonUtils.GsonToBean(result, GameInitBean.class);
                if (!initBean.getCustomerqq().isEmpty()) {
                    ConfigUtils.setQQ(initBean.getCustomerqq());
                }
                if (initBean.getLogin_status().equals("true")) {
                    //走联盟渠道
                    ConfigUtils.setIssueLogin(false);
                } else if (initBean.getLogin_status().equals("platform")) {
                    //走发行SDK
                    ConfigUtils.setIssueLogin(true);
                } else {
                    //游戏已关闭
                    System.exit(0);
                }
                if (initBean.getPay_status().equals("true")) {
                    //走联盟渠道
                    ConfigUtils.setIssuePay(false);
                } else if (initBean.getPay_status().equals("platform")) {
                    //走发行SDK
                    ConfigUtils.setIssuePay(true);
                } else {
                    //游戏已关闭
                }

            }

            @Override
            public void requestFailure(String request, IOException e) {

            }

            @Override
            public void requestNoConnect(String msg, String data) {

            }
        });
    }

    /**
     * 登录
     */
    public static void getLogin(Activity activity, LoginBean loginBean) {
        if (ConfigUtils.isIssueLogin()) {
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivityForResult(intent, 1000);
        } else {
            getActivityLogin(loginBean);
        }

    }

    /**
     * 上传角色信息
     */
    public static void getUpload(UploadBean uploadBean) {
        uploadBean.setUid(uid);
        getActivityUpload(uploadBean);
    }

    /**
     * 创建订单
     */
    public static void getCreateOrder(Activity activity, CreateOrderBean createOrderBean) {
        if (ConfigUtils.isIssuePay()) {
            Intent intent = new Intent(activity, PayActivity.class);
            intent.putExtra("creatorder", createOrderBean);
            activity.startActivityForResult(intent, 1001);
        } else {
            getActivityCreateOrder(createOrderBean);
        }
    }

    /**
     * 登录
     */
    public static void getActivityLogin(LoginBean loginBean) {
        Map<String, String> hashMap = new HashMap<>();
        if (!loginBean.getUid().isEmpty()) {
            hashMap.put("sid", loginBean.getUid());
        }
        if (!loginBean.getToken().isEmpty()) {
            hashMap.put("token", loginBean.getToken());
        }
        HttpRequestUtil.okPostFormRequest(URLString.Game + "aid/" + aid + "/gid/" + gid + "/" + "?ac=quick", "", hashMap, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                GameBean gameBean = GsonUtils.GsonToBean(result, GameBean.class);
                LogUtils.e("ssss",result);
                switch (gameBean.getCode()) {
                    case 200:
                        uid = gameBean.getData().getUid();
                        LoginInterface.setUID(uid);
                        break;
                    case 103:
                        LoginInterface.Closed("账号被封禁");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void requestFailure(String request, IOException e) {

            }

            @Override
            public void requestNoConnect(String msg, String data) {

            }
        });
    }

    /**
     * 测试登录
     */
    public static void getActivityLogin1(LoginBean loginBean, LoginInterface loginInterface) {
        LoginInterface = loginInterface;
        Map<String, String> hashMap = new HashMap<>();
        if (!loginBean.getUid().isEmpty()) {
            hashMap.put("sid", loginBean.getUid());
        }
        if (!loginBean.getToken().isEmpty()) {
            hashMap.put("token", loginBean.getToken());
        }
        HttpRequestUtil.okPostFormRequest(URLString.Game + "aid/" + aid + "/gid/" + gid + "/" + "?ac=quick", "", hashMap, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("ssss",result);
                GameBean gameBean = GsonUtils.GsonToBean(result, GameBean.class);
                switch (gameBean.getCode()) {
                    case 200:
                        uid = gameBean.getData().getUid();
                        LoginInterface.setUID(uid);
                        break;
                    case 103:
                        LoginInterface.Closed("账号被封禁");
                        break;
                    default:

                        break;
                }
            }

            @Override
            public void requestFailure(String request, IOException e) {

            }

            @Override
            public void requestNoConnect(String msg, String data) {

            }
        });
    }

    /**
     * 测试登录
     */
    public static void getActivityLogin1(String id,String pid,String seeionid, LoginInterface loginInterface) {
        LoginInterface = loginInterface;
        Map<String, String> hashMap = new HashMap<>();
        if (!id.isEmpty()) {
            hashMap.put("sid", id);
        }
        if (!pid.isEmpty()) {
            hashMap.put("id", pid);
        }
        if (!seeionid.isEmpty()) {
            hashMap.put("token", seeionid);
        }

        HttpRequestUtil.okPostFormRequest(URLString.Game + "aid/" + aid + "/gid/" + gid + "/" + "?ac=quick", "", hashMap, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("ssss",result);
                GameBean gameBean = GsonUtils.GsonToBean(result, GameBean.class);
                switch (gameBean.getCode()) {
                    case 200:
                        uid = gameBean.getData().getUid();
                        LoginInterface.setUID(uid);
                        break;
                    case 103:
                        LoginInterface.Closed("账号被封禁");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void requestFailure(String request, IOException e) {

            }

            @Override
            public void requestNoConnect(String msg, String data) {

            }
        });
    }

    public static void getActivityLogin(final String sid, String birthday, String appId, String time, String MD5, LoginInterface loginInterface){
        LoginInterface=loginInterface;
        Map<String, String> hashMap = new HashMap<>();
        if (!birthday.isEmpty()){
            hashMap.put("birthday", birthday);
        }
        if (!appId.isEmpty()){
            hashMap.put("appId", appId);
        }
        if (!time.isEmpty()){
            hashMap.put("time", time);
        }
        if (!MD5.isEmpty()){
            hashMap.put("MD5", MD5);
        }
        if (!sid.isEmpty()){
            hashMap.put("sid", sid);
        }
        HttpRequestUtil.okPostFormRequest(URLString.Game + "aid/" + aid + "/gid/" + gid + "/", "", hashMap, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("ssss",result);
                GameBean gameBean = GsonUtils.GsonToBean(result, GameBean.class);
                switch (gameBean.getCode()) {
                    case 200:
                        uid=gameBean.getData().getUid();
                        LoginInterface.setUID(sid);
                        break;
                    case 103:
                        LoginInterface.Closed("账号被封禁");
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void requestFailure(String request, IOException e) {

            }

            @Override
            public void requestNoConnect(String msg, String data) {

            }
        });
    }

    public static void getActivityLogin(final String gameId, String channel, String uids, String sid, String ext,String version, LoginInterface loginInterface){
        LoginInterface=loginInterface;
        Map<String, String> hashMap = new HashMap<>();
        if (!gameId.isEmpty()){
            hashMap.put("gameId", gameId);
        }
        if (!channel.isEmpty()){
            hashMap.put("channel", channel);
        }
        if (!uids.isEmpty()){
            hashMap.put("uid", uids);
        }
        if (!sid.isEmpty()){
            hashMap.put("sid", sid);
        }
        if (!ext.isEmpty()){
            hashMap.put("ext", ext);
        }
        if (!version.isEmpty()){
            hashMap.put("version", version);
        }
        HttpRequestUtil.okPostFormRequest(URLString.Game + "aid/" + aid + "/gid/" + gid + "/", "", hashMap, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                GameBean gameBean = GsonUtils.GsonToBean(result, GameBean.class);
                LogUtils.e("ssss",result);
                switch (gameBean.getCode()) {
                    case 200:
                        uid=gameBean.getData().getUid();
                        LoginInterface.setUID(uid);
                        break;
                    case 103:
                        LoginInterface.Closed("账号被封禁");
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void requestFailure(String request, IOException e) {

            }

            @Override
            public void requestNoConnect(String msg, String data) {

            }
        });
    }

    /**
     * 上传信息
     * 登录传true
     * 更新信息传false
     */
    private static void getActivityUpload(UploadBean uploadBean) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("isCreateRole", uploadBean.getIsCreateRole());
        hashMap.put("gameId", uploadBean.getGameId());
        hashMap.put("uid", uploadBean.getUid());
        hashMap.put("serverId", uploadBean.getServerId());
        hashMap.put("serverName", uploadBean.getServerName());
        hashMap.put("userRoleId", uploadBean.getUserRoleId());
        hashMap.put("userRoleName", uploadBean.getUserRoleName());
        hashMap.put("vipLevel", uploadBean.getVipLevel());
        hashMap.put("userRoleLevel", uploadBean.getUserRoleLevel());
        hashMap.put("rebornLevel", uploadBean.getRebornLevel());
        hashMap.put("gameRoleMoney", uploadBean.getGameRoleGender());
        hashMap.put("gameRoleGender", uploadBean.getGameRoleGender());
        hashMap.put("gameRolePower", uploadBean.getGameRolePower());
        hashMap.put("gameRoleOnline", uploadBean.getGameRoleOnline()
        );
        HttpRequestUtil.okPostFormRequest(URLString.uploadRole + "aid/" + aid + "/gid/" + gid + "/", "upload", hashMap, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {

            }

            @Override
            public void requestFailure(String request, IOException e) {

            }

            @Override
            public void requestNoConnect(String msg, String data) {

            }
        });
    }

    /**
     * 创建订单
     */
    public static void getActivityCreateOrder(CreateOrderBean createOrderBean) {
        String time = String.valueOf(System.currentTimeMillis() / 1000);//createOrderBean.getCpOrderId()
        String Strsign = "cpOrderId=" + createOrderBean.getCpOrderId() + "&gameId=" + gid +
                "&goodsId=" + createOrderBean.getGoodsId() + "&goodsName=" + createOrderBean.getGoodsName() + "&money=" +
                createOrderBean.getMoney() + "&role=" + createOrderBean.getRole() + "&server=" + createOrderBean.getServer() + "&time=" +
                time + "&uid=" + uid + "&key=" + key;
        String sign = MD5Util.getMD5(Strsign);
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("gameId", gid);
        hashMap.put("uid", uid);
        hashMap.put("time", time);
        hashMap.put("server", createOrderBean.getServer());
        hashMap.put("role", createOrderBean.getRole());
        hashMap.put("goodsId", createOrderBean.getGoodsId());
        hashMap.put("goodsName", createOrderBean.getGoodsName());
        hashMap.put("money", createOrderBean.getMoney());
        hashMap.put("cpOrderId", createOrderBean.getCpOrderId());
        hashMap.put("ext", createOrderBean.getExt());
        hashMap.put("sign", sign);
        hashMap.put("signType", "md5");
        HttpRequestUtil.okPostFormRequest(URLString.CreateOrder + "aid/" + aid + "/gid/" + gid + "/", "createOrder", hashMap, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("ssss",result);
                ExtraBean extraBean = GsonUtils.GsonToBean(result,ExtraBean.class);
                CreateBackBean createBackBean = GsonUtils.GsonToBean(result, CreateBackBean.class);
                if (createBackBean.getCode() == 200) {
                    CreatOrderInterface.CreatOrder(createBackBean);
                }
            }

            @Override
            public void requestFailure(String request, IOException e) {

            }

            @Override
            public void requestNoConnect(String msg, String data) {

            }
        });
    }


    public static void getActivityCreateOrder(CreateOrderBean createOrderBean, CreatOrderInterface creatOrderInterface) {
        CreatOrderInterface = creatOrderInterface;
        String time = String.valueOf(System.currentTimeMillis() / 1000);//createOrderBean.getCpOrderId()
        String Strsign = "cpOrderId=" + createOrderBean.getCpOrderId() + "&gameId=" + gid +
                "&goodsId=" + createOrderBean.getGoodsId() + "&goodsName=" + createOrderBean.getGoodsName() + "&money=" +
                createOrderBean.getMoney() + "&role=" + createOrderBean.getRole() + "&server=" + createOrderBean.getServer() + "&time=" +
                time + "&uid=" + uid + "&key=" + key;
        LogUtils.i(Strsign);
        String sign = MD5Util.getMD5(Strsign);
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("gameId", gid);
        hashMap.put("uid", uid);
        hashMap.put("time", time);
        hashMap.put("server", createOrderBean.getServer());
        hashMap.put("role", createOrderBean.getRole());
        hashMap.put("goodsId", createOrderBean.getGoodsId());
        hashMap.put("goodsName", createOrderBean.getGoodsName());
        hashMap.put("money", createOrderBean.getMoney());
        hashMap.put("cpOrderId", createOrderBean.getCpOrderId());
        hashMap.put("ext", createOrderBean.getExt());
        hashMap.put("sign", sign);
        hashMap.put("signType", "md5");
        LogUtils.e("getActivityCreateOrder",hashMap.toString());
        HttpRequestUtil.okPostFormRequest(URLString.CreateOrder + "aid/" + aid + "/gid/" + gid + "/", "createOrder", hashMap, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                LogUtils.e("ssss",result);
                CreateBackBean createBackBean = GsonUtils.GsonToBean(result, CreateBackBean.class);
                ExtraBean extraBean = GsonUtils.GsonToBean(result,ExtraBean.class);
                LogUtils.d("yysdk","loginpay"+extraBean.getCode());
                if (extraBean.getCode() == 200) {
                    CreatOrderInterface.PayOrder();
                    CreatOrderInterface.CreatOrder(createBackBean);
                }
            }

            @Override
            public void requestFailure(String request, IOException e) {
                Log.e("ssss",request);
            }

            @Override
            public void requestNoConnect(String msg, String data) {
                Log.e("sssss",msg);
            }
        });
    }

    /**
     * 支付
     */
    public static void getActivityPay(final Activity activity, final String pay_type, String order_id) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("pay_type", pay_type);
        hashMap.put("order_id", order_id);
        HttpRequestUtil.okPostFormRequest(URLString.GamePay + "aid/" + aid + "/gid/" + gid + "/", "pay", hashMap, new HttpRequestUtil.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                PayBackBean payBackBean = GsonUtils.GsonToBean(result, PayBackBean.class);
                if (pay_type.equals("alipay_bank")) {
                    AliPayPlugin.alipay(activity, payBackBean.getData().getUrl(), new AliPayInterface() {
                        Intent intent = new Intent();

                        @Override
                        public void PayOver(int code) {
                            Toast.makeText(activity, "支付成功", Toast.LENGTH_LONG).show();
                            intent.putExtra("code", 200);
                            activity.setResult(400, intent);
                            activity.finish();
                        }

                        @Override
                        public void onFailure(int cancel, String action) {
                            Toast.makeText(activity, action, Toast.LENGTH_LONG).show();
                            intent.putExtra("code", 201);
                            activity.setResult(400, intent);
                            activity.finish();
                        }
                    });
//                    DialogWeb dialogWeb=new DialogWeb(activity, payBackBean.getData().getUrl(), DialogWeb.TYPE_FULL);
//                    dialogWeb.show();
                } else {
                    DialogWeb dialogWeb = new DialogWeb(activity, payBackBean.getData().getUrl(), DialogWeb.TYPE_FULL);
                    dialogWeb.show();
                }
            }

            @Override
            public void requestFailure(String request, IOException e) {

            }

            @Override
            public void requestNoConnect(String msg, String data) {

            }
        });
    }
}