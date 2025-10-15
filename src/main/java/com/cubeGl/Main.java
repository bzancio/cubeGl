package com.cubeGl;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
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
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        setupSquare();
        setupShaders();
    }

    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            glClear((GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT));

            glUseProgram(shaderProgram);
            glBindVertexArray(vaoId);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

    private void setupSquare() {
        float[] vertices = {
                -0.5f,  0.5f,      1.0f, 0.0f, 0.0f,   // 0 - A (rojo)
                0.5f,  0.5f,      0.0f, 1.0f, 0.0f,   // 1 - B (verde)
                -0.5f, -0.5f,      0.0f, 0.0f, 1.0f,   // 2 - C (azul)
                0.5f, -0.5f,      1.0f, 1.0f, 0.0f    // 3 - D (amarillo)
        };

        int[] indices = {
                0, 1, 2,   // Primer triángulo (A-B-C)
                1, 3, 2    // Segundo triángulo (B-D-C)
        };


        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        int stride = (2 + 3) * Float.BYTES;

        int eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0L);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 2L * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    private void setupShaders() {
        String vertexShaderSource = """
                #version 330 core
                 layout(location = 0) in vec2 position;
                 layout(location = 1) in vec3 color;
                
                 out vec3 vertexColor; // saldrá hacia el fragment shader
                
                 void main() {
                     gl_Position = vec4(position, 0.0, 1.0);
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
