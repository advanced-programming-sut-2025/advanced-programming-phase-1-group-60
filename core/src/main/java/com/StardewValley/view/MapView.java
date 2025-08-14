package com.StardewValley.view;

import com.StardewValley.AssetsManager.*;
import com.StardewValley.controller.GamePlayController;
import com.StardewValley.controller.HomeController;
import com.StardewValley.controller.WeatherController;
import com.StardewValley.models.*;
import com.StardewValley.models.Tree;
import com.StardewValley.repository.FishingRepository;
import com.StardewValley.repository.FruitsAndVegetablesRepository;
import com.StardewValley.repository.UserRepository;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapView implements Screen {
    private final GameMap gameMap;
    private final com.StardewValley.models.Game gameInstance;
    private GamePlayController gamePlayController;
    private final GameView gameView;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private MapManager mapManager;
    private static final float DEFAULT_ZOOM = 0.6f;
    private Map<Integer, String> farmOwners = new HashMap<>();
    private float messageTimer = 0f;
    private static final float MESSAGE_DISPLAY_TIME = 5f;
    private Texture fallbackTexture;
    private Texture playerTexture;
    private static final boolean DEBUG_GIANT = false;
    private Vector2 playerPos;
    private Map<User, GamePlayController> playerControllers = new HashMap<>();
    private Map<User, Vector2> playerPositions = new HashMap<>();
    private Map<User, Boolean> playerInVillageState = new HashMap<>();
    private GamePlayController currentPlayerController;
    private static final float WALK_ENERGY_COST = 0.5f; // 0.5 energy per tile moved
    private static final int ENERGY_LIMIT_PER_TURN = 50;
    private float energyUsedThisTurn = 0;
    private Vector2 lastEnergyTile = new Vector2(-1, -1);
    private ToolManager toolManager;
    private int selectedQuickSlot = 0;
    private Vector2 renderOffset;
    private boolean fishInitialized = false;
    private Texture nightFogTexture;
    private SpriteBatch nightFogBatch;
    private boolean nightFogReady = false;
    private float nightProgress = 0f;
    private float nightProgressTarget = 0f;
    // --- NEW: Inventory selection and quantity fields ---
    private SelectBox<String> inventorySelectBox;
    private TextField useItemQuantityField;
    private Label currentSelectedItemQuantityLabel;
    private Item selectedInventoryItem; // To store the actual selected Item object
    // --- END NEW ---
    private boolean nightEnabled = true;
    private final Map<String, Texture> quickItemTextureCache = new HashMap<>();
    private static final int GREENHOUSE_REPAIR_COST = 1000;
    private static final float TILE_SIZE = 32f;
    private int currentFarmIndex = 0;
    private boolean inVillage = false;
    private TextButton startProductionButton;
    private TextButton getProductionButton;
    private Stage stage;
    private Dialog travelDialog;
    private Label npcSpeechLabel;
    private boolean speechIsShowing = false;
    private Skin skin;
    private final Runnable onBackToMenu;
    private Texture panelTexture;
    private Dialog npcContextMenu;
    private Dialog friendshipDialog;
    private Dialog questDialog;
    private Npc selectedNpc;
    private Label turnInfoLabel;
    private Label messageLabel;
    private String lastTurnMessage = "";
    private ShapeRenderer shapeRenderer;
    private boolean craftInfoMode = false;
    private int currentToolUseDirection = 0;
    // Player animation fields
    // Player animation / faint system fields
    private enum FaintState { NONE, PLAY_FAINT_ANIM, BARS_CLOSING, DONE }
    private FaintState faintState = FaintState.NONE;
    private float faintAnimTime = 0f;
    private float barsProgress = 0f;     // 0 -> 1
    private boolean faintTurnAdvanced = false;
    private static final float BARS_CLOSE_DURATION = 1.4f; // seconds

    // OLD: private static final int FAINT_ENERGY_THRESHOLD = 3;  // (Remove this)
    // NEW: per-turn threshold (remaining turn energy)
    private static final float FAINT_TURN_REMAINING_THRESHOLD = 3f;
    private static final float TURN_FAINT_USED_THRESHOLD = ENERGY_LIMIT_PER_TURN - FAINT_TURN_REMAINING_THRESHOLD; // 47
    private float faintCooldownTimer = 0f;
    private boolean faintLockedInput = false;
    private boolean wateringHintShown = false;
    private Texture shadowTexture;
    private Animation<TextureRegion> currentPlayerAnimation;
    private Animation<TextureRegion> lastWalkingAnimation;
    private float animationCooldown = 0f;
    private static final float ANIMATION_PERSIST_TIME = 0.15f;
    private float animationTime = 0f;
    private int lastDirection = 0; // 0=down, 1=right, 2=up, 3=left
    private boolean isMoving = false;
    private SelectBox<String> buildingSelectBox;
    private String selectedBuildingType;
    private Pixmap lastFramePixmap;
    private Texture lastFrameTexture;
    private boolean lightningTargetMode = false;
    private boolean lightningActive = false;
    private float lightningTime = 0f;
    private Vector2 lightningWorldPos = null;
    private static final float LIGHTNING_WIDTH = 64f;
    private static final float LIGHTNING_HEIGHT = 160f;
    private static final float FLASH_DURATION = 0.18f;
    private static final float FLASH_MAX_ALPHA = 0.55f;
    private Set<Tile> burntTreeTiles = new HashSet<>();
    private static final String[] ALL_POSSIBLE_BUILDING_NAMES = {
        "Bee_House", "Cheese_Press", "Keg", "Dehydrator", "Charcoal_Kiln",
        "Loom", "Mayonnaise_Machine", "Oil_Maker", "Preserves_Jar",
        "Fish_Smoker", "Furnace"
    };
    private BuildingDetailsDialog buildingContextMenu;
    private PlaceableGameBuilding selectedBuilding;
    private FarmingDialog farmingDialog;
    private boolean uiBlockedByDialog = false;
    private Seeds pendingSelectedSeed;
    private boolean inGreenhouse = false;
    private Vector2 greenhousePlayerPos = new Vector2();
    private Vector2 outsideReturnPos = new Vector2();
    private GreenhouseManager greenhouseManager = GreenhouseManager.getInstance();
    private static final float GREENHOUSE_TILE_SIZE = 32f;
    //Gift players
    private Label notificationLabel;
    private float notificationTimer = 0f;

    // Clock
    private static final float DAY_BOX_X1   = 53f;
    private static final float DAY_BOX_X2   = 133f;
    private static final float DAY_BOX_Y_BOTTOM = 31f;
    private static final float DAY_BOX_Y_TOP    = 50f;
    private static final float TIME_BOX_X1  = 53f;
    private static final float TIME_BOX_X2  = 134f;
    private static final float TIME_BOX_Y_BOTTOM = -13f;
    private static final float TIME_BOX_Y_TOP    = 5f;
    private static final boolean CLOCK_DEBUG_BOUNDS = false;
    private static final float DAY_BOX_CENTER_X  = (DAY_BOX_X1 + DAY_BOX_X2) * 0.5f;
    private static final float TIME_BOX_CENTER_X = (TIME_BOX_X1 + TIME_BOX_X2) * 0.5f;
    private static final float CLOCK_SCALE = 2f;
    private boolean clickDebugMode = false;
    private int clickDebugSampleIndex = 0;
    private List<Fish> lakeFishes;
    private Texture fishTexture;
    public MapView(GameMap gameMap, Runnable onBackToMenu, GameView gameView) {
        this.gameMap = gameMap;
        this.gameView = gameView;
        this.gameInstance = com.StardewValley.models.Game.getInstance();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        skin = MenuManager.getInstance().getPixthulhuSkin();
        font.setColor(Color.WHITE);
        this.onBackToMenu = onBackToMenu;
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = DEFAULT_ZOOM;
        this.mapManager = MapManager.getInstance();
        this.toolManager = ToolManager.getInstance();
        currentPlayerAnimation = MapManager.getInstance().getIdleAnimation();
        for (GamePlayController controller : playerControllers.values()) {
            controller.setMapViewControlled(true);
        }
        CropManager.getInstance().loadAllCropGraphics();
        shadowTexture = new Texture(Gdx.files.internal("assets/Character/Shadow.png"));
        for (User player : gameInstance.getPlayers()) {
            GamePlayController controller = new GamePlayController(player.getFarm(), player, null, gameInstance);
            playerControllers.put(player, controller);

            int farmId = gameInstance.getSelectedMaps().get(player) - 1;
            Vector2 farmOffset = getFarmTopLeft(farmId);
            Vector2 initialPos = new Vector2(farmOffset.x + (25 * TILE_SIZE), farmOffset.y + (25 * TILE_SIZE));
            playerPositions.put(player, initialPos);

            playerInVillageState.put(player, false);
            player.isInVillage = false;
            player.setPosition(new Tile(25, 25));
        }
        panelTexture = new Texture(Gdx.files.internal("assets/Map/Inventory/Panel.png"));
        User currentPlayer = gameInstance.getCurrentPlayer();
        currentFarmIndex = gameInstance.getSelectedMaps().get(currentPlayer) - 1;
        playerPos = new Vector2(playerPositions.get(currentPlayer));
        inVillage = playerInVillageState.get(currentPlayer);
        currentPlayerController = playerControllers.get(gameInstance.getCurrentPlayer());
        this.gamePlayController = playerControllers.get(gameInstance.getCurrentPlayer());
        currentPlayerController = playerControllers.get(currentPlayer);
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        this.fallbackTexture = new Texture(pixmap);
        initNightFogTexture();
        pixmap.setColor(Color.RED);
        pixmap.fill();
        this.playerTexture = new Texture(pixmap);
        // for fish
        pixmap.setColor(Color.BLUE); // A simple blue square for fish
        pixmap.fill();
        this.fishTexture = new Texture(pixmap);
        pixmap.dispose();
        initializeFarmOwnerMap();

        Vector2 farmCenter = getFarmCenter(currentFarmIndex);
        this.playerPos = new Vector2(farmCenter);
        centerCameraOnPlayer();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = MenuManager.getInstance().getPixthulhuSkin();

        User user1 = UserRepository.getInstance().getUserByUsername("kamran");
        User user2 = UserRepository.getInstance().getUserByUsername("kam");
        user1.increaseFriendshipXpsWithUsers(user2, 120);
        user2.increaseFriendshipXpsWithUsers(user1, 120);
        initializeLakeFishes();
        createUI();

    }
    private String ordinal(int d) {
        if (d >= 11 && d <= 13) return d + "th";
        switch (d % 10) {
            case 1: return d + "st";
            case 2: return d + "nd";
            case 3: return d + "rd";
            default: return d + "th";
        }
    }
    private void drawClock() {
        TimeSystem ts = TimeSystem.getInstance();
        WeatherController wc = WeatherController.getInstance();
        if (wc.forecastWeather == null) wc.getForecast();

        Texture frame = mapManager.getSeasonClockFrame(ts.getCurrentSeason());
        float padX = 0f;
        float padY = 0f;

        float topLeftX = camera.position.x - (Gdx.graphics.getWidth() / 2f) * camera.zoom + padX;
        float topLeftY = camera.position.y + (Gdx.graphics.getHeight() / 2f) * camera.zoom - padY;

        if (frame == null) {
            font.setColor(Color.WHITE);
            font.draw(batch, String.format("%02d:00", ts.getCurrentHour()), topLeftX, topLeftY);
            return;
        }

        float fw = frame.getWidth() * CLOCK_SCALE;
        float fh = frame.getHeight() * CLOCK_SCALE;
        float clockX = topLeftX;
        float clockY = topLeftY - fh;

        batch.setColor(Color.WHITE);
        batch.draw(frame, clockX, clockY, fw, fh);

        String timeText = String.format("%02d:00", ts.getCurrentHour());
        String dow = ts.getDayOfWeek();
        String dayText = ((dow != null && dow.length() >= 3) ? dow.substring(0,3) : "Day") + ", " + ordinal(ts.getCurrentDay());

        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
        font.setColor(Color.WHITE);

        float TEXT_X_SHIFT = 3f;

        layout.setText(font, timeText);
        float timeDrawX = clockX + (TIME_BOX_CENTER_X - layout.width / 2f) + TEXT_X_SHIFT;
        float timeBaselineY = clockY + fh * 0.45f + layout.height / 2f;
        font.draw(batch, layout, timeDrawX, timeBaselineY);

        layout.setText(font, dayText);
        float dayDrawX = clockX + (DAY_BOX_CENTER_X - layout.width / 2f) + TEXT_X_SHIFT;
        float dayBaselineY = clockY + fh * 0.85f + layout.height / 2f;
        font.draw(batch, layout, dayDrawX, dayBaselineY);

        if (CLOCK_DEBUG_BOUNDS) {
            Texture px = mapManager.getTexture("pixel_white");
            if (px != null) {
                float dayBoxWidth  = DAY_BOX_X2 - DAY_BOX_X1;
                float dayBoxHeight = DAY_BOX_Y_TOP - DAY_BOX_Y_BOTTOM;
                float timeBoxWidth = TIME_BOX_X2 - TIME_BOX_X1;
                float timeBoxHeight = TIME_BOX_Y_TOP - TIME_BOX_Y_BOTTOM;

                batch.setColor(1,0,0,0.25f);
                batch.draw(px, clockX + DAY_BOX_X1,  clockY + DAY_BOX_Y_BOTTOM, dayBoxWidth, dayBoxHeight);
                batch.setColor(0,0,1,0.25f);
                batch.draw(px, clockX + TIME_BOX_X1, clockY + TIME_BOX_Y_BOTTOM, timeBoxWidth, timeBoxHeight);
                batch.setColor(Color.WHITE);
            }
        }
    }

    private void drawBottomLeftMessage() {
        if (messageTimer <= 0 || lastTurnMessage == null || lastTurnMessage.isEmpty()) return;
        float bottomLeftX = camera.position.x - (Gdx.graphics.getWidth() / 2f) * camera.zoom + 10f;
        float bottomLeftY = camera.position.y - (Gdx.graphics.getHeight() / 2f) * camera.zoom + font.getLineHeight() + 10f;
        font.setColor(Color.WHITE);
        font.draw(batch, lastTurnMessage, bottomLeftX, bottomLeftY);
    }
    private void initializeLakeFishes() {
        lakeFishes = new ArrayList<>();
        List<Fish> allAvailableFishes = FishingRepository.getFishes(); // Get all fish from repository

        if (!allAvailableFishes.isEmpty()) {
            Random random = new Random();
            Fish fish = allAvailableFishes.get(random.nextInt(allAvailableFishes.size()));
            float randomOffsetX = random.nextFloat() * (6 - TILE_SIZE); // Subtract TILE_SIZE to ensure fish stays within bounds
            float randomOffsetY = random.nextFloat() * (4 - TILE_SIZE);

            float nx = randomOffsetX;
            float ny =randomOffsetY;
            fish.setPosition(nx, ny);
            fishTexture = new Texture(Gdx.files.internal("assets/Fish/"+fish.getName()+".png"));
            lakeFishes.add(fish);

        }
    }
    private void handleGameplayMechanics(float delta) {
        User currentPlayer = gameInstance.getCurrentPlayer();
        Vector2 oldPos = new Vector2(playerPos);
        float speed = 200 * delta;
        Vector2 velocity = new Vector2();

        boolean wasMoving = isMoving;
        isMoving = false;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x -= 1;
            lastDirection = 3; // Left
            isMoving = true;
            lastWalkingAnimation = MapManager.getInstance().getWalkLeftAnimation();
            currentPlayerAnimation = lastWalkingAnimation;
            animationCooldown = ANIMATION_PERSIST_TIME;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x += 1;
            lastDirection = 1; // Right
            isMoving = true;
            lastWalkingAnimation = MapManager.getInstance().getWalkRightAnimation();
            currentPlayerAnimation = lastWalkingAnimation;
            animationCooldown = ANIMATION_PERSIST_TIME;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.G)) {
            gameView.showGiftToPlayerView();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.y += 1;
            lastDirection = 2; // Up
            isMoving = true;
            lastWalkingAnimation = MapManager.getInstance().getWalkUpAnimation();
            currentPlayerAnimation = lastWalkingAnimation;
            animationCooldown = ANIMATION_PERSIST_TIME;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.y -= 1;
            lastDirection = 0; // Down
            isMoving = true;
            lastWalkingAnimation = MapManager.getInstance().getWalkDownAnimation();
            currentPlayerAnimation = lastWalkingAnimation;
            animationCooldown = ANIMATION_PERSIST_TIME;
        }
        if (!isMoving) {
            if (animationCooldown > 0) {
                animationCooldown -= delta;
                currentPlayerAnimation = lastWalkingAnimation;
            } else {
                currentPlayerAnimation = MapManager.getInstance().getIdleAnimation();
            }
        }
        velocity.nor().scl(speed);

        if (velocity.len() > 0) {
            Vector2 newPos = new Vector2(playerPos);

            if (isAreaPassable(newPos.x + velocity.x, newPos.y)) {
                newPos.x += velocity.x;
            }
            if (isAreaPassable(newPos.x, newPos.y + velocity.y)) {
                newPos.y += velocity.y;
            }

            if (!oldPos.equals(newPos)) {
                boolean canMove = true;
                if (!currentPlayer.getEnergy().isUnlimited()) {
                    int currentTileX = (int) (newPos.x / TILE_SIZE);
                    int currentTileY = (int) (newPos.y / TILE_SIZE);
                    int lastTileX = (int) (lastEnergyTile.x);
                    int lastTileY = (int) (lastEnergyTile.y);
                    boolean enteredNewTile = (currentTileX != lastTileX || currentTileY != lastTileY);

                    if (enteredNewTile && energyUsedThisTurn < ENERGY_LIMIT_PER_TURN &&
                        currentPlayer.getEnergy().getCurrentEnergy() >= WALK_ENERGY_COST) {

                        // Check if this movement would exceed the per-turn limit
                        if (energyUsedThisTurn + WALK_ENERGY_COST > ENERGY_LIMIT_PER_TURN) {
                            showMessage("Energy limit reached for this turn (50/50)", 2);
                            canMove = false;
                        } else {
                            currentPlayer.getEnergy().decreaseEnergy((int) WALK_ENERGY_COST);
                            energyUsedThisTurn += WALK_ENERGY_COST;
                            lastEnergyTile.set(currentTileX, currentTileY);
                        }
                    } else if (enteredNewTile && energyUsedThisTurn >= ENERGY_LIMIT_PER_TURN) {
                        showMessage("Energy limit reached for this turn", 2);
                        canMove = false;
                    } else if (enteredNewTile && currentPlayer.getEnergy().getCurrentEnergy() < WALK_ENERGY_COST) {
                        showMessage("Not enough energy to move", 2);
                        canMove = false;
                        lastTurnMessage = "Not enough energy to move!";
                        messageTimer = MESSAGE_DISPLAY_TIME;
                    }
                }
                if (canMove) {
                    playerPos.set(newPos);
                }
            }
        }
        animationTime += delta;
        if (messageTimer > 0) {
            messageTimer -= delta;
        }
        if (faintState == FaintState.NONE
            && faintCooldownTimer == 0f) {
            User cp = gameInstance.getCurrentPlayer();
            if (cp != null
                && !cp.getEnergy().isUnlimited()
                && energyUsedThisTurn > TURN_FAINT_USED_THRESHOLD) {
                startFaintSequence();
            }
        }
    }
    private void startFaintSequence() {
        if (faintState != FaintState.NONE) return;
        faintState = FaintState.PLAY_FAINT_ANIM;
        faintAnimTime = 0f;
        barsProgress = 0f;
        faintTurnAdvanced = false;
        faintLockedInput = true;
        showMessage("You feel dizzy...", 2f);
    }

    private void updateFaint(float delta) {
        if (faintState == FaintState.NONE) return;

        switch (faintState) {
            case PLAY_FAINT_ANIM -> {
                faintAnimTime += delta;
                Animation<TextureRegion> faintAnim = MapManager.getInstance().getFaintAnimation();
                if (faintAnim == null) {
                    faintState = FaintState.BARS_CLOSING;
                    return;
                }
                if (faintAnim.isAnimationFinished(faintAnimTime)) {
                    faintState = FaintState.BARS_CLOSING;
                }
            }
            case BARS_CLOSING -> {
                barsProgress += delta / BARS_CLOSE_DURATION;
                if (barsProgress >= 1f) {
                    barsProgress = 1f;
                    faintState = FaintState.DONE;
                }
            }
            case DONE -> {
                if (!faintTurnAdvanced) {
                    advanceTurnAfterFaint();
                    faintTurnAdvanced = true;
                }
            }
        }
    }

    private void advanceTurnAfterFaint() {
        User previous = gameInstance.getCurrentPlayer();

        // Persist previous player's latest position & state exactly like K handling would
        if (previous != null) {
            playerPositions.put(previous, new Vector2(playerPos));
            playerInVillageState.put(previous, inVillage);
            if (inVillage) {
                previous.setPosition(new Tile((int)(playerPos.x / TILE_SIZE), (int)(playerPos.y / TILE_SIZE)));
            } else {
                Vector2 farmOffset = getFarmTopLeft(currentFarmIndex);
                int localX = (int)((playerPos.x - farmOffset.x) / TILE_SIZE);
                int localY = (int)((playerPos.y - farmOffset.y) / TILE_SIZE);
                previous.setPosition(new Tile(localX, localY));
            }
            previous.isInVillage = inVillage;

            // Optional penalty: drop energy to 0
            if (!previous.getEnergy().isUnlimited()) {
                previous.getEnergy().setCurrentEnergy(150);
            }
        }

        // Switch turn
        gameInstance.nextTurn();
        User newCurrent = gameInstance.getCurrentPlayer();

        // Reset per-turn counters to avoid retrigger
        energyUsedThisTurn = 0f;              // FIX: critical to prevent immediate re-faint
        lastEnergyTile.set(-1, -1);
        faintCooldownTimer = 0.25f;           // short grace period

        // Load new player's position / state
        currentPlayerController = playerControllers.get(newCurrent);
        currentFarmIndex = gameInstance.getSelectedMaps().get(newCurrent) - 1;
        playerPos = new Vector2(playerPositions.get(newCurrent));
        inVillage = playerInVillageState.get(newCurrent);
        centerCameraOnPlayer();

        lastTurnMessage = (previous != null ? previous.getUsername() : "Player")
            + " fainted! Now: " + newCurrent.getUsername();
        messageTimer = MESSAGE_DISPLAY_TIME;

        // Reset faint system
        faintState = FaintState.NONE;
        faintLockedInput = false;
    }

    private boolean isFaintingActive() {
        return faintState != FaintState.NONE;
    }

    private boolean handleToolUsage(Vector3 clickPos) {
        User currentPlayer = gameInstance.getCurrentPlayer();
        Item[] quickSlots = currentPlayer.getInventory().getQuickAccessSlots();
        Item selectedItem = quickSlots[selectedQuickSlot];

        if (selectedItem instanceof Tools) {
            Tools tool = (Tools) selectedItem;

            // Prevent overlap while already animating
            if (toolManager.isUsingTool()) {
                return true;
            }

            // Determine direction based on click position relative to player
            currentToolUseDirection = computeDirectionFromClick(clickPos);

            // Start new tool-use animation (character animation)
            toolManager.startToolUse(currentToolUseDirection);

            // (Optional) still start legacy swing if you want rotation overlay for some tools; you asked to remove icons, not necessarily rotation.
            // If you want to fully suppress old rotation, comment the next line.
            toolManager.startToolSwing(tool);

            Vector2 farmTopLeft = inVillage ? new Vector2(0, 0) : getFarmTopLeft(currentFarmIndex);
            int clickedTileX = (int) ((clickPos.x - farmTopLeft.x) / TILE_SIZE);
            int clickedTileY = (int) ((clickPos.y - farmTopLeft.y) / TILE_SIZE);

            if ("Hoe".equals(tool.getName())) {
                return handleHoeUsage(tool, clickPos);
            }
            if ("Scythe".equalsIgnoreCase(tool.getName())) {
                handleScytheUsage(clickPos, currentPlayer, tool);
                return true;
            }
            if ("Watering Can".equalsIgnoreCase(tool.getName())
                || "WateringCan".equalsIgnoreCase(tool.getName())
                || "Watering_Can".equalsIgnoreCase(tool.getName())) {
                if (!wateringHintShown) {
                    showMessage("Watering Can: click a plowed tile to water it.", 2f);
                    wateringHintShown = true;
                }
                waterTileAt(clickPos, currentPlayer, tool);
                return true;
            }
            else if ("Pickaxe".equals(tool.getName())) {
                Tile clickedTile = inVillage
                    ? gameMap.getVillage().getTile(clickedTileX, 20 - 1 - clickedTileY)
                    : gameMap.getFarm(currentFarmIndex).getTile(clickedTileX, FarmTemplate.HEIGHT - 1 - clickedTileY);
                if (clickedTile != null) {
                    if (clickedTile.getRandomElement().isPresent() && clickedTile.getRandomElement().get() instanceof Stone) {
                        Stone stone = (Stone) clickedTile.getRandomElement().get();
                        int energyCost = tool.getEnergyCost();
                        if (currentPlayer.getEnergy().getCurrentEnergy() < energyCost && !currentPlayer.getEnergy().isUnlimited()) {
                            showMessage("Not enough energy to use the pickaxe", 2);
                            return true;
                        }
                        ForagingMineral mineral = stone.getMineral();
                        if (mineral != null) {
                            int quantity = (currentPlayer.getSkill("Mining").getLevel() >= 2) ? 2 : 1;
                            String imagePath = mineral.getImagePath();
                            Item mineralItem = new Item(mineral.getName(), quantity, "Map/ForagingMineral/" + imagePath);
                            mineralItem.setSellPrice(mineral.getBaseSellPrice());
                            currentPlayer.getInventory().addItem(mineralItem);
                            clickedTile.setToNormalTile();
                            clickedTile.setType(".");
                            if (!currentPlayer.getEnergy().isUnlimited()) {
                                currentPlayer.getEnergy().decreaseEnergy(energyCost);
                                energyUsedThisTurn += energyCost;
                            }
                            currentPlayer.getSkill("Mining").gainExperience(10);
                            showMessage("Mined " + quantity + " " + mineral.getName(), 2);
                        }
                        return true;
                    } else if (clickedTile.isPlowed()) {
                        clickedTile.setPlowed(false);
                        showMessage("Removed plowed ground", 2);
                        return true;
                    } else {
                        showMessage("Nothing to mine here", 2);
                        return true;
                    }
                }
                return true;
            }
            else if ("Axe".equals(tool.getName())) {
                Tile clickedTile = inVillage
                    ? gameMap.getVillage().getTile(clickedTileX, 20 - 1 - clickedTileY)
                    : gameMap.getFarm(currentFarmIndex).getTile(clickedTileX, FarmTemplate.HEIGHT - 1 - clickedTileY);

                if (clickedTile != null && clickedTile.getRandomElement().isPresent()) {
                    Object element = clickedTile.getRandomElement().get();
                    int energyCost = tool.getEnergyCost();
                    if (currentPlayer.getEnergy().getCurrentEnergy() < energyCost && !currentPlayer.getEnergy().isUnlimited()) {
                        showMessage("Not enough energy to use the axe", 2);
                        return true;
                    }
                    if (element instanceof Tree ||
                        element instanceof ForagingTree ||
                        element instanceof ForagingCrop ||
                        element instanceof ForagingMineral) {

                        clickedTile.setToNormalTile();
                        clickedTile.setType(".");
                        if (!currentPlayer.getEnergy().isUnlimited()) {
                            currentPlayer.getEnergy().decreaseEnergy(energyCost);
                            energyUsedThisTurn += energyCost;
                        }
                        if (element instanceof Tree || element instanceof ForagingTree) {
                            currentPlayer.getSkill("Foraging").gainExperience(10);
                        } else if (element instanceof ForagingMineral) {
                            currentPlayer.getSkill("Mining").gainExperience(10);
                        }
                        showMessage("Cleared object.", 2);
                        return true;
                    } else {
                        showMessage("Can't use axe on this", 2);
                        return true;
                    }
                } else {
                    showMessage("Nothing to chop here", 2);
                    return true;
                }
            }

            return true;
        }
        return false;
    }
    private int computeDirectionFromClick(Vector3 clickPos) {
        // Player center (approx) - playerPos is bottom-left; use half tile
        float playerCenterX = playerPos.x + TILE_SIZE / 2f;
        float playerCenterY = playerPos.y + TILE_SIZE / 2f;
        float dx = clickPos.x - playerCenterX;
        float dy = clickPos.y - playerCenterY;

        // If both very small, fallback to last walking direction
        if (Math.abs(dx) < 0.001f && Math.abs(dy) < 0.001f) {
            return currentToolUseDirection;
        }

        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? 1 : 3; // right or left
        } else {
            return dy > 0 ? 2 : 0; // up or down
        }
    }
    private void handleScytheUsage(Vector3 clickPos, User player, Tools scythe) {
        Vector2 farmTopLeft = inVillage ? new Vector2(0,0) : getFarmTopLeft(currentFarmIndex);
        int tx = (int)((clickPos.x - farmTopLeft.x)/TILE_SIZE);
        int ty = (int)((clickPos.y - farmTopLeft.y)/TILE_SIZE);

        Tile tile;
        if (inGreenhouse) {
            int gx = (int)(clickPos.x / GREENHOUSE_TILE_SIZE);
            int gy = (int)(clickPos.y / GREENHOUSE_TILE_SIZE);
            Tile t = greenhouseManager.getTile(gx, gy);
            if (t == null) return;

            Seeds seed = t.getPlantedSeed();
            if (seed == null) {
                showMessage("No crop to harvest.", 1.2f);
                return;
            }
            if (t.getDaysGrown() < seed.getTotalHarvestTime()) {
                showMessage("Crop not ready.", 1.2f);
                return;
            }

            // Create produce item
            String cropName = seed.getGrowsInto();
            FruitsAndVegetables fv = FruitsAndVegetablesRepository.getCropByName(cropName);
            int sellPrice = (fv != null && fv.getSellPrice() != 0) ? fv.getSellPrice() : 0;

            String producePath = CropManager.getInstance().resolveProduceInventoryPath(cropName);
            Item produce = new Item(cropName, 1, producePath != null ? producePath : "");
            produce.setSellPrice(sellPrice);
            player.getInventory().addItem(produce);

            // Reset tile (one-time harvest assumption)
            t.setCrop(null);
            t.setPlantedSeed(null);
            t.setPlowed(true);
            t.setDaysGrown(0);
            t.setWatered(false);

            showMessage("Harvested " + cropName + ".", 1.5f);
            return;
        }
        if (inVillage) {
            if (tx <0 || tx >=20 || ty <0 || ty >=20) return;
            tile = gameMap.getVillage().getTile(tx, 20 - 1 - ty);
        } else {
            if (tx <0 || tx >= FarmTemplate.WIDTH || ty <0 || ty >= FarmTemplate.HEIGHT) return;
            tile = gameMap.getFarm(currentFarmIndex).getTile(tx, FarmTemplate.HEIGHT -1 - ty);
        }
        if (tile == null) return;

        // GIANT CROP HARVEST SUPPORT  /// PATCH
        if (!inVillage && tile.isGiantCrop()) {
            Farm farm = gameMap.getFarm(currentFarmIndex);
            harvestGiantCropCluster(tile, farm, player);
            return;
        }

        Seeds seed = tile.getPlantedSeed();
        if (seed == null) {
            showMessage("No crop to harvest.",1.2f);
            return;
        }
        if (tile.getDaysGrown() < seed.getTotalHarvestTime()) {
            showMessage("Crop not ready.",1.2f);
            return;
        }

        FruitsAndVegetables fv = FruitsAndVegetablesRepository.getCropByName(seed.getGrowsInto());
        boolean oneTime = true;
        int sellPrice = 0;
        if (fv != null) {
            oneTime = fv.isOneTime();
            if (fv.getSellPrice() != 0) sellPrice = fv.getSellPrice();
        }

        String producePath = CropManager.getInstance().resolveProduceInventoryPath(seed.getGrowsInto());
        Item produce = new Item(seed.getGrowsInto(), 1, producePath != null ? producePath : "");
        produce.setSellPrice(sellPrice);
        player.getInventory().addItem(produce);

        if (oneTime) {
            tile.setCrop(null);
            tile.setPlantedSeed(null);
            tile.setPlowed(true);
            tile.setDaysGrown(0);
            tile.setWatered(false);
            showMessage("Harvested " + produce.getName() + ".", 1.5f);
        } else {
            int regrowDays = 5;
            if (fv != null && fv.getRegrowthTime() != null && fv.getRegrowthTime() > 0) {
                regrowDays = fv.getRegrowthTime();
            }
            tile.setDaysGrown(0);
            tile.activateMultiHarvestBase(regrowDays);
            showMessage("Harvested " + produce.getName() + ". Regrowing (" + regrowDays + "d)...", 1.8f);
        }
    }
    private void harvestGiantCropCluster(Tile anyTileInCluster, Farm farm, User player) {
        int ix = anyTileInCluster.getPositionX();
        int iy = anyTileInCluster.getPositionY();

        // Find origin (bottom-left in display = internal tile with no giant to left OR no giant below in display mapping)
        // Using internal origin rule: origin has no giant left & no giant with y+1
        int originX = ix;
        int originY = iy;
        // Move left while possible
        while (originX > 0) {
            Tile left = farm.getTile(originX -1, originY);
            if (left == null || !left.isGiantCrop()) break;
            originX--;
        }
        // Move "down" in display terms => internal y+1 while still giant
        while (originY < FarmTemplate.HEIGHT -1) {
            Tile below = farm.getTile(originX, originY +1);
            if (below == null || !below.isGiantCrop()) break;
            originY++;
        }

        // Verify 2x2 cluster
        Tile t00 = farm.getTile(originX, originY);
        Tile t10 = farm.getTile(originX+1, originY);
        Tile t01 = farm.getTile(originX, originY-1);
        Tile t11 = farm.getTile(originX+1, originY-1);
        if (t00==null||t10==null||t01==null||t11==null) return;
        if (!(t00.isGiantCrop() && t10.isGiantCrop() && t01.isGiantCrop() && t11.isGiantCrop())) return;

        Seeds seed = t00.getPlantedSeed();
        if (seed == null) {
            showMessage("Giant crop missing seed data.",1.5f);
            return;
        }
        FruitsAndVegetables fv = FruitsAndVegetablesRepository.getCropByName(seed.getGrowsInto());
        int sellPrice = (fv != null && fv.getSellPrice()!= 0) ? fv.getSellPrice() : 0;

        String producePath = CropManager.getInstance().resolveProduceInventoryPath(seed.getGrowsInto());
        Item produce = new Item(seed.getGrowsInto(), 4, producePath != null ? producePath : "");
        produce.setSellPrice(sellPrice);
        player.getInventory().addItem(produce);

        // Clear all four tiles
        Tile[] cluster = {t00,t10,t01,t11};
        for (Tile t : cluster) {
            t.setGiantCrop(false);
            t.setCrop(null);
            t.setPlantedSeed(null);
            t.setPlowed(true);
            t.setDaysGrown(0);
            t.setWatered(false);
        }
        showMessage("Harvested GIANT " + seed.getGrowsInto() + " x4!", 2.5f);
    }
    private void waterTileAt(Vector3 clickPos, User player, Tools tool) {
        if (inGreenhouse) {
            int gx = (int)(clickPos.x / GREENHOUSE_TILE_SIZE);
            int gy = (int)(clickPos.y / GREENHOUSE_TILE_SIZE);
            Tile t = greenhouseManager.getTile(gx, gy);
            if (t == null) return;
            if (!t.isPlowed()) {
                showMessage("Tile not plowed.", 1.2f);
                return;
            }
            if (t.isWatered()) {
                showMessage("Already watered today.", 1.2f);
                return;
            }
            int cost = tool.getEnergyCost();
            if (!player.getEnergy().isUnlimited() && player.getEnergy().getCurrentEnergy() < cost) {
                showMessage("Not enough energy.", 1.5f);
                return;
            }
            t.setWatered(true);
            t.setLastWateredDay(TimeSystem.getInstance().getCurrentDay());
            if (!player.getEnergy().isUnlimited()) {
                player.getEnergy().decreaseEnergy(cost);
                energyUsedThisTurn += cost;
            }
            showMessage("Watered (Greenhouse).", 1.2f);
            return;
        }
        if (inVillage) {
            showMessage("Cannot water in village.",1.5f);
            return;
        }
        Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
        int tx = (int)((clickPos.x - farmTopLeft.x)/TILE_SIZE);
        int ty = (int)((clickPos.y - farmTopLeft.y)/TILE_SIZE);
        if (tx <0||tx>=FarmTemplate.WIDTH||ty<0||ty>=FarmTemplate.HEIGHT) return;
        Tile tile = gameMap.getFarm(currentFarmIndex).getTile(tx, FarmTemplate.HEIGHT -1 - ty);
        if (tile == null) return;
        if (!tile.isPlowed()) {
            showMessage("Tile not plowed.",1.2f);
            return;
        }
        if (tile.isWatered()) {
            showMessage("Already watered today.",1.2f);
            return;
        }
        // Energy check
        int cost = tool.getEnergyCost();
        if (!player.getEnergy().isUnlimited() && player.getEnergy().getCurrentEnergy()<cost) {
            showMessage("Not enough energy.",1.5f);
            return;
        }
        tile.setWatered(true);
        tile.setLastWateredDay(TimeSystem.getInstance().getCurrentDay());
        if (!player.getEnergy().isUnlimited()) {
            player.getEnergy().decreaseEnergy(cost);
            energyUsedThisTurn += cost;
        }
        showMessage("Watered tile.",1.2f);
    }
    private void createUI() {
        createTravelDialog();
        createBackButton();
        createNpcContextMenu();
        createFriendshipDialog();
        createQuestDialog();
        createBuildingContextMenu();
        npcSpeechLabel = new Label("", skin);
        npcSpeechLabel.setWrap(true);
        npcSpeechLabel.setAlignment(Align.center);
        npcSpeechLabel.setVisible(false);
        stage.addActor(npcSpeechLabel);
    }

    private void createQuestDialog() {
        questDialog = new Dialog("Quests", skin);
        questDialog.setModal(true);
        ScrollPane scrollPane = new ScrollPane(null, skin);
        questDialog.getContentTable().add(scrollPane).grow().pad(10);
        questDialog.getButtonTable().add(new TextButton("Close", skin)).pad(10).getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                questDialog.hide();
            }
        });
    }

    private void showQuestDialog(Npc npc) {
        User currentPlayer = gameInstance.getCurrentPlayer();
        Table questListTable = new Table(skin);
        questListTable.top().left();

        if (npc.getQuests() == null || npc.getQuests().isEmpty()) {
            questListTable.add("This person has no quests.").pad(20);
        } else {
            for (Quest quest : npc.getQuests()) {
                String questText = "ID " + quest.getId() + ": Bring " + quest.getRequiredItems().getQuantity() + " " + quest.getRequiredItems().getName();
                TextButton questButton = new TextButton(questText, skin);
                User completer = quest.getCompletedBy();
                if (completer != null) {
                    if (completer.equals(currentPlayer)) {
                        questButton.getLabel().setColor(Color.GREEN);
                    } else {
                        questButton.getLabel().setColor(Color.RED);
                    }
                    questButton.setDisabled(true);
                } else {
                    questButton.getLabel().setColor(Color.LIGHT_GRAY);
                    questButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            completeQuest(currentPlayer, npc, quest);
                            questDialog.hide();
                            showQuestDialog(npc);
                        }
                    });
                }
                questListTable.add(questButton).left().pad(5).row();
            }
        }
        ((ScrollPane) questDialog.getContentTable().getCells().first().getActor()).setActor(questListTable);
        questDialog.show(stage);
    }

    private void completeQuest(User user, Npc npc, Quest quest) {
        if (quest.getActivationFriendLevel() > 0 && user.getFriendshipLevelWithNpc(npc) < quest.getActivationFriendLevel()) {
            showResultDialog("Quest not active yet. You need a higher friendship level.");
            return;
        }
        Item requiredItem = quest.getRequiredItems();
        if (requiredItem != null) {
            if (!user.getInventory().hasItem(requiredItem.getName(), requiredItem.getQuantity())) {
                showResultDialog("You don't have the required items: " + requiredItem.getQuantity() + " " + requiredItem.getName());
                return;
            }
            user.getInventory().removeItemByName(requiredItem.getName(), requiredItem.getQuantity());
        }
        Reward reward = quest.getReward();
        if (reward != null) {
            if (reward.getMoney() > 0) {
                user.setMoney(user.getMoney() + reward.getMoney());
            }
            if (reward.getItems() != null) {
                user.getInventory().addItem(reward.getItems());
            }
            if (reward.getFriendshipXp() > 0) {
                user.increaseFriendshipXpsWithNpc(npc, reward.getFriendshipXp());
            }
        }
        quest.complete(user);
        showResultDialog("Quest '" + quest.getId() + "' completed successfully!");
    }

    private void showResultDialog(String message) {
        Dialog dialog = new Dialog("Result", skin);
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }
    public int getCurrentFarmIndex() {
        return currentFarmIndex;
    }
    public boolean isInVillage() {
        return inVillage;
    }
    public Vector2 getPlayerWorldPosition() {
        return new Vector2(playerPos); // safe copy
    }
    private void createBackButton() {
        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.top().right();
        TextButton backButton = new TextButton("Back to Menu", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onBackToMenu.run();
            }
        });
        uiTable.add(backButton).pad(10);
        buildingSelectBox = new SelectBox<>(skin);
        buildingSelectBox.setVisible(true); // Initially hide it
        buildingSelectBox.setSize(200, 30);
        buildingSelectBox.setPosition(Gdx.graphics.getWidth() - buildingSelectBox.getWidth() - 20,
            Gdx.graphics.getHeight() - 50);

        buildingSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedBuildingType = buildingSelectBox.getSelected();
                gamePlayController.setSelectedBuildingType(selectedBuildingType);
                System.out.println("Selected building: " + selectedBuildingType);
            }
        });
        stage.addActor(buildingSelectBox); // Add SelectBox to stage

        // Initialize the selection box with available items
        updateBuildingSelectBoxItems();
        // Set initial selected type if there are available buildings
        if (buildingSelectBox.getItems().size > 0) {
            selectedBuildingType = buildingSelectBox.getItems().first();
            buildingSelectBox.setSelected(selectedBuildingType);
            gamePlayController.setSelectedBuildingType(selectedBuildingType);
        } else {
            selectedBuildingType = null; // No buildings available initially
            gamePlayController.setSelectedBuildingType(null);
        }
        uiTable.add(buildingSelectBox).pad(10);
        stage.addActor(uiTable);
    }

    private void createNpcContextMenu() {
        npcContextMenu = new Dialog("NPC Menu", skin);
        TextButton giftButton = new TextButton("Gift", skin);
        TextButton questButton = new TextButton("Quest", skin);
        TextButton friendshipButton = new TextButton("Friendship", skin);
        npcContextMenu.getContentTable().add(giftButton).row();
        npcContextMenu.getContentTable().add(questButton).row();
        npcContextMenu.getContentTable().add(friendshipButton).row();
        giftButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedNpc != null) {
                    gameView.showInventoryForGifting(selectedNpc);
                }
                npcContextMenu.hide();
            }
        });
        questButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedNpc != null) {
                    showQuestDialog(selectedNpc);
                }
                npcContextMenu.hide();
            }
        });
        friendshipButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showFriendshipDialog();
                npcContextMenu.hide();
            }
        });
        npcContextMenu.setModal(true);
    }

    private void createFriendshipDialog() {
        friendshipDialog = new Dialog("Friendship", skin);
        friendshipDialog.text("Friendship details will be shown here.");
        friendshipDialog.button("OK");
        friendshipDialog.setModal(true);
    }

    private void showFriendshipDialog() {
        if (selectedNpc != null) {
            User currentPlayer = gameInstance.getCurrentPlayer();
            int friendshipXp = currentPlayer.getFriendshipXpsWithNPCs().getOrDefault(selectedNpc, 0);
            int friendshipLevel = currentPlayer.getFriendshipLevelWithNpc(selectedNpc);
            Label content = new Label("Friendship with " + selectedNpc.getName() + ":\n" +
                "Level: " + friendshipLevel + "\n" +
                "XP: " + friendshipXp, skin);
            friendshipDialog.getContentTable().clear();
            friendshipDialog.getContentTable().add(content);
            friendshipDialog.show(stage);
        }
    }

    private void updateBuildingSelectBoxItems() {
        List<String> availableBuildingNames = new ArrayList<>();
        Set<String> inventoryItemNames = new HashSet<>();

        // Debug: Print current player and if inventory exists
        if (gamePlayController == null || Game.getInstance().getCurrentPlayer() == null || Game.getInstance().getCurrentPlayer().getInventory() == null) {
            System.out.println("DEBUG: GamePlayController, User, or Inventory is null. Cannot update select box.");
            return;
        }
        // Get all item names from the player's inventory
        for (Item item : Game.getInstance().getCurrentPlayer().getInventory().getItems()) {
            inventoryItemNames.add(item.getName()); // Assuming Item has getName()
        }

        // Filter the list of all possible buildings to only include those in inventory
        for (String buildingName : ALL_POSSIBLE_BUILDING_NAMES) {
            if (inventoryItemNames.contains(buildingName)) {
                availableBuildingNames.add(buildingName);
            }
        }

        // Convert List to Array for SelectBox
        String[] itemsArray = availableBuildingNames.toArray(new String[0]);
        buildingSelectBox.setItems(itemsArray); // This is where items are set
        // If no items are available, make sure the select box doesn't display anything invalid
        if (itemsArray.length > 0) {
            // Keep current selection if it's still available, otherwise set to first item
            if (selectedBuildingType == null || !availableBuildingNames.contains(selectedBuildingType)) {
                selectedBuildingType = itemsArray[0];
            }
            buildingSelectBox.setSelected(selectedBuildingType);
            gamePlayController.setSelectedBuildingType(selectedBuildingType);
        } else {
            selectedBuildingType = null;
            buildingSelectBox.setSelected(""); // Clear selection visually
            gamePlayController.setSelectedBuildingType(null);
        }
    }

    private void showNpcSpeech(Npc npc, User user, float npcWorldX, float npcWorldY) {
        if (speechIsShowing) return;
        speechIsShowing = true;
        String prompt = npc.startConversation(user);
        npcSpeechLabel.setText(prompt);
        npcSpeechLabel.pack();
        npcSpeechLabel.setWidth(250);
        npcSpeechLabel.setHeight(npcSpeechLabel.getPrefHeight());
        Vector3 npcScreenPos = camera.project(new Vector3(npcWorldX, npcWorldY, 0));
        npcSpeechLabel.setPosition(
            npcScreenPos.x - npcSpeechLabel.getWidth() / 2f + (TILE_SIZE / 2f),
            npcScreenPos.y + TILE_SIZE
        );
        npcSpeechLabel.setVisible(true);
        npc.recordTalkTime();
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                npcSpeechLabel.setVisible(false);
                speechIsShowing = false;
            }
        }, 3);
    }

    private void createTravelDialog() {
        travelDialog = new Dialog("Travel", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    performTravel();
                }
            }
        };
        Label contentLabel = new Label("", skin);
        contentLabel.setWrap(true);
        contentLabel.setAlignment(Align.center);
        travelDialog.getContentTable().add(contentLabel).width(250).row();
    }

    public Stage getStage() {
        return this.stage;
    }

    private void showTravelDialog(String destination) {
        if (speechIsShowing) return;
        String dialogText;
        if (destination.startsWith("Farm")) {
            int farmNum = Integer.parseInt(destination.split(" ")[1]);
            String ownerName = farmOwners.getOrDefault(farmNum - 1, "Unknown");
            dialogText = "Do you want to travel to " + destination + " (Owner: " + ownerName + ")?";
        } else {
            dialogText = "Do you want to travel to " + destination + "?";
        }
        ((Label) travelDialog.getContentTable().getCells().first().getActor()).setText(dialogText);
        travelDialog.getButtonTable().clearChildren();
        travelDialog.button("Yes", true);
        travelDialog.button("No", false);
        travelDialog.show(stage);
    }

    private void performTravel() {
        if (inVillage) {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            float destX = 0, destY = 0;
            switch (currentFarmIndex) {
                case 0:
                    destX = farmTopLeft.x + (49 * TILE_SIZE);
                    destY = farmTopLeft.y + (0 * TILE_SIZE);
                    break;
                case 1:
                    destX = farmTopLeft.x + (0 * TILE_SIZE);
                    destY = farmTopLeft.y + (0 * TILE_SIZE);
                    break;
                case 2:
                    destX = farmTopLeft.x + (49 * TILE_SIZE);
                    destY = farmTopLeft.y + (49 * TILE_SIZE);
                    break;
                case 3:
                    destX = farmTopLeft.x + (0 * TILE_SIZE);
                    destY = farmTopLeft.y + (49 * TILE_SIZE);
                    break;
            }
            playerPos.set(destX, destY);
            inVillage = false;
        } else {
            float destX = 0, destY = 0;
            switch (currentFarmIndex) {
                case 0:
                    destX = 0 * TILE_SIZE;
                    destY = 19 * TILE_SIZE;
                    break;
                case 1:
                    destX = 19 * TILE_SIZE;
                    destY = 19 * TILE_SIZE;
                    break;
                case 2:
                    destX = 0 * TILE_SIZE;
                    destY = 0 * TILE_SIZE;
                    break;
                case 3:
                    destX = 19 * TILE_SIZE;
                    destY = 0 * TILE_SIZE;
                    break;
            }
            playerPos.set(destX, destY);
            inVillage = true;
        }
        centerCameraOnPlayer();
    }

    private void createNotificationLabel() {
        notificationLabel = new Label("", skin);
        notificationLabel.setWrap(true);
        notificationLabel.setAlignment(Align.center);
        notificationLabel.setVisible(false);
        // Position it at the top-center of the screen
        Table notificationTable = new Table();
        notificationTable.top();
        notificationTable.setFillParent(true);
        notificationTable.add(notificationLabel).padTop(50);
        stage.addActor(notificationTable);
    }

    public void showNotification(String message) {
        notificationLabel.setText(message);
        notificationLabel.setVisible(true);
        notificationTimer = 5f; // Show for 5 seconds
    }

    private void initializeFarmOwnerMap() {
        try {
            List<User> players = gameInstance.getPlayers();
            for (User player : players) {
                int mapId = gameInstance.getMapSelection(player);
                if (mapId > 0) {
                    farmOwners.put(mapId - 1, player.getUsername());
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing farm owners: " + e.getMessage());
        }
    }

    private Vector2 getFarmCenter(int farmIndex) {
        Vector2 topLeft = getFarmTopLeft(farmIndex);
        return new Vector2(topLeft.x + (FarmTemplate.WIDTH / 2f) * TILE_SIZE,
            topLeft.y + (FarmTemplate.HEIGHT / 2f) * TILE_SIZE);
    }

    private void centerCameraOnPlayer() {
        if (inGreenhouse) {
            // Optionally clamp so you don't see outside edges (simple center follow is fine if interior fills)
            camera.position.set(playerPos.x, playerPos.y, 0);
            camera.update();
            return;
        }
        if (inVillage) {
            // Village - normal camera following
            camera.position.set(playerPos.x, playerPos.y, 0);
        } else {
            // Farm - constrain camera but allow viewing water border
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            float farmWidth = FarmTemplate.WIDTH * TILE_SIZE;
            float farmHeight = FarmTemplate.HEIGHT * TILE_SIZE;

            // Add buffer to show water beyond farm borders (5-10 tiles)
            float waterBuffer = 8 * TILE_SIZE; // Show 8 tiles of water beyond farm

            // Calculate camera bounds (half viewport size)
            float halfViewWidth = (Gdx.graphics.getWidth() * camera.zoom) / 2f;
            float halfViewHeight = (Gdx.graphics.getHeight() * camera.zoom) / 2f;

            // Calculate constrained camera position with water buffer
            float targetX = playerPos.x;
            float targetY = playerPos.y;

            // Constrain X within farm bounds + water buffer
            float minX = farmTopLeft.x - waterBuffer + halfViewWidth;
            float maxX = farmTopLeft.x + farmWidth + waterBuffer - halfViewWidth;
            targetX = Math.max(minX, Math.min(maxX, targetX));

            // Constrain Y within farm bounds + water buffer
            float minY = farmTopLeft.y - waterBuffer + halfViewHeight;
            float maxY = farmTopLeft.y + farmHeight + waterBuffer - halfViewHeight;
            targetY = Math.max(minY, Math.min(maxY, targetY));

            camera.position.set(targetX, targetY, 0);
        }
        camera.update();
    }

    public void setCurrentFarmIndex(int index) {
        if (index >= 0 && index <= 3) {
            currentFarmIndex = index;
            inVillage = false;
            gameMap.setActiveFarm(index);
            playerPos.set(getFarmCenter(index));
            centerCameraOnPlayer();
        }
    }

    private void renderPlayer() {
        float playerWidth = TILE_SIZE * 0.8f;
        float playerHeight = TILE_SIZE * 1.5f;
        float adjustedY = playerPos.y - (playerHeight - TILE_SIZE) * 0.5f;

        // Shadow
        float shadowWidth = TILE_SIZE * 0.8f;
        float shadowHeight = TILE_SIZE * 0.4f;
        float shadowX = playerPos.x + (playerWidth - shadowWidth) * 0.5f;
        float shadowY = playerPos.y - shadowHeight * 0.9f;
        batch.draw(shadowTexture, shadowX, shadowY, shadowWidth, shadowHeight);

        if (faintState == FaintState.PLAY_FAINT_ANIM) {
            Animation<TextureRegion> faintAnim = MapManager.getInstance().getFaintAnimation();
            if (faintAnim != null) {
                TextureRegion frame = faintAnim.getKeyFrame(faintAnimTime);
                if (frame != null) {
                    // Scale faint frames to same logical size as normal body
                    batch.draw(frame, playerPos.x, adjustedY, playerWidth, playerHeight);
                    return;
                }
            }
        } else if (faintState == FaintState.BARS_CLOSING || faintState == FaintState.DONE) {
            // Show last faint frame (eyes closed)
            Animation<TextureRegion> faintAnim = MapManager.getInstance().getFaintAnimation();
            if (faintAnim != null) {
                TextureRegion[] frames = faintAnim.getKeyFrames();
                TextureRegion last = frames[frames.length - 1];
                batch.draw(last, playerPos.x, adjustedY, playerWidth, playerHeight);
                return;
            }
        }

        // Normal rendering
        TextureRegion frame;
        if (toolManager.isUsingTool()) {
            frame = toolManager.getToolUseFrame(currentToolUseDirection);
        } else {
            frame = currentPlayerAnimation.getKeyFrame(animationTime);
        }
        batch.draw(frame, playerPos.x, adjustedY, playerWidth, playerHeight);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fishInitialized = false;
        handleInput(delta);
        updateAnimals(delta);
        updateFaint(delta);
        toolManager.updateAll(delta);
        centerCameraOnPlayer();
        updateBuildingSelectBoxItems(); // This is UI logic, can be here

        camera.update(); // Always update camera before using its combined matrix

        // --- PHASE 1: Draw all sprites/textures with SpriteBatch ---
        batch.setProjectionMatrix(camera.combined); // Set camera for world rendering
        batch.begin(); // BEGIN MAIN BATCH
        renderMap();
        renderAnimals();
        renderPlayer();
        renderLightning(delta);
        // Draw all text for progress bars while batch is active
        // If renderUI draws with the main batch (world-space UI), keep it here.
        // If it draws screen-space UI or uses its own batch/stage, it should be moved.
        renderUI();
        batch.end(); // END MAIN BATCH

        // --- PHASE 2: Draw all shapes/primitives with ShapeRenderer ---
        shapeRenderer.setProjectionMatrix(camera.combined); // Set camera for world rendering
        // Enable blending once for all transparent shape drawing
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // BEGIN MAIN SHAPE RENDERER
        // Draw the building placement highlight shape
        renderBuildingPlacementHighlightShapesOnly(); // This method now only draws shapes
        shapeRenderer.end(); // END MAIN SHAPE RENDERER
        if (faintState == FaintState.BARS_CLOSING || faintState == FaintState.DONE) {
            float progress = barsProgress;
            progress = Math.min(1f, Math.max(0f, progress));

            float viewW = camera.viewportWidth;
            float viewH = camera.viewportHeight;

            // Each bar height increases from 0 to half screen
            float maxHalf = viewH / 2f;
            float barHeight = maxHalf * progress;

            // We must map camera coordinates to world. Bars drawn in world projection to overlay properly.
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0f, 0f, 0f, 1f);
            float left = camera.position.x - viewW / 2f;
            float bottom = camera.position.y - viewH / 2f;
            // Top bar (from top downward)
            shapeRenderer.rect(left, bottom + viewH - barHeight, viewW, barHeight);
            // Bottom bar (from bottom upward)
            shapeRenderer.rect(left, bottom, viewW, barHeight);
            shapeRenderer.end();
        }
        Gdx.gl.glDisable(GL20.GL_BLEND); // Disable blending after all shapes are drawn
        drawNightOverlays(delta);
        // --- PHASE 3: Draw UI Stage last ---
        stage.act(delta);
        stage.draw();

        // This line is usually not needed or problematic if stage.draw() handles its batch correctly.
        // if (stage.getBatch() != null) {
        //     stage.getBatch().setColor(Color.WHITE);
        // }
    }
    private void renderGreenhouseInterior() {
        // Draw static interior full image
        Texture interior = greenhouseManager.getInteriorTexture();
        float w = GreenhouseManager.WIDTH * GREENHOUSE_TILE_SIZE;
        float h = GreenhouseManager.HEIGHT * GREENHOUSE_TILE_SIZE;
        if (interior != null) {
            batch.draw(interior, 0, 0, w, h);
        }

        // Draw farming overlays (plowed / watered / crops)
        for (int y = 0; y < GreenhouseManager.HEIGHT; y++) {
            for (int x = 0; x < GreenhouseManager.WIDTH; x++) {
                Tile t = greenhouseManager.getTile(x, y);
                if (t == null) continue;
                float wx = x * GREENHOUSE_TILE_SIZE;
                float wy = y * GREENHOUSE_TILE_SIZE;

                if (t.isPlowed()) {
                    Texture ground = t.isWatered()
                        ? mapManager.getWateredGroundTexture()
                        : mapManager.getPlowedGroundTexture();
                    if (ground != null) {
                        batch.draw(ground, wx, wy, GREENHOUSE_TILE_SIZE, GREENHOUSE_TILE_SIZE);
                    }
                }

                if (t.getPlantedSeed() != null) {
                    String cropName = t.getPlantedSeed().getGrowsInto();
                    int day = Math.max(1, t.getDaysGrown());
                    Texture stageTex = CropManager.getInstance()
                        .getStageTextureForDay(cropName, day);
                    if (stageTex != null) {
                        batch.draw(stageTex, wx, wy, GREENHOUSE_TILE_SIZE, GREENHOUSE_TILE_SIZE);
                    }
                }
            }
        }
    }
    private void renderMap() {
        int width, height;
        List<Vector2> npcChatIconPositions = new ArrayList<>();
        List<TreeRenderData> treesToRender = new ArrayList<>();
        List<StructureRenderData> structuresToRender = new ArrayList<>();
        if (inGreenhouse) {
            renderGreenhouseInterior();
            return;
        }
        if (inVillage) {
            width = 20;
            height = 20;
        } else {
            width = FarmTemplate.WIDTH;
            height = FarmTemplate.HEIGHT;
        }

        renderOffset = inVillage ? new Vector2(0, 0) : getFarmTopLeft(currentFarmIndex);
        int waterBorderSize = 100;

        // Water background
        for (int y = -waterBorderSize; y < height + waterBorderSize; y++) {
            for (int x = -waterBorderSize; x < width + waterBorderSize; x++) {
                float posX = renderOffset.x + (x * TILE_SIZE);
                float posY = renderOffset.y + (y * TILE_SIZE);
                batch.draw(mapManager.getWaterTexture(), posX, posY, TILE_SIZE, TILE_SIZE);
            }
        }
        Texture seasonFloor = mapManager.getSeasonFloorTexture(TimeSystem.getInstance().getCurrentSeason());
        // Giant crop aggregation
        class GiantRender {
            final float x, y;
            final Texture tex;
            GiantRender(float x, float y, Texture tex) { this.x = x; this.y = y; this.tex = tex; }
        }
        boolean[][] giantPart = new boolean[width][height];
        List<GiantRender> giantRenders = new ArrayList<>();

        // Main tile pass
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int finalX = x;
                final int finalY = y;

                float posX = renderOffset.x + (x * TILE_SIZE);
                float posY = renderOffset.y + (y * TILE_SIZE);

                // Base ground
                batch.draw(seasonFloor, posX, posY, TILE_SIZE, TILE_SIZE);

                Tile tile = inVillage
                    ? gameMap.getVillage().getTile(x, 20 - 1 - y)
                    : gameMap.getFarm(currentFarmIndex).getTile(x, FarmTemplate.HEIGHT - 1 - y);

                if (tile == null) continue;

                // Plowed / watered overlay
                if (tile.isPlowed()) {
                    batch.draw(tile.isWatered()
                            ? mapManager.getWateredGroundTexture()
                            : mapManager.getPlowedGroundTexture(),
                        posX, posY, TILE_SIZE, TILE_SIZE);
                }

                // Giant detection (farms only)
                if (!inVillage && tile.isGiantCrop() && !giantPart[x][y]) {
                    Tile tRight = (x + 1 < width) ? gameMap.getFarm(currentFarmIndex).getTile(x + 1, FarmTemplate.HEIGHT - 1 - y) : null;
                    Tile tUp = (y + 1 < height) ? gameMap.getFarm(currentFarmIndex).getTile(x, FarmTemplate.HEIGHT - 1 - (y + 1)) : null;
                    Tile tUpRight = (x + 1 < width && y + 1 < height)
                        ? gameMap.getFarm(currentFarmIndex).getTile(x + 1, FarmTemplate.HEIGHT - 1 - (y + 1))
                        : null;

                    boolean cluster = tRight != null && tUp != null && tUpRight != null
                        && tRight.isGiantCrop() && tUp.isGiantCrop() && tUpRight.isGiantCrop();

                    if (cluster) {
                        giantPart[x][y] = true;
                        giantPart[x + 1][y] = true;
                        giantPart[x][y + 1] = true;
                        giantPart[x + 1][y + 1] = true;

                        Seeds s = tile.getPlantedSeed();
                        if (s != null) {
                            Texture gTex = CropManager.getInstance().getGiantCropTexture(s.getGrowsInto());
                            if (gTex != null) {
                                giantRenders.add(new GiantRender(posX, posY, gTex));
                            } else if (DEBUG_GIANT) {
                                Gdx.app.log("GIANT", "Missing texture for " + s.getGrowsInto());
                            }
                        }
                    } else {
                        // Mark anyway to suppress normal crop draw
                        giantPart[x][y] = true;
                        if (DEBUG_GIANT) {
                            Gdx.app.log("GIANT", "Non-cluster giant tile at ("+x+","+y+")");
                        }
                    }
                } else if (!inVillage && tile.isGiantCrop()) {
                    giantPart[x][y] = true;
                }

                // Normal crop (skip if part of a giant)
                if (!giantPart[x][y] && tile.getPlantedSeed() != null) {
                    Seeds planted = tile.getPlantedSeed();
                    String cropName = planted.getGrowsInto();
                    if (tile.shouldShowMultiHarvestBase()) {
                        Texture base = CropManager.getInstance().getMultiHarvestBaseTexture(cropName);
                        if (base != null) {
                            batch.draw(base, posX, posY, TILE_SIZE, TILE_SIZE);
                        } else {
                            batch.setColor(Color.SCARLET);
                            batch.draw(mapManager.getPlaceholderTile(), posX, posY, TILE_SIZE, TILE_SIZE);
                            batch.setColor(Color.WHITE);
                        }
                    } else {
                        int day = Math.max(1, tile.getDaysGrown());
                        Texture stageTex = CropManager.getInstance().getStageTextureForDay(cropName, day);
                        if (stageTex != null) {
                            batch.draw(stageTex, posX, posY, TILE_SIZE, TILE_SIZE);
                        } else {
                            batch.setColor(Color.FOREST);
                            batch.draw(mapManager.getPlaceholderTile(), posX, posY, TILE_SIZE, TILE_SIZE);
                            batch.setColor(Color.WHITE);
                        }
                    }
                }
                if (!inVillage && currentPlayerController != null) {
                    List<int[]> crowEvents = currentPlayerController.getCrowAttackEvents();
                    if (!crowEvents.isEmpty()) {
                        Texture crowTex = CropManager.getInstance().getCrowTexture();
                        if (crowTex != null) {
                            for (int[] pos : crowEvents) {
                                int internalX = pos[0];
                                int internalY = pos[1];
                                // Convert internal Y (array index) to render loop Y:
                                int renderY = FarmTemplate.HEIGHT - 1 - internalY;
                                // Ensure still inside bounds
                                if (internalX >= 0 && internalX < FarmTemplate.WIDTH &&
                                    renderY >= 0 && renderY < FarmTemplate.HEIGHT) {
                                    float drawX = renderOffset.x + internalX * TILE_SIZE;
                                    float drawY = renderOffset.y + renderY * TILE_SIZE;
                                    batch.draw(crowTex, drawX, drawY, TILE_SIZE, TILE_SIZE);
                                }
                            }
                        }
                    }
                }
                // Static elements
                tile.getStaticElement().ifPresent(element -> {
                    if (element instanceof Cabin) {
                        if (isStructureOrigin(finalX, finalY, element, width, height)) {
                            structuresToRender.add(new StructureRenderData(
                                mapManager.getCabinTexture(), posX, posY, 4, 4));
                        }
                    }
                    else if (element instanceof Greenhouse) {
                        if (isStructureOrigin(finalX, finalY, element, width, height)) {
                            Greenhouse gh = (Greenhouse) element;
                            Texture ghTex = gh.isRepaired()
                                ? mapManager.getGreenhouseTexture()
                                : mapManager.getGreenhouseBrokenTexture();
                            structuresToRender.add(new StructureRenderData(
                                ghTex, posX, posY, gh.getWidth(), gh.getHeight()
                            ));
                        }
                    }
                    else if (element instanceof Lake) {
                        batch.draw(mapManager.getWaterTexture(), posX, posY, TILE_SIZE, TILE_SIZE);
                        if (!fishInitialized) {
                            batch.draw(fishTexture, posX, posY, TILE_SIZE, TILE_SIZE);
                            fishInitialized = true;
                        }
                    } else if (element instanceof Quarry) {
                        batch.draw(mapManager.getQuarryTexture(), posX, posY, TILE_SIZE, TILE_SIZE);
                    } else if (element instanceof CoopStaticElement) {
                        if (isStructureOrigin(finalX, finalY, element, width, height)) {
                            structuresToRender.add(new StructureRenderData(
                                mapManager.getCoopTexture(), posX, posY, 6, 3));
                        }
                    } else if (element instanceof BarnStaticElement) {
                        if (isStructureOrigin(finalX, finalY, element, width, height)) {
                            structuresToRender.add(new StructureRenderData(
                                mapManager.getBarnTexture(), posX, posY, 7, 4));
                        }
                    } else if (element instanceof Npc) {
                        Npc npc = (Npc) element;
                        Texture t = mapManager.getNpcTexture(npc.getName());
                        if (t != null) {
                            batch.draw(t, posX, posY, TILE_SIZE, TILE_SIZE);
                            if (npc.isDialogueReady()) {
                                npcChatIconPositions.add(new Vector2(posX, posY));
                            }
                        }
                    } else if (element instanceof Store) {
                        Texture t = mapManager.getStoreTexture();
                        if (t != null) batch.draw(t, posX, posY, TILE_SIZE, TILE_SIZE);
                    } else if (element instanceof PlaceableGameBuilding) {
                        PlaceableGameBuilding bld = (PlaceableGameBuilding) element;
                        Texture t = mapManager.getBuildingTexture(bld.getName());
                        if (t != null) {
                            batch.draw(t, posX, posY,
                                bld.getWidth() * TILE_SIZE,
                                bld.getHeight() * TILE_SIZE);
                        } else {
                            batch.setColor(Color.MAGENTA);
                            batch.draw(mapManager.getPlaceholderTile(), posX, posY, TILE_SIZE, TILE_SIZE);
                            batch.setColor(Color.WHITE);
                        }
                    } else if (element instanceof SellingBin) {
                        Texture t = mapManager.getSellingBinTexture();
                        if (t != null) batch.draw(t, posX, posY, TILE_SIZE, TILE_SIZE);
                    } else {
                        batch.setColor(Color.BROWN);
                        batch.draw(mapManager.getPlaceholderTile(), posX, posY, TILE_SIZE, TILE_SIZE);
                        batch.setColor(Color.WHITE);
                    }
                });

                // Random elements
                tile.getRandomElement().ifPresent(element -> {
                    if (element instanceof Stone) {
                        batch.draw(mapManager.getStoneTile(((Stone) element).getStoneVariant()), posX, posY, TILE_SIZE, TILE_SIZE);
                    } else if (element instanceof Tree) {
                        Texture tt = mapManager.getTreeTexture(((Tree) element).getImagePath());
                        if (tt != null) treesToRender.add(new TreeRenderData(tt, posX, posY, false));
                    } else if (element instanceof ForagingTree) {
                        Texture tt = mapManager.getForagingTreeTexture(((ForagingTree) element).getImagePath());
                        if (tt != null) treesToRender.add(new TreeRenderData(tt, posX, posY, true));
                    } else if (element instanceof ForagingMineral) {
                        ForagingMineral m = (ForagingMineral) element;
                        Texture tt = mapManager.getForagingMineralTexture(m.getImagePath());
                        float size = TILE_SIZE * 0.6f;
                        float ox = (TILE_SIZE - size) / 2f;
                        float oy = (TILE_SIZE - size) / 2f;
                        batch.draw(tt, posX + ox, posY + oy, size, size);
                    } else if (element instanceof ForagingCrop) {
                        ForagingCrop c = (ForagingCrop) element;
                        Texture tt = mapManager.getForagingCropTexture(c.getImagePath());
                        float size = TILE_SIZE * 0.7f;
                        float ox = (TILE_SIZE - size) / 2f;
                        float oy = (TILE_SIZE - size) / 2f;
                        batch.draw(tt, posX + ox, posY + oy, size, size);
                    }
                });
            }
        }

        // DRAW GIANT CROPS (after ground & tile elements, before structures & trees for layering)
        for (GiantRender gr : giantRenders) {
            batch.draw(gr.tex, gr.x, gr.y, TILE_SIZE * 2f, TILE_SIZE * 2f);
        }

        // Structures
        for (StructureRenderData s : structuresToRender) {
            renderStructure(s.texture, s.posX, s.posY, s.width, s.height);
        }

        // Trees
        for (TreeRenderData tree : treesToRender) {
            float treeWidth = TILE_SIZE * 1.8f;
            float treeHeight = TILE_SIZE * 2.5f;
            float treeX = tree.posX - (treeWidth - TILE_SIZE) / 2f;
            batch.draw(tree.texture, treeX, tree.posY, treeWidth, treeHeight);
        }

        // NPC chat icons
        for (Vector2 pos : npcChatIconPositions) {
            batch.draw(mapManager.getChatIconTexture(),
                pos.x + TILE_SIZE / 4f,
                pos.y + TILE_SIZE,
                TILE_SIZE / 2f,
                TILE_SIZE / 2f);
        }

        // Build mode highlights (unchanged)
        if (gamePlayController != null && gameMap != null &&
            gamePlayController.getCurrentGameState() == GamePlayController.GameState.BUILD_MODE) {

            StaticElement selectedBlueprint = gamePlayController.getSelectedBuildingBlueprint();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Tile tile = inVillage
                        ? gameMap.getVillage().getTile(x, 20 - 1 - y)
                        : gameMap.getFarm(currentFarmIndex).getTile(x, FarmTemplate.HEIGHT - 1 - y);
                    if (tile != null && tile.isAvailableForBuilding()) {
                        float hx = renderOffset.x + x * TILE_SIZE;
                        float hy = renderOffset.y + y * TILE_SIZE;
                        batch.setColor(new Color(0f, 1f, 0f, 0.4f));
                        batch.draw(mapManager.getTexture("pixel_white"), hx, hy, TILE_SIZE, TILE_SIZE);
                        batch.setColor(Color.WHITE);
                    }
                }
            }

            if (selectedBlueprint != null) {
                Vector3 world = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                float gx = world.x - renderOffset.x;
                float gy = world.y - renderOffset.y;
                int tx = (int)(gx / TILE_SIZE);
                int ty = (int)(gy / TILE_SIZE);
                float drawX = renderOffset.x + tx * TILE_SIZE;
                float drawY = renderOffset.y + ty * TILE_SIZE;
                Texture blueprintTexture = null;
                if (selectedBlueprint instanceof Cabin) blueprintTexture = mapManager.getCabinTexture();
                else if (selectedBlueprint instanceof Greenhouse) blueprintTexture = mapManager.getGreenhouseTexture();
                if (blueprintTexture != null) {
                    batch.setColor(new Color(1f,1f,1f,0.6f));
                    batch.draw(blueprintTexture, drawX, drawY, TILE_SIZE, TILE_SIZE);
                    batch.setColor(Color.WHITE);
                }
            }
        }
    }

    private boolean isStructureOrigin(int x, int y, Object element, int mapWidth, int mapHeight) {
        if (element instanceof Cabin) {
            return isTopLeftOfStructure(x, y, 4, 4, mapWidth, mapHeight, Cabin.class);
        } else if (element instanceof Greenhouse) {
            return isTopLeftOfStructure(x, y, 5, 6, mapWidth, mapHeight, Greenhouse.class);
        } else if (element instanceof CoopStaticElement) {
            return isTopLeftOfStructure(x, y, 6, 3, mapWidth, mapHeight, CoopStaticElement.class);
        } else if (element instanceof BarnStaticElement) {
            return isTopLeftOfStructure(x, y, 7, 4, mapWidth, mapHeight, BarnStaticElement.class);
        }
        return false;
    }

    private boolean isTopLeftOfStructure(int x, int y, int structWidth, int structHeight,
                                         int mapWidth, int mapHeight, Class<?> structureType) {
        for (int dy = 0; dy < structHeight; dy++) {
            for (int dx = 0; dx < structWidth; dx++) {
                int checkX = x + dx;
                int checkY = y + dy;
                if (checkX >= mapWidth || checkY >= mapHeight) return false;
                Tile checkTile;
                if (inVillage) {
                    checkTile = gameMap.getVillage().getTile(checkX, 20 - 1 - checkY);
                } else {
                    checkTile = gameMap.getFarm(currentFarmIndex).getTile(checkX, FarmTemplate.HEIGHT - 1 - checkY);
                }
                if (checkTile == null || !checkTile.getStaticElement().isPresent() ||
                    !structureType.isInstance(checkTile.getStaticElement().get())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static class StructureRenderData {
        final Texture texture;
        final float posX;
        final float posY;
        final int width;
        final int height;

        StructureRenderData(Texture texture, float posX, float posY, int width, int height) {
            this.texture = texture;
            this.posX = posX;
            this.posY = posY;
            this.width = width;
            this.height = height;
        }
    }

    private void renderStructure(Texture texture, float startX, float startY, int width, int height) {
        float structureWidth = width * TILE_SIZE;
        float structureHeight = height * TILE_SIZE;
        batch.draw(texture, startX, startY, structureWidth, structureHeight);
    }

    private static class TreeRenderData {
        final Texture texture;
        final float posX;
        final float posY;
        final boolean isForagingTree;

        TreeRenderData(Texture texture, float posX, float posY, boolean isForagingTree) {
            this.texture = texture;
            this.posX = posX;
            this.posY = posY;
            this.isForagingTree = isForagingTree;
        }
    }

    private boolean isMyTurn() {
        return true; // Simplified for now
    }

    private void renderUI() {
        drawClock();
        drawEnergyStats();
        drawBottomLeftMessage();
        renderQuickAccessToolbar();
    }
    private void drawEnergyStats() {
        User player = gameInstance.getCurrentPlayer();
        if (player == null || player.getEnergy() == null) return;

        // Recompute the same anchor used in drawClock
        float padX = 40f;
        float padY = 0f;
        Texture frame = mapManager.getSeasonClockFrame(TimeSystem.getInstance().getCurrentSeason());
        if (frame == null) return;

        float fw = frame.getWidth() * CLOCK_SCALE;
        float fh = frame.getHeight() * CLOCK_SCALE;

        float topLeftX = camera.position.x - (Gdx.graphics.getWidth() / 2f) * camera.zoom + padX;
        float topLeftY = camera.position.y + (Gdx.graphics.getHeight() / 2f) * camera.zoom - padY;
        float clockX = topLeftX;
        float clockY = topLeftY - fh;

        int cur = player.getEnergy().getCurrentEnergy();
        int max = player.getEnergy().getMaxEnergy();
        String energyLine = "Energy: " + cur + "/" + max + " (Used: " +
            String.format("%.1f", energyUsedThisTurn) + "/" + ENERGY_LIMIT_PER_TURN + ")";

        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, energyLine);

        float yOffsetBelowFrame = 8f;   // distance below the frame
        float x = clockX + (fw - layout.width) / 2f;
        float y = clockY - yOffsetBelowFrame; // baseline just below frame

        font.setColor(Color.YELLOW);
        font.draw(batch, layout, x, y);
        font.setColor(Color.WHITE);
    }

    private void renderQuickAccessToolbar() {
        User currentPlayer = gameInstance.getCurrentPlayer();
        Item[] quickSlots = currentPlayer.getInventory().getQuickAccessSlots();

        float toolbarWidth = 6 * 60f;
        float startX = camera.position.x - toolbarWidth / 2f;
        float startY = camera.position.y - Gdx.graphics.getHeight() / 2f * camera.zoom + 30;

        for (int i = 0; i < 6; i++) {
            float slotX = startX + (i * 60f);
            float slotY = startY;

            // Draw Panel.png as slot background
            if (panelTexture != null) {
                if (i == selectedQuickSlot) {
                    batch.setColor(Color.YELLOW);
                } else {
                    batch.setColor(Color.LIGHT_GRAY);
                }
                batch.draw(panelTexture, slotX, slotY, 55f, 55f);
                batch.setColor(Color.WHITE);
            }

            // Draw item if present
            Item item = quickSlots[i];
            if (item != null) {
                // ---- FARMING ADDITION: render seed or generic item texture if available ----
                if (item instanceof Tools tool) {
                    Texture toolTexture = toolManager.getToolTexture(tool);
                    if (toolTexture != null) {
                        batch.draw(toolTexture, slotX + 5, slotY + 5, 45f, 45f);
                    } else {
                        font.setColor(Color.GREEN);
                        font.draw(batch, tool.getName().substring(0, Math.min(3, tool.getName().length())),
                            slotX + 10, slotY + 35);
                        font.setColor(Color.WHITE);
                    }
                } else if (item instanceof Seeds seeds) {
                    // Use CropManager seed textures (seed packet icon)
                    Texture seedTex = CropManager.getInstance().getSeedTexture(seeds);
                    if (seedTex != null) {
                        batch.draw(seedTex, slotX + 5, slotY + 5, 45f, 45f);
                    } else {
                        font.setColor(Color.GREEN);
                        font.draw(batch, seeds.getName().substring(0, Math.min(6, seeds.getName().length())),
                            slotX + 5, slotY + 35);
                        font.setColor(Color.WHITE);
                    }
                } else {
                    // Generic item with path
                    Texture tex = getOrLoadQuickItemTexture(item.getPath());
                    if (tex != null) {
                        batch.draw(tex, slotX + 5, slotY + 5, 45f, 45f);
                    } else {
                        font.setColor(Color.GREEN);
                        font.draw(batch, item.getName().substring(0, Math.min(6, item.getName().length())),
                            slotX + 5, slotY + 35);
                        font.setColor(Color.WHITE);
                    }
                }
            }

            font.setColor(Color.CYAN);
            font.draw(batch, String.valueOf(i + 1), slotX + 2, slotY + 52);
            font.setColor(Color.WHITE);
        }
    }
    private Texture getOrLoadQuickItemTexture(String path) {
        if (path == null || path.isEmpty()) return null;
        Texture cached = quickItemTextureCache.get(path);
        if (cached != null) return cached;
        if (Gdx.files.internal(path).exists()) {
            Texture t = new Texture(Gdx.files.internal(path));
            quickItemTextureCache.put(path, t);
            return t;
        }
        return null;
    }
    private void handleInput(float delta) {
        if (inGreenhouse && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            // Are we at door tile?
            int tx = (int)(playerPos.x / GREENHOUSE_TILE_SIZE);
            int ty = (int)(playerPos.y / GREENHOUSE_TILE_SIZE);
            if (ty <= 1) { // near bottom
                exitGreenhouse();
                return;
            }
        }
        if (isFaintingActive()) {
            // Still allow camera to center each frame, but block movement & actions
            // We still call handleGameplayMechanics internally for pose freeze if desired.
            // Prevent double-triggering start; gameplay mechanics call will not re-trigger because state != NONE.
            handleGameplayMechanics(delta); // optional; or skip to freeze animation
            return;
        }
        if (speechIsShowing) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) speechIsShowing = false;
            return;
        }
        if (uiBlockedByDialog) {
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
            lightningTargetMode = !lightningTargetMode;
            showMessage("Lightning mode: " + (lightningTargetMode ? "Click a tile" : "Off"), 2f);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F7)) {
            nightEnabled = !nightEnabled;
            showMessage("Night overlay: " + (nightEnabled ? "ON" : "OFF"), 1.5f);
        }
        // ADDED: F10 toggle
        if (Gdx.input.isKeyJustPressed(Input.Keys.F10)) {
            clickDebugMode = !clickDebugMode;
            showMessage("ClickDebug: " + (clickDebugMode ? "ON" : "OFF"), 2f);
            clickDebugSampleIndex = 0;
        }

        // ADDED: capture click first (before other left click logic)
        if (clickDebugMode && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 world = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(world);

            int screenX = Gdx.input.getX();
            int screenY = Gdx.graphics.getHeight() - Gdx.input.getY();

            float viewLeft = camera.position.x - (Gdx.graphics.getWidth() / 2f) * camera.zoom;
            float viewTop  = camera.position.y + (Gdx.graphics.getHeight() / 2f) * camera.zoom;

            float relWorldX = world.x - viewLeft;
            float relWorldYFromTop = viewTop - world.y;

            Texture clockFrame = mapManager.getClockMainTexture();
            float clockFrameX = viewLeft + 10f;
            float clockFrameY = viewTop - 10f - (clockFrame != null ? clockFrame.getHeight() : 0f);

            float relClockX = (clockFrame != null) ? (world.x - clockFrameX) : -1f;
            float relClockY = (clockFrame != null) ? (world.y - clockFrameY) : -1f;

            clickDebugSampleIndex++;
            System.out.println("[ClickDebug #" + clickDebugSampleIndex + "] "
                + "Screen=(" + screenX + "," + screenY + ") "
                + "World=(" + (int)world.x + "," + (int)world.y + ") "
                + "RelView=(" + (int)relWorldX + ", down " + (int)relWorldYFromTop + ") "
                + (clockFrame != null
                ? ("RelClock=(" + (int)relClockX + "," + (int)relClockY + ")")
                : "(NoClockFrame)")
            );
            showMessage("Logged click #" + clickDebugSampleIndex, 1.2f);
            // (Do NOT return; let normal click logic continue if you want interaction at the same time)
        }
        User currentPlayer = Game.getInstance().getCurrentPlayer();
        handleGameplayMechanics(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            openFarmingDialog();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            TimeSystem.getInstance().advanceSeason();
            showMessage("Season changed to " + TimeSystem.getInstance().getCurrentSeason() +
                " (Year " + TimeSystem.getInstance().getCurrentYear() + ")", 3f);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F8)) {
            craftInfoMode = !craftInfoMode;
            showMessage(craftInfoMode ? "Craft Info: Click a crop/tree/forage to inspect" : "Craft Info: Off", 2f);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F9)) {
            TimeSystem.getInstance().advanceDate(1);

            // Per-player controller daily init
            for (GamePlayController controller : playerControllers.values()) {
                controller.initializeNextDay();
            }

            // Update all farms (exactly 4 as per Game.initializeGameMap())
            for (int fi = 0; fi < 4; fi++) {
                Farm farm = gameMap.getFarm(fi);
                if (farm != null) {
                    farm.updateDaily();
                }
            }
            greenhouseManager.advanceDaily();
            showMessage("A new day begins. Crops advanced.", 3f);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            if (isMyTurn()) {
                playerPositions.put(currentPlayer, new Vector2(playerPos));
                playerInVillageState.put(currentPlayer, inVillage);
                if (inVillage) {
                    currentPlayer.setPosition(new Tile((int) (playerPos.x / TILE_SIZE), (int) (playerPos.y / TILE_SIZE)));
                } else {
                    Vector2 farmOffset = getFarmTopLeft(currentFarmIndex);
                    int localX = (int) ((playerPos.x - farmOffset.x) / TILE_SIZE);
                    int localY = (int) ((playerPos.y - farmOffset.y) / TILE_SIZE);
                    currentPlayer.setPosition(new Tile(localX, localY));
                }
                currentPlayer.isInVillage = inVillage;
                User previousPlayer = gameInstance.getCurrentPlayer();
                gameInstance.nextTurn();
                User newCurrentPlayer = gameInstance.getCurrentPlayer();
                boolean completedRound = false;
                List<User> players = gameInstance.getPlayers();
                if (players.indexOf(newCurrentPlayer) == 0 && !previousPlayer.equals(newCurrentPlayer)) {
                    completedRound = true;
                }

                StringBuilder notificationMessage = new StringBuilder();
                List<String> giftNotifications = newCurrentPlayer.getUnreadGiftNotificationsAndClear();
                for (String notification : giftNotifications) {
                    notificationMessage.append(notification).append("\n");
                }

                List<String> messageNotifications = newCurrentPlayer.getUnreadMessagesAndClear();
                for (String notification : messageNotifications) {
                    notificationMessage.append(notification).append("\n");
                }

                if (notificationMessage.length() > 0) {
                    lastTurnMessage = "Now playing as: " + newCurrentPlayer.getUsername() + "\n" + notificationMessage.toString().trim();
                } else {
                    lastTurnMessage = "Now playing as: " + newCurrentPlayer.getUsername();
                }

                if (completedRound) {
                    TimeSystem.getInstance().advanceTime(1);
                    if (TimeSystem.getInstance().getCurrentHour() >= 12) {
                        for (GamePlayController controller : playerControllers.values()) {
                        }
                    }
                }
                currentPlayerController = playerControllers.get(newCurrentPlayer);
                currentFarmIndex = gameInstance.getSelectedMaps().get(newCurrentPlayer) - 1;
                playerPos = new Vector2(playerPositions.get(newCurrentPlayer));
                inVillage = playerInVillageState.get(newCurrentPlayer);
                centerCameraOnPlayer();
                messageTimer = MESSAGE_DISPLAY_TIME;
                energyUsedThisTurn = 0;
                lastEnergyTile.set(-1, -1);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            int playerTileX, playerTileY;
            if (inVillage) {
                playerTileX = (int) (playerPos.x / TILE_SIZE);
                playerTileY = (int) (playerPos.y / TILE_SIZE);
            } else {
                Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
                playerTileX = (int) ((playerPos.x - farmTopLeft.x) / TILE_SIZE);
                playerTileY = (int) ((playerPos.y - farmTopLeft.y) / TILE_SIZE);
            }

            // Check adjacent tiles for a Cabin
            boolean nextToCabin = false;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    int checkX = playerTileX + dx;
                    int checkY = playerTileY + dy;

                    Tile adjacentTile;
                    if (inVillage) {
                        adjacentTile = gameMap.getVillage().getTile(checkX, 20 - 1 - checkY);
                    } else {
                        adjacentTile = gameMap.getFarm(currentFarmIndex).getTile(checkX, FarmTemplate.HEIGHT - 1 - checkY);
                    }

                    if (adjacentTile != null && adjacentTile.getStaticElement().isPresent() && adjacentTile.getStaticElement().get() instanceof Cabin) {
                        nextToCabin = true;
                        break;
                    }
                }
                if (nextToCabin) break;
            }

            if (nextToCabin) {
                gameView.showKitchenView();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            if (!currentPlayer.isInVillage) { // Can only place on farm
                GamePlayController controller = playerControllers.get(currentPlayer);

                if (controller.getAnimalPlaces().isEmpty()) {
                    lastTurnMessage = "You have no buildings in your inventory to place.";
                } else {
                    // 1. Get graphical tile coordinates
                    Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
                    int graphicalTileX = (int) ((playerPos.x - farmTopLeft.x) / TILE_SIZE);
                    int graphicalTileY = (int) ((playerPos.y - farmTopLeft.y) / TILE_SIZE);

                    // 2. Convert to model's coordinate system (Y is flipped)
                    int modelY = FarmTemplate.HEIGHT - 1 - graphicalTileY;

                    // 3. Set the player's logical position correctly in the model
                    // Note: Farm.getTile uses (x, y) which maps to tiles[y][x]
                    Tile targetTile = gameMap.getFarm(currentFarmIndex).getTile(graphicalTileX, graphicalTileY);
                    if (targetTile != null) {
                        currentPlayer.setPosition(targetTile);
                        String result = controller.placeFirstAvailableBuilding();
                        lastTurnMessage = result;
                    } else {
                        lastTurnMessage = "Cannot place building outside of farm bounds.";
                    }
                }
            } else {
                lastTurnMessage = "You can only place buildings on your farm.";
            }
            messageTimer = MESSAGE_DISPLAY_TIME;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            boolean isUnlimited = !currentPlayer.getEnergy().isUnlimited();
            currentPlayer.getEnergy().setUnlimited(isUnlimited);
            if (isUnlimited) {
                lastTurnMessage = "Infinite energy activated!";
                currentPlayer.getEnergy().setCurrentEnergy(currentPlayer.getEnergy().getMaxEnergy());
            } else {
                lastTurnMessage = "Infinite energy deactivated!";
            }
            messageTimer = MESSAGE_DISPLAY_TIME;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            captureCurrentFrame(); // Take screenshot
            gameView.setLastFrameTexture(lastFrameTexture); // Pass to GameView
            gameView.showInventoryScreen();
            return;// Use existing method
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            Item coal = new Item("Coal", 5);
            currentPlayer.getInventory().addItem(coal);
            currentPlayer.getInventory().addItem(new Item("Rice",5));
            HomeController.unlockRecipesByLevel("mining", 1);
            HomeController.unlockRecipesByLevel("farming", 1);
            HomeController.unlockRecipesByLevel("foraging", 1);
            gameView.showCraftingMenu(currentPlayer);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            currentPlayer.getInventory().addItem(new Item("Keg", 10));
            if (gamePlayController.isInBuildMode()) {
                gamePlayController.exitBuildMode();
            } else {
                if (selectedBuildingType == null) {
                    gamePlayController.exitBuildMode();
                } else {
                    gamePlayController.enterBuildMode(gamePlayController.getBuildingDefinition(selectedBuildingType)); // Example blueprint
                }
            }
        }
        // Inside your handleInput(float delta) method:

        if (gamePlayController.isInBuildMode() && Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos); // Converts screen coords to world coords

            // --- NEW: Get the current farm's render offset ---
            Vector2 renderOffset = inVillage ? new Vector2(0, 0) : getFarmTopLeft(currentFarmIndex);

            // --- NEW: Adjust touchPos relative to the farm's origin ---
            float adjustedTouchX = touchPos.x - renderOffset.x;
            float adjustedTouchY = touchPos.y - renderOffset.y;

            // Calculate tile coordinates based on adjusted position
            int tileX = (int) (adjustedTouchX / TILE_SIZE);
            int tileY = (int) (adjustedTouchY / TILE_SIZE); // This is the Y from the bottom of the farm's local coords

            // Invert tileY for array access (if array row 0 is at the top of the map)
            int finalTileY;
            // Check if the calculated tileY is within the positive range of map height
            if (tileY >= 0 && tileY < FarmTemplate.HEIGHT) {
                finalTileY = FarmTemplate.HEIGHT - 1 - tileY;
            } else {
                // If it's outside this range, it's genuinely out of bounds relative to the farm map
                finalTileY = -1; // Or throw an error/log a message in attemptToPlaceBuilding
            }
            // Pass the calculated tileX and finalTileY to the controller
            gamePlayController.attemptToPlaceBuilding(tileX, finalTileY);

            return; // Consume the right-click event
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);
            if (lightningTargetMode) {
                castLightningAtClick();
                return; // do not process normal left-click logic
            }
            if (craftInfoMode) {
                if (tryShowCraftInfoAt(clickPos)) {
                    // After one inspection, keep mode on or turn it off based on preference; here we keep it on
                } else {
                    showMessage("Nothing inspectable here", 1.2f);
                }
                return;
            }
            if (handleToolUsage(clickPos)) {
                return;
            }

            int clickedTileX, playerTileX, clickedTileY, playerTileY;

            if (handleAnimalClick(clickPos)) {
                return; // اگر روی حیوان کلیک شد، ادامه نده
            }

            // بررسی کلیک روی ساختمان‌ها
            if (handleBuildingClick(clickPos)) {
                return;
            }

            if (inVillage) {
                clickedTileX = (int) (clickPos.x / TILE_SIZE);
                clickedTileY = (int) (clickPos.y / TILE_SIZE);
                playerTileX = (int) (playerPos.x / TILE_SIZE);
                playerTileY = (int) (playerPos.y / TILE_SIZE);
            } else {
                Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
                clickedTileX = (int) ((clickPos.x - farmTopLeft.x) / TILE_SIZE);
                clickedTileY = (int) ((clickPos.y - farmTopLeft.y) / TILE_SIZE);
                playerTileX = (int) ((playerPos.x - farmTopLeft.x) / TILE_SIZE);
                playerTileY = (int) ((playerPos.y - farmTopLeft.y) / TILE_SIZE);
            }

            Tile clickedTile;
            if (inVillage) {
                if (clickedTileX >= 0 && clickedTileX < 20 && clickedTileY >= 0 && clickedTileY < 20) {
                    clickedTile = gameMap.getVillage().getTile(clickedTileX, 20 - 1 - clickedTileY);
                } else {
                    clickedTile = null;
                }
            } else {
                if (clickedTileX >= 0 && clickedTileX < FarmTemplate.WIDTH &&
                    clickedTileY >= 0 && clickedTileY < FarmTemplate.HEIGHT) {
                    clickedTile = gameMap.getFarm(currentFarmIndex).getTile(clickedTileX, FarmTemplate.HEIGHT - 1 - clickedTileY);
                } else {
                    clickedTile = null;
                }
            }

            if (clickedTile != null) {
                Item[] qs = currentPlayer.getInventory().getQuickAccessSlots();
                Item selected = qs[selectedQuickSlot];
                if (selected instanceof Seeds seed) {
                    if (inVillage) {
                        showMessage("Cannot plant seeds in the village.", 2f);
                        return;
                    }

                    if (inGreenhouse) {
                        // Convert clickPos to greenhouse tile
                        int gx = (int)(clickPos.x / GREENHOUSE_TILE_SIZE);
                        int gy = (int)(clickPos.y / GREENHOUSE_TILE_SIZE);
                        Tile gTile = greenhouseManager.getTile(gx, gy);
                        if (gTile == null) return;
                        if (!gTile.isPlowed()) {
                            showMessage("Tile is not plowed.", 1.5f);
                            return;
                        }
                        if (gTile.getPlantedSeed() != null) {
                            showMessage("Tile already has a seed.", 1.5f);
                            return;
                        }
                        // Always allowed season here
                        // Mixed seeds logic (reuse your existing clone logic)
                        Seeds seedToPlant = seed;
                        if (seed.getName().equalsIgnoreCase("Mixed Seeds")) {
                            // Reuse simplified random selection across all seeds
                            List<Seeds> viable = FruitsAndVegetablesRepository.seeds;
                            if (viable.isEmpty()) {
                                showMessage("No seeds available.", 2f);
                                return;
                            }
                            Seeds picked = viable.get(new java.util.Random().nextInt(viable.size()));
                            seedToPlant = cloneSeed(picked);
                            gameInstance.getCurrentPlayer().getInventory().removeItemByName("Mixed Seeds", 1);
                            showMessage("Mixed Seeds -> " + picked.getGrowsInto(), 2f);
                        } else {
                            gameInstance.getCurrentPlayer().getInventory().removeItemByName(seed.getName(), 1);
                            showMessage("Planted " + seed.getName(), 2f);
                        }
                        greenhouseManager.plantSeed(gx, gy, seedToPlant);
                        return;
                    }
                    if (!clickedTile.isPlowed()) {
                        showMessage("Tile is not plowed.", 1.5f);
                        return;
                    }
                    if (clickedTile.getPlantedSeed() != null) {
                        showMessage("Tile already has a seed.", 1.5f);
                        return;
                    }
                    if (!clickedTile.canPlant()) {
                        showMessage("Cannot plant here.", 1.5f);
                        return;
                    }
                    Seeds seedToPlant = seed; // default

                    if (seed.getName().equalsIgnoreCase("Mixed Seeds")) {
                        String season = TimeSystem.getInstance().getCurrentSeason();
                        List<String> spring = List.of("Cauliflower", "Parsnip", "Potato", "Blue Jazz", "Tulip");
                        List<String> summer = List.of("Corn", "Hot Pepper", "Radish", "Wheat", "Poppy", "Sunflower", "Summer Spangle");
                        List<String> fall   = List.of("Artichoke", "Corn", "Eggplant", "Pumpkin", "Sunflower", "Fairy Rose");
                        List<String> winter = List.of("Powdermelon");

                        List<String> options;
                        switch (season) {
                            case "Spring": options = spring; break;
                            case "Summer": options = summer; break;
                            case "Fall":   options = fall;   break;
                            case "Winter": options = winter; break;
                            default:       options = Collections.emptyList(); break;
                        }

                        if (options.isEmpty()) {
                            showMessage("No crops available for this season.", 2f);
                            return;
                        }

                        String chosenCrop = options.get(new Random().nextInt(options.size()));
                        Seeds actualSeed = FruitsAndVegetablesRepository.seeds.stream()
                            .filter(s -> s.getGrowsInto().equalsIgnoreCase(chosenCrop))
                            .findFirst().orElse(null);

                        if (actualSeed == null) {
                            showMessage("Mixed Seeds failed (" + chosenCrop + " missing).", 2f);
                            return;
                        }

                        seedToPlant = cloneSeed(actualSeed);
                        currentPlayer.getInventory().removeItemByName("Mixed Seeds", 1);
                        showMessage("Mixed Seeds -> " + chosenCrop, 2f);
                    } else {
                        // Regular seed: consume 1
                        currentPlayer.getInventory().removeItemByName(seed.getName(), 1);
                        showMessage("Planted " + seed.getName(), 2f);
                    }

                    // CRITICAL FIX: plant the resolved seedToPlant, NOT the original seed
                    clickedTile.setPlantedSeed(seedToPlant);
                    clickedTile.setDaysGrown(0);
                    clickedTile.setWatered(true);
                    clickedTile.setLastWateredDay(TimeSystem.getInstance().getCurrentDay());
                    clickedTile.setType("*"); // optional visual marker if you use text map

                    // Debug log (remove later if noisy)
                    System.out.println("Planted seed: displayName=" + seedToPlant.getName()
                        + " growsInto=" + seedToPlant.getGrowsInto()
                        + " harvestTime=" + seedToPlant.getTotalHarvestTime());

                    return;
                }
                if (selected != null && selected.getName() != null &&
                    selected.getName().endsWith("_Fertilizer") && clickedTile != null) {
                    applyFertilizerToTile(clickedTile, selected, currentPlayer);
                    return;
                }
            }

            if (clickedTile != null && clickedTile.getStaticElement().isPresent()) {
                StaticElement element = clickedTile.getStaticElement().get();
                if (element instanceof Greenhouse) {
                    if (isAdjacent(playerTileX, playerTileY, clickedTileX, clickedTileY)) {
                        Greenhouse gh = (Greenhouse) element;
                        if (!gh.isRepaired()) {
                            showGreenhouseRepairDialog(gh);
                        } else {
                            // Enter greenhouse
                            enterGreenhouse(playerPos.x, playerPos.y);
                        }
                    } else {
                        showMessage("Get closer to interact with the greenhouse.", 2f);
                    }
                    return;
                }
                if (element instanceof Npc) {
                    Npc npc = (Npc) element;
                    if (Math.abs(playerTileX - clickedTileX) <= 1 && Math.abs(playerTileY - clickedTileY) <= 1) {
                        if (npc.isDialogueReady()) {
                            float npcWorldX = clickPos.x;
                            float npcWorldY = clickPos.y;
                            showNpcSpeech(npc, gameInstance.getCurrentPlayer(), npcWorldX, npcWorldY);
                        }
                    }
                } else if (element instanceof SellingBin) {
                    if (Math.abs(playerTileX - clickedTileX) <= 1 && Math.abs(playerTileY - clickedTileY) <= 1) {
                        gameView.showInventoryForSelling();
                    }
                } else if (element instanceof Store) {
                    Store store = (Store) element;
                    if (Math.abs(playerTileX - clickedTileX) <= 1 && Math.abs(playerTileY - clickedTileY) <= 1) {
                        if (store.isOpen()) {
                            if (store.getName().equalsIgnoreCase("Blacksmith")) {
                                gameView.showBlacksmithView(store);
                            } else if (store.getName().equalsIgnoreCase("Fish Shop")) {
                                gameView.showFishShopView(store);
                            } else if (store.getName().equalsIgnoreCase("The Stardrop Saloon")) {
                                gameView.showSaloonView(store);
                            } else if (store.getName().equalsIgnoreCase("Marin'sRanch")) {
                                gameView.showMarinsRanchView(store);
                            } else if (store.getName().equalsIgnoreCase("Carpenter'sShop")) {
                                gameView.showCarpenterShopView(store);
                            }
                        } else {
                            showResultDialog("The " + store.getName() + " is closed.");
                        }
                    }
                }
            }
        }


        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);
            int clickedTileX, playerTileX, clickedTileY, playerTileY;
            if (inVillage) {
                clickedTileX = (int) (clickPos.x / TILE_SIZE);
                clickedTileY = (int) (clickPos.y / TILE_SIZE);
                playerTileX = (int) (playerPos.x / TILE_SIZE);
                playerTileY = (int) (playerPos.y / TILE_SIZE);
            } else {
                Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
                clickedTileX = (int) ((clickPos.x - farmTopLeft.x) / TILE_SIZE);
                clickedTileY = (int) ((clickPos.y - farmTopLeft.y) / TILE_SIZE);
                playerTileX = (int) ((playerPos.x - farmTopLeft.x) / TILE_SIZE);
                playerTileY = (int) ((playerPos.y - farmTopLeft.y) / TILE_SIZE);
            }
            Tile clickedTile;
            if (inVillage) {
                if (clickedTileX >= 0 && clickedTileX < 20 && clickedTileY >= 0 && clickedTileY < 20) {
                    clickedTile = gameMap.getVillage().getTile(clickedTileX, 20 - 1 - clickedTileY);
                } else {
                    clickedTile = null;
                }
            } else {
                if (clickedTileX >= 0 && clickedTileX < FarmTemplate.WIDTH &&
                    clickedTileY >= 0 && clickedTileY < FarmTemplate.HEIGHT) {
                    clickedTile = gameMap.getFarm(currentFarmIndex).getTile(clickedTileX, FarmTemplate.HEIGHT - 1 - clickedTileY);
                } else {
                    clickedTile = null;
                }
            }
            if (clickedTile != null && clickedTile.getStaticElement().isPresent() &&
                clickedTile.getStaticElement().get() instanceof Npc) {
                Npc npc = (Npc) clickedTile.getStaticElement().get();
                if (Math.abs(playerTileX - clickedTileX) <= 1 && Math.abs(playerTileY - clickedTileY) <= 1) {
                    selectedNpc = npc;
                    npcContextMenu.show(stage);
                }
            } else if (clickedTile != null && clickedTile.getStaticElement().isPresent() && clickedTile.getStaticElement().get() instanceof PlaceableGameBuilding) {
                PlaceableGameBuilding clickedBuilding = (PlaceableGameBuilding) clickedTile.getStaticElement().get();
                showBuildingContextMenu(clickedBuilding);
            }
            else if (clickedTile != null && clickedTile.getStaticElement().isPresent() && clickedTile.getStaticElement().get() instanceof Lake) {
                gameView.showFishingMinigameScreen(gameInstance.getCurrentPlayer(),TimeSystem.getInstance().getCurrentSeason(), gameInstance.getCurrentPlayer().getFishingSkills());            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS))
            camera.zoom -= 0.02f;
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) camera.zoom += 0.02f;
        camera.zoom = Math.max(0.3f, Math.min(2f, camera.zoom));
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) setCurrentFarmIndex(0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) setCurrentFarmIndex(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) setCurrentFarmIndex(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) setCurrentFarmIndex(3);

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) selectQuickAccessSlot(0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) selectQuickAccessSlot(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) selectQuickAccessSlot(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) selectQuickAccessSlot(3);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) selectQuickAccessSlot(4);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) selectQuickAccessSlot(5);
        checkTravel();
    }
    private boolean isAdjacent(int ax, int ay, int bx, int by) {
        return Math.abs(ax - bx) <= 1 && Math.abs(ay - by) <= 1;
    }
    private void showGreenhouseRepairDialog(Greenhouse gh) {
        final User player = gameInstance.getCurrentPlayer();
        final int cost = GREENHOUSE_REPAIR_COST;

        if (gh.isRepaired()) {
            showMessage("Greenhouse is already repaired.", 2f);
            return;
        }

        Dialog dialog = new Dialog("Broken Greenhouse", skin) {
            @Override
            protected void result(Object object) {
                if (Boolean.TRUE.equals(object)) {
                    // Confirm repair
                    if (player.getMoney() >= cost && !gh.isRepaired()) {
                        player.setMoney(player.getMoney() - cost);
                        gh.repair();
                        showMessage("Greenhouse repaired!", 3f);
                    } else {
                        showMessage("Unable to repair (not enough gold or already repaired).", 2.5f);
                    }
                }
            }
        };

        if (player.getMoney() >= cost) {
            dialog.text("Repair the greenhouse for " + cost + "g?\nYou have: " + player.getMoney() + "g");
            dialog.button("Repair", true);
            dialog.button("Cancel", false);
        } else {
            dialog.text("The greenhouse is broken.\nNeed " + cost + "g to repair.\nYou have: " + player.getMoney() + "g");
            dialog.button("OK");
        }

        dialog.show(stage);
    }
    private void renderLightning(float delta) {
        if (!lightningActive) return;
        Animation<TextureRegion> anim = MapManager.getInstance().getLightningAnimation();
        if (anim == null || lightningWorldPos == null) {
            lightningActive = false;
            return;
        }
        lightningTime += delta;
        TextureRegion frame = anim.getKeyFrame(lightningTime, false);
        if (frame == null) {
            lightningActive = false;
            return;
        }
        final float TARGET_HEIGHT_PIX = 560f;

        final boolean STRETCH_VERTICALLY = false;
        float srcW = frame.getRegionWidth();
        float srcH = frame.getRegionHeight();
        float drawW;
        float drawH;
        if (STRETCH_VERTICALLY) {
            drawH = TARGET_HEIGHT_PIX;
            drawW = srcW * (drawH / srcH) * 0.65f;
        } else {
            float scale = TARGET_HEIGHT_PIX / srcH;
            drawH = TARGET_HEIGHT_PIX;
            drawW = srcW * scale;
        }

        float drawX = lightningWorldPos.x - (drawW / 2f);
        float drawY = lightningWorldPos.y;
        batch.setColor(Color.WHITE);
        batch.draw(frame, drawX, drawY, drawW, drawH);
        if (lightningTime < FLASH_DURATION) {
            float t = lightningTime / FLASH_DURATION;
            float alpha = FLASH_MAX_ALPHA * (1f - t);
            Texture px = MapManager.getInstance().getPixelWhiteTexture();
            float left = camera.position.x - camera.viewportWidth / 2f;
            float bottom = camera.position.y - camera.viewportHeight / 2f;
            batch.setColor(1f, 1f, 1f, alpha);
            batch.draw(px, left, bottom, camera.viewportWidth, camera.viewportHeight);
            batch.setColor(Color.WHITE);
        }

        if (anim.isAnimationFinished(lightningTime)) {
            lightningActive = false;
        }
    }
    private void castLightningAtClick() {
        lightningTargetMode = false;

        Animation<TextureRegion> anim = MapManager.getInstance().getLightningAnimation();
        if (anim == null) {
            showMessage("Lightning assets missing.", 2f);
            return;
        }

        Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(clickPos);

        int clickedTileX, clickedTileY;
        Tile clickedTile = null;
        if (inVillage) {
            clickedTileX = (int) (clickPos.x / TILE_SIZE);
            clickedTileY = (int) (clickPos.y / TILE_SIZE);
            if (clickedTileX >= 0 && clickedTileX < 20 && clickedTileY >= 0 && clickedTileY < 20) {
                clickedTile = gameMap.getVillage().getTile(clickedTileX, 20 - 1 - clickedTileY);
            }
        } else {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            clickedTileX = (int) ((clickPos.x - farmTopLeft.x) / TILE_SIZE);
            clickedTileY = (int) ((clickPos.y - farmTopLeft.y) / TILE_SIZE);
            if (clickedTileX >= 0 && clickedTileX < FarmTemplate.WIDTH &&
                clickedTileY >= 0 && clickedTileY < FarmTemplate.HEIGHT) {
                clickedTile = gameMap.getFarm(currentFarmIndex)
                    .getTile(clickedTileX, FarmTemplate.HEIGHT - 1 - clickedTileY);
            }
        }

        float worldX, worldY;
        if (inVillage) {
            worldX = clickedTileX * TILE_SIZE + TILE_SIZE / 2f;
            worldY = clickedTileY * TILE_SIZE;
        } else {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            worldX = farmTopLeft.x + clickedTileX * TILE_SIZE + TILE_SIZE / 2f;
            worldY = farmTopLeft.y + clickedTileY * TILE_SIZE;
        }

        lightningWorldPos = new Vector2(worldX, worldY);
        lightningActive = true;
        lightningTime = 0f;

        if (clickedTile != null && !inVillage && clickedTile.getRandomElement().isPresent()) {
            Object rnd = clickedTile.getRandomElement().get();
            if (rnd instanceof Tree || rnd instanceof ForagingTree) {
                burntTreeTiles.add(clickedTile);
                showMessage("Tree was struck by lightning!", 2f);
            } else {
                showMessage("Lightning strikes!", 1.2f);
            }
        } else {
            showMessage("Lightning strikes!", 1.2f);
        }
    }
    private Seeds cloneSeed(Seeds original) {
        if (original == null) return null;
        Seeds copy = new Seeds();
        copy.setName(original.getName());
        copy.setGrowsInto(original.getGrowsInto());
        copy.setSuitableSeasons(new ArrayList<>(original.getSuitableSeasons()));
        copy.setTotalHarvestTime(original.getTotalHarvestTime());
        copy.setImagePath(original.getImagePath());
        copy.setQuantity(1);
        // If Seeds has extra fields (sell price, etc.) add copying here.
        return copy;
    }
    private void applyFertilizerToTile(Tile tile, Item fertilizer, User player) {
        if (inVillage) {
            showMessage("Cannot fertilize here.",1.2f);
            return;
        }
        if (tile.getPlantedSeed()==null) {
            showMessage("No crop planted.",1.2f);
            return;
        }
        if (tile.hasFertilizer()) {
            showMessage("Fertilizer already applied.",1.2f);
            return;
        }
        String fertName = fertilizer.getName();
        tile.setFertilizerType(fertName);
        tile.setFertilizerApplied(false); // will give bonus on next growth update (or immediate)
        // Immediate bonus model: add virtual days now:
        int bonus = fertilizerGrowthBonus(fertName);
        tile.setDaysGrown(Math.max(1, tile.getDaysGrown()+bonus));
        tile.setFertilizerApplied(true); // since we applied instantly
        // Consume one fertilizer unit
        player.getInventory().removeItemByName(fertName,1);
        showMessage("Applied " + fertName.replace("_"," "),1.5f);
    }
    private int fertilizerGrowthBonus(String fertName) {
        if (fertName == null) return 0;
        switch (fertName) {
            case "Basic_Fertilizer": return 1;
            case "Deluxe_Fertilizer": return 2;
            case "Quality_Fertilizer": return 3;
            default: return 0;
        }
    }
    private void openFarmingDialog() {
        if (farmingDialog == null) {
            farmingDialog = new FarmingDialog(skin, gameInstance.getCurrentPlayer());
            farmingDialog.setOnSeedSelected(seed -> {
                pendingSelectedSeed = seed;
                showResultDialog("Selected: " + seed.getName() + " (Choose plowed tile to plant)");
                uiBlockedByDialog = false;
            });
            farmingDialog.setOnClosed(() -> uiBlockedByDialog = false);
        }
        // Update current user reference (fixes earlier bug using first user)
        farmingDialog.setCurrentUser(gameInstance.getCurrentPlayer());
        farmingDialog.show(stage);
        uiBlockedByDialog = true;
    }
    private boolean tryShowCraftInfoAt(Vector3 worldClick) {
        int clickedTileX, clickedTileY;

        if (inVillage) {
            clickedTileX = (int) (worldClick.x / TILE_SIZE);
            clickedTileY = (int) (worldClick.y / TILE_SIZE);
        } else {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            clickedTileX = (int) ((worldClick.x - farmTopLeft.x) / TILE_SIZE);
            clickedTileY = (int) ((worldClick.y - farmTopLeft.y) / TILE_SIZE);
        }

        Tile clickedTile;
        if (inVillage) {
            if (clickedTileX < 0 || clickedTileX >= 20 || clickedTileY < 0 || clickedTileY >= 20) return false;
            clickedTile = gameMap.getVillage().getTile(clickedTileX, 20 - 1 - clickedTileY);
        } else {
            if (clickedTileX < 0 || clickedTileX >= FarmTemplate.WIDTH ||
                clickedTileY < 0 || clickedTileY >= FarmTemplate.HEIGHT) return false;
            clickedTile = gameMap.getFarm(currentFarmIndex).getTile(clickedTileX, FarmTemplate.HEIGHT - 1 - clickedTileY);
        }
        if (clickedTile == null) return false;

        // 1) Planted seed/crop on this tile?
        if (clickedTile.getPlantedSeed() != null) {
            CraftInfoDialog dialog = CraftInfoDialog.forPlantedSeed(clickedTile, skin);
            dialog.show(stage);
            return true;
        }

        // 2) Random element (foraging crops, trees, minerals, stones)?
        if (clickedTile.getRandomElement().isPresent()) {
            Object element = clickedTile.getRandomElement().get();
            CraftInfoDialog dialog = null;

            if (element instanceof Tree) {
                dialog = CraftInfoDialog.forTree((Tree) element, skin);
            } else if (element instanceof ForagingCrop) {
                dialog = CraftInfoDialog.forForagingCrop((ForagingCrop) element, skin);
            } else if (element instanceof ForagingTree) {
                dialog = CraftInfoDialog.forForagingTree((ForagingTree) element, skin);
            } else if (element instanceof ForagingMineral) {
                dialog = CraftInfoDialog.forForagingMineral((ForagingMineral) element, skin);
            } else if (element instanceof Stone) {
                dialog = CraftInfoDialog.forStone((Stone) element, skin);
            }

            if (dialog != null) {
                dialog.show(stage);
                return true;
            }
        }

        return false;
    }
    private void selectQuickAccessSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < 6) {
            selectedQuickSlot = slotIndex;
            System.out.println("Selected quick access slot: " + slotIndex);

            // Debug: show what's in the selected slot
            User currentPlayer = gameInstance.getCurrentPlayer();
            Item[] quickSlots = currentPlayer.getInventory().getQuickAccessSlots();
            Item selectedItem = quickSlots[selectedQuickSlot];
            System.out.println("Item in slot " + slotIndex + ": " +
                (selectedItem != null ? selectedItem.getName() : "empty"));

            if (selectedItem instanceof Tools) {
                Tools tool = (Tools) selectedItem;
                System.out.println("Tool selected: " + tool.getName() + " with path: " + tool.getPath());
            }
        }
    }

    private boolean isAreaPassable(float worldX, float worldY) {
        if (inGreenhouse) {
            // Allow movement anywhere inside rectangle; block outside bounds
            float maxX = GreenhouseManager.WIDTH * GREENHOUSE_TILE_SIZE;
            float maxY = GreenhouseManager.HEIGHT * GREENHOUSE_TILE_SIZE;

            // Player feet point; ensure within bounding box
            if (worldX < 0 || worldX > maxX - TILE_SIZE) return false;
            if (worldY < 0 || worldY > maxY - TILE_SIZE) return false;

            // (Later you can add internal collision here)
            return true;
        }
        // Check all four corners with a smaller hitbox to prevent clipping
        float hitboxInset = TILE_SIZE * 0.15f; // Increased inset for better boundary detection
        float hitboxX = worldX + hitboxInset;
        float hitboxY = worldY + hitboxInset;
        float hitboxWidth = TILE_SIZE - (2 * hitboxInset);
        float hitboxHeight = TILE_SIZE - (2 * hitboxInset);

        // If ANY corner is not passable, the whole area is not passable
        boolean bottomLeft = isTilePassable(hitboxX, hitboxY);
        boolean bottomRight = isTilePassable(hitboxX + hitboxWidth, hitboxY);
        boolean topLeft = isTilePassable(hitboxX, hitboxY + hitboxHeight);
        boolean topRight = isTilePassable(hitboxX + hitboxWidth, hitboxY + hitboxHeight);

        // Additional center point check for small objects
        boolean center = isTilePassable(worldX + TILE_SIZE / 2, worldY + TILE_SIZE / 2);

        return bottomLeft && bottomRight && topLeft && topRight && center;
    }

    private boolean handleHoeUsage(Tools hoe, Vector3 clickPos) {
        User currentPlayer = gameInstance.getCurrentPlayer();
        int energyCost = hoe.getEnergyCost();

        if (!currentPlayer.getEnergy().isUnlimited() && energyUsedThisTurn + energyCost > ENERGY_LIMIT_PER_TURN) {
            showMessage("Energy limit reached for this turn", 2);
            return false;
        }
        if (!currentPlayer.getEnergy().isUnlimited() && currentPlayer.getEnergy().getCurrentEnergy() < energyCost) {
            showMessage("Not enough energy to use the hoe", 2);
            return false;
        }

        int clickedTileX, clickedTileY;
        if (inGreenhouse) {
            Vector3 cp = clickPos;
            int tx = (int)(cp.x / GREENHOUSE_TILE_SIZE);
            int ty = (int)(cp.y / GREENHOUSE_TILE_SIZE);
            Tile t = greenhouseManager.getTile(tx, ty);
            if (t == null) return false;
            if (t.isPlowed()) {
                showMessage("Already plowed.", 1.2f);
                return true;
            }
            t.setPlowed(true);
            if (!currentPlayer.getEnergy().isUnlimited()) {
                currentPlayer.getEnergy().decreaseEnergy(energyCost);
                energyUsedThisTurn += energyCost;
            }
            showMessage("Plowed (Greenhouse).", 1.2f);
            return true;
        }
        if (inVillage) {
            showMessage("You can't plow in the village", 2);
            return false;
        } else {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            clickedTileX = (int) ((clickPos.x - farmTopLeft.x) / TILE_SIZE);
            clickedTileY = (int) ((clickPos.y - farmTopLeft.y) / TILE_SIZE);
        }

        if (clickedTileX < 0 || clickedTileX >= FarmTemplate.WIDTH ||
            clickedTileY < 0 || clickedTileY >= FarmTemplate.HEIGHT) {
            showMessage("Invalid tile", 2);
            return false;
        }

        // Mirror Y for all tile access to match rendering and object checks
        int mirroredY = FarmTemplate.HEIGHT - 1 - clickedTileY;
        Tile clickedTile = gameMap.getFarm(currentFarmIndex).getTile(clickedTileX, mirroredY);

        if (clickedTile == null) {
            showMessage("No tile found", 2);
            return false;
        }

        if (clickedTile.getStaticElement().isPresent() || clickedTile.getRandomElement().isPresent()) {
            showMessage("Can't plow this tile - remove objects first", 2);
            return false;
        }
        if (clickedTile.isPlowed()) {
            showMessage("This tile is already plowed", 2);
            return false;
        }

        clickedTile.setPlowed(true);
        toolManager.startToolSwing(hoe);

        if (!currentPlayer.getEnergy().isUnlimited()) {
            currentPlayer.getEnergy().decreaseEnergy(energyCost);
            energyUsedThisTurn += energyCost;
        }

        showMessage("Tile plowed successfully at (" + clickedTileX + ", " + clickedTileY + ")", 1);
        return true;
    }

    public void showMessage(String message, float duration) {
        lastTurnMessage = message;
        messageTimer = duration;
    }

    private boolean isTilePassable(float worldX, float worldY) {
        // First, absolute boundary check without even checking tiles
        if (inVillage) {
            // Village boundaries (0,0 to 19,19)
            if (worldX < 0 || worldX >= 20 * TILE_SIZE ||
                worldY < 0 || worldY >= 20 * TILE_SIZE) {
                return false;
            }
        } else {
            // Farm boundaries based on current farm
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            float farmWidth = FarmTemplate.WIDTH * TILE_SIZE;
            float farmHeight = FarmTemplate.HEIGHT * TILE_SIZE;

            // Adjust hitbox boundaries - extend top/bottom detection further
            float topBoundaryAdjustment = TILE_SIZE * 0.8f; // Extend top detection
            float bottomBoundaryAdjustment = TILE_SIZE * 0.00001f; // Extend bottom detection

            if (worldX < farmTopLeft.x || worldX >= farmTopLeft.x + farmWidth ||
                worldY < farmTopLeft.y - bottomBoundaryAdjustment ||
                worldY >= farmTopLeft.y + farmHeight + topBoundaryAdjustment) {
                return false;
            }
        }

        // If we pass the hard boundary check, proceed with normal tile check
        Tile tile;
        if (inVillage) {
            int tileX = (int) (worldX / TILE_SIZE);
            int tileY = (int) (worldY / TILE_SIZE);
            tile = gameMap.getVillage().getTile(tileX, 20 - 1 - tileY);
        } else {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            int localX = (int) ((worldX - farmTopLeft.x) / TILE_SIZE);
            int localY = (int) ((worldY - farmTopLeft.y) / TILE_SIZE);
            tile = gameMap.getFarm(currentFarmIndex).getTile(localX, FarmTemplate.HEIGHT - 1 - localY);
        }

        if (tile == null) return false;
        if (tile.getStaticElement().isPresent()) {
            Object element = tile.getStaticElement().get();
            if (element instanceof Cabin || element instanceof Greenhouse || element instanceof Lake) {
                return false;
            }
        }
        return tile.isPassable();
    }

    private void checkTravel() {
        if (speechIsShowing) return;
        if (inVillage) {
            int playerTileX = (int) (playerPos.x / TILE_SIZE);
            int playerTileY = (int) (playerPos.y / TILE_SIZE);
            if (playerTileX == 0 && playerTileY == 19) {
                currentFarmIndex = 0;
                showTravelDialog("Farm 1");
            } else if (playerTileX == 19 && playerTileY == 19) {
                currentFarmIndex = 1;
                showTravelDialog("Farm 2");
            } else if (playerTileX == 0 && playerTileY == 0) {
                currentFarmIndex = 2;
                showTravelDialog("Farm 3");
            } else if (playerTileX == 19 && playerTileY == 0) {
                currentFarmIndex = 3;
                showTravelDialog("Farm 4");
            }
        } else {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            int localX = (int) ((playerPos.x - farmTopLeft.x) / TILE_SIZE);
            int localY = (int) ((playerPos.y - farmTopLeft.y) / TILE_SIZE);
            boolean onPortal = false;
            switch (currentFarmIndex) {
                case 0:
                    if (localX == 49 && localY == 0) onPortal = true;
                    break;
                case 1:
                    if (localX == 0 && localY == 0) onPortal = true;
                    break;
                case 2:
                    if (localX == 49 && localY == 49) onPortal = true;
                    break;
                case 3:
                    if (localX == 0 && localY == 49) onPortal = true;
                    break;
            }
            if (onPortal) {
                showTravelDialog("the Village");
            }
        }
    }

    // In MapView.java

    private void createBuildingContextMenu() {
        buildingContextMenu = new BuildingDetailsDialog("Building Actions", skin, gamePlayController);
    }

    private void showBuildingContextMenu(PlaceableGameBuilding building) {
        buildingContextMenu.showForBuilding(building, stage);
    }
    private void updateAnimals(float delta) {
        if (inVillage) return; // Animals are only on the farm

        User currentPlayer = gameInstance.getCurrentPlayer();
        Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);

        float playerFarmX = playerPos.x - farmTopLeft.x;
        float playerFarmY = playerPos.y - farmTopLeft.y;

        for (Animal animal : currentPlayer.getPutAnimals()) {
            if (animal.isOutside) {
                animal.update(delta, gameMap.getFarm(currentFarmIndex), playerFarmX, playerFarmY);
            }
        }
    }



    // render animals
    private void renderAnimals() {
        if (inVillage) return;

        User currentPlayer = gameInstance.getCurrentPlayer();
        Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);

        for (Animal animal : currentPlayer.getPutAnimals()) {
            if (animal.isOutside) {
                TextureRegion currentFrame;
                float animalSize = TILE_SIZE * 1.5f;

                if (animal.isBeingFed()) {
                    batch.draw(mapManager.getHayTexture(), farmTopLeft.x + animal.getPositionX(), farmTopLeft.y + animal.getPositionY(), TILE_SIZE, TILE_SIZE);
                }

                if (animal.isBeingPetted()) {
                    currentFrame = mapManager.getPettingFrame(animal.getType());
                } else {
                    String direction = "down";
                    switch(animal.getLastDirection()){
                        case 1: direction = "right"; break;
                        case 2: direction = "up"; break;
                        case 3: direction = "left"; break;
                    }

                    Animation<TextureRegion> animation = mapManager.getAnimalAnimation(animal.getType(), direction, animal.isMoving());
                    if (animation != null) {
                        currentFrame = animation.getKeyFrame(animal.getAnimationStateTime(), true);
                    } else {
                        continue; // Skip rendering if no animation is found
                    }
                }

                if (currentFrame != null) {
                    float absoluteX = farmTopLeft.x + animal.getPositionX();
                    float absoluteY = farmTopLeft.y + animal.getPositionY();
                    batch.draw(currentFrame, absoluteX, absoluteY, animalSize, animalSize);
                }
            }
        }
    }
    private void renderToolSwing() {
        // If the new character tool-use animation is active, skip old overlay.
        if (toolManager.isUsingTool()) {
            return;
        }
        if (toolManager.isSwinging()) {
            Texture toolTexture = toolManager.getCurrentToolTexture();
            if (toolTexture != null) {
                float swingAngle = toolManager.getSwingAngle();
                float toolX = playerPos.x;
                float toolY = playerPos.y;
                float toolWidth = TILE_SIZE * 1.0f;
                float toolHeight = TILE_SIZE * 1.0f;
                toolX += TILE_SIZE * 0.4f;
                float[] origin = toolManager.getToolRotationOrigin();
                float originX = toolWidth * origin[0];
                float originY = toolHeight * origin[1];
                batch.draw(
                    toolTexture,
                    toolX,
                    toolY,
                    originX,
                    originY,
                    toolWidth,
                    toolHeight,
                    1, 1,
                    swingAngle,
                    0, 0,
                    toolTexture.getWidth(),
                    toolTexture.getHeight(),
                    false, false
                );
            }
        }
    }
    private Vector2 getFarmTopLeft(int farmIndex) {
        int farmW = FarmTemplate.WIDTH;
        int farmH = FarmTemplate.HEIGHT;
        int vilW = gameMap.getVilW();
        int vilH = gameMap.getVilH();
        float x_offset, y_offset;
        switch (farmIndex) {
            case 0:
                x_offset = 0;
                y_offset = vilH * TILE_SIZE;
                break;
            case 1:
                x_offset = (vilW + farmW) * TILE_SIZE;
                y_offset = vilH * TILE_SIZE;
                break;
            case 2:
                x_offset = 0;
                y_offset = 0;
                break;
            case 3:
                x_offset = (vilW + farmW) * TILE_SIZE;
                y_offset = 0;
                break;
            default:
                x_offset = 0;
                y_offset = 0;
        }
        return new Vector2(x_offset, y_offset);
    }
    private void enterGreenhouse(float exteriorWorldX, float exteriorWorldY) {
        if (inGreenhouse) return;
        outsideReturnPos.set(exteriorWorldX, exteriorWorldY);
        inGreenhouse = true;
        // Door spawn: bottom center
        greenhousePlayerPos.set((GreenhouseManager.WIDTH / 2f) * GREENHOUSE_TILE_SIZE, GREENHOUSE_TILE_SIZE * 1.2f);
        playerPos.set(greenhousePlayerPos);
        centerCameraOnPlayer();
        showMessage("Entered Greenhouse", 2f);
    }

    private void exitGreenhouse() {
        if (!inGreenhouse) return;
        inGreenhouse = false;
        playerPos.set(outsideReturnPos);
        centerCameraOnPlayer();
        showMessage("Left Greenhouse", 2f);
    }
    private void captureCurrentFrame() {
        if (lastFrameTexture != null) {
            lastFrameTexture.dispose();
        }
        if (lastFramePixmap != null) {
            lastFramePixmap.dispose();
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderMap();
        renderPlayer();
        renderUI();
        batch.end();
        int width = Gdx.graphics.getBackBufferWidth();
        int height = Gdx.graphics.getBackBufferHeight();
        lastFramePixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, width, height, true);
        ByteBuffer buffer = lastFramePixmap.getPixels();
        buffer.clear();
        buffer.put(pixels);
        buffer.position(0);
        lastFrameTexture = new Texture(lastFramePixmap);
    }

    private boolean handleAnimalClick(Vector3 clickPos) {
        User currentPlayer = gameInstance.getCurrentPlayer();
        if (inVillage || currentPlayer.getPutAnimals().isEmpty()) {
            return false;
        }

        Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);

        for (Animal animal : currentPlayer.getPutAnimals()) {
            if (animal.isOutside) {
                float animalGlobalWorldX = farmTopLeft.x + animal.getPositionX();
                float animalGlobalWorldY = farmTopLeft.y + animal.getPositionY();
                float animalClickableSize = TILE_SIZE * 1.5f;

                if (clickPos.x >= animalGlobalWorldX && clickPos.x <= animalGlobalWorldX + animalClickableSize &&
                    clickPos.y >= animalGlobalWorldY && clickPos.y <= animalGlobalWorldY + animalClickableSize) {

                    showAnimalInteractionMenu(animal);
                    return true;
                }
            }
        }
        return false;
    }

    private void showAnimalInteractionMenu(final Animal animal) {
        Dialog dialog = new Dialog("Interact with " + animal.getName(), skin);

        TextButton petButton = new TextButton("Pet", skin);
        TextButton feedButton = new TextButton("Feed", skin);
        TextButton productButton = new TextButton("Product", skin); // New button
        TextButton profileButton = new TextButton("Profile", skin);

        String followText = animal.isFollowing() ? "Stop Following" : "Follow";
        TextButton followButton = new TextButton(followText, skin);

        TextButton bringButton = new TextButton("Bring Inside", skin);
        TextButton sellButton = new TextButton("Sell", skin);
        TextButton closeButton = new TextButton("Close", skin);

        dialog.getContentTable().add(petButton).pad(5).row();
        dialog.getContentTable().add(feedButton).pad(5).row();
        dialog.getContentTable().add(productButton).pad(5).row(); // Add new button to the dialog
        dialog.getContentTable().add(profileButton).pad(5).row();
        dialog.getContentTable().add(followButton).pad(5).row();
        dialog.getContentTable().add(bringButton).pad(5).row();
        dialog.getContentTable().add(sellButton).pad(5).row();
        dialog.getButtonTable().add(closeButton).pad(10);

        petButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String result = currentPlayerController.petAnimal(animal.getName());
                if (animal.isPettedToday()) {
                    showResultDialog(result);
                }
                dialog.hide();
            }
        });

        feedButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String result = currentPlayerController.feedAnimal(animal.getName());
                if (animal.isFed()) {
                    showResultDialog(result);
                }
                dialog.hide();
            }
        });

        productButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String result = currentPlayerController.collectProductFromAnimal(animal.getName());
                showResultDialog(result);
                dialog.hide();
            }
        });

        profileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
                showAnimalProfile(animal);
            }
        });

        followButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                animal.setFollowing(!animal.isFollowing(), gameInstance.getCurrentPlayer());
                String message = animal.isFollowing() ? "is now following you." : "has stopped following you.";
                showResultDialog(animal.getName() + " " + message);
                dialog.hide();
            }
        });

        bringButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String result = currentPlayerController.bringAnimal(animal.getName());
                showResultDialog(result);
                dialog.hide();
            }
        });

        sellButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String result = currentPlayerController.sellAnimal(animal.getName());
                showResultDialog(result);
                dialog.hide();
            }
        });

        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });

        dialog.show(stage);
    }


    private void showAnimalProfile(Animal animal) {
        Dialog profileDialog = new Dialog(animal.getName() + "'s Profile", skin);
        profileDialog.text("Type: " + animal.getType() + "\n" +
            "Petted Today: " + (animal.isPettedToday()) + "\n" +
            "Fed Today: " + (animal.isFed()) + "\n" +
            "Friendship: " + animal.getFriendship());
        profileDialog.button("OK");
        profileDialog.show(stage);
    }

    private boolean handleBuildingClick(Vector3 clickPos) {
        if (inVillage) return false;

        User currentPlayer = gameInstance.getCurrentPlayer();
        Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);

        for (Item building : currentPlayer.getPlacedAnimalPlaces()) {
            int buildingX, buildingY, width, height;

            if (building instanceof Coop) {
                Coop coop = (Coop) building;
                buildingX = coop.getLeftCornerX();
                buildingY = coop.getLeftCornerY();
                width = coop.getWidth();
                height = coop.getHeight();
            } else if (building instanceof Barn) {
                Barn barn = (Barn) building;
                buildingX = barn.getLeftCornerX();
                buildingY = barn.getLeftCornerY();
                width = barn.getWidth();
                height = barn.getHeight();
            } else {
                continue;
            }

            float worldX = farmTopLeft.x + (buildingX * TILE_SIZE);
            float worldY = farmTopLeft.y + ((FarmTemplate.HEIGHT - 1 - buildingY - height + 1) * TILE_SIZE);

            if (clickPos.x >= worldX && clickPos.x <= worldX + (width * TILE_SIZE) &&
                clickPos.y >= worldY && clickPos.y <= worldY + (height * TILE_SIZE)) {
                showBuildingInteractionMenu(building);
                return true;
            }
        }

        return false;
    }

    private void showBuildingInteractionMenu(final Item building) {
        final Dialog dialog = new Dialog(building.getName(), skin);

        TextButton placeAnimalButton = new TextButton("Place Animal", skin);
        TextButton removeAnimalButton = new TextButton("Remove Animal", skin);

        dialog.getContentTable().add(placeAnimalButton).pad(5).row();
        dialog.getContentTable().add(removeAnimalButton).pad(5).row();
        dialog.button("Close");

        placeAnimalButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide(); // <<-- FIX: Hide the current dialog first
                showPlaceAnimalDialog(building);
            }
        });

        removeAnimalButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide(); // <<-- FIX: Hide the current dialog first
                showRemoveAnimalDialog(building);
            }
        });

        dialog.show(stage);
    }

    private void showPlaceAnimalDialog(final Item building) {
        User currentPlayer = gameInstance.getCurrentPlayer();
        int capacity = (building instanceof Coop) ? ((Coop) building).getCapacity() : ((Barn) building).getCapacity();
        int currentOccupancy = currentPlayerController.getAnimalsInBuilding(building).size();

        if (currentOccupancy >= capacity) {
            showResultDialog("This building is full.");
            return;
        }

        final Dialog placeDialog = new Dialog("Place Animal", skin);
        Table animalListTable = new Table();
        ScrollPane scrollPane = new ScrollPane(animalListTable, skin);

        String buildingType = (building instanceof Coop) ? "Coop" : "Barn";
        List<Animal> placeableAnimals = currentPlayer.getAnimals().stream()
            .filter(animal -> animal.getBuildingType().equalsIgnoreCase(buildingType))
            .collect(Collectors.toList());

        if (placeableAnimals.isEmpty()) {
            placeDialog.text("You have no animals to place in this building.");
        } else {
            for (final Animal animal : placeableAnimals) {
                TextButton animalButton = new TextButton(animal.getName(), skin);
                animalButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        String result = currentPlayerController.placeAnimalInBuilding(animal.getName(), building);
                        showResultDialog(result);
                        placeDialog.hide();
                    }
                });
                animalListTable.add(animalButton).row();
            }
        }
        placeDialog.getContentTable().add(scrollPane);
        placeDialog.button("Cancel");
        placeDialog.show(stage);
    }

    private void showRemoveAnimalDialog(final Item building) {
        final Dialog removeDialog = new Dialog("Remove Animal", skin);
        Table animalListTable = new Table();
        ScrollPane scrollPane = new ScrollPane(animalListTable, skin);

        List<Animal> animalsInBuilding = currentPlayerController.getAnimalsInBuilding(building);

        if (animalsInBuilding.isEmpty()) {
            removeDialog.text("There are no animals in this building.");
        } else {
            for (final Animal animal : animalsInBuilding) {
                TextButton animalButton = new TextButton(animal.getName(), skin);
                animalButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
                        float playerHeight = TILE_SIZE * 1.5f;
                        float playerRenderOffsetY = (playerHeight - TILE_SIZE) * 0.5f;
                        float playerGroundY = playerPos.y - playerRenderOffsetY;
                        float relativeX = (playerPos.x - farmTopLeft.x) + 32f;
                        float relativeY = playerGroundY - farmTopLeft.y;
                        animal.bringOutside(relativeX, relativeY);

                        currentPlayerController.removeAnimalFromBuilding(animal.getName(), building);
                        removeDialog.hide();
                    }
                });
                animalListTable.add(animalButton).row();
            }
        }

        removeDialog.getContentTable().add(scrollPane);
        removeDialog.button("Cancel");
        removeDialog.show(stage);
    }
    private void initNightFogTexture() {
        if (nightFogReady) return;
        nightFogBatch = new SpriteBatch();
        int texSize = 512;
        Pixmap pixmap = new Pixmap(texSize, texSize, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        float radius = texSize / 2f;
        for (int x = 0; x < texSize; x++) {
            for (int y = 0; y < texSize; y++) {
                float dist = Vector2.dst(x, y, radius, radius) / radius;
                float alpha;
                if (dist < 0.25f) {
                    alpha = 0f;
                } else if (dist < 0.6f) {
                    float t = (dist - 0.25f) / 0.35f;
                    alpha = Math.min(0.8f, t * t * 0.8f);
                } else {
                    alpha = 0.8f;
                }
                pixmap.drawPixel(x, y, Color.rgba8888(0.02f, 0.03f, 0.06f, alpha));
            }
        }
        nightFogTexture = new Texture(pixmap);
        pixmap.dispose();
        nightFogReady = true;
    }
    private float computeNightTarget() {
        int hour = TimeSystem.getInstance().getCurrentHour();
        if (hour < 17) return 0f;
        if (hour >= 19) return 1f;
        float t = (hour - 17f) / 2f;
        return Math.max(0f, Math.min(1f, t));
    }
    private void drawNightOverlays(float delta) {
        if (!nightEnabled) return;
        nightProgressTarget = computeNightTarget();
        float lerpRate = 2.0f;
        nightProgress += (nightProgressTarget - nightProgress) * Math.min(1f, delta * lerpRate);
        nightProgress = Math.max(0f, Math.min(1f, nightProgress));
        if (nightProgress <= 0.001f) return;
        float left   = camera.position.x - camera.viewportWidth  / 2f;
        float bottom = camera.position.y - camera.viewportHeight / 2f;
        float w = camera.viewportWidth;
        float h = camera.viewportHeight;
        float darkAlpha = 0.6f * nightProgress;
        float blueAlpha = 0.4f * nightProgress;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, darkAlpha);
        shapeRenderer.rect(left, bottom, w, h);
        shapeRenderer.setColor(0.06f, 0.09f, 0.15f, blueAlpha);
        shapeRenderer.rect(left, bottom, w, h);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        if (buildingSelectBox != null) {
            buildingSelectBox.setPosition(Gdx.graphics.getWidth() - buildingSelectBox.getWidth() - 20,
                Gdx.graphics.getHeight() - 50);
        }
        // --- NEW: Reposition buildingContextMenu on resize ---
        if (buildingContextMenu != null) {
            buildingContextMenu.setPosition(Gdx.graphics.getWidth() / 2f - buildingContextMenu.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f - buildingContextMenu.getHeight() / 2f);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }


    // In MapView.java, add this new private method
    private void renderBuildModeHighlight() {
        // Only highlight if we are in build mode
        if (gamePlayController.isInBuildMode()) {
            // Get current mouse position in world coordinates (using the same logic as for placing buildings)
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);

            // Get the current farm's render offset
            Vector2 renderOffset = inVillage ? new Vector2(0, 0) : getFarmTopLeft(currentFarmIndex);

            // Adjust mousePos relative to the farm's origin
            float adjustedMouseX = mousePos.x - renderOffset.x;
            float adjustedMouseY = mousePos.y - renderOffset.y;

            // Calculate raw tile coordinates (0 at bottom of farm's local coords)
            int tileX = (int) (adjustedMouseX / TILE_SIZE);
            int tileY = (int) (adjustedMouseY / TILE_SIZE); // Use adjustedTouchY from your input handling if you named it that

            // Ensure calculated tile coordinates are within the farm's bounds (0 to WIDTH/HEIGHT - 1)
            if (tileX >= 0 && tileX < FarmTemplate.WIDTH && tileY >= 0 && tileY < FarmTemplate.HEIGHT) {
                // Get the actual Tile object using the inverted Y for array lookup
                // This needs to match how gamePlayController.attemptToPlaceBuilding fetches the tile
                int actualTileYForLookup = FarmTemplate.HEIGHT - 1 - tileY; // Invert to array index

                Tile targetTile;
                if (inVillage) {
                    // For village, you might use a different HEIGHT constant if it's not FarmTemplate.HEIGHT
                    // And ensure your getTile method expects the inverted Y for village as well.
                    targetTile = gameMap.getVillage().getTile(tileX, 20 - 1 - tileY); // Adjust 20 to your Village height
                } else {
                    targetTile = gameMap.getFarm(currentFarmIndex).getTile(tileX, actualTileYForLookup);
                }

                if (targetTile != null) {
                    // Determine highlight color based on buildability, mirroring GamePlayController's logic
                    Color highlightColor;
                    if (targetTile.isAvailableForBuilding()) { // Use the method from Tile.java
                        highlightColor = Color.GREEN; // Buildable
                    } else {
                        highlightColor = Color.RED;   // Not buildable/occupied
                    }

                    // Calculate the world coordinates for drawing the highlight rectangle
                    // This uses the raw tileX and tileY (from adjustedMouse) because that's how renderMap draws them:
                    // renderOffset.x + (tileX * TILE_SIZE), renderOffset.y + (tileY * TILE_SIZE)
                    float drawX = renderOffset.x + (tileX * TILE_SIZE);
                    float drawY = renderOffset.y + (tileY * TILE_SIZE);

                    // Start drawing shapes
                    shapeRenderer.setProjectionMatrix(camera.combined); // Use camera's matrix for world coordinates
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(highlightColor.r, highlightColor.g, highlightColor.b, 0.5f); // Semi-transparent color

                    // Draw the rectangle over the tile
                    shapeRenderer.rect(drawX, drawY, TILE_SIZE, TILE_SIZE);

                    shapeRenderer.end(); // End drawing shapes
                }
            }
        }
    }
    //    private void drawArtisanProductionProgress() {
