package jp.mani.samples.sampleproject.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * <p>{@link AsyncTaskLoader}の共通的な実装を助ける抽象クラス</p>
 * <p>利用方法</p>
 * <ul>
 * <li>{@link #onLoadInBackground()}にロードする処理を実装してください。</li>
 * <li>ロード対象のデータに変更があった場合は、{@link #onContentChanged}で通知してください。</li>
 * <li>ロード中のキャンセルをサポートする場合は、ロード中に定期的に{@link #isLoadInBackgroundCanceled()}を監視し、それが{@code true}を返却してきたらロードを中断するか、{@link #cancelLoadInBackground()}を実装して、ロードをキャンセルしてください。{@link #onLoadInBackground()}のロードがキャンセルされた場合は、そのままデータをreturnに返すか、{@link android.support.v4.os.OperationCanceledException}を返却するかをしなければいけません。どちらにしても、その後に{@link #onCanceled(Object)}がコールされます。</li>
 * </ul>
 */
public abstract class AbsAsyncTaskLoader<D> extends AsyncTaskLoader<D> {

    /**
     * ロードしたデータ
     */
    private D mLoadData;

    /**
     * コンストラクタ
     *
     * @param context {@link Context}
     */
    public AbsAsyncTaskLoader(final Context context) {
        super(context);
    }

    @Override
    public void deliverResult(final D data) {

        if (isReset()) {
            // 既にLoaderがstopされており、ロードしたdataはもう不要なので、解放する
            if (mLoadData != null) {
                onReleaseLoadData(mLoadData);
            }
        }

        final D oldData = mLoadData;
        mLoadData = data;

        if (isStarted()) {
            // Loaderは動作中のため、ロードしたデータを結果に反映する
            super.deliverResult(data);
        }

        onReleaseLoadData(oldData);
    }

    /**
     * ロードを開始する要求をハンドリングします。
     */
    @Override
    protected void onStartLoading() {
        if (mLoadData != null) {
            deliverResult(mLoadData);
        }

        onRegistObserver();

        final boolean isForceLoad = onCheckDataChanged();

        // 次の条件 or 何かしら以前のデータから変更があったら
        if (takeContentChanged() || mLoadData == null || isForceLoad) {
            forceLoad();
        }
    }

    /**
     * Loaderをストップするリクエストをハンドリングします。
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    /**
     * ロードをキャンセルするリクエストをハンドリングします。
     *
     * @param data {@link #onLoadInBackground()} がキャンセルを検知し、ロード中の（or ロード完了した）データをreturnした場合はそのデータ。
     *             {@link android.support.v4.os.OperationCanceledException}をthrowしていたら{@code null}。
     */
    @Override
    public void onCanceled(final D data) {
        super.onCanceled(data);

        onReleaseLoadData(data);
    }

    /**
     * Loaderを完全にリセットするリクエストをハンドリングします。
     */
    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        if (mLoadData != null) {
            onReleaseLoadData(mLoadData);
            mLoadData = null;
        }

        onUnRegistObserver();
    }

    /**
     * ロードしたデータに解放処理が必要な場合(Cursorなど)にこれを実装し、引数のインスタンスを解放してください。
     *
     * @param data 解放するデータ
     */
    protected abstract void onReleaseLoadData(final D data);

    /**
     * ロードしたデータ
     *
     * @return ロード完了前やリセット後などは{@code null}
     */
    protected D getLoadData() {
        return mLoadData;
    }

    /**
     * <p>ロードを開始する判定中にコールされます。
     * 前回ロードした時と条件が違うなど、明らかにロードが必要な場合に{@code true}を返却してください。</p>
     *
     * @return 強制的に再ロードする場合は{@code true}、それ以外は{@code false}を返却してください。
     */
    protected boolean onCheckDataChanged() {
        return false;
    }

    /**
     * 必要があれば、データの変更を監視するObserverを登録してください。
     */
    protected abstract void onRegistObserver();

    /**
     * {@link #onRegistObserver()}で登録したObserverを登録解除してください。
     */
    protected abstract void onUnRegistObserver();
}
