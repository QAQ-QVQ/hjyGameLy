package com.renard.paysdkss.listener;

import com.renard.paysdkss.bean.CreateBackBean;

/**
 * Created by Riven_rabbit on 2019/5/5
 *
 * @author Lemon酱
 */
public interface CreatOrderInterface {

    void CreatOrder(CreateBackBean createBackBean);

    void PayOrder();

}
