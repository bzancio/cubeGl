package com.cubeGl.graphics;

import org.joml.Matrix4f;

/**
 * Representa la matriz de transformación del modelo (Model Matrix).
 */
public class Transform {
    private final Matrix4f modelMatrix;

    public Transform() {
        this.modelMatrix = new Matrix4f(); // Matriz identidad por defecto
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }
}
