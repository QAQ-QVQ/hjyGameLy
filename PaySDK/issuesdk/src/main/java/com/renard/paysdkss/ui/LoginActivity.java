package com.renard.paysdkss.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.renard.common.utils.GsonUtils;
import com.renard.common.utils.HttpRequestUtil;
import com.renard.common.utils.KLog;
import com.renard.common.utils.RegexUtils;
import com.renard.common.utils.ResourceUtil;
import com.renard.common.utils.URLString;
import com.renard.paysdkss.ConfigUtils;
import com.renard.paysdkss.PayManager;
import com.renard.paysdkss.bean.GameBean;
import com.renard.paysdkss.bean.LoginBean;
import com.renard.paysdkss.listener.LoginInterface;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Riven_rabbit on 2019/4/30
 *
 * @author Lemon酱
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    TextView login;
    private TextView start_qq;
    EditText et_login_phone;
    EditText et_login_pwd;
    private String login_number,login_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //防止出现黑色棱角
        getWindow().setBackgroundDrawable(new ColorDrawable());
        setContentView(ResourceUtil.getLayoutResIDByName(this, "issue_login"));
        //绑定初始化ButterKnife
        BindView();
        //不允许点击窗口外部区域关闭窗口
        this.setFinishOnTouchOutside(false);
    }

    /**
     * 绑定资源文件
     */
    private void BindView() {
        login = findViewById(ResourceUtil.getIdResIDByName(this, "login"));
        start_qq = findViewById(ResourceUtil.getIdResIDByName(this, "start_qq"));
        et_login_phone=findViewById(ResourceUtil.getIdResIDByName(this,"et_login_phone"));
        et_login_pwd=findViewById(ResourceUtil.getIdResIDByName(this,"et_login_pwd"));
        login.setOnClickListener(this);
        start_qq.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == ResourceUtil.getIdResIDByName(this, "login")) {
            if (Verification()){
                LoginBean loginBean = new LoginBean();
                loginBean.setUid("");
                loginBean.setToken("");
                PayManager.getActivityLogin1(loginBean, new LoginInterface() {
                    @Override
                    public void setUID(String uid) {
                        Intent intent=new Intent();
                        intent.putExtra("uid",uid);
                        setResult(300,intent);
                        finish();
                    }

                    @Override
                    public void Closed(String msg) {
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if (i == ResourceUtil.getIdResIDByName(this, "start_qq")) {
            try {
                String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=" + ConfigUtils.getQQ() + "&version=1";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (ActivityNotFoundException a) {
                a.getMessage();
            }
        }
    }

    /**
     * 验证&判空
     */
    private boolean Verification() {
        if (!et_login_phone.getText().toString().isEmpty()
                && RegexUtils.isMobileSimple(et_login_phone.getText().toString())) {
            login_number = et_login_phone.getText().toString();
        } else {
            Toast.makeText(this,"手机号码不正确",Toast.LENGTH_LONG).show();
            return false;
        }
        if (!et_login_pwd.getText().toString().isEmpty()) {
            login_pwd = et_login_pwd.getText().toString();
        }
        return true;
    }
}
