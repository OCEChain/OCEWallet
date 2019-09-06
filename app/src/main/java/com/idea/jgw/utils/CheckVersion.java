package com.idea.jgw.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.idea.jgw.App;
import com.idea.jgw.utils.common.CommonUtils;
import com.idea.jgw.utils.common.MyLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class CheckVersion {

	private static final int NETTIMEOUTLONG = 10 * 1000;
	private boolean checkNotice;  //是否监测提示

	public class VersionInfo{
		public boolean isParaseOk;
		public int mContentLen = 0;
		public String mNum = "";	//升级包版本号
		public String mName = "";
		public String mPackageName = "";	//升级包名称
		public long mPackageSize = 0;		//包大小
		public String mPackageUrl = "";	//下载地址
		public String mLog= "";	//更新日志
		
		public VersionInfo(){
			isParaseOk = false;
		}
	}
	
	private VersionInfo verInfo;
	private boolean isParaseOK;
	
	public final static String LOGTAG = "GeniusIdea";
	
	private static final String checkVersionUrl = "http://ideafota.com:9533/api/data.ashx";
	
	public CheckVersion() {
		// TODO Auto-generated constructor stub
		verInfo = new VersionInfo();
	}
	

	public boolean getParaseResult(){
		return isParaseOK;
	}
	
	public VersionInfo getVerInfo(){
		return verInfo;
	}
	
	/**
	 * 解析从服务器下载的版本信息
	 * @param buffer 信息缓存
	 * @param length 信息长度
	 * @return 解析成功或者失败
	 */
//	public boolean  paraseNetData(byte[] buffer, int length){
//		int pos = 0;
//		int contentLen = 0;
//
//		isParaseOK = false;
//		VersionInfo info = new VersionInfo();
//		verInfo.isParaseOk = false;
//
//		//若信息长度小于0则没有解析的必要了
//		if ( (null == buffer) || (length < 8)
//				|| (0x01 != StatHelper.bytesToInt(buffer, 0)) || ((contentLen=StatHelper.bytesToInt(buffer, 4)) <= 0) ){
//			return isParaseOK;
//		}
//
//		info.mContentLen = contentLen;
//		String data = new String(buffer);
//		try {
//			JSONObject content = new JSONObject(data);
//			verInfo.mNum = content.getString("pakver");
//
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		info.isParaseOk = isParaseOK;
//		verInfo = info;
//		return isParaseOK;
//	}
	
	/**
	 * 解析从服务器下载的版本信息
	 * @param recStr 信息
	 * @return 解析成功或者失败
	 */
	public boolean  paraseNetData(String recStr){
		
		isParaseOK = false;
		verInfo.isParaseOk = false;

		if(recStr == null || recStr.length() == 0)
			return isParaseOK;
		try {
			JSONObject content = new JSONObject(recStr);
			verInfo.mNum = content.getString("pakver");
			verInfo.mPackageName = content.getString("pakname");
			verInfo.mPackageSize = content.getLong("paksize");
			verInfo.mLog = content.getString("pakmsg");
			verInfo.mPackageUrl = content.getString("pakurl");
			isParaseOK = true;
			verInfo.isParaseOk = true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			

		return isParaseOK;
	}
	
	
	public String getCheckNewVerUrl(){
		return checkVersionUrl;
	}
	
	public static String encodeToNetString(String StrSrc){
			
		byte[] bytes = StrSrc.getBytes();
		String base64EncodeStr = Base64.encodeToString(bytes, Base64.DEFAULT);
		
		return base64EncodeStr;
		
	}
	
//	public static String encodeToNetString(String StrSrc){
//
//		byte[] bytes = StrSrc.getBytes();
//		String base64EncodeStr = Base64.encodeToString(bytes, Base64.DEFAULT);
//
//		return base64EncodeStr;
//
//	}
	
	public static String getCommonParam() {
		TelephonyManager tm = (TelephonyManager) App.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
		String imei="";
		String imsi="";
		String op="";
		if(null != tm){
		 imei = tm.getImei();
		 imei = "867515023747909";
		 imsi = tm.getSubscriberId();
		 op = tm.getSimOperator();
		}
		String pf = android.os.Build.VERSION.RELEASE;
		//int net = tm.getNetworkType();
		ConnectivityManager cm = (ConnectivityManager) App.getInstance()
	            .getSystemService(Context.CONNECTIVITY_SERVICE);
		int net = -1;
		if(null != cm){
		   net = cm.getActiveNetworkInfo().getType();
		}
		String appver = CommonUtils.getAppName(App.getInstance());
		String lang = Locale.getDefault().getLanguage();
		String brand =android.os.Build.BRAND;
		String model =android.os.Build.MODEL;//SystemProperties.get("ro.product.model");
		
		WindowManager windowManager = (WindowManager) App.getInstance().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		int sw = dm.widthPixels;
		int sh = dm.heightPixels;
		String ext = "extParam";


//		if(imsi == null ||imsi.equals("")){
//			imsi = getSimSlaveImsi();
//		}
//		if(op == null ||op.equals("")){
//			op = getSimSlaveOp();
//		}
		JSONObject commonParam = new JSONObject();
		try {
		    
		     commonParam.put("imei", imei!=null?imei:"0");
		     commonParam.put("pf", pf);
		     commonParam.put("brand", brand);
		     commonParam.put("model", model);
		     commonParam.put("sw", sw);
		     commonParam.put("sh", sh);
		     commonParam.put("imsi", imsi!=null?imsi:"0");
		     commonParam.put("op", op);
		     commonParam.put("lang", lang);
		     commonParam.put("net", net);
		     commonParam.put("appver", appver!=null?appver:"unknown");
		     commonParam.put("ext", ext);
		   //  commonParam.put("category", category);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return commonParam.toString(); 
		return commonParam.toString();
		
	}

	public String getCheckVerContent(String version){
		JSONObject content = new JSONObject();
//		String bldver = SystemProperties.get("1.0.3"); //老祝提供的当前飞机里面飞控的版本号
//		String bldid1 = SystemProperties.get("wurenji");//厂商，固定不变
//		String bldid2 = SystemProperties.get("GeniusIdea");//客户，固定不变
//		String bldid3 = SystemProperties.get("FollowME");//项目名称，固定不变
//		String bldid4 = SystemProperties.get("FollowMe");//型号，固定不变

//		String bldver = SharedPreferenceManager.getInstance().getSnavVersion(); //老祝提供的当前飞机里面飞控的版本号
//		String bldver = "1.0.3"; //老祝提供的当前飞机里面飞控的版本号
		String bldver = version; // 要下载的版本号标识
		String bldid1 = "wurenji";//厂商，固定不变
		String bldid2 = "JGW";//客户，固定不变
		String bldid3 = "T3";//项目名称，固定不变
		String bldid4;
		if(checkNotice) {
			bldid4 = "DAPP";//型号，固定不变
		} else {
			bldid4 = "DAPP";//型号，固定不变
		}
		try {
			content.put("comm", new JSONObject(getCommonParam()));
			content.put("bldver", bldver);
			content.put("bldid1", bldid1);
			content.put("bldid2", bldid2);
			content.put("bldid3", bldid3);
			content.put("bldid4", bldid4);

			MyLog.d(LOGTAG,"getCheckVerContent:"+content.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content.toString();
	}

	public boolean isCheckNotice() {
		return checkNotice;
	}

	public void setCheckNotice(boolean checkNotice) {
		this.checkNotice = checkNotice;
	}

	private  void checkUpdateSuccessfully(){
		//sSuccess = true;
	
	}
	public VersionInfo checkUpdate(String version){

		URL url;
		CheckVersion mCheckVersion= new CheckVersion();
		HttpURLConnection urlConn;
		String urlStr = getCheckNewVerUrl();
		
		try {
			url = new URL(urlStr);
		} catch (Exception e) {
		MyLog.i(LOGTAG, "checkUpdate create URL exception!");
			return null;
		}
		
		boolean isChecked = false;//是否已经成功联网检查
		try {
			urlConn = (HttpURLConnection) url.openConnection();
			// 设置超时时间
			urlConn.setRequestMethod("POST");
			urlConn.setConnectTimeout(NETTIMEOUTLONG);
			urlConn.setReadTimeout(NETTIMEOUTLONG);
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			urlConn.setUseCaches(false);
			urlConn.setRequestProperty("Content-Type", "text/xml");
			urlConn.connect();
			
			DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
			out.writeBytes(encodeToNetString(getCheckVerContent(version)));
			out.flush();
			out.close();
			
			isChecked = true;
			
			int responseCode = urlConn.getResponseCode();
			MyLog.i("responseCode = "+responseCode);
			if ( (206 != responseCode) && (200 != responseCode)){
				return null;
			}else{
				InputStream inPutSteam = urlConn.getInputStream();
				InputStreamReader inr= new InputStreamReader(inPutSteam);
				BufferedReader br = new BufferedReader(inr);
				StringBuilder recStr = new StringBuilder();
				
				String readLine = null;
				while((readLine = br.readLine()) !=null){
					recStr.append(readLine);
				}
				inPutSteam.close();
				inr.close();
				br.close();
				urlConn.disconnect();
				MyLog.i("recStr = "+recStr);
				mCheckVersion.paraseNetData(recStr.toString());	
				checkUpdateSuccessfully();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		
		
		return mCheckVersion.getVerInfo();
	}
}
