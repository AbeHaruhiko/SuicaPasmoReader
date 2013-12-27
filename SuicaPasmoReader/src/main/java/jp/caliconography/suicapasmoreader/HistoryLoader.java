package jp.caliconography.suicapasmoreader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import net.kazzz.AbstractNfcTagFragment;
import net.kazzz.felica.NfcFeliCaTagFragment;

import java.util.ArrayList;
import java.util.HashMap;

import jp.caliconography.suicapasmoreader.suica.HistoryBean;

/**
 * Created by abeharuhiko on 2013/12/26.
 */
public class HistoryLoader extends AsyncTaskLoader {

    private Context mContext = null;
    private AbstractNfcTagFragment mLastFfagment;
    private ArrayList<HistoryBean> mHistories;

    public HistoryLoader(Context context, AbstractNfcTagFragment lastFfagment) {
        super(context);
        this.mContext = context;
        this.mLastFfagment = lastFfagment;
    }

    @Override
    public HashMap<String, Object> loadInBackground() {
        HashMap<String, Object> historiesMap = new HashMap<String, Object>();
        try {
            if ( mLastFfagment != null && mLastFfagment instanceof NfcFeliCaTagFragment) {
                NfcFeliCaTagFragment nfcf = (NfcFeliCaTagFragment)mLastFfagment;

                historiesMap.put("dump", nfcf.dumpFeliCaHistoryData());
                historiesMap.put("histories", nfcf.getFeliCaHistoryData());
            } else {
                historiesMap.put("dump", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historiesMap;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
