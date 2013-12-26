package jp.caliconography.suicapasmoreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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

import org.apache.poi.ss.usermodel.Workbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.caliconography.suicapasmoreader.suica.HistoryBean;
import jp.caliconography.suicapasmoreader.excelutil.DetailsData;
import jp.caliconography.suicapasmoreader.excelutil.ExcelFileUtil;
import jp.caliconography.suicapasmoreader.excelutil.HeaderData;
import jp.caliconography.suicapasmoreader.excelutil.ReportData;
import jp.caliconography.suicapasmoreader.excelutil.SimpleReportCreator;

public class MainActivity extends ActionBarActivity  implements AbstractNfcTagFragment.INfcTagListener, LoaderManager.LoaderCallbacks {

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
                    save2Excel();

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

    private void save2Excel() {

        // create workbook
        Workbook wb = null;
        try {
            wb = ExcelFileUtil.getWorkbook(getAssets(), "template.xls");

            // create data
            List<ReportData> dataList = new ArrayList<ReportData>();
            dataList.add(setData(mHistories));

            SimpleReportCreator reportCreator = new SimpleReportCreator(wb, dataList);

            ExcelFileUtil.write(reportCreator.create(), "経費精算書.xls");
        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }

    }

    // setup test data
    private ReportData setData(ArrayList<HistoryBean> histories) {


        ReportData dataContainer = new ReportData();

        HeaderData header = new HeaderData();
        Map<String, String> dataMap = header.getDataMap();

        DetailsData details = new DetailsData();
        Map<String, Object[]> dataListMap = details.getDataListMap();

        // ヘッダ
        header.setReportName("経費精算書");
        dataMap.put("$APPLY_DATE", new SimpleDateFormat("yyyy'年'MM'月'dd'日'").format(new Date()));
        dataMap.put("$APPLYER_NAME", "");


        // 履歴の準備
        Collections.reverse(histories);
        dataListMap.put(getString(R.string.KEY_WORK_DATE), new String[histories.size()]);
        dataListMap.put(getString(R.string.KEY_WORK), new String[histories.size()]);
        dataListMap.put(getString(R.string.KEY_FROM_TO), new String[histories.size()]);
        dataListMap.put(getString(R.string.KEY_EXPENSE), new String[histories.size()]);
        dataListMap.put(getString(R.string.KEY_PRICE), new String[histories.size()]);
        dataListMap.put(getString(R.string.KEY_REMAIN), new String[histories.size()]);
        dataListMap.put(getString(R.string.KEY_IS_PRODUCT_SALES), new String[histories.size()]);

        for (int i = 0; i < histories.size(); i++) {
            HistoryBean historyBean = histories.get(i);
            ((String[])dataListMap.get(getString(R.string.KEY_WORK_DATE)))[i] = new SimpleDateFormat("yyyy/MM/dd").format(historyBean.getProcessDate());
            ((String[])dataListMap.get(getString(R.string.KEY_FROM_TO)))[i] = historyBean.isProductSales() ? historyBean.getProcessType(): (historyBean.getEntranceStation() + "→" + historyBean.getExitStation());
            ((String[])dataListMap.get(getString(R.string.KEY_REMAIN)))[i] = Long.toString(historyBean.getRemain());
            ((String[])dataListMap.get(getString(R.string.KEY_IS_PRODUCT_SALES)))[i] = historyBean.isProductSales() ? "1": "";
        }


        details.setNumOfDetails(dataListMap.get(getString(R.string.KEY_WORK_DATE)).length);

        dataContainer.setHeader(header);
        dataContainer.setDetails(details);
        return dataContainer;
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

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
