package club.pulsive.api.yoint.shader;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Shader {
    protected int programID;

    private Map<String, Integer> uniformsMap;

    public Shader() {
        int vertexID;
        int fragmentID;
        try {
            final String vertexShader = "/assets/minecraft/vertex.vsh";
            final InputStream vertexStream = getClass().getResourceAsStream(vertexShader);
            assert vertexStream != null;
            vertexID = createShader(IOUtils.toString(vertexStream), ARBVertexShader.GL_VERTEX_SHADER_ARB);
            IOUtils.closeQuietly(vertexStream);

            final String fragmentShader = "/assets/minecraft/" + getClass().getAnnotation(ShaderInfo.class).name();
            final InputStream fragmentStream = getClass().getResourceAsStream(fragmentShader);
            assert fragmentStream != null;
            fragmentID = createShader(IOUtils.toString(fragmentStream), ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
            IOUtils.closeQuietly(fragmentStream);
        } catch (final Exception e) {
            e.printStackTrace();
            return;
        }

        if (vertexID == 0 || fragmentID == 0)
            return;

        programID = ARBShaderObjects.glCreateProgramObjectARB();

        if (programID == 0)
            return;

        ARBShaderObjects.glAttachObjectARB(programID, vertexID);
        ARBShaderObjects.glAttachObjectARB(programID, fragmentID);

        ARBShaderObjects.glLinkProgramARB(programID);
        ARBShaderObjects.glValidateProgramARB(programID);

    }

    public void startShader(final boolean update) {
        GL11.glPushMatrix();
        GL20.glUseProgram(programID);

        if (uniformsMap == null) {
            uniformsMap = new HashMap<>();
            setupUniforms();
        }
        if (update) {
            updateUniforms();
        }
    }

    public void stopShader() {
        GL20.glUseProgram(0);
        GL11.glPopMatrix();
    }

    public abstract void setupUniforms();

    public abstract void updateUniforms();

    private String getLogInfo(final int i) {
        return ARBShaderObjects.glGetInfoLogARB(i, ARBShaderObjects.glGetObjectParameteriARB(i, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    private int createShader(final String shaderSource, final int shaderType) {
        int shader = 0;

        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

            if (shader == 0)
                return 0;

            ARBShaderObjects.glShaderSourceARB(shader, shaderSource);
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
                throw new RuntimeException("Error creating shader: " + getLogInfo(shader));

            return shader;
        } catch (final Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            throw e;
        }
    }

    public void setUniform(final String uniformName, final int location) {
        uniformsMap.put(uniformName, location);
    }

    public void setupUniform(final String uniformName) {
        setUniform(uniformName, GL20.glGetUniformLocation(programID, uniformName));
    }

    public int getUniform(final String uniformName) {
        return uniformsMap.get(uniformName);
    }
}
