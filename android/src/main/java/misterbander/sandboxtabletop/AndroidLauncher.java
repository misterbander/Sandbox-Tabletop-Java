package misterbander.sandboxtabletop;

import android.os.Bundle;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import misterbander.gframework.GScreen;
import misterbander.gframework.scene2d.KeyboardHeightObserver;

/**
 * Launches the Android application.
 */
public class AndroidLauncher extends AndroidApplication implements KeyboardHeightObserver
{
	private final SandboxTabletop sandboxTabletop = new SandboxTabletop();
	private KeyboardHeightProvider keyboardHeightProvider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
		initialize(sandboxTabletop, configuration);
		
		keyboardHeightProvider = new KeyboardHeightProvider(this);
		
		// Make sure to start the keyboard height provider after the onResume
		// of this activity. This is because a popup window must be initialised
		// and attached to the activity root view
		View rootView = getWindow().getDecorView().getRootView();
		rootView.post(() -> keyboardHeightProvider.start());
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		keyboardHeightProvider.setKeyboardHeightObserver(null);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		keyboardHeightProvider.setKeyboardHeightObserver(this);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		keyboardHeightProvider.close();
	}
	
	@Override
	public void onKeyboardHeightChanged(int height, int orientation)
	{
		GScreen<?> gScreen = ((GScreen<?>)sandboxTabletop.getScreen());
		if (gScreen != null)
		{
			for (KeyboardHeightObserver observer : gScreen.keyboardHeightObservers)
				observer.onKeyboardHeightChanged(height, orientation);
		}
	}
}
