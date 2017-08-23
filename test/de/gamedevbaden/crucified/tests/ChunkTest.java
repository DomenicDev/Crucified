package de.gamedevbaden.crucified.tests;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import de.gamedevbaden.crucified.appstates.paging.GameWorldPagingManager;
import de.gamedevbaden.crucified.appstates.paging.WorldChunk;

import java.util.List;

public class ChunkTest extends SimpleApplication {

    public static void main(String[] args) {
        new ChunkTest().start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);
        cam.setLocation(Vector3f.ZERO.add(0,0,10));

        Node scene = (Node) assetManager.loadModel("Scenes/PagingTestScene.j3o");
        rootNode.attachChild(scene);

        GameWorldPagingManager pagingManager = new GameWorldPagingManager();
        stateManager.attach(pagingManager);

        List<WorldChunk> chunkList = pagingManager.createChunksForGameWorld(scene, 4, assetManager);
        pagingManager.setChunks(chunkList);

        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.White);
        rootNode.addLight(light);
    }
}
