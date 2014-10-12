#version 101

precision mediump float;

varying vec2 v_TexCoordinate;
uniform sampler2D tex;
uniform vec4 color;

void main() {
    gl_FragColor = vec4(1, 1, 1, texture2D(tex, v_TexCoordinate).a) * color;
}