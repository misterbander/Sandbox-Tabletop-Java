package misterbander.sandboxtabletop.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

import misterbander.gframework.scene2d.AccessibleInputWindow;
import misterbander.sandboxtabletop.SandboxTabletop;
import misterbander.sandboxtabletop.SandboxTabletopScreen;

/**
 * Represents generic windows. All windows will inherit from this class.
 */
public abstract class SandboxTabletopWindow extends AccessibleInputWindow
{
	protected final SandboxTabletop game;
	protected final SandboxTabletopScreen screen;
	
	public final Button closeButton;
	
	public SandboxTabletopWindow(SandboxTabletopScreen screen, String title, boolean isModal)
	{
		super(title, screen.game.skin, "windowstyle");
		game = screen.game;
		this.screen = screen;
		
		closeButton = new Button(game.skin, "closebuttonstyle");
		closeButton.addListener(screen.new ChangeListener(this::close));
		
		getTitleTable().add(closeButton).right();
		getTitleTable().pad(2, 16, 0, 8);
		
		setModal(isModal);
		setVisible(false);
	}
	
	public void show()
	{
		setVisible(true);
		pack();
		centerPosition();
	}
	
	void centerPosition()
	{
		assert getStage() != null;
		float x = (getStage().getWidth() - getWidth())/2;
		float y = (getStage().getHeight() - getHeight())/2;
		setPosition(x, y);
	}
	
	protected void close()
	{
		setVisible(false);
		Gdx.input.setOnscreenKeyboardVisible(false);
	}
}
