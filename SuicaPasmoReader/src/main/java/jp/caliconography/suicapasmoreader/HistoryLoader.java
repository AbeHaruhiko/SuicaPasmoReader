package jp.caliconography.suicapasmoreader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import net.kazzz.AbstractNfcTagFragment;
import net.kazzz.felica.NfcFeliCaTagFragment;

import java.util.ArrayList;

import jp.caliconography.suicapasmoreader.suica.HistoryBean;

/**
 * Created by abeharuhiko on 2013/12/26.
 */
public class HistoryLoader extends AsyncTaskLoader {

    private Context mContext = null;
    private AbstractNfcTagFragment mLastFfagment;
    private ArrayList<HistoryBean> mHistories;

    public HistoryLoader(Context context, AbstractNfcTagFragment lastFfagment, ArrayList<HistoryBean> histories) {
        super(context);
        this.mContext = context;
        this.mLastFfagment = lastFfagment;
        this.mHistories = histories;
    }

    @Override
    public String loadInBackground() {
        try {
            if ( mLastFfagment != null && mLastFfagment instanceof NfcFeliCaTagFragment) {
                NfcFeliCaTagFragment nfcf = (NfcFeliCaTagFragment)mLastFfagment;
                mHistories = nfcf.getFeliCaHistoryData();
                return nfcf.dumpFeliCaHistoryData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
