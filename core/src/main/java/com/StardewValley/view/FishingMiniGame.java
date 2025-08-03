package com.StardewValley.view;

import com.StardewValley.AssetsManager.MapManager;
import com.StardewValley.models.*;
import com.StardewValley.repository.FishingRepository;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;
import java.util.Random;

/**
 * صفحه بازی کوچک ماهیگیری، بر اساس دستورالعمل‌های جدید بازطراحی شده است.
 * بازیکن یک میله سبز را کنترل می‌کند تا آن را با ماهی در حال حرکت هماهنگ نگه دارد و پیشرفت صید را افزایش دهد.
 */
public class FishingMiniGame extends InputAdapter implements Screen {

    // Enum برای مدیریت وضعیت بازی
    private enum GameState {
        CASTING,
        PLAYING,
        END_SCREEN
    }

    // Enum برای انواع حرکت ماهی
    private enum FishMovementType {
        MIXED,
        SMOOTH,
        SINKER,
        FLOATER,
        DART
    }

    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;

    private final GameView gameView;
    private final User user;
    private final String currentSeason;
    private final int fishingSkill;
    private final boolean hasSonarBobber; // پرچم برای ابزار Sonar Bobber

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private Rectangle playerBar;
    private Rectangle fishBar;
    private Rectangle progressBar;
    private Rectangle barPoleBounds; // محدوده میله برای محاسبات
    private Rectangle barWaterBounds; // محدوده دقیق آب در داخل میله ماهیگیری

    private float playerBarSpeed = 200f;
    private float fishBarSpeed = 100f;
    private float fishBarDirection = 1;
    private float fishingProgress = 0;
    private float catchDuration = 10f;
    private float timer = 0;
    private float fishMovementTimer = 0.5f; // تایمر برای به‌روزرسانی حرکت ماهی (هر نیم ثانیه)

    private Fish caughtFish;
    private FishMovementType fishMovementType;
    private boolean isPerfectCatch = true; // پرچم برای ردیابی صید Perfect

    // Texture ها برای تصاویر
    private Texture fishingSystemTexture;
    private Texture greenBarTexture;
    private Texture normalFishTexture;
    private Texture legendFishTexture;

    private GameState gameState;
    private String endMessage = "";
    private String fishName = "???";

    public FishingMiniGame(GameView gameView, User user, String currentSeason, int fishingSkill, boolean hasSonarBobber) {
        this.gameView = gameView;
        this.user = user;
        this.currentSeason = currentSeason;
        this.fishingSkill = fishingSkill;
        this.hasSonarBobber = hasSonarBobber;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);

        Gdx.input.setInputProcessor(this);

        // بارگذاری Texture ها
        try {
            fishingSystemTexture = new Texture(Gdx.files.internal("MiniGame/FishingSystem.png"));
            greenBarTexture = new Texture(Gdx.files.internal("MiniGame/Green_Bar.png"));
            normalFishTexture = new Texture(Gdx.files.internal("MiniGame/Normal_Fish.png"));
            legendFishTexture = new Texture(Gdx.files.internal("MiniGame/Legend_Fish.png"));
        } catch (Exception e) {
            Gdx.app.error("FishingMiniGame", "Error loading textures: " + e.getMessage());
        }

        // تعریف ابعاد و موقعیت‌ها بر اساس اندازه Texture ها
        float fishingSystemWidth = fishingSystemTexture.getWidth();
        float fishingSystemHeight = fishingSystemTexture.getHeight();

        float fishingSystemX = WORLD_WIDTH / 2 - fishingSystemWidth / 2;
        float fishingSystemY = WORLD_HEIGHT / 2 - fishingSystemHeight / 2;

        barPoleBounds = new Rectangle(fishingSystemX, fishingSystemY, fishingSystemWidth, fishingSystemHeight);

        // تعریف محدوده آب (ناحیه داخلی) بر اساس تصویر
        float waterPaddingX = barPoleBounds.width * 0.15f; // پدینگ افقی
        float waterPaddingY = barPoleBounds.height * 0.12f; // پدینگ عمودی
        barWaterBounds = new Rectangle(
            barPoleBounds.x + waterPaddingX,
            barPoleBounds.y + waterPaddingY,
            barPoleBounds.width - 2 * waterPaddingX,
            barPoleBounds.height - 2 * waterPaddingY
        );

