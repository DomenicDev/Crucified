package de.gamedevbaden.crucified.net;


import com.jme3.network.serializing.Serializer;
import com.jme3.network.serializing.serializers.FieldSerializer;
import de.gamedevbaden.crucified.es.components.*;

/**
 * Created by Domenic on 17.04.2017.
 */
public class NetworkUtils {


    public static void initSerializers() {

//        Serializer.registerClass(FixedTransformation.class, new FieldSerializer());
        Serializer.registerClass(Transform.class, new FieldSerializer());
        Serializer.registerClass(Model.class, new FieldSerializer());
//        Serializer.registerClass(Name.class, new FieldSerializer());
        Serializer.registerClass(OnMovement.class, new FieldSerializer());
        Serializer.registerClass(PhysicsRigidBody.class, new FieldSerializer());
        Serializer.registerClass(PhysicsCharacterControl.class, new FieldSerializer());
        Serializer.registerClass(CharacterMovementState.class, new FieldSerializer());
        Serializer.registerClass(PlayerControlled.class);
    }

}
