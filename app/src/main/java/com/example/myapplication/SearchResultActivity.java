package com.example.myapplication;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.Adapter.CartAdapter;
import com.example.myapplication.Adapter.SearchResultAdapter;
import com.example.myapplication.viewholder.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.io.Serializable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class SearchResultActivity extends AppCompatActivity implements SearchResultAdapter.ItemClickListener {
    @BindView(R.id.title)
    TextView mTvTitle;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.search_layout)
    RelativeLayout searchLayout;
    @BindView(R.id.shopping_cart)
    ImageView cartImage;

    private double totalPrice = 0.00;
    private int totalCount = 0;
    private List<HashMap<String, String>> goodsList;
    public List<HashMap<String, String>> goodsList_order;
    private SearchResultAdapter adapter;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);
        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        mTvTitle.setText("搜索结果");
        //模拟一些数据
        initDate();
        initView();
    }

    private void initDate() {
        goodsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", "0");
            map.put("name", "购物车里的第" + (i + 1) + "件商品");
            map.put("number", "666-"+i%3+i%4+i%5);
            map.put("inventory", (new Random().nextInt(10))+"");
            map.put("price", (new Random().nextInt(100) % (100 - 29 + 29) + 29) + "");
            map.put("count","1");
            goodsList.add(map);
        }
    }

    private void initView() {
        adapter = new SearchResultAdapter(this, goodsList, R.layout.item_search_result);
        mListView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.notifyDataSetChanged();
    }



    @OnClick({R.id.tv_go_to_cart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_go_to_cart:
                    Bundle bundle=new Bundle();
                    Intent intent=new Intent();
                    goodsList_order=new ArrayList<>();

                    for (int i = 0; i < goodsList.size(); i++)
                    {
                        if (goodsList.get(i).get("id").equals("1"))
                        {
                            goodsList_order.add(goodsList.get(i));
                        }
                    }
                    intent.setClass(this, MainActivity.class);
                    bundle.putSerializable("goodsList_order",(Serializable)goodsList_order);
                    intent.putExtras(bundle);
                    startActivity(intent);
                break;
        }
    }
    @Override
    public void AddToCart(final View view, int position) {
        if (goodsList.get(position).get("id").equals("0")) {
            //贝塞尔起始数据点
            int[] startPosition = new int[2];
            //贝塞尔结束数据点
            int[] endPosition = new int[2];
            //控制点
            int[] recyclerPosition = new int[2];
            goodsList.get(position).put("id", "1");
            view.setBackgroundColor(Color.parseColor("#b5b5b5"));
            view.getLocationInWindow(startPosition);
            cartImage.getLocationInWindow(endPosition);
            mListView.getLocationInWindow(recyclerPosition);

            PointF startF = new PointF();
            PointF endF = new PointF();
            PointF controllF = new PointF();

            startF.x = startPosition[0];
            startF.y = startPosition[1];
            endF.x = endPosition[0];
            endF.y = endPosition[1];
            controllF.x = endF.x;
            controllF.y = startF.y;

            final ImageView imageView = new ImageView(this);
            searchLayout.addView(imageView);
            imageView.setImageResource(R.drawable.add_to_cart);
            imageView.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.add_to_cart);
            imageView.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.add_to_cart);
            imageView.setVisibility(View.VISIBLE);
            imageView.setX(startF.x);
            imageView.setY(startF.y);

            ValueAnimator valueAnimator = ValueAnimator.ofObject(new BezierTypeEvaluator(controllF), startF, endF);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    PointF pointF = (PointF) animation.getAnimatedValue();
                    imageView.setX(pointF.x);
                    imageView.setY(pointF.y);
                    Log.i("wangjtiao", "viewF:" + view.getX() + "," + view.getY());
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    imageView.setVisibility(View.GONE);
                    searchLayout.removeView(imageView);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            ObjectAnimator objectAnimatorX = new ObjectAnimator().ofFloat(cartImage, "scaleX", 0.6f, 1.0f);
            ObjectAnimator objectAnimatorY = new ObjectAnimator().ofFloat(cartImage, "scaleY", 0.6f, 1.0f);
            objectAnimatorX.setInterpolator(new AccelerateInterpolator());
            objectAnimatorY.setInterpolator(new AccelerateInterpolator());
            AnimatorSet set = new AnimatorSet();
            set.play(objectAnimatorX).with(objectAnimatorY).after(valueAnimator);
            set.setDuration(800);
            set.start();

        }
    }
}