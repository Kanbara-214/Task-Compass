package com.kanbara.taskcompass.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kanbara.taskcompass.entity.AppUser;
import com.kanbara.taskcompass.entity.TaskItem;
import com.kanbara.taskcompass.entity.TaskStatus;
import com.kanbara.taskcompass.mapper.AppUserMapper;
import com.kanbara.taskcompass.mapper.TaskItemMapper;

@Component
@ConditionalOnProperty(name = "task-compass.demo-seed.enabled", havingValue = "true")
public class DemoDataSeeder implements CommandLineRunner {

	private static final String DEMO_EMAIL = "demo@example.com";
	private static final String DEMO_PASSWORD = "password123";

	private final AppUserMapper appUserMapper;
	private final TaskItemMapper taskItemMapper;
	private final PasswordEncoder passwordEncoder;

	public DemoDataSeeder(
			AppUserMapper appUserMapper,
			TaskItemMapper taskItemMapper,
			PasswordEncoder passwordEncoder) {
		this.appUserMapper = appUserMapper;
		this.taskItemMapper = taskItemMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

		AppUser demoUser = appUserMapper.findByEmail(DEMO_EMAIL);
		if (demoUser == null) {
			demoUser = createDemoUser(now);
		} else {
			taskItemMapper.deleteByOwnerId(demoUser.getId());
		}

		seedDemoTasks(demoUser.getId(), now);
	}

	private AppUser createDemoUser(LocalDateTime now) {
		AppUser demoUser = new AppUser();
		demoUser.setDisplayName("Demo User");
		demoUser.setEmail(DEMO_EMAIL);
		demoUser.setPasswordHash(passwordEncoder.encode(DEMO_PASSWORD));
		demoUser.setCreatedAt(now);
		appUserMapper.insert(demoUser);
		return demoUser;
	}

