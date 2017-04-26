package de.gamedevbaden.crucified.tests;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import de.gamedevbaden.crucified.appstates.*;

/**
 * Created by Domenic on 11.04.2017.
 */
public class EntitySystemTest extends SimpleApplication {


    public static void main(String[] args) {
        new EntitySystemTest().start();
    }


    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        stateManager.attach(new EntityDataState());
        stateManager.attach(new ModelLoaderAppState());
        stateManager.attach(new ModelViewAppState());
        stateManager.attach(new CameraAppState());
        stateManager.attach(new PhysicAppState());
        stateManager.attach(new CharacterControlAppState());
        stateManager.attach(new PlayerInputAppState());
        stateManager.attach(new PhysicsPlayerMovementAppState());
        stateManager.attach(new PlayerAnimationAppState());

        stateManager.attach(new GameState());


        AmbientLight light = new AmbientLight(ColorRGBA.White);
        rootNode.addLight(light);


    }
}
