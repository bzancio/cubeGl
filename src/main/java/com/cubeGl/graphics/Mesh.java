package com.cubeGl.graphics;

import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Representa una malla 3D (geometría). Contiene el VAO, VBO, EBO y la información de conteo.
 */
public class Mesh {
    private final int vaoId;
    private final int vertexCount;

    // IDs de VBO/EBO para su correcta liberación
    private final int posColorVboId; // VBO único para Posición y Color
    private final int texVboId;      // VBO para Coordenadas de Textura
    private final int eboId;

    // Constructor privado
    private Mesh(int vaoId, int vertexCount, int posColorVboId, int texVboId, int eboId) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
        this.posColorVboId = posColorVboId;
        this.texVboId = texVboId;
        this.eboId = eboId;
    }

    /**
     * Crea un objeto Mesh y lo configura como un cubo 3D.
     * @return Una instancia de Mesh con la geometría del cubo cargada.
     */
    public static Mesh createCube() {
        // --- 1. 24 VÉRTICES ÚNICOS (4 por cara * 6 caras) ---
        float[] vertices = {
                // Posiciones x,y,z         // Colores r,g,b (mantener 1,1,1 o el color base)
                // Cara frontal (-Z)
                -0.5f, -0.5f, -0.5f,    1.0f, 1.0f, 1.0f, // 0
                0.5f, -0.5f, -0.5f,    1.0f, 1.0f, 1.0f, // 1
                0.5f,  0.5f, -0.5f,    1.0f, 1.0f, 1.0f, // 2
                -0.5f,  0.5f, -0.5f,    1.0f, 1.0f, 1.0f, // 3

                // Cara trasera (+Z)
                -0.5f, -0.5f,  0.5f,    1.0f, 1.0f, 1.0f, // 4
                0.5f, -0.5f,  0.5f,    1.0f, 1.0f, 1.0f, // 5
                0.5f,  0.5f,  0.5f,    1.0f, 1.0f, 1.0f, // 6
                -0.5f,  0.5f,  0.5f,    1.0f, 1.0f, 1.0f, // 7

                // Cara Izquierda (-X)
                -0.5f, -0.5f,  0.5f,    1.0f, 1.0f, 1.0f, // 8 (coincide con 4)
                -0.5f, -0.5f, -0.5f,    1.0f, 1.0f, 1.0f, // 9 (coincide con 0)
                -0.5f,  0.5f, -0.5f,    1.0f, 1.0f, 1.0f, // 10 (coincide con 3)
                -0.5f,  0.5f,  0.5f,    1.0f, 1.0f, 1.0f, // 11 (coincide con 7)

                // Cara Derecha (+X)
                0.5f, -0.5f, -0.5f,    1.0f, 1.0f, 1.0f, // 12 (coincide con 1)
                0.5f, -0.5f,  0.5f,    1.0f, 1.0f, 1.0f, // 13 (coincide con 5)
                0.5f,  0.5f,  0.5f,    1.0f, 1.0f, 1.0f, // 14 (coincide con 6)
                0.5f,  0.5f, -0.5f,    1.0f, 1.0f, 1.0f, // 15 (coincide con 2)

                // Cara Arriba (+Y)
                -0.5f,  0.5f, -0.5f,    1.0f, 1.0f, 1.0f, // 16 (coincide con 3)
                0.5f,  0.5f, -0.5f,    1.0f, 1.0f, 1.0f, // 17 (coincide con 2)
                0.5f,  0.5f,  0.5f,    1.0f, 1.0f, 1.0f, // 18 (coincide con 6)
                -0.5f,  0.5f,  0.5f,    1.0f, 1.0f, 1.0f, // 19 (coincide con 7)

                // Cara Abajo (-Y)
                -0.5f, -0.5f, -0.5f,    1.0f, 1.0f, 1.0f, // 20 (coincide con 0)
                0.5f, -0.5f, -0.5f,    1.0f, 1.0f, 1.0f, // 21 (coincide con 1)
                0.5f, -0.5f,  0.5f,    1.0f, 1.0f, 1.0f, // 22 (coincide con 5)
                -0.5f, -0.5f,  0.5f,    1.0f, 1.0f, 1.0f  // 23 (coincide con 4)
        };

        // --- 2. 24 COORDENADAS DE TEXTURA (4 por cara * 6 caras) ---
        // Se repiten las mismas 4 coordenadas (0,0) a (1,1) para cada cara
        float[] texCoords = {
                // Frontal (-Z)
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                // Trasera (+Z)
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                // Izquierda (-X)
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                // Derecha (+X)
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                // Arriba (+Y)
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                // Abajo (-Y)
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f
        };

        // --- 3. 36 ÍNDICES (6 por cara * 6 caras) ---
        int[] indices = {
                // Frontal
                0, 1, 2, 2, 3, 0,
                // Trasera
                4, 5, 6, 6, 7, 4,
                // Izquierda
                8, 9, 10, 10, 11, 8,
                // Derecha
                12, 13, 14, 14, 15, 12,
                // Arriba
                16, 17, 18, 18, 19, 16,
                // Abajo
                20, 21, 22, 22, 23, 20
        };

        // --- INICIALIZACIÓN DE OPENGL ---
        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // VBOs e EBO IDs
        int posColorVboId = glGenBuffers();
        int texVboId = glGenBuffers();
        int eboId = glGenBuffers();

        // 1. VBO de Posición y Color (location 0 y 1)
        FloatBuffer vertexBuffer = null;
        try {
            vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
            vertexBuffer.put(vertices).flip();
            glBindBuffer(GL_ARRAY_BUFFER, posColorVboId);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

            int stride = (3 + 3) * Float.BYTES;

            // Atributo 0: Posición
            glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
            glEnableVertexAttribArray(0);

            // Atributo 1: Color
            glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3L * Float.BYTES);
            glEnableVertexAttribArray(1);
        } finally {
            if (vertexBuffer != null) MemoryUtil.memFree(vertexBuffer);
        }

        // 2. VBO de Coordenadas de Textura (location 2)
        FloatBuffer texCoordsBuffer = null;
        try {
            texCoordsBuffer = MemoryUtil.memAllocFloat(texCoords.length);
            texCoordsBuffer.put(texCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, texVboId);
            glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);

            // Atributo 2: TexCoord (2 floats, no intercalado, stride 0)
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(2);
        } finally {
            if (texCoordsBuffer != null) MemoryUtil.memFree(texCoordsBuffer);
        }

        // 3. EBO (Element Buffer Object)
        java.nio.IntBuffer indexBuffer = null;
        try {
            indexBuffer = MemoryUtil.memAllocInt(indices.length);
            indexBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        } finally {
            if (indexBuffer != null) MemoryUtil.memFree(indexBuffer);
        }

        // Desenlazar
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        return new Mesh(vao, indices.length, posColorVboId, texVboId, eboId);
    }

    /**
     * Dibuja la malla usando el VAO y los índices.
     */
    public void render() {
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    /**
     * Libera los recursos de OpenGL (VAO, VBO, EBO).
     */
    public void cleanup() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);

        // Eliminar buffers VBO/EBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(posColorVboId);
        glDeleteBuffers(texVboId);
        glDeleteBuffers(eboId);

        // Eliminar VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}