package misterbander.sandboxtabletop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.Serializable;
import java.util.UUID;

import misterbander.gframework.scene2d.MBTextField;
import misterbander.gframework.util.MathUtils;
import misterbander.gframework.util.TextUtils;
import misterbander.sandboxtabletop.net.Connection;
import misterbander.sandboxtabletop.net.ConnectionEventListener;
import misterbander.sandboxtabletop.net.SandboxTabletopClient;
import misterbander.sandboxtabletop.net.model.Chat;
import misterbander.sandboxtabletop.net.model.CursorPosition;
import misterbander.sandboxtabletop.net.model.FlipCardEvent;
import misterbander.sandboxtabletop.net.model.LockEvent;
import misterbander.sandboxtabletop.net.model.OwnerEvent;
import misterbander.sandboxtabletop.net.model.ServerCard;
import misterbander.sandboxtabletop.net.model.ServerObject;
import misterbander.sandboxtabletop.net.model.ServerObjectList;
import misterbander.sandboxtabletop.net.model.ServerObjectPosition;
import misterbander.sandboxtabletop.net.model.User;
import misterbander.sandboxtabletop.net.model.UserEvent;
import misterbander.sandboxtabletop.net.model.UserList;
import misterbander.sandboxtabletop.scene2d.Card;
import misterbander.sandboxtabletop.scene2d.Cursor;
import misterbander.sandboxtabletop.scene2d.GameMenuWindow;
import misterbander.sandboxtabletop.scene2d.Hand;

public class RoomScreen extends SandboxTabletopScreen implements ConnectionEventListener
{
	private static final float TICK_TIME = 1/40F;
	
	public final SandboxTabletopClient client;
	private float tick;
	
	public final User user;
	
	private final ImageButton menuButton = new ImageButton(game.skin, "menubuttonstyle");
	private final MBTextField chatTextField = new MBTextField("", game.skin, "chattextfieldstyle");
	/** Stores chat history */
	private final VerticalGroup chatHistory = new VerticalGroup();
	private final ScrollPane chatHistoryScrollPane = new ScrollPane(chatHistory, game.skin, "scrollpanestyle");
	/** Stores recent chat popup labels that disappear after 5 seconds. */
	private final VerticalGroup chatPopup = new VerticalGroup();
	
	private final ShaderProgram shader = new ShaderProgram(Gdx.files.internal("shaders/passthrough.vsh").readString(), Gdx.files.internal("shaders/vignette.fsh").readString());
	
	private final ObjectMap<UUID, User> otherUsers = new ObjectMap<>();
	private @Null Cursor myCursor;
	private final CursorPosition cursorPosition;
	public @Null ServerObjectPosition latestServerObjectPosition;
	
	public final ObjectMap<UUID, Actor> uuidActorMap = new ObjectMap<>();
	public final Hand hand = new Hand(this);
	
	private final TextureRegion handRegion = game.skin.getRegion("hand");
	
