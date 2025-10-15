package com.cubeGl.graphics;

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

    private Mesh(int vaoId, int vertexCount) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
    }

    /**
     * Crea un objeto Mesh y lo configura como un cubo 3D.
     * @return Una instancia de Mesh con la geometría del cubo cargada.
     */
    public static Mesh createCube() {
        // Datos del cubo: Posiciones (3) y Colores (3)
        float[] vertices = {
                // posiciones         // colores (RGB)
                -0.5f, -0.5f, -0.5f,    1.0f, 0.0f, 0.0f,
                0.5f, -0.5f, -0.5f,    0.0f, 1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,    0.0f, 0.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,    1.0f, 1.0f, 0.0f,
                // ... (el resto de los vértices de tu código original)
                -0.5f, -0.5f,  0.5f,    1.0f, 0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,    0.0f, 1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,    1.0f, 1.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,    0.0f, 0.0f, 0.0f
        };

        int[] indices = {
                // Índices para el orden de dibujo
                0, 1, 2, 2, 3, 0, // Cara trasera
                // ... (el resto de los índices de tu código original)
                4, 5, 6, 6, 7, 4, // cara delantera
                0, 4, 7, 7, 3, 0, // izquierda
                1, 5, 6, 6, 2, 1, // derecha
                3, 2, 6, 6, 7, 3, // arriba
                0, 1, 5, 5, 4, 0  // abajo
        };

        int vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // VBO (Vertex Buffer Object): Cargar datos de posición y color
        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // EBO (Element Buffer Object): Cargar índices
        int eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Especificación del layout (cómo leer los datos en el VBO)
        int stride = (3 + 3) * Float.BYTES;

        // Atributo 0: Posición (3 floats, offset 0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        // Atributo 1: Color (3 floats, offset 3 * Float.BYTES)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Desenlazar
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        // Nota: Los buffers VBO/EBO se mantienen enlazados al VAO,
        // por lo que no es necesario guardar sus ID, solo el VAO ID.

        return new Mesh(vao, indices.length);
    }

    /**
     * Dibuja la malla usando el VAO y los índices.
     */
    public void render() {
        // Enlazar el VAO (lo que enlaza el VBO y el EBO guardados)
        glBindVertexArray(vaoId);

        // Dibujar los elementos (triángulos) usando los índices
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        // Desenlazar el VAO
        glBindVertexArray(0);
    }

    /**
     * Libera los recursos de OpenGL (VAO, VBO, EBO).
     */
    public void cleanup() {
        glDeleteVertexArrays(vaoId);
        // glDeleteBuffers(vbo/ebo ids, si los hubieras guardado)
    }
}
