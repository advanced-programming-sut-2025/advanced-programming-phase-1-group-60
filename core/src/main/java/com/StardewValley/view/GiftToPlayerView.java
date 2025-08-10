package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.GiftController;
import com.StardewValley.models.Item;
import com.StardewValley.models.User;
import com.StardewValley.repository.UserRepository;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.List;

public class GiftToPlayerView implements Screen {

    private final Game game;
    private final Stage stage;
    private final MenuManager menuManager;
    private final User currentPlayer;
    private final GameView gameView;
    private final GiftController giftController;

    private Table mainContainer;
    private Table playerListTable;
    private Table actionTable;

    private User selectedPlayer;

    public GiftToPlayerView(Game game, User currentPlayer, GameView gameView) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.currentPlayer = currentPlayer;
        this.gameView = gameView;
        this.giftController = GiftController.getInstance();

        for (User u : UserRepository.getInstance().getAllUsers()) {
            currentPlayer.increaseFriendshipXpsWithUsers(u, 150);
            u.increaseFriendshipXpsWithUsers(currentPlayer, 150);
        }

        setupUI();
    }

    private void setupUI() {
        mainContainer = new Table();
        mainContainer.setFillParent(true);
        mainContainer.setBackground(new Image(new Texture(Gdx.files.internal("assets/Background/layers/background.png"))).getDrawable());
        mainContainer.pad(20);

        playerListTable = new Table(menuManager.getPixthulhuSkin());
        actionTable = new Table(menuManager.getPixthulhuSkin());

        ScrollPane playerScrollPane = new ScrollPane(playerListTable, menuManager.getPixthulhuSkin());
        ScrollPane actionScrollPane = new ScrollPane(actionTable, menuManager.getPixthulhuSkin());

        mainContainer.add(new Label("Gift to Player", menuManager.getPixthulhuSkin(), "title")).colspan(2).center().padBottom(20).row();
        mainContainer.add(playerScrollPane).expand().fill().padLeft(50); // ** FIX: Added padding to the left **
        mainContainer.add(actionScrollPane).expand().fill();

        TextButton backButton = new TextButton("Back to Game", menuManager.getPixthulhuSkin());
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameView.showMapView();
            }
        });

        // ** FIX: Position Back button to bottom right **
        Table bottomTable = new Table();
        bottomTable.setFillParent(true);
        bottomTable.bottom().right();
        bottomTable.add(backButton).pad(20);
        stage.addActor(mainContainer);
        stage.addActor(bottomTable);
        populatePlayerList();
    }

    private void populatePlayerList() {
        playerListTable.clear();
        playerListTable.top().left();

        List<User> allUsers = UserRepository.getInstance().getAllUsers();
        for (User user : allUsers) {
            if (user.equals(currentPlayer)) continue;

            int friendshipXp = currentPlayer.getFriendshipXpsWithUsers(user);
            int friendshipLevel = currentPlayer.getFriendshipLevelWithUsers(user);
            String labelText = String.format("%s - Level: %d (XP: %d)", user.getUsername(), friendshipLevel, friendshipXp);
            TextButton playerButton = new TextButton(labelText, menuManager.getPixthulhuSkin());

            playerButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    selectedPlayer = user;
                    showPlayerActions();
                }
            });
            playerListTable.add(playerButton).fillX().pad(5).row();
        }
    }

    private void showPlayerActions() {
        actionTable.clear();
        actionTable.top().left();
        actionTable.add(new Label("Actions for " + selectedPlayer.getUsername(), menuManager.getPixthulhuSkin(), "default")).pad(10).row();

        TextButton giftButton = new TextButton("Give Gift", menuManager.getPixthulhuSkin());
        TextButton historyButton = new TextButton("View Gift History", menuManager.getPixthulhuSkin());
        TextButton messageButton = new TextButton("Send Message", menuManager.getPixthulhuSkin());
        TextButton messageHistoryButton = new TextButton("View Message History", menuManager.getPixthulhuSkin());

        // ** FIX: Friendship level check moved to individual buttons **
        if (currentPlayer.getFriendshipLevelWithUsers(selectedPlayer) < 1) {
            giftButton.setDisabled(true);
            giftButton.getLabel().setColor(Color.GRAY);
            historyButton.setDisabled(true);
            historyButton.getLabel().setColor(Color.GRAY);
        } else {
            giftButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    showInventoryForGifting();
                }
            });
            historyButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    showGiftHistory();
                }
            });
        }

        messageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showSendMessageDialog();
            }
        });
        messageHistoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showMessageHistory();
            }
        });

        actionTable.add(giftButton).fillX().pad(5).row();
        actionTable.add(historyButton).fillX().pad(5).row();
        actionTable.add(messageButton).fillX().pad(5).row();
        actionTable.add(messageHistoryButton).fillX().pad(5).row();
    }

    private void showSendMessageDialog() {
        Dialog dialog = new Dialog("Send Message", menuManager.getPixthulhuSkin());
        TextField messageField = new TextField("", menuManager.getPixthulhuSkin());
        dialog.getContentTable().add(messageField).width(300).pad(20);
        TextButton sendButton = new TextButton("Send", menuManager.getPixthulhuSkin());
        sendButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    currentPlayer.talk(selectedPlayer, message);
                    showResultDialog("Message sent!");
                    dialog.hide();
                }
            }
        });
        dialog.getButtonTable().add(sendButton);
        dialog.button("Cancel");
        dialog.show(stage);
    }

    private void showMessageHistory() {
        actionTable.clear();
        actionTable.add(new Label("Message History with " + selectedPlayer.getUsername(), menuManager.getPixthulhuSkin())).pad(10).row();
        StringBuilder history = currentPlayer.getAllMessages(selectedPlayer);
        Label historyLabel = new Label(history.toString(), menuManager.getPixthulhuSkin());
        historyLabel.setWrap(true);
        actionTable.add(new ScrollPane(historyLabel, menuManager.getPixthulhuSkin())).expand().fill();
    }

    private void showInventoryForGifting() {
        actionTable.clear();
        actionTable.top().left();
        actionTable.add(new Label("Select an item to gift:", menuManager.getPixthulhuSkin())).pad(10).row();

        Table inventoryTable = new Table();
        for (Item item : currentPlayer.getInventory().getItems()) {
            TextButton itemButton = new TextButton(item.getName() + " x" + item.getQuantity(), menuManager.getPixthulhuSkin());
            itemButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    showQuantitySelector(item);
                }
            });
            inventoryTable.add(itemButton).left().pad(5).row();
        }
        actionTable.add(new ScrollPane(inventoryTable, menuManager.getPixthulhuSkin())).expand().fill();
    }

    private void showQuantitySelector(Item item) {
        Dialog quantityDialog = new Dialog("Select Quantity", menuManager.getPixthulhuSkin());
        Slider slider = new Slider(1, item.getQuantity(), 1, false, menuManager.getPixthulhuSkin());
        Label quantityLabel = new Label("1", menuManager.getPixthulhuSkin());
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                quantityLabel.setText(String.valueOf((int) slider.getValue()));
            }
        });

        TextButton confirmButton = new TextButton("Confirm", menuManager.getPixthulhuSkin());
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int quantity = (int) slider.getValue();
                String result = giftController.giftToPlayer(currentPlayer, selectedPlayer.getUsername(), item.getName(), quantity);
                showResultDialog(result);
                quantityDialog.hide();
                showPlayerActions();
            }
        });

        quantityDialog.getContentTable().add(quantityLabel).pad(10);
        quantityDialog.getContentTable().add(slider).width(200).pad(10).row();
        quantityDialog.getButtonTable().add(confirmButton);
        quantityDialog.show(stage);
    }

    private void showGiftHistory() {
        actionTable.clear();
        actionTable.add(new Label("Gift History with " + selectedPlayer.getUsername(), menuManager.getPixthulhuSkin())).pad(10).row();

        List<String> history = giftController.getGiftHistory(currentPlayer);
        Table historyTable = new Table();
        for (String entry : history) {
            if (entry.contains(selectedPlayer.getUsername())) {
                historyTable.add(new Label(entry, menuManager.getPixthulhuSkin())).left().pad(5).row();
            }
        }

        String pendingGift = giftController.checkNewGift(currentPlayer);
        if (pendingGift != null && pendingGift.contains(selectedPlayer.getUsername())) {
            historyTable.add(new Label("PENDING: " + pendingGift, menuManager.getPixthulhuSkin())).left().pad(10).row();

            TextButton rateButton = new TextButton("Rate This Gift", menuManager.getPixthulhuSkin());
            rateButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    showRatingDialog();
                }
            });
            historyTable.add(rateButton).pad(10).row();
        }

        actionTable.add(new ScrollPane(historyTable, menuManager.getPixthulhuSkin())).expand().fill();
    }

    private void showRatingDialog() {
        Dialog ratingDialog = new Dialog("Rate Gift", menuManager.getPixthulhuSkin());
        Slider ratingSlider = new Slider(1, 5, 1, false, menuManager.getPixthulhuSkin());
        Label ratingLabel = new Label("3", menuManager.getPixthulhuSkin());
        ratingSlider.setValue(3);

        ratingSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ratingLabel.setText(String.valueOf((int) ratingSlider.getValue()));
            }
        });

        TextButton submitButton = new TextButton("Submit Rating", menuManager.getPixthulhuSkin());
        submitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int rating = (int) ratingSlider.getValue();
                String result = giftController.rateGift(currentPlayer, rating);
                showResultDialog(result);
                ratingDialog.hide();
                showGiftHistory();
            }
        });

        ratingDialog.getContentTable().add(ratingLabel).pad(10);
        ratingDialog.getContentTable().add(ratingSlider).width(200).pad(10);
        ratingDialog.getButtonTable().add(submitButton);
        ratingDialog.show(stage);
    }

    private void showResultDialog(String message) {
        Dialog dialog = new Dialog("Result", menuManager.getPixthulhuSkin());
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
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
