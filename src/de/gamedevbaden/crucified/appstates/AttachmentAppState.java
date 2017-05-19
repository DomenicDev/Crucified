package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.*;
import de.gamedevbaden.crucified.es.components.ChildOf;
import de.gamedevbaden.crucified.es.components.Transform;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Domenic on 12.05.2017.
 */
public class AttachmentAppState extends AbstractAppState {

    private EntitySet attachedEntities;
    private EntityData entityData;

    private HashMap<EntityId, ArrayList<EntityId>> parentChildMap;
    private ArrayList<WatchedEntity> parents; // to look for changes

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.entityData = stateManager.getState(EntityDataState.class).getEntityData();
        this.attachedEntities = entityData.getEntities(ChildOf.class, Transform.class);

        this.parentChildMap = new HashMap<>();
        this.parents = new ArrayList<>();

        super.initialize(stateManager, app);
    }

    @Override
    public void update(float tpf) {

        if (attachedEntities.applyChanges()) {

            for (Entity entity : attachedEntities.getAddedEntities()) {

                ChildOf childOf = entity.get(ChildOf.class);

                if (!parentChildMap.containsKey(childOf.getParentId())) {
                    parentChildMap.put(childOf.getParentId(), new ArrayList<>());

                    WatchedEntity watchedEntity = entityData.watchEntity(childOf.getParentId(), Transform.class);
                    parents.add(watchedEntity);
                }
                parentChildMap.get(childOf.getParentId()).add(entity.getId());
            }

            for (Entity entity : attachedEntities.getRemovedEntities()) {
                ChildOf childOf = entity.get(ChildOf.class);
                ArrayList<EntityId> children = parentChildMap.get(childOf.getParentId());
                children.remove(entity.getId());

                if (children.isEmpty()) {
                    WatchedEntity parentEntity = null;
                    for (WatchedEntity parent : parents) {
                        if (parent.getId() == childOf.getParentId()) {
                            parent.release();
                            parentEntity = parent;
                            break;
                        }
                    }
                    if (parentEntity != null) {
                        parents.remove(parentEntity);
                    }
                }
            }
        }

        for (WatchedEntity parent : parents) {
            if (parent.applyChanges()) {

                for (EntityId child : parentChildMap.get(parent.getId())) {
                    Entity childEntity = attachedEntities.getEntity(child);
                    Transform parentTransform = parent.get(Transform.class);
                    ChildOf childOf = childEntity.get(ChildOf.class);
                    Transform newChildTransform = combineWithParent(parentTransform, childOf);
                    childEntity.set(newChildTransform);
                }
            }
        }
    }

    private Transform combineWithParent(Transform parentTransform, ChildOf childChildOf) {
        Vector3f translation = childChildOf.getOffsetTranslation().clone();
        Quaternion rotation = new Quaternion();

        parentTransform.getRotation().mult(childChildOf.getOffsetRotation(), rotation);
        translation.multLocal(parentTransform.getScale());
        parentTransform.getRotation().multLocal(translation).addLocal(parentTransform.getTranslation());

        return new Transform(translation, rotation, parentTransform.getScale());
    }

    @Override
    public void cleanup() {
        this.attachedEntities.release();
        this.attachedEntities.clear();
        this.attachedEntities = null;

        for (WatchedEntity entity : parents) {
            entity.release();
        }
        this.parents.clear();
        this.parents = null;

        this.parentChildMap.clear();
        this.parentChildMap = null;

        super.cleanup();
    }
}
