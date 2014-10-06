package com.fska.hexboggle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Shape;
import com.fska.hexboggle.grid.HexTile;

public class HexBoggle extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	PolygonSprite poly;
	PolygonSpriteBatch polyBatch;
	SpriteBatch spriteBatch;
	BitmapFont font;
	Texture textureSolid;
	Set<HexTile> myTiles;
	
	OrthographicCamera camera;
	ShapeRenderer debugRenderer;
	
	ShapeRenderer lineRenderer;
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		
		float a = 150;
		float b = 150;
		
		//Creating the color filling (but textures would work the same way)
		Pixmap pix = new Pixmap(1,1,Pixmap.Format.RGBA8888);
		pix.setColor(Color.CYAN);
		pix.fill();
		textureSolid = new Texture(pix);
		
		PolygonRegion polyReg = new PolygonRegion(new TextureRegion(textureSolid),
			new float[] {      // Four vertices
			    a, b,            // Vertex 0         3--2
			    a+100, b,          // Vertex 1         | /|
			    a+100, b+100,        // Vertex 2         |/ |
			    a, b+100           // Vertex 3         0--1
			}, new short[] {
			    0, 1, 2,         // Two triangles using vertex indices.
			    0, 2, 3          // Take care of the counter-clockwise direction. 
			});
		
		poly = new PolygonSprite(polyReg);
		poly.setOrigin(a+50, b+50);
		polyBatch = new PolygonSpriteBatch();
		
		float radius = 50;
		
		float widthMod = 0.93f;
		float heightMod = 1.55f;
		
		myTiles = new HashSet<HexTile>();
		for(int x = -10; x < 40; x+= 2 ){
			for(int y = -10; y < 20; y++){
				if((y % 2) == 0)
					myTiles.add(new HexTile(new Vector2(x * radius* widthMod,y * heightMod * radius),radius));
				else
					myTiles.add(new HexTile(new Vector2((x + 1) * radius * widthMod,y * heightMod * radius),radius));
			}
		}
		
		//tiles[1].drawPolySprite().rotate(90);
		
		camera = new OrthographicCamera(640,480);
		camera.position.x = 320;
		camera.position.y = 240;
		
		debugRenderer = new ShapeRenderer();
		lineRenderer = new ShapeRenderer();
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(Color.MAGENTA);
		font.setScale(2);
		
		currentPath = new ArrayList<HexTile>();
	}

	@Override
	public void dispose() {
		super.dispose();
		textureSolid.dispose();
		polyBatch.dispose();
		for(HexTile tile : myTiles)
			tile.dispose();
	}

	List<HexTile> currentPath;
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(0,0,0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		polyBatch.setProjectionMatrix(camera.combined);
		
		//Get accurate mouse coordinates base on the camera's position
		Vector3 worldCoordinates = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
		
		camera.unproject(worldCoordinates);
    	Vector2 mouseRay = new Vector2(worldCoordinates.x, worldCoordinates.y);
    	
//    	mouseRay.add(Gdx.graphics.getWidth() - Gdx.input.getX(), Gdx.input.getY());
		
		
		polyBatch.begin();
	    
	    for(HexTile tile : myTiles){
	    	if(mouseRay.dst(tile.getCenter()) < (tile.getRadius())){
	    		tile.setSelected(true);
	    		if(Gdx.input.isButtonPressed(Buttons.LEFT) && !currentPath.contains(tile)){
	    			currentPath.add(tile);
	    			if(currentPath.size() > 5)
	    				camera.zoom += 0.1f;
	    		}
	    			
	    	} else
	    		tile.setSelected(false);
	    	tile.draw(polyBatch);
	    	font.draw(polyBatch, Character.toString(tile.getLetter()), tile.getCenter().x, tile.getCenter().y);
	    	
	    }
	    polyBatch.end();

//	    debugRenderer.setProjectionMatrix(camera.combined);
//	    debugRenderer.begin(ShapeType.Line);
//	    debugRenderer.setColor(Color.RED);
//	    for(HexTile tile : myTiles){
//	    	debugRenderer.circle(tile.getCenter().x, tile.getCenter().y, tile.getRadius());
//	    }
//	    debugRenderer.end();
	    
	    lineRenderer.setProjectionMatrix(camera.combined);
	    lineRenderer.begin(ShapeType.Line);
	    lineRenderer.setColor(Color.TEAL);
	    for(int i = 1; i < currentPath.size(); i++){
	    	
	    	lineRenderer.line(currentPath.get(i-1).getCenter(), currentPath.get(i).getCenter());
	    }
	    lineRenderer.end();
	    
	    spriteBatch.setProjectionMatrix(camera.combined);
	    spriteBatch.begin();
	    spriteBatch.end();
	    
	    if(Gdx.input.isButtonPressed(Buttons.LEFT)){
	    	//camera.translate(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
	    } else {
	    	currentPath = new ArrayList<HexTile>();
	    	camera.zoom = 1;
	    }
	    
	    
	}
}
