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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import java.util.UUID;

import misterbander.gframework.GFramework;
import misterbander.gframework.scene2d.MBTextField;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class SandboxTabletop extends GFramework
{
	// Fonts
	public BitmapFont jhengheiui, jhengheiuiMini, jhengheiuiMax;
	
	// Skins
	public final Skin skin = new Skin();
	
	/** Random UUID for player identification. */
	public UUID uuid = UUID.randomUUID();
	
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
		skin.add("infolabelstyle", new Label.LabelStyle(jhengheiuiMini, Color.WHITE));
		
		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("button");
		textButtonStyle.over = skin.getDrawable("buttonover");
		textButtonStyle.down = skin.getDrawable("buttondown");
		textButtonStyle.font = jhengheiui;
		textButtonStyle.fontColor = Color.WHITE;
		textButtonStyle.downFontColor = Color.BLACK;
		Window.WindowStyle windowStyle = new Window.WindowStyle();
		windowStyle.background = skin.getDrawable("window");
		windowStyle.titleFont = jhengheiuiMini;
		windowStyle.titleFontColor = Color.WHITE;
		Button.ButtonStyle closeButtonStyle = new Button.ButtonStyle();
		closeButtonStyle.up = skin.getDrawable("closebutton");
		closeButtonStyle.over = skin.getDrawable("closebuttonover");
		closeButtonStyle.down = skin.getDrawable("closebuttondown");
		MBTextField.MBTextFieldStyle mbTextFieldStyle = new MBTextField.MBTextFieldStyle();
		mbTextFieldStyle.background =skin.getDrawable("textfield");
		mbTextFieldStyle.font = jhengheiuiMini;
		mbTextFieldStyle.fontColor = Color.WHITE;
		mbTextFieldStyle.messageFontColor = Color.PINK;
		mbTextFieldStyle.focusedBackground = skin.getDrawable("textfieldfocused");
		mbTextFieldStyle.focusedFontColor = Color.WHITE;
		mbTextFieldStyle.cursor = skin.getDrawable("textcursor");
		mbTextFieldStyle.selection = skin.getDrawable("textselection");
		mbTextFieldStyle.disabledFontColor = new Color(0xAAAAAAFF);
		
		skin.add("textbuttonstyle", textButtonStyle);
		skin.add("windowstyle", windowStyle);
		skin.add("closebuttonstyle", closeButtonStyle);
		skin.add("textfieldstyle", mbTextFieldStyle);
		
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
