package com.afroant.atlasmanager;

import java.util.ArrayList;
import java.util.Observable;

public class Data extends Observable {

	private ArrayList<Sprite> sprites;
	
	private Sprite currentSprite;
	
	private String spritesheetPath;
	
	public static enum Message {
		SPRITE_LIST_CHANGED, SELECT_DATUM, SPRITESHEET_CHANGED, SPRITE_CHANGED;
	}
	
	public Data() {
		sprites = new ArrayList<Sprite>();
		
		clear();
	}

	public void add(Sprite sprite) {
		add(sprite, sprites.size());
	}

	public void add(Sprite sprite, int i) {
		sprites.add(i, sprite);
		
		setChanged();
	}
	
	public ArrayList<Sprite> getSprites() {
		return sprites;
	}
	
	public void clear() {
		sprites.clear();
		
		currentSprite = null;
		
		spritesheetPath = "sample/sample.png";
		
		setChanged();
	}
	
	public void setCurrentSprite(Sprite sprite) {
		currentSprite = sprite;
		
		setChanged();
	}
	
	public Sprite getCurrentSprite() {
		return currentSprite;
	}

	public Sprite getSprite(String name, int index) {
		for (Sprite sprite : sprites) {
			if (sprite.key.name.equals(name) && sprite.key.index == index) {
				return sprite;
			}
		}
		return null;
	}
	
	public void setSpritesheetPath(String path) {
		spritesheetPath = path;
		
		setChanged();
	}
	
	public String getSpritesheetPath() {
		return spritesheetPath;
	}
	
	public void setPosition(Sprite sprite, int x, int y) {
		sprite.x = x;
		sprite.y = y;
		
		setChanged();
	}

	public void setWidth(Sprite sprite, int width) {
		sprite.width = width;
		
		setChanged();
	}

	public void setHeight(Sprite sprite, int height) {
		sprite.height = height;
		
		setChanged();
	}

	public void setIndex(Sprite sprite, int index) {
		sprite.key.index = index;
		
		setChanged();
	}

	public int remove(Sprite sprite) {
		int index = sprites.indexOf(sprite);
		
		sprites.remove(index);
		
		if (currentSprite == sprite) {
			currentSprite = null;
		}
		
		setChanged();
		
		return index;
	}

	public void setName(Sprite sprite, String name) {
		sprite.key.name = name;
		
		setChanged();
	}

	public int getIndexOf(Sprite sprite) {
		return sprites.indexOf(sprite);
	}
}
