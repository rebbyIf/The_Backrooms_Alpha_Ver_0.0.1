package ai;

import world.Collider;
import engine.Component;
import engine.WorldScene;
import org.joml.Vector2f;
import world.Tile;

public abstract class AI extends Component {

    protected int startingSpriteIndex;
    protected int runCycle;
    protected int indexOfRunCycles;
    protected boolean isStatic = true;
    protected float [] time;
    protected int spriteIndex;

    protected float x = 0.0f,y = 0.0f, dx = 0.0f, dy = 0.0f;
    protected float [] velocity;
    protected int velocityIndex;
    protected double angle;

    @Override
    public void start(){

    }

    @Override
    public void update(float dt){

    }

    protected void doWalkingAnimation(float dt){
        time[1] += dt;

        if (!isStatic){
            if (angle >= Math.PI / 4.0 && angle <= 3.0*Math.PI / 4.0){
                spriteIndex = startingSpriteIndex + indexOfRunCycles*runCycle*4 + (int)(time[1]*velocity[velocityIndex]/
                        (gameObject.transform.scale.x*runCycle*0.5) % runCycle) + runCycle*2;
            }
            else if (angle > 3.0*Math.PI / 4.0 && angle < 5.0*Math.PI / 4.0){
                spriteIndex = startingSpriteIndex + indexOfRunCycles*runCycle*4 + (int)(time[1]*velocity[velocityIndex]/
                        (gameObject.transform.scale.x*runCycle*0.5) % runCycle) + runCycle*3;
            }
            else if (angle >= 5.0*Math.PI / 4.0 && angle <= 7.0*Math.PI / 4.0){
                spriteIndex = startingSpriteIndex + indexOfRunCycles*runCycle*4 + (int)(time[1]*velocity[velocityIndex]/
                        (gameObject.transform.scale.x*runCycle*0.5) % runCycle);
            }
            else{
                spriteIndex = startingSpriteIndex + indexOfRunCycles*runCycle*4 + (int)(time[1]*velocity[velocityIndex]/
                        (gameObject.transform.scale.x*runCycle*0.5) % runCycle) + runCycle;
            }
        }
    }

    /** Getters */
    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
    public float getDx(){
        return dx;
    }
    public float getDy(){
        return dy;
    }
    public boolean getIsStatic(){
        return isStatic;
    }

    /** Mutators */
    public void setX(float x){
        this.x = x;
    }
    public void setY(float y){
        this.y = y;
    }
    public void setDx(float dx){
        this.dx = dx;
    }
    public void setDy(float dy){
        this.dy = dy;
    }

}
