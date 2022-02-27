package engine;

import utility.Utility;
import world.Collider;
import components.SpriteRenderer;
import org.joml.Vector2f;
import renderer.Renderer;
import utility.AssetPool;
import world.Level0Gen;
import world.Tile;
import world.WorldGen;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.glViewport;

public class WorldScene extends Scene{

    private HashMap<String,Tile> tiles;
    private ArrayList<Tile> loadedTiles;

    private WorldGen worldGen;
    private ArrayList<int []> rooms;
    private int roomGenerationSize = 2;
    private int [] directions = new int[2];

    private Integer playersPrevious3RoomPositionX = null, playersPrevious3RoomPositionY = null;
    private Integer playersPreviousRoomPositionX = null, playersPreviousRoomPositionY = null;
    private int playersRoomPositionX, playersRoomPositionY, players3RoomPositionX, players3RoomPositionY;
    private int loadWidth = 5, loadHeight = 5;

    public WorldScene(int sceneIndex){
        gameObjects = new ArrayList<>();
        this.sceneIndex = sceneIndex;
        renderer = new Renderer(sceneIndex);
        tiles = new HashMap<>();
        loadedTiles = new ArrayList<>();
        rooms = new ArrayList<>();
    }

    @Override
    public void init(){

        AssetPool.getShader("assets/shaders/default.glsl");

        //loadSprites("assets/sprites/wall_sprites/0000-0.png",32,32,2,0);


        this.camera = worldCamera;

        worldGen = new Level0Gen(0.0f,0.0f);
        //worldGen.addRoom(0.0f,0.0f, 0, tiles);

        System.out.println(tiles.isEmpty());

        //removeSprites("assets/sprites/wall_sprites/0000-0.png",32,32,2,0);
    }

    @Override
    public void update(float dt, int width, int height) {

        updateMousePosition(width, height);

        //Updates the player's position in tile coordinates
        int additionsX = 0, additionsY = 0;
        if (EntityScene.player.transform.position.x < 0.0f){
            additionsX--;
        }
        if (EntityScene.player.transform.position.y < 0.0f){
            additionsY--;
        }

        playersRoomPositionX = (int)(EntityScene.player.transform.position.x / (Tile.TILE_SIZE * worldGen.roomSize())) + additionsX;
        playersRoomPositionY = (int)(EntityScene.player.transform.position.y / (Tile.TILE_SIZE * worldGen.roomSize())) + additionsY;

        players3RoomPositionX = playersRoomPositionX / ((roomGenerationSize*2 + 1)*3) + additionsX;
        players3RoomPositionY = playersRoomPositionY / ((roomGenerationSize*2 + 1)*3) + additionsY;

        /*
        if (playersPrevious3RoomPositionX != null || playersPrevious3RoomPositionY != null){
            if (playersPrevious3RoomPositionX != players3RoomPositionX || playersPrevious3RoomPositionY != players3RoomPositionY) {
                int [] changeDirection = {0,1};
                int randomDirection = changeDirection[Utility.getRandomInt(0, 2)];
                directions[0] = (directions[0] + randomDirection)%4;
                directions[1] = (directions[1] + randomDirection)%4;
            }
        }
        else{
            int randomDirection = Utility.getRandomInt(0,4);
            directions[0] = randomDirection + 1;
            directions[1] = (randomDirection+1)%4 + 1;
        }

         */

        if (playersPreviousRoomPositionX != null || playersPreviousRoomPositionY != null) {
            if (playersRoomPositionX != playersPreviousRoomPositionX || playersRoomPositionY != playersPreviousRoomPositionY) {

                if (playersRoomPositionX > playersPreviousRoomPositionY && playersRoomPositionY > playersPreviousRoomPositionY){
                    directions[0] = 3;
                    directions[1] = 4;
                }
                else if (playersRoomPositionX < playersPreviousRoomPositionX && playersRoomPositionY > playersPreviousRoomPositionY){
                    directions[0] = 4;
                    directions[1] = 1;
                }
                else if (playersRoomPositionX < playersPreviousRoomPositionX && playersRoomPositionY < playersPreviousRoomPositionY){
                    directions[0] = 1;
                    directions[1] = 2;
                }
                else if (playersRoomPositionX > playersPreviousRoomPositionX && playersRoomPositionY < playersPreviousRoomPositionY){
                    directions[0] = 2;
                    directions[1] = 3;
                }
                else if (playersRoomPositionX > playersPreviousRoomPositionX){
                    for (int i = 0; i < directions.length; i++){
                        if (directions[i] == 1){
                            directions[i] = 3;
                        }
                    }
                }
                else if (playersRoomPositionY > playersPreviousRoomPositionY){
                    for (int i = 0; i < directions.length; i++){
                        if (directions[i] == 2){
                            directions[i] = 4;
                        }
                    }
                }
                else if (playersRoomPositionX < playersPreviousRoomPositionX){
                    for (int i = 0; i < directions.length; i++){
                        if (directions[i] == 3){
                            directions[i] = 1;
                        }
                    }
                }
                else{
                    for (int i = 0; i < directions.length; i++){
                        if (directions[i] == 4){
                            directions[i] = 2;
                        }
                    }
                }

                worldGen.generateRooms(playersRoomPositionX - roomGenerationSize,playersRoomPositionY - roomGenerationSize,
                        playersRoomPositionX + roomGenerationSize + 1, playersRoomPositionY + roomGenerationSize + 1,
                        rooms, tiles, directions, gameObjects);

                //worldGen.deleteRooms(playersRoomPositionX, playersRoomPositionY, roomGenerationSize, rooms);

                loadTiles((playersRoomPositionX - roomGenerationSize) * worldGen.roomSize() - 1,
                        (playersRoomPositionX + roomGenerationSize + 1) * worldGen.roomSize() + 1, 1,
                        (playersRoomPositionY - roomGenerationSize) * worldGen.roomSize() - 1,
                        (playersRoomPositionY + roomGenerationSize + 1) * worldGen.roomSize() + 1, 1);

                unloadTiles();
            }
        }
        else {
            int randInt = Utility.getRandomInt(0,4);
            directions[0] = randInt+1;
            directions[1] = (randInt+1)%4 + 1;

            worldGen.generateRooms(playersRoomPositionX - roomGenerationSize,playersRoomPositionY - roomGenerationSize,
                    playersRoomPositionX + roomGenerationSize + 1, playersRoomPositionY + roomGenerationSize + 1,
                    rooms, tiles, directions, gameObjects);

            //worldGen.deleteRooms(playersRoomPositionX, playersRoomPositionY, roomGenerationSize, rooms);

            loadTiles((playersRoomPositionX - roomGenerationSize) * worldGen.roomSize() - 1,
                    (playersRoomPositionX + roomGenerationSize + 1) * worldGen.roomSize() + 1, 1,
                    (playersRoomPositionY - roomGenerationSize) * worldGen.roomSize() - 1,
                    (playersRoomPositionY + roomGenerationSize + 1) * worldGen.roomSize() + 1, 1);

            unloadTiles();
        }


        //Updates player's current position

        playersPreviousRoomPositionX = playersRoomPositionX;
        playersPreviousRoomPositionY = playersRoomPositionY;

        playersPrevious3RoomPositionX = players3RoomPositionX;
        playersPrevious3RoomPositionY = players3RoomPositionY;

        for (int i = 0; i < gameObjects.size(); i++){
            gameObjects.get(i).update(dt);
            //System.out.println(gameObjects.get(i).name());
        }

        this.renderer.render();


    }

