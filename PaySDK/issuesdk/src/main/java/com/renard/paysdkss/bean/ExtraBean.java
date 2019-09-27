package com.renard.paysdkss.bean;

public class ExtraBean {
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
        private String extra;
        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }
    }
}
