import java.util.*;
import java.util.regex.*;

/*
 * Universidad del Valle de Guatemala
 * Algoritmos y Estructuras de Datos
 * Ing. Douglas Barrios
 * @author: Marcelo Detlefsen, Jose Rivera, Fabián Prado
 * Creación: 01/03/2025
 * última modificación: 16/03/2025
 * File Name: Lexer.java
 * Descripción: Clase que se encarga de analizar la expresión LISP.
 * 
 * Implementación basada en el diseño del intérprete proporcionado en el diagrama UML.
 * Código original generado con la asistencia de ChatGPT.
 */

public class Lexer 
{
    /**
     * Lista de tokens obtenidos durante el análisis léxico.
     */
    private List<Token> tokens;

    /**
     * Constructor de la clase Lexer.
     * Inicializa la lista de tokens vacía.
     */
    public Lexer() {
        this.tokens = new ArrayList<>();
    }

    /**
     * Divide una cadena de código en tokens según patrones definidos.
     * Reconoce paréntesis, palabras, números y operadores aritméticos.
     *
     * @param code La cadena de código LISP a analizar
     * @return Una lista de tokens extraídos del código
     */
    public List<Token> tokenize(String code) {
        tokens.clear();
        Matcher matcher = Pattern.compile("\\(|\\)|[a-zA-Z]+|[0-9]+|[-+*/]").matcher(code);
        while (matcher.find()) {
            tokens.add(new Token(matcher.group()));
        }
        return tokens;
    }

    /**
     * Corrige errores en la lista de tokens.
     * Este método se implementará en futuras versiones.
     *
     * @param tokens Lista de tokens que posiblemente contiene errores
     * @return Lista de tokens corregida
     */
    public List<Token> fixErrors(List<Token> tokens) {
        // Este método se implementará más adelante, para el propósito de esta entrega no es necesario.
        return tokens;
    }

    /**
     * Analiza la intención del usuario basado en los tokens.
     * Este método se implementará en futuras versiones.
     *
     * @param tokens Lista de tokens a analizar
     * @return Lista de tokens procesada según la intención detectada
     */
    public List<Token> detectIntent(List<Token> tokens) {
        // Este método se implementará más adelante, para el propósito de esta entrega no es necesario.
        return tokens;
    }

    /**
     * Verifica si los paréntesis en el código están balanceados.
     * Un código balanceado tiene el mismo número de paréntesis de apertura y cierre,
     * y no hay paréntesis de cierre antes de un paréntesis de apertura correspondiente.
     *
     * @param code La cadena de código a verificar
     * @return true si los paréntesis están balanceados, false en caso contrario
     */
    public boolean isBalanced(String code) {
        int balance = 0;
        for (char c : code.toCharArray()) {
            if (c == '(') balance++;
            else if (c == ')') balance--;
            if (balance < 0) return false;
        }
        return balance == 0; // si al final el balance es cero, los paréntesis están equilibrados
    }

    /**
     * Verifica si una lista de tokens representa una expresión LISP válida.
     * Una expresión LISP válida debe contener al menos un paréntesis y un operador.
     *
     * @param tokens Lista de tokens a verificar
     * @return true si la expresión es válida, false en caso contrario
     */
    public boolean isValidExpression(List<Token> tokens) {
        boolean hasParentheses = false;
        boolean hasOperators = false;
        String operators = "+-*/";

        for (Token token : tokens) {
            String value = token.getValue();
            if (value.equals("(") || value.equals(")")) {
                hasParentheses = true;
            } else if (operators.contains(value)) {
                hasOperators = true;
            }
        }
        return hasParentheses && hasOperators; //retorna verdadero si hay parentesis y operadores
    }
    
    /**
     * Divide la entrada en expresiones LISP de nivel superior individuales.
     * Cada expresión comienza con '(' y termina con su ')' correspondiente.
     * 
     * @param input La cadena de entrada que contiene una o más expresiones LISP
     * @return Una lista de expresiones LISP individuales
     */
    public List<String> splitExpressions(String input) {
        List<String> result = new ArrayList<>();
        int start = -1;
        int depth = 0;
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            if (c == '(') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth == 0 && start != -1) {
                    result.add(input.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Encuentra el contenido que no pudo ser procesado como expresiones LISP válidas.
     * Este método identifica contenido que no forma parte de ninguna expresión LISP válida.
     * 
     * @param fullInput La entrada completa
     * @param validExpressions Lista de expresiones válidas ya identificadas
     * @return El contenido que no forma parte de ninguna expresión válida
     */
    public String findInvalidContent(String fullInput, List<String> validExpressions) {
        // Creamos una copia del texto para marcar las partes válidas
        StringBuilder result = new StringBuilder(fullInput);
        
        // Ordenamos las expresiones por posición de inicio para procesarlas en orden
        Map<Integer, String> exprPositions = new HashMap<>();
        
        for (String expr : validExpressions) {
            int pos = fullInput.indexOf(expr);
            while (pos >= 0) {
                // Si la expresión está rodeada por espacios o está al principio/final
                boolean validStart = (pos == 0 || Character.isWhitespace(fullInput.charAt(pos - 1)));
                boolean validEnd = (pos + expr.length() == fullInput.length() || 
                                   Character.isWhitespace(fullInput.charAt(pos + expr.length())));
                
                if (validStart && validEnd) {
                    exprPositions.put(pos, expr);
                    break;
                }
                pos = fullInput.indexOf(expr, pos + 1);
            }
        }
        
        // Ordenamos las posiciones
        List<Integer> positions = new ArrayList<>(exprPositions.keySet());
        Collections.sort(positions);
        
        // Reemplazamos cada expresión con espacios para mantener los índices correctos
        for (Integer pos : positions) {
            String expr = exprPositions.get(pos);
            for (int i = 0; i < expr.length(); i++) {
                // Reemplazamos cada caracter con espacio para mantener la longitud
                result.setCharAt(pos + i, ' ');
            }
        }
        
        // Eliminamos espacios múltiples
        String invalidContent = result.toString().trim();
        invalidContent = invalidContent.replaceAll("\\s+", " ");
        
        return invalidContent;
    }
}