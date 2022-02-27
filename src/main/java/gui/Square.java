package gui;

import org.joml.Vector2f;

public class Square{
    private float top, bottom, left, right;

    public boolean isIn(float x, float y){
        return (y <= top && y >= bottom) && (x <= right && x >= left);
    }
}
