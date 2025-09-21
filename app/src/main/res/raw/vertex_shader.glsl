//attribute vec4 a_Position;
//attribute vec2 a_TexCoord;
//
//varying vec2 v_TexCoord;
//
//void main() {
//    gl_Position = a_Position;
//    v_TexCoord = a_TexCoord;
//}
attribute vec2 a_Position;
varying vec2 v_TexCoord;

void main() {
    v_TexCoord = a_Position * 0.5 + 0.5;
    gl_Position = vec4(a_Position, 0.0, 1.0);
}
