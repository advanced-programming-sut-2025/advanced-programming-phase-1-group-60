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
    // Map of farm index to owner name
    private Map<Integer, String> farmOwners = new HashMap<>();

    // Fallback textures
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

        // Camera setup
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.zoom = DEFAULT_ZOOM;
        // Get map manager
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

        // Initialize farm owner mapping
        initializeFarmOwnerMap();

        // Initialize player at farm 0 center
        Vector2 farmCenter = getFarmCenter(0);
        this.playerPos = new Vector2(farmCenter);

        // Center camera on player
        centerCameraOnPlayer();
    }

    private void initializeFarmOwnerMap() {
        try {
            // Get all players from the game instance
            List<User> players = gameInstance.getPlayers();

            for (User player : players) {
                // Get the map selection for this player (1-4)
                int mapId = gameInstance.getSelectedMaps().get(player);

                if (mapId > 0) {
                    // Convert to 0-based index
                    int farmIndex = mapId - 1;
                    farmOwners.put(farmIndex, player.getUsername());
                }
            }

            // Debug output
            System.out.println("Farm ownership mapping:");
            for (Map.Entry<Integer, String> entry : farmOwners.entrySet()) {
                System.out.println("Farm " + (entry.getKey() + 1) + " owned by: " + entry.getValue());
            }
        } catch (Exception e) {
            System.err.println("Error initializing farm owners: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Vector2 getFarmCenter(int farmIndex) {
        if (gameMap == null || farmIndex < 0 || farmIndex > 3) {
            return new Vector2(400, 300);
        }

        int farmWidth = FarmTemplate.WIDTH;
        int farmHeight = FarmTemplate.HEIGHT;
        int vilW = gameMap.getVilW();
        int vilH = gameMap.getVilH();

        float centerX, centerY;

        switch (farmIndex) {
            case 0: // Top-left
                centerX = farmWidth * TILE_SIZE / 2;
                centerY = farmHeight * TILE_SIZE / 2;
                break;
            case 1: // Top-right
                centerX = (farmWidth + vilW) * TILE_SIZE + farmWidth * TILE_SIZE / 2;
                centerY = farmHeight * TILE_SIZE / 2;
                break;
            case 2: // Bottom-left
                centerX = farmWidth * TILE_SIZE / 2;
                centerY = (farmHeight + vilH) * TILE_SIZE + farmHeight * TILE_SIZE / 2;
                break;
            case 3: // Bottom-right
                centerX = (farmWidth + vilW) * TILE_SIZE + farmWidth * TILE_SIZE / 2;
                centerY = (farmHeight + vilH) * TILE_SIZE + farmHeight * TILE_SIZE / 2;
                break;
            default:
                centerX = farmWidth * TILE_SIZE / 2;
                centerY = farmHeight * TILE_SIZE / 2;
        }

        return new Vector2(centerX, centerY);
    }

    private void centerCameraOnPlayer() {
        camera.position.set(playerPos.x, playerPos.y, 0);
        camera.update();
    }

    public void setCurrentFarmIndex(int index) {
        if (index >= 0 && index <= 3) {
            currentFarmIndex = index;
            gameMap.setActiveFarm(index);

            // Move player to center of selected farm
            Vector2 farmCenter = getFarmCenter(index);
            playerPos.set(farmCenter);

            // Center camera on player
            centerCameraOnPlayer();
        }
    }

    // No-parameter render method for GameView
    public void render() {
        render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Process input
        handleInput(delta);

        // Center camera on player
        centerCameraOnPlayer();

        // Set batch to use camera
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Get current farm boundaries
        Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
        int farmWidth = FarmTemplate.WIDTH;
        int farmHeight = FarmTemplate.HEIGHT;

        // Draw the farm tiles
        for (int y = 0; y < farmHeight; y++) {
            for (int x = 0; x < farmWidth; x++) {
                float posX = farmTopLeft.x + x * TILE_SIZE;
                float posY = farmTopLeft.y + y * TILE_SIZE;

                // World coordinates for tile lookup
                int tileX = (int)(farmTopLeft.x / TILE_SIZE) + x;
                int tileY = (int)(farmTopLeft.y / TILE_SIZE) + y;

                // Always draw grass texture
                Texture grassTexture = mapManager.getGrassTile();
                if (grassTexture != null) {
                    batch.draw(grassTexture, posX, posY, TILE_SIZE, TILE_SIZE);
                } else {
                    batch.draw(fallbackTexture, posX, posY, TILE_SIZE, TILE_SIZE);
                }

                // Inside the render method in MapView
                try {
                    Tile tile = gameMap.getTile(tileX, tileY);
                    if (tile != null) {
                        // First draw the grass base
                        batch.draw(mapManager.getGrassTile(), posX, posY, TILE_SIZE, TILE_SIZE);

                        // Check for static elements
                        Optional<StaticElement> staticElement = tile.getStaticElement();
                        if (staticElement.isPresent()) {
                            batch.setColor(1, 0.8f, 0.8f, 1);
                            batch.draw(mapManager.getPlaceholderTile(), posX, posY, TILE_SIZE, TILE_SIZE);
                            batch.setColor(Color.WHITE);
                        }

                        // Check for random elements
                        Optional<RandomElement> randomElement = tile.getRandomElement();
                        if (randomElement.isPresent()) {
                            RandomElement element = randomElement.get();
                            if (element.symbol() == 'S') {
                                // Get the specific stone variant
                                Stone stone = (Stone) element;
                                batch.draw(mapManager.getStoneTile(stone.getStoneVariant()), posX, posY, TILE_SIZE, TILE_SIZE);
                            } else {
                                // For other random elements, use placeholder
                                batch.setColor(0.8f, 1, 0.8f, 1);
                                batch.draw(mapManager.getPlaceholderTile(), posX, posY, TILE_SIZE, TILE_SIZE);
                                batch.setColor(Color.WHITE);
                            }
                        }
                    }
                } catch (Exception e) {
                    // Continue if error
                }
            }
        }

        // Draw player (centered on position)
        batch.draw(playerTexture, playerPos.x - TILE_SIZE/2, playerPos.y - TILE_SIZE/2, TILE_SIZE, TILE_SIZE);

        // Draw UI text that follows the camera
        float textX = camera.position.x - camera.viewportWidth/2 + 10;
        float textY = camera.position.y + camera.viewportHeight/2 - 10;

        // Display farm number and owner
        String ownerName = farmOwners.getOrDefault(currentFarmIndex, "Unknown");
        font.draw(batch, "Farm " + (currentFarmIndex + 1) + " - Owner: " + ownerName, textX, textY);
        font.draw(batch, "WASD to move", textX, textY - 20);
        font.draw(batch, "Press 1-4 to switch farms", textX, textY - 40);

        batch.end();
    }

    private Vector2 getFarmTopLeft(int farmIndex) {
        int farmWidth = FarmTemplate.WIDTH;
        int farmHeight = FarmTemplate.HEIGHT;
        int vilW = gameMap.getVilW();
        int vilH = gameMap.getVilH();

        float x, y;

        switch (farmIndex) {
            case 0: // Top-left
                x = 0;
                y = 0;
                break;
            case 1: // Top-right
                x = (farmWidth + vilW) * TILE_SIZE;
                y = 0;
                break;
            case 2: // Bottom-left
                x = 0;
                y = (farmHeight + vilH) * TILE_SIZE;
                break;
            case 3: // Bottom-right
                x = (farmWidth + vilW) * TILE_SIZE;
                y = (farmHeight + vilH) * TILE_SIZE;
                break;
            default:
                x = 0;
                y = 0;
        }

        return new Vector2(x, y);
    }

    private void handleInput(float delta) {
        float speed = 200 * delta;
        Vector2 newPos = new Vector2(playerPos);

        // Store the original position for collision checking
        Vector2 originalPos = new Vector2(playerPos);

        // Try moving in each direction individually
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A))
            newPos.x -= speed;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D))
            newPos.x += speed;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W))
            newPos.y += speed;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S))
            newPos.y -= speed;

        // Handle zoom separately
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.PLUS) ||
            Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.EQUALS)) {
            camera.zoom -= 0.02f;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.MINUS)) {
            camera.zoom += 0.02f;
        }

        // Clamp zoom to reasonable values
        camera.zoom = Math.max(0.3f, Math.min(2f, camera.zoom));

        // Check farm boundaries
        Vector2 farmTopLeft = getFarmTopLeft(currentFarmIndex);
        int farmWidth = FarmTemplate.WIDTH;
        int farmHeight = FarmTemplate.HEIGHT;

        float minX = farmTopLeft.x + TILE_SIZE/2;
        float maxX = farmTopLeft.x + farmWidth * TILE_SIZE - TILE_SIZE/2;
        float minY = farmTopLeft.y + TILE_SIZE/2;
        float maxY = farmTopLeft.y + farmHeight * TILE_SIZE - TILE_SIZE/2;

        // Keep player within boundaries
        newPos.x = Math.max(minX, Math.min(maxX, newPos.x));
        newPos.y = Math.max(minY, Math.min(maxY, newPos.y));

        // Check if the new position is on a passable tile
        if (!isTilePassable(newPos.x, newPos.y)) {
            // If not passable, revert to original position
            newPos.set(originalPos);
        }

        playerPos.set(newPos);

        // Farm switching code (unchanged)
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
        // Convert world coordinates to tile coordinates
        int tileX = (int)(worldX / TILE_SIZE);
        int tileY = (int)(worldY / TILE_SIZE);

        try {
            Tile tile = gameMap.getTile(tileX, tileY);
            if (tile == null) return false;

            // Check static elements
            Optional<StaticElement> staticElement = tile.getStaticElement();
            if (staticElement.isPresent() && !staticElement.get().isPassable()) {
                return false;
            }

            // Check random elements
            Optional<RandomElement> randomElement = tile.getRandomElement();
            if (randomElement.isPresent() && !randomElement.get().isPassable()) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false; // If there's an error, don't allow passage
        }
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
