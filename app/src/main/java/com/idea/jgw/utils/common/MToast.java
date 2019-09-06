package com.idea.jgw.utils.common;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.idea.jgw.App;
import com.idea.jgw.R;

/**
 * 带图片的toast
 * 
 * 注意：2s内相同内容的toast会被过滤掉
 * 
 */
public class MToast {
	private static final long DROP_DUPLICATE_TOAST_TS = 2 * 1000; // 2s

	private static String sLast = "";

	private static long sLastTs = 0;

	private static Toast mBasicToast = null;

	public static void showToast(int resId) {
		String str = App.getInstance().getString(resId);
		showToast(str);
	}

	/**
	 * toast（带动画）-显示时间2S
	 * @param str 要显示的字符串
	 */
	public static void showToast(String str) {
		long newTs = System.currentTimeMillis();
		if (str != null
				&& (!str.equals(sLast) || newTs < sLastTs || (newTs - sLastTs) > DROP_DUPLICATE_TOAST_TS)) {
			sLast = str;
			sLastTs = newTs;
			if (mBasicToast == null) {
				mBasicToast = new Toast(App.getInstance());
			}
			View toastView = LayoutInflater.from(App.getInstance()).inflate(
					R.layout.comm_toast_view, null);
			TextView txt = (TextView) toastView.findViewById(R.id.xl_toast_txt);
			txt.setText(str);
			mBasicToast.setView(toastView);
			int px = DipPixelUtils.dip2px(App.getInstance(), 60);
			mBasicToast.setGravity(Gravity.BOTTOM, 0, px);
			mBasicToast.setDuration(Toast.LENGTH_SHORT);// 默认只显示2S
			mBasicToast.show();
		}
	}

	public static void hideToast() {
		if (mBasicToast != null) {
			mBasicToast.cancel();
		}

	}

	/**
	 * toast（带动画）-显示时间2S
	 *
	 * @param str 要显示的字符串
	 * @param maxLines -最大字行数
	 */
	public static void showToast(String str, int maxLines) {
		long newTs = System.currentTimeMillis();
		if (str != null
				&& (!str.equals(sLast) || newTs < sLastTs || (newTs - sLastTs) > DROP_DUPLICATE_TOAST_TS)) {
			sLast = str;
			sLastTs = newTs;
			if (mBasicToast == null) {
				mBasicToast = new Toast(App.getInstance());
			}
			View toastView = LayoutInflater.from(App.getInstance()).inflate(
					R.layout.comm_toast_view, null);
			TextView txt = (TextView) toastView.findViewById(R.id.xl_toast_txt);
			txt.setText(str);

			if (maxLines > 0) {
				txt.setMaxLines(maxLines);
			}

			mBasicToast.setView(toastView);
			int px = DipPixelUtils.dip2px(App.getInstance(), 60);
			mBasicToast.setGravity(Gravity.BOTTOM, 0, px);
			mBasicToast.setDuration(Toast.LENGTH_SHORT);// 默认只显示2S
			mBasicToast.show();
		}
	}

	public static void showLongToast(int strResId) {
		showLongToast(App.getInstance().getResources().getString(strResId));
	}

	/**
	 * toast（带动画）-显示时间3.5S
	 *
	 * @param str 要显示的字符串
	 */
	public static void showLongToast(String str) {
		long newTs = System.currentTimeMillis();
		if (str != null
				&& (!str.equals(sLast) || newTs < sLastTs || (newTs - sLastTs) > DROP_DUPLICATE_TOAST_TS)) {
			sLast = str;
			sLastTs = newTs;
			if (mBasicToast == null) {
				mBasicToast = new Toast(App.getInstance());
			}
			View toastView = LayoutInflater.from(App.getInstance()).inflate(
					R.layout.comm_toast_view, null);
			TextView txt = (TextView) toastView.findViewById(R.id.xl_toast_txt);
			txt.setText(str);

			mBasicToast.setView(toastView);
			int px = DipPixelUtils.dip2px(App.getInstance(), 60);
			mBasicToast.setGravity(Gravity.BOTTOM, 0, px);
			mBasicToast.setDuration(Toast.LENGTH_LONG);
			mBasicToast.show();
		}
	}

}
