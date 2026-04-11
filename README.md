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
- PostgreSQL

## ローカル起動

1. PostgreSQL にデータベースを作成します

```sql
create database task_compass;
```

2. 必要に応じて接続情報を環境変数で設定します

```powershell
$env:TASK_COMPASS_DB_URL="jdbc:postgresql://localhost:5432/task_compass"
$env:TASK_COMPASS_DB_USERNAME="postgres"
$env:TASK_COMPASS_DB_PASSWORD="postgres"
```

3. アプリを起動します

```powershell
.\mvnw.cmd spring-boot:run
```

起動時に `schema.sql` からテーブルを自動作成します。  
ログイン画面は [http://localhost:8080/login](http://localhost:8080/login) です。

## デモアカウント

- メールアドレス: `demo@example.com`
- パスワード: `password123`

初回起動時のみ、サンプルタスクを含むデモユーザーを自動投入します。

## 開発メモ
初期実装はAI支援で作成しています。

今後は要件整理、レビュー、修正、README整備を実施し、コード理解を深めながら改善していく予定です。