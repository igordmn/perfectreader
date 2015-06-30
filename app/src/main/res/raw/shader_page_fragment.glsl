#extension GL_OES_EGL_image_external : require

precision mediump float;

varying vec2 textureCoordinate;
uniform sampler2D texture;

void main() {
    gl_FragColor = texture2D(texture, textureCoordinate);
}