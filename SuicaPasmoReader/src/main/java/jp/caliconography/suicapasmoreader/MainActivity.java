package jp.caliconography.suicapasmoreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.kazzz.AbstractNfcTagFragment;
import net.kazzz.felica.FeliCaException;
import net.kazzz.felica.NfcFeliCaTagFragment;
import net.kazzz.felica.lib.FeliCaLib;

import java.util.ArrayList;

import jp.caliconography.suicapasmoreader.suica.HistoryBean;

public class MainActivity extends ActionBarActivity  implements AbstractNfcTagFragment.INfcTagListener {

    private String TAG = this.getClass().getSimpleName();
    private AbstractNfcTagFragment mLastFragment;
    private NfcFeliCaTagFragment mFeliCafragment;

    private ArrayList<HistoryBean> mHistories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        //FeliCa, FeliCaLite
        mFeliCafragment = new NfcFeliCaTagFragment(this);
        mFeliCafragment.addNfcTagListener(this);

        //インテントから起動された際の処理
        Intent intent = this.getIntent();
        this.onNewIntent(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;

        switch (item.getItemId()){ // if使うとエラー（itemがInt形式なため）
            case android.R.id.home:   // アプリアイコン（ホームアイコン）を押した時の処理
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.action_save:

                try {
                    intent = new Intent(getApplicationContext(), DriveHelper.class);
                    intent.putExtra("histories", mHistories);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.action_refresh:
                readHistory();
        }


        return super.onOptionsItemSelected(item);
    }

    private void readHistory() {
        try {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);

            AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                @Override
                protected void onPreExecute() {
                    dialog.setMessage(getString(R.string.MES_READING_HISTORY));
                    dialog.show();
                }

                @Override
                protected String doInBackground(Void... arg0) {
                    try {
                        if ( mLastFragment != null && mLastFragment instanceof NfcFeliCaTagFragment) {
                            NfcFeliCaTagFragment nfcf = (NfcFeliCaTagFragment)mLastFragment;
                            mHistories = nfcf.getFeliCaHistoryData();
                            return nfcf.dumpFeliCaHistoryData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return "";
                }

                /* (non-Javadoc)
                 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
                 */
                @Override
                protected void onPostExecute(String result) {
                    dialog.dismiss();
                    TextView tv_tag = (TextView) findViewById(R.id.result_tv);
                    if (result != null && result.length() > 0) tv_tag.setText(result);
                }
            };

            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }



    /* (non-Javadoc)
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {

        if ( mFeliCafragment != null ) {
            mFeliCafragment.onNewIntent(intent);
        }
    }
    /* (non-Javadoc)
     * @see net.kazzz.NfcTagFragment.INfcTagListener#onTagDiscovered(android.content.Intent, android.os.Parcelable)
     */
    @Override
    public void onTagDiscovered(Intent intent, Parcelable nfcTag, AbstractNfcTagFragment fragment) {

        try {

            mLastFragment = fragment;

            //フラグメントの判定
            if ( mLastFragment instanceof NfcFeliCaTagFragment ) {
                NfcFeliCaTagFragment nff = (NfcFeliCaTagFragment)mLastFragment;
                boolean isFeliCatLite = nff.isFeliCaLite();
                try {
                    FeliCaLib.IDm idm =
                            new FeliCaLib.IDm(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));

                    if ( idm == null ) {
                        throw new FeliCaException(getString(R.string.MES_FAILED_TO_GET_FELICA_ID));
                    }

                    readHistory();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
            } else {
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }


}