	public RoomScreen(SandboxTabletop game, SandboxTabletopClient client, User user)
	{
		super(game);
		this.client = client;
		
		this.user = user;
		
		cursorPosition = new CursorPosition(user.uuid, 640, 360);
		
		GameMenuWindow gameMenuWindow = new GameMenuWindow(this);
		
		// Set up UI
		
		Actor fallbackActor = new Actor()
		{
			@Override
			public Actor hit(float x, float y, boolean touchable)
			{
				return this;
			}
		};
		fallbackActor.addListener(new InputListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				uiStage.setKeyboardFocus(null);
				Gdx.input.setOnscreenKeyboardVisible(false);
				return false;
			}
		});
		menuButton.addListener(new ChangeListener(gameMenuWindow::show));
		chatTextField.setMessageText(Gdx.app.getType() == Application.ApplicationType.Android ? "Tap here to chat..." : "Press T to chat...");
		chatTextField.setMaxLength(256);
		chatTextField.setFocusTraversal(false);
		// Add a listener so that we can send chat on enter key
		chatTextField.addListener(new InputListener()
		{
			@Override
			public boolean keyTyped(InputEvent event, char character)
			{
				if ((character == '\r' || character == '\n') && !chatTextField.getText().isEmpty())
				{
					client.send(new Chat(user, "<" + user.username + "> " + chatTextField.getText(), false));
					chatTextField.setText("");
					uiStage.setKeyboardFocus(null);
					return true;
				}
				return false;
			}
		});
		// Add another listener so that we can toggle between full chat history view or recent popup chat history view
		chatTextField.addListener(new FocusListener()
		{
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused)
			{
				chatPopup.setVisible(!focused);
				chatHistoryScrollPane.setVisible(focused);
				Gdx.input.setOnscreenKeyboardVisible(focused);
			}
		});
		chatPopup.columnAlign(Align.left);
		chatHistory.grow();
		chatHistoryScrollPane.addListener(new InputListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				Gdx.input.setOnscreenKeyboardVisible(false);
				return true;
			}
		});
		chatHistoryScrollPane.setVisible(false);
		uiStage.setScrollFocus(chatHistoryScrollPane);
		
		Table table = new Table();
		table.setFillParent(true);
		table.top();
		table.add(menuButton).top().pad(16);
		
		Table chatTable = new Table();
		chatTable.defaults().growX();
		chatTable.add(chatTextField);
		chatTable.row();
		Stack stack = new Stack();
		Container<VerticalGroup> chatPopupContainer = new Container<>(chatPopup);
		chatPopupContainer.top().left();
		stack.add(chatPopupContainer);
		stack.add(chatHistoryScrollPane);
		chatTable.add(stack).left();
		
		table.add(chatTable).pad(16).top().expandX().fillX().maxHeight(312);
		
		uiStage.addActor(fallbackActor);
		uiStage.addActor(table);
		uiStage.addListener(new InputListener()
		{
			@Override
			public boolean keyDown(InputEvent event, int keycode)
			{
				if (event.getKeyCode() == Input.Keys.T && !chatTextField.hasKeyboardFocus())
				{
					Gdx.app.postRunnable(() -> uiStage.setKeyboardFocus(chatTextField));
					return true;
				}
				else if (event.getKeyCode() == Input.Keys.ESCAPE)
				{
					uiStage.setKeyboardFocus(null);
					return true;
				}
				return false;
			}
		});
		
		uiStage.addActor(gameMenuWindow);
		
