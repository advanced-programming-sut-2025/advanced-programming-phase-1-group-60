package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.models.Item;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.StardewValley.Network.Client.ClientMain;
import com.StardewValley.Network.Message;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeView implements Screen {
    private final Game game;
    private final Stage stage;
    private final MenuManager menuManager;
    private final User localPlayer;
    private final User remotePlayer;
    private final GameView gameView;
    private final boolean isRequester;
    private final String tradeId;
    private List<Item> localPlayerOffer = new ArrayList<>();
    private List<Item> remotePlayerOffer = new ArrayList<>();
    private int localPlayerMoneyOffer = 0;
    private int remotePlayerMoneyOffer = 0;
    private Table localInventoryTable;
    private Table remoteInventoryTable;
    private Table offerTable;
    private Table requestTable;
    private Slider offerMoneySlider;
    private Slider requestMoneySlider;
    private Label offerMoneyLabel;
    private Label requestMoneyLabel;
    private TextButton submitButton;
    private TextButton acceptButton;
    private TextButton rejectButton;
    private Label waitingLabel;
    private Timer.Task updateTask;

    public TradeView(Game game, GameView gameView, User localPlayer, User remotePlayer, boolean isRequester, String tradeId) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.localPlayer = localPlayer;
        this.remotePlayer = remotePlayer;
        this.gameView = gameView;
        this.isRequester = isRequester;
        this.tradeId = tradeId;
        createUI();
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new Image(new Texture(Gdx.files.internal("assets/Background/layers/background.png"))).getDrawable());
        root.pad(20);

        // Inventories
        localInventoryTable = new Table(menuManager.getPixthulhuSkin());
        remoteInventoryTable = new Table(menuManager.getPixthulhuSkin());
        ScrollPane localScrollPane = new ScrollPane(localInventoryTable, menuManager.getPixthulhuSkin());
        ScrollPane remoteScrollPane = new ScrollPane(remoteInventoryTable, menuManager.getPixthulhuSkin());

        root.add(new Label(localPlayer.getUsername() + "'s Inventory", menuManager.getPixthulhuSkin(), "title")).expandX();
        root.add(new Label(remotePlayer.getUsername() + "'s Inventory", menuManager.getPixthulhuSkin(), "title")).expandX().row();
        root.add(localScrollPane).expand().fill();
        root.add(remoteScrollPane).expand().fill().row();

        // Offer and Request Areas
        offerTable = new Table(menuManager.getPixthulhuSkin());
        requestTable = new Table(menuManager.getPixthulhuSkin());
        ScrollPane offerScrollPane = new ScrollPane(offerTable, menuManager.getPixthulhuSkin());
        ScrollPane requestScrollPane = new ScrollPane(requestTable, menuManager.getPixthulhuSkin());

        root.add(new Label("Your Offer", menuManager.getPixthulhuSkin(), "title")).padTop(20);
        root.add(new Label("Your Request", menuManager.getPixthulhuSkin(), "title")).padTop(20).row();
        root.add(offerScrollPane).expand().fill().height(150);
        root.add(requestScrollPane).expand().fill().height(150).row();

        // Money Sliders
        offerMoneySlider = new Slider(0, localPlayer.getMoney(), 1, false, menuManager.getPixthulhuSkin());
        requestMoneySlider = new Slider(0, remotePlayer.getMoney(), 1, false, menuManager.getPixthulhuSkin());
        offerMoneyLabel = new Label("0g", menuManager.getPixthulhuSkin());
        requestMoneyLabel = new Label("0g", menuManager.getPixthulhuSkin());

        if (!isRequester) {
            offerMoneySlider.setDisabled(true);
            requestMoneySlider.setDisabled(true);
        }

        Table offerMoneyTable = new Table();
        offerMoneyTable.add(offerMoneySlider).width(200);
        offerMoneyTable.add(offerMoneyLabel).padLeft(10);

        Table requestMoneyTable = new Table();
        requestMoneyTable.add(requestMoneySlider).width(200);
        requestMoneyTable.add(requestMoneyLabel).padLeft(10);

        root.add(offerMoneyTable);
        root.add(requestMoneyTable).row();

        // Action Buttons
        Table actionButtonTable = new Table();
        submitButton = new TextButton("Submit Trade", menuManager.getPixthulhuSkin());
        acceptButton = new TextButton("Accept", menuManager.getPixthulhuSkin());
        rejectButton = new TextButton("Reject", menuManager.getPixthulhuSkin());
        TextButton backButton = new TextButton("Back", menuManager.getPixthulhuSkin());
        waitingLabel = new Label("Waiting for other player...", menuManager.getPixthulhuSkin());

        if (isRequester) {
            actionButtonTable.add(submitButton);
        } else {
            actionButtonTable.add(waitingLabel);
            actionButtonTable.add(acceptButton).padRight(10);
            actionButtonTable.add(rejectButton);
        }
        actionButtonTable.add(backButton).padLeft(20);
        root.add(actionButtonTable).colspan(2).padTop(20);

        acceptButton.setVisible(false);
        rejectButton.setVisible(false);

        stage.addActor(root);

        populateInventories();
        addListeners();
    }

    private void addListeners() {
        offerMoneySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                localPlayerMoneyOffer = (int) offerMoneySlider.getValue();
                offerMoneyLabel.setText(localPlayerMoneyOffer + "g");
                debounceSendUpdate();
            }
        });

        requestMoneySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                remotePlayerMoneyOffer = (int) requestMoneySlider.getValue();
                requestMoneyLabel.setText(remotePlayerMoneyOffer + "g");
                debounceSendUpdate();
            }
        });

        submitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("tradeId", tradeId);
                payload.put("requester", isRequester ? localPlayer.getUsername() : remotePlayer.getUsername());
                payload.put("receiver", isRequester ? remotePlayer.getUsername() : localPlayer.getUsername());
                ClientMain.sendMessage(new Message(Message.ActionType.TRADE_SUBMIT, payload));
                submitButton.setDisabled(true);
                submitButton.setText("Offer Sent");
                if (isRequester) {
                    disableTradeEdits();
                }
            }
        });

        acceptButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("tradeId", tradeId);
                payload.put("requester", isRequester ? localPlayer.getUsername() : remotePlayer.getUsername());
                payload.put("receiver", isRequester ? remotePlayer.getUsername() : localPlayer.getUsername());
                payload.put("accepted", true);
                ClientMain.sendMessage(new Message(Message.ActionType.TRADE_FINALIZE_RESPONSE, payload));
                acceptButton.setDisabled(true);
                rejectButton.setDisabled(true);
            }
        });

        rejectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("tradeId", tradeId);
                payload.put("requester", isRequester ? localPlayer.getUsername() : remotePlayer.getUsername());
                payload.put("receiver", isRequester ? remotePlayer.getUsername() : localPlayer.getUsername());
                payload.put("accepted", false);
                ClientMain.sendMessage(new Message(Message.ActionType.TRADE_FINALIZE_RESPONSE, payload));
                acceptButton.setDisabled(true);
                rejectButton.setDisabled(true);
            }
        });
    }

    private void debounceSendUpdate() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        updateTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                sendTradeUpdate();
            }
        }, 0.3f); // Delay of 300ms
    }

    private void sendTradeUpdate() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("tradeId", tradeId);
        payload.put("requester", isRequester ? localPlayer.getUsername() : remotePlayer.getUsername());
        payload.put("receiver", isRequester ? remotePlayer.getUsername() : localPlayer.getUsername());
        payload.put("offeredItems", localPlayerOffer);
        payload.put("requestedItems", remotePlayerOffer);
        payload.put("offeredMoney", localPlayerMoneyOffer);
        payload.put("requestedMoney", remotePlayerMoneyOffer);
        ClientMain.sendMessage(new Message(Message.ActionType.TRADE_UPDATE_OFFER, payload));
    }

    // NEW: Method to disable editing after submit
    private void disableTradeEdits() {
        offerMoneySlider.setDisabled(true);
        requestMoneySlider.setDisabled(true);
        // Disable inventory buttons to prevent adding more items
        for (Actor actor : localInventoryTable.getChildren()) {
            if (actor instanceof Button) {
                ((Button) actor).setDisabled(true);
            }
        }
        for (Actor actor : remoteInventoryTable.getChildren()) {
            if (actor instanceof Button) {
                ((Button) actor).setDisabled(true);
            }
        }
    }

    private void populateInventories() {
        populateInventoryTable(localInventoryTable, localPlayer.getInventory().getItems(), true);
        populateInventoryTable(remoteInventoryTable, remotePlayer.getInventory().getItems(), false);
    }

    private void populateInventoryTable(Table table, List<Item> items, boolean isLocal) {
        table.clear();
        table.top().left();
        int col = 0;
        for (Item item : items) {
            Button itemButton = new Button(new Button.ButtonStyle());
            Stack itemSlot = createItemSlot(item);
            itemButton.add(itemSlot);
            if (isRequester) {
                itemButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        showQuantityDialog(item, isLocal);
                    }
                });
            }
            table.add(itemButton).size(64, 64).pad(5);
            if (++col % 5 == 0) {
                table.row();
            }
        }
    }

    private void showQuantityDialog(Item item, boolean isLocal) {
        Dialog dialog = new Dialog("Select Quantity", menuManager.getPixthulhuSkin());
        Slider slider = new Slider(1, item.getQuantity(), 1, false, menuManager.getPixthulhuSkin());
        Label quantityLabel = new Label("1", menuManager.getPixthulhuSkin());
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                quantityLabel.setText(String.valueOf((int) slider.getValue()));
            }
        });
        TextButton okButton = new TextButton("OK", menuManager.getPixthulhuSkin());
        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int quantity = (int) slider.getValue();
                Item tradeItem = new Item(item.getName(), quantity, item.getPath());
                if (isLocal) {
                    localPlayerOffer.add(tradeItem);
                } else {
                    remotePlayerOffer.add(tradeItem);
                }
                updateOfferRequestTables();
                sendTradeUpdate();
                dialog.hide();
            }
        });
        dialog.getContentTable().add(quantityLabel).pad(10);
        dialog.getContentTable().add(slider).width(200).pad(10).row();
        dialog.getButtonTable().add(okButton);
        dialog.show(stage);
    }

    private void updateOfferRequestTables() {
        populateOfferRequestTable(offerTable, localPlayerOffer);
        populateOfferRequestTable(requestTable, remotePlayerOffer);
    }

    private void populateOfferRequestTable(Table table, List<Item> items) {
        table.clear();
        table.top().left();
        int col = 0;
        for (Item item : items) {
            Stack itemSlot = createItemSlot(item);
            table.add(itemSlot).size(64, 64).pad(5);
            if (++col % 5 == 0) {
                table.row();
            }
        }
    }

    private Stack createItemSlot(Item item) {
        Stack itemSlot = new Stack();
        if (item != null && item.getPath() != null && !item.getPath().isEmpty()) {
            Image itemImage = new Image(new Texture(Gdx.files.internal(item.getPath())));
            itemSlot.add(itemImage);
            int quantity = item.getQuantity();
            if (quantity > 1) {
                Label quantityLabel = new Label(String.valueOf(quantity), menuManager.getPixthulhuSkin());
                quantityLabel.setAlignment(Align.bottomRight);
                itemSlot.add(quantityLabel);
            }
        }
        return itemSlot;
    }

    public void updateTradeOffer(List<Item> offeredItems, List<Item> requestedItems, int offeredMoney, int requestedMoney) {
        this.remotePlayerOffer = offeredItems;
        this.localPlayerOffer = requestedItems;
        this.remotePlayerMoneyOffer = offeredMoney;
        this.localPlayerMoneyOffer = requestedMoney;
        Gdx.app.postRunnable(() -> {
            updateOfferRequestTables();
            offerMoneySlider.setValue(localPlayerMoneyOffer);
            offerMoneyLabel.setText(localPlayerMoneyOffer + "g");
            requestMoneySlider.setValue(remotePlayerMoneyOffer);
            requestMoneyLabel.setText(remotePlayerMoneyOffer + "g");
        });
    }

    public void showFinalizeButtons() {
        if (!isRequester) {
            waitingLabel.setVisible(false);
            acceptButton.setVisible(true);
            rejectButton.setVisible(true);
        }
    }

    public void finalizeTrade(boolean accepted) {
        Dialog dialog = new Dialog("Trade Result", menuManager.getPixthulhuSkin());
        StringBuilder message = new StringBuilder();
        if (accepted) {
            message.append("Trade successful!\n\n");
            message.append("Offered Items: \n");
            for (Item item : localPlayerOffer) {
                message.append("- ").append(item.getName()).append(" x").append(item.getQuantity()).append("\n");
            }
            message.append("\nRequested Items: \n");
            for (Item item : remotePlayerOffer) {
                message.append("+ ").append(item.getName()).append(" x").append(item.getQuantity()).append("\n");
            }
            message.append("\nMoney Changes: \n");
            message.append("Offered Money: -").append(localPlayerMoneyOffer).append("g\n");
            message.append("Requested Money: +").append(remotePlayerMoneyOffer).append("g\n");
        } else {
            message.append("Trade rejected.\nNo changes applied.");
        }
        dialog.text(message.toString());
        dialog.button("OK", true).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameView.showMapView();
                // اگر نیاز به refresh inventory باشه، اینجا صدا بزنید
            }
        });
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
        if (updateTask != null) {
            updateTask.cancel();
        }
    }
}
