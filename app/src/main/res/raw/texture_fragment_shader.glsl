//precision mediump float;
//
//uniform sampler2D u_Background;
//uniform sampler2D u_Texture;
//
//varying vec2 v_TexCoord;
//
//void main() {
//    vec4 bg = texture2D(u_Background, v_TexCoord);
//    vec4 fg = texture2D(u_Texture, v_TexCoord);
//
//    gl_FragColor = fg.a * fg + (1.0 - fg.a) * bg;
//}

precision mediump float;

uniform sampler2D u_Texture;
varying vec2 v_TexCoord;

void main() {
    gl_FragColor = texture2D(u_Texture, v_TexCoord);
}
