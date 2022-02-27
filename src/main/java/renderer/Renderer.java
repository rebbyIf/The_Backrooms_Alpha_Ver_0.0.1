package renderer;

import components.SpriteRenderer;
import engine.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;
    private int rendererID;

    public Renderer(int rendererID){
        this.batches = new ArrayList<>();
        this.rendererID = rendererID;
    }

    public void add(GameObject gameObject){
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null){
            add(spriteRenderer);
        }
    }


    private void add(SpriteRenderer sprite){
        boolean added = false;
        for (int i = 0; i < batches.size(); i++){
            RenderBatch batch = batches.get(i);
            if (batch.hasRoom() && batch.zIndex() == sprite.gameObject.zIndex()){
                Texture texture = sprite.getTexture();
                if (texture == null || (batch.hasTexture(texture) || batch.hasTextureRoom())){
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }

            }
        }

        if (!added){
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.zIndex(),this.rendererID);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }



    public void render(){
        for (RenderBatch batch : batches){
            batch.render();
        }
    }

}
