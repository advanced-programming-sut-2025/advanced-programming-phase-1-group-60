package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.models.Game;
import com.StardewValley.models.User;
import com.StardewValley.models.Skill; // برای دسترسی به Skill.getLevel()
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont; // اضافه شده
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.List;

/**
 * نمایشگر جدول امتیازات زنده بازی.
 * بازیکنان را بر اساس معیارهای مختلف (پول، ماموریت‌های تکمیل شده، میانگین مهارت‌ها) رتبه‌بندی می‌کند.
 * رتبه‌بندی به صورت بی‌درنگ به‌روزرسانی می‌شود.
 */
public class ScoreboardView implements Screen {
    public final GameView gameView; // ارجاع به GameView برای تغییر صفحه
    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;
    private BitmapFont font; // حالا اینجا ایجاد می‌شود
    private Texture backgroundTexture;
    private Drawable backgroundDrawable = null;
    private Table rootTable;
    private Table controlsTable; // شامل دکمه‌های مرتب‌سازی
    private ScrollPane playersScrollPane;
    private Table playersTable; // شامل ردیف‌های داده بازیکنان

    private RankingCriterion currentCriterion;
    private float updateTimer;
    private final float UPDATE_INTERVAL = 1.0f; // هر 1 ثانیه به‌روزرسانی می‌شود

    // Enum برای معیارهای رتبه‌بندی
    private enum RankingCriterion {
        MONEY,
        COMPLETED_TASKS,
        AVERAGE_SKILLS
    }

    public ScoreboardView(GameView gameView) {
        this.gameView = gameView;
        this.skin = MenuManager.getInstance().getPixthulhuSkin(); // استفاده از Skin موجود
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport(), batch);
        // فونت در متد show() ایجاد می‌شود
        // this.font = skin.getFont("default"); // این خط حذف شد

        currentCriterion = RankingCriterion.MONEY; // معیار مرتب‌سازی پیش‌فرض

