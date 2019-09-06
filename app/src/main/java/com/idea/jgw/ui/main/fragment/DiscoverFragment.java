package com.idea.jgw.ui.main.fragment;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.idea.jgw.App;
import com.idea.jgw.R;
import com.idea.jgw.RouterPath;
import com.idea.jgw.api.retrofit.ServiceApi;
import com.idea.jgw.bean.AllMiningData;
import com.idea.jgw.bean.BaseResponse;
import com.idea.jgw.bean.CoinMining;
import com.idea.jgw.ui.BaseAdapter;
import com.idea.jgw.ui.BaseFragment;
import com.idea.jgw.ui.main.adapter.MiningAdapter;
import com.idea.jgw.utils.baserx.RxSubscriber;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.MToast;
import com.idea.jgw.utils.common.MyLog;
import com.idea.jgw.utils.common.SharedPreferenceManager;
import com.idea.jgw.view.FloatView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * <p>挖矿tab</p>
 * Created by idea on 2018/5/16.
 */

public class DiscoverFragment extends BaseFragment implements BaseAdapter.OnItemClickListener<CoinMining> {
    public static final int IMEI_REQUEST = 113;

    MiningAdapter miningAdapter;
    MediaPlayer mMediaPlayer;

    //    @BindView(R.id.sv_of_content)
    SurfaceView svOfContent;
    //    @BindView(R.id.tv_notify)
//    TextView tvNotify;
//    @BindView(R.id.tv_asset)
    TextView tvAsset;
    //    @BindView(R.id.tv_hashrate)
    TextView tvHashrate;
    //    @BindView(R.id.tv_mining_income_label)
//    TextView tvMiningIncomeLabel;
//    @BindView(R.id.btn_add_hashrate)
    Button btnAddHashrate;
    @BindView(R.id.rv_of_detail_mining)
    XRecyclerView rvOfDetailAsset;
    //    @BindView(R.id.fv_of_mining)
    FloatView fvOfMining;
    //    @BindView(R.id.iv_of_content_bg)
    ImageView ivOfContentBg;

    AllMiningData allMiningData;
    final static int INTERVAL = 5 * 60;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        miningAdapter = new MiningAdapter(getActivity());
//        miningAdapter.addDatas(getTestDatas(3));
        miningAdapter.setOnItemClickListener(this);
        disposables.add(Observable.interval(INTERVAL, INTERVAL, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        getMiningData(false);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        MyLog.d(throwable);
                    }
                }));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_discovery, null);
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvOfDetailAsset.setLayoutManager(layoutManager);
        rvOfDetailAsset.setAdapter(miningAdapter);

        View header = inflater.inflate(R.layout.header_of_mining, null, false);
        svOfContent = header.findViewById(R.id.sv_of_content);
        fvOfMining = header.findViewById(R.id.fv_of_mining);
        tvHashrate = header.findViewById(R.id.tv_hashrate);
        btnAddHashrate = header.findViewById(R.id.btn_add_hashrate);
        ivOfContentBg = header.findViewById(R.id.iv_of_content_bg);
        rvOfDetailAsset.addHeaderView(header);
        btnAddHashrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(RouterPath.MINING_HASHRATE_ACTIVITY).navigation();
            }
        });
        initSurfaceView();

//        fvOfMining.setList(getTestData());
        fvOfMining.setOnItemClickListener(new FloatView.OnItemClickListener() {
            @Override
            public void itemClick(FloatView.FloatViewData value) {
//                Toast.makeText(getActivity(), "当前是第"+position+"个，其值是"+value.floatValue(), Toast.LENGTH_SHORT).show();
                receiveMiningData(value.getType(), value.getValue());
            }
        });
        rvOfDetailAsset.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getMiningData(false);
            }

            @Override
            public void onLoadMore() {

            }
        });
        rvOfDetailAsset.setHeaderBackgroundColor(R.color.title_1);
        rvOfDetailAsset.setHeaderTextColor(R.color.white);
        rvOfDetailAsset.setNoMore(true);

        tvHashrate.setText(String.format(getString(R.string.sample_hashrate), 0));

