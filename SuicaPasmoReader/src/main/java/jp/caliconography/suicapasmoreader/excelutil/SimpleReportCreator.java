package jp.caliconography.suicapasmoreader.excelutil;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * original source by http://codezine.jp/
 */
public class SimpleReportCreator {

    private Workbook wb;

    private List<ReportData> reportList = new ArrayList<ReportData>();

    private int numOfDetails = 0;

    public SimpleReportCreator(Workbook wb, List<ReportData> dataList) {
        this.wb = wb;
        this.reportList = dataList;
    }

    // レポート用のワークブックを作成
    public Workbook create() {

        // 印刷範囲を取得
        String printArea = wb.getPrintArea(wb.getSheetIndex("TEMPLATE"));
        if (printArea != null) {
            int sheetPosition = printArea.indexOf("!");
            if (sheetPosition != -1) {
                printArea = printArea.substring(sheetPosition + 1);
            } else {
                printArea = null;
            }
        }

        if (reportList.size() > 1) {
            for (int reportIndex = 0; reportIndex < reportList.size(); reportIndex++) {
                // テンプレートシートをデータ数分シートコピーする
                Sheet cloneSheet = wb.cloneSheet(wb.getSheetIndex("TEMPLATE"));
                // ワークシート名を設定
                wb.setSheetName(wb.getSheetIndex(cloneSheet), reportList.get(
                        reportIndex).getHeader().getReportName());
                // 印刷範囲を設定
                if (printArea != null) {
                    wb.setPrintArea(wb.getSheetIndex(cloneSheet), printArea);
                }
            }
            // テンプレートシートの削除
            wb.removeSheetAt(wb.getSheetIndex("TEMPLATE"));
        }

        ReportData reportData = null;
        Map<String, String> headerMap = null;
        Map<String, Object[]> detailsMap = null;

        // ワークシート単位の繰返し処理
        int numOfSheet = wb.getNumberOfSheets();
        for (int sheetIndex = 0; sheetIndex < numOfSheet; sheetIndex++) {
            Sheet sheet = wb.getSheetAt(sheetIndex);

            // ワークシートに対応するデータを取得
            reportData = reportList.get(sheetIndex);
            headerMap = reportData.getHeader().getDataMap();
            detailsMap = reportData.getDetails().getDataListMap();
            if (reportData.getDetails().getNumOfDetails() != 0) {
                numOfDetails = reportData.getDetails().getNumOfDetails();
            }

            // 行単位の繰返し処理
            int lastRow = sheet.getLastRowNum();
            for (int rowIndex = 0; rowIndex <= lastRow; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                // セル単位の繰返し処理
                int lastColumn = row.getLastCellNum();
                for (int columnIndex = 0; columnIndex < lastColumn; columnIndex++) {
                    Log.d("SuicaPasmoReader", rowIndex + ":" + columnIndex);
                    Cell cell = row.getCell(columnIndex);
                    if (cell == null) {
                        continue;
                    }

                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        // 値を置換える為に準備したセルであればデータを設定
                        String key = cell.getStringCellValue();
                        if (headerMap.containsKey(key)) {
                            setCellValue(cell, headerMap.get(key));
                        } else if (detailsMap.containsKey(key)) {
                            setCellValues(cell, sheet, detailsMap.get(key));
                        }
                    }
                }
            }
        }
        // 完成したワークブックオブジェクトを戻す
        return wb;
    }

    // セルに値を設定
    private void setCellValue(Cell cell, Object value) {

        CellStyle style = cell.getCellStyle();

        if (value != null) {
            if (value instanceof String) {
                cell.setCellValue((String) value);
            } else if (value instanceof Number) {
                Number numValue = (Number) value;
                if (numValue instanceof Float) {
                    Float floatValue = (Float) numValue;
                    numValue = new Double(String.valueOf(floatValue));
                }
                cell.setCellValue(numValue.doubleValue());
            } else if (value instanceof Date) {
                Date dateValue = (Date) value;
                cell.setCellValue(dateValue);
            } else if (value instanceof Boolean) {
                Boolean boolValue = (Boolean) value;
                cell.setCellValue(boolValue);
            }
        } else {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
            cell.setCellStyle(style);
        }

    }

    private void setCellValues(Cell baseCell, Sheet sheet, Object[] values) {

        // 繰返し処理開始セルの位置情報を保持
        int startRowPosition = baseCell.getRowIndex();
        int columnPosition = baseCell.getColumnIndex();

        // 明細の数分繰返し処理をする
        for (int i = 0; i < numOfDetails; i++) {

            // 行を取得または生成
            Row row = sheet.getRow(startRowPosition + i);
            if (row == null) {
                row = sheet.createRow(startRowPosition + i);
                // 繰返し処理開始行と同じ高さを設定
                row.setHeight(sheet.getRow(startRowPosition).getHeight());
            }
            // セルを取得または生成
            Cell cell = row.getCell(columnPosition);
            if (cell == null) {
                cell = row.createCell(columnPosition);
                // 繰返し処理開始セルの情報をコピー
                copyCell(baseCell, cell);
            }
            // セルに値を設定
            setCellValue(cell, values[i]);
        }

    }

    // セルの値を別セルにコピーする
    public static void copyCell(Cell fromCell, Cell toCell) {

        if (fromCell != null) {

            int cellType = fromCell.getCellType();
            switch (cellType) {
                case Cell.CELL_TYPE_BLANK:
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    toCell.setCellFormula(fromCell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    toCell.setCellValue(fromCell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    toCell.setCellErrorValue(fromCell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    toCell.setCellValue(fromCell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    toCell.setCellValue(fromCell.getRichStringCellValue());
                    break;
                default:
            }

            if (fromCell.getCellStyle() != null) {
                toCell.setCellStyle(fromCell.getCellStyle());
            }

            if (fromCell.getCellComment() != null) {
                toCell.setCellComment(fromCell.getCellComment());
            }
        }
    }
}
