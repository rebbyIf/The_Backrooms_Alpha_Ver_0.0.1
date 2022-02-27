package engine;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFW;

public class ImGuiLayer {
    private static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private boolean showColorPicker;
    private float [] color;

    public void imGui(){
        //ImGui
        imGuiGlfw.newFrame();
        ImGui.newFrame();
        /** Anything between these two calls creates ImGui stuff */

        //Begins frame
        ImGui.begin("Level Editor");

        if (ImGui.button("Button")){
            showColorPicker = true;
        }

        if (showColorPicker){
            ImGui.sameLine();
            if (ImGui.button("No Button")){
                showColorPicker = false;
            }
        }

        ImGui.end();

        /** Anything between these two calls creates ImGui stuff */
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    public void init(long windowPointer, String glslVersion){
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        imGuiGlfw.init(windowPointer, true);
        imGuiGl3.init(glslVersion);
    }

    public void render(){
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    public void update(float dt, Scene currentScene){

    }

    public void destroy(){
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
    }
}
