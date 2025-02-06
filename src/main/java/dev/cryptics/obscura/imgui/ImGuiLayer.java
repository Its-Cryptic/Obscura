package dev.cryptics.obscura.imgui;

import com.example.mygame.MyGame;
import com.example.mygame.ParticleSimRenderer;
import dev.cryptics.obscura.Obscura;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;

public class ImGuiLayer {
    private int[] sliderValue = {1};
    public float[] sliderValue2 = {1};
    public float[] sliderValue3 = {920};
    public ImBoolean checkbox = new ImBoolean(false);
    public float[] dt = {0.001f};
    public ImBoolean checkbox2 = new ImBoolean(false);
    public void imgui() {
//        ImGui.begin("Cool Window");
//        ImGui.text("Hello, Obscura!");
//        ImGui.text("Window size: " + Obscura.getWindow().getWidth() + "x" + Obscura.getWindow().getHeight());
//
//        ImGui.sliderInt("Texture", sliderValue, 1, 20);
//        ImGui.sliderFloat("Scale", sliderValue2, 0, 5);
//        ImGui.sliderFloat("Image Scale", sliderValue3, 0, 2000);
//
//        int fboWidth = Obscura.getWindow().getWidth();
//        int fboHeight = Obscura.getWindow().getHeight();
//        float aspectRatio = (float) fboWidth / fboHeight;
//        boolean isLandscape = aspectRatio > 1;
//
//        float imageWidth = isLandscape ? sliderValue3[0] : sliderValue3[0] * aspectRatio;
//        float imageHeight = isLandscape ? sliderValue3[0] / aspectRatio : sliderValue3[0];
//
//
//        //ImGui.image(sliderValue[0], (float) imageWidth, (float) imageHeight, 0, 1, 1, 0);
//
//        ImGui.end();
//        ImGui.showDemoWindow();

        ImGui.begin("GPU Particles");
        ImGui.text("Particle Count: " + ( (ParticleSimRenderer) Obscura.getContext().getRenderer()).particleCount);
        ImGui.checkbox("Play", checkbox);
        ImGui.sliderFloat("dt", dt, 0.001f, 0.1f);
        ImGui.checkbox("Invert", checkbox2);
        if (ImGui.button("Reset")) {
            //((ParticleSimRenderer) Obscura.getContext().getRenderer()).particleCount = 1000000;
        }
        ImGui.end();

    }
}
