package sample.apachepoi;

import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * データ出力用テンプレート(template.xlsx)を作成する。
 * Data シートにヘッダーと入力規則(プルダウン)を、Choices シートに選択肢を用意する。
 */
public class TemplateGenerator {

    public static void main(String[] args) throws IOException {
        Path output = Path.of(args.length > 0 ? args[0] : "template.xlsx");
        generate(output);
        System.out.println("テンプレートを作成しました: " + output.toAbsolutePath());
    }

    static void generate(Path output) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            createChoiceSheet(workbook);
            createDataSheet(workbook);

            try (FileOutputStream out = new FileOutputStream(output.toFile())) {
                workbook.write(out);
            }
        }
    }

    private static void createChoiceSheet(XSSFWorkbook workbook) {
        Sheet sheet = workbook.createSheet(Constants.CHOICE_SHEET_NAME);
        for (int i = 0; i < Constants.CHOICE_COUNT; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue("選択肢" + (i + 1));
        }
        // 入力規則の参照専用シートなので非表示にする
        workbook.setSheetHidden(workbook.getSheetIndex(sheet), true);
    }

    private static void createDataSheet(XSSFWorkbook workbook) {
        Sheet sheet = workbook.createSheet(Constants.DATA_SHEET_NAME);

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("区分");
        for (int col = 1; col < Constants.COLUMN_COUNT; col++) {
            header.createCell(col).setCellValue("データ" + col);
        }
        sheet.createFreezePane(0, 1);

        // 1列目(区分)に Choices シートを参照するプルダウンを設定する
        DataValidationHelper helper = sheet.getDataValidationHelper();
        String formula = Constants.CHOICE_SHEET_NAME + "!$A$1:$A$" + Constants.CHOICE_COUNT;
        DataValidationConstraint constraint = helper.createFormulaListConstraint(formula);
        CellRangeAddressList addressList =
                new CellRangeAddressList(1, Constants.VALIDATION_LAST_ROW, 0, 0);
        DataValidation validation = helper.createValidation(constraint, addressList);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }
}
