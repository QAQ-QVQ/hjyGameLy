package com.renard.hjysdkapi.bean.info;

/**
 * Created by Riven_rabbit on 2019/4/25
 *
 * @author Lemon酱
 */
public class ConfigInfo {
    public String gameid;
    public String gamekey;
    public String paykey;

    public ConfigInfo(String gameid, String gamekey,String paykey){

        this.gameid = gameid;
        this.gamekey = gamekey;
        this.paykey=paykey;
    }

    public String getGameid() {
        return gameid;
    }

    public String getGamekey() {
        return gamekey;
    }

    public String getPaykey() {
        return paykey;
    }
}
