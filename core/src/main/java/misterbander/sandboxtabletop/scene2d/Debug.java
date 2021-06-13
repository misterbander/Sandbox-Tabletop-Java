package misterbander.sandboxtabletop.scene2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class Debug extends Actor
{
	private final ShapeDrawer drawer;
	private final Viewport viewport;
	
	public Debug(Viewport viewport, ShapeDrawer drawer)
	{
		this.drawer = drawer;
		this.viewport = viewport;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
//		drawer.rectangle(0, 0, 1280, 720);
		drawer.rectangle(32, 32, 1280 - 64, 720 - 64, Color.GREEN);
	}
}
