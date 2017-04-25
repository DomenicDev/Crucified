package de.gamedevbaden.crucified;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * <code>TestMain</code> contains the main-method.
 * Created by Domenic on 09.04.2017.
 */
public class TestMain extends SimpleApplication {

    public static void main(String[] args) {
        new TestMain().start();
    }

    @Override
    public void simpleInitApp() {
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        geom.setLocalTranslation(1, 0, 1);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);



        rootNode.attachChild(geom);
    }
}
