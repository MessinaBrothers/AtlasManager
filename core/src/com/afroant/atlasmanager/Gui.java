package com.afroant.atlasmanager;

import java.util.Observable;
import java.util.Observer;

import com.afroant.atlasmanager.Data.Message;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Gui implements Screen, Observer {
	
	private boolean isDebug = false;
	
	private final int
		SPRITE_INFO_WIDTH_PX = 400,
		BORDER_PX = 10,
		BUTTON__LABEL_BORDER_PX = 5,
		BUTTON_ROW_PADDING_PX = 5,
		BUTTON_PADDING_PX = 5;
	
	private Model model;
	
	private GridGui grid;
	private AnimationList animationList;

	// gui
	private Stage stage;
	private TextureAtlas atlas;
	private Skin skin, defaultSkin;
	private BitmapFont whiteFont;
	private Label infoLabel;
	private Table animationListTable;
	private ListStyle listStyle;
	private ScrollPaneStyle scrollStyle;
	private Stack spritesheetStack;
	
	private Label spriteNameLabel, spritePositionLabel, spriteWidthLabel, spriteHeightLabel, spriteIndexLabel;
	
	private int previousIndex = -1;
	private ScrollPane spriteScrollPane;
	
	public Gui(Model model) {
		this.model = model;
	}
	
	@Override
	public void show() {
		model.registerSpriteList(this);
		
		stage = new Stage();
		stage.setViewport(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		stage.setDebugAll(isDebug);
		Gdx.input.setInputProcessor(stage);
		
		grid = new GridGui(model, stage.getCamera());
		
		atlas = new TextureAtlas("ui/ui.pack");
		skin = new Skin(atlas);
		defaultSkin = new Skin(new TextureAtlas("examples/uiskin.atlas"));
		
		whiteFont = new BitmapFont(Gdx.files.internal("ui/white.fnt"), false);
		whiteFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("button_up");
		textButtonStyle.down = skin.getDrawable("button_down");
		textButtonStyle.pressedOffsetX = 2; // move the button 2 pixels to the right when pressed
		textButtonStyle.pressedOffsetY = -2;
		textButtonStyle.font = whiteFont;
		textButtonStyle.fontColor = Color.BLACK;
		textButtonStyle.font.getData().setScale(0.75f);
		
		CheckBoxStyle checkboxStyle = new CheckBoxStyle();
		checkboxStyle.checkboxOff = defaultSkin.getDrawable("check-off");
		checkboxStyle.checkboxOn = defaultSkin.getDrawable("check-on");
		checkboxStyle.font = whiteFont;
		checkboxStyle.fontColor = Color.WHITE;
		
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.background = defaultSkin.getDrawable("default-pane");
		labelStyle.font = whiteFont;
		labelStyle.fontColor = Color.WHITE;
		
		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.background = defaultSkin.getDrawable("default-pane");
		textFieldStyle.font = whiteFont;
		textFieldStyle.fontColor = Color.WHITE;
		
		listStyle = new ListStyle();
		listStyle.background = defaultSkin.getDrawable("default-pane");
		listStyle.selection = defaultSkin.getDrawable("default-pane");
		listStyle.font = whiteFont;
		listStyle.fontColorUnselected = Color.WHITE;
		listStyle.fontColorSelected = Color.GREEN;
		
		scrollStyle = new ScrollPaneStyle();
		scrollStyle.vScrollKnob = defaultSkin.getDrawable("default-scroll");
		
		// main table
		Table guiTable = new Table();
		guiTable.setFillParent(true);
		guiTable.pad(BORDER_PX);
		stage.addActor(guiTable);
		
		// file management
		Table menuTable = new Table();
		guiTable.add(menuTable).expandX().fill().padBottom(BUTTON_ROW_PADDING_PX).colspan(2);
		
		guiTable.row();
		
		// sprites work area
		Table spritesTable = new Table();
		guiTable.add(spritesTable).expand().fill();
		
		// grid management
		Table gridManagementTable = new Table();
		spritesTable.add(gridManagementTable).expandX().fill().padBottom(BUTTON_ROW_PADDING_PX);
		
		spritesTable.row();
		
		// spritesheet
		Table spritesheetTable = new Table();
		spritesTable.add(spritesheetTable).expand().fill();
		
		spritesTable.row();
		
		// Debug display
		Table debugTable = new Table();
		spritesTable.add(debugTable).expandX().fill().padBottom(BUTTON_ROW_PADDING_PX);
		
		Table infoTable = new Table();
		guiTable.add(infoTable).width(SPRITE_INFO_WIDTH_PX).fill();
		
		Table spriteInfoTable = new Table();
		infoTable.add(spriteInfoTable).expandX().fill();
		
		infoTable.row();
		
		animationListTable = new Table();
		infoTable.add(animationListTable).expand().fill();
		
		infoTable.row();
		
		Table spriteEditTable = new Table();
		infoTable.add(spriteEditTable).expandX().fill();
		
		/**
		 *  MENU BUTTONS
		 */
		
		TextButton newAtlasButton = new TextButton("New Atlas", textButtonStyle);
//		loadImageButton.setFillParent(true);
		menuTable.add(newAtlasButton).padRight(BUTTON_PADDING_PX);
		newAtlasButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.loadImage();
				model.newAtlas();
			}
		});
		newAtlasButton.pad(BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX);
		
		menuTable.add();

		TextButton loadImageButton = new TextButton("Load Image", textButtonStyle);
