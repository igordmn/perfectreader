#version 101

precision mediump float;

uniform mat4 u_MVPMatrix;
attribute vec4 coord;
varying vec2 v_TexCoordinate;

void main() {
    gl_Position = u_MVPMatrix * vec4(coord.xy, 0, 1);
    v_TexCoordinate = coord.zw;
}