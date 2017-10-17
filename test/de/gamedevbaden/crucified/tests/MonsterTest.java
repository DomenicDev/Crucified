package de.gamedevbaden.crucified.tests;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import de.gamedevbaden.crucified.enums.ModelType;

public class MonsterTest extends SimpleApplication {

    public static void main(String[]args) {
        new MonsterTest().start();
    }

    @Override
    public void simpleInitApp() {

        // demon model does not work
        Spatial demon = assetManager.loadModel("Models/Monster/Demon.j3o");
        AnimControl control = demon.getControl(AnimControl.class);
        AnimChannel channel = control.createChannel();
        channel.setAnim("FastRun");

        // player works perfectly
        Spatial player = assetManager.loadModel(ModelType.Player);
        player.setLocalTranslation(2,0,0);
        AnimControl control1 = player.getControl(AnimControl.class);
        AnimChannel channel1 =control1.createChannel();
        channel1.setAnim("walk");

        rootNode.attachChild(demon);
        rootNode.attachChild(player);

        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.White);
        rootNode.addLight(light);

    }
}
