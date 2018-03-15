package de.gamedevbaden.crucified.controls;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import de.gamedevbaden.crucified.enums.FootstepSound;
import de.gamedevbaden.crucified.es.components.CharacterMovementState;
import de.gamedevbaden.crucified.userdata.FootstepSoundUserData;
import de.gamedevbaden.crucified.utils.GameConstants;

/**
 * A control which plays footstep sounds when object is walking.
 * Call <code>setMovementState()</code> to tell the control that
 * the player is moving.
 * <p>
 * Created by Domenic on 06.06.2017.
 */
public class FootstepSoundControl extends AbstractControl {

    private AssetManager assetManager;
    private Node gameWorld;
    private AudioNode currentFootstepSound;
    private FootstepSound lastSound;

    private Ray ray;
    private CollisionResults results;

    private int movementState;

    public FootstepSoundControl(Node gameWorld, AssetManager assetManager) {
        this.gameWorld = gameWorld;
        this.assetManager = assetManager;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            // init
            this.ray = new Ray();
            this.ray.setDirection(Vector3f.UNIT_Y.negate());
            this.ray.setLimit(0.1f);
            this.results = new CollisionResults();
        } else {
            // cleanup
            this.results.clear();
            this.results = null;
            this.ray = null;
            if (currentFootstepSound != null) {
                currentFootstepSound.stop();
                currentFootstepSound.removeFromParent();
            }
        }
    }

    /**
     * Tells the control about the movement state of the spatial.
     * The control will then play or stop footstep sounds dependently.
     *
     * @param movementState the movement state of the spatial
     */
    public void setMovementState(int movementState) {
        this.movementState = movementState;
    }

    @Override
    protected void controlUpdate(float tpf) {
        // if player walks play footstep sound
        if (movementState != CharacterMovementState.IDLE) {

            // clear old collision results
            this.results.clear();

            // refresh ray object
            this.ray.setOrigin(spatial.getWorldTranslation());

            // let ray collide with game world
            gameWorld.collideWith(ray, results);

            // if we hit something we can get the specific sound
            // from the added user data object

            // here we store the sound we found from our collision result
            FootstepSound sound = null;

            // here we reference to the footstep user data
            FootstepSoundUserData userData;

            // maybe it could be that we hit our own feet
            // that's why we give the chance to check all results here
            for (CollisionResult result : results) {

                userData = result.getGeometry().getUserData(GameConstants.USER_DATA_FOOTSTEP_SOUND);

                // if the hit geometry was terrain we just play a default sound (for now)
                if (result.getGeometry().getParent() instanceof TerrainQuad) {
                    sound = FootstepSound.Sand; // default sound
                }

                // if the geometry has valid footstep data
                // we set our sound
                if (userData != null) {
                    if (userData.getFootstepSound() != null) {
                        sound = userData.getFootstepSound();
                        break;
                    }
                }
            }

            // see whether sound is set
            if (sound == null) {
                return;
            }

            // if the same sound was found and doesn't play at the moment
            // play it again
            if (lastSound == sound) {
                if (currentFootstepSound != null && currentFootstepSound.getStatus() != AudioSource.Status.Playing) {
                    currentFootstepSound.play();
                }
            } else {
                if (currentFootstepSound != null) {
                    // stop current played sound and detach it
                    currentFootstepSound.stop();
                    currentFootstepSound.removeFromParent();
                }

                // create new sound
                currentFootstepSound = new AudioNode(assetManager, sound.getSoundPath(), AudioData.DataType.Buffer);
                currentFootstepSound.setLooping(false);
                currentFootstepSound.setPositional(true);
                currentFootstepSound.setLocalTranslation(0, 0, 0);
                currentFootstepSound.setRefDistance(3f);
                currentFootstepSound.setMaxDistance(1000);
                ((Node) spatial).attachChild(currentFootstepSound);
                currentFootstepSound.play();

                lastSound = sound;
            }
        } else {
            // player does not move, so stop (here: pause) sound
            if (currentFootstepSound != null && currentFootstepSound.getStatus() == AudioSource.Status.Playing) {
                currentFootstepSound.pause();
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