//        getMiningData(false);
        checkPhoneStatePermission();
        return view;
    }

    @Override
    public void showPhoneStateExplain(Intent intent) {
        showExplain(intent, getString(R.string.why_need_phone_state2));
    }

    @Override
    public void phoneStateGranted() {
        getMiningData(false);
    }

    private void getMiningData(boolean showDialog) {
        String token = SharedPreferenceManager.getInstance().getSession();
        String imei = CommonUtils.getIMEI(App.getInstance());
//        String imei = "qwe"; //设备号暂时使用qwe
        disposables.add(ServiceApi.getInstance().getApiService()
                .miningData(imei, token)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(getActivity(), getResources().getString(R.string.loading), showDialog) {
                               @Override
                               protected void _onNext(BaseResponse baseResponse) {
                                   if (baseResponse.getCode() == BaseResponse.RESULT_OK) {
//                                       allMiningData = GsonUtils.parseJson(baseResponse.getData().toString(), AllMiningData.class);
                                       allMiningData = JSON.parseObject(baseResponse.getData().toString(), AllMiningData.class);
                                       tvHashrate.setText(String.format(getString(R.string.sample_hashrate), allMiningData.getCalculation()));
                                       SharedPreferenceManager.getInstance().setHashrate(allMiningData.getCalculation());
                                       List<CoinMining> coinMinings = allMiningData.getList();
                                       miningAdapter.replaceDatas(coinMinings);

                                       List<FloatView.FloatViewData> list = new ArrayList<>();
                                       for (CoinMining coinMining : coinMinings) {
                                           if (coinMining.getReceive_profit() > 0) {
                                               FloatView.FloatViewData data = new FloatView.FloatViewData();
                                               if (App.testIP) {
                                                   data.setType(coinMining.getCoin_info().getCharX());
                                               } else {
                                                   data.setType(coinMining.getCoin_info().getId() + "");
                                               }
                                               data.setValue(coinMining.getReceive_profit());
                                               data.setUrl(coinMining.getCoin_info().getFace());
                                               list.add(data);
                                           }
                                       }
                                       fvOfMining.setList(list);
                                   } else if (baseResponse.getCode() == BaseResponse.INVALID_SESSION) {
                                       reLogin();
                                       MToast.showToast(baseResponse.getData().toString());
                                   } else {
                                       MToast.showToast(baseResponse.getData().toString());
                                   }
                                   rvOfDetailAsset.refreshComplete();
                               }

                               @Override
                               protected void _onError(String message) {
                                   MToast.showToast(message);
                                   rvOfDetailAsset.refreshComplete();
                               }
                           }
                ));
    }

    private void receiveMiningData(final String type, final double value) {
        String token = SharedPreferenceManager.getInstance().getSession();
        disposables.add(ServiceApi.getInstance().getApiService()
                .receiveMiningData(type, String.valueOf(value), token)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxSubscriber<BaseResponse>(getActivity(), getResources().getString(R.string.loading), true) {
                               @Override
                               protected void _onNext(BaseResponse baseResponse) {
                                   if (baseResponse.getCode() == BaseResponse.RESULT_OK) {
                                       fvOfMining.removeAt(type);
                                       for (CoinMining coinMining : miningAdapter.getmDatas()) {
                                           if (coinMining.getCoin_info().getCharX().equals(type)) {
                                               double profit = coinMining.getBalance() + value;
                                               coinMining.setBalance(profit);
                                               break;
                                           }
                                       }
                                       miningAdapter.notifyDataSetChanged();
                                       MToast.showToast(R.string.received_success);
                                       return;
                                   }
                                   if (baseResponse.getCode() == BaseResponse.INVALID_SESSION) {
                                       reLogin();
                                       MToast.showToast(baseResponse.getData().toString());
                                       return;
                                   }
                                   MToast.showToast(R.string.received_failed);
                               }

                               @Override
                               protected void _onError(String message) {
//                                   MToast.showToast(message);
                                   MToast.showToast(R.string.received_failed);
                               }
                           }
                ));
    }

    private List<Float> getTestData() {
        List<Float> list = new ArrayList<>();
        list.add((float) 1.567);
        list.add((float) 0.261);
        list.add((float) 2.455);
        list.add((float) 2.4000255);
        list.add((float) 0.000255);
        return list;
    }

    @Override
    public void onItemClick(int position, CoinMining data) {
        String coinType = data.getCoin_info().getCharX();
        if (!App.testIP) {
            coinType = data.getCoin_info().getId() + "";
        }
        String coinLogo = data.getCoin_info().getFace();
        double balance = data.getBalance();
        ARouter.getInstance().build(RouterPath.MINING_DETAIL_ACTIVITY).withString("coinLogo", coinLogo).withString("coinType", coinType).withDouble("balance", balance).navigation();
    }

    @Override
    public void onResume() {
        super.onResume();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                initMediaPlayer();
//            }
//        }, 1000);
    }

    private void initSurfaceView() {

        svOfContent.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initMediaPlayer();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                mMediaPlayer.release();
                Log.i(">>>>>>>>>>>>>>>>>>>>>", "销毁了");
            }
        });
    }

    boolean loopVideo;
    Bitmap bitmap;

    private void initMediaPlayer() {

        try {
            ivOfContentBg.setVisibility(View.VISIBLE);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.reset();//初始化
            AssetFileDescriptor afd = getActivity().getAssets().openFd("video1.mp4");//获取视频资源
            if (bitmap == null || bitmap.isRecycled()) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                bitmap = retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                retriever.release();
            }
            ivOfContentBg.setImageBitmap(bitmap);
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mMediaPlayer.setDisplay(svOfContent.getHolder());
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                svOfContent.setVisibility(View.VISIBLE);
                                ivOfContentBg.setVisibility(View.GONE);
                            }
                            return true;
                        }
                    });
                    mMediaPlayer.start();
                }
            });
            loopVideo = false;
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    @OnClick(R.id.btn_add_hashrate)
//    public void onClick() {
//        ARouter.getInstance().build(RouterPath.MINING_HASHRATE_ACTIVITY).navigation();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

//    public void unSubscribe(Subscription subscription) {
//        if(subscription != null && !subscription.isUnsubscribed()) {
//            subscription.unsubscribe();
//        }
//    }
}
