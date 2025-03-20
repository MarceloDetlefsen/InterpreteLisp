import java.util.ArrayList;
import java.util.List;

/*
 * Universidad del Valle de Guatemala
 * Algoritmos y Estructuras de Datos
 * Ing. Douglas Barrios
 * @author: Marcelo Detlefsen, Jose Rivera, Fabián Prado
 * Creación: 11/03/2025
 * última modificación: 20/03/2025
 * File Name: Parser.java
 * Descripción: Clase que se encarga de analizar la expresión LISP.
 * 
 * Implementación siguiendo las especificaciones proporcionadas en la estructura UML y los archivos .java anteriores.
 * Código generado con la asistencia de DeepSeek.
 */

public class Parser 
{
    private List<Token> tokens;
    private int currentTokenIndex;

    /**
     * Constructor de la clase Parser.
     * @param tokens La lista de tokens a analizar.
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }

    /**
     * Método que inicia el análisis de la expresión LISP.
     * @return Una lista de nodos del árbol de sintaxis abstracta.
     */ 
    public List<ASTNode> parse() {
        List<ASTNode> expressions = new ArrayList<>();
        while (currentTokenIndex < tokens.size()) {
            if (peek().getValue().equals(")")) {
                // Si encontramos un paréntesis de cierre, lo consumimos y continuamos
                consume(")");
                continue;
            }
            expressions.add(parseExpression());
        }
        return expressions;
    }

    /**
     * Método que analiza una expresión LISP.
     * convierte las secuencias de tokens en un arbol de sintaxis abstracta
     * analiza si la expresion en un QUOTE o una operación algebraica 
     * @return node
     */
    private ASTNode parseExpression() {
        if (currentTokenIndex >= tokens.size()) {
            throw new RuntimeException("Unexpected end of input");
        }
        
        // Comprobar si la expresión comienza con comilla simple
        if (peek().getValue().equals("'")) {
            consume("'");
            ASTNode quoteNode = new ASTNode("QUOTE");
            
            // Si lo que sigue es un paréntesis, analizamos la expresión dentro
            if (peek().getValue().equals("(")) {
                quoteNode.addChild(parseExpression());
            } else {
                // Si no es un paréntesis, es un símbolo atomico
                quoteNode.addChild(parseAtom(consumeAny().getValue()));
            }
            
            return quoteNode;
        }
    
        if (!peek().getValue().equals("(")) {
            throw new RuntimeException("Expected '(' at position " + currentTokenIndex + " but found " + peek().getValue());
        }
    
        consume("(");
    
        // Si el siguiente token es ', lo convertimos en QUOTE
        if (peek().getValue().equals("'")) {
            consume("'");
            ASTNode quoteNode = new ASTNode("QUOTE");
            quoteNode.addChild(parseExpression());
            consume(")");
            return quoteNode;
        }
    
        if (peek().getValue().equals("QUOTE")) {
            consumeAny(); // Consume el token QUOTE
            ASTNode quoteNode = new ASTNode("QUOTE");
            
            if (peek().getValue().equals("(")) {
                quoteNode.addChild(parseExpression());
            } else {
                // Para casos como (QUOTE symbol)
                quoteNode.addChild(parseAtom(consumeAny().getValue()));
            }
    
            consume(")");
            return quoteNode;
        }
        // Manejo especial para COND
        else if (peek().getValue().equals("COND")) {
            Token condToken = consumeAny(); // Consume el token COND
            ASTNode condNode = new ASTNode(condToken.getValue());
            
            // Procesar cada cláusula del COND
            while (!peek().getValue().equals(")")) {
                // Cada cláusula debe comenzar con un paréntesis
                if (!peek().getValue().equals("(")) {
                    throw new RuntimeException("Expected '(' at start of COND clause");
                }
                
                consume("("); // Consumir el paréntesis de apertura de la cláusula
                
                // Crear un nodo para esta cláusula
                ASTNode clauseNode = new ASTNode("CLAUSE");
                
                // Procesar la condición de la cláusula
                if (peek().getValue().equals("(")) {
                    // La condición es una expresión entre paréntesis
                    clauseNode.addChild(parseExpression());
                } else {
                    // La condición es un átomo (como T)
                    clauseNode.addChild(parseAtom(consumeAny().getValue()));
                }
                
                // Procesar el resultado de la cláusula
                if (peek().getValue().equals("'")) {
                    // Si el resultado es una expresión quotada
                    consume("'");
                    ASTNode quoteNode = new ASTNode("QUOTE");
                    quoteNode.addChild(parseAtom(consumeAny().getValue()));
                    clauseNode.addChild(quoteNode);
                } else if (peek().getValue().equals("(")) {
                    // Si el resultado es una expresión entre paréntesis
                    clauseNode.addChild(parseExpression());
                } else {
                    // Si el resultado es un átomo
                    clauseNode.addChild(parseAtom(consumeAny().getValue()));
                }
                
                consume(")"); // Consumir el paréntesis de cierre de la cláusula
                condNode.addChild(clauseNode);
            }
            
            consume(")"); // Consumir el paréntesis de cierre del COND
            return condNode;
        }
        else { // si no es quote o COND: 
            Token firstToken = consumeAny();
            ASTNode node = new ASTNode(firstToken.getValue());
    
            while (!peek().getValue().equals(")")) {
                if (peek().getValue().equals("(")) {
                    node.addChild(parseExpression());
                } 
                else {
                    String rawValue = consumeAny().getValue();
                    node.addChild(parseAtom(rawValue));
                }
            }
    
            consume(")");
            return node; // devuelve el nodo principal
        }
    }

