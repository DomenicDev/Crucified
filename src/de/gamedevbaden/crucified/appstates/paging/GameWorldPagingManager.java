package de.gamedevbaden.crucified.appstates.paging;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.terrain.geomipmap.TerrainQuad;
import de.gamedevbaden.crucified.userdata.PagingOptionsUserData;
import de.gamedevbaden.crucified.utils.GameConstants;
import de.gamedevbaden.crucified.utils.Optimizer;

import java.util.*;

/**
 * This app state can create chunks for a game world (level).
 * It also manages them and dynamically can attach / detach chunks from the scene
 * depending on whether they can be seen or not.
 * <p>
 * Use <code>createChunksForGameWorld()</code> to generate chunks
 */
public class GameWorldPagingManager extends AbstractAppState {

    private HashMap<Vector2f, WorldChunk> chunks = new HashMap<>(); // all chunks are stored in this map
    private WorldChunk currentChunk; // the chunk the player is inside right now
    private List<WorldChunk> currentVisibleChunks = new ArrayList<>(); // the surrounding chunks of the 'currentChunk'

    // used to look up entries in the chunk map, so we don't need to create a new vector object every time
    private Vector2f lookUpVector = new Vector2f();

    private Camera cam; // the camera is used for position reference

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.cam = app.getCamera();
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        Vector3f referencePos = cam.getLocation();

        // if currentChunk is null we set it the first time
        if (currentChunk == null) {
            // we detach all chunks since we don't know which chunk the player is in
            for (WorldChunk chunk : getChunks()) {
                chunk.getChunkRootNode().removeFromParent();
            }
            // now we try to find the chunk the player is in currently
            for (WorldChunk chunk : getChunks()) {
                if (chunk.isInChunk(referencePos)) {
                    this.currentChunk = chunk;
                    updateGrid(currentChunk);
                    break;
                }
            }
            return;
        }

