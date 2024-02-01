package com.sehmusacar.AcarSMSDetector;
//Veritabanı yönetimiyle ilgili işlemleri içerir.
//Bu sınıf veritabanı oluşturma, okuma, güncelleme ve silme işlemlerini yönetir.
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.sehmusacar.AcarSMSDetector.Utils.MsgData;

public class DatabaseManager extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MessageHandler.db";

    public static final String TABLE_NAME_MESSAGES = "messages";
    public static final String TABLE_NAME_MESSAGE_ID = "message_ids";

    public static final String M_ID = "id";
    public static final String M_ADDRESS_ID = "sender_address_id";
    public static final String M_MESSAGE = "message";
    public static final String M_TIME = "milli_time";
    public static final String M_IS_SPAM = "is_spam";
    public static final String M_IS_CHECKED = "is_checked";

    public static final String M_ID_ID = "id";
    public static final String M_ID_ADDRESS_NAME = "address_name";
    public static final String M_ID_LAST_TIME = "last_time";
    public static final String M_ID_IS_SPAM = "is_spam";
    public static final String M_ID_LAST_MESSAGE = "last_message";
    public static final String M_ID_SERVICE_NUMBER = "service_number";

    public DatabaseManager(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Mesajlar tablosunu oluştur
        String create_message_table = "CREATE TABLE " + TABLE_NAME_MESSAGES + "("
                + M_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + M_ADDRESS_ID + " INTEGER,"
                + M_MESSAGE + " TEXT,"
                + M_TIME + " INTEGER,"
                + M_IS_SPAM + " INTEGER,"
                + M_IS_CHECKED + " INTEGER)";
        sqLiteDatabase.execSQL(create_message_table);

        // Mesaj kimlikleri tablosunu oluştur
        String create_message_id_table = "CREATE TABLE " + TABLE_NAME_MESSAGE_ID + "("
                + M_ID_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + M_ID_ADDRESS_NAME + " TEXT,"
                + M_ID_SERVICE_NUMBER + " TEXT,"
                + M_ID_LAST_MESSAGE + " TEXT,"
                + M_ID_LAST_TIME + " INTEGER,"
                + M_ID_IS_SPAM + " INTEGER)";
        sqLiteDatabase.execSQL(create_message_id_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Güncelleme işlemi yapılacaksa burada tanımlayabilirsiniz.
    }

    // Mesajları veritabanına eklemek için kullanılan metod
    public int InsertMessages(MsgData msgData, int is_checked, int is_spam) {
        SQLiteDatabase db_write = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(M_ADDRESS_ID, msgData.getId());
        values.put(M_MESSAGE, msgData.getMsgBody());
        values.put(M_TIME, msgData.getMsgDateLong());
        values.put(M_IS_SPAM, is_spam);
        values.put(M_IS_CHECKED, is_checked);

        return (int) db_write.insert(TABLE_NAME_MESSAGES, null, values);
    }

    // Adres adını ve diğer bilgileri eklemek için kullanılan metod
    public int insertAddressName(String address, String message, long time_milli, String serviceNum) {
        SQLiteDatabase db_write = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(M_ID_ADDRESS_NAME, address);
        values.put(M_ID_SERVICE_NUMBER, serviceNum);
        values.put(M_ID_LAST_MESSAGE, message);
        values.put(M_ID_LAST_TIME, time_milli);
        values.put(M_ID_IS_SPAM, 0);

        return (int) db_write.insert(TABLE_NAME_MESSAGE_ID, null, values);
    }

    // Son zamanı güncellemek için kullanılan metod
    public void updateLastTime(int m_address_id, long time_milli, String message) {
        SQLiteDatabase db_write = this.getWritableDatabase();

        ContentValues m_update = new ContentValues();
        m_update.put(M_ID_LAST_TIME, time_milli);
        m_update.put(M_ID_LAST_MESSAGE, message);

        db_write.update(TABLE_NAME_MESSAGE_ID, m_update, "id = ?", new String[]{String.valueOf(m_address_id)});
    }

    // Veritabanında adres var mı kontrol etmek için kullanılan metod
    public int messageAddressPresent(String address) {
        SQLiteDatabase db_read = this.getReadableDatabase();

        String query = "SELECT " + M_ID_ID + " FROM " + TABLE_NAME_MESSAGE_ID + " WHERE " + M_ID_ADDRESS_NAME + " = ?";
        Cursor cursor = db_read.rawQuery(query, new String[]{address});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            int index_message = cursor.getColumnIndex(M_ID_ID);
            int message_id = cursor.getInt(index_message);
            cursor.close();
            return message_id;
        }

        cursor.close();

        return -1;
    }

    // Spam kontrolü ve işaretlenme bilgisini güncellemek için kullanılan metod
    public void UpdateSpamChecked(int message_id, int is_spam, int is_checked, int table) {
        SQLiteDatabase db_write = this.getWritableDatabase();
        ContentValues update_values = new ContentValues();
        if (table == 0) {
            update_values.put(M_IS_SPAM, is_spam);
            update_values.put(M_IS_CHECKED, is_checked);
            db_write.update(TABLE_NAME_MESSAGES, update_values, "id = ?", new String[]{String.valueOf(message_id)});
        } else if (table == 1) {
            update_values.put(M_ID_IS_SPAM, is_spam);
            db_write.update(TABLE_NAME_MESSAGE_ID, update_values, "id = ?", new String[]{String.valueOf(message_id)});
        }
    }

    // Veritabanından en son mesaj bilgilerini almak için kullanılan metod
    public MsgData getMessagesFromDB() {
        SQLiteDatabase db_read = this.getReadableDatabase();

        MsgData msgData = new MsgData();

        String query = "SELECT " + M_ID_ADDRESS_NAME + "," + M_ID_LAST_TIME + " FROM "
                + TABLE_NAME_MESSAGE_ID + " ORDER BY " + M_ID_LAST_TIME + " DESC LIMIT 1";
        Cursor cursor = db_read.rawQuery(query, null);

        cursor.moveToFirst();

        int index_title = cursor.getColumnIndex(M_ID_ADDRESS_NAME);
        int index_date = cursor.getColumnIndex(M_ID_LAST_TIME);

        msgData.setMsgTitle(cursor.getString(index_title));
        msgData.setMsgDateLong(cursor.getLong(index_date));
        cursor.close();
        return msgData;
    }

    // Tüm adresleri getirmek için kullanılan metod
    public Cursor getAllMsgAddressTable() {
        SQLiteDatabase db_read = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_MESSAGE_ID + " ORDER BY " + M_ID_LAST_TIME + " DESC";
        return db_read.rawQuery(query, null);
    }

    // Yeni zamanı almak için kullanılan metod
    public long GetNewLong(int messageId) {
        SQLiteDatabase dbRead = this.getReadableDatabase();

        String query = "SELECT " + M_ID_LAST_TIME + " FROM " + TABLE_NAME_MESSAGE_ID + " WHERE " + M_ID_ID + " = ?";

        Cursor cursor = dbRead.rawQuery(query, new String[]{String.valueOf(messageId)});

        cursor.moveToFirst();

        int index_time = cursor.getColumnIndex(M_ID_LAST_TIME);

        long timeLong = cursor.getLong(index_time);
        cursor.close();

        return timeLong;
    }

    // Belirli bir adresin tüm mesajlarını getirmek için kullanılan metod
    public Cursor getAllAddressMessage(String addressId) {
        SQLiteDatabase dbRead = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME_MESSAGES + " WHERE " + M_ADDRESS_ID + " = ? ORDER BY " + M_TIME + " DESC";

        return dbRead.rawQuery(query, new String[]{addressId});
    }

    // Belirli bir adrese göre arama yapmak için kullanılan metod
    public Cursor getSearchAddress(String address) {
        String query = "SELECT * FROM " + TABLE_NAME_MESSAGE_ID + " WHERE " + M_ID_ADDRESS_NAME + " LIKE '%" + address + "%' LIMIT 10";

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        return cursor;
    }

    // Belirli bir mesaja göre arama yapmak için kullanılan metod
    public Cursor getSearchMessage(String msg) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_MESSAGES + " WHERE " + M_MESSAGE + " LIKE '%" + msg + "%' LIMIT 10";
        Cursor cursor = database.rawQuery(query, null);

        return cursor;
    }
}