    /**
     * Método que analiza un átomo de la expresión LISP.
     * @param rawValue El valor del átomo.
     * @return Un nodo del árbol de sintaxis abstracta.
     */
    private ASTNode parseAtom(String rawValue) {
        try {
            int intValue = Integer.parseInt(rawValue);
            return new ASTNode(String.valueOf(intValue));
        } catch (NumberFormatException e1) {
            try {
                double doubleValue = Double.parseDouble(rawValue);
                return new ASTNode(String.valueOf(doubleValue));
            } catch (NumberFormatException e2) {
                return new ASTNode(rawValue);
            }
        }
    }

    /**
     * verifica si el token actual coincide con el valor esperado
     * si no coincide, lanza una excepción
     * si coincide avanza al siguiente token y lo devuelve
     * @param expectedValue
     * @return token
     */
    private Token consume(String expectedValue) {
        if (currentTokenIndex >= tokens.size()) {
            throw new RuntimeException("Expected " + expectedValue + " but reached end of input");
        }

        Token token = tokens.get(currentTokenIndex);
        if (!token.getValue().equals(expectedValue)) {
            throw new RuntimeException("Expected " + expectedValue + " but found " + token.getValue());
        }
        currentTokenIndex++;
        return token;
    }

    /**
     * devuelve el token actual y avanza al siguiente en la lista
     * @return tokens
     */
    private Token consumeAny() {
        if (currentTokenIndex >= tokens.size()) {
            throw new RuntimeException("Unexpected end of input");
        }

        return tokens.get(currentTokenIndex++);
    }

    /**
     * devuelve el token actual pero no avanza en la lista
     * @return
     */
    private Token peek() {
        if (currentTokenIndex >= tokens.size()) {
            throw new RuntimeException("Unexpected end of input");
        }

        return tokens.get(currentTokenIndex);
    }

    // /**
    //  * Optimiza el Árbol de Sintaxis Abstracta (AST) para mejorar su eficiencia.
    //  * @param ast El nodo raíz del AST a optimizar.
    //  * @return El AST optimizado.
    //  */
    // public ASTNode optimizeAST(ASTNode ast) {
    //     // Este método se implementará más adelante, para el propósito de esta entrega no es necesario.
    //     throw new UnsupportedOperationException("Método optimizeAST no implementado aún.");
    // }

    // /**
    //  * Extiende la sintaxis del AST para soportar características adicionales.
    //  * @param ast El nodo raíz del AST a extender.
    //  * @return El AST con la sintaxis extendida.
    //  */
    // public ASTNode extendSyntax(ASTNode ast) {
    //     // Este método se implementará más adelante, para el propósito de esta entrega no es necesario.
    //     throw new UnsupportedOperationException("Método extendSyntax no implementado aún.");
    // }
}