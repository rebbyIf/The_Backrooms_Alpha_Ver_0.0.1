package world;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import engine.GameObject;
import engine.Scene;
import engine.Transform;
import org.joml.Vector2f;
import utility.Utility;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tile {
    public static final float TILE_SIZE = 32.0f;

    private GameObject gameObject;
    private Rectangle2D square;

    private int id;
    private static long numberOfTilesMade = 0L;
    private long serial;
    private boolean running = false;

    public Tile(float x, float y, int id){
        square = new Rectangle2D.Float(x,y,TILE_SIZE,TILE_SIZE);
        this.id = id;
        this.serial = numberOfTilesMade++;
    }

    public GameObject start(Spritesheet sprites){

        int zIndex = id;
        if (zIndex != 0){
            zIndex--;
        }

        if (!running) {
            gameObject = new GameObject("Tile: " + serial, new Transform(new Vector2f((float) square.getX(), (float) square.getY()),
                    new Vector2f(TILE_SIZE, TILE_SIZE)), zIndex);
            gameObject.addComponent(new SpriteRenderer(sprites.getSprite(zIndex+1)));

        }
        running = true;

        return gameObject;
    }

    public void stop(ArrayList<GameObject> gameObjects){
        if (gameObjects != null && gameObject != null) {
            gameObject.getComponent(SpriteRenderer.class).setSprite(Scene.sprites.getSprite(0));
            gameObjects.remove(gameObject);

            running = false;
        }
    }

    public float getX(){
        return (float)square.getX();
    }

    public float getY(){
        return (float)square.getY();
    }

    public float [] getXY(){
        float [] xy = {(float)square.getX(), (float)square.getY()};
        return xy;
    }

    public long getID(){
        return id;
    }

    public boolean running(){
        return running;
    }
}
