package de.gamedevbaden.crucified.net;


import com.jme3.network.serializing.Serializer;
import com.jme3.network.serializing.serializers.EnumSerializer;
import de.gamedevbaden.crucified.enums.PaperScript;
import de.gamedevbaden.crucified.enums.Scene;
import de.gamedevbaden.crucified.enums.Type;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.net.messages.LoadLevelMessage;
import de.gamedevbaden.crucified.net.messages.ReadNoteMessage;
import de.gamedevbaden.crucified.net.messages.ReadyForGameStartMessage;

/**
 * Created by Domenic on 17.04.2017.
 */
public class NetworkUtils {

    /**
     * Only called by server
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
        Serializer.registerClass(ObjectType.class);
    }

    /**
     * Called by server and client
     */
    public static void initMessageSerializers() {
        Serializer.registerClass(Scene.class, new EnumSerializer());
        Serializer.registerClass(LoadLevelMessage.class);
        Serializer.registerClass(ReadyForGameStartMessage.class);
        Serializer.registerClass(PaperScript.class, new EnumSerializer());
        Serializer.registerClass(ReadNoteMessage.class);
    }

}
