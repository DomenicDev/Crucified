package de.gamedevbaden.crucified.appstates.action;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.filter.FieldFilter;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.enums.ActionType;
import de.gamedevbaden.crucified.enums.Sound;
import de.gamedevbaden.crucified.es.components.*;

/**
 * This app state handles the single actions
 */
public class ActionHandlerAppState extends AbstractAppState {

    private EntityData entityData;
    private EntitySet screamingEntities;
    private EntitySet attackingEntities;
    private EntitySet alivePlayers;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.screamingEntities = entityData.getEntities(new FieldFilter<>(ActionComponent.class, "action", ActionType.Scream), ActionComponent.class, Transform.class);
        this.attackingEntities = entityData.getEntities(new FieldFilter<>(ActionComponent.class, "action", ActionType.Attack), ActionComponent.class, PhysicsCharacterControl.class, Transform.class);
        this.alivePlayers = entityData.getEntities(AliveComponent.class, Transform.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        this.alivePlayers.applyChanges();

        if (screamingEntities.applyChanges()) {

            for (Entity entity : screamingEntities.getAddedEntities()) {
                createScream(entity.get(Transform.class).getTranslation());
            }

        }

        if (attackingEntities.applyChanges()) {

            for (Entity entity : attackingEntities.getAddedEntities()) {
                for (Entity player : alivePlayers) {
                    if (entity.getId().equals(player.getId())) {
                        continue;
                    }
                    Vector3f attackerPos = entity.get(Transform.class).getTranslation();
                    Vector3f playerPos = player.get(Transform.class).getTranslation();
                    //System.out.println("distance = " + attackerPos.distance(playerPos));
                    if (attackerPos.distance(playerPos) <= 2) {
                        // hit
                        Vector3f dir = playerPos.subtract(attackerPos).normalizeLocal();
                        dir.setY(0);
                        Vector3f viewDirection = entity.get(PhysicsCharacterControl.class).getViewDirection().normalizeLocal();
                        viewDirection.setY(0);
                        //      System.out.println(dir + " " + viewDirection);

                        System.out.println(viewDirection.angleBetween(dir) * FastMath.RAD_TO_DEG);
                        if (viewDirection.angleBetween(dir) * FastMath.RAD_TO_DEG <= 45) {
                            System.out.println("hit");
                        }
                    }
                }
            }

        }
    }

    private void createScream(Vector3f translation) {
        EntityId scream = entityData.createEntity();
        entityData.setComponents(scream,
                new Transform(translation),
                new SoundComponent(Sound.Miss, false, true),
                new Decay(5000)
        );
    }
}