        createUI();
    }

    /**
     * عناصر UI جدول امتیازات را ایجاد می‌کند.
     */
    private void createUI() {
        try {
            backgroundTexture = new Texture(Gdx.files.internal("assets/Background/layers/background.png"));
            backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        } catch (Exception e) {
            Gdx.app.error("CraftingMenuScreenView", "Failed to load main background texture: " + e.getMessage());
            // Fallback to a solid color if background.png fails
            Gdx.app.log("CraftingMenuScreenView", "Using Pixmap-based ColorDrawable as fallback for main background.");
        }
        rootTable = new Table(skin);
        rootTable.setFillParent(true);
        rootTable.pad(10);
        rootTable.setBackground(backgroundDrawable); // فرض بر وجود یک پس‌زمینه پنجره در Skin

        // جدول کنترل‌ها (دکمه‌های مرتب‌سازی)
        controlsTable = new Table(skin);
        TextButton moneyButton = new TextButton("Money", skin);
        TextButton tasksButton = new TextButton("Completed Tasks", skin);
        TextButton skillsButton = new TextButton("Avg. Skills", skin);
        TextButton backButton = new TextButton("Back", skin);

        // افزودن شنونده‌ها به دکمه‌ها
        moneyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentCriterion = RankingCriterion.MONEY;
                updateScoreboard();
            }
        });
        tasksButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentCriterion = RankingCriterion.COMPLETED_TASKS;
                updateScoreboard();
            }
        });
        skillsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentCriterion = RankingCriterion.AVERAGE_SKILLS;
                updateScoreboard();
            }
        });
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameView.showMapView(); // بازگشت به نقشه
            }
        });

        controlsTable.add(new Label("Sort By:", skin)).padRight(10); // این لیبل هم باید از فونت جدید استفاده کند
        controlsTable.add(moneyButton).pad(5);
        controlsTable.add(tasksButton).pad(5);
        controlsTable.add(skillsButton).pad(5).row();
        controlsTable.add(backButton).colspan(4).padTop(20).width(150).height(50);

        rootTable.add(controlsTable).expandX().fillX().padBottom(20).row();

        // جدول بازیکنان (داخل ScrollPane)
        playersTable = new Table(skin);
        playersTable.align(Align.top); // محتوا را به بالا تراز می‌کند
        playersScrollPane = new ScrollPane(playersTable, skin);
        playersScrollPane.setFadeScrollBars(false);
        playersScrollPane.setScrollingDisabled(true, false); // فقط اسکرول عمودی

        rootTable.add(playersScrollPane).expand().fill().row();

        stage.addActor(rootTable);
    }

    /**
     * جدول امتیازات را بر اساس معیار فعلی به‌روزرسانی می‌کند.
     */
    private void updateScoreboard() {
        playersTable.clearChildren(); // ردیف‌های موجود را پاک می‌کند

        // افزودن ردیف سربرگ
        // از فونت جدید برای لیبل‌ها استفاده می‌کنیم
        playersTable.add(new Label("Rank", new Label.LabelStyle(font, Color.YELLOW))).pad(5).width(50);
        playersTable.add(new Label("Player Name", new Label.LabelStyle(font, Color.YELLOW))).pad(5).width(200);
        playersTable.add(new Label("Score", new Label.LabelStyle(font, Color.YELLOW))).pad(5).width(150).row();
        playersTable.add().colspan(3).height(2).padBottom(5).row(); // جداکننده

        List<User> users = Game.getInstance().getPlayers(); // دریافت تمام کاربران در بازی فعلی

        // مرتب‌سازی کاربران بر اساس معیار فعلی
        users.sort((u1, u2) -> {
            float score1 = getScore(u1, currentCriterion);
            float score2 = getScore(u2, currentCriterion);
            return Float.compare(score2, score1); // مرتب‌سازی نزولی
        });

        int rank = 1;
        for (User user : users) {
            // از فونت جدید برای لیبل‌های داده استفاده می‌کنیم
            playersTable.add(new Label(String.valueOf(rank), new Label.LabelStyle(font, Color.WHITE))).pad(5);
            playersTable.add(new Label(user.getUsername(), new Label.LabelStyle(font, Color.WHITE))).pad(5);
            playersTable.add(new Label(formatScore(getScore(user, currentCriterion)), new Label.LabelStyle(font, Color.WHITE))).pad(5).row();
            rank++;
        }
    }

    /**
     * امتیاز یک کاربر را بر اساس معیار داده شده محاسبه می‌کند.
     * @param user کاربر مورد نظر.
     * @param criterion معیار رتبه‌بندی.
     * @return امتیاز کاربر.
     */
    private float getScore(User user, RankingCriterion criterion) {
        switch (criterion) {
            case MONEY:
                return user.getMoney();
            case COMPLETED_TASKS:
                return user.getCompletedQuestsCount();
            case AVERAGE_SKILLS:
                return user.getAverageSkillLevel();
            default:
                return 0;
        }
    }

    /**
     * امتیاز را به یک رشته قابل نمایش تبدیل می‌کند.
     * @param score امتیاز عددی.
     * @return امتیاز فرمت شده به صورت رشته.
     */
    private String formatScore(float score) {
        if (currentCriterion == RankingCriterion.MONEY) {
            return "$" + (int) score;
        }
        return String.valueOf((int) score);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        // ایجاد فونت در اینجا
        font = new BitmapFont();
        font.getData().setScale(1.5f); // اندازه فونت را تنظیم کنید
        font.setColor(Color.WHITE); // رنگ فونت را تنظیم کنید

        // به‌روزرسانی style لیبل "Sort By:" پس از ایجاد فونت
        ((Label)controlsTable.getChildren().get(0)).setStyle(new Label.LabelStyle(font, Color.YELLOW));

        updateScoreboard(); // به‌روزرسانی اولیه
        updateTimer = 0; // بازنشانی تایمر
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateTimer += delta;
        if (updateTimer >= UPDATE_INTERVAL) {
            updateScoreboard(); // به‌روزرسانی زنده
            updateTimer = 0;
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        if (font != null) { // اطمینان از آزاد شدن فونت
            font.dispose();
        }
        // Skin فرض بر این است که توسط MenuManager مدیریت می‌شود یا مشترک است، بنابراین اینجا آزاد نمی‌شود.
    }
}
