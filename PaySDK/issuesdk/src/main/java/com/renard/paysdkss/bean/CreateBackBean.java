package com.renard.paysdkss.bean;

import java.util.List;

/**
 * Created by Riven_rabbit on 2019/5/5
 *
 * @author Lemon酱
 */
public class CreateBackBean {

    /**
     * code : 200
     * data : {"partner_id":"97","game_id":585,"uid":189766,"goods_name":"测试商品","real_money":"0.01","order_id":"201904300940368404","pay_type":[{"channel":"alipay_bank","name":"支付宝"},{"channel":"wxpay_bank","name":"微信支付"}]}
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
         * partner_id : 97
         * game_id : 585
         * uid : 189766
         * goods_name : 测试商品
         * real_money : 0.01
         * order_id : 201904300940368404
         * pay_type : [{"channel":"alipay_bank","name":"支付宝"},{"channel":"wxpay_bank","name":"微信支付"}]
         */

        private String partner_id;
        private int game_id;
        private int uid;
        private String goods_name;
        private String real_money;
        private String order_id;
        private List<PayTypeBean> pay_type;

        public String getPartner_id() {
            return partner_id;
        }

        public void setPartner_id(String partner_id) {
            this.partner_id = partner_id;
        }

        public int getGame_id() {
            return game_id;
        }

        public void setGame_id(int game_id) {
            this.game_id = game_id;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getGoods_name() {
            return goods_name;
        }

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
        }

        public String getReal_money() {
            return real_money;
        }

        public void setReal_money(String real_money) {
            this.real_money = real_money;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public List<PayTypeBean> getPay_type() {
            return pay_type;
        }

        public void setPay_type(List<PayTypeBean> pay_type) {
            this.pay_type = pay_type;
        }

        public static class PayTypeBean {
            /**
             * channel : alipay_bank
             * name : 支付宝
             */

            private String channel;
            private String name;

            public String getChannel() {
                return channel;
            }

            public void setChannel(String channel) {
                this.channel = channel;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
