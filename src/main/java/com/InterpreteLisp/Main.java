package com.InterpreteLisp;

import java.util.List;
import java.util.Scanner;

/*
 * Universidad del Valle de Guatemala
 * Algoritmos y Estructuras de Datos
 * Ing. Douglas Barrios
 * @author: Marcelo Detlefsen, Jose Rivera, Fabián Prado
 * Creación: 11/03/2025
 * última modificación: 20/03/2025
 * File Name: Main.java
 * Descripción: Clase principal que utiliza el Lexer, Parser y Evaluator para:
 * 1. Tokenizar la expresión LISP.
 * 2. Verificar si la expresión está balanceada.
 * 3. Crear un AST a partir de los tokens.
 * 4. Evaluar el AST en un entorno de ejecución.
 */

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder codeBuilder = new StringBuilder();
        
        // Crear el entorno y el evaluador
        Environment globalEnv = new Environment();
        initializeBuiltins(globalEnv);
        Evaluator evaluator = new Evaluator();

        System.out.println("\nBienvenido al Intérprete LISP");
        System.out.println("Este intérprete posee: operaciones aritméticas, QUOTE, DEFUN, SETQ, predicados (ATOM, LIST, EQUAL, <, >), COND y paso de parámetros.");
        System.out.println("Funcionamiento: Ingrese la expresión LISP a ejecutar, es importante considerar que puede ejecutar varias líneas");
        System.out.println("Si así lo desea, presione 'Enter' en la linea terminada en ')' para ejecutar la expresión. y pasar a la siguiente");
        System.out.println("Nota: Para salir del intérprete debe escribir 'exit' o 'salir'.");

        // Ciclo principal del intérprete
        while (true) {
            System.out.print("\nlisp> ");
            codeBuilder.setLength(0); // Limpiar el StringBuilder
            
            // Leer múltiples líneas de entrada
            while (true) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    break;
                }
                codeBuilder.append(line).append(" ");
            }

            String fullInput = codeBuilder.toString().trim();
            
            // Comandos para salir del intérprete
            if (fullInput.equalsIgnoreCase("exit") || fullInput.equalsIgnoreCase("salir")) {
                System.out.println("¡Gracias por utilizar este intérprete Lisp, espero te haya sido útil!");
                break;
            }
            
            // Si no hay entrada, continuar
            if (fullInput.isEmpty()) {
                continue;
            }
            
            // Verificar si hay contenido en la entrada que no se pudo parsear como expresiones válidas
            Lexer lexerForFullInput = new Lexer();
            if (!lexerForFullInput.isBalanced(fullInput)) {
                System.out.println("\nAdvertencia: La entrada completa contiene paréntesis desbalanceados.");
                System.out.println("Se intentarán procesar las expresiones válidas que se puedan identificar.");
            }
            
            // Dividir la entrada en expresiones individuales basadas en paréntesis de nivel superior
            List<String> expressions = lexerForFullInput.splitExpressions(fullInput);
            
            // Verificar si se encontraron expresiones válidas
            if (expressions.isEmpty() && !fullInput.isEmpty()) {
                System.out.println("\nError: No se pudieron identificar expresiones LISP válidas en la entrada.");
                continue;
            }
            
            // Identificar contenido que no se pudo parsear como expresión válida
            String remainingContent = lexerForFullInput.findInvalidContent(fullInput, expressions);
            if (!remainingContent.trim().isEmpty()) {
                // Solo mostramos mensaje de error si hay contenido válido aparte de espacios
                String trimmedContent = remainingContent.trim();
                if (!trimmedContent.isEmpty() && !trimmedContent.equals(" ")) {
                    System.out.println("\nLa siguiente entrada no es una expresión LISP válida: " + trimmedContent);
                }
            }
            
            // Procesar cada expresión individualmente
            for (String expression : expressions) {
                System.out.println("\nProcesando expresión: " + expression);
                
                // Validar que la expresión contenga paréntesis
                if (!expression.contains("(") || !expression.contains(")")) {
                    System.out.println("Error: La expresión '" + expression + "' no es una expresión válida en LISP (falta de paréntesis).");
                    continue;
                }

                Lexer lexer = new Lexer();
                
                // Verificar balance de paréntesis para esta expresión específica
                boolean balanced = lexer.isBalanced(expression);
                if (!balanced) {
                    System.out.println("Error: La expresión '" + expression + "' no es válida. Tiene paréntesis desbalanceados.");
                    continue;
                }
                
                // Tokenizar y parsear si la expresión es válida
                List<Token> tokens = lexer.tokenize(expression);
                
                // Validar que la expresión tenga tokens
                if (tokens.isEmpty()) {
                    System.out.println("Error: La expresión '" + expression + "' no es válida. No se encontraron tokens.");
                    continue;
                }
                
                // Mostrar la lista de tokens
                System.out.print("Lista de elementos (tokens): ");
                for (int i = 0; i < tokens.size(); i++) {
                    System.out.print("[" + tokens.get(i).getValue() + "]");
                    if (i < tokens.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
                
                // Validar que la expresión sea una expresión LISP válida
                if (!lexer.isValidExpression(tokens, globalEnv)) {
                    System.out.println("Error: La expresión '" + expression + "' no es una expresión LISP válida. Debe contener al menos un operador.");
                    continue;
                }
                
                // Crear el AST si hay tokens
                try {
                    Parser parser = new Parser(tokens);
                    List<ASTNode> astList = parser.parse();
                    if (astList.isEmpty()) {
                        System.out.println("Error: No se pudo generar un AST para la expresión '" + expression + "'.");
                    } else {
                        for (ASTNode ast : astList) {
                            System.out.println("Árbol de sintaxis abstracta (AST): " + ast.toString());
                            
                            // Evaluar el AST
                            try {
                                Object result = evaluator.evaluate(ast, globalEnv);
                                System.out.println("Resultado: " + formatResult(result));
                            } catch (Exception e) {
                                System.out.println("Error durante la evaluación: " + e.getMessage());
                            }
                        }
                    }
                } catch (RuntimeException e) {
                    System.out.println("Error al parsear la expresión '" + expression + "': " + e.getMessage());
                }
            }
        }
        
        scanner.close();
    }
    
    /**
     * Inicializa las funciones integradas del intérprete LISP.
     * 
     * @param env El entorno global donde se registrarán las funciones
     */
    private static void initializeBuiltins(Environment env) {
        // Constantes predefinidas (ya inicializadas en Environment.initializeBuiltins())
        env.initializeBuiltins();
        
        // Operaciones aritméticas - Ya están implementadas en Evaluator.evaluate()
        // pero necesitamos registrarlas como funciones del sistema
        env.defineSystemFunction("+", new BuiltinFunction("+"));
        env.defineSystemFunction("-", new BuiltinFunction("-"));
        env.defineSystemFunction("*", new BuiltinFunction("*"));
        env.defineSystemFunction("/", new BuiltinFunction("/"));
        env.defineSystemFunction("=", new BuiltinFunction("="));
        
        // Predicados
        env.defineSystemFunction("ATOM", new BuiltinFunction("ATOM"));
        env.defineSystemFunction("LIST", new BuiltinFunction("LIST"));
        env.defineSystemFunction("EQUAL", new BuiltinFunction("EQUAL"));
        env.defineSystemFunction("<", new BuiltinFunction("<"));
        env.defineSystemFunction(">", new BuiltinFunction(">"));
        
        // Operaciones especiales
        env.defineSystemFunction("QUOTE", new BuiltinFunction("QUOTE"));
        env.defineSystemFunction("'", new BuiltinFunction("QUOTE")); // Alias para QUOTE
        env.defineSystemFunction("SETQ", new BuiltinFunction("SETQ"));
        env.defineSystemFunction("DEFUN", new BuiltinFunction("DEFUN"));
        env.defineSystemFunction("COND", new BuiltinFunction("COND"));
        
        // Funciones auxiliares
        env.defineSystemFunction("PRINT", new BuiltinFunction("PRINT") {
            public Object execute(List<Object> args) {
                if (args.isEmpty()) {
                    System.out.println("NIL");
                    return null;
                }
                System.out.println(formatResult(args.get(0)));
                return args.get(0);
            }
        });
    }
    
    /**
     * Clase interna para representar funciones integradas del sistema.
     */
    private static class BuiltinFunction {
        private final String name;
        
        public BuiltinFunction(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return "#<BUILTIN-FUNCTION:" + name + ">";
        }
    }
    
    /**
     * Formatea el resultado de la evaluación para mostrarlo al usuario.
     * 
     * @param result El resultado a formatear
     * @return Una representación en cadena del resultado
     */
    private static String formatResult(Object result) {
        if (result == null) {
            return "NIL";
        } else if (result instanceof Boolean) {
            return ((Boolean) result) ? "T" : "NIL";
        } else if (result instanceof ASTNode) {
            return formatASTNode((ASTNode) result);
        } else if (result instanceof Double) {
            double d = (Double) result;
            // Si es un número entero, mostrarlo sin decimal
            if (d == Math.floor(d) && !Double.isInfinite(d)) {
                return Integer.toString((int) d);
            }
        }
        return result.toString();
    }
    
    /**
     * Formatea un nodo AST para mostrarlo al usuario.
     * Especialmente útil para representar resultados de QUOTE.
     * 
     * @param node El nodo AST a formatear
     * @return Una representación en cadena del nodo AST
     */
    private static String formatASTNode(ASTNode node) {
        if (node == null) {
            return "NIL";
        }
        
        String value = node.getValue();
        List<ASTNode> children = node.getChildren();
        
        if (children.isEmpty()) {
            return value;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        
        // Si es un nodo regular, mostrar su valor
        sb.append(value);
        
        // Mostrar los hijos con espacios
        for (ASTNode child : children) {
            sb.append(" ").append(formatASTNode(child));
        }
        
        sb.append(")");
        return sb.toString();
    }
}