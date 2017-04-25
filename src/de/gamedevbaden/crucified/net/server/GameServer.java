package de.gamedevbaden.crucified.net.server;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.simsilica.es.EntityData;
import com.simsilica.es.base.DefaultEntityData;
import de.gamedevbaden.crucified.net.NetworkUtils;

import java.io.IOException;

/**
 * Created by Domenic on 16.04.2017.
 */
public class GameServer extends SimpleApplication {

    public static void main(String[] args) {
        new GameServer().start();
    }

    @Override
    public void simpleInitApp() {

        NetworkUtils.initSerializers();

        try {
            Server server = Network.createServer(5555);


            EntityData entityData = new DefaultEntityData();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
