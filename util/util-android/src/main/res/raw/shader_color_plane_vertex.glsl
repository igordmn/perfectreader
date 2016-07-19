precision mediump float;

uniform mat4 mvpMatrix;
attribute vec2 coordinate;

void main() {
    gl_Position = mvpMatrix * vec4(coordinate.xy, 0, 1);
}