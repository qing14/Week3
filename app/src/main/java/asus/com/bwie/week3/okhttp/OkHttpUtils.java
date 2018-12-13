package asus.com.bwie.week3.okhttp;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpUtils {

    private static OkHttpUtils instance;

    private Handler mHandle=new Handler(Looper.getMainLooper());
    private  OkHttpClient client;

    public static OkHttpUtils getInstance() {
        if(instance==null){
            synchronized (OkHttpUtils.class){
                if(null==instance){
                    instance=new OkHttpUtils();
                }
            }
        }
        return instance;
    }

    private OkHttpUtils(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS);
        client = new OkHttpClient.Builder()
                .connectTimeout(10,TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

    }
    //post请求
    public void getqueue(String dataUrl, Map<String,String> param, final Class clazz, final ICallBack callBack){
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String,String> entry:param.entrySet()){
            builder.add(entry.getKey(),entry.getValue());
        }
        RequestBody build = builder.build();

        Request builder1 = new Request.Builder()
                .url(dataUrl)
                .post(build)
                .build();

        Call call = client.newCall(builder1);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                mHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.getFaile(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String string = response.body().string();
                final Object o = new Gson().fromJson(string, clazz);
                mHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.getResponse(o);
                    }
                });
            }
        });

    }
}
