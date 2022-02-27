package engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import world.Tile;


public class Camera {
    private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;
    private Vector2f position;
    public static float SCREEN_COORDINATES = 6.0f * Tile.TILE_SIZE;

    public Camera(Vector2f position){
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection(){
        projectionMatrix.identity();
        projectionMatrix.ortho(-SCREEN_COORDINATES*1.5f, SCREEN_COORDINATES*1.5f, -SCREEN_COORDINATES, SCREEN_COORDINATES,0.0f,100.0f);
        projectionMatrix.invert(inverseProjection);
    }

    public Matrix4f getViewMatrix(){

        Vector3f cameraFront = new Vector3f(0.0f,0.0f,-1.0f);
        Vector3f cameraUp = new Vector3f(0.0f,1.0f,0.0f);
        this.viewMatrix.identity();
        this.viewMatrix = viewMatrix.lookAt(new Vector3f(position.x,position.y,20.0f),
                                            cameraFront.add(position.x,position.y, 0.0f),
                                            cameraUp);

        this.viewMatrix.invert(inverseView);

        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix(){
        return this.projectionMatrix;
    }

    public Matrix4f getInverseProjection(){return this.inverseProjection;}

    public Matrix4f getInverseView(){return this.inverseView;}

    public void changePosition(float x, float y){
        this.position.x = x;
        this.position.y = y;
    }
}
