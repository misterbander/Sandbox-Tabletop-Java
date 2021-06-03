package misterbander.sandboxtabletop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import misterbander.gframework.GFramework;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class SandboxTabletop extends GFramework
{
	// Fonts
	public BitmapFont jhengheiui, jhengheiuiMini, jhengheiuiMax;
	
	// Skins
	public final Skin skin = new Skin();
	
	@Override
	public void create()
	{
		super.create();
		Gdx.graphics.setContinuousRendering(false);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		// Load assets
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/msjhl.ttc"));
		jhengheiui = generator.generateFont(generateFontParameter(40));
		FreeTypeFontGenerator miniGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/msjhl.ttc"));
		jhengheiuiMini = miniGenerator.generateFont(generateFontParameter(25));
		FreeTypeFontGenerator maxGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/msjhl.ttc"));
		jhengheiuiMax = maxGenerator.generateFont(generateFontParameter(50));
		
		AssetManager assetManager = getAssetManager();
		assetManager.load("textures/logo.png", Texture.class);
		assetManager.load("textures/gui.atlas", TextureAtlas.class);
		assetManager.load("sounds/click.wav", Sound.class);
		assetManager.finishLoading();
		Gdx.app.log("SandboxTabletop | INFO", "Finished loading assets!");
		
		// Initialize skin
		skin.add("logo", assetManager.get("textures/logo.png", Texture.class));
		skin.addRegions(assetManager.get("textures/gui.atlas", TextureAtlas.class));
		skin.add("infolabelstyle", new Label.LabelStyle(jhengheiui, Color.WHITE));
		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("button");
		textButtonStyle.over = skin.getDrawable("buttonover");
		textButtonStyle.down = skin.getDrawable("buttondown");
		textButtonStyle.font = jhengheiui;
		textButtonStyle.fontColor = Color.WHITE;
		textButtonStyle.downFontColor = Color.BLACK;
		skin.add("textbuttonstyle", textButtonStyle);
		
		setScreen(new MenuScreen(this));
	}
	
	private FreeTypeFontGenerator.FreeTypeFontParameter generateFontParameter(int size)
	{
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.minFilter = Texture.TextureFilter.Linear;
		parameter.magFilter = Texture.TextureFilter.Linear;
		parameter.incremental = true;
		parameter.size = size;
		return parameter;
	}
}
