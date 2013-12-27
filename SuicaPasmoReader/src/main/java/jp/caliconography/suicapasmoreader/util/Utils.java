package jp.caliconography.suicapasmoreader.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by abeharuhiko on 2013/12/26.
 */
public class Utils {

    public static void showToast(Activity activity, final Context context, final String toast) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
