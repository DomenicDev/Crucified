package de.gamedevbaden.crucified.net;


import com.jme3.network.serializing.Serializer;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.net.messages.LoadLevelMessage;

/**
 * Created by Domenic on 17.04.2017.
 */
public class NetworkUtils {

    public static void initEntityDataSerializers() {
        Serializer.registerClass(Transform.class);
        Serializer.registerClass(Model.class);
        Serializer.registerClass(OnMovement.class);
        Serializer.registerClass(PhysicsRigidBody.class);
        Serializer.registerClass(PhysicsCharacterControl.class);
        Serializer.registerClass(CharacterMovementState.class);
        Serializer.registerClass(PlayerControlled.class);
        Serializer.registerClass(SoundComponent.class);
        Serializer.registerClass(InteractionComponent.class);
        Serializer.registerClass(Pickable.class);
        Serializer.registerClass(Container.class);
        Serializer.registerClass(StoredIn.class);
        Serializer.registerClass(Equipable.class);
        Serializer.registerClass(EquippedBy.class);
        Serializer.registerClass(PhysicsTerrain.class);
    }

    public static void initMessageSerializers() {
        Serializer.registerClass(LoadLevelMessage.class);
    }

}
