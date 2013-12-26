package jp.caliconography.suicapasmoreader.excelutil;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * original source by http://codezine.jp/
 */
public class ExcelFileUtil {

    private ExcelFileUtil() {
    }

    // Excelファイルの取得
    public static Workbook getWorkbook(AssetManager assetManager, String fileName) throws Exception {

        // Android用に修正
        InputStream inp = assetManager.open(fileName);

        Workbook wb = new HSSFWorkbook(inp);
        System.out.println("ファイル名「" + fileName + "」を読込ました");
        return wb;
    }

    // Excelファイルか確認
    public static boolean isExcelFile(String inputFile) {
        if (inputFile != null) {
            if (inputFile.endsWith("xls") || inputFile.endsWith("xlsx")) {
                return true;
            }
        }
        return false;
    }

    // 存在するExcelファイルか確認
    public static boolean isExistExcelFile(String inputFile) {
        if (isExcelFile(inputFile)) {
            File file = new File(inputFile);
            if (file.exists() && file.isFile()) {
                return true;
            }
        }
        return false;
    }

    // Excelファイルの出力
    public static void write(Workbook wb, String outFile) throws Exception {
        // Android用に修正
//        OutputStream out = new FileOutputStream(Environment.c() + "/spr/" + outFile);
        File outDir = new File(Environment.getExternalStorageDirectory(), "spr");
//        File outDir = new File("/sdcard", "spr");
        if (!outDir.exists()) {
            if(outDir.mkdir()) Log.d("ExcelFIleUtil", outDir.getAbsolutePath());
        }
        OutputStream out = new FileOutputStream(outDir.getAbsolutePath() + "/" + outFile);
//        OutputStream out = new FileOutputStream(outFile);
        wb.write(out);
        System.out.println("ファイル名「" + outFile + "」が出力されました");
    }

}