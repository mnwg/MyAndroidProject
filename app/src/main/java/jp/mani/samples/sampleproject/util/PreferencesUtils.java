package jp.mani.samples.sampleproject.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * <p>{@link SharedPreferences}へのアクセスを助けるクラス</p>
 * <p>{@code SharedPreferences}を利用する際の一般的な注意点</p>
 * <ul>
 *     <li>{@link SharedPreferences.Editor#apply()}は非同期のファイルアクセス</li>
 *     <li>{@link SharedPreferences.Editor#commit()}は同期のファイルアクセス</li>
 *     <li>プロファレンスから取得した値への書き込みアクセスをしてはいけない</li>
 *     <li>複数Threadからの同時アクセスには対応できていない</li>
 * </ul>
 */
public class PreferencesUtils {

    /**
     * {@link Context#MODE_PRIVATE}で{@link SharedPreferences}を取得します。
     *
     * @param context コンテキスト
     * @param name プリファレンスの名前空間（ファイル名）
     * @return {@link SharedPreferences}のインスタンス
     */
    public static SharedPreferences from(final Context context, final String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * <p>プリファレンスの書き込みを行います。</p>
     * <p>{@link SharedPreferences.Editor}の取得と、最後の{@link SharedPreferences.Editor#apply()}をラップしているだけなので、
     * 複数の編集を行う場合など、編集内容だけを記述したい時に利用できます。</p>
     * <p>{@link SharedPreferences.Editor#apply()}でファイル書き込みを行うため、
     * 結果は即座に反映されますが、ファイルへの書き込みは非同期で行われます。（オンメモリのキャッシュにのみ即時反映される）
     * このため、UI threadでも扱いやすい一方、ファイル書き込みの成否は取得する事ができません。</p>
     *
     * @param context コンテキスト
     * @param name プリファレンスの名前空間（ファイル名）
     * @param invoke プリファレンスの編集を行う実装
     */
    public static void editAsync(final Context context, final String name, final Invocation invoke) {

        final SharedPreferences.Editor editor = from(context, name).edit();
        invoke.invoke(editor);
        editor.apply();
    }

    /**
     * <p>プリファレンスの書き込みを行います。</p>
     * <p>{@link SharedPreferences.Editor}の取得と、最後の{@link SharedPreferences.Editor#commit()} ()}をラップしているだけなので、
     * 複数の編集を行う場合など、編集内容だけを記述したい時に利用できます。</p>
     * <p>{@link SharedPreferences.Editor#commit()} ()}でファイル書き込みを行うため、
     * 即座に（そのThread上で）ファイル書き込みも行いますので、UI threadでの利用はあまり推奨されません。</p>
     *
     * @param context コンテキスト
     * @param name プリファレンスの名前空間（ファイル名）
     * @param invoke プリファレンスの編集を行う実装
     * @return 保存に成功したら{@code true}、失敗したら{@code false}
     */
    public static boolean edit(final Context context, final String name, final Invocation invoke) {

        final SharedPreferences.Editor editor = from(context, name).edit();
        invoke.invoke(editor);
        return editor.commit();
    }

    /**
     * {@link SharedPreferences}の編集用インタフェース
     */
    public interface Invocation {

        /**
         * プリファレンスの編集を行います。
         *
         * @param editor {@link android.content.SharedPreferences.Editor}
         */
        void invoke(final SharedPreferences.Editor editor);
    }
}
