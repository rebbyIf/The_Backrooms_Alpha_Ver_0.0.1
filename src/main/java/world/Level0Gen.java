package world;

import java.util.ArrayList;
import java.util.HashMap;

public class Level0Gen extends WorldGen{
    public Level0Gen(float initialX, float initialY){
        room = new Tile[7][7];
        this.initialX = initialX;
        this.initialY = initialY;
        this.roomSize = 7;


    }


}
