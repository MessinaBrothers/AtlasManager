package com.afroant.atlasmanager;

import com.badlogic.gdx.Game;

public class Main extends Game {
	
	public static final String VERSION_NUMBER = "0.2a";
	
	@Override
	public void create () {
		Model model = new Model();
		
		setScreen(new Gui(model));
	}

	@Override
	public void render () {
		super.render();
	}
}
