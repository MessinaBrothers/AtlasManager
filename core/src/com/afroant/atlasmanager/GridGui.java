package com.afroant.atlasmanager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class GridGui extends Actor {
	
	private ShapeRenderer shapeRenderer;
	
	private Camera camera;
	
	private Model model;
	
	private float lineWidth = 2;
	
	public GridGui(Model model, Camera camera) {
		this.model = model;
		this.camera = camera;
		shapeRenderer = new ShapeRenderer();
	}
	
	@Override
	public void draw (Batch batch, float parentAlpha) {
		batch.end();
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(new Color(1, 0, 1, 0.5f));
		Gdx.gl.glEnable(GL20.GL_BLEND);
		
		float gridLineCount = model.getGridSize();
		
		float originX = getX();
		float originY = getY();
		
		float width = model.getSpritesheetWidth();
		float height = model.getSpritesheetHeight();
		
		float incrementX = gridLineCount;
		float incrementY = gridLineCount;
		
		for (float x = 0; x < width; x += incrementX) {
			shapeRenderer.rectLine(originX + x, originY, originX + x, originY + height, lineWidth);
		}
		for (float y = height; y > 0; y -= incrementY) {
			shapeRenderer.rectLine(originX, originY + y, originX + width, originY + y, lineWidth);
		}
		
		Sprite datum = model.getCurrentSprite();
		
		if (datum != null) {
			float x = originX + datum.x;
			float y = originY + model.getSpritesheetHeight() - datum.y;
			
			width = datum.width;
			height = datum.height;
			
			shapeRenderer.setColor(new Color(1, 1, 1, 1.0f));
			shapeRenderer.rectLine(x, y, x + width, y, lineWidth);
			shapeRenderer.rectLine(x + width, y, x + width, y - height, lineWidth);
			shapeRenderer.rectLine(x + width, y - height, x, y - height, lineWidth);
			shapeRenderer.rectLine(x, y, x, y - height, lineWidth);
		}
		
		shapeRenderer.end();
		
		batch.begin();
	}
}
