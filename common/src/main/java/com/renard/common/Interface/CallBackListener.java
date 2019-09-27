package com.renard.common.Interface;

/**
 * Created by Riven_rabbit on 2019/4/24
 *
 * @author Lemon酱
 */
public interface CallBackListener<T> {
    /**
     * 成功回调
     * @param t 详细信息
     */
    void onSuccess(T t);

    /**
     * 失败回调
     *
     * @param code 错误码
     * @param msg 错误详细描述信息
     */
    void onFailure(int code, String msg);
}
