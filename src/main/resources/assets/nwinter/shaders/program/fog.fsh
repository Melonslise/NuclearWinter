#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler3D NoiseVolume;
uniform sampler3D SkylightVolume;

uniform mat4 ProjInverseMat;
uniform mat4 ViewInverseMat;

uniform vec3 CameraPosition;
uniform float GameTime;

in vec2 texCoord;

out vec4 fragColor;

vec3 playerSpace(vec2 uv, float depth)
{
	vec3 ndc = vec3(uv, depth) * 2.0 - 1.0;
	vec4 posCS = vec4(ndc, 1.0);
	vec4 posVS = ProjInverseMat * posCS;
	posVS /= posVS.w;
	vec4 posPS = ViewInverseMat * posVS;
	return posPS.xyz;
}

/*
vec3 applyFog(vec3 rgb, float distance)
{
	float density = 0.1;
	//float fogAmount = 1.0 - exp2(- density * density * distance * distance * LOG2);
	float fogAmount = 0.4 + 0.6 * sqrt(distance / 16.0);
	fogAmount = clamp(fogAmount, 0.0, 1.0);

	vec3 fogColor = vec3(0.6, 0.7, 0.8);
	return mix(rgb, fogColor, fogAmount);
}
*/

/*
https://www.shadertoy.com/view/XsX3zB
vec3 random3(vec3 c)
{
	float j = 4096.0 * sin(dot(c, vec3(17.0, 59.4, 15.0)));
	vec3 r;
	r.z = fract(512.0 * j);
	j *= 0.125;
	r.x = fract(512.0 * j);
	j *= 0.125;
	r.y = fract(512.0 * j);
	return r - 0.5;
}

const float F3 = 0.3333333;
const float G3 = 0.1666667;

float simplex3d(vec3 p)
{
	vec3 s = floor(p + dot(p, vec3(F3)));
	vec3 x = p - s + dot(s, vec3(G3));

	vec3 e = step(vec3(0.0), x - x.yzx);
	vec3 i1 = e*(1.0 - e.zxy);
	vec3 i2 = 1.0 - e.zxy*(1.0 - e);

	vec3 x1 = x - i1 + G3;
	vec3 x2 = x - i2 + 2.0*G3;
	vec3 x3 = x - 1.0 + 3.0*G3;

	vec4 w, d;

	w.x = dot(x, x);
	w.y = dot(x1, x1);
	w.z = dot(x2, x2);
	w.w = dot(x3, x3);

	w = max(0.6 - w, 0.0);

	d.x = dot(random3(s), x);
	d.y = dot(random3(s + i1), x1);
	d.z = dot(random3(s + i2), x2);
	d.w = dot(random3(s + 1.0), x3);

	 w *= w;
	 w *= w;
	 d *= w;

	 return dot(d, vec4(52.0));
}

const mat3 rot1 = mat3(-0.37, 0.36, 0.85, -0.14, -0.93, 0.34, 0.92, 0.01, 0.4);
const mat3 rot2 = mat3(-0.55, -0.39, 0.74, 0.33, -0.91, -0.24, 0.77, 0.12, 0.63);
const mat3 rot3 = mat3(-0.71, 0.52, -0.47, -0.08, -0.72, -0.68, -0.7, -0.45, 0.56);

float fbm(vec3 m, int octaves, float amplitude)
{
	float value = 0.0;

	for(int i = 0; i < octaves; ++i)
	{
		value += amplitude * simplex3d(m);
		m *= 2.0;
		amplitude *= 0.5;
	}

	return value;
}
*/

float marchFog(vec3 ro, vec3 rd, float maxDist, float stepSize, vec3 driftDir, float time)
{
	float fog = 0.0;
	float dist = 0.0;

	int maxSteps = 128;
	// stepSize = maxDist / maxSteps;

	for(int i = 0; i < maxSteps; ++i)
	{
		vec3 p = ro + rd * dist;

		if(dist > maxDist || p.y - ro.y > 8.0)
		{
			break;
		}

		vec3 sp = p;
		sp -= driftDir * time;
		sp /= 64.0;

		// float n = texCoord.x > 0.5 ? (texture(NoiseVolume, sp).r * 2.0 - 1.0) * 0.5 : fbm(sp * 8.0, 5, 0.5);

		float n = (texture(NoiseVolume, sp).r * 2.0 - 1.0) * 0.5;
		fog += max(0.0, n) / clamp(dist, 8.0, 64.0) * 2.0;

		dist += stepSize;
	}

	return min(fog, 1.0);
}

void main()
{
	vec3 color = texture(DiffuseSampler, texCoord).rgb;
	float depth = texture(DiffuseDepthSampler, texCoord).r;

	vec3 posPS = playerSpace(texCoord, depth);
	float dstToSurface = length(posPS);

	vec3 windDir = normalize(vec3(-2.0, 1.0, 2.0));
	float fog = marchFog(CameraPosition, normalize(posPS), dstToSurface, 1.0, windDir, GameTime * 24000.0);
	fragColor = vec4(mix(color, vec3(1.0, 1.0, 1.0), fog), 1.0);

	float r = texture(SkylightVolume, vec3(texCoord.x, 0.0, texCoord.y)).r; // mod(int(GameTime * 1200), 3) / 3.0
	fragColor = vec4(r, r, r, 1.0);

	/*
	float r = fbm(vec3(texCoord, mod(int(GameTime * 24000), 64) / 64.0) * 8.0, 4, 0.5) * 0.5 + 0.5;
	float r2 = texture(NoiseVolume, vec3(texCoord, mod(int(GameTime * 24000), 64) / 64.0)).r;

	if(texCoord.x < 0.5)
		fragColor = vec4(r, r, r, 1.0);
	else
		fragColor = vec4(r2, r2, r2, 1.0);
	*/
}