    private void loadTiles(int startX, int endX, int dx, int startY, int endY, int dy){
        for (int x = startX; x <= endX; x+=dx){
            for (int y = startY; y <= endY; y+=dy){
                //Gets associated tiles
                Tile tile = tiles.get(x*Tile.TILE_SIZE+" "+y*Tile.TILE_SIZE);

                if (tile != null && !tile.running()){
                    GameObject tileObject = tile.start(sprites);
                    if (tile.getID() == 1) {
                        //Nearby Tiles
                        Tile tileRight = tiles.get((x+1)*Tile.TILE_SIZE+" "+y*Tile.TILE_SIZE);
                        Tile tileUp = tiles.get(x*Tile.TILE_SIZE+" "+(y+1)*Tile.TILE_SIZE);
                        Tile tileLeft = tiles.get((x-1)*Tile.TILE_SIZE+" "+y*Tile.TILE_SIZE);
                        Tile tileDown = tiles.get(x*Tile.TILE_SIZE+" "+(y-1)*Tile.TILE_SIZE);

                        ArrayList<Line2D> tileColliders = new ArrayList<>();
                        int numOfTiles = 4;
                        boolean addRightTile = false, addUpTile = false, addLeftTile = false, addDownTile = false;
                        /*
                        tileColliders.add(new Line2D.Float(tile.getX() + Tile.TILE_SIZE, tile.getY(),
                                tile.getX() + Tile.TILE_SIZE, tile.getY() + Tile.TILE_SIZE));
                        tileColliders.add(new Line2D.Float(tile.getX(), tile.getY(),
                                tile.getX(), tile.getY() + Tile.TILE_SIZE));
                        tileColliders.add(new Line2D.Float(tile.getX(), tile.getY() + Tile.TILE_SIZE,
                                tile.getX() + Tile.TILE_SIZE, tile.getY() + Tile.TILE_SIZE));
                        tileColliders.add(new Line2D.Float(tile.getX(), tile.getY(),
                                tile.getX() + Tile.TILE_SIZE, tile.getY()));

                         */

                        //Adds colliders if there are tiles next to the colliders
                        if (tileRight != null) {
                            if (tileRight.getID() != 1){
                                addRightTile = true;
                                numOfTiles--;
                            }
                        }

                        else{
                            addRightTile = true;
                            numOfTiles--;
                        }



                        if (tileLeft != null) {
                            if (tileLeft.getID() != 1){
                                addLeftTile = true;
                                numOfTiles--;
                            }
                        }

                        else{
                            addLeftTile = true;
                            numOfTiles--;
                        }



                        if (tileUp != null) {
                            if (tileUp.getID() != 1){
                                addUpTile = true;
                                numOfTiles--;
                            }
                        }

                        else{
                            addUpTile = true;
                            numOfTiles--;
                        }



                        if (tileDown != null) {
                            if (tileDown.getID() != 1){
                                addDownTile = true;
                                numOfTiles--;
                            }
                        }
                        else{
                            addDownTile = true;
                            numOfTiles--;
                        }

                        //Checks if there is more than one tile connected or there is a bend
                        if (numOfTiles >= 3){
                            addDownTile = true;
                            addLeftTile = true;
                            addRightTile = true;
                            addUpTile = true;
                        }
                        else if (numOfTiles == 2 && ((addRightTile && addUpTile) || (addUpTile && addLeftTile) ||
                                (addLeftTile && addDownTile) || (addDownTile && addRightTile))){
                            addDownTile = true;
                            addLeftTile = true;
                            addRightTile = true;
                            addUpTile = true;
                        }

                        if (addRightTile){
                            tileColliders.add(new Line2D.Float(tile.getX() + Tile.TILE_SIZE, tile.getY(),
                                    tile.getX() + Tile.TILE_SIZE, tile.getY() + Tile.TILE_SIZE));
                        }
                        if (addLeftTile){
                            tileColliders.add(new Line2D.Float(tile.getX(), tile.getY(),
                                    tile.getX(), tile.getY() + Tile.TILE_SIZE));
                        }
                        if (addUpTile){
                            tileColliders.add(new Line2D.Float(tile.getX(), tile.getY() + Tile.TILE_SIZE,
                                    tile.getX() + Tile.TILE_SIZE, tile.getY() + Tile.TILE_SIZE));
                        }
                        if (addDownTile){
                            tileColliders.add(new Line2D.Float(tile.getX(), tile.getY(),
                                    tile.getX() + Tile.TILE_SIZE, tile.getY()));
                        }

                        //Adds colliders to gameObject
                        tileObject.addComponent(new Collider(tileColliders, Window.scenes.getOrDefault(1, null).gameObjects));
                    }

                    this.addGameObjectToScene(tileObject);
                    loadedTiles.add(tile);
                }
            }
        }
    }

