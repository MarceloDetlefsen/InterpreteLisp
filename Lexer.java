import java.util.*;
import java.util.regex.*;

/*
 * Universidad del Valle de Guatemala
 * Algoritmos y Estructuras de Datos
 * Ing. Douglas Barrios
 * @author: Marcelo Detlefsen, Jose Rivera, Fabián Prado
 * Creación: 01/03/2025
 * última modificación: 20/03/2025
 * File Name: Lexer.java
 * Descripción: Clase que se encarga de analizar la expresión LISP.
 * 
 * Implementación basada en el diseño del intérprete proporcionado en el diagrama UML.
 * Código original generado con la asistencia de ChatGPT.
 */

public class Lexer {
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
        Matcher matcher = Pattern.compile("\\(|\\)|[a-zA-Z]+|[0-9]+(\\.[0-9]+)?|[-+*/=']|<|>").matcher(code);
        while (matcher.find()) {
            tokens.add(new Token(matcher.group()));
        }
        return tokens;
    }

    // /**
    //  * Corrige errores en la lista de tokens.
    //  * Este método se implementará en futuras versiones.
    //  *
    //  * @param tokens Lista de tokens que posiblemente contiene errores
    //  * @return Lista de tokens corregida
    //  */
    // public List<Token> fixErrors(List<Token> tokens) {
    //     // Este método se implementará más adelante, para el propósito de esta entrega no es necesario.
    //     return tokens;
    // }

    // /**
    //  * Analiza la intención del usuario basado en los tokens.
    //  * Este método se implementará en futuras versiones.
    //  *
    //  * @param tokens Lista de tokens a analizar
    //  * @return Lista de tokens procesada según la intención detectada
    //  */
    // public List<Token> detectIntent(List<Token> tokens) {
    //     // Este método se implementará más adelante, para el propósito de esta entrega no es necesario.
    //     return tokens;
    // }

    /**
     * Verifica si los paréntesis en el código están balanceados.
     * Un código balanceado tiene el mismo número de paréntesis de apertura y cierre,
     * y no hay paréntesis de cierre antes de un paréntesis de apertura correspondiente.
     * Ahora también maneja el caso especial de comillas simples (') adecuadamente.
     *
     * @param code La cadena de código a verificar
     * @return true si los paréntesis están balanceados, false en caso contrario
     */
    public boolean isBalanced(String code) {
        // Preprocesar el código para manejar las comillas simples como notación abreviada de QUOTE
        String preprocessedCode = preprocessQuotes(code);
        int balance = 0;
        for (char c : preprocessedCode.toCharArray()) {
            if (c == '(') balance++;
            else if (c == ')') balance--;
            if (balance < 0) return false;
        }
        return balance == 0; // si al final el balance es cero, los paréntesis están equilibrados
    }

    /**
     * Preprocesa el código para manejar las comillas simples (') de manera especial.
     * Convierte '(expr) en una forma que se pueda verificar balanceada.
     *
     * @param code El código original
     * @return El código preprocesado
     */
    private String preprocessQuotes(String code) {
        // Tratar las comillas simples como si no afectaran el balance de paréntesis
        return code.replace("'", "");
    }

    /**
     * Verifica si una lista de tokens representa una expresión LISP válida.
     * Una expresión LISP válida debe contener al menos un paréntesis y un operador,
     * ahora ya permite expresiones que comienzan con comilla simple ('), 
     * ademas de hacer una excepción para funciones definidas por el usuario.
     *
     * @param tokens Lista de tokens a verificar
     * @return true si la expresión es válida, false en caso contrario
     */
    public boolean isValidExpression(List<Token> tokens, Environment env) {
        // Si comienza con una comilla simple, consideramos que es una expresión de QUOTE válida
        if (tokens.size() > 0 && tokens.get(0).getValue().equals("'")) {
            return true;
        }
        
        // Verificar que haya al menos 3 tokens: paréntesis abierto, operador/función y paréntesis cerrado
        if (tokens.size() < 3) {
            return false;
        }
    
        // El primer token debe ser un paréntesis abierto
        if (!tokens.get(0).getValue().equals("(")) {
            return false;
        }
    
        // El último token debe ser un paréntesis cerrado
        if (!tokens.get(tokens.size() - 1).getValue().equals(")")) {
            return false;
        }
    
        // El segundo token debe ser un operador, función especial o identificador de función definida por el usuario
        String secondToken = tokens.get(1).getValue();
    
        // Lista de operadores y funciones especiales válidas
        Set<String> validOperators = new HashSet<>(Arrays.asList(
            "+", "-", "*", "/",         // Operaciones aritméticas
            "QUOTE", "'",               // Quote
            "SETQ",                     // Asignación
            "DEFUN",                    // Definición de funciones
            "ATOM", "LIST", "EQUAL", "<", ">", // Predicados
            "COND",                     // Condicional
            "PRINT"                     // Función auxiliar
        ));
    
        // Verificar si el segundo token es un operador o función especial
        if (validOperators.contains(secondToken)) {
            return true;
        }
    
        // Verificar si el segundo token es una función definida por el usuario
        if (env != null && env.getVariable(secondToken) != null) {
            return true;
        }
    
        // Si no es un operador o función especial, verificar si hay al menos un operador en la expresión
        String operators = "+-*/";
        boolean hasOperators = false;
        for (Token token : tokens) {
            String value = token.getValue();
            if (operators.contains(value) || value.equals("<") || value.equals(">")) {
                hasOperators = true;
                break;
            }
        }
    
        // La expresión es válida si tiene al menos un operador o si es una función definida por el usuario
        return hasOperators;
    }
    
    /**
     * Sobrecarga del método isValidExpression para mantener compatibilidad
     * con código existente que no pasa el entorno.
     */
    public boolean isValidExpression(List<Token> tokens) {
        return isValidExpression(tokens, null);
    }

    /**
     * Divide la entrada en expresiones LISP de nivel superior individuales.
     * Cada expresión comienza con '(' y termina con su ')' correspondiente.
     * También maneja expresiones que comienzan con comilla simple (').
     *
     * @param input La cadena de entrada que contiene una o más expresiones LISP
     * @return Una lista de expresiones LISP individuales
     */
    public List<String> splitExpressions(String input) {
        List<String> result = new ArrayList<>();
        int i = 0;
        
        while (i < input.length()) {
            char c = input.charAt(i);
            
            // Saltarse espacios en blanco
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }
            
            // Manejar expresiones que comienzan con comilla simple
            if (c == '\'') {
                int start = i;
                i++; // Avanzar después de la comilla
                
                // Verificar si hay una expresión después de la comilla
                while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
                    i++; // Saltarse espacios en blanco
                }
                
                if (i < input.length() && input.charAt(i) == '(') {
                    // Si hay un paréntesis abierto, encontrar su paréntesis de cierre correspondiente
                    int depth = 1;
                    i++; // Saltarse el paréntesis abierto
                    
                    while (i < input.length() && depth > 0) {
                        if (input.charAt(i) == '(') depth++;
                        else if (input.charAt(i) == ')') depth--;
                        i++;
                    }
                    
                    if (depth == 0) {
                        // Se encontró una expresión '(...) completa
                        result.add(input.substring(start, i));
                    }
                } else {
                    // Si hay un símbolo después de la comilla (como 'a)
                    while (i < input.length() && !Character.isWhitespace(input.charAt(i)) && 
                           input.charAt(i) != '(' && input.charAt(i) != ')') {
                        i++;
                    }
                    result.add(input.substring(start, i));
                }
                continue;
            }
            
            // Manejar expresiones que comienzan con paréntesis
            if (c == '(') {
                int start = i;
                int depth = 1;
                i++; // Avanzar después del paréntesis abierto
                
                while (i < input.length() && depth > 0) {
                    if (input.charAt(i) == '(') depth++;
                    else if (input.charAt(i) == ')') depth--;
                    i++;
                }
                
                if (depth == 0) {
                    // Se encontró una expresión (...) completa
                    result.add(input.substring(start, i));
                }
                continue;
            }
            // Avanzar si se encuentra otro carácter
            i++;
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