package jp.mani.samples.sampleproject.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteDatabaseのtransactionをハンドリングするクラス
 */
public abstract class TransactionHandler {

    /**
     * コンストラクタで与えられたSQLiteOpenHelper
     */
    protected final SQLiteOpenHelper mHelper;

    /**
     * コンストラクタ
     *
     * @param helper {@code null}は許容しません
     */
    protected TransactionHandler(final SQLiteOpenHelper helper) {
        mHelper = helper;
    }

    /**
     * DBのトランザクションを実行します
     *
     * @param invocation DBアクセスの実装
     * @param <T>        返却したい型を指定してください
     * @return 返却値（任意）
     */
    public abstract <T> T execute(final TransactionHandler.Invocation<T> invocation);

    /**
     * トランザクションで行うDBアクセスを実装するインタフェース
     *
     * @param <T> 結果として返却したい値の型を指定してください
     */
    public interface Invocation<T> {

        /**
         * トランザクションで行うDBアクセスの実装
         *
         * @param db トランザクションを張ったDB
         * @return 返却したい型
         */
        T invoke(final SQLiteDatabase db);
    }
}
