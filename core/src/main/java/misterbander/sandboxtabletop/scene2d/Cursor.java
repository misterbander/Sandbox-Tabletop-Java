package misterbander.sandboxtabletop.scene2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;

import misterbander.sandboxtabletop.net.model.User;

public class Cursor extends SmoothMovable
{
	private final Color color;
	private final TextureRegion base, border;
	private final @Null Label usernameLabel;
	
	public Cursor(User user, Skin skin, boolean noLabel)
	{
		color = User.getUserColor(user);
		base = skin.getRegion("cursorbase");
		border = skin.getRegion("cursorborder");
		setSize(base.getRegionWidth(), base.getRegionHeight());
		setPosition(637, 328);
		setTargetPosition(637, 328);
		
		usernameLabel = noLabel ? null : new Label(user.username, skin, "usernametaglabelstyle");
		if (usernameLabel != null)
			usernameLabel.pack();
	}
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		if (usernameLabel != null)
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
		if (usernameLabel != null)
			usernameLabel.draw(batch, parentAlpha);
	}
}
