package utility;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time {
    public static double timeStarted = glfwGetTime();

    public static float getTime(){
        return (float)(glfwGetTime() - timeStarted);
    }
}
