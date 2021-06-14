/*
 * This file is part of Siebe Projects samples.
 *
 * Siebe Projects samples is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Siebe Projects samples is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with Siebe Projects samples.  If not, see <http://www.gnu.org/licenses/>.
 */

package misterbander.sandboxtabletop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

import com.badlogic.gdx.Gdx;

import misterbander.gframework.scene2d.KeyboardHeightObserver;

/**
 * The keyboard height provider, this class uses a {@code PopupWindow}
 * to calculate the window height when the floating keyboard is opened and closed.
 */
public class KeyboardHeightProvider extends PopupWindow
{
	/** The keyboard height observer. */
	private KeyboardHeightObserver observer;
	
	/** The view that is used to calculate the keyboard height. */
	private final View popupView;
	
	/** The parent view. */
	private final View parentView;
	
	/** The root activity that uses this {@code KeyboardHeightProvider}. */
	private final Activity activity;
	
	private int prevKeyboardHeight;
	
	/**
	 * Construct a new {@code KeyboardHeightProvider}
	 * @param activity the parent activity
	 */
	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	public KeyboardHeightProvider(Activity activity)
	{
		super(activity);
		this.activity = activity;
		
		LayoutInflater inflator = (LayoutInflater)activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		popupView = inflator.inflate(R.layout.popup_window, null, false);
		setContentView(popupView);
		
		setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE | LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
		
		parentView = activity.findViewById(android.R.id.content);
		
		setWidth(0);
		setHeight(LayoutParams.MATCH_PARENT);
		
		popupView.getViewTreeObserver().addOnGlobalLayoutListener(this::handleOnGlobalLayout);
	}
	
	/**
	 * Start the {@code KeyboardHeightProvider}, this must be called after the {@code onResume()} of the {@code Activity}.
	 * {@code PopupWindow}s are not allowed to be registered before the onResume has finished
	 * of the {@code Activity}.
	 */
	public void start()
	{
		if (!isShowing() && parentView.getWindowToken() != null)
		{
			setBackgroundDrawable(new ColorDrawable(0));
			showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
		}
	}
	
	/**
	 * Close the keyboard height provider,
	 * this provider will not be used anymore.
	 */
	public void close()
	{
		this.observer = null;
		dismiss();
	}
	
	/**
	 * Set the keyboard height observer to this provider. The
	 * observer will be notified when the keyboard height has changed.
	 * For example when the keyboard is opened or closed.
	 * @param observer the observer to be added to this provider
	 */
	public void setKeyboardHeightObserver(KeyboardHeightObserver observer)
	{
		this.observer = observer;
	}
	
	/**
	 * Popup window itself is as big as the window of the {@code Activity}.
	 * The keyboard can then be calculated by extracting the popup view bottom
	 * from the activity window height.
	 */
	@SuppressWarnings("deprecation")
	private void handleOnGlobalLayout()
	{
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		Rect rect = new Rect();
		popupView.getWindowVisibleDisplayFrame(rect);
		
		// REMIND, you may like to change this using the fullscreen size of the phone
		// and also using the status bar and navigation bar heights of the phone to calculate
		// the keyboard height. But this worked fine on a Nexus.
		int orientation = getScreenOrientation();
		int keyboardHeight = metrics.heightPixels - rect.bottom;
		
		if (keyboardHeight == 0)
			notifyKeyboardHeightChanged(0, orientation);
		else if (orientation == Configuration.ORIENTATION_PORTRAIT)
			notifyKeyboardHeightChanged(keyboardHeight, orientation);
		else
			notifyKeyboardHeightChanged(keyboardHeight, orientation);
	}
	
	private int getScreenOrientation()
	{
		return activity.getResources().getConfiguration().orientation;
	}
	
	private void notifyKeyboardHeightChanged(int height, int orientation)
	{
		if (height == prevKeyboardHeight || height > Gdx.graphics.getHeight())
			return;
		prevKeyboardHeight = height;
		if (observer != null)
			observer.onKeyboardHeightChanged(height, orientation);
	}
}
