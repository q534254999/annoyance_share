package com.example.shares;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Blaine on .
 */
public abstract class HttpTask {

    private final String TAG = "异步HTTP通信:";
    private String mApiUrl;
    private Map<String,String> mStringParams = null;
    private Map<String,File> mFileParams = null;
//    private Activity mActivity;

    public HttpTask() {
        mStringParams = new HashMap<>();
        mFileParams = new HashMap<>();
    }

    public void clearParams(){
        mStringParams.clear();
        mFileParams.clear();
    }

    public HttpTask url(String apiUrl) {
        this.mApiUrl = apiUrl;
        return this;
    }

    public HttpTask addParams(String key, String value) {
        mStringParams.put(key, value);
        return this;
    }

    public HttpTask addParams(String key, File value) {
        mFileParams.put(key,value);
        return this;
    }

    public void sendRequest() {
        _HttpTask _httpTask = new _HttpTask(this,this.mStringParams,this.mFileParams);
        _httpTask.execute(this.mApiUrl);
    }

    private void _callback(JSONObject jsonObject) {
        if(jsonObject==null) {
            Log.d(TAG, "通信失败");
        } else {
            callback(this.mApiUrl, jsonObject);
        }
    }

    public abstract void callback(String apiUrl, JSONObject jsonObject);
    public abstract void progressCallback(int progress);

    private class _HttpTask extends AsyncTask<String, Integer, String> {

        private HttpTask mHttpTask = null;
        private Map<String,String> mStringParams = null;
        private Map<String,File> mFileParams = null;

        private OkHttpClient client;

        public _HttpTask(HttpTask mHttpTask,
                         Map<String,String> mStringParams,
                         Map<String,File> mFileParams) {
            this.mHttpTask = mHttpTask;
            this.mStringParams = mStringParams;
            this.mFileParams = mFileParams;

            this.client = new OkHttpClient();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            String url = null;
            Request request = null;
            if (params.length > 0) url = params[0];

            try {
                if (this.mFileParams.size() > 0) {
                    MultipartBody.Builder mBuilder = new MultipartBody.Builder();
                    mBuilder.setType(MultipartBody.FORM);
                    for (Map.Entry<String,String> entry:mStringParams.entrySet()) {
                        mBuilder.addFormDataPart(entry.getKey(), entry.getValue());
                    }
                    for (Map.Entry<String,File> entry:mFileParams.entrySet()) {
                        mBuilder.addFormDataPart(entry.getKey(),
                                entry.getValue().getAbsolutePath(),
                                RequestBody.create(null, entry.getValue()));
                    }
                    RequestBody requestBody = mBuilder.build();
                    request = new Request.Builder().url(url).post(requestBody).build();

                } else {
                    //不带文件的POST
                    RequestBody formBody;
                    FormBody.Builder builder = new FormBody.Builder();
                    for (Map.Entry<String,String> entry:mStringParams.entrySet()) {
                        builder.add(entry.getKey(),entry.getValue());
                    }
                    formBody = builder.build();
                    request = new Request.Builder().url(url).post(formBody).build();
                }

                Response response = client.newCall(request).execute();

                if(response.isSuccessful()) {
                    if (response.body() != null) {
                        result = response.body().string();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
            //完成后，解析并回调
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException je) {
                jsonObject = null;
                je.printStackTrace();
            }
            this.mHttpTask._callback(jsonObject);
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //进度回调
            if(values.length > 0) progressCallback(values[0]);
            super.onProgressUpdate(values);
        }
    }
}
