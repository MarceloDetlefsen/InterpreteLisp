/*
 * Universidad del Valle de Guatemala
 * Algoritmos y Estructuras de Datos
 * Ing. Douglas Barrios
 * @author: Marcelo Detlefsen, Jose Rivera, Fabián Prado
 * Creación: 01/03/2025
 * última modificación: 16/03/2025
 * File Name: Token.java
 * Descripción: Clase que se encarga de almacenar los tokens de la expresión LISP.
 */

/**
 * Clase que representa un token en el análisis léxico de expresiones LISP.
 * Un token es la unidad básica del código fuente, como paréntesis, operadores,
 * números o identificadores.
 */
public class Token 
{
    /**
     * Valor textual del token.
     */
    private String value; 

    /**
     * Constructor que crea un nuevo token con el valor especificado.
     *
     * @param value El valor textual del token
     */
    public Token(String value) {
        this.value = value; 
    }

    /**
     * Obtiene el valor textual del token.
     *
     * @return El valor del token como cadena de texto
     */
    public String getValue() {
        return value;
    }

    /**
     * Devuelve una representación en forma de cadena del token.
     *
     * @return El valor textual del token
     */
    @Override
    public String toString() {
        return value;
    }
}