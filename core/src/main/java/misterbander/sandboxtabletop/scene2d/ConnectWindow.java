package misterbander.sandboxtabletop.scene2d;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import misterbander.gframework.scene2d.MBTextField;
import misterbander.gframework.scene2d.UnfocusListener;
import misterbander.sandboxtabletop.SandboxTabletopScreen;

public class ConnectWindow extends SandboxTabletopWindow
{
	public ConnectWindow(SandboxTabletopScreen screen)
	{
		super(screen, "Connect", true);
		defaults().pad(16).space(16);
		Table table = new Table();
		table.defaults().left().space(16);
		table.add(new Label("Username:", game.skin, "infolabelstyle"));
		table.add(new MBTextField(this, "", game.skin, "textfieldstyle")).width(288);
		table.row();
		table.add(new Label("Server IP Address:", game.skin, "infolabelstyle"));
		table.add(new MBTextField(this, "", game.skin, "textfieldstyle")).width(288);
		table.row();
		table.add(new Label("Server Port:", game.skin, "infolabelstyle"));
		table.add(new MBTextField(this, "", game.skin, "textfieldstyle")).width(288);
		table.row();
		TextButton joinButton = new TextButton("Join", game.skin, "textbuttonstyle");
		joinButton.addListener(screen.new ChangeListener(() -> {}));
		table.add(joinButton).colspan(2).center();
		add(table);
		addListener(new UnfocusListener(this));
	}
}
