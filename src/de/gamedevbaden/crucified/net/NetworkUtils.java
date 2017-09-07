package de.gamedevbaden.crucified.net;


import com.jme3.network.serializing.Serializer;
import com.jme3.network.serializing.serializers.EnumSerializer;
import de.gamedevbaden.crucified.enums.*;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.net.messages.LoadLevelMessage;
import de.gamedevbaden.crucified.net.messages.ReadNoteMessage;
import de.gamedevbaden.crucified.net.messages.ReadyForGameStartMessage;

/**
 * This class provides util methods for networked game sessions.
 * Created by Domenic on 17.04.2017.
 */
public class NetworkUtils {

    /**
     * Serializes all Components
     */
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
        Serializer.registerClass(FlashLight.class);
        Serializer.registerClass(Type.class, new EnumSerializer());
        Serializer.registerClass(NeedToBeCrafted.class);
        Serializer.registerClass(FireState.class);
        Serializer.registerClass(ItemType.class, new EnumSerializer());
        Serializer.registerClass(ItemComponent.class);
        Serializer.registerClass(SkeletonType.class, new EnumSerializer());
        Serializer.registerClass(SkeletonComponent.class);
    }

    /**
     * Serializes all Messages
     */
    public static void initMessageSerializers() {
        Serializer.registerClass(Scene.class, new EnumSerializer());
        Serializer.registerClass(LoadLevelMessage.class);
        Serializer.registerClass(ReadyForGameStartMessage.class);
        Serializer.registerClass(PaperScript.class, new EnumSerializer());
        Serializer.registerClass(ReadNoteMessage.class);
    }

}
