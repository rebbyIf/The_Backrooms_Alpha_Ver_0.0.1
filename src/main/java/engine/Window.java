package engine;

import imgui.ImGui;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import utility.Time;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {


    private String glslVersion = null;
    private static int width, height;
    private String title;
    private static float rgba [] = {0.0f,0.0f,0.0f,1.0f};
    private static long windowPointer;
    private static ImGuiLayer imGuiLayer;


    private static Window gameWindow = null;

    public static HashMap<Integer,Scene> scenes = new HashMap<>();
    public static List<Integer> sceneList;

    private Window (ImGuiLayer imGuiLayer){
        this.imGuiLayer = imGuiLayer;
        width = 1920;
        height = 1080;
        this.title = "The Backrooms";

    }

    public static void changeScene(int newScene){
        switch (newScene){
            case 0:
                scenes.put(0,new WorldScene(0));
                break;
            case 1:
                scenes.put(1,new EntityScene(1));
            default:
                assert false : "Unknown Scene #"+newScene;
                break;

        }
        Scene.loadAllSpritesheets();
        sceneList = scenes.keySet().stream().sorted().toList();
        for (int i = 0; i < sceneList.size(); i++){
            Integer key = sceneList.get(i);
            scenes.get(key).init();
        }
        for (int i = 0; i < sceneList.size(); i++){
            Integer key = sceneList.get(i);
            scenes.get(key).start();
        }
    }

    public static Window get(){
        if (Window.gameWindow == null){
            Window.gameWindow = new Window(new ImGuiLayer());
        }

        return Window.gameWindow;
    }

    public static Scene getScene(int index){
        return get().scenes.get(index);
    }

    public void run(){
        System.out.println("Hello");

        init();
        loop();

        //Frees the memory
        glfwFreeCallbacks(windowPointer);
        glfwDestroyWindow(windowPointer);

        //Terminates GLFW and the free callback error
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init(){
        // New Code
        initWindow();

        imGuiLayer.init(windowPointer, glslVersion);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        Window.changeScene(0);
        Window.changeScene(1);
    }


    private void initWindow(){
        //Error Callback
        GLFWErrorCallback.createPrint(System.err).set();

        //Initializes GLFW
        if (!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glslVersion = "#version 150";
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);

        //Configures GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //Create the Window
        windowPointer = glfwCreateWindow(width, height, this.title, NULL, NULL);
        if (windowPointer == NULL){
            throw new IllegalStateException("Failed to create the GLFW Window.");
        }

        /**
         * Listeners that listen for mouse and key inputs
         */
        glfwSetCursorPosCallback(windowPointer, MouseListener::mousePositionCallback);
        glfwSetMouseButtonCallback(windowPointer, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(windowPointer, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(windowPointer, KeyListener::keyCallback);

        //Make the OpenGL context current
        glfwMakeContextCurrent(windowPointer);
        //Enable v-sync
        glfwSwapInterval(1);

        //Makes the window visible
        glfwShowWindow(windowPointer);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
    }




    public static void loop(){
        float beginningTime = Time.getTime();
        float endTime;
        float dt = -1.0f;


        while (!glfwWindowShouldClose(windowPointer)){

            update();
            //System.out.println(width+" "+height);

            //Poll Events
            glfwPollEvents();

            //Color for Screen
            glClearColor(rgba[0],rgba[1],rgba[2],rgba[3]);
            glClear(GL_COLOR_BUFFER_BIT);


            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
                System.out.println("Space");
            }

            //imGuiLayer.imGui();

            if (dt >= 0.0f){
                sceneList = scenes.keySet().stream().sorted().toList();
                for (int i = 0; i < sceneList.size(); i++){
                    Integer key = sceneList.get(i);
                    scenes.get(key).update(dt, width, height);
                }

            }

            if (width < height) {
                glViewport(0, (height/2 -width/2), width, width);
            }
            else{
                glViewport((width/2 - 3*height/4), 0, (int)(height*1.5), height);
            }






            //imGuiLayer.render();

            GLFW.glfwSwapBuffers(windowPointer);
            GLFW.glfwPollEvents();


            //Keeps Time Elapsed of Frame
            System.out.println(1/dt);
            endTime = Time.getTime();
            dt = endTime - beginningTime;


            beginningTime = endTime;
        }
    }

    public static void update(){
        //Gets Size of Window
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(windowPointer, w, h);
        width = w.get(0);
        height = h.get(0);
    }

    public void destroy(){
        imGuiLayer.destroy();
        ImGui.destroyContext();
        Callbacks.glfwFreeCallbacks(windowPointer);
        glfwDestroyWindow(windowPointer);
        glfwTerminate();
    }

    /**
     * Accessors
     */
    public float rgba(int selectedItem){
        return rgba[selectedItem];
    }

    public static int width(){ return width;}

    public static int height(){ return height;}
    /**
     * Mutators
     */
    public void changeRGBA(int selectedItem, float value){
        rgba[selectedItem] = value;
    }
}