        // ابعاد میله سبز را متناسب با محدوده آب تنظیم می‌کنیم
        playerBar = new Rectangle(
            barWaterBounds.x + barPoleBounds.width * 0.15f,
            barWaterBounds.y + barWaterBounds.height / 2 - (barWaterBounds.height * 0.2f) / 2,
            barWaterBounds.width * 0.8f,
            barWaterBounds.height * 0.2f
        );

        // تعیین نوع حرکت ماهی و انتخاب ماهی
        List<Fish> fishList = FishingRepository.getAvailableFish(FishingRepository.getFishes(),currentSeason, fishingSkill);
        if (!fishList.isEmpty()) {
            caughtFish = fishList.get(MathUtils.random(fishList.size() - 1));
            Random random = new Random();
            switch (random.nextInt(2)){
                case 0:
                    caughtFish.setQuality("Silver");
                    break;
                case 1:
                    caughtFish.setQuality("Golden");
                    break;
                case 2:
                    caughtFish.setQuality("Iridium");
                    break;
            }
            fishMovementType = getFishMovementType(caughtFish);
            if (hasSonarBobber || caughtFish.isLegendary()) {
                fishName = caughtFish.getName();
            }
        } else {
            caughtFish = null;
            fishMovementType = FishMovementType.MIXED;
        }

        // ابعاد و موقعیت ماهی را متناسب با محدوده آب تنظیم می‌کنیم
        fishBar = new Rectangle(
            barWaterBounds.x + barPoleBounds.width * 0.15f,
            barWaterBounds.y + barWaterBounds.height / 2 - (barWaterBounds.height * 0.2f) / 2,
            barWaterBounds.width * 0.5f,
            barWaterBounds.height * 0.1f
        );

        progressBar = new Rectangle(barPoleBounds.x - 30, barPoleBounds.y, 20, 0);

        playerBarSpeed += fishingSkill * 10;
        fishBarSpeed = MathUtils.clamp(200 - (fishingSkill * 5), 50, 200); // افزایش سرعت پایه ماهی

        gameState = GameState.CASTING;
        timer = 0;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.4f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // رندر کردن تمام تصاویر در یک بلوک batch
        batch.begin();

        // کشیدن پس‌زمینه میله ماهیگیری
        if (fishingSystemTexture != null) {
            batch.draw(fishingSystemTexture, barPoleBounds.x, barPoleBounds.y, barPoleBounds.width, barPoleBounds.height);
        }

        // کشیدن میله بازیکن
        if (greenBarTexture != null) {
            batch.draw(greenBarTexture, playerBar.x, playerBar.y, playerBar.width, playerBar.height);
        }

        // کشیدن ماهی
        if (caughtFish != null) {
            if (caughtFish.isLegendary() && legendFishTexture != null) {
                batch.draw(legendFishTexture, fishBar.x, fishBar.y, fishBar.width, fishBar.height);
            } else if (normalFishTexture != null) {
                batch.draw(normalFishTexture, fishBar.x, fishBar.y, fishBar.width, fishBar.height);
            }
        }

        // رندر کردن متن بر اساس حالت بازی
        if (gameState == GameState.CASTING) {
            font.draw(batch, "Casting...", 0, WORLD_HEIGHT / 2, WORLD_WIDTH, Align.center, false);
        } else if (gameState == GameState.PLAYING) {
            if (hasSonarBobber && caughtFish != null) {
                font.draw(batch, caughtFish.getName(), barPoleBounds.x, barPoleBounds.y - 10);
            }
        } else if (gameState == GameState.END_SCREEN) {
            font.draw(batch, endMessage, 0, WORLD_HEIGHT / 5, WORLD_WIDTH, Align.center, false);
        }

        batch.end();

        // رندر کردن میله پیشرفت با ShapeRenderer در بلوک جداگانه
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        progressBar.height = (fishingProgress / catchDuration) * barPoleBounds.height;
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.rect(progressBar.x, progressBar.y, progressBar.width, progressBar.height);
        shapeRenderer.end();