//		loadImageButton.setFillParent(true);
		menuTable.add(loadImageButton).padRight(BUTTON_PADDING_PX);
		loadImageButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.loadImage();
			}
		});
		loadImageButton.pad(BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX);
		
		menuTable.add();
		
		TextButton loadAtlasButton = new TextButton("Load Atlas", textButtonStyle);
//		loadImageButton.setFillParent(true);
		menuTable.add(loadAtlasButton).padRight(BUTTON_PADDING_PX);;
		loadAtlasButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.loadAtlas();
			}
		});
		loadAtlasButton.pad(BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX);
		
		menuTable.add();
		
		TextButton saveAtlasButton = new TextButton("Save Atlas", textButtonStyle);
//		loadImageButton.setFillParent(true);
		menuTable.add(saveAtlasButton).padRight(BUTTON_PADDING_PX);;
		saveAtlasButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.saveAtlas();
			}
		});
		saveAtlasButton.pad(BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX);
		
		menuTable.add().expand().fill();
		
		if (isDebug) {
			CheckBox debugCheckBox = new CheckBox("Debug", checkboxStyle);
			menuTable.add(debugCheckBox).padRight(BUTTON_PADDING_PX);;
			debugCheckBox.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					isDebug = !isDebug;
					stage.setDebugAll(isDebug);
				}
			});
			debugCheckBox.setChecked(isDebug);
		}
		
		TextButton aboutButton = new TextButton("About", textButtonStyle);
//		loadImageButton.setFillParent(true);
		menuTable.add(aboutButton).width(100).padRight(BUTTON_PADDING_PX);
		aboutButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.displayAboutScreen();
			}
		});
		aboutButton.pad(BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX);
		
		TextButton quitButton = new TextButton("Quit", textButtonStyle);
//		loadImageButton.setFillParent(true);
		menuTable.add(quitButton).width(100);
		quitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.quit();
			}
		});
		quitButton.pad(BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX);
		
		/**
		 * GRID MANAGEMENT TABLE
		 */
		
		final Label gridSizeLabel = new Label("Grid Size: " + model.getGridSize(), labelStyle);
		gridManagementTable.add(gridSizeLabel).padRight(BUTTON_PADDING_PX);

		TextButton increaseGridButton = new TextButton("+", textButtonStyle);
		gridManagementTable.add(increaseGridButton).padRight(BUTTON_PADDING_PX);
		increaseGridButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				int newGridSize = model.increaseGrid(1);
				gridSizeLabel.setText("Grid Size: " + newGridSize);
			}
		});
		increaseGridButton.pad(BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX);
		
		TextButton decreaseGridButton = new TextButton("-", textButtonStyle);
		gridManagementTable.add(decreaseGridButton).padRight(BUTTON_PADDING_PX);
		decreaseGridButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				int newGridSize = model.increaseGrid(-1);
				gridSizeLabel.setText("Grid Size: " + newGridSize);
			}
		});
		decreaseGridButton.pad(BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX, BUTTON__LABEL_BORDER_PX);
		
		gridManagementTable.add().expand().fill();
		
		/**
		 * DEBUG MESSAGES
		 */
		
		infoLabel = new Label("Awaiting input...", labelStyle);
