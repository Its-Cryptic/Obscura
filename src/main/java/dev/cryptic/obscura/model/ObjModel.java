package dev.cryptic.obscura.model;

import dev.cryptic.obscura.core.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * An {@link IndexedModel} that is parsed from an Wavefront OBJ file.
 */
public class ObjModel extends IndexedModel {
    public ObjModel(String modelId) {
        super(modelId);
    }

    @Override
    public void loadModel() {
        ObjParser parser = new ObjParser();
        parser.startParse(this);
        //this.applyModifiers();
    }
}