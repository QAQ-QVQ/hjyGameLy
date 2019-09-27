package com.renard.paysdkss;

/**
 * Created by Riven_rabbit on 2019/4/30
 *
 * @author Lemon酱
 */
public class ConfigUtils {
    //允许登录
    private static boolean IssueLogin=false;
    //允许支付
    private static boolean IssuePay=false;
    //客服
    private static String QQ="1156137954";

    public static String getQQ() {
        return QQ;
    }

    public static void setQQ(String QQ) {
        ConfigUtils.QQ = QQ;
    }

    public static boolean isIssueLogin() {
        return IssueLogin;
    }

    public static void setIssueLogin(boolean issueLogin) {
        IssueLogin = issueLogin;
    }

    public static boolean isIssuePay() {
        return IssuePay;
    }

    public static void setIssuePay(boolean issuePay) {
        IssuePay = issuePay;
    }
}
