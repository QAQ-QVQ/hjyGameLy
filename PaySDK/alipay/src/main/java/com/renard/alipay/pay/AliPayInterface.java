package com.renard.alipay.pay;

/**
 * Created by Riven_rabbit on 2019/5/5
 *
 * @author Lemon酱
 */
public interface AliPayInterface {
    void PayOver(int code);

    void onFailure(int cancel, String action);
}
