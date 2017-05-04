package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * Mouse input to control the cam is captured here.
 */
public class CameraAppState extends AbstractAppState implements AnalogListener, ActionListener {

    private final Matrix3f mat = new Matrix3f();
    private final String[] mappings = new String[]{
            "CAM_Left",
            "CAM_Right",
            "CAM_Up",
            "CAM_Down",
            "MouseRightZoom"
        };
    private final Quaternion camRotation = new Quaternion();
    ;
    private final int zommSpeed = 25;
    private final int maxFieldOfView = 45;
    private final int minFieldOfView = 30;
    private Application app;
    private Camera cam;
    private float rotationSpeed = 1f;
    private InputManager inputManager;
    private float fieldOfView = 45;
    private boolean zoomIn = false;
    // values for how much the camera can look "down" or "up"
    /**
     * 85 degrees is actually up to the sky (enough).
     */
    private int maxUp = 85;
    /**
     * Remember, -90 (degrees) is down to the bottom
     */
    private int maxDown = -90;


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = app;
        super.initialize(stateManager, app);
        this.inputManager = app.getInputManager();
        this.cam = app.getCamera();
        registerWithInput();
        resetCamera();
    }

    @Override
    public void cleanup() {
        resetCamera();
        for (String mapping : mappings) {
            inputManager.deleteMapping(mapping);
        }
        super.cleanup();
    }

    /*
     * Resets the position, looking direction etc. to the standard values
     * Important when the game restarts
     */
    public void resetCamera() {
        cam.setFrustumPerspective(45f, (float) app.getContext().getSettings().getWidth() / app.getContext().getSettings().getHeight(), 0.05f, 200);

        Vector3f left = new Vector3f(-1, 0, 0);
        Vector3f up = new Vector3f(0, 1, 0);
        Vector3f direction = new Vector3f(-1, 0, 0);
        cam.setAxes(left, up, direction);
   //     cam.setFrustumPerspective(45f, (float) settings.getWidth() / settings.getHeight(), 0.01f, 500);
    }

    private void registerWithInput() {        
        // mouse trigger for camera rotation
        inputManager.addMapping(mappings[0], new MouseAxisTrigger(0, true));
        inputManager.addMapping(mappings[1], new MouseAxisTrigger(0, false));
        inputManager.addMapping(mappings[2], new MouseAxisTrigger(1, false));
        inputManager.addMapping(mappings[3], new MouseAxisTrigger(1, true));
        
        // right mouse button for zoom
        inputManager.addMapping(mappings[4], new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        
        inputManager.addListener(this, mappings);
        inputManager.setCursorVisible(false);
    }

    private void rotateCamera(float value, Vector3f axis) {       
        mat.fromAngleNormalAxis(rotationSpeed * value, axis);

        Vector3f up = cam.getUp();
        Vector3f left = cam.getLeft();
        Vector3f dir = cam.getDirection();

        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);
        
        camRotation.fromAxes(left, up, dir);
        camRotation.normalizeLocal();

        float[] angles = new float[3];
        camRotation.toAngles(angles);

        if (angles[0] > getMaxDown() * FastMath.DEG_TO_RAD && angles[0] < getMaxUp() * FastMath.DEG_TO_RAD) {
            cam.setAxes(camRotation);
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (isEnabled()) {            
            switch (name) {
                case "CAM_Left":
                    rotateCamera(value, Vector3f.UNIT_Y);
                    break;
                case "CAM_Right":
                    rotateCamera(-value, Vector3f.UNIT_Y);
                    break;
                case "CAM_Up":
                    rotateCamera(-value, cam.getLeft());
                    break;
                case "CAM_Down":
                    rotateCamera(value, cam.getLeft());
                    break;
                default: break;
            }
        }
    }    

    @Override
    public void update(float tpf) {
//        if (!isEnabled()) {
//            return;
//        }
        // zoom function interferes with point light; TODO: FIX THAT!!!
        //zoom(tpf);
       
    }
    
    private void zoom(float tpf) {
        if (zoomIn && fieldOfView > minFieldOfView) {
            fieldOfView -= tpf * zommSpeed;
            cam.setFrustumPerspective(fieldOfView, (float) cam.getWidth() / cam.getHeight(), 0.01f, 500);
        } else if (!zoomIn && fieldOfView < maxFieldOfView) {
            fieldOfView += tpf * zommSpeed;
            cam.setFrustumPerspective(fieldOfView, (float) cam.getWidth() / cam.getHeight(), 0.01f, 500);
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isEnabled()) {
            if (name.equals("MouseRightZoom")) {
                zoomIn = isPressed; 
            }            
        }
    }
    
    public Camera getCamera() {
        return cam;
    }
    
    /**
     * @return the maxUp
     */
    public int getMaxUp() {
        return maxUp;
    }

    /**
     * @param maxUp the maxUp to set
     */
    public void setMaxUp(int maxUp) {
        this.maxUp = maxUp;
    }

    /**
     * @return the maxDown
     */
    public int getMaxDown() {
        return maxDown;
    }

    /**
     * @param maxDown the maxDown to set
     */
    public void setMaxDown(int maxDown) {
        this.maxDown = maxDown;
    }
    
    /**
     * Sets the roation speed of the camera
     * @param rotationSpeed 
     */
    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }
}