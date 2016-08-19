package com.afroant.atlasmanager;

public class Sprite {

//grass0
//	rotate: false
//	xy: 0, 0
//	size: 16, 16
//	orig: 16, 16
//	offset: 0, 0
//	index: -1
	
	public Key key;
	
	public int x, y, width, height;
	
	public Sprite(String name, int index, int x, int y, int width, int height) {
		this.key = new Key(name, index);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public class Key {
		
		public String name;
		public int index;
		
		public Key(String name, int index) {
			this.name = name;
			this.index = index;
		}
		
		public Key(String name) {
			this(name, -1);
		}
	}
	
	@Override
	public String toString() {
		return key.name + " (" + key.index + ")";
	}
}
