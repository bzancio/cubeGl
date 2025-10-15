package com.cubeGl.graphics;

import java.nio.ByteBuffer;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;

/**
 * Clase que gestiona una textura de OpenGL (imagen) cargada desde un archivo.
 */
public class Texture {
    private final int textureID;

    public Texture(String filename) throws Exception {
        // Generar un ID de textura en OpenGL
        textureID = glGenTextures();

        // Cargar la imagen usando STBImage
        int width;
        int height;
        int numChannels;
        ByteBuffer image;

        // Usamos un MemoryStack para la gestión temporal de memoria
        try (MemoryStack stack = MemoryStack.stackPush()) {
            java.nio.IntBuffer w = stack.mallocInt(1);
            java.nio.IntBuffer h = stack.mallocInt(1);
            java.nio.IntBuffer c = stack.mallocInt(1);

            String fullPath = "src/main/resources/" + filename;

            // Intentar cargar la imagen
            image = STBImage.stbi_load(fullPath, w, h, c, 0);
            if (image == null) {
                throw new Exception("Error al cargar la textura " + filename + ": " + STBImage.stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
            numChannels = c.get();
        }

        // Enlazar (Bind) la textura y configurar sus parámetros
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Voltear la textura verticalmente (OpenGL espera que el origen Y esté abajo)
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        // Configurar los parámetros de la textura (cómo debe muestrearse)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // Repetir horizontalmente
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); // Repetir verticalmente
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // Reducir con NEAREST (pixeles cuadrados)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); // Agrandar con NEAREST

        // Determinar el formato de color
        int format = GL_RGB;
        if (numChannels == 4) format = GL_RGBA;

        // Cargar la imagen a la GPU
        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, image);

        // Generar mipmaps (útil para optimización a diferentes distancias)
        glGenerateMipmap(GL_TEXTURE_2D);

        // Liberar la memoria de la imagen cargada por STBImage
        STBImage.stbi_image_free(image);
    }

    /** Activa y enlaza la textura para su uso en el shader. */
    public void bind() {
        glActiveTexture(GL_TEXTURE0); // Activa la unidad de textura 0
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    /** Limpia la textura de la GPU. */
    public void cleanup() {
        glDeleteTextures(textureID);
    }
}