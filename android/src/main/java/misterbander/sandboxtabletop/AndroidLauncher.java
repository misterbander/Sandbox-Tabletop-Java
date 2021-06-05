package misterbander.sandboxtabletop;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

/**
 * Launches the Android application.
 */
public class AndroidLauncher extends AndroidApplication
{
	private int prevWidth, prevHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
		SandboxTabletop sandboxTabletop = new SandboxTabletop();
		initialize(sandboxTabletop, configuration);
		
		View rootView = getWindow().getDecorView().getRootView();
		Rect rect = new Rect();
		rootView.getWindowVisibleDisplayFrame(rect);
		prevWidth = rect.width();
		prevHeight = rect.height();
		
		rootView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
		{
			Rect rect1 = new Rect();
			rootView.getWindowVisibleDisplayFrame(rect1);
			if (!(prevWidth == rect1.width() && prevHeight == rect1.height()))
			{
				prevWidth = rect1.width();
				prevHeight = rect1.height();
				sandboxTabletop.notifyLayoutSizeChange(prevHeight);
			}
		});
	}
}