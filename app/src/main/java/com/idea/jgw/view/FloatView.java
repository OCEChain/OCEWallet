package com.idea.jgw.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.idea.jgw.R;
import com.idea.jgw.utils.common.MyLog;
import com.idea.jgw.utils.glide.GlideApp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public class FloatView extends RelativeLayout {
    private static final long ANIMATION_TIME = 1000;
    private static final long ANIMATION_DEFAULT_TIME = 2000;
    private static final String TAG = "FloatView";
    private Context mcontext;
    private List<? extends FloatViewData> mFloat;
    private List<View> mViews = new ArrayList<>();
    public RelativeLayout parentView;
    private int parentWidth;
    private int parentHeight;
    private OnItemClickListener mListener;
    private int textColor;
    private float childSize;

    public FloatView(Context context) {
        this(context, null);
        mcontext = context;
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.floatView);
        textColor = typedArray.getColor(R.styleable.floatView_childTextColor, getResources().getColor(R.color.white));
        childSize = typedArray.getDimension(R.styleable.floatView_chidTextSize, 10);
        //一定会要释放资源
        typedArray.recycle();
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
        mcontext = context;
    }

    private void init() {
//        setDefaultView();
        for(View view:mViews) {
            remove(view);
        }
        addChidView();
    }

    //添加小球
    private void addChidView() {
        for (int i = 0; i < mFloat.size(); i++) {
//            TextView floatview = (TextView) LayoutInflater.from(mcontext).inflate(R.layout.view_float, this, false);
            LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER);
            layout.setLayoutParams(tvLayoutParams);
            ImageView imageView = new ImageView(getContext());
            int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26, getContext().getResources().getDisplayMetrics());
            LinearLayout.LayoutParams ivLayoutParams = new LinearLayout.LayoutParams(w, w);
            imageView.setLayoutParams(ivLayoutParams);
            if(getContext() == null)
                return;
            GlideApp.with(getContext()).load(mFloat.get(i).getUrl()).into(imageView);
            TextView floatview = new TextView(getContext());
            floatview.setLayoutParams(tvLayoutParams);
            floatview.setTextColor(textColor);
            floatview.setTextSize(childSize);
            floatview.setText(String.valueOf(new BigDecimal(mFloat.get(i).getValue() + "").toString()));
            floatview.setGravity(Gravity.CENTER);
            String type = mFloat.get(i).getType();
//            int resid = R.mipmap.icon_oce_small;
//            if(type == 1) {
//                resid = R.mipmap.icon_btc_small;
//            } else if(type == 2) {
//                resid = R.mipmap.icon_eth;
//            }
//            floatview.setCompoundDrawablesWithIntrinsicBounds(0, resid, 0, 0);
            layout.addView(imageView);
            layout.addView(floatview);
            layout.setTag(type);
            layout.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    childClick(v);
                }
            });
            setChildViewPosition(layout);
            initAnim(layout);
            initFloatAnim(layout);
            mViews.add(layout);
            addView(layout);
        }
    }

    public static int fromYDelta = -10;
    public static int toYDelta = 20;

    //FloatView上下抖动的动画
    private void initFloatAnim(View view) {
        Animation anim = new TranslateAnimation(0, 0, fromYDelta, toYDelta);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(ANIMATION_TIME);
        anim.setRepeatCount(Integer.MAX_VALUE);
        anim.setRepeatMode(Animation.REVERSE);//反方向执行
        view.startAnimation(anim);
    }

    //FloatView初始化时动画
    private void initAnim(View view) {
        view.setAlpha(0);
        view.setScaleX(0);
        view.setScaleY(0);
        view.animate().alpha(1).scaleX(1).scaleY(1).setDuration(ANIMATION_DEFAULT_TIME).start();
    }

    //设置数据添加子小球
    public void setList(List<? extends FloatViewData> list) {
        this.mFloat = list;
        //使用post方法确保在UI加载完的情况下 调用init() 避免获取到的宽高为0
        post(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    //设置子view的位置
    private void setChildViewPosition(View childView) {
        parentWidth = getWidth();
        parentHeight = getHeight();
        //设置随机位置
        Random randomX = new Random();
        Random randomY = new Random();
        float x = randomX.nextFloat() * (parentWidth - childView.getMeasuredWidth());
        float y = randomY.nextFloat() * (parentHeight - childView.getMeasuredHeight() - toYDelta + fromYDelta) - fromYDelta;
        Log.d(TAG, "setChildViewPosition: parentWidth=" + parentWidth + ",parentHeight=" + parentHeight);
        Log.d(TAG, "setChildViewPosition: childWidth=" + childView.getMeasuredWidth() + ",childHeight=" + childView.getMeasuredHeight());
        Log.d(TAG, "setChildViewPosition: x=" + x + ",y=" + y);
        childView.setX(x);
        childView.setY(y);
    }

    private void childClick(View view) {
        //设置接口回调

//        remove(view);
        String type = view.getTag().toString();
        for (FloatViewData viewData : mFloat) {
            if (viewData.getType().equals(type)) {
                mListener.itemClick(viewData);
            }
        }
    }

    public void remove(View view) {
        mViews.remove(view);
        animRemoveView(view);
    }

    public void removeAt(String type) {
        for (View view : mViews) {
            if (view.getTag().equals(type)) {
                mViews.remove(view);
                animRemoveView(view);
                break;
            }
            MyLog.e("view tag = " + view.getTag());
        }
    }

    public boolean needRefresh() {
        return mViews.size() != mFloat.size();
    }

    private void animRemoveView(final View view) {
//
        ValueAnimator animator = ValueAnimator.ofFloat(parentHeight, 0);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());

        //动画更新的监听
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float Value = (float) animation.getAnimatedValue();
                Log.d(TAG, "onAnimationUpdate: " + view.getTranslationY());
                Log.d(TAG, "onAnimationUpdate: " + view.getY());
                view.setAlpha(Value / parentHeight);
                view.setTranslationY(view.getY() - (parentHeight - Value));
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(view);
            }
        });
        animator.start();
    }

    public interface OnItemClickListener {
        void itemClick(FloatViewData value);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class FloatViewData {
        double value;
        String type;
        String url;

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}
