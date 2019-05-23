package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.Adapter.OrderAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckOrderActivity extends AppCompatActivity  {
    @BindView(R.id.title)
    TextView mTvTitle;
    @BindView(R.id.listView_order)
    ListView mListView;
    @BindView(R.id.stu_num)
    TextView stu_num;
    @BindView(R.id.stu_name)
    TextView stu_name;
    @BindView(R.id.tv_total_price)
    TextView mTvTotalPrice;
    @BindView(R.id.tv_go_to_pay_wechat)
    ImageButton mTvGoToPaywc;
    @BindView(R.id.tv_go_to_pay_ali)
    ImageButton mTvGoToPayali;
    private double totalPrice = 0.00;
    private int totalCount = 0;
    public List<HashMap<String, String>> goodsList_order;
    private OrderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_order);
        ButterKnife.bind(this);
        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        goodsList_order=new ArrayList<>();
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        goodsList_order=(List<HashMap<String, String>>) bundle.getSerializable("goodsList_order");
        stu_num.setText("学号 ：" + "161310611");
        stu_name.setText("姓名 ：" + "DHU");
        initView();
        priceContro();
    }


    private void initView() {
        adapter = new OrderAdapter(this, goodsList_order, R.layout.item_order);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void priceContro() {
        totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < goodsList_order.size(); i++) {
            double goodsPrice = Integer.valueOf(goodsList_order.get(i).get("count")) * Double.valueOf(goodsList_order.get(i).get("price"));
            totalPrice = totalPrice + goodsPrice;
        }
        mTvTotalPrice.setText("￥ " + totalPrice);
}
    @OnClick({R.id.tv_go_to_pay_wechat, R.id.tv_go_to_pay_ali, R.id.tv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_go_to_pay_wechat:

                break;
            case R.id.tv_go_to_pay_ali:

                break;
            case R.id.tv_back:
                Intent intent = new Intent();
                intent.setClass(this, ShoppingCartActivity.class);
                startActivity(intent);
                break;
        }
    }

}


