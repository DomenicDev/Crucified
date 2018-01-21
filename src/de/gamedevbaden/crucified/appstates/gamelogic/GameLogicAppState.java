package de.gamedevbaden.crucified.appstates.gamelogic;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.appstates.GameCommanderHolder;
import de.gamedevbaden.crucified.enums.GameDecisionType;
import de.gamedevbaden.crucified.es.components.AliveComponent;
import de.gamedevbaden.crucified.es.components.ArtifactComponent;
import de.gamedevbaden.crucified.es.components.CanPickupArtifactCompont;
import de.gamedevbaden.crucified.game.GameCommander;

public class GameLogicAppState extends AbstractAppState {

    private EntitySet artifacts;
    private EntitySet players;
    private GameCommanderHolder gameCommanderHolder;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.gameCommanderHolder = stateManager.getState(GameCommanderHolder.class);
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.artifacts = entityData.getEntities(ArtifactComponent.class);
        this.players = entityData.getEntities(CanPickupArtifactCompont.class, AliveComponent.class);
    }

    @Override
    public void update(float tpf) {

        if (players.applyChanges()) {

            if (players.size() == 0) {
                // monster win
                setGameDecided(GameDecisionType.MonsterWins);
            }

        }

        if (artifacts.applyChanges()) {

            if (artifacts.size() == 0) {
                // human player wins
                setGameDecided(GameDecisionType.HumanPlayersWin);
            }

        }

    }

    private void setGameDecided(GameDecisionType decisionType) {
        for (GameCommander commander : gameCommanderHolder.getAll()) {
            commander.onGameDecided(decisionType);
        }
    }
}
