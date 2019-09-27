package com.renard.paysdkss.ui;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.renard.common.Interface.CallBackListener;
import com.renard.common.utils.ResourceUtil;
import com.renard.paysdkss.PayManager;
import com.renard.paysdkss.bean.CreateBackBean;
import com.renard.paysdkss.bean.CreateOrderBean;
import com.renard.paysdkss.listener.CreatOrderInterface;

/**
 * Created by Riven_rabbit on 2019/4/30
 *
 * @author Lemon酱
 */
public class PayActivity extends Activity implements View.OnClickListener,CreatOrderInterface {

    ImageView Wechat;
    ImageView AliPay;
    TextView pay_money;
    TextView pay_gold;
    TextView user_name;
    TextView pay_bindgold;
    TextView pay_money_balance;
    TextView good_name;
    Button pay;
    private String PayType="alipay_bank";
    private String OrderId="";
    private CreateOrderBean createOrderBean;

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
        setContentView(ResourceUtil.getLayoutResIDByName(this, "issue_pay"));
        //绑定初始化ButterKnife
        BindView();
        //不允许点击窗口外部区域关闭窗口
        this.setFinishOnTouchOutside(false);
        if (getIntent().getSerializableExtra("creatorder")!=null){
            createOrderBean= (CreateOrderBean) getIntent().getSerializableExtra("creatorder");
            PayManager.getActivityCreateOrder(createOrderBean,this);
        }
    }

    private void BindView() {
        Wechat=findViewById(ResourceUtil.getIdResIDByName(this,"img_wx_select"));
        AliPay=findViewById(ResourceUtil.getIdResIDByName(this,"img_zfb_select"));
        good_name=findViewById(ResourceUtil.getIdResIDByName(this,"good_name"));
        pay_money=findViewById(ResourceUtil.getIdResIDByName(this,"pay_money"));
        pay_gold=findViewById(ResourceUtil.getIdResIDByName(this,"pay_gold"));
        user_name=findViewById(ResourceUtil.getIdResIDByName(this,"user_name"));
        pay_bindgold=findViewById(ResourceUtil.getIdResIDByName(this,"pay_bindgold"));
        pay_money_balance=findViewById(ResourceUtil.getIdResIDByName(this,"pay_money_balance"));
        pay=findViewById(ResourceUtil.getIdResIDByName(this,"pay"));
        Wechat.setOnClickListener(this);
        AliPay.setOnClickListener(this);
        pay.setOnClickListener(this);
    }
    private boolean payx=false;
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i==ResourceUtil.getIdResIDByName(this,"goback")){
//            SDKManager.getCallBackListener().onPay(201);
            this.finish();
        } else if (i == ResourceUtil.getIdResIDByName(this,"pay")) {
            if (payx){
                PayManager.getActivityPay(this,PayType,OrderId);
            }else {
                Toast.makeText(this,"订单创建失败，无法进行支付",Toast.LENGTH_LONG).show();
            }
        } else if (i == ResourceUtil.getIdResIDByName(this,"img_zfb_select")) {
            Wechat.setImageResource(ResourceUtil.getDrawableResIDByName(this,"img_check_n"));
            AliPay.setImageResource(ResourceUtil.getDrawableResIDByName(this,"img_check_y"));
            PayType="alipay_bank";
        } else if (i == ResourceUtil.getIdResIDByName(this,"img_wx_select")) {
            Wechat.setImageResource(ResourceUtil.getDrawableResIDByName(this,"img_check_y"));
            AliPay.setImageResource(ResourceUtil.getDrawableResIDByName(this,"img_check_n"));
            PayType="wxpay_bank";
        }
    }

    @Override
    public void CreatOrder(CreateBackBean createBackBean) {
        good_name.setText(createBackBean.getData().getGoods_name());
        user_name.setText("(UID:"+createBackBean.getData().getUid()+")");
        pay_money_balance.setText("还需支付："+String.valueOf(Double.parseDouble(createBackBean.getData().getReal_money())));
        pay_money.setText(String.valueOf(Double.parseDouble(createBackBean.getData().getReal_money())));
        OrderId=createBackBean.getData().getOrder_id();
        payx=true;
    }

    @Override
    public void PayOrder() {

    }
}
