package de.gamedevbaden.crucified.tests.SimpleClientServerTest;

import com.jme3.app.SimpleApplication;
import com.simsilica.es.EntityData;
import de.gamedevbaden.crucified.appstates.*;
import de.gamedevbaden.crucified.game.GameSession;
import de.gamedevbaden.crucified.net.client.GameClient;

/**
 * Created by Domenic on 26.04.2017.
 */
public class ClientTest extends SimpleApplication {


    public static void main(String[] args) {
        //   NetworkUtils.initSerializers();
        new ClientTest().start();
    }

    @Override
    public void simpleInitApp() {

        setPauseOnLostFocus(false);

        flyCam.setMoveSpeed(10);


        GameClient client = new GameClient();
        stateManager.attach(client);
        client.connect("localhost", 5555);

        EntityData entityData = client.getEntityData();
        GameSession gameSession = client.getGameSession();

        stateManager.attach(new InputAppState());
        stateManager.attach(new ModelLoaderAppState());
        stateManager.attach(new ModelViewAppState());
        stateManager.attach(new MovementInterpolator());
        stateManager.attach(new EntityDataState(entityData));
        stateManager.attach(new GameSessionAppState(gameSession));
        stateManager.attach(new GameEventAppState());

    }


}
