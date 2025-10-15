package com.cubeGl.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Gestiona las matrices de Vista (posición del observador) y Proyección (perspectiva o FOV).
 * La traslación (movePosition) se realiza a lo largo de los vectores locales Front y Right
 * para un control de cámara de primera persona (FPS/Free Look).
 */
public class Camera {
    private final Matrix4f viewMatrix;
    private final Matrix4f projectionMatrix;

    private final Vector3f position; // Posición de la cámara en el mundo
    private final Vector3f front;    // Vector que apunta hacia adelante (se recalcula con rotación)
    private final Vector3f up;       // Vector 'arriba' del mundo (fijo)
    private final Vector3f right;    // Vector 'derecha' de la cámara

    private float yaw;   // Rotación horizontal (grados)
    private float pitch; // Rotación vertical (grados)

    private static final float PITCH_LIMIT = 89.0f;

    public Camera(float fov, float aspectRatio, float near, float far) {
        this.viewMatrix = new Matrix4f();
        this.projectionMatrix = new Matrix4f();

        // Inicialización
        this.position = new Vector3f(0.0f, 0.0f, 3.0f);
        this.front = new Vector3f(0.0f, 0.0f, -1.0f);
        this.up = new Vector3f(0.0f, 1.0f, 0.0f);
        this.right = new Vector3f();

        this.yaw = -90.0f;
        this.pitch = 0.0f;

        this.projectionMatrix.setPerspective(fov, aspectRatio, near, far);

        updateCameraVectors();
        updateViewMatrix();
    }

    /**
     * Recalcula los vectores Front y Right a partir de Yaw y Pitch.
     */
    private void updateCameraVectors() {
        // Calcular el vector 'Front'
        this.front.x = (float)(Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        this.front.y = (float)Math.sin(Math.toRadians(pitch));
        this.front.z = (float)(Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        this.front.normalize();

        // Recalcular el vector 'Right'
        this.front.cross(this.up, this.right).normalize();
    }

    /**
     * Recalcula la matriz de vista usando la posición y el vector Front.
     */
    public void updateViewMatrix() {
        // lookAt(posición, posición + vector Front, vector Up)
        this.viewMatrix.identity().lookAt(
                this.position,
                new Vector3f(position).add(this.front),
                this.up
        );
    }

    /**
     * Aplica el desplazamiento de rotación (simula entrada de teclado) a Yaw y Pitch.
     */
    public void processMouseMovement(float xOffset, float yOffset, boolean constrainPitch) {
        float sensitivity = 1.0f;
        yaw += xOffset * sensitivity;
        pitch += yOffset * sensitivity;

        // Limitar el Pitch para evitar voltear la pantalla
        if (constrainPitch) {
            if (pitch > PITCH_LIMIT) pitch = PITCH_LIMIT;
            if (pitch < -PITCH_LIMIT) pitch = -PITCH_LIMIT;
        }

        updateCameraVectors();
    }

    /**
     * Mueve la cámara una cantidad específica a lo largo de los vectores locales de la cámara (Front, Right, Up).
     *
     * @param offsetX Movimiento a lo largo del eje RIGHT (A/D)
     * @param offsetY Movimiento a lo largo del eje UP del mundo (SPACE/SHIFT)
     * @param offsetZ Movimiento a lo largo del eje FRONT (W/S)
     */
    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        // Movimiento Front/Back (Z): se mueve a lo largo del vector Front
        if (offsetZ != 0) {
            this.position.x += front.x * offsetZ;
            this.position.y += front.y * offsetZ;
            this.position.z += front.z * offsetZ;
        }

        // Movimiento Lateral (X): se mueve a lo largo del vector Right
        if (offsetX != 0) {
            this.position.x += right.x * offsetX;
            this.position.y += right.y * offsetX;
            this.position.z += right.z * offsetX;
        }

        // Movimiento Vertical (Y): se mueve a lo largo del eje Y del mundo
        if (offsetY != 0) {
            this.position.y += offsetY;
        }
    }

    // Métodos Getters...
    public Vector3f getPosition() { return position; }
    public Matrix4f getViewProjection() { return new Matrix4f(projectionMatrix).mul(viewMatrix); }
    public Matrix4f getViewMatrix() { return viewMatrix; }
    public Matrix4f getProjectionMatrix() { return projectionMatrix; }
}