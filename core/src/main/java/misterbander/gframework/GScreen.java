package misterbander.gframework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import misterbander.gframework.scene2d.GObject;
import misterbander.gframework.scene2d.KeyboardHeightObserver;

public abstract class GScreen<T extends GFramework> extends ScreenAdapter
{
	public final T game;
	
	public final OrthographicCamera camera = new OrthographicCamera();
	public final OrthographicCamera uiCamera = new OrthographicCamera();
	/** Viewport to project camera contents. */
	public final ExtendViewport viewport = new ExtendViewport(1280, 720, camera);
	/** Viewport to project UI contents */
	public final ExtendViewport uiViewport = new ExtendViewport(1280, 720, uiCamera);
	public final Stage stage;
	public final Stage uiStage;
	
	public final ObjectSet<GObject<T>> scheduledAddingGObjects = new ObjectSet<>();
	public final ObjectSet<GObject<T>> scheduledRemovalGObjects = new ObjectSet<>();
	
	public final ObjectSet<KeyboardHeightObserver> keyboardHeightObservers = new ObjectSet<>();
	
	public GScreen(T game)
	{
		this.game = game;
		camera.setToOrtho(false);
		uiCamera.setToOrtho(false);
		stage = new Stage(viewport, game.getBatch());
		uiStage = new Stage(uiViewport, game.getBatch());
	}
	
	@Override
	public void show()
	{
		Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, stage));
	}
	
	/**
	 * Spawns the GObject into the world and adds it to the stage. Calls {@code GObject::onSpawn()}.
	 */
	public void spawnGObject(GObject<T> gObject)
	{
		stage.addActor(gObject);
		gObject.onSpawn();
	}
	
	public void scheduleSpawnGObject(GObject<T> gObject)
	{
		scheduledAddingGObjects.add(gObject);
	}
	
	@Override
	public void resize(int width, int height)
	{
		camera.position.set(viewport.getMinWorldWidth()/2, viewport.getMinWorldHeight()/2, 0);
		viewport.update(width, height, false);
		uiViewport.update(width, height, true);
		Gdx.graphics.requestRendering();
	}
	
	@Override
	public void render(float delta)
	{
		clearScreen();
		
		camera.update();
		game.getBatch().setProjectionMatrix(camera.combined);
		game.getShapeRenderer().setProjectionMatrix(camera.combined);
		game.getShapeDrawer().update();
		stage.act(delta);
		stage.draw();
		
		uiCamera.update();
		game.getBatch().setProjectionMatrix(uiCamera.combined);
		game.getShapeRenderer().setProjectionMatrix(uiCamera.combined);
		game.getShapeDrawer().update();
		uiStage.act(delta);
		uiStage.draw();
		
		for (GObject<T> gObject : scheduledAddingGObjects)
			spawnGObject(gObject);
		scheduledAddingGObjects.clear();
		for (GObject<T> gObject : scheduledRemovalGObjects)
			scheduledRemovalGObjects.remove(gObject);
		scheduledRemovalGObjects.clear();
	}
	
	/**
	 * Clears the screen and paints it black. Gets called every frame.
	 *
	 * You can override this to change the background color.
	 */
	public void clearScreen()
	{
		Gdx.gl.glClearColor(152/255F, 0, 132/255F, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}
	
	@Override
	public void dispose()
	{
		stage.dispose();
	}
}
