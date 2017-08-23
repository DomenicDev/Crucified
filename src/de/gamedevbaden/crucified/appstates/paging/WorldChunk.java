package de.gamedevbaden.crucified.appstates.paging;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.List;

public class WorldChunk {

    private BoundingBox chunkArea;
    private Node chunkRootNode;
    private List<Node> subNodes;
    private ChunkPosition chunkPosition;
    private Node gameWorldNode;

    public WorldChunk(Node gameWorldNode, BoundingBox chunkArea, ChunkPosition chunkPosition) {
        this.gameWorldNode = gameWorldNode;
        this.chunkArea = chunkArea;
        this.chunkPosition = chunkPosition;

        // init chunk root node
        this.chunkRootNode = new Node("ChunkRootNode");
        this.chunkRootNode.setLocalTranslation(0,0,0);

        // init sub node list
        this.subNodes = new ArrayList<>();
    }

    public Node getGameWorldNode() {
        return gameWorldNode;
    }

    public Node getChunkRootNode() {
        return chunkRootNode;
    }

    public ChunkPosition getChunkPosition() {
        return chunkPosition;
    }

    public boolean isInChunk(Spatial spatial) {
        return isInChunk(spatial.getLocalTranslation());
    }

    public boolean isInChunk(Vector3f translation) {
        return chunkArea.contains(translation) || chunkArea.distanceToEdge(translation) == 0.0f;
    }

    /**
     * Will return all sub nodes attached to the chunks root node.
     * Each of sub node should have a PagingOptions user data added.
     * @return all sub nodes currently added to this chunk
     */
    public List<Node> getSubNodes() {
        return subNodes;
    }

    /**
     * Attaches the specified node to the root node of this chunk
     * @param subNodeToAdd the node to add to the chunks root node
     */
    public void addSubNode(Node subNodeToAdd) {
        if (subNodeToAdd == null) {
            return;
        }
        chunkRootNode.attachChild(subNodeToAdd);
        subNodes.add(subNodeToAdd);
    }
}
