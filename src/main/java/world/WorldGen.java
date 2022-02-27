package world;

import engine.GameObject;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public abstract class WorldGen {

    protected float initialX, initialY;
    protected Tile [] [] room;
    protected int roomSize;
    protected static Random rng = new Random();

    public WorldGen(){

    }
    public void setRoom(int x, int y, int configs, ArrayList<int []> rooms, HashMap<String, Tile> tiles, ArrayList<GameObject> gameObjects){
        int [] newRoom = {x,y};
        boolean addNewRoom = true;
        for (int i = rooms.size()-1; i >= 0; i--){
            int [] room = rooms.get(i);
            if (room[0] == newRoom[0] && room[1] == newRoom[1]){
                addNewRoom = false;
                rooms.set(i,newRoom);
                break;
            }
        }

        if (addNewRoom){
            addRoom((x-1)*roomSize*Tile.TILE_SIZE, (y-1)*roomSize*Tile.TILE_SIZE,configs,tiles,gameObjects);
            rooms.add(newRoom);
        }
    }

    public void generateRooms(int initialX, int initialY, int finalX, int finalY, ArrayList<int []> rooms, HashMap<String, Tile> tiles, int [] direction, ArrayList<GameObject> gameObjects){
        for (int x = initialX; x <= finalX; x++){
            for (int y = initialY; y <= finalY; y++){
                if (!rooms.isEmpty()) {
                    boolean generateRoom = true;
                    for (int i = rooms.size() - 1; i >= 0; i--) {
                        int[] getRoom = rooms.get(i);
                        if (x == getRoom[0] && y == getRoom[1]) {
                            System.out.print("Found Room:");
                            generateRoom = false;
                            break;
                        }
                    }
                    if (generateRoom){
                        System.out.println("Generating Rooms...");
                        addRoom((x * (roomSize -1)) * Tile.TILE_SIZE, (y * (roomSize -1)) * Tile.TILE_SIZE, direction[rng.nextInt(2)], tiles, gameObjects);
                        int[] newRoom = {x, y};
                        rooms.add(newRoom);
                    }
                    else{
                        System.out.println(" No Rooms");
                    }
                } else {
                    addRoom((x * (roomSize-1)) * Tile.TILE_SIZE, (y * (roomSize - 1)) * Tile.TILE_SIZE, direction[rng.nextInt(2)], tiles, gameObjects);
                    int[] newRoom = {x, y};
                    rooms.add(newRoom);
                }
            }
        }
    }

    public void deleteRooms(int originX, int originY, int size, ArrayList<int []> rooms){
        for (int i = rooms.size()-1; i >= 0; i--){
            int [] room = rooms.get(i);
            if (room[0] < originX-size || room[0] > originX+size || room[1] < originY-size || room[1] > originY-size) {
                rooms.remove(i);
            }
        }
    }

    public void addRoom(float originX, float originY, int spaceConfigs, HashMap<String, Tile> tiles, ArrayList<GameObject> gameObjects /*ArrayList<Collider> colliders*/){
        //Creates a hash map in order to read the list
        int leap = room.length;

        //Sets up a room
        for (int x = 0; x < leap; x++){
            for (int y = 0; y < leap; y++){
                if (x >= 1 && x < leap-1 && y >= 1 && y < leap-1) {
                    room[x][y] = new Tile(x * Tile.TILE_SIZE + originX, y * Tile.TILE_SIZE + originY, 2);
                }
                else{
                    room[x][y] = new Tile(x * Tile.TILE_SIZE + originX, y * Tile.TILE_SIZE + originY, 1);
                }
            }
        }

        //Switch statement in order to config the room space
        switch (spaceConfigs){
            case 1:
                for (int y = 1; y < leap-1; y++){
                    room[0][y] = new Tile(0 * Tile.TILE_SIZE + originX, y * Tile.TILE_SIZE + originY, 2);
                }
                break;
            case 2:
                for (int x = 1; x < leap-1; x++){
                    room[x][0] = new Tile(x * Tile.TILE_SIZE + originX, 0 * Tile.TILE_SIZE + originY, 2);
                }
                break;
            case 3:
                for (int y = 1; y < leap-1; y++){
                    room[leap-1][y] = new Tile((leap-1) * Tile.TILE_SIZE + originX, y * Tile.TILE_SIZE + originY, 2);
                }
                break;
            case 4:
                for (int x = 1; x < leap-1; x++){
                    room[x][leap-1] = new Tile(x * Tile.TILE_SIZE + originX, (leap-1) * Tile.TILE_SIZE + originY, 2);
                }
                break;
        }

        //Adds room to hash map
        for (int x = 0; x < leap; x++){
            for (int y = 0; y < leap; y++){
                String key = room[x][y].getX()+" "+room[x][y].getY();

                Tile usedTile = tiles.get(key);
                if (usedTile != null){
                    if (usedTile.getID() < room[x][y].getID()){
                        //System.out.println("Tile = Tile");
                        usedTile.stop(gameObjects);
                        tiles.replace(key, room[x][y]);

                    }
                    else{
                        //System.out.println("Tile != Tile");
                    }

                }
                else{
                    tiles.put(key,room[x][y]);
                }
            }
        }
    }

    /** Getters */
    public int roomSize(){
        return this.roomSize - 1;
    }
}
