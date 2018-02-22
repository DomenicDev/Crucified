package de.gamedevbaden.crucified.appstates.gamelogic;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.GameCommanderHolder;
import de.gamedevbaden.crucified.appstates.game.GameSessionManager;
import de.gamedevbaden.crucified.es.components.Transform;
import de.gamedevbaden.crucified.es.utils.EntityFactory;
import de.gamedevbaden.crucified.game.GameCommander;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameStartupAppState extends AbstractAppState {

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();

        // create predefined locations where the artifacts could be placed
        // the positions represent special locations on the map
        List<Vector3f> artifactPositions = new ArrayList<>();
        artifactPositions.add(new Vector3f(-115.70802f, 29.93187f, 42.908363f)); // stone ruin
        artifactPositions.add(new Vector3f(82.321434f, 8.465233f, 161.46268f)); // Jetty
        artifactPositions.add(new Vector3f(99.2426f, 11.6180725f, -20.611597f)); // cave
        artifactPositions.add(new Vector3f(43.783066f, 52.197044f, -89.26014f)); // church ruin
        artifactPositions.add(new Vector3f(74.073235f, 19.828806f, -182.2285f)); // stone ruin 2
        artifactPositions.add(new Vector3f(-130.023f, 12.014025f, -88.979195f)); // beach house

        // create 4 artifacts at random (predefined) places
        for (int i = 0; i < 4; i++) {
            Random r = new Random();
            int x = r.nextInt(artifactPositions.size());
            Vector3f artifactPos = artifactPositions.get(x);

            // create artifact
            EntityFactory.createArtifact(entityData, artifactPos);

            // now we delete the just used index position from the list so it can't be used more than once
            artifactPositions.remove(x);
        }

        // Todo: set start position for players...
          Vector3f[][] startPositions = new Vector3f[][] {
                {new Vector3f(-19.920132f, 12.266039f, 52.698334f), new Vector3f(-4.485677f, 12.948147f, -130.6954f)},
                {new Vector3f(15.484093f, 12.042202f, 158.9679f), new Vector3f(-153.5913f, 10.669474f, -91.215935f)},
                {new Vector3f(100.89399f, 24.614304f, 69.54313f), new Vector3f(37.130386f, 50.90343f, -82.32215f)},
                {new Vector3f(150.73814f, 21.07069f, -122.06702f),	new Vector3f(-77.181366f, 13.087656f, -23.556862f)}
        };

     /*   Vector3f[][] startPositions = new Vector3f[][] {
                {new Vector3f(0,8,0), new Vector3f(5, 5, 0)},
                {new Vector3f(10, 5, 3), new Vector3f(1, 5, 0)}
        }; */

        Random m = new Random();
        int x = m.nextInt(1);
        Vector3f[] pos = startPositions[x];

        PlayerHolderAppState playerHolder = stateManager.getState(PlayerHolderAppState.class);
        EntityId playerOne = playerHolder.getPlayerOne();
        EntityId playerTwo = playerHolder.getPlayerTwo();

        EntityFactory.createPlayer(stateManager.getState(EntityDataState.class).getEntityData(), playerOne, pos[0]);
        EntityFactory.createDemon(stateManager.getState(EntityDataState.class).getEntityData(), playerTwo, pos[1]);

        System.out.println(pos[0] + "  " + pos[1]);

    }
}
