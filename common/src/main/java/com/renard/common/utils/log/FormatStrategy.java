package com.renard.common.utils.log;

/**
 * Created by Riven_rabbit on 2019/4/25
 *
 * @author Lemon酱
 */
public interface FormatStrategy {
    void log(int priority, String tag, String message);
}
