package components;

import engine.Component;

public class Shade extends Component {

    private float [][] shade = new float[4][4];

    public Shade(float [] topRight, float [] bottomRight, float [] bottomLeft, float [] topLeft){
        shade[0] = topRight;
        shade[1] = bottomRight;
        shade[2] = bottomLeft;
        shade[3] = topLeft;
    }

    public float [][] getShade (){
        return shade;
    }

}
