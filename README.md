# Task Compass

`Task Compass` は、転職活動や学習、ポートフォリオ制作のタスクを優先度付きで整理する Web アプリです。  
単なる ToDo の CRUD ではなく、`重要度` `緊急度` `締切` `予想作業時間` から `priorityScore` を計算し、「今やるべき順」でタスクを表示します。

日々のタスク管理で「何から手を付けるべきか」が分かりにくい、という課題を解決するために開発しました。

## できること

- ユーザー登録 / ログイン
- ログインユーザーごとのタスク管理
- タスクの作成 / 一覧 / 詳細 / 編集 / 削除
- ステータス、カテゴリ、並び替えでの絞り込み
- ダッシュボードでの `今日やるべきタスク` `期限切れタスク` `今週締切タスク` の可視化
- 優先度スコアと「なぜ上位なのか」の説明表示

## 優先度スコアの考え方

次の要素をもとにスコアを計算しています。

- 重要度が高いほど加点
- 緊急度が高いほど加点
- 締切が近いほど加点
- 期限切れは強く加点
- 作業時間が長いタスクは早めに着手を促すため加点
- 進行中タスクは少し優先
- 完了済みタスクはおすすめ対象外

## 技術構成

- Spring Boot
- Spring Security
- Thymeleaf
- MyBatis
- H2 / PostgreSQL

## ローカル起動

まずはそのまま起動できます。

1. Windows の `PowerShell` または `Windows Terminal` を開きます。
2. このプロジェクトのフォルダで、次のコマンドを実行します。

```powershell
.\mvnw.cmd spring-boot:run
```

このコマンドは、アプリを起動するためのものです。  
デフォルトではインメモリ H2 を使うため、DB の事前準備なしで起動できます。  
起動時に `schema.sql` からテーブルを自動作成し、デモアカウントも投入します。  
ログイン画面は [http://localhost:8080/login](http://localhost:8080/login) です。

## PostgreSQL を使う場合

ポートフォリオ確認だけなら、この設定は不要です。  
上の `.\mvnw.cmd spring-boot:run` だけで起動できます。

データを保持したい場合や PostgreSQL を使いたい場合だけ、`config/application.properties` を作成してください。

やることは、ひな型ファイルの [config/application-example.properties](./config/application-example.properties) をコピーして、
ファイル名を `application.properties` に変えるだけです。

Windows の `PowerShell` または `Windows Terminal` を使う場合は、次のコマンドでコピーできます。

```powershell
Copy-Item .\config\application-example.properties .\config\application.properties
```

このコマンドは、サンプル設定を自分用設定としてコピーするためのものです。

コピー後に、`config/application.properties` の中の PostgreSQL 接続情報を自分の環境に合わせて編集してください。

`config/application.properties` は `.gitignore` で除外しているため、ローカル設定やパスワードは公開されません。

## デモアカウント

- メールアドレス: `demo@example.com`
- パスワード: `password123`

初回起動時のみ、サンプルタスクを含むデモユーザーを自動投入します。

## 開発メモ
初期実装はAI支援で作成しています。

今後は要件整理、レビュー、修正、README整備を実施し、コード理解を深めながら改善していく予定です。
