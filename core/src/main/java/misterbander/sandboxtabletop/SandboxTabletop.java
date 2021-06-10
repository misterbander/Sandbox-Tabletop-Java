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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
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
	
	/**
	 * Random UUID for player identification.
	 */
	public UUID uuid = UUID.randomUUID();
	
	@Override
	public void create()
	{
		super.create();
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
		
		Label.LabelStyle infoLabelStyle = new Label.LabelStyle(jhengheiuiMini, Color.WHITE);
		Label.LabelStyle chatLabelStyle = new Label.LabelStyle(infoLabelStyle);
		chatLabelStyle.background = skin.newDrawable("chatbackground");
		chatLabelStyle.background.setTopHeight(4);
		chatLabelStyle.background.setLeftWidth(16);
		chatLabelStyle.background.setRightWidth(16);
		chatLabelStyle.background.setBottomHeight(4);
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
		ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
		scrollPaneStyle.background = skin.newDrawable("chatbackground");
		scrollPaneStyle.vScrollKnob = skin.getDrawable("textcursor");
		Button.ButtonStyle closeButtonStyle = new Button.ButtonStyle();
		closeButtonStyle.up = skin.getDrawable("closebutton");
		closeButtonStyle.over = skin.getDrawable("closebuttonover");
		closeButtonStyle.down = skin.getDrawable("closebuttondown");
		ImageButton.ImageButtonStyle imageButtonStyleBase = new ImageButton.ImageButtonStyle();
		imageButtonStyleBase.up = skin.getDrawable("button");
		imageButtonStyleBase.over = skin.getDrawable("buttonover");
		imageButtonStyleBase.down = skin.getDrawable("buttondown");
		ImageButton.ImageButtonStyle menuButtonStyle = new ImageButton.ImageButtonStyle(imageButtonStyleBase);
		menuButtonStyle.imageUp = skin.getDrawable("menuicon");
		menuButtonStyle.imageDown = skin.getDrawable("menuicondown");
		MBTextField.MBTextFieldStyle mbTextFieldStyleBase = new MBTextField.MBTextFieldStyle();
		mbTextFieldStyleBase.font = jhengheiuiMini;
		mbTextFieldStyleBase.fontColor = Color.WHITE;
		mbTextFieldStyleBase.messageFontColor = Color.GRAY;
		mbTextFieldStyleBase.focusedFontColor = Color.WHITE;
		mbTextFieldStyleBase.cursor = skin.getDrawable("textcursor");
		mbTextFieldStyleBase.selection = skin.getDrawable("textselection");
		mbTextFieldStyleBase.disabledFontColor = new Color(0xAAAAAAFF);
		MBTextField.MBTextFieldStyle chatTextFieldStyle = new MBTextField.MBTextFieldStyle(mbTextFieldStyleBase);
		chatTextFieldStyle.background = skin.getDrawable("chatbackground");
		chatTextFieldStyle.background.setTopHeight(16);
		chatTextFieldStyle.background.setLeftWidth(16);
		chatTextFieldStyle.background.setRightWidth(16);
		chatTextFieldStyle.background.setBottomHeight(16);
		MBTextField.MBTextFieldStyle formtextfieldstyle = new MBTextField.MBTextFieldStyle(mbTextFieldStyleBase);
		formtextfieldstyle.background = skin.getDrawable("textfield");
		formtextfieldstyle.focusedBackground = skin.getDrawable("textfieldfocused");
		
		skin.add("infolabelstyle", infoLabelStyle);
		skin.add("chatlabelstyle", chatLabelStyle);
		skin.add("textbuttonstyle", textButtonStyle);
		skin.add("windowstyle", windowStyle);
		skin.add("scrollpanestyle", scrollPaneStyle);
		skin.add("closebuttonstyle", closeButtonStyle);
		skin.add("menubuttonstyle", menuButtonStyle);
		skin.add("chattextfieldstyle", chatTextFieldStyle);
		skin.add("formtextfieldstyle", formtextfieldstyle);
		
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
