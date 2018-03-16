package de.gamedevbaden.crucified.utils;

import com.jme3.math.Vector3f;

/**
 * Created by Domenic on 12.05.2017.
 */
public class GameConstants {

    // ---------- INFO ABOUT THE GAME ITSELF ---------//

    public static final String GAME_NAME = "CRUCIFIED";
    public static final float VERSION = 0.14f;

    // ------------- MULTIPLAYER --------------------- //

    public static final int DEFAULT_PORT = 5555;

    //------------ USER DATA NAMES ---------------------//

    public static final String USER_DATA_ENTITY_TYPE = "type";
    public static final String USER_DATA_ENTITY_ID = "entityId";
    public static final String USER_DATA_FOOTSTEP_SOUND = "footstep";
    public static final String USER_DATA_READABLE_SCRIPT = "script";
    public static final String USER_DATA_COOP_TAKS = "coopTask";
    public static final String USER_DATA_STATIC_PHYSICAL_OBJECT = "staticPhysicalObject";
    public static final String USER_DATA_PAGING_OPTIONS = "pagingOptions";
    public static final String USER_DATA_GRASS_TEXTURE_INDEX = "grassTextureIndex";
    public static final String USER_DATA_IGNORE_RAY_CAST = "ignoreRayCast";

    // ----------------- OTHER CONSTANTS ---------------- //

    public static final String CUSTOM_COLLISION_SHAPE_NAME = "collision";

    public static final Vector3f FIRST_PERSON_CAM_OFFSET = new Vector3f(0.0f, 0.1f, 0.20f); // 0.2f, 1.8f, -1.0f
    public static final Vector3f THIRD_PERSON_CAM_OFFSET = new Vector3f(0.0f, 2.3f, -2f);

}
