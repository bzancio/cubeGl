package com.cubeGl.graphics;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.*;

/**
 * Gestiona la inicialización de GLFW, la creación de la ventana
 * y las operaciones básicas de control de la ventana (cerrar, limpiar, swap).
 */
public class Window {
    private long windowHandle; // Corregido: Almacena el identificador único de la ventana.
    private final int width;
    private final int height;
    private final String title;

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    /**
     * Inicializa GLFW, crea la ventana y establece el contexto de OpenGL.
     */
    public void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("ERROR: No se pudo iniciar GLFW.");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);

        // Crear la ventana y ASIGNAR el handle al campo de la clase
        this.windowHandle = GLFW.glfwCreateWindow(width, height, title, 0, 0);

        if (this.windowHandle == 0) {
            GLFW.glfwTerminate();
            throw new RuntimeException("ERROR: No se pudo crear la ventana de GLFW.");
        }

        GLFW.glfwMakeContextCurrent(this.windowHandle);
        GLFW.glfwSwapInterval(1); // Habilitar V-Sync
        GLFW.glfwShowWindow(this.windowHandle);

        // Crear las capacidades de OpenGL
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Limpia la pantalla (buffers de color y profundidad).
     */
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Intercambia los buffers de la ventana (double buffering).
     */
    public void swapBuffers() {
        GLFW.glfwSwapBuffers(windowHandle);
    }

    /**
     * Procesa los eventos de la ventana (input, redimensionamiento, etc.).
     */
    public void pollEvents() {
        GLFW.glfwPollEvents();
    }

    /**
     * @return Verdadero sí se ha solicitado cerrar la ventana.
     */
    public boolean shouldClose() {
        // Usa el handle de la ventana asignado en init()
        return GLFW.glfwWindowShouldClose(windowHandle);
    }

    public long getWindowHandle() {
        return windowHandle;
    }
}