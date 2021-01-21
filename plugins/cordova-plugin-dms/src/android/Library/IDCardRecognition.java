package com.Library;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class IDCardRecognition extends Thread {
	private String TAG = getClass().getSimpleName();
	private Activity mContext;
	private boolean running = false;
	private IDCardManager mIDCardManager;
//	private String mIdCardNo = "";
	private FileUtils mFileUtils;
	
	private boolean isCardFound = false;
	
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 1) {
				mIDCardRecListener.onResp((IDCardMsg)msg.obj);
			} else {
				mIDCardRecListener.onResp(null);
			}
		};
	};
	
	public IDCardRecognition(Activity context, IDCardRecListener listener) {
		mContext = context;
		mIDCardRecListener = listener;
		running = true;
		
		mFileUtils = new FileUtils();
	}

	public static byte[] Bitmap2Bytes(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public void close() {
		running = false;
	}
	
	@Override
	public void run() {
		super.run();
		Looper.prepare();
		while (this.running) {
			mHandler.sendEmptyMessage(2);
			if(mIDCardManager == null) {
				init();
				sleepTime(2000);//5000
			} else {
				int ret = mIDCardManager.SDT_GetSAMStatus();
				if(ret != 0X90) {
					init();
					sleepTime(500);//2000
				}
				try {
					IDCardMsg msg = mIDCardManager.getIDCardMsg();
//					if(!isCardFound) {
						isCardFound = true;
						mFileUtils.saveCardImg(Bitmap2Bytes(msg.getPhoto()));
						mHandler.sendMessage(mHandler.obtainMessage(1, msg));
//					}
				} catch (IDCardException e) {
					isCardFound = false;
//					GrgLog.w(TAG, "IDCardException", e);
				}
//				GrgLog.w(TAG, "ret = " + ret + ", isCardFound = " + isCardFound);
				sleepTime(200);//1000
			}
		}
	}
	
	
	private void init() {
		try {
			mIDCardManager = new IDCardManager(mContext);
			mIDCardManager.SDT_ResetSAM();
			Log.i(TAG, "init IDCardManager");
		} catch (Exception e) {
			Log.w(TAG, "IDCardRecognition", e);
		}
	}
	
	private void sleepTime(long time) {
		try {
			sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	
	private IDCardRecListener mIDCardRecListener;
	
	public interface IDCardRecListener {
		public void onResp(IDCardMsg info);
	}

}