        // به‌روزرسانی منطق بازی
        if (gameState == GameState.PLAYING) {
            updatePlayerBar(delta);
            updateFishBar(delta);

            if (playerBar.overlaps(fishBar)) {
                fishingProgress += delta;
            } else {
                fishingProgress -= delta;
                isPerfectCatch = false; // اگر ماهی از میله خارج شود، صید Perfect نیست
            }
            fishingProgress = MathUtils.clamp(fishingProgress, 0, catchDuration);

            if (fishingProgress >= catchDuration) {
                endGame(true);
            }
            if (fishingProgress <= 0 && timer > 3f) {
                endGame(false);
            }

            // خروج از بازی با کلید 'Q'
            if (Gdx.input.isKeyJustPressed(Keys.Q)) {
                gameView.showMapView();
            }

            timer += delta;
        } else if (gameState == GameState.END_SCREEN) {
            timer += delta;
            if (timer > 3f) {
                // تنها زمانی که زمان به پایان رسید، به صفحه نقشه بازگردید
                // اجازه می‌دهیم Game class مدیریت dispose را انجام دهد
                gameView.showMapView();
            }
        } else if (gameState == GameState.CASTING) {
            timer += delta;
            if (timer > 2f) {
                gameState = GameState.PLAYING;
                timer = 0;
            }
        }
    }

    private void updatePlayerBar(float delta) {
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            playerBar.y += playerBarSpeed * delta;
        } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            playerBar.y -= playerBarSpeed * delta;
        }
        playerBar.y = MathUtils.clamp(playerBar.y, barWaterBounds.y, barWaterBounds.y + barWaterBounds.height - playerBar.height);
    }

    private void updateFishBar(float delta) {
        fishMovementTimer -= delta;
        if (fishMovementTimer <= 0) {
            moveFish(fishMovementType);
            fishMovementTimer = 0.25f; // افزایش فرکانس حرکت ماهی
        }
        fishBar.y = MathUtils.clamp(fishBar.y, barWaterBounds.y, barWaterBounds.y + barWaterBounds.height - fishBar.height);
    }

    private void moveFish(FishMovementType movementType) {
        float moveDistance = 0;
        int randomMove = MathUtils.random(2); // 0=Down, 1=Still, 2=Up

        // افزایش مقادیر حرکت برای سرعت بیشتر
        switch (movementType) {
            case MIXED:
                if (randomMove == 0) moveDistance = -5;
                if (randomMove == 2) moveDistance = 5;
                break;
            case SMOOTH:
                if (MathUtils.randomBoolean(0.7f)) {
                    moveDistance = fishBarDirection * 7;
                } else {
                    fishBarDirection *= -1;
                    moveDistance = fishBarDirection * 7;
                }
                break;
            case SINKER:
                if (randomMove == 0) moveDistance = -12;
                if (randomMove == 2) moveDistance = 7;
                break;
            case FLOATER:
                if (randomMove == 0) moveDistance = -7;
                if (randomMove == 2) moveDistance = 12;
                break;
            case DART:
                randomMove = MathUtils.random(2);
                if (randomMove == 0) moveDistance = -9;
                if (randomMove == 2) moveDistance = 9;
                break;
        }

        fishBar.y += moveDistance;
        fishBar.y = MathUtils.clamp(fishBar.y, barWaterBounds.y, barWaterBounds.y + barWaterBounds.height - fishBar.height);
    }

    private FishMovementType getFishMovementType(Fish fish) {
        if (fish.getName().equals("Salmon")) return FishMovementType.SMOOTH;
        if (fish.getName().equals("Squid")) return FishMovementType.SINKER;
        if (fish.getName().equals("Flounder")) return FishMovementType.FLOATER;
        if (fish.getName().equals("Ghostfish")) return FishMovementType.DART;
        return FishMovementType.MIXED;
    }

    private void endGame(boolean success) {
        gameState = GameState.END_SCREEN;
        timer = 0;

        if (success) {
            if (caughtFish != null) {
                Item fish =new Item(caughtFish.getName(),1,"assets/Fish/"+caughtFish.getName()+".png");
                if(caughtFish.getQuality().equals("Silver")){
                    fish.setQuality("Golden");
                }
                else if(caughtFish.getQuality().equals("Golden")){
                    fish.setQuality("Iridium");
                }
                user.getInventory().addItem(fish);
                float xpMultiplier = 1.0f;
                if (isPerfectCatch) {
                    xpMultiplier = 2.4f;
                    endMessage = "Perfect Catch! You caught a " + caughtFish.getName() + "!";
                } else {
                    endMessage = "You caught a " + caughtFish.getName() + "!";
                }
                user.increaseFishingSkills((int)(50 * xpMultiplier)* user.getFishingSkills());
            } else {
                endMessage = "You didn't catch anything this time.";
            }
        } else {
            endMessage = "The fish got away...";
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        if (fishingSystemTexture != null) fishingSystemTexture.dispose();
        if (greenBarTexture != null) greenBarTexture.dispose();
        if (normalFishTexture != null) normalFishTexture.dispose();
        if (legendFishTexture != null) legendFishTexture.dispose();
    }
}
