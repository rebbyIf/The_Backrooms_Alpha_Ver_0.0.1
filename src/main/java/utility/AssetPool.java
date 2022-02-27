package utility;

import components.Spritesheet;
import renderer.Shader;
import renderer.Texture;

import java.io.File;
import java.util.HashMap;


public class AssetPool {
    private static HashMap<String, Shader> shaders = new HashMap<>();
    private static HashMap<String, Texture> textures = new HashMap<>();
    private static HashMap<String, Spritesheet>  spritesheets = new HashMap<>();


    public static Shader getShader(String resourceName){
        File file = new File(resourceName);
        if (AssetPool.shaders.containsKey(file.getAbsolutePath())){
            return AssetPool.shaders.get(file.getAbsolutePath());
        }
        else{
            Shader shader = new Shader(file.getAbsolutePath());
            shader.compile();
            AssetPool.shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName){
        File file = new File(resourceName);
        if (textures.containsKey(file.getAbsolutePath())){
            return AssetPool.textures.get(file.getAbsolutePath());
        }
        else{
            Texture texture = new Texture(file.getAbsolutePath());
            AssetPool.textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSpriteSheet(String resourceName, Spritesheet spritesheet){
        File file = new File(resourceName);
        if (!AssetPool.spritesheets.containsKey(file.getAbsolutePath())){
            AssetPool.spritesheets.put(file.getAbsolutePath(),spritesheet);
        }
    }


    public static Spritesheet getSpritesheet(String resourceName){
        File file = new File(resourceName);
        if (!AssetPool.spritesheets.containsKey(file.getAbsolutePath())){
            assert false : "Error: tried to access spritesheet '"+ resourceName +"' and it has not been added to asset pool";
        }
        System.out.println("Spritesheets: "+spritesheets.size());
        System.out.println("Textures: "+textures.size());
        System.out.println(file.getAbsolutePath());
        return AssetPool.spritesheets.get(file.getAbsolutePath());
    }

    public static void removeSpriteSheet(String resourceName, Spritesheet spritesheet){
        File file = new File(resourceName);
        if (AssetPool.spritesheets.containsKey(file.getAbsolutePath())){
            AssetPool.spritesheets.remove(file.getAbsolutePath(),spritesheet);
        }
    }
}
