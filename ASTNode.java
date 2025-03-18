import java.util.ArrayList;
import java.util.List;

/*
 * Universidad del Valle de Guatemala
 * Algoritmos y Estructuras de Datos
 * Ing. Douglas Barrios
 * @author: Marcelo Detlefsen, Jose Rivera, Fabián Prado
 * Creación: 11/03/2025
 * última modificación: 11/03/2025
 * File Name: ASTNode.java
 * Descripción: Clase que se encarga de almacenar los nodos del árbol de sintaxis abstracta.
 */

public class ASTNode 
{
    /**
     * Valor del nodo. Puede representar un operador, número o símbolo.
     */
    private String value;
    /**
     * Lista de nodos hijos de este nodo.
     */
    private List<ASTNode> children;

    /*
     * constructor
     */
    public ASTNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    /**
     * agrega un nodo hijo a la lista de este nodo.
     * @param child El nodo hijo a agregar
     */
    public void addChild(ASTNode child) {
        this.children.add(child);
    }

    /**
     * Obtiene el valor del nodo.
     *
     * @return El valor del nodo como una cadena de texto.
     */
    public String getValue() {
        return value;
    }

    /**
     * Obtiene la lista de nodos hijos de este nodo.
     *
     * @return Una lista de nodos hijos.
     */
    public List<ASTNode> getChildren() {
        return children;
    }

    /**
     * Devuelve una representación en forma de cadena del nodo y sus hijos.
     *
     * @return Una representación en cadena del nodo en formato de árbol.
     */
    @Override
    public String toString() {
        if (children.isEmpty()) {
            return value;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(value);
            for (ASTNode child : children) {
                sb.append(" ").append(child.toString());
            }
            sb.append("]");
            return sb.toString();
        }
    }
}