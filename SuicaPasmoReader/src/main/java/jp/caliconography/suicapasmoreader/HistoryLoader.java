package jp.caliconography.suicapasmoreader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import net.kazzz.AbstractNfcTagFragment;
import net.kazzz.felica.NfcFeliCaTagFragment;

import java.util.ArrayList;

import jp.caliconography.suicapasmoreader.suica.HistoryBean;

/**
 * Created by abe on 2013/12/26.
 */
public class HistoryLoader extends AsyncTaskLoader<String> {
    private AbstractNfcTagFragment mLastFragment;
    private ArrayList<HistoryBean> mHistories;

    public HistoryLoader(Context context, AbstractNfcTagFragment lastFragment, ArrayList<HistoryBean> histories) {
        super(context);
        this.mLastFragment = lastFragment;
        this.mHistories = histories;
    }

    @Override
    public String loadInBackground() {
        try {
            if ( mLastFragment != null && mLastFragment instanceof NfcFeliCaTagFragment) {
                NfcFeliCaTagFragment nfcf = (NfcFeliCaTagFragment)mLastFragment;
                mHistories = nfcf.getFeliCaHistoryData();
                return nfcf.dumpFeliCaHistoryData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