    private void unloadTiles(){
        for (int i = loadedTiles.size()-1; i >= 0; i--){
            Tile loadedTile = loadedTiles.get(i);
            //System.out.println(loadedTile.running());
            if ((loadedTile.getX() < ((playersRoomPositionX - roomGenerationSize)*worldGen.roomSize() - 1)*Tile.TILE_SIZE
                    || loadedTile.getX() > ((playersRoomPositionX + roomGenerationSize + 1)*worldGen.roomSize() + 1)*Tile.TILE_SIZE)
                    || (loadedTile.getY() < ((playersRoomPositionY-roomGenerationSize)*worldGen.roomSize() - 1)*Tile.TILE_SIZE
                    || loadedTile.getY() > ((playersRoomPositionY+roomGenerationSize + 1)*worldGen.roomSize() + 1)*Tile.TILE_SIZE)){
                loadedTile.stop(gameObjects);
                loadedTiles.remove(i);
            }
        }
        System.out.println(gameObjects.size());
    }




}

/** Special Method For Later
        if (playersPreviousPositionX == null || playersPreviousPositionY == null ||
                (Math.abs(playersPositionX-playersPreviousPositionX) > 6 && Math.abs(playersPositionY-playersPreviousPositionY) > 6)){

            loadTiles(playersPositionX-loadWidth, playersPositionX+loadWidth, 1,
                    playersPositionY-loadHeight, playersPositionY+loadHeight, 1);

        }

        else if (playersPositionX != playersPreviousPositionX && playersPositionY != playersPreviousPositionY){
            int dx = playersPositionX - playersPreviousPositionX;
            int dy = playersPositionY - playersPreviousPositionY;

            if (dx == 0 && dy > 0){
                loadTiles(playersPositionX-loadWidth,playersPositionX+loadWidth,1,
                    playersPositionY+loadHeight, playersPositionY+loadHeight+dy,1);
            }
            else if (dx == 0 && dy < 0){
                loadTiles(playersPositionX-loadWidth,playersPositionX+loadWidth,1,
                        playersPositionY-loadHeight+dy, playersPositionY-loadHeight,1);
            }
            else if (dx > 0 && dy == 0){
                loadTiles(playersPositionX+loadWidth, playersPositionX+loadWidth+dx,1,
                        playersPositionY-loadWidth,playersPositionY+loadHeight,1);
            }
            else if (dx < 0 && dy == 0){
                loadTiles(playersPositionX-loadWidth+dx, playersPositionX-loadWidth,1,
                        playersPositionY-loadWidth,playersPositionY+loadHeight,1);
            }
        }

         */
