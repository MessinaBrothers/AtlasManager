package com.afroant.atlasmanager.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.afroant.atlasmanager.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 1000;
		config.height = 800;
		
		config.addIcon("data/icons/icon_128x128.png", Files.FileType.Internal);
		config.addIcon("data/icons/icon_32x32.png", Files.FileType.Internal);
		config.addIcon("data/icons/icon_16x16.png", Files.FileType.Internal);
		
		config.title = "Atlas Manager " + Main.VERSION_NUMBER;
		
		new LwjglApplication(new Main(), config);
	}
}
