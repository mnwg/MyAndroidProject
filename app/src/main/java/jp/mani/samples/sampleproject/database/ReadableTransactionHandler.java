package jp.mani.samples.sampleproject.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * {@link SQLiteDatabase}への読み込み用のトランザクションハンドラ
 */
public class ReadableTransactionHandler extends TransactionHandler {

    /**
     * コンストラクタ
     *
     * @param helper 操作するDBの{@link SQLiteOpenHelper}
     */
    public ReadableTransactionHandler(final SQLiteOpenHelper helper) {
        super(helper);
    }

    @Override
    public <T> T execute(final Invocation<T> invocation) {

        final SQLiteDatabase db = mHelper.getReadableDatabase();
        try {
            db.beginTransaction();

            final T t = invocation.invoke(db);
            db.setTransactionSuccessful();
            return t;
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
