precision mediump float;

uniform vec4 u_Color;
uniform float u_Thickness;
uniform vec2 u_Points[2];
uniform vec2 u_resolution;
uniform sampler2D u_Texture;

varying vec2 v_TexCoord;

void main() {
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

    vec4 prev = texture2D(u_Texture, v_TexCoord);

    if(dist <= u_Thickness * 0.5) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);  // black = erase
    } else {
        gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);  // white = keep foreground
    }
}
