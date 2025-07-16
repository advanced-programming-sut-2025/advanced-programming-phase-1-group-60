package com.StardewValley.view;

import com.StardewValley.AssetsManager.MapManager;
import com.StardewValley.models.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MapView implements Screen {
    private final GameMap gameMap;
    private final Game gameInstance;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private MapManager mapManager;
    private static final float DEFAULT_ZOOM = 0.6f;
    private Map<Integer, String> farmOwners = new HashMap<>();

    private Texture fallbackTexture;
    private Texture playerTexture;
    private Vector2 playerPos;

    private static final float TILE_SIZE = 32f;
    private int currentFarmIndex = 0;

    public MapView(GameMap gameMap) {
        this.gameMap = gameMap;
        this.gameInstance = Game.getInstance();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        font.setColor(Color.WHITE);

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = DEFAULT_ZOOM;
        this.mapManager = MapManager.getInstance();

        // Create fallback texture (green)
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        this.fallbackTexture = new Texture(pixmap);
        pixmap.dispose();

        // Create player texture (red)
        pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        this.playerTexture = new Texture(pixmap);
        pixmap.dispose();

        initializeFarmOwnerMap();

        Vector2 farmCenter = getFarmCenter(0);
        this.playerPos = new Vector2(farmCenter);
        centerCameraOnPlayer();
    }

    private void initializeFarmOwnerMap() {
        try {
            List<User> players = gameInstance.getPlayers();
            for (User player : players) {
                int mapId = gameInstance.getSelectedMaps().get(player);
                if (mapId > 0) {
                    int farmIndex = mapId - 1;
                    farmOwners.put(farmIndex, player.getUsername());
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing farm owners: " + e.getMessage());
        }
    }

    private Vector2 getFarmTopLeft(int farmIndex) {
        switch (farmIndex) {
            case 0: // Farm 1 - Top-left
                return new Vector2(0, 0);
            case 1: // Farm 2 - Top-right
                return new Vector2(70 * TILE_SIZE, 0);
            case 2: // Farm 3 - Bottom-left
                return new Vector2(0, 70 * TILE_SIZE);
            case 3: // Farm 4 - Bottom-right
                return new Vector2(70 * TILE_SIZE, 70 * TILE_SIZE);
            default:
                return new Vector2(0, 0);
        }
    }

    private Vector2 getFarmCenter(int farmIndex) {
        Vector2 farmTopLeft = getFarmTopLeft(farmIndex);
        float centerX = farmTopLeft.x + (FarmTemplate.WIDTH * TILE_SIZE) / 2;
        float centerY = farmTopLeft.y + (FarmTemplate.HEIGHT * TILE_SIZE) / 2;
        return new Vector2(centerX, centerY);
    }

    private void centerCameraOnPlayer() {
        camera.position.set(playerPos.x, playerPos.y, 0);
        camera.update();
    }

    public void setCurrentFarmIndex(int index) {
        if (index >= 0 && index <= 3) {
            System.out.println("Switching to farm " + (index + 1));
            currentFarmIndex = index;
            gameMap.setActiveFarm(index);

            Vector2 farmCenter = getFarmCenter(index);
            playerPos.set(farmCenter);
            centerCameraOnPlayer();
        }
    }

    public void render() {
        render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);
        centerCameraOnPlayer();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
        int farmWidth = FarmTemplate.WIDTH;
        int farmHeight = FarmTemplate.HEIGHT;

        // FIRST PASS: Draw grass base
        for (int y = 0; y < farmHeight; y++) {
            for (int x = 0; x < farmWidth; x++) {
                float posX = farmTopLeft.x + x * TILE_SIZE;
                float posY = farmTopLeft.y + (farmHeight - 1 - y) * TILE_SIZE;

                Texture grassTexture = mapManager.getGrassTile();
                if (grassTexture != null) {
                    batch.draw(grassTexture, posX, posY, TILE_SIZE, TILE_SIZE);
                } else {
                    batch.draw(fallbackTexture, posX, posY, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // SECOND PASS: Draw structures LAST so they appear on top
        drawStructures(farmTopLeft);


        // THIRD PASS: Draw tall elements (trees)
        drawTallElements(farmTopLeft, farmWidth, farmHeight);

        // FOURTH PASS: Draw ground elements (stones, foraging items)
        drawGroundElements(farmTopLeft, farmWidth, farmHeight);

        // Draw player
        batch.draw(playerTexture, playerPos.x - TILE_SIZE/2, playerPos.y - TILE_SIZE/2, TILE_SIZE, TILE_SIZE);

        // Draw UI
        float textX = camera.position.x - camera.viewportWidth/2 + 10;
        float textY = camera.position.y + camera.viewportHeight/2 - 10;
        String ownerName = farmOwners.getOrDefault(currentFarmIndex, "Unknown");
        font.draw(batch, "Farm " + (currentFarmIndex + 1) + " - Owner: " + ownerName, textX, textY);
        font.draw(batch, "WASD to move", textX, textY - 20);
        font.draw(batch, "Press 1-4 to switch farms", textX, textY - 40);

        batch.end();
    }

    private void drawStructures(Vector2 farmTopLeft) {
        // Draw template structures first
        FarmTemplate template = null;
        switch (currentFarmIndex) {
            case 0: template = FarmTemplate.template1(); break;
            case 1: template = FarmTemplate.template2(); break;
            case 2: template = FarmTemplate.template3(); break;
            case 3: template = FarmTemplate.template4(); break;
        }

        if (template != null) {
            for (FarmTemplate.Placement placement : template.getPlacements()) {
                float posX = farmTopLeft.x + placement.x * TILE_SIZE;
                float posY = farmTopLeft.y + (FarmTemplate.HEIGHT - placement.y - placement.h) * TILE_SIZE;
                float width = placement.w * TILE_SIZE;
                float height = placement.h * TILE_SIZE;

                StaticElement element = placement.element;

                if (element instanceof Cabin) {
                    batch.draw(mapManager.getCabinTexture(), posX, posY, width, height);
                }
                else if (element instanceof Greenhouse) {
                    batch.draw(mapManager.getGreenhouseTexture(), posX, posY, width, height);
                }
                else if (element instanceof Lake) {
                    for (int y = 0; y < placement.h; y++) {
                        for (int x = 0; x < placement.w; x++) {
                            batch.draw(mapManager.getWaterTexture(),
                                posX + x * TILE_SIZE,
                                posY + y * TILE_SIZE,
                                TILE_SIZE, TILE_SIZE);
                        }
                    }
                }
                else if (element instanceof Quarry) {
                    for (int y = 0; y < placement.h; y++) {
                        for (int x = 0; x < placement.w; x++) {
                            batch.draw(mapManager.getQuarryTexture(),
                                posX + x * TILE_SIZE,
                                posY + y * TILE_SIZE,
                                TILE_SIZE, TILE_SIZE);
                        }
                    }
                }
            }
        }

        // Draw static elements from tiles (foraging items)
        Farm currentFarm = gameMap.getFarm(currentFarmIndex);
        if (currentFarm != null) {
            for (int y = 0; y < FarmTemplate.HEIGHT; y++) {
                for (int x = 0; x < FarmTemplate.WIDTH; x++) {
                    float posX = farmTopLeft.x + x * TILE_SIZE;
                    float posY = farmTopLeft.y + (FarmTemplate.HEIGHT - 1 - y) * TILE_SIZE;

                    try {
                        Tile tile = currentFarm.getTile(x, y);
                        if (tile != null) {
                            Optional<StaticElement> staticElement = tile.getStaticElement();
                            if (staticElement.isPresent()) {
                                StaticElement element = staticElement.get();
                                if (element instanceof ForagingMineral) {
                                    ForagingMineral mineral = (ForagingMineral) element;
                                    batch.draw(mapManager.getForagingMineralTexture(mineral.getImagePath()),
                                        posX, posY, TILE_SIZE, TILE_SIZE);
                                } else if (element instanceof ForagingCrop) {
                                    ForagingCrop crop = (ForagingCrop) element;
                                    batch.draw(mapManager.getForagingCropTexture(crop.getImagePath()),
                                        posX, posY, TILE_SIZE, TILE_SIZE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Continue if error
                    }
                }
            }
        }
    }

    private void drawGroundElements(Vector2 farmTopLeft, int farmWidth, int farmHeight) {
        Farm currentFarm = gameMap.getFarm(currentFarmIndex);
        if (currentFarm != null) {
            for (int y = 0; y < farmHeight; y++) {
                for (int x = 0; x < farmWidth; x++) {
                    float posX = farmTopLeft.x + x * TILE_SIZE;
                    float posY = farmTopLeft.y + (farmHeight - 1 - y) * TILE_SIZE;

                    try {
                        Tile tile = currentFarm.getTile(x, y);
                        if (tile != null) {
                            Optional<RandomElement> randomElement = tile.getRandomElement();
                            if (randomElement.isPresent()) {
                                RandomElement element = randomElement.get();
                                if (element.symbol() == 'S') {
                                    Stone stone = (Stone) element;
                                    batch.draw(mapManager.getStoneTile(stone.getStoneVariant()),
                                        posX, posY, TILE_SIZE, TILE_SIZE);
                                }
                                else if (element instanceof ForagingMineral) {
                                    ForagingMineral mineral = (ForagingMineral) element;
                                    batch.draw(mapManager.getForagingMineralTexture(mineral.getImagePath()),
                                        posX, posY, TILE_SIZE, TILE_SIZE);
                                }
                                else if (element instanceof ForagingCrop) {
                                    ForagingCrop crop = (ForagingCrop) element;
                                    batch.draw(mapManager.getForagingCropTexture(crop.getImagePath()),
                                        posX, posY, TILE_SIZE, TILE_SIZE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Continue if error
                    }
                }
            }
        }
    }

    private void drawTallElements(Vector2 farmTopLeft, int farmWidth, int farmHeight) {
        Farm currentFarm = gameMap.getFarm(currentFarmIndex);
        if (currentFarm != null) {
            for (int y = 0; y < farmHeight; y++) {
                for (int x = 0; x < farmWidth; x++) {
                    float posX = farmTopLeft.x + x * TILE_SIZE;
                    float posY = farmTopLeft.y + (farmHeight - 1 - y) * TILE_SIZE;

                    try {
                        Tile tile = currentFarm.getTile(x, y);
                        if (tile != null) {
                            Optional<RandomElement> randomElement = tile.getRandomElement();
                            if (randomElement.isPresent()) {
                                RandomElement element = randomElement.get();
                                if (element.symbol() == 'T') {
                                    Tree tree = (Tree) element;
                                    drawTree(tree.getImagePath(), posX, posY);
                                }
                                else if (element instanceof ForagingTree) {
                                    ForagingTree foragingTree = (ForagingTree) element;
                                    drawForagingTree(foragingTree.getImagePath(), posX, posY);
                                }
                            }

                            Optional<StaticElement> staticElement = tile.getStaticElement();
                            if (staticElement.isPresent() && staticElement.get() instanceof ForagingTree) {
                                ForagingTree foragingTree = (ForagingTree) staticElement.get();
                                drawForagingTree(foragingTree.getImagePath(), posX, posY);
                            }
                        }
                    } catch (Exception e) {
                        // Continue if error
                    }
                }
            }
        }
    }
    private void handleInput(float delta) {
        float speed = 200 * delta;
        Vector2 newPos = new Vector2(playerPos);

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A))
            newPos.x -= speed;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D))
            newPos.x += speed;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W))
            newPos.y += speed;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S))
            newPos.y -= speed;

        // Handle zoom
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.PLUS) ||
            Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.EQUALS)) {
            camera.zoom -= 0.02f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.MINUS)) {
            camera.zoom += 0.02f;
        }
        camera.zoom = Math.max(0.3f, Math.min(2f, camera.zoom));

        // Check boundaries
        Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
        float minX = farmTopLeft.x + TILE_SIZE/2;
        float maxX = farmTopLeft.x + FarmTemplate.WIDTH * TILE_SIZE - TILE_SIZE/2;
        float minY = farmTopLeft.y + TILE_SIZE/2;
        float maxY = farmTopLeft.y + FarmTemplate.HEIGHT * TILE_SIZE - TILE_SIZE/2;

        newPos.x = Math.max(minX, Math.min(maxX, newPos.x));
        newPos.y = Math.max(minY, Math.min(maxY, newPos.y));

        // Check collision
        if (isTilePassable(newPos.x, newPos.y)) {
            playerPos.set(newPos);
        }

        // Farm switching
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1))
            setCurrentFarmIndex(0);
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_2))
            setCurrentFarmIndex(1);
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_3))
            setCurrentFarmIndex(2);
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_4))
            setCurrentFarmIndex(3);
    }

    private boolean isTilePassable(float worldX, float worldY) {
        try {
            Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
            int farmTileX = (int)((worldX - farmTopLeft.x) / TILE_SIZE);
            int farmTileY = FarmTemplate.HEIGHT - 1 - (int)((worldY - farmTopLeft.y) / TILE_SIZE);

            if (farmTileX < 0 || farmTileX >= FarmTemplate.WIDTH ||
                farmTileY < 0 || farmTileY >= FarmTemplate.HEIGHT) {
                return false;
            }

            // Check structures from template
            FarmTemplate template = null;
            switch (currentFarmIndex) {
                case 0: template = FarmTemplate.template1(); break;
                case 1: template = FarmTemplate.template2(); break;
                case 2: template = FarmTemplate.template3(); break;
                case 3: template = FarmTemplate.template4(); break;
            }

            if (template != null) {
                for (FarmTemplate.Placement placement : template.getPlacements()) {
                    if (farmTileX >= placement.x && farmTileX < placement.x + placement.w &&
                        farmTileY >= placement.y && farmTileY < placement.y + placement.h) {
                        return false;
                    }
                }
            }

            // Check for trees and stones collision using farm's local coordinates
            Farm currentFarm = gameMap.getFarm(currentFarmIndex);
            if (currentFarm != null) {
                try {
                    Tile tile = currentFarm.getTile(farmTileX, farmTileY);
                    if (tile != null) {
                        Optional<RandomElement> randomElement = tile.getRandomElement();
                        if (randomElement.isPresent()) {
                            RandomElement element = randomElement.get();
                            if (element.symbol() == 'T' || element.symbol() == 'S') {
                                return false;
                            }
                        }
                    }
                } catch (Exception e) {
                    // Continue if error
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void drawTree(String imagePath, float posX, float posY) {
        float widthMultiplier = 2f;
        float heightMultiplier = 3.0f;
        float width = TILE_SIZE * widthMultiplier;
        float height = TILE_SIZE * heightMultiplier;
        float adjustedX = posX - (width - TILE_SIZE) / 2;
        float adjustedY = posY;
        batch.draw(mapManager.getTreeTexture(imagePath), adjustedX, adjustedY, width, height);
    }

    private void drawForagingTree(String imagePath, float posX, float posY) {
        float widthMultiplier = 2f;
        float heightMultiplier = 3.0f;
        float width = TILE_SIZE * widthMultiplier;
        float height = TILE_SIZE * heightMultiplier;
        float adjustedX = posX - (width - TILE_SIZE) / 2;
        float adjustedY = posY;
        batch.draw(mapManager.getForagingTreeTexture(imagePath), adjustedX, adjustedY, width, height);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fallbackTexture.dispose();
        playerTexture.dispose();
    }
}
