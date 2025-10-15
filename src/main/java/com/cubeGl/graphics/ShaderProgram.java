package com.cubeGl.graphics;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * Gestiona la compilación, enlazado y uso de un par de shaders (Vertex y Fragment).
 */
public class ShaderProgram {
    private final int programId;
    private final FloatBuffer fb;
    private final Map<String, Integer> uniformLocations;

    public ShaderProgram() {
        this.uniformLocations = new HashMap<>();
        this.programId = setupShaders();
        this.fb = BufferUtils.createFloatBuffer(16);

        // Inicializar la ubicación de los uniforms esenciales
        createUniform("mvp");
        createUniform("uTexture");
    }

    // Método auxiliar para crear y guardar la ubicación de un uniform
    private void createUniform(String uniformName) {
        int location = glGetUniformLocation(programId, uniformName);
        uniformLocations.put(uniformName, location);
    }

    private int compileShader(String source, int type) {
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        // Comprobación de errores de compilación
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Error al compilar shader: " + glGetShaderInfoLog(shaderId, 1024));
            throw new RuntimeException("Error de compilación de Shader.");
        }
        return shaderId;
    }

    private int setupShaders() {
        // Shaders embebidos, adaptados para la textura:
        String vertexShaderSource = """
                #version 330 core
                layout(location = 0) in vec3 aPos;     // Usamos aPos para claridad
                layout(location = 1) in vec3 aColor;
                layout(location = 2) in vec2 aTexCoord; // NUEVO: Coordenadas de textura
                
                out vec2 vTexCoord;
                out vec3 vColor;
                
                uniform mat4 mvp;
                
                void main() {
                    vTexCoord = aTexCoord;
                    vColor = aColor;
                    gl_Position = mvp * vec4(aPos, 1.0f);
                }""";

        String fragmentShaderSource = """
                #version 330 core
                
                uniform sampler2D uTexture; // NUEVO: Uniform para muestrear la textura
                
                in vec2 vTexCoord;
                in vec3 vColor; // Mantenemos el color por si acaso
                out vec4 fragColor;
                
                void main() {
                    // Usamos la textura para obtener el color final
                    fragColor = texture(uTexture, vTexCoord);
                }""";

        // 1. Compilar shaders
        int vertexShader = compileShader(vertexShaderSource, GL_VERTEX_SHADER);
        int fragmentShader = compileShader(fragmentShaderSource, GL_FRAGMENT_SHADER);

        // 2. Enlazar Programa
        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);

        // Comprobación de errores de enlazado (Linking)
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Error al enlazar programa: " + glGetProgramInfoLog(program, 1024));
            throw new RuntimeException("Error de enlazado de Shader.");
        }

        // 3. Limpiar recursos intermedios (shaders individuales)
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return program;
    }

    /**
     * Activa este programa de shaders para su uso en el renderizado.
     */
    public void use() {
        glUseProgram(programId);
    }

    /**
     * Desactiva este programa de shaders.
     */
    public void unuse() {
        glUseProgram(0);
    }

    /**
     * Establece un valor de matriz 4x4 (uniform) en el shader.
     */
    public void setUniformMat4f(String name, Matrix4f matrix) {
        int location = uniformLocations.get(name);
        if (location != -1) {
            matrix.get(fb);
            glUniformMatrix4fv(location, false, fb);
        }
    }

    /**
     * Establece el valor de la uniform de la textura (sampler2D) en el shader.
     */
    public void setUniformTexture(String uniformName, int unit) {
        Integer location = uniformLocations.get(uniformName);
        if (location == null || location == -1) {
            System.err.println("Advertencia: Uniform '" + uniformName + "' no se encontró o no se inicializó.");
            return;
        }
        glUniform1i(location, unit);
    }

    /**
     * Libera los recursos del programa de shaders.
     */
    public void cleanup() {
        glDeleteProgram(programId);
    }
}