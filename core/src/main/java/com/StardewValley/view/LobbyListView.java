package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.LobbyController;
import com.StardewValley.models.Lobby;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.List;

public class LobbyListView implements Screen {
    private final Game game;
    private final LobbyController lobbyController;
    private final Stage stage;
    private final MenuManager menuManager;
    private final SpriteBatch batch;
    private Table lobbyListTable;

    public LobbyListView(Game game, LobbyController lobbyController) {
        this.game = game;
        this.lobbyController = lobbyController;
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.batch = new SpriteBatch();

        createUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);

        Label titleLabel = new Label("Join a Lobby", menuManager.getPixthulhuSkin(), "title");

        lobbyListTable = new Table(menuManager.getPixthulhuSkin());
        ScrollPane scrollPane = new ScrollPane(lobbyListTable, menuManager.getPixthulhuSkin());

        TextButton joinByIdBtn = new TextButton("Join by ID", menuManager.getPixthulhuSkin());
        TextButton refreshBtn = new TextButton("Refresh", menuManager.getPixthulhuSkin());
        TextButton backBtn = new TextButton("Back", menuManager.getPixthulhuSkin());

        root.add(titleLabel).pad(20).row();
        root.add(scrollPane).expand().fill().pad(10).row();

        Table buttonTable = new Table();
        buttonTable.add(joinByIdBtn).pad(10);
        buttonTable.add(refreshBtn).pad(10);
        buttonTable.add(backBtn).pad(10);
        root.add(buttonTable).pad(10);

        stage.addActor(root);

        joinByIdBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showJoinByIdDialog();
            }
        });

        refreshBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                refreshLobbyList();
            }
        });

        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new LobbyChoiceView(game, lobbyController));
            }
        });

        refreshLobbyList();
    }

    private void refreshLobbyList() {
        lobbyListTable.clear();
        List<Lobby> lobbies = lobbyController.getLobbies();

        boolean hasVisibleLobbies = false;
        for (Lobby lobby : lobbies) {
            if (!lobby.isVisible()) continue;
            hasVisibleLobbies = true;

            String info = String.format("%s (%d/%d) - Admin: %s - %s",
                lobby.getName(), lobby.getMembers().size(), lobby.getCapacity(), lobby.getAdmin(),
                lobby.isPublic() ? "Public" : "Private");

            Label lobbyLabel = new Label(info, menuManager.getPixthulhuSkin());
            TextButton joinBtn = new TextButton("Join", menuManager.getPixthulhuSkin());

            joinBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    handleJoin(lobby);
                }
            });

            lobbyListTable.add(lobbyLabel).expandX().left().pad(5);
            lobbyListTable.add(joinBtn).pad(5).row();
        }

        if (!hasVisibleLobbies) {
            lobbyListTable.add(new Label("No visible lobbies found.", menuManager.getPixthulhuSkin()));
        }
    }

    private void handleJoin(Lobby lobby) {
        if (lobby.isPublic()) {
            lobbyController.joinLobby(lobby.getId(), null, lobbyController.getCurrentUsername());
        } else {
            showPasswordDialog(lobby);
        }
    }

    private void showJoinByIdDialog() {
        Dialog dialog = new Dialog("Join by ID", menuManager.getPixthulhuSkin());
        TextField idField = new TextField("", menuManager.getPixthulhuSkin());
        idField.setMessageText("Enter Lobby ID");
        dialog.getContentTable().add(idField).width(300).pad(10).row();

        TextButton findBtn = new TextButton("Find and Join", menuManager.getPixthulhuSkin());
        dialog.button(findBtn);
        dialog.button("Cancel");

        findBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Lobby lobby = lobbyController.getLobbyForUser(idField.getText());
                if (lobby != null) {
                    dialog.hide();
                    handleJoin(lobby);
                } else {
                    showErrorDialog("Lobby with that ID not found.");
                }
            }
        });
        dialog.show(stage);
    }

    private void showPasswordDialog(Lobby lobby) {
        Dialog dialog = new Dialog("Enter Password", menuManager.getPixthulhuSkin());
        TextField passwordField = new TextField("", menuManager.getPixthulhuSkin());
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        dialog.getContentTable().add(passwordField).width(250).pad(10);

        TextButton joinBtn = new TextButton("Join", menuManager.getPixthulhuSkin());
        dialog.button(joinBtn);
        dialog.button("Cancel");

        joinBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Send join request to server. Server will handle success/failure response.
                lobbyController.joinLobby(lobby.getId(), passwordField.getText(), lobbyController.getCurrentUsername());
                dialog.hide(); // Hide the dialog immediately after sending the request
            }
        });

        dialog.show(stage);
    }

    private void showErrorDialog(String message) {
        Dialog errorDialog = new Dialog("Error", menuManager.getPixthulhuSkin());
        errorDialog.text(message);
        errorDialog.button("OK");
        errorDialog.show(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Add background rendering
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
    public void show() { Gdx.input.setInputProcessor(stage); }
    @Override
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }
}
