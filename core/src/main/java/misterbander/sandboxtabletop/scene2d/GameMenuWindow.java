package misterbander.sandboxtabletop.scene2d;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import misterbander.sandboxtabletop.RoomScreen;

/**
 * This is the game menu window that displays after clicking the "Pause" button in the top left button in the room. There
 * are two buttons, one to continue and another to disconnect from the server.
 */
public class GameMenuWindow extends SandboxTabletopWindow
{
	public GameMenuWindow(RoomScreen screen)
	{
		super(screen, "Game Menu", true);
		
		// Set up the UI
		
		defaults().pad(16);
		TextButton continueButton = new TextButton("Continue", game.skin, "textbuttonstyle");
		continueButton.addListener(screen.new ChangeListener(this::close));
		TextButton disconnectButton = new TextButton("Disconnect", game.skin, "textbuttonstyle");
		disconnectButton.addListener(screen.new ChangeListener(() ->
		{
			assert screen.client != null;
			screen.client.disconnect();
		}));
		
		Table table = new Table();
		table.defaults().center().space(16);
		table.add(continueButton);
		table.row();
		table.add(disconnectButton);
		
		add(table);
	}
}
