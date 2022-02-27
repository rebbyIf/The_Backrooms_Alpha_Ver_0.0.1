package renderer;

import components.Shade;
import components.SpriteRenderer;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import utility.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {
    // Vertex
    // ================
    // Pos               Color                          Tex Coords      Texture ID  Layer
    // float, float      float, float, float, float     float, float    float       float
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;


    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_ID_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;


    private SpriteRenderer sprites[];
    private int numSprites;
    private boolean hasRoom;
    private float vertices [];
    private int texSlots [] = {0,1,2,3,4,5,6,7};
    private int rendererID;

    private List<Texture> textures;
    private int vaoID, vboID, maxBatchSize;
    private Shader shader;
    private int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex, int rendererID){
        this.rendererID = rendererID;
        this.zIndex = zIndex;
        shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.sprites = new SpriteRenderer [maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 Vertices
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    public void start(){
        //Generates and Binds a Vertex Array Object.
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate Space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int indices [] = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);



    }

    public void addSprite(SpriteRenderer sprite){
        // Get index and add renderObject
        int index = this.numSprites;
        this.sprites[index] = sprite;
        this.numSprites++;

        //Uses Sprites
        if (sprite.getTexture() != null){
            if (!textures.contains(sprite.getTexture()) && textures.size() < 8){
                textures.add(sprite.getTexture());
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (numSprites >= this.maxBatchSize){
            this.hasRoom = false;
        }
    }

    public void render(){
        boolean rebufferData = false;
        for (int i = 0; i < numSprites; i++){
            SpriteRenderer spr = sprites[i];
            if (spr.isDirty()){
                loadVertexProperties(i);
                spr.setClean();
                rebufferData = true;
            }
        }

        //Rebuffers data
        if (rebufferData){
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }


        // Uses Shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene(rendererID).camera().getProjectionMatrix());
        shader.uploadMat4f("uView",Window.getScene(rendererID).camera().getViewMatrix());
        for (int i = 0; i < textures.size(); i++){
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //System.out.println("Drawing Elements...");
        glDrawElements(GL_TRIANGLES, this.numSprites*6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }

        shader.detach();
    }

    private void loadVertexProperties(int index){
        SpriteRenderer sprite = this.sprites[index];

        //Finds offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f [] textCoords = sprite.getTextCoords();

        int textID = 0;
        if (sprite.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if (textures.get(i) == sprite.getTexture()){
                    textID = i + 1;
                    break;
                }
            }
        }

        // Adds vertices with the appropriate properties
        float xAdd = 1.0f, yAdd = 1.0f;
        for (int i = 0; i < 4; i++){
            switch (i){
                case 1:
                    yAdd = 0.0f;
                    break;
                case 2:
                    xAdd = 0.0f;
                    break;
                case 3:
                    yAdd = 1.0f;
                    break;
            }

            //Loads Position
            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd*sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd*sprite.gameObject.transform.scale.y);

            //Loads color
            Shade shade = sprite.gameObject.getComponent(Shade.class);
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            //Loads texture coordinates
            vertices[offset + 6] = textCoords[i].x;
            vertices[offset + 7] = textCoords[i].y;

            //Loads texture ID
            vertices[offset + 8] = textID;

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateIndices(){
        // 6 indices per quad (3 per triangle)
        int [] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++){
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int [] elements, int index){
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 0, 1, 2, 0, 2, 3
        // Triangle 1
        elements[offsetArrayIndex] = offset + 0;
        elements[offsetArrayIndex+1] = offset + 1;
        elements[offsetArrayIndex+2] = offset + 2;

        // Triangle 2
        elements[offsetArrayIndex+3] = offset + 0;
        elements[offsetArrayIndex+4] = offset + 2;
        elements[offsetArrayIndex+5] = offset + 3;
    }

    public boolean hasRoom(){
        return this.hasRoom;
    }

    public boolean hasTextureRoom(){
        return this.textures.size() < 8;
    }

    public boolean hasTexture(Texture texture){
        return this.textures.contains(texture);
    }

    public int zIndex(){
        return this.zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex());
    }
}