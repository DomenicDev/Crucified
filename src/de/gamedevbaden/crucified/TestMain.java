package de.gamedevbaden.crucified;

import com.jme3.app.SimpleApplication;
import de.gamedevbaden.crucified.appstates.*;

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

        flyCam.setMoveSpeed(10);

        stateManager.attach(new EntityDataState());
        stateManager.attach(new ModelLoaderAppState());
        stateManager.attach(new ModelViewAppState());
        stateManager.attach(new MovementInterpolator());
        //    stateManager.attach(new CameraAppState());
        stateManager.attach(new PhysicAppState());

        stateManager.attach(new SceneEntityLoader());

    }
}
