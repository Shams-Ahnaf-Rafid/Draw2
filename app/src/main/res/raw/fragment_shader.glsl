precision mediump float;

uniform vec4 u_Color;
uniform float u_Thickness;
uniform vec2 u_Points[2];
uniform vec2 u_resolution;
uniform sampler2D u_Mask;
uniform sampler2D u_Forest;
uniform sampler2D u_Texture;
uniform bool u_Display;

varying vec2 v_TexCoord;

void main() {

    if (u_Display) {
        float a = texture2D(u_Mask, v_TexCoord).r;
        if (a == 1.0) {
            gl_FragColor = texture2D(u_Texture, v_TexCoord);
        }
        else gl_FragColor = texture2D(u_Forest, v_TexCoord);
    }
    else {
        vec2 fragPos = gl_FragCoord.xy;

        vec2 a = u_Points[0] * 0.5 + 0.5;
        vec2 b = u_Points[1] * 0.5 + 0.5;

        a *= u_resolution;
        b *= u_resolution;

        vec2 ab = b - a;
        vec2 ap = fragPos - a;

        float t = clamp(dot(ap, ab) / dot(ab, ab), 0.0, 1.0);
        vec2 closest = a + t * ab;

        float dist = length(fragPos - closest);

        if (dist <= u_Thickness * 0.5) {
            gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
        }
        else {
            gl_FragColor = texture2D(u_Mask, v_TexCoord);
        }
    }
}