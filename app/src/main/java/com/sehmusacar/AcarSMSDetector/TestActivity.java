package com.sehmusacar.AcarSMSDetector;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smsspamdetector.R;

import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.text.nlclassifier.BertNLClassifier;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestExp";
    EditText messageInput;
    Button predictBtn;
    TextView result;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Arayüz bileşenlerini tanımla
        messageInput = findViewById(R.id.msg_text_example);
        predictBtn = findViewById(R.id.predict_btn_example);
        result = findViewById(R.id.result_example);

        handler = new Handler();

        // Tahminleme butonuna tıklama işlevi ekle
        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String data = messageInput.getText().toString();
                getDataFromModel(data);

            }
        });

    }

    // Modelden veri almak için işlev
    private void getDataFromModel(String data) {
        classify(data);
    }

    // Metni sınıflandırmak için işlev
    private void classify(final String text) {

        handler.post(
                () -> {

                    BertNLClassifier classifier = null;

                    try {

                        // BertNLClassifier nesnesi oluştur
                        classifier = BertNLClassifier.createFromFile(TestActivity.this,"model.tflite");

                        // Metni sınıflandır
                        List<Category> results = classifier.classify(text);

                        // Sonuçları göster
                        Log.e(TAG, "isSpamMessage: msg : "+text+"\n 0 : "+results.get(0).getLabel() + " : "+results.get(0).getScore()+"\n" );
                        Log.e(TAG, "isSpamMessage: 1 : "+results.get(1).getLabel() + " : "+results.get(1).getScore()+"\n" );

                        String data = results.get(0).getLabel()+":"+results.get(0).getScore();

                        data += "\n"+results.get(1).getLabel()+":"+results.get(1).getScore();

                        result.setText(data);
                    } catch (Exception e){

                        Log.e(TAG, "classify: "+e.getMessage());

                    }

                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }
}
