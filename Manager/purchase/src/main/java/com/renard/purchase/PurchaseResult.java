package com.renard.purchase;

/**
 * Created by Riven_rabbit on 2019/4/26
 *
 * @author Lemon酱
 */
public class PurchaseResult {
    public static final int OrderState = 1; // 创建订单成功
    public static final int PurchaseState = 2; // 支付结果

    public int status;
    public Object message;

    public PurchaseResult(int status, Object message) {

        this.status = status;
        this.message = message;

    }
}
