package dev.cryptics.obscura.imgui;

import dev.cryptics.obscura.Obscura;
import imgui.ImGui;

public class ImGuiLayer {
    private int[] sliderValue = {0};
    public void imgui() {
        ImGui.begin("Cool Window");
        ImGui.text("Hello, Obscura!");
        ImGui.text("Window size: " + Obscura.getWindow().getWidth() + "x" + Obscura.getWindow().getHeight());

        ImGui.sliderInt("Texture", sliderValue, 0, 100);

        int fboWidth = 1280;
        int fboHeight = 720;
        ImGui.image(sliderValue[0], (float) fboWidth /2, (float) fboHeight /2, 0, 1, 1, 0);

        ImGui.end();
    }
}
