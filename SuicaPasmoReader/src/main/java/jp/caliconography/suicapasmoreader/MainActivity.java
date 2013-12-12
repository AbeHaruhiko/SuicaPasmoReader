package jp.caliconography.suicapasmoreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;

import net.kazzz.AbstractNfcTagFragment;
import net.kazzz.felica.FeliCaException;
import net.kazzz.felica.NfcFeliCaTagFragment;
import net.kazzz.felica.lib.FeliCaLib;

public class MainActivity extends ActionBarActivity  implements View.OnClickListener, AbstractNfcTagFragment.INfcTagListener {

    private String TAG = this.getClass().getSimpleName();
    private AbstractNfcTagFragment mLastFragment;
    private NfcFeliCaTagFragment mFeliCafragment;

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
        switch (item.getItemId()){ // if使うとエラー（itemがInt形式なため）
            case android.R.id.home:   // アプリアイコン（ホームアイコン）を押した時の処理
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.action_refresh:



                try {
                    final ProgressDialog dialog = new ProgressDialog(this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setIndeterminate(true);

                    AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                        @Override
                        protected void onPreExecute() {
                            dialog.setMessage("読み込み中...");
                            dialog.show();
                        }

                        @Override
                        protected String doInBackground(Void... arg0) {
                            try {
                                if ( mLastFragment != null && mLastFragment instanceof NfcFeliCaTagFragment) {
                                    NfcFeliCaTagFragment nfcf = (NfcFeliCaTagFragment)mLastFragment;
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


        return super.onOptionsItemSelected(item);
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



    public void onClick(final View v) {
        try {
            final int id = v.getId();

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);

            AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                @Override
                protected void onPreExecute() {
                    switch (id) {
                        case R.id.btn_read:
                            dialog.setMessage("読み込み処理を実行中です...");
                            break;
                        case R.id.btn_write:
                            dialog.setMessage("書き込み画面に移動中です...");
                            break;
                        case R.id.btn_hitory:
                            dialog.setMessage("使用履歴を読み込み中です...");
                            break;
                    }
                    dialog.show();
                }

                @Override
                protected String doInBackground(Void... arg0) {
                    switch (id) {
                        case R.id.btn_read:
                            try {
                                return mLastFragment.dumpTagData();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case R.id.btn_write:
                            try {
                                Intent intent =
                                        new Intent(getApplicationContext(), DriveHelper.class);
//                                intent.putExtra("nfcTag", mLastFragment.getNfcTag());
                                startActivity(intent);
                                return "";
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case R.id.btn_hitory:
                            try {
                                if ( mLastFragment != null && mLastFragment instanceof NfcFeliCaTagFragment) {
                                    NfcFeliCaTagFragment nfcf = (NfcFeliCaTagFragment)mLastFragment;
                                    return nfcf.dumpFeliCaHistoryData();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
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
        //TextView tv_tag = (TextView) findViewById(R.id.result_tv);

        Button btnRead = (Button) findViewById(R.id.btn_read);
//        btnRead.setOnClickListener(this);
//
        Button btnHistory = (Button) findViewById(R.id.btn_hitory);
//        btnHistory.setOnClickListener(this);
//        btnHistory.setEnabled(false);
//
        Button btnWrite = (Button) findViewById(R.id.btn_write);
//        btnWrite.setOnClickListener(this);
//        btnWrite.setEnabled(false);
//
//        Button btnInout = (Button) findViewById(R.id.btn_inout);
//        btnInout.setOnClickListener(this);
//        btnInout.setEnabled(false);

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
                        throw new FeliCaException("Felica IDm を取得できませんでした");
                    }

                    btnHistory.setEnabled(!isFeliCatLite);
                    btnWrite.setEnabled(isFeliCatLite);

//                    btnRead.performClick();
                    btnHistory.performClick();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
                btnHistory.setEnabled(!isFeliCatLite);
                btnWrite.setEnabled(isFeliCatLite);
            } else {
                btnRead.performClick();
                btnHistory.setEnabled(false);
                btnWrite.setEnabled(true);
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }


}
