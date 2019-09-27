package com.renard.paysdkss.bean;

/**
 * Created by Riven_rabbit on 2019/5/5
 *
 * @author Lemon酱
 */
public class PayBackBean {

    /**
     * code : 200
     * data : {"type":"app","url":"app_id=2017060807449469&biz_content=%7B%22timeout_express%22%3A%2215m%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22total_amount%22%3A%220.01%22%2C%22subject%22%3A%22%E6%B5%8B%E8%AF%95%E5%95%86%E5%93%81%22%2C%22out_trade_no%22%3A%22201904300940368404%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fissue.hjygame.com%2Fnotify%2Falipay&return_url=http%3A%2F%2Fissue.hjygame.com%2Freturn%2Falipay&sign_type=RSA2&timestamp=2019-04-30+09%3A58%3A54&version=1.0&sign=GBi%2FXNZX0moLYacejSsm5eCnN8FpR%2FprvlJdKxRTp4uogXRwe592e%2B6j7zP2LJLsW%2Bryxewqh%2FPtAgB5eBphy1TH0ybMdNLcb0%2Bkky%2FUwXfnG6uhcFAJl2ZM4iuTXKw06%2FleOUc0WZk%2BGMWJP9vhPc9dSfDZPVHgxZH5aYiMap04ZRuV2Lzz1d6ZIGUotRjyiaKv6KixJfaHwNxbP5Hmw9TEqvhSWhOjBwdoXjj6V92SWMIHp7k7icedtb05Ys1ympy8UBaHYs%2FfgCY7ogbGo32VuWi7iZgNfTg78oWCEj6%2F6M06hSHzZMFphQo9csulZdNYhnSthtmeqK29Fxd%2ByQ%3D%3D"}
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
         * type : app
         * url : app_id=2017060807449469&biz_content=%7B%22timeout_express%22%3A%2215m%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22total_amount%22%3A%220.01%22%2C%22subject%22%3A%22%E6%B5%8B%E8%AF%95%E5%95%86%E5%93%81%22%2C%22out_trade_no%22%3A%22201904300940368404%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fissue.hjygame.com%2Fnotify%2Falipay&return_url=http%3A%2F%2Fissue.hjygame.com%2Freturn%2Falipay&sign_type=RSA2&timestamp=2019-04-30+09%3A58%3A54&version=1.0&sign=GBi%2FXNZX0moLYacejSsm5eCnN8FpR%2FprvlJdKxRTp4uogXRwe592e%2B6j7zP2LJLsW%2Bryxewqh%2FPtAgB5eBphy1TH0ybMdNLcb0%2Bkky%2FUwXfnG6uhcFAJl2ZM4iuTXKw06%2FleOUc0WZk%2BGMWJP9vhPc9dSfDZPVHgxZH5aYiMap04ZRuV2Lzz1d6ZIGUotRjyiaKv6KixJfaHwNxbP5Hmw9TEqvhSWhOjBwdoXjj6V92SWMIHp7k7icedtb05Ys1ympy8UBaHYs%2FfgCY7ogbGo32VuWi7iZgNfTg78oWCEj6%2F6M06hSHzZMFphQo9csulZdNYhnSthtmeqK29Fxd%2ByQ%3D%3D
         */

        private String type;
        private String url;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
