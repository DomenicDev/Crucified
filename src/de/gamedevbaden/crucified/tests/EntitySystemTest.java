package de.gamedevbaden.crucified.tests;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import de.gamedevbaden.crucified.appstates.*;
import de.gamedevbaden.crucified.appstates.view.ModelLoaderAppState;
import de.gamedevbaden.crucified.appstates.view.ModelViewAppState;

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
        stateManager.attach(new InputAppState());

        stateManager.attach(new PhysicsPlayerMovementAppState());


        AmbientLight light = new AmbientLight(ColorRGBA.White);
        rootNode.addLight(light);


    }
}