        // we check if the player has moved out of the current chunk
        if (!currentChunk.isInChunk(referencePos)) {
            // find new "middle" chunk (the chunk the player moved into)
            // for that we look up the surrounding chunks first
            WorldChunk newCenterChunk = null;
            for (WorldChunk chunk : this.currentVisibleChunks) {
                if (chunk.isInChunk(referencePos)) {
                    newCenterChunk = chunk;
                    break;
                }
            }

            if (newCenterChunk != null) { // should always be the case, except when teleported
                updateGrid(newCenterChunk);
            } else {
                // the player got warped
                // we have to check every chunk to find the current chunk for the player
                for (WorldChunk chunk : getChunks()) {
                    if (chunk.isInChunk(referencePos)) {
                        updateGrid(chunk);
                        break;
                    }
                }
            }
        }
    }

    private void updateGrid(WorldChunk centerChunk) {
        // we found the chunk the player moved inside
        // we now want to get the surrounding chunks for that new 'center' chunk
        List<WorldChunk> newSurroundingChunks = getSurroundingCells(centerChunk.getChunkPosition());
        // we need to add the center chunk to that chunk set as well
        newSurroundingChunks.add(centerChunk);
        // now we also want to get the chunks which need to be loaded and unloaded
        List<WorldChunk> chunksToUnload = getChunksNotContainedInList(this.currentVisibleChunks, newSurroundingChunks);
        List<WorldChunk> chunksToLoad = getChunksNotContainedInList(newSurroundingChunks, this.currentVisibleChunks);

        // unload first and then load new chunks
        unloadChunks(chunksToUnload);
        loadChunks(chunksToLoad);

        // apply new chunks globally
        this.currentChunk = centerChunk;
        this.currentVisibleChunks = newSurroundingChunks;
    }

    private void addToListIfExists(int x, int y, List<WorldChunk> chunkList) {
        lookUpVector.set(x, y);
        WorldChunk chunk;
        if ((chunk = this.chunks.get(lookUpVector)) != null) {
            chunkList.add(chunk);
        }
    }

    private List<WorldChunk> getSurroundingCells(ChunkPosition pos) {
        List<WorldChunk> surroundingChunks = new ArrayList<>();
        int x = pos.getX();
        int y = pos.getY();
        addToListIfExists(x - 1, y - 1, surroundingChunks); // top left
        addToListIfExists(x, y - 1, surroundingChunks); // top north
        addToListIfExists(x + 1, y - 1, surroundingChunks); // top right
        addToListIfExists(x + 1, y, surroundingChunks); // middle right
        addToListIfExists(x + 1, y + 1, surroundingChunks); // bottom right
        addToListIfExists(x, y + 1, surroundingChunks); // bottom south
        addToListIfExists(x - 1, y + 1, surroundingChunks); // bottom left
        addToListIfExists(x - 1, y, surroundingChunks); // middle left
        return surroundingChunks;
    }

    private List<WorldChunk> getChunksNotContainedInList(List<WorldChunk> listToCheck, List<WorldChunk> referenceList) {
        List<WorldChunk> notContained = new ArrayList<>();
        for (WorldChunk chunk : listToCheck) {
            if (!referenceList.contains(chunk)) {
                notContained.add(chunk);
            }
        }
        return notContained;
    }

    private Collection<WorldChunk> getChunks() {
        return chunks.values();
    }

    public void setChunks(List<WorldChunk> chunks) {
        HashMap<Vector2f, WorldChunk> chunkHashMap = new HashMap<>();
        for (WorldChunk chunk : chunks) {
            ChunkPosition chunkPos = chunk.getChunkPosition();
            Vector2f gridPos = new Vector2f(chunkPos.getX(), chunkPos.getY());
            chunkHashMap.put(gridPos, chunk);
        }
        this.chunks = chunkHashMap;
    }

    private void loadChunks(List<WorldChunk> chunksToLoad) {
        for (WorldChunk chunk : chunksToLoad) {
            Node chunkRootNode = chunk.getChunkRootNode();
            Node gameWorldNode = chunk.getGameWorldNode();
            gameWorldNode.attachChild(chunkRootNode);
        }
    }

    private void unloadChunks(List<WorldChunk> chunksToUnload) {
        for (WorldChunk chunk : chunksToUnload) {
            Node chunkRootNode = chunk.getChunkRootNode();
            chunkRootNode.removeFromParent();
        }
    }

    public List<WorldChunk> createChunksForGameWorld(Node gameWorld, int amountOfChunksPerRow, AssetManager assetManager) {
        // we will store all chunks the following list
        List<WorldChunk> chunkList = new ArrayList<>();

        // we divide our level in chunks based on the bounding box of the level
        BoundingBox gameWorldBoundingBox = (BoundingBox) gameWorld.getWorldBound();

        // for debugging
     //   gameWorld.attachChild(visualizeBoundingBox(gameWorldBoundingBox, assetManager, ColorRGBA.Red));

        float worldSizeX = gameWorldBoundingBox.getXExtent() * 2;
        float worldSizeZ = gameWorldBoundingBox.getZExtent() * 2;
        float worldSizeY = gameWorldBoundingBox.getYExtent() * 2;

        if (worldSizeX == Float.POSITIVE_INFINITY || worldSizeZ == Float.POSITIVE_INFINITY) {
            // sometimes it happens that the world bounding box is infinite
            // this is infinite due to some scene objects (probably audio nodes?)
            // we take the bounding box of the terrain instead
            for (Spatial child : gameWorld.getChildren()) {
                if (child instanceof TerrainQuad) {
                    BoundingBox boundingBox = (BoundingBox) child.getWorldBound();
                    worldSizeX = boundingBox.getXExtent() * 2;
                    worldSizeZ = boundingBox.getZExtent() * 2;
                    worldSizeY = boundingBox.getYExtent() * 2;
                    break;
                }
            }
        }

        float halfWorldSizeX = worldSizeX / 2f;
        float halfWorldSizeZ = worldSizeZ / 2f;
        float halfWorldSizeY = worldSizeY / 2f;

        float widthOfOneChunk = worldSizeX / amountOfChunksPerRow;
        float heightOfOneChunk = worldSizeZ / amountOfChunksPerRow;

        float halfWidthOfChunk = widthOfOneChunk / 2f;
        float halfHeightOfChunk = heightOfOneChunk / 2f;

        int yPos = 0; // represents the y ChunkPosition
        int xPos = 0; // represents the x ChunkPosition

        for (float y = -halfWorldSizeZ; y < halfWorldSizeZ; y += heightOfOneChunk, yPos++, xPos = 0) {
            for (float x = -halfWorldSizeX; x < halfWorldSizeX; x += widthOfOneChunk, xPos++) {
                // we set the center of our next chunk
                Vector3f chunkCenter = new Vector3f();
                chunkCenter.z = y + halfHeightOfChunk + gameWorldBoundingBox.getCenter().z;
                chunkCenter.x = x + halfWidthOfChunk + gameWorldBoundingBox.getCenter().x;
                chunkCenter.y = gameWorldBoundingBox.getCenter().getY();

                // visualize the chunk area (for debugging)
                BoundingBox chunkArea = new BoundingBox(chunkCenter, halfWidthOfChunk, halfWorldSizeY, halfHeightOfChunk);
                gameWorld.attachChild(visualizeBoundingBox(chunkArea, assetManager, ColorRGBA.Blue));

                // we create a new chunk for this area
                WorldChunk chunk = new WorldChunk(gameWorld, chunkArea, new ChunkPosition(xPos, yPos));
                chunkList.add(chunk);
            }
        }

        // look up for nodes with the paging user data
        for (Spatial node : gameWorld.getChildren()) {
            if (!(node instanceof Node)) continue;
            PagingOptionsUserData options;
            if ((options = node.getUserData(GameConstants.USER_DATA_PAGING_OPTIONS)) != null) {
                // we want to look up all children of this node and want
                // to add it to the specific chunk

                // we create a map which holds the sub node for the specific chunk for that model type
                HashMap<WorldChunk, Node> subNodesForChunks = new HashMap<>();

                // go through every spatial and look which chunk this spatial should be added to
                for (Spatial child : ((Node) node).getChildren()) {
                    for (WorldChunk chunk : chunkList) {
                        if (chunk.isInChunk(child)) {
                            Node subNode;
                            // check if there is already a node for that chunk
                            // if not create a node and add it to the list
                            if ((subNode = subNodesForChunks.get(chunk)) == null) {
                                subNode = new Node(node.getName());
                                subNode.setUserData(GameConstants.USER_DATA_PAGING_OPTIONS, options);
                                subNodesForChunks.put(chunk, subNode);
                            }

                            // we attach this scene object to the sub node of this chunk
                            subNode.attachChild(child);

                            // we can break out because we found the chunk for that model
                            break;
                        }
                    }
                }

                // add the newly created sub nodes to the specific chunk
                for (Map.Entry<WorldChunk, Node> e : subNodesForChunks.entrySet()) {
                    WorldChunk chunk = e.getKey();
                    Node subNode = e.getValue();
                    chunk.addSubNode(subNode);
                }

                // remove the node because there should not be any children attached to it anymore
                node.removeFromParent();

                // attach chunks to game world
                for (WorldChunk chunk : chunkList) {
                    gameWorld.attachChild(chunk.getChunkRootNode());
                }
            }
        }

        // we now want to apply the paging settings from the user data
        applyPagingOptions(chunkList);

        // finally return the list
        return chunkList;
    }

    private Geometry visualizeBoundingBox(BoundingBox boundingBox, AssetManager assetManager, ColorRGBA color) {
        Geometry box = WireBox.makeGeometry(boundingBox);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        box.setMaterial(mat);
        return box;
    }

    private void applyPagingOptions(List<WorldChunk> chunkList) {
        for (WorldChunk chunk : chunkList) {
            applyPagingOptions(chunk);
        }
    }

    private void applyPagingOptions(WorldChunk chunk) {
        List<Node> nodes = chunk.getSubNodes();
        for (Node subNode : nodes) {
            PagingOptionsUserData options = subNode.getUserData(GameConstants.USER_DATA_PAGING_OPTIONS);
            if (options != null) {

                // we remove the collision shape geometry of that model if there is one.
                // I primarily wanted to batch the geometries first and remove the batched collision shape afterwards
                // but for some reason batching did not always lead to the same index position for the geometry
                // so it becomes difficult to remove it afterwards
                if (options.isHasCustomCollisionShapeGeometry()) {
                    subNode.depthFirstTraversal(spatial -> {
                        if (spatial.getName().equals(GameConstants.CUSTOM_COLLISION_SHAPE_NAME)) spatial.removeFromParent();
                    });
                }

                // see if we need to batch
                if (options.isUseBatching()) {
                    // batch geometries
                    Optimizer.optimize(subNode);

                    // Since the scene is now optimized we need to remove the old (empty) nodes!
                    // To avoid a concurrent modifier exception we need to create a new list containing
                    // the nodes we want to remove
                    List<Spatial> nodesToRemove = new ArrayList<>();
                    for (Spatial child : subNode.getChildren()) {
                        if (child instanceof Node) {
                            nodesToRemove.add(child);
                        }
                    }

                    // finally we remove the nodes
                    for (Spatial child : nodesToRemove) {
                        subNode.detachChild(child);
                    }
                    nodesToRemove.clear(); // free memory
                }
            }
        }
    }
}
