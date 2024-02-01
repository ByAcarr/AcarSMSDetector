package com.sehmusacar.AcarSMSDetector.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.text.nlclassifier.BertNLClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class CheckSpamUtils {

    private static final String TAG = "CheckSpamExp";
    Handler handler;
    Context context;
    Activity activity;

    InputStream is;

    BertNLClassifier classifier = null;

    public CheckSpamUtils(Context context, Activity activity) {

        this.activity = activity;
        this.context = context;

        handler = new Handler(Looper.getMainLooper());

        setFileData();

    }

    private void setFileData() {

        try {

            AssetManager am = context.getAssets();
            this.is = am.open("model.tflite");
            File file = new File(context.getCacheDir(), "cacheFileAppeal.srl");
            OutputStream output = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024]; // or other buffer size
            int read;

            while ((read = is.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            output.flush();
            BertNLClassifier.BertNLClassifierOptions options =
                    BertNLClassifier.BertNLClassifierOptions.builder()
                            .setBaseOptions(BaseOptions.builder().setNumThreads(4).build())
                            .build();

            classifier = BertNLClassifier.createFromFileAndOptions(file,options);

        } catch (Exception e) {

            Log.e(TAG, "setFileData:" + e.getMessage());

        }

    }

    public synchronized void classify(String text, OnSpamGotListener onSpamGotListener) {

        handler.post(new Runnable() {
            @Override
            public void run() {



                try {

                    List<Category> results = classifier.classify(text);

                    onSpamGotListener.onResult(results);

                } catch (Exception e) {
                    Log.e(TAG, "run: " + e.getMessage());
                }
            }
        });

    }

    public void closeInputStream() throws IOException {

        this.is.close();

    }


    public interface OnSpamGotListener {
        void onResult(List<Category> results);
    }

}
