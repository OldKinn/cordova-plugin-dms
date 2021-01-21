package jni;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class JniCall {
	private static native byte[] wlt2bmp(byte[] wlt);

	static {
		System.loadLibrary("fenglin_usb_sdt");
	}

    public static Bitmap com_Wlt2Bmp(byte[] wlt) {
		byte[] temp = wlt2bmp(wlt);
		Bitmap bmp = BitmapFactory.decodeByteArray(temp,0,temp.length).copy(Bitmap.Config.ARGB_8888, true);;
		return bmp;
	}
}

