package jp.caliconography.suicapasmoreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import net.kazzz.felica.suica.Suica;

import org.apache.poi.ss.usermodel.Workbook;

import jp.caliconography.suicapasmoreader.suica.HistoryBean;
import jp.caliconography.suicapasmoreader.util.DetailsData;
import jp.caliconography.suicapasmoreader.util.ExcelFileUtil;
import jp.caliconography.suicapasmoreader.util.HeaderData;
import jp.caliconography.suicapasmoreader.util.ReportData;
import jp.caliconography.suicapasmoreader.util.SimpleReportCreator;

/**
 * Created by abe on 2013/12/08.
 */
public class DriveHelper extends Activity {
    private String TAG = this.getClass().getSimpleName();

    static final int REQUEST_ACCOUNT_PICKER = 1;
    static final int REQUEST_AUTHORIZATION = 2;

    static final String FILE_TITLE = "google_drive_test";

//    private Drive service = null;
//    private SpreadsheetService s;
//    private GoogleAccountCredential credential = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (service == null) {
//            credential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(DriveScopes.DRIVE));
//            startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
//        }

        ((Button)findViewById(R.id.saveButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("save");
                saveTextToDrive();
            }
        });
        ((Button)findViewById(R.id.loadButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("load");
//                loadTextFromDrive();
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
//                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
//                        credential.setSelectedAccountName(accountName);
//                        service = getDriveService(credential);
                    }

                    // http://www.blogface.org/2013/07/reading-google-spreadsheets-from.html
                    Log.d(TAG, accountName);

/*
                    (new AsyncTask<String, String,String>(){
                        @Override
                        protected String doInBackground(String... arg0) {
                            try {
                                // Turn account name into a token, which must
                                // be done in a background task, as it contacts
                                // the network.
                                String token = GoogleAuthUtil.getToken(getApplicationContext(),
                                                accountName,
                                                "oauth2:https://spreadsheets.google.com/feeds https://docs.google.com/feeds");
                                Log.d(TAG, "Token: " + token);

                                // Now that we have the token, can we actually list
                                // the spreadsheets or anything...
                                s = new SpreadsheetService("SuicaPasumoReader");
                                s.setAuthSubToken(token);

                            } catch (UserRecoverableAuthException e) {
                                // This is NECESSARY so the user can say, "yeah I want
                                // this app to have permission to read my spreadsheet."
                                Intent recoveryIntent = e.getIntent();
                                startActivityForResult(recoveryIntent, 2);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (GoogleAuthException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            return null;
                        }}).execute();
*/





                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK) {
                    saveTextToDrive();
                } else {
//                    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
                }
                break;
        }
    }

    private void saveTextToDrive() {

        // create workbook
        Workbook wb = null;
        try {
            wb = ExcelFileUtil.getWorkbook(getAssets(), "template.xls");

            // create data
            List<ReportData> dataList = new ArrayList<ReportData>();
          dataList.add(setData((ArrayList<HistoryBean>)this.getIntent().getSerializableExtra("histories")));
//          dataList.add(setData(2));

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
//        HashMap<String, String[]> historyMap = new HashMap();
        dataListMap.put(getString(R.string.KEY_WORK_DATE), new String[histories.size()]);
        dataListMap.put(getString(R.string.KEY_WORK), new String[histories.size()]);
        dataListMap.put(getString(R.string.KEY_FROM_TO), new String[histories.size()]);
        dataListMap.put(getString(R.string.KEY_EXPENSE), new String[histories.size()]);
        dataListMap.put(getString(R.string.KEY_PRICE), new String[histories.size()]);
        dataListMap.put(getString(R.string.KEY_REMAIN), new String[histories.size()]);

        for (int i = 0; i < histories.size(); i++) {
            ((String[])dataListMap.get(getString(R.string.KEY_WORK_DATE)))[i] = new SimpleDateFormat("yyyy/MM/dd").format(histories.get(i).getProcessDate());
            ((String[])dataListMap.get(getString(R.string.KEY_FROM_TO)))[i] = histories.get(i).getEntranceStation() + "→" + histories.get(i).getExitStation();
            ((String[])dataListMap.get(getString(R.string.KEY_REMAIN)))[i] = Long.toString(histories.get(i).getRemain());
        }


//        dataListMap.put("$WORK_DATE[]", new String[] { "2013/12/20", "2013/12/18", "2013/12/19" });
//        dataListMap.put("$WORK[]", new String[] {"日清医", "JHF", "ほげ" });
//        dataListMap.put("$FROM_TO[]", new String[] { "京成立石→秋葉原", "東京→有楽町", "東京→東東京" });
//        dataListMap.put("$EXPENSE[]", new String[] { "12000", "14000", "16000" });
//        dataListMap.put("$PRICE[]", new String[] { "12000", "14000", "16000" });
//        dataListMap.put("$REMAIN[]", new String[] { "12000", "14000", "16000" });

        details.setNumOfDetails(dataListMap.get(getString(R.string.KEY_WORK_DATE)).length);

        dataContainer.setHeader(header);
        dataContainer.setDetails(details);
        return dataContainer;
    }

/*
    private void saveTextToDrive() {
        final String inputText = ((EditText)findViewById(R.id.editText)).getText().toString();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    // http://stackoverflow.com/questions/13229294/how-do-i-create-a-google-spreadsheet-with-a-service-account-and-share-to-other-g

                    // Define the URL to request.  This should never change.
                    // (Magic URL good for all users.)
                    URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");

                    // Make a request to the API and get all spreadsheets.
                    SpreadsheetFeed feed;
                    try {
                        feed = s.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
                        List<SpreadsheetEntry> spreadsheets = feed.getEntries();

                        // Iterate through all of the spreadsheets returned
                        SpreadsheetEntry template = null;
                        for (SpreadsheetEntry spreadsheet : spreadsheets) {
                            // Print the title of this spreadsheet to the screen
                            Log.d(TAG, spreadsheet.getTitle().getPlainText());
                            if ("経費精算書".equals(spreadsheet.getTitle().getPlainText())) {
                                template = spreadsheet;
                                break;
                            }
                        }
                        Log.d(TAG, "あった！" + template.getTitle().getPlainText());
                        Log.d(TAG, template.getDefaultWorksheet().getXmlBlob().getBlob());
//                        Log.d(TAG, template.getXmlBlob().getBlob());

                        com.google.api.services.drive.model.File  file = new com.google.api.services.drive.model.File();
                        file.setTitle(new Date().toString());
                        file.setMimeType("application/vnd.google-apps.spreadsheet");
                        Insert insert = service.files().insert(file);

                        file = insert.execute();

                        String spreadsheetURL = "https://spreadsheets.google.com/feeds/spreadsheets/" + file.getId();
                        SpreadsheetEntry spreadsheet = s.getEntry(new URL(spreadsheetURL), SpreadsheetEntry.class);

                        WorksheetFeed worksheetFeed = s.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
                        List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
                        WorksheetEntry worksheet = worksheets.get(0);

                        URL cellFeedUrl= worksheet.getCellFeedUrl ();
                        CellFeed cellFeed= s.getFeed (cellFeedUrl, CellFeed.class);

                        CellEntry cellEntry= new CellEntry (1, 1, "aa");
                        cellFeed.insert (cellEntry);


                    } catch (ServiceException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }






//                    // 指定のタイトルのファイルの ID を取得
//                    String fileIdOrNull = null;
//                    FileList list = service.files().list().execute();
//                    for (File f : list.getItems()) {
//                        if (FILE_TITLE.equals(f.getTitle())) {
//                            fileIdOrNull = f.getId();
//                        }
//                    }
//
//                    File body = new File();
//                    body.setTitle(FILE_TITLE);//fileContent.getName());
//                    body.setMimeType("text/plain");
//
//                    ByteArrayContent content = new ByteArrayContent("text/plain", inputText.getBytes(Charset.forName("UTF-8")));
//                    if (fileIdOrNull == null) {
//                        service.files().insert(body, content).execute();
//                        showToast("insert!");
//                    } else {
//                        service.files().update(fileIdOrNull, body, content).execute();
//                        showToast("update!");
//                    }
                    // TODO 失敗時の処理?
                } catch (UserRecoverableAuthIOException e) {
                    startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                } catch (IOException e) {
                    showToast("error occur...");
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
*/

/*
    private void loadTextFromDrive() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 指定のタイトルのファイルの ID を取得
                    String fileIdOrNull = null;
                    FileList list = service.files().list().execute();
                    for (File f : list.getItems()) {
                        if (FILE_TITLE.equals(f.getTitle())) {
                            fileIdOrNull = f.getId();
                        }
                    }

                    InputStream is = null;
                    if (fileIdOrNull != null) {
                        File f = service.files().get(fileIdOrNull).execute();
                        is = downloadFile(service, f);
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    try {
                        StringBuffer sb = new StringBuffer();
                        sb.append(br.readLine());

                        final String text = sb.toString();
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                ((EditText)findViewById(R.id.editText)).setText(text);
                            }
                        });
                    } finally {
                        if (br != null) br.close();
                    }
                    // TODO 失敗時の処理?
                } catch (UserRecoverableAuthIOException e) {
                    startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                } catch (IOException e) {
                    showToast("error occur...");
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
*/

/*
    // https://developers.google.com/drive/v2/reference/files/get より
    private static InputStream downloadFile(Drive service, File file) {
        if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
            try {
                HttpResponse resp =
                        service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()))
                                .execute();
                return resp.getContent();
            } catch (IOException e) {
                // An error occurred.
                e.printStackTrace();
                return null;
            }
        } else {
            // The file doesn't have any content stored on DriveHelper.
            return null;
        }
    }
*/

/*
    private Drive getDriveService(GoogleAccountCredential credential) {
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                .build();
    }
*/

    public void showToast(final String toast) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}