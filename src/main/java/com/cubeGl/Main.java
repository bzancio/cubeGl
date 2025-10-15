package com.cubeGl;

import com.cubeGl.graphics.Camera;
import com.cubeGl.graphics.Mesh;
import com.cubeGl.graphics.ShaderProgram;
import com.cubeGl.graphics.Window;
import com.cubeGl.graphics.Transform;
import com.cubeGl.graphics.Texture; // Importar la clase Texture

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Clase principal que contiene el punto de entrada (main) y el bucle de renderizado.
 */
public class Main {
    private Window window;
    private Mesh cube;
    private ShaderProgram shader;
    private Transform transform;
    private Camera camera;
    private Texture texture; // Campo para la textura

    // Variables para el control del tiempo (deltaTime y lastFrame)
    private float deltaTime = 0.0f;
    private float lastFrame = 0.0f;

    public void run() {
        try {
            init();
            loop();
        } catch (Exception e) {
            System.err.println("Un error fatal ocurrió:");
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    /**
     * Inicializa la ventana y carga los recursos de OpenGL (malla, shaders, cámara y textura).
     */
    private void init() {
        window = new Window(800, 800, "CubeGl Modular");
        window.init();

        // Carga de recursos
        cube = Mesh.createCube();
        shader = new ShaderProgram();
        transform = new Transform();

        // --- Cargar la Textura sasel.png ---
        try {
            // Asume que 'sasel.png' está en el classpath (ej: src/main/resources/)
            texture = new Texture("sasel.png");
        } catch (Exception e) {
            System.err.println("ERROR: No se pudo cargar la textura 'sasel.png'.");
            e.printStackTrace();
            throw new RuntimeException("Fallo al inicializar recursos.");
        }

        // Configuración de la uniform de la textura en el shader (solo se hace una vez)
        shader.use();
        // Le decimos al shader que la uniform 'uTexture' debe leer de la unidad de textura 0
        shader.setUniformTexture("uTexture", 0);
        shader.unuse();

        // Inicializar la cámara
        float fov = (float)Math.toRadians(60.0f);
        float aspectRatio = 1.0f;
        camera = new Camera(fov, aspectRatio, 0.1f, 100f);
    }

    /**
     * Procesa la entrada del teclado para mover la cámara.
     */
    private void processInput() {
        float cameraMoveSpeed = 5.0f * deltaTime;
        float cameraRotationSpeed = 80.0f * deltaTime;

        // --- CERRAR VENTANA ---
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window.getWindowHandle(), true);
        }

        // --- TRASLACIÓN (Movimiento Local/Relativo) ---
        // W/S (Front/Back)
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_W) == GLFW_PRESS) {
            camera.movePosition(0, 0, cameraMoveSpeed);
        }
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_S) == GLFW_PRESS) {
            camera.movePosition(0, 0, -cameraMoveSpeed);
        }

        // A/D (Left/Right - Strafe)
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_A) == GLFW_PRESS) {
            camera.movePosition(-cameraMoveSpeed, 0, 0);
        }
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_D) == GLFW_PRESS) {
            camera.movePosition(cameraMoveSpeed, 0, 0);
        }

        // SPACE/SHIFT (Up/Down)
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_SPACE) == GLFW_PRESS) {
            camera.movePosition(0, cameraMoveSpeed, 0);
        }
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            camera.movePosition(0, -cameraMoveSpeed, 0);
        }

        // --- ROTACIÓN (Giro de la vista con Q/E) ---
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_Q) == GLFW_PRESS) {
            camera.processMouseMovement(-cameraRotationSpeed, 0, true);
        }
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_E) == GLFW_PRESS) {
            camera.processMouseMovement(cameraRotationSpeed, 0, true);
        }

        camera.updateViewMatrix();
    }


    /**
     * Bucle principal de renderizado.
     */
    private void loop() {
        while (!window.shouldClose()) {
            // Calcular delta time
            float currentFrame = (float)GLFW.glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;

            // 1. Entrada de datos y Lógica
            processInput();

            float time = (float)GLFW.glfwGetTime();

            // --- Movimiento del Cubo (Matriz del Modelo) ---
            transform.getModelMatrix().identity()
                    .rotateY(time * 0.5f)
                    .rotateX(time * 0.5f);

            // 2. CÁLCULO DE LA MATRIZ MVP (Modelo * Vista * Proyección)
            Matrix4f mvp = camera.getViewProjection().mul(transform.getModelMatrix());

            // 3. Renderizado
            window.clear();
            shader.use();
            shader.setUniformMat4f("mvp", mvp);

            // Enlazar (Bind) la Textura ANTES de dibujar el cubo
            texture.bind();

            cube.render();

            // 4. Presentación
            window.swapBuffers();
            window.pollEvents();
        }
    }

    /**
     * Libera los recursos de OpenGL y termina GLFW.
     */
    private void cleanup() {
        if (shader != null) {
            shader.cleanup();
        }
        if (cube != null) {
            cube.cleanup();
        }
        if (texture != null) { // Liberar la textura
            texture.cleanup();
        }

        GLFW.glfwTerminate();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}