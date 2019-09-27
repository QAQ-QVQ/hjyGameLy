package com.renard.common.Interface;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * Created by Riven_rabbit on 2019/4/26
 *
 * @author Lemon酱
 */
public interface LifeCycleInterface {
    void onCreate(Context context, Bundle savedInstanceState);

    void onStart(Context context);

    void onResume(Context context);

    void onPause(Context context);

    void onStop(Context context);

    void onRestart(Context context);

    void onDestroy(Context context);

    void onNewIntent(Context context, Intent intent);

    void onActivityResult(Context context, int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults);

    void onConfigurationChanged (Context context, Configuration configuration);

}
