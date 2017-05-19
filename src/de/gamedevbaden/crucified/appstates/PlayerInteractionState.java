package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import de.gamedevbaden.crucified.enums.InputCommand;
import de.gamedevbaden.crucified.es.components.InteractionComponent;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.es.components.Pickable;

import java.util.ArrayList;


/**
 * Created by Domenic on 18.05.2017.
 */
public class PlayerInteractionState extends AbstractAppState implements ActionListener {

    private EntitySet pickables;
    private EntitySet interactableEntities;

    private ModelViewAppState modelViewAppState;
    private InputManager inputManager;
    private Camera cam;

    private Ray ray;
    private CollisionResults results;
    private Node rootNode;

    private ArrayList<PlayerInteractionListener> listeners;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.interactableEntities = entityData.getEntities(InteractionComponent.class, Model.class);
        this.pickables = entityData.getEntities(Pickable.class);

        this.cam = app.getCamera();
        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);
        this.rootNode = ((SimpleApplication) app).getRootNode();
        this.listeners = new ArrayList<>();

        this.ray = new Ray();
        this.ray.setLimit(2f);

        this.results = new CollisionResults();

        this.inputManager = app.getInputManager();
        this.inputManager.addListener(this, InputCommand.Interaction.name());

        super.initialize(stateManager, app);
    }

    public void addInteractionListener(PlayerInteractionListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void update(float tpf) {
        interactableEntities.applyChanges();
        pickables.applyChanges();
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed) {

            ray.setOrigin(cam.getLocation());
            ray.setDirection(cam.getDirection());

            results.clear();

            rootNode.collideWith(ray, results);

            if (results.size() > 0) {
                CollisionResult closest = results.getClosestCollision();
                if (closest.getGeometry().getParent().getUserData("entityId") instanceof Long) {
                    long id = closest.getGeometry().getParent().getUserData("entityId");
                    EntityId entityId = new EntityId(id);

                    if (interactableEntities.containsId(entityId)) {
                        for (PlayerInteractionListener listener : listeners) {
                            listener.onInteractionWith(entityId);
                        }
                    } else if (pickables.containsId(entityId)) {
                        for (PlayerInteractionListener listener : listeners) {
                            listener.onItemPickup(entityId);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void cleanup() {
        this.interactableEntities.release();
        this.interactableEntities.clear();
        this.interactableEntities = null;

        this.results.clear();
        this.results = null;
        this.ray = null;

        this.listeners.clear();
        this.inputManager.removeListener(this);

        super.cleanup();
    }


    public interface PlayerInteractionListener {

        void onInteractionWith(EntityId interactedEntity);

        void onItemPickup(EntityId entityToPickup);

    }

}