//		infoLabel.setAlignment(Align.topLeft);
		infoLabel.setColor(1, 1, 1, 1.0f);
		debugTable.add(infoLabel);
		debugTable.add().expand().fill();
		
		/**
		 * SPRITESHEET
		 */
		
		spritesheetStack = new Stack();
		spritesheetTable.add(spritesheetStack);
		
		Texture spritesheetTexture = new Texture(Gdx.files.internal("badlogic.jpg"));
		Image sprite = new Image(spritesheetTexture);
		spritesheetStack.add(sprite);
		
		spritesheetStack.add(grid);
		
		/**
		 * SPRITE INFO TABLE
		 */
		
		spriteNameLabel = new Label("Name: ", labelStyle);
		spriteInfoTable.add(spriteNameLabel).expandX().fill().colspan(3);
		spriteNameLabel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.editSpriteName();
			}
		});
		
		spriteInfoTable.row();
		
		spritePositionLabel = new Label("Position: ", labelStyle);
		spriteInfoTable.add(spritePositionLabel).expandX().fill().colspan(3);
		
		spriteInfoTable.row();
		
		spriteWidthLabel = new Label("Width: ", labelStyle);
		spriteInfoTable.add(spriteWidthLabel).expandX().fill();
		
		TextButton spriteWidthIncreaseButton = new TextButton("+", textButtonStyle);
		spriteWidthIncreaseButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.increaseSpriteWidth();
			}
		});
		spriteInfoTable.add(spriteWidthIncreaseButton);
		TextButton spriteWidthDecreaseButton = new TextButton("-", textButtonStyle);
		spriteWidthDecreaseButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.decreaseSpriteWidth();
			}
		});
		spriteInfoTable.add(spriteWidthDecreaseButton);
		
		spriteInfoTable.row();
		
		spriteHeightLabel = new Label("Height: ", labelStyle);
		spriteInfoTable.add(spriteHeightLabel).expandX().fill();
		
		TextButton spriteHeightIncreaseButton = new TextButton("+", textButtonStyle);
		spriteHeightIncreaseButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.increaseSpriteHeight();
			}
		});
		spriteInfoTable.add(spriteHeightIncreaseButton);
		TextButton spriteHeightDecreaseButton = new TextButton("-", textButtonStyle);
		spriteHeightDecreaseButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.decreaseSpriteHeight();
			}
		});
		spriteInfoTable.add(spriteHeightDecreaseButton);
		
		spriteInfoTable.row();
		
		spriteIndexLabel = new Label("Index: ", labelStyle);
		spriteInfoTable.add(spriteIndexLabel).expandX().fill();
		
		TextButton spriteIndexIncreaseButton = new TextButton("+", textButtonStyle);
		spriteIndexIncreaseButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.increaseSpriteIndex();
			}
		});
		spriteInfoTable.add(spriteIndexIncreaseButton);
		TextButton spriteIndexDecreaseButton = new TextButton("-", textButtonStyle);
		spriteIndexDecreaseButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.decreaseSpriteIndex();
			}
		});
		spriteInfoTable.add(spriteIndexDecreaseButton);
		
		animationList = new AnimationList(model, listStyle);
		Table animationListInnerTable = new Table();
		ScrollPane scroll = new ScrollPane(animationListInnerTable, scrollStyle);
		animationListInnerTable.add(animationList.getList()).expand().fill();
		animationListTable.add(scroll).expand().fill();
		scroll.setFlickScroll(false);
		scroll.setFadeScrollBars(false);
		scroll.setScrollbarsOnTop(true);
		
		/*
		 * SPRITE EDIT BUTTONS
		 */
		
		TextButton addSpriteButton = new TextButton("Add", textButtonStyle);
		addSpriteButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.addSprite();
			}
		});
		spriteEditTable.add(addSpriteButton).expandX().fill();
		
		TextButton addAnimationButton = new TextButton("+ Anim", textButtonStyle);
		addAnimationButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.addAnimation();
			}
		});
		spriteEditTable.add(addAnimationButton).expandX().fill();
		
		TextButton removeSpriteButton = new TextButton("Remove", textButtonStyle);
		removeSpriteButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.removeSprite();
			}
		});
		spriteEditTable.add(removeSpriteButton).expandX().fill();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// update label
		infoLabel.setText(model.getMessage());
		
		// scroll to previous index, if any
		if (previousIndex > -1) {
			spriteScrollPane.layout();
			spriteScrollPane.setScrollY(previousIndex * animationList.getList().getItemHeight());
//			spriteScrollPane.scroll
//			spriteScrollPane.scrollTo(0, animationList.getList().getHeight() - previousIndex * animationList.getList().getItemHeight(), 0, 0);
			previousIndex = -1;
		}
		
		stage.act();
		stage.draw();
		
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		stage.dispose();
		atlas.dispose();
		skin.dispose();
		whiteFont.dispose();
	}

	@Override
	public void update(Observable o, Object arg) {
		Object[] args = (Object[]) arg;
		
		Data.Message msg = (Message) args[0];
		
		switch (msg) {
		case SPRITE_LIST_CHANGED:
			if (animationList != null) previousIndex = animationList.getSelectedIndex();
			
			animationList = new AnimationList(model, listStyle);
			
			animationList.add(model.getData());
			
			Table animationListInnerTable = new Table();
			spriteScrollPane = new ScrollPane(animationListInnerTable, scrollStyle);
			animationListInnerTable.add(animationList.getList()).expand().fill();
			
			animationListTable.clear();
			animationListTable.add(spriteScrollPane).expand().fill();
			
			spriteScrollPane.setFlickScroll(false);
			spriteScrollPane.setFadeScrollBars(false);
			spriteScrollPane.setScrollbarsOnTop(true);
			
			if (args.length > 1) {
				int newIndex = (Integer) args[1];
				newIndex = Math.min(newIndex, animationList.getList().getItems().size - 1);
				animationList.getList().setSelectedIndex(newIndex);
			} else if (previousIndex != -1) {
				previousIndex += 1;
				previousIndex = Math.min(previousIndex, animationList.getList().getItems().size - 1);
				animationList.getList().setSelectedIndex(previousIndex);
			}
			break;
		case SELECT_DATUM:
		case SPRITE_CHANGED:
			Sprite sprite = (Sprite) args[1];
			spriteNameLabel.setText(String.format("Name: %s", sprite.key.name));
			spritePositionLabel.setText(String.format("Position: %d, %d", sprite.x, sprite.y));
			spriteWidthLabel.setText(String.format("Width: %d", sprite.width));
			spriteHeightLabel.setText(String.format("Height: %d", sprite.height));
			spriteIndexLabel.setText(String.format("Index: %d", sprite.key.index));
			break;
		case SPRITESHEET_CHANGED:
			final Texture texture = model.getSpritesheetTexture();
			
			if (texture != null) {
				spritesheetStack.clear();
				
				Image image = new Image(texture);
				spritesheetStack.add(image);
				
				spritesheetStack.setWidth(texture.getWidth());
				spritesheetStack.setHeight(texture.getHeight());
//				spritesheetStack.pack();
//				spritesheetStack.layout();
				
				spritesheetStack.add(grid);
				
				spritesheetStack.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						model.clickedOn(x, texture.getHeight() - y);
					}
				});
			}
			break;
		default:
			break;
		
		}
	}

}
