precision mediump float;

uniform mat4 mvpMatrix;
attribute vec4 coordinate;
varying vec2 textureCoordinate;

void main() {
    gl_Position = mvpMatrix * vec4(coordinate.xy, 0, 1);
    textureCoordinate = coordinate.zw;
}