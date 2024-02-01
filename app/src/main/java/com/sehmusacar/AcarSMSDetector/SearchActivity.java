package com.sehmusacar.AcarSMSDetector;
// kullanıcı arayüzü etkileşimleri ile list/recycler view için adapter
// ayarlamayı içerir. Bu sınıf, uygulama içinde arama işlevselliğini yönetiyor olabilir.
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.smsspamdetector.R;
import com.sehmusacar.AcarSMSDetector.Adapter.SearchAdapter;
import com.sehmusacar.AcarSMSDetector.Utils.SearchData;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "sExp";
    EditText searchInput;
    ArrayList<SearchData> searchData;

    DatabaseManager databaseManager;

    RecyclerView recyclerView;
    SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchInput = findViewById(R.id.search_message);
        searchInput.setFocusable(true);
        ViewCompat.setTransitionName(searchInput, "transition");

        recyclerView = findViewById(R.id.searched_data);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchData = new ArrayList<>();
        adapter = new SearchAdapter(SearchActivity.this, searchData, "");

        recyclerView.setAdapter(adapter);

        searchInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);

        searchData = new ArrayList<>();
        databaseManager = new DatabaseManager(getApplicationContext());
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Yazı değişmeden önce yapılan işlemler
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Yazı değiştiğinde yapılan işlemler
                searchData.clear();
                getSearchData(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Yazı değiştikten sonra yapılan işlemler
            }
        });
    }

    private void getSearchData(String searchText) {
        // Veritabanından arama sonuçlarını al ve listeyi güncelle
        Cursor header = databaseManager.getSearchAddress(searchText);
        Cursor message = databaseManager.getSearchMessage(searchText);

        ArrayList<SearchData> headerSearch = new ArrayList<>();
        ArrayList<SearchData> msgSearch = new ArrayList<>();

        Log.e(TAG, "getSearchData: h = " + header.getCount());
        Log.e(TAG, "getSearchData: m = " + message.getCount());

        if (header.moveToFirst() && message.moveToFirst()) {
            int index_header_id = header.getColumnIndex(DatabaseManager.M_ID_ID);
            int index_header_name = header.getColumnIndex(DatabaseManager.M_ID_ADDRESS_NAME);

            int index_msg_id = message.getColumnIndex(DatabaseManager.M_ID);
            int index_msg_body = message.getColumnIndex(DatabaseManager.M_MESSAGE);

            do {
                if (!header.isAfterLast()) {
                    Log.e(TAG, "getSearchData: h da =" + header.getString(index_header_id));
                    SearchData temp = new SearchData();
                    temp.setMessageID(header.getString(index_header_id));
                    temp.setMessage(header.getString(index_header_name));
                    temp.setType(0);
                    headerSearch.add(temp);
                }

                if (!message.isAfterLast()) {
                    Log.e(TAG, "getSearchData: msg data = " + message.getPosition());
                    SearchData temp = new SearchData();
                    temp.setMessageID(message.getString(index_msg_id));
                    temp.setMessage(message.getString(index_msg_body));
                    temp.setType(1);
                    msgSearch.add(temp);
                }
            } while (header.moveToNext() && message.moveToNext());

            if (!header.isClosed() || !message.isClosed()) {
                header.close();
                message.close();
            }

            searchData.addAll(headerSearch);
            searchData.addAll(msgSearch);

            Log.e(TAG, "getSearchData: size" + searchData.size());
            adapter.UpdateList(searchData);
        }
    }
}
