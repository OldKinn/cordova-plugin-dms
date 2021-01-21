package com.chinacreator.plugin.zzzhzf.persion;

import android.graphics.Bitmap;
import android.util.Base64;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.Library.IDCardMsg;
import com.Library.IDCardRecognition;

/**
 * This class echoes a string called from JavaScript.
 */
public class DMSPlugin extends CordovaPlugin {

    private IDCardRecognition mIDCardRecognition;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("startReading")) {
            this.startReading(callbackContext);
            return true;
        } else if (action.equals("stopReading")) {
            this.stopReading(callbackContext);
            return true;
        }
        return false;
    }

    private void startReading(CallbackContext callbackContext) {
        mIDCardRecognition = new IDCardRecognition(cordova.getActivity(), new IDCardRecognition.IDCardRecListener() {
            @Override
            public void onResp(IDCardMsg info) {
                if(info == null) {
                    return;
                }
                try {
                    JSONObject data = new JSONObject();
                    data.put("name", info.getName());
                    data.put("address", info.getAddress());
                    data.put("birthday", info.getBirthDate());
                    data.put("cardNum", info.getIdCardNum());
                    data.put("nation", info.getNationStr());
                    data.put("sex", info.getSexStr());
                    data.put("start", info.getUsefulStartDate());
                    data.put("end", info.getUsefulEndDate());
                    data.put("signOffice", info.getSignOffice());
                    data.put("avatar", DMSPlugin.bitmapToBase64(info.getPhoto()));
                    callbackContext.success(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callbackContext.error(e.getMessage());
                }
            }
        });
        mIDCardRecognition.start();
    }

    private void stopReading(CallbackContext callbackContext) {
        mIDCardRecognition.close();
        JSONObject data = new JSONObject();
        try {
            data.put("success", true);
            callbackContext.success(data);
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
    }

    private static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "data:image/jpg;base64," + result;
    }
}
