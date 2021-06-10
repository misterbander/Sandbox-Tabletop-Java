package misterbander.gframework.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.StringBuilder;

public class TextUtils
{
	private static final GlyphLayout GLYPH = new GlyphLayout();
	private static final Vector2 TEMP_VEC = new Vector2();
	
	/**
	 * Returns the dimensions of a text in pixels based on the BitmapFont.
	 * @param text the text
	 * @return A [Vector2] containing the dimensions of the text, in pixels. The returned `Vector2` is not safe for reuse.
	 */
	public static Vector2 textSize(BitmapFont font, String text)
	{
		GLYPH.setText(font, text);
		TEMP_VEC.set(GLYPH.width, GLYPH.height);
		return TEMP_VEC;
	}
	
	/**
	 * Wraps a string to fit within a specified width, adding line feeds between words where necessary.
	 * @param text        the text
	 * @param targetWidth the width of the wrapped text
	 * @return A string wrapped within the specified width.
	 */
	public static String wrap(BitmapFont font, String text, int targetWidth)
	{
		StringBuilder builder = new StringBuilder(); // Current line builder
		StringBuilder peeker = new StringBuilder(); // Current line builder to check if the next word fits within the line
		String[] words = text.split(" ");
		boolean isFirstWord = true;
		// Add each word one by one, moving on to the next line if there's not enough space
		for (String word : words)
		{
			peeker.append(isFirstWord ? word : " " + word); // Have the peeker check if the next word fits
			if (textSize(font, peeker.toString()).x <= targetWidth) // It fits
				builder.append(isFirstWord ? word : " " + word);
			else  // It doesn't fit, move on to the next line
			{
				builder.append("\n").append(word);
				peeker = new StringBuilder(word);
			}
			isFirstWord = false;
		}
		return builder.toString();
	}
}
