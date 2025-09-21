precision mediump float;

uniform sampler2D u_Foreground; // foreground texture
uniform sampler2D u_Mask;       // FBO mask texture

varying vec2 v_TexCoord;

void main() {
    vec4 fg = texture2D(u_Foreground, v_TexCoord);
    float mask = texture2D(u_Mask, v_TexCoord).r; // mask is grayscale
    gl_FragColor = vec4(fg.rgb, fg.a * mask);     // multiply alpha by mask
}
