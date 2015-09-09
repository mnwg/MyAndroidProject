package jp.mani.samples.sampleproject.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * {@link SQLiteDatabase}への書き込み用のトランザクションハンドラ
 */
public class WritableTransactionHandler extends TransactionHandler {

    /**
     * コンストラクタ
     *
     * @param helper 操作するDBの{@link SQLiteOpenHelper}
     */
    public WritableTransactionHandler(final SQLiteOpenHelper helper) {
        super(helper);
    }

    @Override
    public <T> T execute(final TransactionHandler.Invocation<T> invocation) {

        final SQLiteDatabase db = mHelper.getWritableDatabase();
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
