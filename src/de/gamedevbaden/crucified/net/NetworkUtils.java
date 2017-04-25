package de.gamedevbaden.crucified.net;


import com.jme3.network.serializing.Serializer;
import com.jme3.network.serializing.serializers.FieldSerializer;
import de.gamedevbaden.crucified.es.components.*;
import de.gamedevbaden.crucified.es.utils.physics.BoxCollisionShape;
import de.gamedevbaden.crucified.es.utils.physics.CollisionShapeType;
import de.gamedevbaden.crucified.es.utils.physics.CustomMeshCollisionShape;
import de.gamedevbaden.crucified.es.utils.physics.SphereCollisionShape;

/**
 * Created by Domenic on 17.04.2017.
 */
public class NetworkUtils {


    public static void initSerializers() {
        Serializer.registerClass(FixedTransformation.class, new FieldSerializer());
        Serializer.registerClass(DynamicTransform.class, new FieldSerializer());
        Serializer.registerClass(Model.class, new FieldSerializer());
        Serializer.registerClass(Name.class, new FieldSerializer());
        Serializer.registerClass(PhysicsRigidBody.class, new FieldSerializer());
        Serializer.registerClass(PhysicsCharacterControl.class, new FieldSerializer());
        Serializer.registerClass(CollisionShapeType.class);
        Serializer.registerClass(BoxCollisionShape.class, new FieldSerializer());
        Serializer.registerClass(SphereCollisionShape.class, new FieldSerializer());
        Serializer.registerClass(CustomMeshCollisionShape.class, new FieldSerializer());
    }

}
