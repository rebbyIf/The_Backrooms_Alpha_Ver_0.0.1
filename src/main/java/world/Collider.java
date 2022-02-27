package world;

import ai.AI;
import engine.Component;
import engine.GameObject;
import org.joml.Vector2f;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Collider extends Component{
    public ArrayList<Line2D> lines = new ArrayList<>();
    private ArrayList<GameObject> entityObjects;

    /** Constructors */
    public Collider(ArrayList<Line2D> lines, ArrayList<GameObject> entityObjects){
        this.lines = lines;
        this.entityObjects = entityObjects;
    }

    /** Methods */

    @Override
    public void update(float dt){
        for (GameObject entityObject : entityObjects){
            AI ai = entityObject.getComponent(AI.class);
            if (ai != null && lines != null){
                testCollision(ai);
            }
        }
    }

    public void testCollision(AI ai){
        float x1 = ai.getX();
        float y1 = ai.getY();
        float x2 = x1 + ai.getDx();
        float y2 = y1 + ai.getDy();
        if (ai.getDx() != 0 || ai.getDy() != 0) {
            //System.out.printf("Change: (%f, %f) (%f, %f)\n", x1, y1, x2, y2);
            Rectangle2D temporary = new Rectangle2D.Float(gameObject.transform.position.x, gameObject.transform.position.y,
                    gameObject.transform.scale.x, gameObject.transform.scale.y);
            if (temporary.intersectsLine(x1,y1,x2,y2)) {
                for (int i = 0; i < lines.size(); i++) {
                    Line2D line = lines.get(i);
                    if (line != null) {
                        if (line.intersectsLine(x1,y1,x2,y2)) {
                            double a1 = line.ptLineDist(x2, y2);

                            double angle = Math.atan2(y2 - y1, x2 - x1);
                            if (angle != Math.PI) {
                                angle = (angle + Math.PI * 2) % (Math.PI * 2);
                            } else {
                                angle = Math.PI;
                            }

                            double lineAngle = Math.atan2(line.getY2() - line.getY1(), line.getX2() - line.getX1());
                            if (lineAngle != Math.PI) {
                                lineAngle = (lineAngle + Math.PI) % (Math.PI);
                            } else {
                                lineAngle = Math.PI;
                            }

                            double reflectedAngle = lineAngle * 2 - angle;
                            if (reflectedAngle != Math.PI) {
                                reflectedAngle = (reflectedAngle + Math.PI * 2) % (Math.PI * 2);
                            } else {
                                reflectedAngle = Math.PI;
                            }

                            double inverseAngle = Math.PI + angle;
                            if (inverseAngle != Math.PI) {
                                inverseAngle = (inverseAngle + Math.PI * 2) % (Math.PI * 2);
                            } else {
                                inverseAngle = Math.PI;
                            }

                            double finalAngle;

                            if (inverseAngle != reflectedAngle) {
                                if ((inverseAngle >= Math.PI / 2 && inverseAngle <= 3 * Math.PI / 2) || (reflectedAngle >= Math.PI / 2 && reflectedAngle <= 3 * Math.PI / 2)) {
                                    finalAngle = (inverseAngle + reflectedAngle) / 2;
                                } else {
                                    finalAngle = (inverseAngle + reflectedAngle + Math.PI * 2) / 2;
                                }
                            } else {
                                finalAngle = inverseAngle;
                            }

                            ai.setDx(ai.getDx() + (float) ((a1 + 1.0) * Math.cos(finalAngle)));
                            ai.setDy(ai.getDy() + (float) ((a1 + 1.0) * Math.sin(finalAngle)));
                        }
                    }
                }
            }
        }
    }

}
