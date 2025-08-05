package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.LobbyController;
import com.StardewValley.models.Lobby;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.StardewValley.Network.Client.ClientMain;
import com.StardewValley.Network.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapSelectionView implements Screen {
    private final Game game;
    private final LobbyController lobbyController;
    private final Stage stage;
    private final MenuManager menuManager;
    private final SpriteBatch batch;
    private final Lobby lobby;
    private final Map<Integer, Button> mapButtons = new HashMap<>();
    private final Map<Integer, String> mapSelections = new HashMap<>(); // map index -> username
    private Label statusLabel;

    public MapSelectionView(Game game, LobbyController lobbyController, Lobby lobby) {
        this.game = game;
        this.lobbyController = lobbyController;
        this.lobby = lobby;
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.batch = new SpriteBatch();
        createUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        Label titleLabel = new Label("Select Your Farm", menuManager.getPixthulhuSkin(), "title");
        statusLabel = new Label("Waiting for other players to select their map...", menuManager.getPixthulhuSkin());

        Table mapsTable = new Table();
        for (int i = 1; i <= 4; i++) {
            Button mapButton = new TextButton("Farm " + i, menuManager.getPixthulhuSkin());
            final int mapIndex = i;
            mapButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    selectMap(mapIndex);
                }
            });
            mapButtons.put(i, mapButton);
            mapsTable.add(mapButton).width(200).height(200).pad(20);
        }

        root.add(titleLabel).padBottom(40).row();
        root.add(mapsTable).padBottom(20).row();
        root.add(statusLabel).padBottom(20).row();

        // Add Start Game button if user is the lobby admin
        if (lobbyController.getCurrentUsername().equals(lobby.getAdmin())) {
            TextButton startGameButton = new TextButton("Start Game", menuManager.getPixthulhuSkin());
            startGameButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // Send request to server to start the game
                    lobbyController.startGame(lobby.getId());
                }
            });
            root.add(startGameButton).width(200).height(60).padBottom(20).row();
        }

        stage.addActor(root);
    }

    private void selectMap(int mapIndex) {
        // Send selection to server
        Map<String, Object> payload = new HashMap<>();
        payload.put("lobbyId", lobby.getId());
        payload.put("mapIndex", mapIndex);
        Message message = new Message(Message.ActionType.SELECT_MAP, payload);
        ClientMain.sendMessage(message);
    }

    public void updateMapSelections(Map<String, Integer> selections) {
        mapSelections.clear();
        for(Map.Entry<String, Integer> entry : selections.entrySet()){
            mapSelections.put(entry.getValue(), entry.getKey());
        }

        for (int i = 1; i <= 4; i++) {
            Button button = mapButtons.get(i);
            if (mapSelections.containsKey(i)) {
                button.setDisabled(true);
                ((TextButton)button).setText("Farm " + i + "\n(Taken by " + mapSelections.get(i) + ")");
                button.setColor(Color.RED);
            } else {
                button.setDisabled(false);
                ((TextButton)button).setText("Farm " + i);
                button.setColor(Color.GREEN);
            }
        }
    }

    public void allPlayersReady() {
        statusLabel.setText("All players have selected a map! Starting game...");
        // Here you would transition to the actual game view, probably triggered by a server message
    }


    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Ensure background is drawn first
        menuManager.updateBackgroundAnimation(delta);
        float[] positions = menuManager.getBackgroundPositions();
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        batch.begin();
        // Draw background layers
        for (float position : positions) {
            batch.draw(menuManager.getBackgroundLayer(), position, 0, width, height);
        }
        // Draw middleground layer
        batch.draw(menuManager.getMiddlegroundLayer(), 0, 0, width, height);
        batch.end();

        // Draw UI elements
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }
}
