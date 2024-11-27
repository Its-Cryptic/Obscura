package dev.cryptics.obscura.model;

/**
 * An {@link IndexedModel} that is parsed from a Wavefront OBJ file.
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