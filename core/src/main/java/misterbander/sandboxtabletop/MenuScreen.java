package misterbander.sandboxtabletop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import misterbander.sandboxtabletop.scene2d.ConnectWindow;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class MenuScreen extends SandboxTabletopScreen
{
	private final Texture logo = game.skin.get("logo", Texture.class);
	
	private final Color backgroundAccentColor = new Color(0xBA00A1FF);
	
	private final ConnectWindow connectWindow = new ConnectWindow(this);
	
	public MenuScreen(SandboxTabletop game)
	{
		super(game);
		logo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		
		accessibleInputWindows.add(connectWindow);
	}
	
	@Override
	public void show()
	{
		super.show();
		Table table = new Table();
		table.setFillParent(true);
		table.add(new Image(logo)).top().pad(16);
		
		Table menuTable = new Table();
		TextButton playButton = new TextButton("Play", game.skin, "textbuttonstyle");
		playButton.addListener(new ChangeListener(connectWindow::show));
		TextButton quitButton = new TextButton("Quit", game.skin, "textbuttonstyle");
		quitButton.addListener(new ChangeListener(() -> Gdx.app.exit()));
		menuTable.add(playButton).padBottom(16).row();
		menuTable.add(quitButton);
		
		table.row();
		table.add(menuTable).expand();
		
		stage.addActor(table);
		
		stage.addActor(connectWindow);
	}
	
	@Override
	public void resize(int width, int height)
	{
		viewport.update(width, height, true);
	}
	
	@Override
	public void clearScreen()
	{
		super.clearScreen();
		PolygonSpriteBatch batch = game.getBatch();
		ShapeDrawer shapeDrawer = game.getShapeDrawer();
		batch.begin();
		shapeDrawer.setColor(backgroundAccentColor);
		shapeDrawer.filledRectangle(80, 96, viewport.getWorldWidth() - 96, viewport.getWorldHeight() - 96, 4*MathUtils.degRad);
		batch.end();
	}
}
