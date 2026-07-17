# sample-apache-poi-simple

Apache POI を使い、Excel テンプレートに大量データをストリーミング形式で出力するサンプルです。

## 概要

- 事前に**入力規則(プルダウン)付きのテンプレートファイル**(`template.xlsx`)を作成します
- プルダウンの選択肢は別シート(`Choices`)にあらかじめ用意しておきます(10件)
- テンプレートを読み込み、`Data` シートへ 50 列 × 大量行のデータを **SXSSF(ストリーミング)** で書き込みます
- テンプレートのヘッダー・入力規則・選択肢シートはそのまま維持されます

## 必要環境

- Java 21
- Maven 3.9 以降

## プロジェクト構成

```
pom.xml
src/main/java/sample/apachepoi/
  Constants.java          列数・選択肢数などの共通定数
  TemplateGenerator.java  テンプレート(template.xlsx)を作成する
  LargeDataExporter.java  テンプレートを読み込み、ストリーミングでデータを出力する
```

## 使い方

```bash
# コンパイル
mvn compile

# 1. テンプレート作成(template.xlsx を生成)
mvn exec:java -Dexec.mainClass=sample.apachepoi.TemplateGenerator

# 2. テンプレートを読み込み、output.xlsx へ大量データを出力
mvn exec:java -Dexec.mainClass=sample.apachepoi.LargeDataExporter
```

引数で出力先パスを指定することもできます。

```bash
mvn exec:java -Dexec.mainClass=sample.apachepoi.TemplateGenerator -Dexec.args="path/to/template.xlsx"
mvn exec:java -Dexec.mainClass=sample.apachepoi.LargeDataExporter -Dexec.args="path/to/template.xlsx path/to/output.xlsx"
```

## 実装のポイント

- `SXSSFWorkbook(XSSFWorkbook, rowAccessWindowSize)` コンストラクタで、既存テンプレートを読み込みつつ以降の行だけをストリーミングで書き出しています
- 入力規則はテンプレート作成時点で十分大きい行範囲(`A2:A100001`)へ設定しておくことで、実際の出力行数に関わらず有効になります
- 選択肢シート(`Choices`)は参照専用のため非表示に設定しています
- 生成される `template.xlsx` / `output.xlsx` は `.gitignore` で除外しています(`TemplateGenerator` / `LargeDataExporter` を実行すればいつでも再生成できます)
