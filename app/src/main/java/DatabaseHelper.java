import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "messages.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE messages (_id INTEGER PRIMARY KEY, body TEXT, is_spam INTEGER)");
        // İlk defa veritabanı oluşturulduğunda tablolar ve başlangıç verileri burada oluşturulur.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Veritabanı versiyonu güncellendiğinde eski verileri sil ve yeni yapıyı kur.
        db.execSQL("DROP TABLE IF EXISTS messages");
        onCreate(db);
    }
}
