package de.gamedevbaden.crucified.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import de.gamedevbaden.crucified.enums.Models;
import de.gamedevbaden.crucified.userdata.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by the scene composer when designing a scene.
 * It looks for nodes with a special user data and generates the right model.
 *
 *
 * Created by Domenic on 25.04.2017.
 */
public class LevelDesignerAppState extends AbstractAppState {

    private float refreshTimer;
    private float refreshIntervall = 1f; // 1 sec
    private boolean refresh;

    private Node rootNode;
    private AssetManager assetManager;

    private List<Node> entities = new ArrayList<>();


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.rootNode = (Node) ((SimpleApplication) app).getRootNode().getChild(0);
        this.assetManager = app.getAssetManager();
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public void setRefreshIntervall(float refreshIntervall) {
        this.refreshIntervall = refreshIntervall;
    }

    public float getRefreshIntervall() {
        return refreshIntervall;
    }

    private void refresh() {
        rootNode.depthFirstTraversal(new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
                if (spatial instanceof Node) {
                    EntityType t = spatial.getUserData("type");
                    if (t != null) {
                        if (!((Node)spatial).getChildren().isEmpty()) {
                            return;
                        }
                        if (t.getModel() != null) {
                            Models model = t.getModel();
                            ((Node)spatial).attachChild(assetManager.loadModel(model.getModelPath()));
                        }
                    }
                }

            }
        });
    }

    @Override
    public void update(float tpf) {

        if (refresh) {

            if ((refreshTimer+=tpf) >= refreshIntervall) {
                refreshTimer = 0;
                refresh();
            }

        }

    }

    @Override
    public void cleanup() {
        super.cleanup();
        for (Node n : entities) {
            n.detachAllChildren();
        }
        entities.clear();
    }

}
