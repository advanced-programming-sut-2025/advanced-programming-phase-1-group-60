package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.LobbyController;
import com.StardewValley.models.Lobby;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Import SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class InLobbyView implements Screen {
    private final Game game;
    private final LobbyController lobbyController;
    private Stage stage;
    private MenuManager menuManager;
    private SpriteBatch batch; // Add SpriteBatch
    private Lobby currentLobby;
    private Label lobbyInfoLabel;
    private Table playerListTable;

    public InLobbyView(Game game, LobbyController lobbyController, Lobby lobby) {
        this.game = game;
        this.lobbyController = lobbyController;
        this.currentLobby = lobby;
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.batch = new SpriteBatch();
        createUI();
        updateLobbyState(lobby);
        Gdx.input.setInputProcessor(stage);
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.pad(20);

        lobbyInfoLabel = new Label("", menuManager.getPixthulhuSkin(), "title");

        playerListTable = new Table();
        ScrollPane scrollPane = new ScrollPane(playerListTable, menuManager.getPixthulhuSkin());

        TextButton startGameBtn = new TextButton("Start Game", menuManager.getPixthulhuSkin());
        TextButton leaveBtn = new TextButton("Leave Lobby", menuManager.getPixthulhuSkin());

        // FIX: Add Refresh button
        TextButton refreshBtn = new TextButton("Refresh", menuManager.getPixthulhuSkin());

        root.add(lobbyInfoLabel).padBottom(20).row();
        root.add(scrollPane).expand().fill().pad(10).row();

        Table buttonTable = new Table();
        if (lobbyController.getCurrentUsername().equals(currentLobby.getAdmin())) {
            buttonTable.add(startGameBtn).pad(10);
        }
        buttonTable.add(leaveBtn).pad(10);
        buttonTable.add(refreshBtn).pad(10); // Add refresh button to the table
        root.add(buttonTable);

        stage.addActor(root);

        leaveBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                lobbyController.leaveLobby(currentLobby.getId(), lobbyController.getCurrentUsername());
                game.setScreen(new LobbyChoiceView(game, lobbyController));
            }
        });

        startGameBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // This is the changed part
                lobbyController.startGame(currentLobby.getId());
            }
        });

        // FIX: Add listener for the refresh button
        refreshBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                refreshLobbyState();
            }
        });

        refreshLobbyState();
    }

    private void refreshLobbyState() {
        this.currentLobby = lobbyController.getLobbyForUser(lobbyController.getCurrentUsername());

        if (this.currentLobby == null) {
            showLobbyClosedDialog();
            return;
        }

        lobbyInfoLabel.setText("Lobby: " + currentLobby.getName() + " (ID: " + currentLobby.getId() + ")");
        playerListTable.clear();

        for(String member : currentLobby.getMembers()) {
            String labelText = member;
            if (member.equals(currentLobby.getAdmin())) {
                labelText += " (Admin)";
            }
            playerListTable.add(new Label(labelText, menuManager.getPixthulhuSkin())).pad(5).row();
        }
    }

    public void updateLobbyState(Lobby newLobbyState) {
        if (newLobbyState == null) {
            showLobbyClosedDialog();
            return;
        }

        this.currentLobby = newLobbyState;

        lobbyInfoLabel.setText("Lobby: " + currentLobby.getName() + " (ID: " + currentLobby.getId() + ")");
        playerListTable.clear();

        for(String member : currentLobby.getMembers()) {
            String labelText = member;
            if (member.equals(currentLobby.getAdmin())) {
                labelText += " (Admin)";
            }
            playerListTable.add(new Label(labelText, menuManager.getPixthulhuSkin())).pad(5).row();
        }
    }

    private void showLobbyClosedDialog() {
        Dialog dialog = new Dialog("Lobby Closed", menuManager.getPixthulhuSkin());
        dialog.text("This lobby is no longer available.");
        dialog.button("OK");
        dialog.show(stage);
        dialog.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new LobbyChoiceView(game, lobbyController));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // FIX: Add background rendering
        menuManager.updateBackgroundAnimation(delta);
        float[] positions = menuManager.getBackgroundPositions();
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        batch.begin();
        for (float position : positions) {
            batch.draw(menuManager.getBackgroundLayer(), position, 0, width, height);
        }
        batch.draw(menuManager.getMiddlegroundLayer(), 0, 0, width, height);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {}
    @Override
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }
}
