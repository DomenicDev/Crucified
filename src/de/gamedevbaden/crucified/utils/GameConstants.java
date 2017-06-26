package de.gamedevbaden.crucified.utils;

import com.jme3.math.Vector3f;

/**
 * Created by Domenic on 12.05.2017.
 */
public class GameConstants {

    // ---------- INFO ABOUT THE GAME ITSELF ---------//

    public static final String GAME_NAME = "CRUCIFIED";
    public static final float VERSION = 0.1f;

    //------------ USER DATA NAMES ---------------------//

    public static final String USER_DATA_ENTITY_ID = "entityId";
    public static final String USER_DATA_FOOTSTEP_SOUND = "footstep";
    public static final String USER_DATA_READABLE_SCRIPT = "script";

    public static final Vector3f FIRST_PERSON_CAM_OFFSET = new Vector3f(0.0f, 0.1f, 0.15f); // 0.2f, 1.8f, -1.0f
    public static final Vector3f THIRD_PERSON_CAM_OFFSET = new Vector3f(0.0f, 1.7f, -1.1f);

}