	private void seedDemoTasks(Long ownerId, LocalDateTime now) {
		LocalDateTime sameDeadline = now.plusDays(4);

		seedTask(ownerId, "応募書類を企業向けに最終調整する", "45分設定で、短時間の前倒しタスクと組み合わせやすい高重要度タスクです。",
				now.plusDays(1), 5, 5, 30, TaskStatus.TODO, "転職活動", now);
		seedTask(ownerId, "READMEの小さな表記ゆれを直す", "高重要度タスクの後に余った15分で完了できる前倒しタスクです。",
				now.plusDays(7), 1, 2, 15, TaskStatus.TODO, "ポートフォリオ", now);
		seedTask(ownerId, "期限切れ: 求人票メモを見直す", "期限切れですが、45分では高重要度タスクと両立しにくい低重要度タスクです。",
				now.minusDays(1), 1, 3, 30, TaskStatus.TODO, "転職活動", now);

		seedTask(ownerId, "期限切れ: 面談後のお礼メールを送る", "重要度が高い期限切れタスクです。短い作業時間でも解消候補になりやすいです。",
				now.minusHours(4), 5, 5, 30, TaskStatus.TODO, "面談", now);
		seedTask(ownerId, "期限切れ: ポートフォリオのスクリーンショットを差し替える", "期限切れですが作業時間がやや長く、他タスクとの組み合わせで選択が変わります。",
				now.minusDays(2), 2, 3, 45, TaskStatus.TODO, "ポートフォリオ", now);
		seedTask(ownerId, "30分後: 応募前チェックリストを確認する", "締切時刻ちょうどに完了できるケースを確認しやすいタスクです。",
				now.plusMinutes(30), 5, 4, 30, TaskStatus.TODO, "転職活動", now);
		seedTask(ownerId, "45分後: 面接URLと提出資料を確認する", "15分で完了できる締切間近のタスクです。",
				now.plusMinutes(45), 4, 4, 15, TaskStatus.TODO, "面談", now);
		seedTask(ownerId, "90分後: コードレビュー指摘へ返信する", "進行中タスクです。IN_PROGRESSでも追加加点せず、未完了タスクとして扱う確認に使えます。",
				now.plusMinutes(90), 5, 5, 60, TaskStatus.IN_PROGRESS, "ポートフォリオ", now);
		seedTask(ownerId, "2時間後: 企業研究メモを面接用に整理する", "作業時間を120分に広げたときに入りやすい中時間タスクです。",
				now.plusHours(2), 3, 4, 45, TaskStatus.TODO, "転職活動", now);

		seedTask(ownerId, "認可まわりの統合テストを書く", "長めですが重要度が高く、90分以上の作業時間で選択候補になります。",
				now.plusDays(2), 5, 4, 90, TaskStatus.TODO, "ポートフォリオ", now);
		seedTask(ownerId, "推薦ロジックの境界値テストを追加する", "120分設定で選ばれやすい高重要度タスクです。",
				now.plusDays(3), 5, 3, 120, TaskStatus.TODO, "ポートフォリオ", now);
		seedTask(ownerId, "デプロイDBの移行手順をREADMEに追記する", "45分で終わる中重要度タスクです。",
				now.plusDays(2), 4, 3, 45, TaskStatus.TODO, "ドキュメント", now);
		seedTask(ownerId, "AtCoder茶色対策としてDP問題を1問解く", "推薦ロジックと同じDP文脈で、学習カテゴリの見え方を確認できます。",
				now.plusDays(6), 3, 2, 60, TaskStatus.TODO, "学習", now);
		seedTask(ownerId, "Java Gold復習: 並行処理章を読み直す", "優先度は中程度ですが、まとまった時間があると選ばれる候補です。",
				now.plusDays(10), 2, 2, 90, TaskStatus.TODO, "学習", now);
		seedTask(ownerId, "ポートフォリオ全体のリファクタリング計画を作る", "180分の長時間タスクです。短い作業時間では推薦されないことを確認できます。",
				now.plusDays(14), 3, 2, 180, TaskStatus.TODO, "設計", now);
		seedTask(ownerId, "GitHubプロフィール文を更新する", "15分で終わるため、余り時間に入りやすい低負荷タスクです。",
				now.plusDays(5), 2, 2, 15, TaskStatus.TODO, "転職活動", now);
		seedTask(ownerId, "Issueコメントの実装意図を整理する", "30分で終わるドキュメント整理タスクです。",
				now.plusDays(3), 2, 2, 30, TaskStatus.TODO, "ドキュメント", now);
		seedTask(ownerId, "志望企業Aの技術ブログを読む", "同じ締切・重要度・作業時間のタスクです。ID順タイブレークの確認に使えます。",
				sameDeadline, 3, 2, 30, TaskStatus.TODO, "企業研究", now);
		seedTask(ownerId, "志望企業Bのプロダクトページを読む", "同じ締切・重要度・作業時間のタスクです。安定した並び順を確認できます。",
				sameDeadline, 3, 2, 30, TaskStatus.TODO, "企業研究", now);

		seedTask(ownerId, "ログイン導線の手動確認を完了する", "完了済みタスクです。推薦対象から除外されることを確認できます。",
				now.minusDays(1), 3, 3, 30, TaskStatus.DONE, "ポートフォリオ", now);
		seedTask(ownerId, "デモアカウントのREADME記載を確認する", "完了済みタスクです。進捗率や一覧表示の確認に使えます。",
				now.minusHours(2), 2, 2, 15, TaskStatus.DONE, "ドキュメント", now);
	}

	private void seedTask(
			Long ownerId,
			String title,
			String description,
			LocalDateTime dueDateTime,
			int importance,
			int urgency,
			int estimatedMinutes,
			TaskStatus status,
			String category,
			LocalDateTime now) {
		TaskItem task = new TaskItem();
		task.setOwnerId(ownerId);
		task.setTitle(title);
		task.setDescription(description);
		task.setDueDateTime(dueDateTime);
		task.setImportance(importance);
		task.setUrgency(urgency);
		task.setEstimatedMinutes(estimatedMinutes);
		task.setStatus(status);
		task.setCategory(category);
		task.setCreatedAt(now);
		task.setUpdatedAt(now);
		taskItemMapper.insert(task);
	}
}
