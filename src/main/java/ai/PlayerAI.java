package ai;

import components.SpriteRenderer;
import engine.KeyListener;
import engine.Scene;
import world.Tile;

import static org.lwjgl.glfw.GLFW.*;


public class PlayerAI extends AI{

    public PlayerAI(){
        this.velocity = new float [2];
        velocity[0] = Tile.TILE_SIZE*12;
        velocity[1] = Tile.TILE_SIZE*24;

        //Sets the sprite indices
        startingSpriteIndex = 3;
        runCycle = 2;
        indexOfRunCycles = 0;

        //Initializes time array
        time = new float[2];
    }

    @Override
    public void start(){
        for (float t : time){
            t = 0.0f;
        }
        spriteIndex = startingSpriteIndex;
    }

    @Override
    public void update(float dt){

        //Moves the Player According to Screen Inputs
        gameObject.transform.position.x += dx;
        gameObject.transform.position.y += dy;
        x = gameObject.transform.position.x + gameObject.transform.scale.x/2;
        y = gameObject.transform.position.y + gameObject.transform.scale.y/4;

        setVelocityBindings(dt);

        System.out.println(gameObject.transform.position.x + " "+gameObject.transform.position.y);

        doWalkingAnimation(dt);

        //Sets Screen Animations
        gameObject.getComponent(SpriteRenderer.class).setSprite(Scene.sprites.getSprite(spriteIndex));
    }

    public void setVelocityBindings(float dt){
        double relativeX = 0, relativeY = 0;
        if (KeyListener.isKeyPressed(GLFW_KEY_D)){
            relativeX+=1;
        }
        else{
            relativeX-=1;
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_A)){
            relativeX-=1;
        }
        else{
            relativeX+=1;
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_W)){
            relativeY+=1;
        }
        else{
            relativeY-=1;
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_S)){
            relativeY-=1;
        }
        else{
            relativeY+=1;
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || KeyListener.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)){
            velocityIndex = 1;
        }
        else{
            velocityIndex = 0;
        }


        if (relativeX != 0.0 || relativeY != 0.0) {
            isStatic = false;
            angle = (Math.atan2(relativeY, relativeX) + 2*Math.PI) % (2*Math.PI);

            dx = (float)Math.cos(angle) * velocity[velocityIndex] * dt;
            dy = (float)Math.sin(angle) * velocity[velocityIndex] * dt;
        }
        else{
            isStatic = true;

            dx = 0.0f;
            dy = 0.0f;
        }
    }
}
