package com.renard.common.utils.log;

import android.util.Log;

/**
 * Created by Riven_rabbit on 2019/4/25
 *
 * @author Lemon酱
 */
public class LogcatLogStrategy implements LogStrategy {

    @Override
    public void log(int priority, String tag, String message) {
        Log.println(priority, tag, message);
    }

}