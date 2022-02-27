package utility;

import org.joml.Vector2f;

import java.util.Random;

public class Utility {
    public static Random RNG = new Random();

    public static int getRandomInt(int origin, int bound){
        return RNG.nextInt(origin, bound);
    }

    public static int getRandomInt(){
        return RNG.nextInt();
    }

    public static float getRandomFloat(float origin, float bound){
        return RNG.nextFloat(origin, bound);
    }
}
