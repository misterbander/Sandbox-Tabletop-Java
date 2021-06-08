package misterbander.sandboxtabletop;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;

import misterbander.gframework.scene2d.MBTextField;
import misterbander.gframework.scene2d.UnfocusListener;
import misterbander.sandboxtabletop.net.ConnectionEventListener;

public class RoomScreen extends SandboxTabletopScreen implements ConnectionEventListener
{
	private final VerticalGroup chatHistoryLabels = new VerticalGroup();
	
	public RoomScreen(SandboxTabletop game)
	{
		super(game);
		
		// Set up UI
		
		ImageButton menuButton = new ImageButton(game.skin, "menubuttonstyle");
		
		MBTextField chatTextField = new MBTextField("", game.skin, "chattextfieldstyle");
		chatTextField.setMessageText("Tap here to chat...");
		
		Label label = new Label("<User1> Tets 1", game.skin, "chatlabelstyle");
		Label label1 = new Label("<user 2> Test 2 this is really long", game.skin, "chatlabelstyle");
		
		Table chatTable = new Table();
		chatTable.add(chatTextField).growX();
		chatTable.row();
		chatTable.add(chatHistoryLabels).left();
		chatHistoryLabels.columnAlign(Align.left);
		chatHistoryLabels.addActor(label);
		chatHistoryLabels.addActor(label1);
		
		Table table = new Table();
		table.setFillParent(true);
		table.setDebug(true, true);
		table.top();
		table.add(menuButton).top().pad(16);
		table.add(chatTable).pad(16).top().expandX().fillX();
		
		uiStage.addActor(table);
		uiStage.addListener(new UnfocusListener(chatTextField));
	}
}
