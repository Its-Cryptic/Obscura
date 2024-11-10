package dev.cryptic.obscura.model;

import dev.cryptic.obscura.core.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public abstract class ObscuraParser<M extends IndexedModel> {
    public void startParse(M model) {
        ResourceLocation resource = model.getAssetLocation();
        try {
            InputStream inputStream = resource.open();
            parse(inputStream, model);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Model file: " + model.getModelId(), e);
        }
    }

    public abstract void parse(InputStream inputStream, M model) throws IOException;
}
