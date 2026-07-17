package sample.apachepoi;

final class Constants {

    private Constants() {
    }

    static final String DATA_SHEET_NAME = "Data";
    static final String CHOICE_SHEET_NAME = "Choices";
    static final int COLUMN_COUNT = 50;
    static final int CHOICE_COUNT = 10;
    // プルダウンを効かせておく最終行(0始まり)。テンプレート作成時点ではデータ行数が未定のため、十分大きい値を確保しておく。
    static final int VALIDATION_LAST_ROW = 100_000;
}
