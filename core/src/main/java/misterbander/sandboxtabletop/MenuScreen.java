package misterbander.sandboxtabletop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Null;

import misterbander.sandboxtabletop.net.Connection;
import misterbander.sandboxtabletop.net.ConnectionEventListener;
import misterbander.sandboxtabletop.net.SandboxTabletopClient;
import misterbander.sandboxtabletop.net.model.User;
import misterbander.sandboxtabletop.scene2d.ConnectWindow;
import misterbander.sandboxtabletop.scene2d.MessageDialog;
import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * This is the menu screen. There will be two buttons "Play" and "Quit". Clicking "Play" brings up a window where you
 * can connect to the server. Clicking "Quit" closes the application.
 * <p>
 * This class implements {@link ConnectionEventListener} that listens for network events such as when the client is connected,
 * disconnected, or receives an object from the remote server.
 * </p>
 */
public class MenuScreen extends SandboxTabletopScreen implements ConnectionEventListener
{
	private final Color backgroundAccentColor = new Color(0xBA00A1FF);
	
	public final ConnectWindow connectWindow = new ConnectWindow(this);
	public final MessageDialog connectingDialog = new MessageDialog(this, "", true);
	
	public @Null SandboxTabletopClient client;
	
	public MenuScreen(SandboxTabletop game)
	{
		super(game);
		
		// Set up the UI
		
		Texture logo = game.skin.get("logo", Texture.class);
		logo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		TextButton playButton = new TextButton("Play", game.skin, "textbuttonstyle");
		playButton.addListener(new ChangeListener(connectWindow::show));
		TextButton quitButton = new TextButton("Quit", game.skin, "textbuttonstyle");
		quitButton.addListener(new ChangeListener(() -> Gdx.app.exit()));
		
		Table table = new Table();
		table.setFillParent(true);
		table.add(new Image(logo)).top().pad(16);
		table.row();
		
		Table menuTable = new Table();
		menuTable.defaults().prefWidth(224).space(16);
		menuTable.add(playButton).row();
		menuTable.add(quitButton);
		table.add(menuTable).expand();
		
		uiStage.addActor(table);
		
		uiStage.addActor(connectWindow);
		uiStage.addActor(connectingDialog);
		
		keyboardHeightObservers.add(connectWindow);
	}
	
	@Override
	public void clearScreen()
	{
		super.clearScreen();
		PolygonSpriteBatch batch = game.getBatch();
		ShapeDrawer shapeDrawer = game.getShapeDrawer();
		batch.begin();
		shapeDrawer.setColor(backgroundAccentColor);
		shapeDrawer.filledRectangle(80, 96, viewport.getWorldWidth() - 96, viewport.getWorldHeight() - 96, 4*MathUtils.degRad);
		batch.end();
	}
	
	@Override
	public void connectionOpened(Connection connection)
	{
		Gdx.app.log("SandboxTabletopClient | INFO", "Connected to " + connection.remoteAddress);
		// We just connected to the server from the main menu, we can go into the room now
		assert client != null;
		User user = new User(connectWindow.usernameTextField.getText(), game.uuid);
		RoomScreen roomScreen = new RoomScreen(game, client, user);
		client.send(user);
		client.setConnectionEventListener(roomScreen);
		game.setScreen(roomScreen);
		Gdx.app.log("SandboxTabletopClient | INFO", "Joining game as " + user);
	}
	
	@Override
	public void connectionClosed(Connection connection)
	{
		Gdx.app.log("SandboxTabletopClient | INFO", "Disconnected from " + connection.remoteAddress);
		client = null;
	}
	
	@Override
	public void exceptionOccurred(@Null Connection connection, Exception e)
	{
		assert client != null;
		if (connection == null)
		{
			// Exception occurred because cannot connect to remote server
			connectingDialog.show("Connection Failed", "Failed to connect to server.\n" + e.toString()
					+ (e.getCause() != null ? "\n" + e.getCause() : ""), "OK", connectWindow::show);
			e.printStackTrace();
		}
		else
			ConnectionEventListener.super.exceptionOccurred(connection, e);
	}
}
