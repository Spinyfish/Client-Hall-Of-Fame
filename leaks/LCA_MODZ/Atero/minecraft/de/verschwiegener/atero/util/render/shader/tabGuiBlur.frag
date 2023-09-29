uniform vec2 resolution;
uniform vec2 u_size;
uniform sampler2D a_texture;

void main() {
    float Pi = 6.28318530718; // Pi*2
    
    // GAUSSIAN BLUR SETTINGS {{{
    float Directions = 16.0; // BLUR DIRECTIONS (Default 16.0 - More is better but slower)
    float Quality = 10.0; // BLUR QUALITY (Default 4.0 - More is better but slower)
    float Size = 4.0; // BLUR SIZE (Radius)
    // GAUSSIAN BLUR SETTINGS }}}
    
    float maxX = u_size.x;
    float maxY = u_size.y;
    
    if(gl_FragCoord.x < maxX && (gl_FragCoord.y - resolution.y) /-1.0 < maxY){
    vec2 Radius = Size/resolution.xy;
    
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv =  gl_FragCoord/resolution.xy;
    // Pixel colour
    vec4 Color = texture2D(a_texture, uv);
    
    // Blur calculations
    for( float d=0.0; d<Pi; d+=Pi/Directions)
    {
        for(float i=1.0/Quality; i<=1.0; i+=1.0/Quality)
        {
            Color += texture2D(a_texture, uv + vec2(cos(d), sin(d)) *Radius * i);      
        }
    }
    
        // Output to screen
        Color /= Quality * Directions - 15.0;
        gl_FragColor =  Color;
    }else{
        vec2 uv = gl_FragCoord/resolution.xy;
        vec4 Color = texture2D(a_texture, uv);
        gl_FragColor = Color;
    }
}