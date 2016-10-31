precision mediump float;

attribute vec4 coordinate;
varying vec2 textureCoordinate;

void main() {
    gl_Position = vec4(coordinate.xy, -1, 1);
    textureCoordinate = coordinate.zw;
}