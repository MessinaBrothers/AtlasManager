package com.afroant.atlasmanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Model extends JPanel {
	private static final long serialVersionUID = 1L;

	private String displayMessage;
	
	private int gridSize = 16;
	
	private int currentAtlasLine;
	
	// atlas header information
	private String atlasFileName, atlasFormat, atlasFilter, atlasRepeat;
	
	private Data data;
	
	private float spritesheetWidth = 256, spritesheetHeight = 256;
	
	private int newSpriteIndex = 0;
	
	public Model() {
		data = new Data();
		
		this.newAtlas();
		
		displayMessage = "Ready...";
	}
	
	public void loadImage() {
		displayMessage = "Loading image...";
		
	    JFileChooser fc;
	    fc = new JFileChooser();
		
	    int returnVal = fc.showOpenDialog(this);
	    
		File javaFile = fc.getSelectedFile();
		
		switch (returnVal) {
		case JFileChooser.APPROVE_OPTION:
			displayMessage = "Opening: " + javaFile.getName();
			break;
		case JFileChooser.CANCEL_OPTION:
			displayMessage = "Loading image cancelled.";
			return;
		case JFileChooser.ERROR_OPTION:
			displayMessage = "ERROR when loading image.";
			return;
		}
		
		String absPath = javaFile.getAbsolutePath();
		
		data.setSpritesheetPath(absPath);
		
		data.notifyObservers(new Object[] { Data.Message.SPRITESHEET_CHANGED });
	}
	
	public void loadAtlas() {
		displayMessage = "Loading atlas...";
		
	    JFileChooser fc;
	    fc = new JFileChooser();
		
	    int returnVal = fc.showOpenDialog(this);
	    
		File javaFile = fc.getSelectedFile();
		
		switch (returnVal) {
		case JFileChooser.APPROVE_OPTION:
			displayMessage = "Opening: " + javaFile.getName();
			break;
		case JFileChooser.CANCEL_OPTION:
			displayMessage = "Loading atlas cancelled.";
			return;
		case JFileChooser.ERROR_OPTION:
			displayMessage = "ERROR when loading atlas.";
			return;
		}
		
		String absPath = javaFile.getAbsolutePath();
		FileHandle file = Gdx.files.absolute(absPath);
		
		String[] fileLines = file.readString().split("\n");
		
		if (fileLines.length == 0) {
			displayMessage = "ERROR: atlas is empty";
			return;
		}
		
		data.clear();
		
		currentAtlasLine = 0;
		
		// get header info
		boolean isParsed = parseHeader(fileLines);
		
		if (isParsed == false) {
			return;
		}
		
		// get sprite info
		String line = "";
		
		do {
			line = getNextLine(fileLines);
			
			isParsed = parseSprite(line, fileLines);
			
			if (isParsed == false) {
				return;
			}
		} while (currentAtlasLine < fileLines.length);
		
		displayMessage = "Opened " + file.name();
		
		data.notifyObservers(new Object[] { Data.Message.SPRITE_LIST_CHANGED });
	}
	
	public void saveAtlas() {
		displayMessage = "Saving atlas...";
		
	    JFileChooser fc;
	    fc = new JFileChooser();
		
	    int returnVal = fc.showSaveDialog(this);
	    
		File javaFile = fc.getSelectedFile();
		
		switch (returnVal) {
		case JFileChooser.APPROVE_OPTION:
			displayMessage = "Opening: " + javaFile.getName();
			break;
		case JFileChooser.CANCEL_OPTION:
			displayMessage = "Loading atlas cancelled.";
			return;
		case JFileChooser.ERROR_OPTION:
			displayMessage = "ERROR when loading atlas.";
			return;
		}
		
		try {
			String output = "";
			
			// write header
			output += atlasFileName + "\n";
			output += atlasFormat + "\n";
			output += atlasFilter + "\n";
			output += atlasRepeat + "\n";
			
			// write sprites
			for (Sprite sprite : data.getSprites()) {
				output += sprite.key.name + "\n";
				output += "\trotate: false\n";
				output += String.format("\txy: %d, %d\n", sprite.x, sprite.y);
				output += String.format("\tsize: %d, %d\n", sprite.width, sprite.height);
				output += String.format("\torig: %d, %d\n", sprite.width, sprite.height);
				output += "\toffset: 0, 0\n";
				output += String.format("\tindex: %d\n", sprite.key.index);
			}
			
//			System.out.println(output);
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(fc.getSelectedFile()));
			writer.write(output);
			writer.close();
			displayMessage = "File has been saved";
		} catch (IOException e) {
			displayMessage = "ERROR: could not read " + fc.getSelectedFile().getName();
			return;
		}
	}
	
	public void newAtlas() {
		displayMessage = "Creating atlas...";
		
		newSpriteIndex = 0;
		
		data.clear();
		
		atlasFileName = data.getSpritesheetPath();
		atlasFileName = atlasFileName.substring(atlasFileName.lastIndexOf('\\') + 1, atlasFileName.length());
		
		atlasFormat = "format: RGBA8888";
		atlasFilter = "filter: Nearest, Nearest";
		atlasRepeat = "repeat: none";
		
		data.notifyObservers(new Object[] { Data.Message.SPRITE_LIST_CHANGED });
		data.notifyObservers(new Object[] { Data.Message.SPRITESHEET_CHANGED });
	}
	
	public void quit() {
		displayMessage = "Quitting...";
		Gdx.app.exit();
	}
	
	public void selectSprite(String selection) {
		// find the datum that corresponds with selection
		String[] s = selection.split(" ");
		
		String name = s[0];
		
		int index = -1;
		
		if (s.length > 1) {
			String indexString = s[1];
			indexString = indexString.replace("(", "");
			indexString = indexString.replace(")", "");
			index = Integer.parseInt(indexString);
		}
		
		Sprite sprite = data.getSprite(name, index);
		
		data.setCurrentSprite(sprite);
		data.notifyObservers(new Object[] { Data.Message.SELECT_DATUM, sprite });
	}
	
	public String getMessage() {
		return displayMessage;
	}

	public int increaseGrid(int i) {
		if (i > 0) {
			gridSize *= 2;
		} else if (i < 0) {
			gridSize /= 2;
		}
		
		gridSize = Math.max(gridSize, 1);
		gridSize = Math.min(gridSize, (int)Math.min(spritesheetWidth, spritesheetHeight));
		
//		gridSize += i;
		
		return gridSize;
	}
	
	public int getGridSize() {
		return gridSize;
	}

	public float getSpritesheetWidth() {
		return spritesheetWidth;
	}

	public float getSpritesheetHeight() {
		return spritesheetHeight;
	}
	
	private boolean parseHeader(String[] lines) {
		String line = "";
		
		line = getNextLine(lines);
		
		if (line.endsWith(".png")) {
			atlasFileName = line;
		} else {
			displayMessage = "ERROR: heading must contain .png";
			return false;
		}
		
		line = getNextLine(lines);
		
		if (line.startsWith("format: ")) {
			atlasFormat = line;
		} else {
			displayMessage = "ERROR: heading must contain a format";
			return false;
		}
		
		line = getNextLine(lines);
		
		if (line.startsWith("filter: ")) {
			atlasFilter = line;
		} else {
			displayMessage = "ERROR: heading must contain a filter";
			return false;
		}
		
		line = getNextLine(lines);
		
		if (line.startsWith("repeat: ")) {
			atlasRepeat = line;
		} else {
			displayMessage = "ERROR: heading must contain a repeat";
			return false;
		}
		
		return true;
	}
	
	private boolean parseSprite(String spriteName, String[] lines) {
//		grass0
//		rotate: false
//		xy: 0, 0
//		size: 16, 16
//		orig: 16, 16
//		offset: 0, 0
//		index: -1
		
		if (currentAtlasLine + 5 > lines.length) {
			displayMessage = "ERROR: sprite " + spriteName + " contains insufficient data";
			return false;
		}
		
		String line = getNextLine(lines);
		
		if (parseSpriteHelper(spriteName, line, "rotate") == false) return false;
		
		line = getNextLine(lines);
		
		if (parseSpriteHelper(spriteName, line, "xy") == false) return false;
		Vector2 position = parseSpriteVector2(line, "xy");
		if (position == null) {
			displayMessage = String.format("ERROR, sprite %s contains invalid line (%s)", spriteName, line);
			return false;
		}
		
		line = getNextLine(lines);
		
		if (parseSpriteHelper(spriteName, line, "size") == false) return false;
		Vector2 size = parseSpriteVector2(line, "size");
		if (size == null) {
			displayMessage = String.format("ERROR, sprite %s contains invalid line (%s)", spriteName, line);
			return false;
		}
		
		line = getNextLine(lines);
		
		if (parseSpriteHelper(spriteName, line, "orig") == false) return false;
		
		line = getNextLine(lines);
		
		if (parseSpriteHelper(spriteName, line, "offset") == false) return false;
		
		line = getNextLine(lines);
		
		if (parseSpriteHelper(spriteName, line, "index") == false) return false;
		Integer index = parseSpriteNumber(line, "index");
		if (index == null) {
			displayMessage = String.format("ERROR, sprite %s contains invalid line (%s)", spriteName, line);
			return false;
		}
		
//		System.out.printf("Parsed sprite: %s %s, %s, %s\n", spriteName, position, size, index);
		
		//TODO store information
		Sprite datum = new Sprite(spriteName, index, (int)position.x, (int)position.y, (int)size.x, (int)size.y);
		
		data.add(datum);
		
		return true;
	}
	
	private Vector2 parseSpriteVector2(String line, String arg) {
		// xy: 48, 64
		String vectorString = line.replace(arg + ": ", "");
		
		String[] ints = vectorString.split(", ");
		
		if (ints.length < 2) {
			return null;
		}
		
		try {
			Integer x = Integer.parseInt(ints[0]);
			Integer y = Integer.parseInt(ints[1]);
			
			return new Vector2(x, y);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	private Integer parseSpriteNumber(String line, String arg) {
		// index: -1
		String integerString = line.replace(arg + ": ", "");
		
		try {
			Integer i = Integer.parseInt(integerString);
			
			return i;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private boolean parseSpriteHelper(String spriteName, String line, String arg) {
		if (line.startsWith(arg + ": ") == false) {
			displayMessage = "ERROR: sprite " + spriteName + " doesn't have " + arg;
			return false;
		}
		return true;
	}
	
	private String getNextLine(String[] lines) {
		String line = "";
		
		do {
//			System.out.printf("Parseing line %d of %d\n", currentAtlasLine, lines.length - 1);
			line = lines[currentAtlasLine].trim();
			currentAtlasLine += 1;
		} while (line.isEmpty() && currentAtlasLine < lines.length);
		
		return line;
	}
	
	public void registerSpriteList(Observer o) {
		data.addObserver(o);
	}
	
	public ArrayList<Sprite> getData() {
		return data.getSprites();
	}
	
	public Texture getSpritesheetTexture() {
		try {
			Texture spritesheetTexture = new Texture(Gdx.files.absolute(data.getSpritesheetPath()));
			
			spritesheetWidth = spritesheetTexture.getWidth();
			spritesheetHeight = spritesheetTexture.getHeight();
			
			return spritesheetTexture;
		} catch (GdxRuntimeException e) {
			displayMessage = "ERROR: could not load image " + data.getSpritesheetPath();
		}
		
		return null;
	}
	
	public Sprite getCurrentSprite() {
		return data.getCurrentSprite();
	}
	
	public void clickedOn(float x, float y) {
		int gridX = (int) (x / (int)getGridSize()) * getGridSize();
		
		int gridY = (int) (y / (int)getGridSize()) * getGridSize();
		
//		System.out.printf("User clicked on %d, %d\n", gridX, gridY);
		
		Sprite sprite = getCurrentSprite();
		
		data.setPosition(sprite, gridX, gridY);
		
		data.notifyObservers(new Object[] { Data.Message.SPRITE_CHANGED, sprite });
	}

	public void increaseSpriteWidth() {
		Sprite sprite = data.getCurrentSprite();
		
		if (sprite != null) {
			int width = sprite.width;
			
			width += gridSize;
			width = Math.min(width, (int)spritesheetWidth);
			
			data.setWidth(sprite, width);
			
			data.notifyObservers(new Object[] { Data.Message.SPRITE_CHANGED, sprite });
		}
	}

	public void decreaseSpriteWidth() {
		Sprite sprite = data.getCurrentSprite();
		
		if (sprite != null) {
			int width = sprite.width;
			
			width -= gridSize;
			width = Math.max(width, gridSize);
			
			data.setWidth(sprite, width);
			
			data.notifyObservers(new Object[] { Data.Message.SPRITE_CHANGED, sprite });
		}
	}

	public void increaseSpriteHeight() {
		Sprite sprite = data.getCurrentSprite();
		
		if (sprite != null) {
			int height = sprite.height;
			
			height += gridSize;
			height = Math.min(height, (int)spritesheetHeight);
			
			data.setHeight(sprite, height);
			
			data.notifyObservers(new Object[] { Data.Message.SPRITE_CHANGED, sprite });
		}
	}

	public void decreaseSpriteHeight() {
		Sprite sprite = data.getCurrentSprite();
		
		if (sprite != null) {
			int height = sprite.height;
			
			height -= gridSize;
			height = Math.max(height, gridSize);
			
			data.setHeight(sprite, height);
			
			data.notifyObservers(new Object[] { Data.Message.SPRITE_CHANGED, sprite });
		}
	}

	public void increaseSpriteIndex() {
		Sprite sprite = data.getCurrentSprite();
		
		if (sprite != null) {
			int index = sprite.key.index;
			index += 1;
			
			data.setIndex(sprite, index);
			
			data.notifyObservers(new Object[] { Data.Message.SPRITE_LIST_CHANGED });
		}
	}

	public void decreaseSpriteIndex() {
		Sprite sprite = data.getCurrentSprite();
		
		if (sprite != null) {
			int index = sprite.key.index;
			index -= 1;
			index = Math.max(index, -1);
			
			data.setIndex(sprite, index);
			
			data.notifyObservers(new Object[] { Data.Message.SPRITE_LIST_CHANGED });
		}
	}

	public void addSprite() {
		String name = "NewSprite" + newSpriteIndex;
		int index = -1;
		int x = 0;
		int y = 0;
		int width = gridSize;
		int height = gridSize;
		
		Sprite sprite = data.getCurrentSprite();
		
		if (sprite != null) {
			x = sprite.x + sprite.width;
			y = sprite.y;
			width = sprite.width;
			height = sprite.height;
		}
		
		Sprite newSprite = new Sprite(name, index, x, y, width, height);
		
		newSpriteIndex += 1;
		
		data.add(newSprite);
		
		data.notifyObservers(new Object[] { Data.Message.SPRITE_LIST_CHANGED, data.getSprites().size() - 1 });
		
		editSpriteName();
	}

	public void addAnimation() {
		Sprite sprite = data.getCurrentSprite();
		
		if (sprite == null) return;
		
		if (sprite.key.index == -1) {
			sprite.key.index = 0;
		}
		
		Sprite newSprite = new Sprite(sprite.key.name, sprite.key.index + 1, sprite.x + sprite.width, sprite.y, sprite.width, sprite.height);
		
		data.add(newSprite, data.getIndexOf(sprite) + 1);
		
		data.notifyObservers(new Object[] { Data.Message.SPRITE_LIST_CHANGED });
	}

	public void removeSprite() {
		Sprite sprite = data.getCurrentSprite();
		
		if (sprite == null) return;
		
		int index = data.remove(sprite);
		
		data.notifyObservers(new Object[] { Data.Message.SPRITE_LIST_CHANGED, index });
	}

	public void editSpriteName() {
		Sprite sprite = data.getCurrentSprite();
		if (sprite == null) return;
		
		String newName = (String)JOptionPane.showInputDialog(this, "Enter sprite name:", "Edit Sprite Name", JOptionPane.PLAIN_MESSAGE, null, null, sprite.key.name);
		
		if (newName == null) newName = sprite.key.name;
		
		data.setName(sprite, newName);
		
		data.notifyObservers(new Object[] { Data.Message.SPRITE_LIST_CHANGED });
	}

	public void displayAboutScreen() {
		JOptionPane.showMessageDialog(this, 
				"ATLAS MANAGER\n"
				+ "\n"
				+ "Atlas Manager v " + Main.VERSION_NUMBER + "\n"
				+ "Copyright \u00A9 2016 AfroAnt, redanthelion.com. All Rights Reserved.", "About Atlas Manager", JOptionPane.PLAIN_MESSAGE);
	}
}
