package com.renard.paysdkss.bean;

/**
 * Created by Riven_rabbit on 2019/4/29
 *
 * @author Lemon酱
 */
public class GameBean {

    /**
     * code : 200
     * data : {"uid":"617","userName":"test1","time":""}
     */

    private int code;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * uid : 617
         * userName : test1
         * time :
         */

        private String uid;
        private String userName;
        private String time;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
