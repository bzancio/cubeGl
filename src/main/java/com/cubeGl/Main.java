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
        glClearColor(0.5f, 0.7f, 1.0f, 0.0f);

        setupTriangle();
        setupShaders();
    }

    private void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            glClear((GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT));

            glUseProgram(shaderProgram);
            glBindVertexArray(vaoId);
            glDrawArrays(GL_TRIANGLES, 0, 3);

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

    private void setupTriangle() {
        float[] vertices = {
                0.0f, 0.5f,
                -0.5f, -0.5f,
                0.5f, -0.5f
        };
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);
    }

    private void setupShaders() {
        String vertexShaderSource = """
                #version 330 core
                layout(location = 0) in vec2 position;
                void main() {
                    gl_Position = vec4(position, 0.0, 1.0);
                }""";

        String fragmentShaderSource = """
                #version 330 core
                out vec4 fragColor;
                void main() {
                    fragColor = vec4(1.0, 0.5, 0.2, 1.0);
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
