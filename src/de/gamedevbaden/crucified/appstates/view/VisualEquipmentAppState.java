package de.gamedevbaden.crucified.appstates.view;

import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.filter.FieldFilter;
import de.gamedevbaden.crucified.appstates.EntityDataState;
import de.gamedevbaden.crucified.enums.ModelType;
import de.gamedevbaden.crucified.enums.SkeletonType;
import de.gamedevbaden.crucified.es.components.EquippedBy;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.es.components.SkeletonComponent;

/**
 * This app state watches equipped items and make them be positioned in their right position.
 * This state "overrides" the entity's position and rotation in the game world.
 * Created by Domenic on 20.05.2017.
 */
public class VisualEquipmentAppState extends AbstractAppState {

    private EntitySet equipables;
    private EntitySet players;

    private ModelViewAppState modelViewAppState;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        EntityData entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.equipables = entityData.getEntities(EquippedBy.class, Model.class);
        this.players = entityData.getEntities(new FieldFilter<>(SkeletonComponent.class, "skeletonType", SkeletonType.HUMAN), Model.class);
        this.modelViewAppState = stateManager.getState(ModelViewAppState.class);
        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {
        this.players.applyChanges();

        if (equipables.applyChanges()) {

            for (Entity entity : equipables.getAddedEntities()) {
                addEquipable(entity);
            }

            for (Entity entity : equipables.getRemovedEntities()) {
                removeEquipable(entity);
            }

        }
    }

    private void addEquipable(Entity entity) {
        // look what this entity is equipped by
        EquippedBy equippedBy = entity.get(EquippedBy.class);
        EntityId equipper = equippedBy.getPlayer();
        EntityId itemToEquip = entity.getId();

        // check if it has been equipped by a player
        if (players.containsId(equipper)) {
            equipToPlayer(equipper, itemToEquip);
        }
        // add more ... (if necessary)


        // exclude this entity from normal position updates by the ModelViewAppState
        // we set the position for our equipped entity here now
        // when this item is not equipped anymore we set it back to false
        modelViewAppState.setExcludedFromUpdate(itemToEquip, true);
    }

    private void equipToPlayer(EntityId playerId, EntityId itemToEquip) {
        Spatial playerModel = modelViewAppState.getSpatial(playerId);
        Spatial itemModel = modelViewAppState.getSpatial(itemToEquip);

        if (playerModel != null && itemModel != null) {

            Model modelType = equipables.getEntity(itemToEquip).get(Model.class);

            if (modelType.getPath().equals(ModelType.Headlamp)) {

                // attach to head
                Node attachmentNode = playerModel.getControl(SkeletonControl.class).getAttachmentsNode("Head");
                Node itemNode = new Node("ItemNode");
                attachmentNode.attachChild(itemNode);
                itemNode.setLocalTranslation(-0.011107027f, 0.09165355f, 0.12990493f);

                //   attachmentNode.attachChild(itemModel);
                itemNode.attachChild(itemModel);
                itemModel.setLocalTranslation(0, 0, 0);
                itemModel.setLocalRotation(Quaternion.IDENTITY);
                itemModel.setCullHint(Spatial.CullHint.Inherit);

            } else {
                // later we could check where exactly to attach that item
                // now we want to attach it to the hand bone
                Node attachmentNode = playerModel.getControl(SkeletonControl.class).getAttachmentsNode("RightHand");
                attachmentNode.attachChild(itemModel);
                itemModel.setLocalTranslation(0, 0, 0);
                itemModel.setCullHint(Spatial.CullHint.Inherit);
            }
        }
    }

    private void removeEquipable(Entity entity) {
        Spatial itemModel = modelViewAppState.getSpatial(entity.getId());
        modelViewAppState.attachSpatial(itemModel);
        modelViewAppState.setExcludedFromUpdate(entity.getId(), false);
    }

    @Override
    public void cleanup() {
        this.equipables.release();
        this.equipables.clear();
        this.equipables = null;

        this.players.release();
        this.players.clear();
        this.players = null;
        super.cleanup();
    }
}
