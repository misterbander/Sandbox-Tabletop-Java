package misterbander.gframework;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import space.earlygrey.shapedrawer.ShapeDrawer;

/**
 * Framework built on top of LibGDX.
 * <p>
 * `GFramework` is the central spine, it contains references to the {@link SpriteBatch}, {@link ShapeRenderer}, {@link ShapeDrawer},
 * default {@link BitmapFont} and an {@link AssetManager}.
 * <p>
 */
public abstract class GFramework extends Game
{
	private PolygonSpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private ShapeDrawer shapeDrawer;
	private AssetManager assetManager;
	
	@Override
	public void create()
	{
		batch = new PolygonSpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		Texture texture = new Texture(pixmap);
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		TextureRegion region = new TextureRegion(texture);
		pixmap.dispose();
		shapeDrawer = new ShapeDrawer(batch, region);
		
		assetManager = new AssetManager();
	}
	
	public PolygonSpriteBatch getBatch()
	{
		return batch;
	}
	
	public ShapeRenderer getShapeRenderer()
	{
		return shapeRenderer;
	}
	
	public ShapeDrawer getShapeDrawer()
	{
		return shapeDrawer;
	}
	
	public AssetManager getAssetManager()
	{
		return assetManager;
	}
	
	public void notifyLayoutSizeChange(int screenHeight)
	{
		if (getScreen() instanceof GScreen)
			((GScreen<?>)getScreen()).onLayoutSizeChange(screenHeight);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		batch.dispose();
	}
}
