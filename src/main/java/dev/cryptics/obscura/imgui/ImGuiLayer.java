package dev.cryptics.obscura.imgui;

import dev.cryptics.obscura.Obscura;
import imgui.ImGui;

public class ImGuiLayer {
    private boolean showText = false;

    public void imgui() {
        ImGui.begin("Cool Window");
        ImGui.text("Hello, Obscura!");
        ImGui.text("Window size: " + Obscura.getWindow().getWidth() + "x" + Obscura.getWindow().getHeight());

        if (ImGui.button("I am a button")) {
            showText = true;
        }

        if (showText) {
            ImGui.text("You clicked a button");
            ImGui.sameLine();
            if (ImGui.button("Stop showing text")) {
                showText = false;
            }
        }

        //ImGui.image(0, 1280, 720);

        ImGui.end();
    }
}
