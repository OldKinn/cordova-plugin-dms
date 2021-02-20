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

import com.Library.IDCardMsg;
import com.Library.IDCardRecognition;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

/**
 * This class echoes a string called from JavaScript.
 */
public class DMSPlugin extends CordovaPlugin {

    private IDCardRecognition mIDCardRecognition;
    private CallbackContext callbackContextx;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContextx = callbackContext;
        if (action.equals("startReading")) {
            this.startReading(callbackContext);
            return true;
        } else if (action.equals("stopReading")) {
            this.stopReading(callbackContext);
            return true;
        } else if (action.equals("startSignActive")) {
            JSONObject obj = args.optJSONObject(0);
            this.startSignActive(obj, callbackContext);
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
    // 签名功能
    private void startSignActive(JSONObject fileObj, CallbackContext callbackContext) throws JSONException {
        Intent intent = new Intent();
        ComponentName cn = new ComponentName("com.example.fenglinsignapp", "com.example.fenglinsignapp.MainActivity");
        intent.setComponent(cn);
        intent.putExtra("url", fileObj.getString("url"));
        intent.putExtra("name", fileObj.getString("name"));
        intent.putExtra("downloadPath", fileObj.getString("downloadPath"));
        intent.putExtra("isReadOnly", fileObj.getString("isReadOnly"));
        intent.setAction("android.intent.action.MAIN");
        cordova.startActivityForResult(this,intent,1);
    }
    //onActivityResult为第二个Activity执行完后的回调接收方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (resultCode) { //resultCode为回传的标记
            case 1:
                Bundle b=data.getExtras();  //data为第二个Activity中回传的Intent
                String pdfPath=b.getString("pdf");//pdf即为回传的值
                if(pdfPath == null) {
                    return;
                }
                try {
                    JSONObject result = new JSONObject();
                    result.put("success", true);
                    result.put("pdfPath", pdfPath);
                    this.callbackContextx.success(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    this.callbackContextx.error(e.getMessage());
                }
                break;
            default:
                break;
        }
    }
}
