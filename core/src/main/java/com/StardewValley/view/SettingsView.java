package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.models.Game;
import com.StardewValley.models.User;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.List;
import java.util.stream.Collectors;


public class SettingsView implements Screen {
    private final com.badlogic.gdx.Game game;
    private final Stage stage;
    private final MenuManager menuManager;
    private final GameView gameView;
    private final User currentPlayer;
    private final Game gameInstance;

    public SettingsView(com.badlogic.gdx.Game libgdxGame, GameView gameView, User currentPlayer) {
        this.game = libgdxGame;
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.gameView = gameView;
        this.currentPlayer = currentPlayer;
        this.gameInstance = Game.getInstance();
        createUI();
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        Label titleLabel = new Label("Settings", menuManager.getPixthulhuSkin(), "title");
        TextButton kickPlayerButton = new TextButton("Kick Player", menuManager.getPixthulhuSkin());
        TextButton exitGameButton = new TextButton("Exit Game", menuManager.getPixthulhuSkin());
        TextButton backButton = new TextButton("Back to Inventory", menuManager.getPixthulhuSkin());

        mainTable.add(titleLabel).padBottom(50).row();
        mainTable.add(kickPlayerButton).width(300).height(80).padBottom(20).row();
        mainTable.add(exitGameButton).width(300).height(80).padBottom(20).row();
        mainTable.add(backButton).width(300).height(80).padTop(30).row();

        kickPlayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showKickPlayerDialog();
            }
        });

        exitGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showExitConfirmationDialog();
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameView.showInventoryScreen();
            }
        });

        stage.addActor(mainTable);
    }
    private void showKickPlayerDialog() {
        Dialog kickDialog = new Dialog("Kick a Player", menuManager.getPixthulhuSkin());
        kickDialog.text("Select a player to vote kick:").pad(20);

        Table playerList = new Table();
        List<User> otherPlayers = gameInstance.getPlayers().stream()
            .filter(p -> !p.equals(currentPlayer))
            .collect(Collectors.toList());

        if (otherPlayers.isEmpty()) {
            kickDialog.text("No other players to kick.");
        } else {
            for (User player : otherPlayers) {
                TextButton playerButton = new TextButton(player.getUsername(), menuManager.getPixthulhuSkin());
                playerButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        String result = gameInstance.voteToKick(currentPlayer, player);
                        showResultDialog(result);
                        kickDialog.hide();
                    }
                });
                playerList.add(playerButton).width(200).pad(5).row();
            }
        }

        ScrollPane scrollPane = new ScrollPane(playerList, menuManager.getPixthulhuSkin());
        kickDialog.getContentTable().add(scrollPane).maxHeight(600).row();
        kickDialog.button("Cancel");
        kickDialog.show(stage);
    }
    private void showExitConfirmationDialog() {
        Dialog confirmDialog = new Dialog("Confirm Exit", menuManager.getPixthulhuSkin());
        confirmDialog.text("Are you sure you want to leave the game?").pad(20);
        TextButton yesButton = new TextButton("Yes", menuManager.getPixthulhuSkin());
        TextButton noButton = new TextButton("No", menuManager.getPixthulhuSkin());

        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.removePlayer(currentPlayer);
                gameView.showMainMenu();
            }
        });

        noButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                confirmDialog.hide();
            }
        });

        confirmDialog.getButtonTable().add(yesButton).pad(10);
        confirmDialog.getButtonTable().add(noButton).pad(10);
        confirmDialog.show(stage);
    }
    private void showResultDialog(String message) {
        Dialog dialog = new Dialog("Result", menuManager.getPixthulhuSkin());
        dialog.text(message).pad(20);
        dialog.button("OK");
        dialog.show(stage);
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
