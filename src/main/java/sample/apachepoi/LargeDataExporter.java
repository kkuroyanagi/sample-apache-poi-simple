package sample.apachepoi;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * テンプレート(template.xlsx)を読み込み、Data シートにストリーミング形式で大量データを書き込む。
 * テンプレートに設定済みのヘッダーや入力規則(プルダウン)、Choices シートはそのまま維持される。
 */
public class LargeDataExporter {

    // メモリ上に保持する行数。これを超えた分は順次ディスクへフラッシュされる。
    private static final int ROW_ACCESS_WINDOW_SIZE = 100;
    private static final int DATA_ROW_COUNT = 1000000;

    public static void main(String[] args) throws IOException, InvalidFormatException {
        Path templatePath = Path.of(args.length > 0 ? args[0] : "template.xlsx");
        Path outputPath = Path.of(args.length > 1 ? args[1] : "output.xlsx");
        export(templatePath, outputPath, DATA_ROW_COUNT);
        System.out.println("出力しました: " + outputPath.toAbsolutePath());
    }

    static void export(Path templatePath, Path outputPath, int rowCount)
            throws IOException, InvalidFormatException {
        try (XSSFWorkbook template = new XSSFWorkbook(templatePath.toFile());
             SXSSFWorkbook workbook = new SXSSFWorkbook(template, ROW_ACCESS_WINDOW_SIZE)) {

            Sheet sheet = workbook.getSheet(Constants.DATA_SHEET_NAME);

            for (int r = 0; r < rowCount; r++) {
                int rowNum = r + 1; // 0行目はヘッダーなので1行目から
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue("選択肢" + ((r % Constants.CHOICE_COUNT) + 1));
                for (int col = 1; col < Constants.COLUMN_COUNT; col++) {
                    row.createCell(col).setCellValue(rowNum + "-" + col);
                }
            }

            try (FileOutputStream out = new FileOutputStream(outputPath.toFile())) {
                workbook.write(out);
            }
            workbook.dispose(); // ストリーミング処理で使った一時ファイルを削除
        }
    }
}