//		stage.addActor(new Debug(viewport, game.getShapeDrawer()));
		if (Gdx.app.getType() != Application.ApplicationType.Desktop)
		{
			myCursor = new Cursor(user, game.skin, true);
			stage.addActor(myCursor);
		}
		
		if (!shader.isCompiled())
			Gdx.app.log("RoomScreen | ERROR", shader.getLog());
	}
	
	@Override
	public void show()
	{
		super.show();
		
		// Set a random cursor color for each player using the hash code of each user's uuid
		Pixmap cursorBorderPixmap = new Pixmap(Gdx.files.internal("textures/cursorborder.png"));
		Pixmap cursorBasePixmap = new Pixmap(Gdx.files.internal("textures/cursorbase.png"));
		Color userColor = User.getUserColor(user);
		for (int i = 0; i < cursorBasePixmap.getWidth(); i++)
		{
			for (int j = 0; j < cursorBasePixmap.getHeight(); j++)
			{
				Color color = new Color(cursorBasePixmap.getPixel(i, j));
				cursorBasePixmap.setColor(color.mul(userColor));
				cursorBasePixmap.drawPixel(i, j);
			}
		}
		cursorBorderPixmap.drawPixmap(cursorBasePixmap, 0, 0);
		Gdx.graphics.setCursor(Gdx.graphics.newCursor(cursorBorderPixmap, 3, 0));
		cursorBorderPixmap.dispose();
		cursorBasePixmap.dispose();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
		for (Actor actor : chatPopup.getChildren())
		{
			Container<Label> chatLabelContainer = (Container<Label>)actor;
			Label label = chatLabelContainer.getActor();
			assert label != null;
			chatLabelContainer.width(getChatTextWidth(label.getStyle().font, label.getText().toString()));
			chatLabelContainer.invalidateHierarchy();
		}
		shader.bind();
		shader.setUniformf("u_resolution", width, height);
	}
	
	private int getChatTextWidth(BitmapFont font, String message)
	{
		return (int)Math.min(TextUtils.textSize(font, message).x + 32, uiViewport.getWorldWidth() - menuButton.getWidth() - 64);
	}
	
	/**
	 * Appends a chat message to the chat history, and adds a chat label that disappears after 5 seconds.
	 * @param message the message
	 * @param color   color of the chat message
	 */
	private void addChatMessage(String message, @Null Color color)
	{
		Label chatLabel = new Label(message, game.skin, "chatlabelstyle");
		Container<Label> chatLabelContainer = new Container<>(chatLabel);
		chatLabelContainer.width(getChatTextWidth(chatLabel.getStyle().font, message));
		chatLabel.setWrap(true);
		if (color != null)
			chatLabel.setColor(color.cpy());
		AlphaAction alphaAction = new AlphaAction(); // Action to fade out
		alphaAction.setAlpha(0);
		alphaAction.setDuration(1);
		RemoveActorAction removeActorAction = new RemoveActorAction(); // Action to remove label after fade out
		removeActorAction.setTarget(chatLabelContainer);
		chatLabelContainer.addAction(new SequenceAction(new DelayAction(10), alphaAction, removeActorAction));
		chatPopup.addActor(chatLabelContainer);
		if (chatPopup.getChildren().size == 7) // Maximum 6 children
		{
			Actor firstChatPopup = chatPopup.removeActorAt(0, false);
			firstChatPopup.clear();
		}
		
		// Add to history
		Label chatHistoryLabel = new Label(message, game.skin, "infolabelstyle");
		chatHistoryLabel.setWrap(true);
		if (color != null)
			chatHistoryLabel.setColor(color.cpy());
		chatHistory.pad(4, 16, 4, 16).space(8);
		chatHistory.addActor(chatHistoryLabel);
		chatHistoryScrollPane.layout();
		chatHistoryScrollPane.setScrollPercentY(100);
	}
	
	private void addUser(User user)
	{
		otherUsers.put(user.uuid, user);
		Cursor cursor = new Cursor(user, game.skin, false);
		user.cursor = cursor;
		stage.addActor(cursor);
		Gdx.app.log("RoomScreen | INFO", "Added " + user.username);
	}
	
	private void removeUser(User user)
	{
		User removedUser = otherUsers.remove(user.uuid);
		assert removedUser != null;
		removedUser.cursor.remove();
		Gdx.app.log("RoomScreen | INFO", "Removed " + user.username);
	}
	
	@Override
	public void render(float delta)
	{
		clearScreen();
		game.getBatch().setShader(shader);
		game.getBatch().begin();
		game.getShapeDrawer().setColor(SandboxTabletop.BACKGROUND_COLOR);
		game.getShapeDrawer().filledRectangle(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
		game.getBatch().setShader(null);
		game.getBatch().setColor(1, 1, 1, 1);
		game.getBatch().draw(handRegion, 0, 0, viewport.getWorldWidth(), 96);
		game.getBatch().end();
		renderStage(camera, stage, delta);
		renderStage(uiCamera, uiStage, delta);
		updateWorld();
		
		tick += delta;
		if (tick > TICK_TIME)
		{
			tick = 0;
			MathUtils.TEMP_VEC.set(Gdx.input.getX(), Gdx.input.getY());
			stage.screenToStageCoordinates(MathUtils.TEMP_VEC);
			if (cursorPosition.set(MathUtils.TEMP_VEC.x, MathUtils.TEMP_VEC.y))
			{
				client.send(cursorPosition);
				if (myCursor != null)
					myCursor.setTargetPosition(cursorPosition.getX() - 3, cursorPosition.getY() - 32);
				if (latestServerObjectPosition != null)
				{
					client.send(latestServerObjectPosition);
					latestServerObjectPosition = null;
				}
			}
		}
	}
	
	@Override
	public void connectionClosed(Connection connection, Exception e)
	{
		MenuScreen menuScreen = new MenuScreen(game);
		game.setScreen(menuScreen);
		Gdx.graphics.setSystemCursor(com.badlogic.gdx.graphics.Cursor.SystemCursor.Arrow);
		if (!client.isDisconnectIntentional())
		{
			String errorMessage = e.toString() + (e.getCause() != null ? "\n" + e.getCause() : "");
			menuScreen.connectingDialog.show("Disconnected", "Connection reset".equals(e.getMessage()) ? "Server closed." : errorMessage, "OK", null);
		}
	}
	
	@Override
	public void objectReceived(Connection connection, Serializable object)
	{
		if (object instanceof Chat)
		{
			Chat chat = (Chat)object;
			Gdx.app.log("SandboxTabletopClient | CHAT", chat.message);
			addChatMessage(chat.message, chat.isSystemMessage ? Color.YELLOW : null);
		}
		else if (object instanceof UserList)
		{
			User[] users = ((UserList)object).users;
			for (User user : users)
			{
				if (!user.equals(this.user))
					addUser(user);
			}
		}
		else if (object instanceof UserEvent.UserJoinEvent)
		{
			UserEvent.UserJoinEvent event = (UserEvent.UserJoinEvent)object;
			addChatMessage(event.user.username + " joined the game", Color.YELLOW);
			if (!event.user.equals(user) && !otherUsers.containsKey(event.user.uuid))
				addUser(event.user);
		}
		else if (object instanceof UserEvent.UserLeaveEvent)
		{
			UserEvent.UserLeaveEvent event = (UserEvent.UserLeaveEvent)object;
			addChatMessage(event.user.username + " left the game", Color.YELLOW);
			removeUser(event.user);
		}
		else if (object instanceof ServerObjectList)
		{
			ServerObject[] objectList = ((ServerObjectList)object).objects;
			for (int i = 0; i < objectList.length; i++)
			{
				ServerObject serverObject = objectList[i];
				if (serverObject instanceof ServerCard)
				{
					ServerCard serverCard = (ServerCard)serverObject;
					Card card = new Card(this, serverCard.getUUID(), serverCard.rank, serverCard.suit, serverCard.lockHolder, serverCard.owner,
							serverCard.getX(), serverCard.getY(), serverCard.getRotation(), serverCard.isFaceUp);
					uuidActorMap.put(serverCard.getUUID(), card);
					stage.addActor(card);
					card.setZIndex(i);
					if (card.owner != null)
					{
						if (user.equals(card.owner))
							hand.addCard(card);
						else
							card.setVisible(false);
					}
				}
			}
			System.out.println("Arranging cards");
			hand.arrangeCards(false);
		}
		else if (object instanceof CursorPosition)
		{
			CursorPosition cursorPosition = (CursorPosition)object;
			User user = otherUsers.get(cursorPosition.userUuid);
			if (user != null)
				user.cursor.setTargetPosition(cursorPosition.getX() - 3, cursorPosition.getY() - 32);
		}
		else if (object instanceof LockEvent)
		{
			LockEvent event = (LockEvent)object;
			Actor actor = uuidActorMap.get(event.lockedUuid);
			if (actor instanceof Card)
			{
				Card card = (Card)actor;
				card.setZIndex(uuidActorMap.size);
				card.lockHolder = event.lockHolder;
			}
		}
		else if (object instanceof OwnerEvent)
		{
			OwnerEvent event = (OwnerEvent)object;
			Actor actor = uuidActorMap.get(event.ownedUuid);
			if (actor instanceof Card)
			{
				Card card = (Card)actor;
				card.owner = event.owner;
				card.setVisible(card.owner == null || card.owner.equals(user));
			}
		}
		else if (object instanceof ServerObjectPosition)
		{
			ServerObjectPosition serverObjectPosition = (ServerObjectPosition)object;
			Actor actor = uuidActorMap.get(serverObjectPosition.uuid);
			if (actor instanceof Card)
			{
				Card card = (Card)actor;
				if (card.owner == null)
					card.setTargetPosition(serverObjectPosition.x, serverObjectPosition.y);
			}
		}
		else if (object instanceof FlipCardEvent)
		{
			FlipCardEvent flipCardEvent = (FlipCardEvent)object;
			Actor actor = uuidActorMap.get(flipCardEvent.uuid);
			if (actor instanceof Card)
			{
				Card card = (Card)actor;
				card.setFaceUp(flipCardEvent.isFaceUp);
				if (card.owner == null)
					card.setZIndex(uuidActorMap.size);
			}
		}
	}
}
