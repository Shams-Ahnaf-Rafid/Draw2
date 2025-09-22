precision mediump float;

uniform sampler2D u_Foreground;
uniform sampler2D u_Mask;

varying vec2 v_TexCoord;

void main() {
    vec4 fg = texture2D(u_Foreground, v_TexCoord);
    float mask = texture2D(u_Mask, v_TexCoord).r;
    gl_FragColor = vec4(fg.rgb, mask);
}
