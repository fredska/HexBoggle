package com.fska.hexboggle.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public class HexTile {
	
	private Vector2 position;
	private float radius;
	private boolean isSelected;
	private boolean isStateChange;
	Texture textureSolid;
	
	PolygonSprite polySprite;
	
	public BitmapFont font;
	private char letter;
	
	public HexTile(Vector2 position, float radius){
		this.position = position;
		this.radius = radius;
		letter = (char)(MathUtils.random(26) + 'A');
	}
	
	public HexTile(Vector2 position, float radius, char letter){
		this.position = position;
		this.radius = radius;
		this.letter = letter;
	}
	
	
	
	public void drawPolySprite(){
		float a = this.position.x;
		float b = this.position.y;
		
		if(polySprite == null || isStateChange){
			isStateChange = false;
			//Creating the color filling (but textures would work the same way)
			Pixmap pix = new Pixmap(1,1,Pixmap.Format.RGBA8888);
			if(isSelected)
				pix.setColor(Color.YELLOW);
			else 
				pix.setColor(Color.DARK_GRAY);
			pix.fill();
			textureSolid = new Texture(pix);
			
			float cosMod = MathUtils.cos(MathUtils.PI / 6) * radius;
			float sinMod = MathUtils.sin(MathUtils.PI / 6) * radius;
			PolygonRegion polyReg = new PolygonRegion(new TextureRegion(textureSolid),
					new float[] {      // Four vertices
//					    a, b,            // Vertex 0           3--2
//					    a+100, b,          // Vertex 1         | /|
//					    a+100, b+100,        // Vertex 2       |/ |
//					    a, b+100           // Vertex 3         0--1
				
						a - cosMod, b - sinMod,
						a, b - radius,
						a + cosMod, b - sinMod,
						a + cosMod, b + sinMod,
						a, b + radius,
						a - cosMod, b + sinMod
					}, new short[] {
//					    0, 1, 2,         // Two triangles using vertex indices.
//					    0, 2, 3          // Take care of the counter-clockwise direction. 
						0, 1, 2,
						2, 3, 5,
						5, 3, 4,
						0, 2, 5
					});
			
			polySprite = new PolygonSprite(polyReg);
			polySprite.setOrigin(position.x, position.y);
		}
	}
	
	public void draw(PolygonSpriteBatch batch){
		if(polySprite == null || isStateChange)
			drawPolySprite();
		polySprite.draw(batch);
	}
	
	public void move(Vector2 vector){
		isStateChange = true;
		position.add(vector);
		System.out.println(position);
	}
	
	public void rotate(float degrees){
		polySprite.rotate(degrees);
	}
	
	public void dispose(){
		textureSolid.dispose();
	}
	
	public void setSelected(boolean selected){
		//Only set the state change if stuff needs to be changed
		isStateChange = selected ^ this.isSelected;
		this.isSelected = selected;
	}
	
	public Vector2 getCenter(){
		return this.position;
	}
	
	public float getRadius(){
		return this.radius;
	}
	
	public char getLetter(){
		return this.letter;
	}
}
