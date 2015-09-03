package com.dmi.typoweb;

import com.dmi.util.opengl.GLRenderer;

public class TypoWebRenderer implements GLRenderer {
    private final TypoWeb typoWeb;
    private RenderContext renderContext;

    public TypoWebRenderer(TypoWeb typoWeb) {
        this.typoWeb = typoWeb;
    }

    @Override
    public void onSurfaceCreated() {
        renderContext = new RenderContext();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
    }

    @Override
    public void onFreeResources() {
        renderContext.destroy();
        renderContext = null;
    }

    @Override
    public void onDrawFrame() {
        typoWeb.draw(renderContext);
    }
}
