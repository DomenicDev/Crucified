package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.WatchedEntity;
import de.gamedevbaden.crucified.appstates.game.GameSessionAppState;
import de.gamedevbaden.crucified.appstates.view.ModelViewAppState;
import de.gamedevbaden.crucified.enums.ActionType;
import de.gamedevbaden.crucified.enums.InputCommand;
import de.gamedevbaden.crucified.enums.ItemType;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.utils.GameConstants;

import java.util.ArrayList;
import java.util.Map;


/**
 * This state lets the player interact with entities in the game world.
 * When the player wants to interact we do a ray cast and check if it collides with something.
 * We then check whether the hit geometry has an EntityId (type: long) added as user data
 * and if it does we inform all added listeners about this event.
 * <p>
 * Created by Domenic on 18.05.2017.
 */
public class PlayerInteractionState extends AbstractAppState implements ActionListener {

    private EntitySet pickables;
    private EntitySet equipables;
    private EntitySet entitiesToCraft;
    private EntitySet interactableEntities;
    private EntitySet containers;

    private ModelViewAppState modelViewAppState;
    private PlayerInventoryState inventoryState;
    private InputManager inputManager;
    private Camera cam;

    private Ray ray;
    private CollisionResults results;
    private Node rootNode;

    private EntityId equippedItem;
    private EntityId storedItem;

    private WatchedEntity player;

    private ArrayList<PlayerInteractionListener> listeners;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.interactableEntities = entityData.getEntities(InteractionComponent.class, Model.class);
        this.pickables = entityData.getEntities(Pickable.class);
        this.equipables = entityData.getEntities(Equipable.class);
        this.entitiesToCraft = entityData.getEntities(NeedToBeCrafted.class);
        this.containers = entityData.getEntities(Container.class);
        this.player = entityData.watchEntity(stateManager.getState(GameSessionAppState.class).getGameSession().getPlayer(), ActionGroupComponent.class);

        this.cam = app.getCamera();
        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);
        this.inventoryState = stateManager.getState(PlayerInventoryState.class);
        this.rootNode = ((SimpleApplication) app).getRootNode();
        this.listeners = new ArrayList<>();

        this.ray = new Ray();
        this.ray.setLimit(4f);

        this.results = new CollisionResults();

        this.inputManager = app.getInputManager();
        this.inputManager.addListener(this, InputCommand.Interaction.name());
        this.inputManager.addListener(this, InputCommand.Scream.name());

        this.inputManager.addMapping("Attack", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        this.inputManager.addListener(this, "Attack");

        this.inputManager.addMapping("R", new KeyTrigger(KeyInput.KEY_R));
        this.inputManager.addMapping("G", new KeyTrigger(KeyInput.KEY_G));
        this.inputManager.addMapping("K", new KeyTrigger(KeyInput.KEY_K));
        this.inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (isPressed) {
                    if (name.equals("R")) {
                        if (equippedItem != null) {
                            for (PlayerInteractionListener l : listeners) {
                                l.onItemUnequipped(equippedItem);
                            }
                            storedItem = equippedItem;
                            equippedItem = null;
                        } else if (storedItem != null) {
                            for (PlayerInteractionListener l : listeners) {
                                l.onItemEquipped(storedItem);
                            }
                            equippedItem = storedItem;
                            storedItem = null;
                        }
                    } else if (name.equals("G")) {
                        if (storedItem != null) {
                            for (PlayerInteractionListener l : listeners) {
                                l.onItemDrop(storedItem);
                            }
                            storedItem = null;
                            equippedItem = null;
                        }
                    } else if (name.equals("K")) {

                        // toggle flashlight
                        EntityId flashLight = inventoryState.getFlashlight();
                        if (flashLight != null) {
                            for (PlayerInteractionListener l : listeners) {
                                l.onFlashLightToggle(flashLight);
                            }
                        }

                    }

                }
            }
        }, "R", "G", "K");

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
        equipables.applyChanges();
        entitiesToCraft.applyChanges();
        containers.applyChanges();
        player.applyChanges();
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed) {

            if (name.equals(InputCommand.Interaction.name())) {

                ray.setOrigin(cam.getLocation());
                ray.setDirection(cam.getDirection());

                results.clear();

                rootNode.collideWith(ray, results);

                if (results.size() > 0) {
                    CollisionResult closest = results.getClosestCollision();
                    if (closest.getGeometry().getParent().getUserData(GameConstants.USER_DATA_ENTITY_ID) instanceof Long) {
                        long id = closest.getGeometry().getParent().getUserData(GameConstants.USER_DATA_ENTITY_ID);
                        EntityId entityId = new EntityId(id);

                        if (containers.containsId(entityId)) {
                            // we check if this an artifact container
                            Container c = containers.getEntity(entityId).get(Container.class);
                            if (c.getTypeToStore() == ItemType.Artifact) {
                                // we now check if the player has an artifact
                                EntityId artifactId = inventoryState.getNextOfType(ItemType.Artifact);
                                if (artifactId != null) {
                                    for (PlayerInteractionListener listener : listeners) {
                                        listener.onPutArtifactIntoContainer(entityId, artifactId);
                                    }
                                }
                            }
                        } else if (entitiesToCraft.containsId(entityId)) {
                            NeedToBeCrafted craftComponent = entitiesToCraft.getEntity(entityId).get(NeedToBeCrafted.class);
                            Map<ItemType, Integer> neededItems = craftComponent.getNeededItems();
                            for (ItemType type : neededItems.keySet()) {
                                EntityId ingredient = inventoryState.getNextOfType(type);
                                if (ingredient != null) {
                                    for (PlayerInteractionListener l : listeners) {
                                        l.onItemCraft(entityId, ingredient);
                                    }
                                    break;
                                }
                            }
                        } else if (interactableEntities.containsId(entityId)) {
                            for (PlayerInteractionListener listener : listeners) {
                                listener.onInteractionWith(entityId);
                            }
                        } else if (pickables.containsId(entityId)) {
                            for (PlayerInteractionListener listener : listeners) {
                                listener.onItemPickup(entityId);
                            }
                            // TODO: REMOVE THIS CODE --> MOVE TO ANOTHER APP STATE MAYBE ???
                            if (equipables.containsId(entityId)) {
                                for (PlayerInteractionListener listener : listeners) {
                                    equippedItem = entityId;
                                    listener.onItemEquipped(entityId);
                                }
                            }
                        }
                    }
                }
            } else if (name.equals(InputCommand.Scream.name())) {
                if (player.get(ActionGroupComponent.class).contains(ActionType.Scream)) {
                    for (PlayerInteractionListener l : listeners) {
                        l.onPerformAction(ActionType.Scream);
                    }
                }
            } else if (name.equals("Attack")) {
                if (player.get(ActionGroupComponent.class).contains(ActionType.Attack)) {
                    for (PlayerInteractionListener l : listeners) {
                        l.onPerformAction(ActionType.Attack);
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
        this.listeners = null;
        this.inputManager.removeListener(this);

        super.cleanup();
    }


    public interface PlayerInteractionListener {

        void onPutArtifactIntoContainer(EntityId containerId, EntityId artifactId);

        void onInteractionWith(EntityId interactedEntity);

        void onItemPickup(EntityId entityToPickup);

        void onItemEquipped(EntityId entityToEquip);

        void onItemUnequipped(EntityId itemToUnequip);

        void onItemDrop(EntityId dropItem);

        void onFlashLightToggle(EntityId flashLight);

        void onItemCraft(EntityId targetItem, EntityId ingredient);

        void onPerformAction(ActionType type);

    }

}