//        Farm farm = gamePlayController.getUser().getFarm();
//        if (farm == null) return;
//
//        // Set projection matrix for ShapeRenderer to match camera view
//        shapeRenderer.setProjectionMatrix(camera.combined);
//
//        // Enable blending for transparent bars
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//
//        int currentDay = TimeSystem.getInstance().getCurrentDay();
//        int currentHour = TimeSystem.getInstance().getCurrentHour();
//        float currentAbsHours = (float)currentDay * 24 + currentHour;
//
//        for (int y = 0; y < farm.getHeight(); y++) {
//            for (int x = 0; x < farm.getWidth(); x++) {
//                Tile tile = farm.getTiles()[y][x];
//                // Check if the tile has a static element and if it's a PlaceableGameBuilding
//                if (tile.getStaticElement().isPresent() && tile.getStaticElement().get() instanceof PlaceableGameBuilding) {
//                    PlaceableGameBuilding building = (PlaceableGameBuilding) tile.getStaticElement().get();
//
//                    // Check if it's an artisan building and currently producing
//                    if (gamePlayController.isArtisanBuilding(building.getName())) {
//                        if (gamePlayController.isArtisanProducing(building.getName())) {
//                            ProductionTask task = gamePlayController.getProductionTask(building.getName());
//                            if (task != null) {
//                                // Calculate total duration and elapsed time in absolute hours
//                                float startAbsHours = (float)task.getStartDayInitial() * 24 + task.getStartHourActual();
//                                float totalTaskHours = task.getTotalCraftingDurationHours();
//
//                                float elapsedHours = currentAbsHours - startAbsHours;
//
//                                // Cap progress at 1.0 (100%) and prevent negative progress
//                                float progress = 0.0f;
//                                if (totalTaskHours > 0) {
//                                    progress = Math.min(1.0f, Math.max(0.0f, elapsedHours / totalTaskHours));
//                                }
//
//                                // Get building's screen position relative to camera and renderOffset
//                                // TILE_SIZE and renderOffset must be accessible instance fields in MapView
//                                float buildingScreenX = (building.getX() * TILE_SIZE) + renderOffset.x;
//                                float buildingScreenY = (building.getY() * TILE_SIZE) + renderOffset.y;
//
//                                // Position and size for the progress bar
//                                float barWidth = TILE_SIZE * 0.9f; // Slightly less than tile width
//                                float barHeight = TILE_SIZE * 0.15f; // A bit thicker bar
//                                float barX = buildingScreenX + (TILE_SIZE - barWidth) / 2f; // Centered horizontally above building
//                                float barY = buildingScreenY + TILE_SIZE + 5; // 5 pixels above the top of the building's tile
//
//                                // Draw background bar (dark grey)
//                                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//                                shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.7f); // Dark grey, semi-transparent
//                                shapeRenderer.rect(barX, barY, barWidth, barHeight);
//
//                                // Draw foreground bar (green)
//                                shapeRenderer.setColor(0.1f, 0.8f, 0.1f, 0.9f); // Bright green, more opaque
//                                shapeRenderer.rect(barX, barY, barWidth * progress, barHeight);
//                                shapeRenderer.end();
//
//                                // Optional: Draw percentage text (requires 'font' and 'batch' fields to be initialized)
//                                if (font != null && batch != null) {
//                                    batch.setProjectionMatrix(camera.combined); // Ensure batch uses the same camera matrix
//                                    font.draw(batch, (int)(progress * 100) + "%",
//                                        barX + barWidth / 2f, barY + barHeight + font.getCapHeight() + 2, // Position text above the bar, centered
//                                        0, Align.center, false); // Align.center for horizontal centering
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        Gdx.gl.glDisable(GL20.GL_BLEND); // Disable blending after drawing all bars
//    }
// This method should be called between batch.begin() and batch.end()
    private void drawArtisanProductionProgressTextOnly() {
        Farm farm = gamePlayController.getUser().getFarm();
        if (farm == null || font == null || batch == null) return;

        int currentDay = TimeSystem.getInstance().getCurrentDay();
        int currentHour = TimeSystem.getInstance().getCurrentHour();
        float currentAbsHours = (float)currentDay * 24 + currentHour;

        for (int y = 0; y < farm.getHeight(); y++) {
            for (int x = 0; x < farm.getWidth(); x++) {
                Tile tile = farm.getTiles()[y][x];
                if (tile.getStaticElement().isPresent() && tile.getStaticElement().get() instanceof PlaceableGameBuilding) {
                    PlaceableGameBuilding building = (PlaceableGameBuilding) tile.getStaticElement().get();

                    if (gamePlayController.isArtisanBuilding(building.getName())) {
                        if (gamePlayController.isArtisanProducing(building.getName())) {
                            ProductionTask task = gamePlayController.getProductionTask(building.getName());
                            if (task != null) {
                                float startAbsHours = (float)task.getStartDayInitial() * 24 + task.getStartHourActual();
                                float totalTaskHours = task.getTotalCraftingDurationHours();
                                float elapsedHours = currentAbsHours - startAbsHours;
                                float progress = 0.0f;
                                if (totalTaskHours > 0) {
                                    progress = Math.min(1.0f, Math.max(0.0f, elapsedHours / totalTaskHours));
                                }

                                float buildingScreenX = (building.getX() * TILE_SIZE) + renderOffset.x;
                                float buildingScreenY = (building.getY() * TILE_SIZE) + renderOffset.y;

                                float barWidth = TILE_SIZE * 0.9f;
                                float barHeight = TILE_SIZE * 0.15f;
                                float barX = buildingScreenX + (TILE_SIZE - barWidth) / 2f;
                                float barY = buildingScreenY + TILE_SIZE + 5;

                                font.draw(batch, (int)(progress * 100) + "%",
                                    barX + barWidth / 2f, barY + barHeight + font.getCapHeight() + 2,
                                    0, Align.center, false);
                            }
                        }
                    }
                }
            }
        }
    }
    private void renderBuildingPlacementHighlightShapesOnly() {
        if (gamePlayController.isInBuildMode() && buildingSelectBox != null) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            Vector3 worldCoordinates = camera.unproject(new Vector3(mouseX, mouseY, 0));

            // Apply renderOffset to the mouse coordinates to get map-relative coordinates
            float adjustedMouseX = worldCoordinates.x - renderOffset.x;
            float adjustedMouseY = worldCoordinates.y - renderOffset.y;

            int highlightX = (int) (adjustedMouseX / TILE_SIZE);
            int highlightY = (int) (adjustedMouseY / TILE_SIZE);

            Farm farm = gamePlayController.getUser().getFarm();

            if (farm != null && farm.getTiles() != null) {
                if (highlightX >= 0 && highlightX < farm.getWidth() &&
                    highlightY >= 0 && highlightY < farm.getHeight()) {

                    Tile tile = farm.getTiles()[highlightY][highlightX];
                    Color highlightColor;

                    if (tile.isEmpty() && tile.getStaticElement().isEmpty() && tile.getRandomElement().isEmpty()) {
                        highlightColor = Color.GREEN;
                    } else {
                        highlightColor = Color.RED;
                    }

                    shapeRenderer.setColor(highlightColor.r, highlightColor.g, highlightColor.b, 0.5f);
                    // Apply renderOffset to the drawing position
                    shapeRenderer.rect(highlightX * TILE_SIZE + renderOffset.x, highlightY * TILE_SIZE + renderOffset.y, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }
    private void drawArtisanProductionProgressShapesOnly() {
        Farm farm = gamePlayController.getUser().getFarm();
        if (farm == null) return;

        int currentDay = TimeSystem.getInstance().getCurrentDay();
        int currentHour = TimeSystem.getInstance().getCurrentHour();
        float currentAbsHours = (float)currentDay * 24 + currentHour;

        for (int y = 0; y < farm.getHeight(); y++) {
            for (int x = 0; x < farm.getWidth(); x++) {
                Tile tile = farm.getTiles()[y][x];
                if (tile.getStaticElement().isPresent() && tile.getStaticElement().get() instanceof PlaceableGameBuilding) {
                    PlaceableGameBuilding building = (PlaceableGameBuilding) tile.getStaticElement().get();

                    if (gamePlayController.isArtisanBuilding(building.getName())) {
                        if (gamePlayController.isArtisanProducing(building.getName())) {
                            ProductionTask task = gamePlayController.getProductionTask(building.getName());
                            if (task != null) {
                                float startAbsHours = (float)task.getStartDayInitial() * 24 + task.getStartHourActual();
                                float totalTaskHours = task.getTotalCraftingDurationHours();
                                float elapsedHours = currentAbsHours - startAbsHours;
                                float progress = 0.0f;
                                if (totalTaskHours > 0) {
                                    progress = Math.min(1.0f, Math.max(0.0f, elapsedHours / totalTaskHours));
                                }

                                float buildingScreenX = (building.getX() * TILE_SIZE) + renderOffset.x;
                                float buildingScreenY = (building.getY() * TILE_SIZE) + renderOffset.y;

                                float barWidth = TILE_SIZE * 0.9f;
                                float barHeight = TILE_SIZE * 0.15f;
                                float barX = buildingScreenX + (TILE_SIZE - barWidth) / 2f;
                                float barY = buildingScreenY + TILE_SIZE + 5;

                                shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.7f);
                                shapeRenderer.rect(barX, barY, barWidth, barHeight);

                                shapeRenderer.setColor(0.1f, 0.8f, 0.1f, 0.9f);
                                shapeRenderer.rect(barX, barY, barWidth * progress, barHeight);
                            }
                        }
                    }
                }
            }
        }
    }
    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        for (Texture t : quickItemTextureCache.values()) {
            if (t != null) t.dispose();
        }
        quickItemTextureCache.clear();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (fallbackTexture != null) fallbackTexture.dispose();
        if (stage != null) stage.dispose();
        if (panelTexture != null) panelTexture.dispose();
        if (lastFrameTexture != null) lastFrameTexture.dispose();
        if (lastFramePixmap != null) lastFramePixmap.dispose();
        if (nightFogTexture != null) nightFogTexture.dispose();
        if (nightFogBatch != null) nightFogBatch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }

}
