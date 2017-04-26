package de.gamedevbaden.crucified.net;


import com.jme3.network.serializing.Serializer;
import com.jme3.network.serializing.serializers.FieldSerializer;
import de.gamedevbaden.crucified.es.components.Model;
import de.gamedevbaden.crucified.es.components.OnMovement;
import de.gamedevbaden.crucified.es.components.PhysicsRigidBody;
import de.gamedevbaden.crucified.es.components.Transform;

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
//        Serializer.registerClass(PhysicsCharacterControl.class, new FieldSerializer());
//        Serializer.registerClass(CollisionShapeType.class);
//        Serializer.registerClass(BoxCollisionShape.class, new FieldSerializer());
//        Serializer.registerClass(SphereCollisionShape.class, new FieldSerializer());
//        Serializer.registerClass(CustomMeshCollisionShape.class, new FieldSerializer());
    }

}
