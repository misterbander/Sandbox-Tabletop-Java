package misterbander.sandboxtabletop.scene2d;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.io.IOException;

import misterbander.gframework.scene2d.MBTextField;
import misterbander.gframework.scene2d.UnfocusListener;
import misterbander.sandboxtabletop.MenuScreen;
import misterbander.sandboxtabletop.net.SandboxTabletopClient;

/**
 * This is the window that displays after clicking "Play" in the main menu. There are three text fields where the user can
 * enter their username of choice, the server IP address to connect to, and the port to listen on.
 */
public class ConnectWindow extends SandboxTabletopWindow
{
	public final MBTextField usernameTextField = new MBTextField(this, "", game.skin, "formtextfieldstyle");
	
	public ConnectWindow(MenuScreen screen)
	{
		super(screen, "Connect", true);
		
		// Set up the UI
		
		defaults().pad(16);
		MBTextField ipTextField = new MBTextField(this, "", game.skin, "formtextfieldstyle");
		MBTextField portTextField = new MBTextField(this, "", game.skin, "formtextfieldstyle");
		portTextField.setTextFieldFilter(new MBTextField.MBTextFieldFilter.DigitsOnlyFilter());
		TextButton joinButton = new TextButton("Join", game.skin, "textbuttonstyle");
		joinButton.addListener(screen.new ChangeListener(() ->
		{
			try
			{
				screen.client = new SandboxTabletopClient(screen, ipTextField.getText(),
						portTextField.getText().isEmpty() ? 11530 : Integer.parseInt(portTextField.getText()));
				screen.client.start();
				close();
				screen.connectingDialog.show("Connecting", "Connecting to " + ipTextField.getText() + "...", "Cancel",
						() ->
						{
							screen.client.disconnect();
							show();
						});
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}));
		TextButton cancelButton = new TextButton("Cancel", game.skin, "textbuttonstyle");
		cancelButton.addListener(screen.new ChangeListener(this::close));
		
		Table table = new Table();
		table.defaults().left().space(16);
		table.add(new Label("Username:", game.skin, "infolabelstyle"));
		table.add(usernameTextField).prefWidth(288);
		table.row();
		table.add(new Label("Server IP Address:", game.skin, "infolabelstyle"));
		table.add(ipTextField).prefWidth(288);
		table.row();
		table.add(new Label("Server Port:", game.skin, "infolabelstyle"));
		table.add(portTextField).prefWidth(288);
		table.row();
		
		Table joinCancelTable = new Table();
		joinCancelTable.defaults().space(16);
		joinCancelTable.add(joinButton).prefWidth(224);
		joinCancelTable.add(cancelButton);
		
		table.add(joinCancelTable).colspan(2).center();
		add(table);
		addListener(new UnfocusListener(this));
	}
}
