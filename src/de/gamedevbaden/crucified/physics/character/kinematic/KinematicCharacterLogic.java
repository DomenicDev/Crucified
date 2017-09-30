package de.gamedevbaden.crucified.physics.character.kinematic;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.math.Vector3f;
import com.jvpichowski.jme3.es.bullet.components.CustomShape;
import com.jvpichowski.jme3.es.bullet.components.Factor;
import com.jvpichowski.jme3.es.bullet.components.RigidBody;
import com.jvpichowski.jme3.es.logic.BaseSimpleEntityLogic;
import com.jvpichowski.jme3.es.logic.SimpleLogicManager;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * This is the logic part of the kinematic character
 */
public final class KinematicCharacterLogic extends BaseSimpleEntityLogic {
//TODO remove updateLogic()
//TODO add getEntityData()
//TODO CharacterLogic ray length

    public static final Logger LOGGER = Logger.getLogger(KinematicCharacterLogic.class.getName());

    private EntityData entityData;
    private SimpleLogicManager logicManager;
    private WarpLogic warpLogic;
    private MoveLogic moveLogic;
    private BackPropagateLogic backPropagateLogic;

    /**
     * This map stores the player entity id as key and the associated physics entity id as value.
     * We have to do this in case the player entity gets fully removed, that means that
     * the PhysicsCharacterObject component also gets removed. so we would loose our "connection" component.
     */
    private HashMap<EntityId, EntityId> entityToPhysicsObjectMap = new HashMap<>();

    public void initLogic(SimpleLogicManager logicManager, EntityData entityData){
        this.entityData = entityData;
        this.logicManager = logicManager;
        this.warpLogic = new WarpLogic();
        this.moveLogic = new MoveLogic();
        this.backPropagateLogic = new BackPropagateLogic();
        warpLogic.initLogic(entityData);
        moveLogic.initLogic(entityData);
        backPropagateLogic.initLogic(entityData);
        logicManager.attach(warpLogic);
        logicManager.attach(moveLogic);
        logicManager.attach(backPropagateLogic);
        //TODO composed logic dependOn ?
        LOGGER.info("INIT finished");
    }

    public void destroyLogic(){
        logicManager.detach(warpLogic);
        logicManager.detach(moveLogic);
        logicManager.detach(backPropagateLogic);
        LOGGER.info("DESTROY finished");
    }

    @Override
    public void registerComponents() {
        dependsOn(KinematicCharacter.class);
    }

    /**
     * This method is called if a new entity is created which fits to the needed components
     */
    @Override
    public void init() {
        super.init();
        EntityId physicsObject = entityData.createEntity();
        KinematicCharacter character = get(KinematicCharacter.class);
        entityData.setComponents(physicsObject,
                new ESCharacter(getId()),
                new RigidBody(false, character.getMass()),
                new CustomShape(new CapsuleCollisionShape(character.getRadius(), character.getHeight()-character.getStepHeight())),
                new Factor(new Vector3f(1,0,1), new Vector3f(0,0,0)));
        set(new PhysicsCharacterObject(physicsObject));

        entityToPhysicsObjectMap.put(getId(), physicsObject);

        LOGGER.info("Character created: "+getId());
    }
    /**
     * This method is called if an entity with the needed components
     * is destroyed or if a needed component is removed from it.
     */
    @Override
    public void destroy() {
        PhysicsCharacterObject physicsObject = get(PhysicsCharacterObject.class);
        EntityId physicsObjectId;

        if (physicsObject != null) {
            physicsObjectId = physicsObject.getObject();
        } else {
            physicsObjectId = entityToPhysicsObjectMap.remove(getId());
        }

        entityData.removeEntity(physicsObjectId);
        entityData.removeComponent(getId(), PhysicsCharacterObject.class);
        LOGGER.info("Character removed: "+getId());
        super.destroy();
    }

    /**
     * This method is called every time the logic should be executed.
     */
    @Override
    public void run() {
        super.run();
    }
}
