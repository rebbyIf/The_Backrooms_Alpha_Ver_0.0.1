package engine;

import components.Spritesheet;
import org.joml.Vector2f;
import renderer.Renderer;
import renderer.Shader;
import utility.AssetPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Scene {
    public static HashMap<String, Scene> scenes = new HashMap<>();

    public static Shader shader = AssetPool.getShader("assets/shaders/default.glsl");
    public static Spritesheet sprites;

    protected ArrayList<GameObject> gameObjects;


    protected Renderer renderer;
    protected Camera camera;
    protected static Camera worldCamera = new Camera(new Vector2f());
    protected Vector2f cameraPosition = new Vector2f(0.0f,0.0f);
    private boolean isRunning = false;
    protected int sceneIndex;
    protected float mouseX = 0, mouseY = 0;

    public Scene(){
        System.out.println("Inside Level Scene");
    }

    public static void loadAllSpritesheets(){
        loadSprites("assets/spritesheets/level_0000_Sprites.png",32,32,11,0);
        sprites = AssetPool.getSpritesheet("assets/spritesheets/level_0000_Sprites.png");
    }

    public void init(){

    }

    public void start(){
        for (int i = 0;i < gameObjects.size(); i++){
            System.out.println("Starting");
            gameObjects.get(i).start();
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject gameObject){
        if (gameObject != null) {
            if (!isRunning) {
                gameObjects.add(gameObject);
                renderer.add(gameObject);
            } else {
                gameObjects.add(gameObject);
                gameObject.start();
                renderer.add(gameObject);
            }
        }
    }


    public void update(float dt, int width, int height){

    }

    public static void loadSprites(String filepath, int spriteWidth, int spriteHeight, int numOfSprites, int spacing){
        AssetPool.addSpriteSheet(filepath,
                new Spritesheet(AssetPool.getTexture(filepath),
                        spriteWidth ,spriteHeight , numOfSprites,spacing));
    }


    protected void removeSprites( String filepath, int spriteWidth, int spriteHeight, int numOfSprites, int spacing){
        AssetPool.removeSpriteSheet(filepath,
                new Spritesheet(AssetPool.getTexture(filepath),
                        spriteWidth,spriteHeight, numOfSprites,spacing));
    }


    public void updateMousePosition(int width, int height){
        if (width < height){
            mouseX = MouseListener.getOrthoX(sceneIndex) + cameraPosition.x;
            mouseY = - (float) ((double) MouseListener.getOrthoY(sceneIndex) * height / width) + cameraPosition.y;
        }
        else {
            mouseX = (float)((double) MouseListener.getOrthoX(sceneIndex) * width / height) + cameraPosition.x;
            mouseY = -MouseListener.getOrthoY(sceneIndex) + cameraPosition.y;
        }
        //System.out.println(mouseX+" "+mouseY);
        //System.out.println((Math.atan2(mouseY,mouseX) +  Math.PI) % ( Math.PI));
    }

    public Camera camera(){
        return this.camera;
    }

}
