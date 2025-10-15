package com.cubeGl.graphics;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

/**
 * Gestiona la compilación, enlazado y uso de un par de shaders (Vertex y Fragment).
 */
public class ShaderProgram {
    private final int programId;
    private final FloatBuffer fb; // Buffer para pasar matrices a OpenGL

    public ShaderProgram() {
        this.programId = setupShaders();
        this.fb = BufferUtils.createFloatBuffer(16);
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
        String vertexShaderSource = """
                #version 330 core
                layout(location = 0) in vec3 position;
                layout(location = 1) in vec3 color;
                // ... (otros atributos)
                
                out vec3 vertexColor;
                
                uniform mat4 mvp; // <-- Cambiamos 'transform' por 'mvp'
                
                void main() {
                    gl_Position = mvp * vec4(position, 1.0f); // Aplicamos la matriz MVP
                    vertexColor = color;
                }""";

        String fragmentShaderSource = """
                #version 330 core
                in vec3 vertexColor;
                out vec4 fragColor;
                
                void main() {
                    fragColor = vec4(vertexColor, 1.0f);
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
     * Establece un valor de matriz 4x4 (uniform) en el shader.
     * @param name Nombre del uniform en el shader (ej. "transform").
     * @param matrix La matriz de JOML a pasar.
     */
    public void setUniformMat4f(String name, Matrix4f matrix) {
        int location = glGetUniformLocation(programId, name);
        if (location != -1) {
            // Rellenar el FloatBuffer con los datos de la matriz
            matrix.get(fb);
            // Pasar los datos a la variable uniform del shader
            glUniformMatrix4fv(location, false, fb);
        }
    }

    /**
     * Libera los recursos del programa de shaders.
     */
    public void cleanup() {
        glDeleteProgram(programId);
    }
}