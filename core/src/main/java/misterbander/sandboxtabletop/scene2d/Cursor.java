package misterbander.sandboxtabletop.scene2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import misterbander.sandboxtabletop.net.model.User;

public class Cursor extends SmoothMovable
{
	private final User user;
	private final Color color;
	private final TextureRegion base, border;
	private final Label usernameLabel;
	
	public Cursor(User user, Skin skin, String baseRegionName, String borderRegionName)
	{
		this.user = user;
		color = User.getUserColor(user);
		base = skin.getRegion(baseRegionName);
		border = skin.getRegion(borderRegionName);
		setSize(base.getRegionWidth(), base.getRegionHeight());
		setPosition(637, 328);
		setTargetPosition(637, 328);
		
		usernameLabel = new Label(user.username, skin, "usernametaglabelstyle");
		usernameLabel.pack();
	}
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		usernameLabel.setPosition(getX() - 3 + getWidth()/2, getY(), Align.topLeft);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		batch.setColor(1, 1, 1, 1);
		batch.draw(border, getX(), getY());
		batch.setColor(color);
		batch.draw(base, getX(), getY());
		batch.setColor(Color.WHITE);
		usernameLabel.draw(batch, parentAlpha);
	}
}
