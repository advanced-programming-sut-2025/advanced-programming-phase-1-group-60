package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.LobbyController;
import com.StardewValley.models.User;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.List;

public class TradeMenuView implements Screen {
    private final Game game;
    private final Stage stage;
    private final MenuManager menuManager;
    private final User player;
    private final GameView gameView;
    private final LobbyController lobbyController;

    private Table contentTable;

    public TradeMenuView(Game game, User player, GameView gameView, LobbyController lobbyController) {
        this.game = game;
        this.player = player;
        this.gameView = gameView;
        this.lobbyController = lobbyController;
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        createUI();
        com.StardewValley.Network.Client.ClientMain.sendMessage(new com.StardewValley.Network.Message(com.StardewValley.Network.Message.ActionType.GET_PLAYER_LIST, new java.util.HashMap<>()));
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new Image(new Texture(Gdx.files.internal("assets/Background/layers/background.png"))).getDrawable());

        Table sidebar = createSidebar();
        contentTable = new Table();
        ScrollPane scrollPane = new ScrollPane(contentTable, menuManager.getPixthulhuSkin());

        root.add(sidebar).width(250).growY();
        root.add(scrollPane).expand().fill();

        stage.addActor(root);
        showPlayerSelection();
    }

    private Table createSidebar() {
        Table sidebar = new Table();
        sidebar.top().pad(10);
        sidebar.setBackground(new Image(new Texture(Gdx.files.internal("assets/Background/layers/middleground.png"))).getDrawable());

        TextButton startTradeButton = new TextButton("Start Trade", menuManager.getPixthulhuSkin());
        TextButton tradeHistoryButton = new TextButton("Trade History", menuManager.getPixthulhuSkin());
        TextButton exitButton = new TextButton("Exit", menuManager.getPixthulhuSkin());

        sidebar.add(startTradeButton).fillX().pad(5).row();
        sidebar.add(tradeHistoryButton).fillX().pad(5).row();
        sidebar.add().expandY().row();
        sidebar.add(exitButton).fillX().pad(5);

        startTradeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showPlayerSelection();
            }
        });

        tradeHistoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showTradeHistory();
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameView.showMapView();
            }
        });

        return sidebar;
    }

    private void showPlayerSelection() {
        contentTable.clear();
        contentTable.top().left().pad(20);
        contentTable.add(new Label("Select a player to trade with:", menuManager.getPixthulhuSkin(), "title")).row();

    }

    public void updatePlayerList(List<String> players) {
        contentTable.clear();
        contentTable.top().left().pad(20);
        contentTable.add(new Label("Select a player to trade with:", menuManager.getPixthulhuSkin(), "title")).row();

        for (String username : players) {
            if (!username.equals(player.getUsername())) {
                TextButton playerButton = new TextButton(username, menuManager.getPixthulhuSkin());
                playerButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        // Send trade request to the selected player
                        java.util.Map<String, Object> payload = new java.util.HashMap<>();
                        payload.put("receiver", username);
                        // NEW: Add the player's current inventory to the request
                        payload.put("inventory", player.getInventory().getItems());

                        com.StardewValley.Network.Message tradeRequestMessage = new com.StardewValley.Network.Message(com.StardewValley.Network.Message.ActionType.TRADE_REQUEST, payload);
                        com.StardewValley.Network.Client.ClientMain.sendMessage(tradeRequestMessage);

                        // Optionally, show a "request sent" message to the requester
                        Dialog sentDialog = new Dialog("Request Sent", menuManager.getPixthulhuSkin());
                        sentDialog.text("Trade request sent to " + username);
                        sentDialog.button("OK");
                        sentDialog.show(stage);
                    }
                });
                contentTable.add(playerButton).pad(10).row();
            }
        }
    }

    private void showTradeHistory() {
        contentTable.clear();
        contentTable.top().left().pad(20);
        contentTable.add(new Label("Trade History", menuManager.getPixthulhuSkin(), "title")).row();
        // Logic to display trade history
    }

    public GameView getGameView() {
        return gameView;
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

    public Stage getStage() {
        return stage;
    }
}
