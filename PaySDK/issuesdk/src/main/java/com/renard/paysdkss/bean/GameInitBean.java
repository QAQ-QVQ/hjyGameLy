package com.renard.paysdkss.bean;

/**
 * Created by Riven_rabbit on 2019/4/24
 *
 * @author Lemon酱
 */
public class GameInitBean {

    /**
     * login_status : true
     * pay_status : true
     * add_status : true
     */

    private String login_status;
    private String pay_status;
    private String add_status;
    private String customerqq;

    public String getLogin_status() {
        return login_status;
    }

    public void setLogin_status(String login_status) {
        this.login_status = login_status;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getAdd_status() {
        return add_status;
    }

    public void setAdd_status(String add_status) {
        this.add_status = add_status;
    }

    public String getCustomerqq() {
        return customerqq;
    }

    public void setCustomerqq(String customerqq) {
        this.customerqq = customerqq;
    }
}
