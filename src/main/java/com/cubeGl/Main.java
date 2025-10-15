package com.cubeGl;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Main {
    private long window;
    private int vaoId;
    private int shaderProgram;

    public void run() {
        init();
        loop();
        GLFW.glfwTerminate();
    }

    public void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("No se pudo iniciar GLFW");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        window = GLFW.glfwCreateWindow(800, 600, "cubeGl", 0, 0);
        if (window == 0) {
            throw new RuntimeException("No se pudo crear la ventana");
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwShowWindow(window);

        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        setupCube();
        setupShaders();
    }

    private void loop() {
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);

        while (!GLFW.glfwWindowShouldClose(window)) {
            glClear((GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT));

            glUseProgram(shaderProgram);

            float time = (float)GLFW.glfwGetTime();
            Matrix4f transform  = new Matrix4f()
                    .rotateY(time * 0.5f)
                    .rotateX(time * 0.5f);

            int transforMLoc = glGetUniformLocation(shaderProgram, "transform");
            transform.get(fb);
            glUniformMatrix4fv(transforMLoc, false, fb);

            glBindVertexArray(vaoId);
            glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

    private void setupCube() {
        float[] vertices = {
                // posiciones        // colores (RGB)
                -0.5f, -0.5f, -0.5f,   1.0f, 0.0f, 0.0f, // rojo
                0.5f, -0.5f, -0.5f,   0.0f, 1.0f, 0.0f, // verde
                0.5f,  0.5f, -0.5f,   0.0f, 0.0f, 1.0f, // azul
                -0.5f,  0.5f, -0.5f,   1.0f, 1.0f, 0.0f, // amarillo
                -0.5f, -0.5f,  0.5f,   1.0f, 0.0f, 1.0f, // magenta
                0.5f, -0.5f,  0.5f,   0.0f, 1.0f, 1.0f, // cian
                0.5f,  0.5f,  0.5f,   1.0f, 1.0f, 1.0f, // blanco
                -0.5f,  0.5f,  0.5f,   0.0f, 0.0f, 0.0f  // negro
        };

        int[] indices = {
                0, 1, 2, 2, 3, 0, // cara trasera
                4, 5, 6, 6, 7, 4, // cara delantera
                0, 4, 7, 7, 3, 0, // izquierda
                1, 5, 6, 6, 2, 1, // derecha
                3, 2, 6, 6, 7, 3, // arriba
                0, 1, 5, 5, 4, 0  // abajo
        };


        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        int eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        int stride = (3 + 3) * Float.BYTES;

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    private void setupShaders() {
        String vertexShaderSource = """
                #version 330 core
                 layout(location = 0) in vec3 position;
                 layout(location = 1) in vec3 color;
                
                 out vec3 vertexColor; // saldrá hacia el fragment shader
                
                 uniform mat4 transform;
                
                 void main() {
                     gl_Position = transform * vec4(position, 1.0);
                     vertexColor = color;
                 }""";

        String fragmentShaderSource = """
                #version 330 core
                in vec3 vertexColor;
                out vec4 fragColor;
                
                void main() {
                    fragColor = vec4(vertexColor, 1.0);
                }""";

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

}
