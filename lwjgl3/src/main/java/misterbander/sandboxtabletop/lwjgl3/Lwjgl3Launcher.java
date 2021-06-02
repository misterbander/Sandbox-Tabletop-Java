package misterbander.sandboxtabletop.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import misterbander.sandboxtabletop.SandboxTabletop;

/**
 * Launches the desktop (LWJGL3) application.
 */
public class Lwjgl3Launcher
{
	public static void main(String[] args)
	{
		new Lwjgl3Application(new SandboxTabletop(), getDefaultConfiguration());
	}
	
	private static Lwjgl3ApplicationConfiguration getDefaultConfiguration()
	{
		Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
		configuration.setTitle("Sandbox Tabletop");
		configuration.setWindowedMode(1280, 720);
		configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
		return configuration;
	}
}