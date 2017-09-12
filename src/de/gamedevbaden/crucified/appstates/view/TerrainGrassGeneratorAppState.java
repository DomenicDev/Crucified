package de.gamedevbaden.crucified.appstates.view;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.texture.image.ImageRaster;

import java.util.ArrayList;
import java.util.List;

/**
 * This app state will add grass to the areas of the terrain which a painted with a grass texture.
 * This app state wil divide the specified terrain into a grid with
 * the specified amount of cells. Grass will then only be visible in the cell the player is in
 * plus the cells around this cell.
 */
public class TerrainGrassGeneratorAppState extends AbstractAppState {

    private Spatial grassModel;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.grassModel = app.getAssetManager().loadModel("Models/Grass/SimpleGrass.j3o");
        super.initialize(stateManager, app);
    }

    /**
     * Will create one node which contains all the small grass batches for the specified terrain.
     * Note: You should batch the grass afterwards, otherwise there might be a huge FPS drop for larger scenes.
     * @param terrain the terrain you want generate grass for
     * @param grassTexturePos the position of the texture (0 = first texture, 1 = second texture, ...)
     * @return a node containing all grass batches.
     */
    public Node createGrassForTerrain(TerrainQuad terrain, int grassTexturePos) {
        if (terrain == null) {
            return null;
        }

        // the following node will contain all grass batches
        Node grassNode = new Node("GrassNode");
        grassNode.setQueueBucket(RenderQueue.Bucket.Transparent);

        // first we get the alpha map
        Material terrainMaterial = terrain.getMaterial();
        Texture alphaMap = terrainMaterial.getTextureParam("AlphaMap").getTextureValue();
        Texture alphaMap_1 = terrainMaterial.getTextureParam("AlphaMap_1").getTextureValue();
        Texture alphaMap_2 = terrainMaterial.getTextureParam("AlphaMap_2").getTextureValue();

        // we get all alpha maps (there are maximum 3 alpha maps)
        List<ImageRaster> rasterList = new ArrayList<>();
        if (alphaMap != null) {
            rasterList.add(ImageRaster.create(alphaMap.getImage()));
            if (alphaMap_1 != null) {
                rasterList.add(ImageRaster.create(alphaMap_1.getImage()));
                if (alphaMap_2 != null) {
                    rasterList.add(ImageRaster.create(alphaMap_2.getImage()));
                }
            }
        }

        ImageRaster[] rasters = rasterList.toArray(new ImageRaster[rasterList.size()]);
        Vector2f v = new Vector2f();

        for (int z = 0; z < rasters[0].getHeight(); z++) {
            for (int x = 0; x < rasters[0].getWidth(); x++) {
                if (isThereGrass(x, z, rasters, grassTexturePos)) {
                    // place grass
                    Spatial grass = grassModel.clone();
                    v.set(x, z);
                    grass.setLocalTranslation(turnIntoTranslation(v, terrain));
                    grass.setLocalRotation(grass.getLocalRotation().fromAngles(new float[]{0, (float) (Math.random() * 180 * FastMath.DEG_TO_RAD), 0}));
                    grassNode.attachChild(grass);
                }
            }
        }

        return grassNode;
    }

    private boolean isThereGrass(int x, int z, ImageRaster[] raster, int posGrassTexture) {
        float threshold = 0.7f; // the intensity of the grass texture which is needed to create grass
        float otherThreshold = 0.1f; // the maximum allowed intensity of other textures at a certain position
        // each alpha map can store information about 4 diffuse maps maximum
        // we now want to get the texture with the grass
        int mapNr = posGrassTexture / 4;
        int colorPos = posGrassTexture % 4; // so we know if it's red, blue, green, or alpha channel

        ImageRaster r = raster[mapNr];
        ColorRGBA c = r.getPixel(x, z);

        if (colorPos == 0) {
            // red
            if (c.getRed() >= threshold) {
                // we now check if there are other textures "overprinting" this texture
                if (c.getGreen() < otherThreshold && c.getBlue() < otherThreshold && c.getAlpha() < otherThreshold) {
                    // this texture is fine, we need to check the others as well
                   return isPixelVisible(mapNr, raster, otherThreshold, x, z);
                }
            }
        } else if (colorPos == 1) {
            // green
            if (c.getGreen() >= threshold) {
                if (c.getBlue() < otherThreshold && c.getAlpha() < otherThreshold) {
                    return isPixelVisible(mapNr, raster, otherThreshold, x, z);
                }
            }
        } else if (colorPos == 2) {
            // blue
            if (c.getBlue() >= threshold) {
                if (c.getAlpha() < threshold) {
                    return isPixelVisible(mapNr, raster, otherThreshold, x, z);
                }
            }
        } else if (colorPos == 3) {
            // alpha
            if (c.getAlpha() >= threshold) {
                return isPixelVisible(mapNr, raster, otherThreshold, x, z);
            }
        }
        return false;
    }

    private boolean isPixelVisible(int mapNr, ImageRaster[] raster, float otherThreshold, int x, int z) {
        // this method checks if the next textures overdraw the grass texture
        // if they don't, the grass texture is the last one and because of that visible
        if (mapNr+1 < raster.length) {
            for (int i = mapNr+1; i < raster.length; i++) {
                ImageRaster r2 = raster[i];
                ColorRGBA c2 = r2.getPixel(x, z);
                if (! (c2.getRed() < otherThreshold && c2.getGreen() < otherThreshold && c2.getBlue() < otherThreshold && c2.getAlpha() < otherThreshold)) {
                    // there is another texture overdrawing the desired (grass) texture, so the grass is not visible at this position
                    return false;
                }
            }
            // we went through all other maps and did not return
            // so there is no other texture overdrawing the grass texture, we can return true
            return true;
        } else {
            // there aren't any more textures, so we can return true
            return true;
        }
    }

    private Vector3f turnIntoTranslation(Vector2f pixelPos, TerrainQuad terrain) {
        float offset = terrain.getTerrainSize() / 2f;
        float x = pixelPos.x - offset;
        float z = (pixelPos.y - offset) * (-1);
        float y = terrain.getHeight(new Vector2f(x,z));
        return new Vector3f(x,y,z);
    }

}
