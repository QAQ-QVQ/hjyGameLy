package com.renard.paysdkss.bean;

import java.io.Serializable;

/**
 * Created by Riven_rabbit on 2019/4/30
 *
 * @author Lemon酱
 */
public class LoginBean implements Serializable {

    private String uid;

    private String token;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
