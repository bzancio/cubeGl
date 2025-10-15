package com.cubeGl;

import com.cubeGl.graphics.Camera;
import com.cubeGl.graphics.Mesh;
import com.cubeGl.graphics.ShaderProgram;
import com.cubeGl.graphics.Window;
import com.cubeGl.graphics.Transform;

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
     * Inicializa la ventana y carga los recursos de OpenGL (malla, shaders y cámara).
     */
    private void init() {
        window = new Window(800, 800, "CubeGl Modular");
        window.init();

        // Carga de recursos
        cube = Mesh.createCube();
        shader = new ShaderProgram();
        transform = new Transform();

        // Inicializar la cámara
        float fov = (float)Math.toRadians(60.0f);
        float aspectRatio = 1.0f;
        camera = new Camera(fov, aspectRatio, 0.1f, 100f);
    }

    /**
     * Procesa la entrada del teclado para mover la cámara.
     */
    private void processInput() {
        // Usamos deltaTime para la traslación.
        // NOTA: MoveSpeed es ahora una unidad base, la velocidad real la da cameraMoveSpeed.
        float cameraMoveSpeed = 5.0f * deltaTime;

        // La velocidad de rotación ya está escalada en grados/segundo
        float cameraRotationSpeed = 80.0f * deltaTime;

        // --- CERRAR VENTANA ---
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window.getWindowHandle(), true);
        }

        // --- TRASLACIÓN (Movimiento en ejes del mundo) ---
        // W/S (Eje Z), A/D (Eje X), SPACE/SHIFT (Eje Y)

        // Nota sobre W/S: El Z positivo en World Space es 'hacia ti'.
        // Queremos 'avanzar' (Z negativo) cuando pulsamos W.
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_W) == GLFW_PRESS) {
            camera.movePosition(0, 0, cameraMoveSpeed);
        }
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_S) == GLFW_PRESS) {
            camera.movePosition(0, 0, -cameraMoveSpeed);
        }

        // Movimiento lateral (Eje X)
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_A) == GLFW_PRESS) {
            camera.movePosition(-cameraMoveSpeed, 0, 0);
        }
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_D) == GLFW_PRESS) {
            camera.movePosition(cameraMoveSpeed, 0, 0);
        }

        // Movimiento vertical (Eje Y)
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_SPACE) == GLFW_PRESS) {
            camera.movePosition(0, cameraMoveSpeed, 0);
        }
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            camera.movePosition(0, -cameraMoveSpeed, 0);
        }

        // --- ROTACIÓN (Giro de la vista con Q/E) ---

        // Rotación Yaw (horizontal) con Q/E
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_Q) == GLFW_PRESS) {
            // Q: Gira a la izquierda (Yaw negativo)
            // Ya aplicamos deltaTime a cameraRotationSpeed, NO lo multiplicamos otra vez.
            camera.processMouseMovement(-cameraRotationSpeed, 0, true);
        }
        if (glfwGetKey(window.getWindowHandle(), GLFW_KEY_E) == GLFW_PRESS) {
            // E: Gira a la derecha (Yaw positivo)
            camera.processMouseMovement(cameraRotationSpeed, 0, true);
        }

        // La matriz de vista se actualiza una vez al final del loop para reflejar
        // CUALQUIER cambio (traslación o rotación).
        camera.updateViewMatrix();
    }


    /**
     * Bucle principal de renderizado.
     */
    private void loop() {
        while (!window.shouldClose()) {
            // Calcular delta time (solo se calcula una vez por frame)
            float currentFrame = (float)GLFW.glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;

            // 1. Entrada de datos y Lógica

            // Procesamos la entrada y actualizamos la matriz de vista dentro de processInput()
            processInput();

            float time = (float)GLFW.glfwGetTime();

            // --- Movimiento del Cubo (Matriz del Modelo) ---
            transform.getModelMatrix().identity()
                    // Si quieres que el cubo no se mueva, mantén la rotación en 0.0f
                    .rotateY(time * 0.5f)
                    .rotateX(time * 0.5f);

            // 2. CÁLCULO DE LA MATRIZ MVP (Modelo * Vista * Proyección)
            Matrix4f mvp = camera.getViewProjection().mul(transform.getModelMatrix());

            // 3. Renderizado
            window.clear();
            shader.use();
            shader.setUniformMat4f("mvp", mvp);

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

        GLFW.glfwTerminate();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}