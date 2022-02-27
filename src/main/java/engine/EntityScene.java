package engine;

import ai.PlayerAI;
import components.SpriteRenderer;
import components.Spritesheet;
import org.joml.Vector2f;
import renderer.Renderer;
import renderer.Texture;
import utility.AssetPool;
import world.Tile;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glViewport;

public class EntityScene extends Scene{

    public static GameObject player;

    public EntityScene(int sceneIndex){
        gameObjects = new ArrayList<>();
        renderer = new Renderer(sceneIndex);
    }

    @Override
    public void init(){

        this.camera = worldCamera;

        player = new GameObject("Player", new Transform(new Vector2f(-Tile.TILE_SIZE*1.5f, -Tile.TILE_SIZE*1.5f),
                new Vector2f(Tile.TILE_SIZE*1.5f, Tile.TILE_SIZE*1.5f)), 0);
        player.addComponent(new SpriteRenderer(sprites.getSprite(2)));
        player.addComponent(new PlayerAI());
        this.addGameObjectToScene(player);

        //removeSprites("assets/sprites/entity_sprites/Forward Walking Wide.png",32,32,7,0);
    }




    @Override
    public void update(float dt,int width, int height) {

        for (int i = 0; i < gameObjects.size(); i++){
            gameObjects.get(i).update(dt);
        }

        updateMousePosition(width, height);
        cameraPosition.x = player.transform.position.x + player.transform.scale.x/2;
        cameraPosition.y = player.transform.position.y + player.transform.scale.y/2;

        camera.changePosition(cameraPosition.x, cameraPosition.y);

        this.renderer.render();
    }
}