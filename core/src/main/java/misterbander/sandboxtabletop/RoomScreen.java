package misterbander.sandboxtabletop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import misterbander.sandboxtabletop.net.model.User;
import misterbander.sandboxtabletop.net.model.UserEvent;
import misterbander.sandboxtabletop.net.model.UserList;
import misterbander.sandboxtabletop.scene2d.Cursor;
import misterbander.sandboxtabletop.scene2d.Debug;

public class RoomScreen extends SandboxTabletopScreen implements ConnectionEventListener
{
	private static final float TICK_TIME = 1/40F;
	
	private final SandboxTabletopClient client;
	private float tick;
	
	private final User user;
	
	private final ImageButton menuButton = new ImageButton(game.skin, "menubuttonstyle");
	private final MBTextField chatTextField = new MBTextField("", game.skin, "chattextfieldstyle");
	/** Stores chat history */
	private final VerticalGroup chatHistory = new VerticalGroup();
	private final ScrollPane chatHistoryScrollPane = new ScrollPane(chatHistory, game.skin, "scrollpanestyle");
	/** Stores recent chat popup labels that disappear after 5 seconds. */
	private final VerticalGroup chatPopup = new VerticalGroup();
	
	private final ObjectMap<UUID, User> otherUsers = new ObjectMap<>();
	
	private @Null Cursor myCursor;
	private final CursorPosition cursorPosition;
	
	public RoomScreen(SandboxTabletop game, SandboxTabletopClient client, User user)
	{
		super(game);
		this.client = client;
		
		this.user = user;
		
		cursorPosition = new CursorPosition(user.uuid, 640, 360);
		
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
				return true;
			}
		});
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
		
		stage.addActor(new Debug(viewport, game.getShapeDrawer()));
		if (Gdx.app.getType() != Application.ApplicationType.Desktop)
		{
			myCursor = new Cursor(user, game.skin, true);
			stage.addActor(myCursor);
		}
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
		super.render(delta);
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
			}
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
		else if (object instanceof CursorPosition)
		{
			CursorPosition cursorPosition = (CursorPosition)object;
			User user = otherUsers.get(cursorPosition.uuid);
			if (user != null)
				user.cursor.setTargetPosition(cursorPosition.getX() - 3, cursorPosition.getY() - 32);
		}
	}
}
