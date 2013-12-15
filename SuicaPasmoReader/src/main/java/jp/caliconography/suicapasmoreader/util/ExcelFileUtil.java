package jp.caliconography.suicapasmoreader.util;

import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelFileUtil {

    private ExcelFileUtil() {
    }

    // Excelファイルの取得
    public static Workbook getWorkbook(AssetManager assetManager, String fileName) throws Exception {

        // Android用に修正
        InputStream inp = assetManager.open(fileName);

//        InputStream inp = new FileInputStream(fileName);
        Workbook wb = WorkbookFactory.create(inp);
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
        OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/spr/" + outFile);
//        OutputStream out = new FileOutputStream(outFile);
        wb.write(out);
        System.out.println("ファイル名「" + outFile + "」が出力されました");
    }

}