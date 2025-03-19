import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Universidad del Valle de Guatemala
 * Algoritmos y Estructuras de Datos
 * Ing. Douglas Barrios
 * @author: Marcelo Detlefsen, Jose Rivera, Fabián Prado
 * Creación: 17/03/2025
 * última modificación: 17/03/2025
 * File Name: Evaluator.java
 * Descripción: Clase que se encarga de evaluar el árbol de sintaxis abstracta.
 * 
 * Implementación basada en el diseño del intérprete proporcionado en el diagrama UML.
 */

public class Evaluator {
    /**
     * Capa actual de evaluación para el manejo de funciones anidadas.
     */
    private int currentLayer;
    
    /**
     * Cache de evaluación para optimizar el proceso de evaluación.
     */
    private Map<ASTNode, Object> evaluationCache;
    
    /**
     * Constructor para el evaluador.
     * Inicializa la capa de evaluación y el cache.
     */
    public Evaluator() {
        this.currentLayer = 0;
        this.evaluationCache = new HashMap<>();
    }
    
    /**
     * Evalúa un nodo AST en un contexto dado.
     * 
     * @param ast El nodo AST a evaluar
     * @param scope El ámbito contextual para la evaluación
     * @return El resultado de la evaluación
     */
    public Object evaluate(ASTNode ast, ContextualScope scope) {
        // Si ya evaluamos este nodo anteriormente en el mismo contexto, devolver el resultado cacheado
        if (evaluationCache.containsKey(ast)) {
            return evaluationCache.get(ast);
        }
        
        String value = ast.getValue();
        List<ASTNode> children = ast.getChildren();
        
        // Evaluar expresiones basadas en su operador/valor
        if (value.equals("QUOTE")) {
            // QUOTE devuelve su argumento sin evaluarlo
            if (children.size() != 1) {
                throw new RuntimeException("QUOTE requiere exactamente un argumento");
            }
            return children.get(0);
        } else if (value.equals("SETQ")) {
            // SETQ asigna un valor a una variable
            if (children.size() != 2) {
                throw new RuntimeException("SETQ requiere exactamente dos argumentos");
            }
            String varName = children.get(0).getValue();
            Object varValue = evaluate(children.get(1), scope);
            scope.setVariable(varName, varValue);
            return varValue;
        } else if (value.equals("DEFUN")) {
            // DEFUN define una nueva función
            if (children.size() < 3) {
                throw new RuntimeException("DEFUN requiere al menos tres argumentos");
            }
            String funcName = children.get(0).getValue();
            ASTNode params = children.get(1);
            
            // Los nodos restantes forman el cuerpo de la función
            List<ASTNode> body = new ArrayList<>();
            for (int i = 2; i < children.size(); i++) {
                body.add(children.get(i));
            }
            
            // Crear y guardar la función en el ámbito actual
            Function function = new Function(params, body, scope);
            scope.setVariable(funcName, function);
            
            return funcName;
        } else if (value.equals("COND")) {
            // COND evalúa condiciones en orden y devuelve el primer resultado para una condición verdadera
            for (ASTNode clause : children) {
                List<ASTNode> clauseChildren = clause.getChildren();
                if (clauseChildren.size() < 2) {
                    throw new RuntimeException("Cada cláusula de COND debe tener al menos una condición y un resultado");
                }
                
                // Evaluar la condición
                Object condition = evaluate(clauseChildren.get(0), scope);
                if (isTruthy(condition)) {
                    // Si la condición es verdadera, evaluar y devolver el resultado
                    return evaluate(clauseChildren.get(1), scope);
                }
            }
            // Si ninguna condición es verdadera, devolver nil
            return null;
        } else if (value.equals("ATOM")) {
            // ATOM verifica si el argumento es un átomo (no es una lista)
            if (children.size() != 1) {
                throw new RuntimeException("ATOM requiere exactamente un argumento");
            }
            Object result = evaluate(children.get(0), scope);
            return !(result instanceof List);
        } else if (value.equals("LIST")) {
            // LIST verifica si el argumento es una lista
            if (children.size() != 1) {
                throw new RuntimeException("LIST requiere exactamente un argumento");
            }
            Object result = evaluate(children.get(0), scope);
            return result instanceof List;
        } else if (value.equals("EQUAL")) {
            // EQUAL compara dos valores
            if (children.size() != 2) {
                throw new RuntimeException("EQUAL requiere exactamente dos argumentos");
            }
            Object val1 = evaluate(children.get(0), scope);
            Object val2 = evaluate(children.get(1), scope);
            return val1.equals(val2);
        } else if (value.equals("<")) {
            // < compara si el primero es menor que el segundo
            if (children.size() != 2) {
                throw new RuntimeException("< requiere exactamente dos argumentos");
            }
            Number val1 = (Number) evaluate(children.get(0), scope);
            Number val2 = (Number) evaluate(children.get(1), scope);
            return val1.doubleValue() < val2.doubleValue();
        } else if (value.equals(">")) {
            // > compara si el primero es mayor que el segundo
            if (children.size() != 2) {
                throw new RuntimeException("> requiere exactamente dos argumentos");
            }
            Number val1 = (Number) evaluate(children.get(0), scope);
            Number val2 = (Number) evaluate(children.get(1), scope);
            return val1.doubleValue() > val2.doubleValue();
        } else if (value.equals("+")) {
            // + suma todos los argumentos
            double sum = 0;
            for (ASTNode child : children) {
                Object result = evaluate(child, scope);
                if (result instanceof Number) {
                    sum += ((Number) result).doubleValue();
                } else {
                    throw new RuntimeException("+ requiere argumentos numéricos");
                }
            }
            return sum;
        } else if (value.equals("-")) {
            // - resta los argumentos (el primero menos los demás)
            if (children.isEmpty()) {
                throw new RuntimeException("- requiere al menos un argumento");
            }
            
            Object first = evaluate(children.get(0), scope);
            if (!(first instanceof Number)) {
                throw new RuntimeException("- requiere argumentos numéricos");
            }
            
            double result = ((Number) first).doubleValue();
            
            if (children.size() == 1) {
                // Si solo hay un argumento, devolver su negativo
                return -result;
            }
            
            // Restar los demás argumentos
            for (int i = 1; i < children.size(); i++) {
                Object nextVal = evaluate(children.get(i), scope);
                if (nextVal instanceof Number) {
                    result -= ((Number) nextVal).doubleValue();
                } else {
                    throw new RuntimeException("- requiere argumentos numéricos");
                }
            }
            
            return result;
        } else if (value.equals("*")) {
            // * multiplica todos los argumentos
            double product = 1;
            for (ASTNode child : children) {
                Object result = evaluate(child, scope);
                if (result instanceof Number) {
                    product *= ((Number) result).doubleValue();
                } else {
                    throw new RuntimeException("* requiere argumentos numéricos");
                }
            }
            return product;
        } else if (value.equals("/")) {
            // / divide los argumentos (el primero entre los demás)
            if (children.size() < 2) {
                throw new RuntimeException("/ requiere al menos dos argumentos");
            }
            
            Object first = evaluate(children.get(0), scope);
            if (!(first instanceof Number)) {
                throw new RuntimeException("/ requiere argumentos numéricos");
            }
            
            double result = ((Number) first).doubleValue();
            
            for (int i = 1; i < children.size(); i++) {
                Object nextVal = evaluate(children.get(i), scope);
                if (nextVal instanceof Number) {
                    double divisor = ((Number) nextVal).doubleValue();
                    if (divisor == 0) {
                        throw new RuntimeException("División por cero");
                    }
                    result /= divisor;
                } else {
                    throw new RuntimeException("/ requiere argumentos numéricos");
                }
            }
            
            return result;
        } else {
            // Si no es un operador especial, buscar en el ámbito
            // Podría ser una variable, una función o un valor literal
            
            // Si es un número, devolver su valor
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                // No es un número
            }
            
            // Buscar en el ámbito si es una variable o función
            Object lookupResult = scope.getVariable(value);
            if (lookupResult != null) {
                if (lookupResult instanceof Object[]) {
                    // Si es una función, ejecutarla con los argumentos
                    Object[] funcData = (Object[]) lookupResult;
                    ASTNode params = (ASTNode) funcData[0];
                    List<ASTNode> body = (List<ASTNode>) funcData[1];
                    
                    // Evaluar los argumentos
                    List<Object> args = new ArrayList<>();
                    for (ASTNode child : children) {
                        args.add(evaluate(child, scope));
                    }
                    
                    // Crear un nuevo ámbito para la función
                    ContextualScope functionScope = scope.createSubScope();
                    
                    // Asignar los argumentos a los parámetros
                    List<ASTNode> paramNodes = params.getChildren();
                    if (args.size() != paramNodes.size()) {
                        throw new RuntimeException("Número incorrecto de argumentos: esperados " + 
                                                paramNodes.size() + ", recibidos " + args.size());
                    }
                    for (int i = 0; i < args.size(); i++) {
                        functionScope.setVariable(paramNodes.get(i).getValue(), args.get(i));
                    }
                    
                    // Evaluar el cuerpo de la función
                    Object result = null;
                    for (ASTNode bodyNode : body) {
                        result = evaluate(bodyNode, functionScope);
                    }
                    
                    return result;
                }
                // Si es una variable, devolver su valor
                return lookupResult;
            }
            
            // Si no se encuentra, devolver el valor como un símbolo
            if (children.isEmpty()) {
                return value;
            }
            
            // Si hay hijos pero no es una función conocida, es un error
            throw new RuntimeException("Función no definida: " + value);
        }
    }
    
    /**
     * Realiza análisis estático del AST para optimizaciones.
     * 
     * @param ast El nodo AST a analizar
     * @return El resultado del análisis
     */
    public String staticAnalysis(ASTNode ast) {
        // En implementaciones futuras, este método analizaría el AST para
        // detectar errores o realizar optimizaciones antes de la ejecución
        StringBuilder result = new StringBuilder();
        result.append("Análisis estático para: ").append(ast.toString()).append("\n");
        
        // Contar nodos
        int nodeCount = countNodes(ast);
        result.append("Total de nodos: ").append(nodeCount).append("\n");
        
        // Identificar operaciones costosas
        List<String> operations = findOperations(ast);
        result.append("Operaciones encontradas: ").append(operations).append("\n");
        
        return result.toString();
    }
    
    /**
     * Ejecuta una capa específica de evaluación.
     * Útil para controlar la evaluación de funciones anidadas.
     * 
     * @param ast El nodo AST a ejecutar
     * @param layer La capa de ejecución
     * @return El resultado de la ejecución
     */
    public Object executeLayer(ASTNode ast, int layer) {
        int originalLayer = this.currentLayer;
        this.currentLayer = layer;
        
        // Crear un nuevo ámbito para esta capa
        ContextualScope layerScope = new Environment();
        
        Object result;
        try {
            result = evaluate(ast, layerScope);
        } finally {
            // Restaurar la capa original
            this.currentLayer = originalLayer;
        }
        
        return result;
    }
    
    /**
     * Genera una representación textual del código a partir del AST.
     * 
     * @param ast El nodo AST a convertir en código
     * @return El código como cadena de texto
     */
    public String introspectCode(ASTNode ast) {
        if (ast == null) {
            return "nil";
        }
        
        String value = ast.getValue();
        List<ASTNode> children = ast.getChildren();
        
        if (children.isEmpty()) {
            return value;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(value);
        
        for (ASTNode child : children) {
            sb.append(" ").append(introspectCode(child));
        }
        
        sb.append(")");
        return sb.toString();
    }
    
    // Métodos auxiliares
    
    /**
     * Determina si un valor es considerado verdadero en el contexto de LISP.
     * 
     * @param value El valor a evaluar
     * @return true si el valor es considerado verdadero, false en caso contrario
     */
    private boolean isTruthy(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        }
        if (value instanceof String && ((String) value).isEmpty()) {
            return false;
        }
        if (value instanceof List && ((List<?>) value).isEmpty()) {
            return false;
        }
        return true;
    }
    
    /**
     * Ejecuta una función con los argumentos dados.
     * 
     * @param function La función a ejecutar
     * @param args Los argumentos para la función
     * @return El resultado de la ejecución
     */
    private Object executeFunction(Function function, List<Object> args) {
        // Crear un nuevo ámbito que extiende al ámbito léxico de la función
        ContextualScope functionScope = function.getParentScope().createSubScope();
        
        // Verificar que el número de argumentos sea correcto
        List<String> paramNames = function.getParamNames();
        if (args.size() != paramNames.size()) {
            throw new RuntimeException("Número incorrecto de argumentos: esperados " + 
                                       paramNames.size() + ", recibidos " + args.size());
        }
        
        // Asignar los argumentos a los parámetros
        for (int i = 0; i < args.size(); i++) {
            functionScope.setVariable(paramNames.get(i), args.get(i));
        }
        
        // Evaluar el cuerpo de la función
        Object result = null;
        for (ASTNode bodyNode : function.getBody()) {
            result = evaluate(bodyNode, functionScope);
        }
        
        return result;
    }
    
    /**
     * Cuenta el número de nodos en un AST.
     * 
     * @param ast El nodo raíz del AST
     * @return El número total de nodos
     */
    private int countNodes(ASTNode ast) {
        if (ast == null) {
            return 0;
        }
        
        int count = 1; // Contar este nodo
        
        for (ASTNode child : ast.getChildren()) {
            count += countNodes(child);
        }
        
        return count;
    }
    
    /**
     * Encuentra las operaciones utilizadas en un AST.
     * 
     * @param ast El nodo raíz del AST
     * @return Lista de operaciones encontradas
     */
    private List<String> findOperations(ASTNode ast) {
        List<String> operations = new ArrayList<>();
        findOperationsHelper(ast, operations);
        return operations;
    }
    
    /**
     * Método auxiliar para encontrar operaciones en un AST.
     * 
     * @param ast El nodo actual
     * @param operations Lista para almacenar las operaciones encontradas
     */
    private void findOperationsHelper(ASTNode ast, List<String> operations) {
        if (ast == null) {
            return;
        }
        
        String value = ast.getValue();
        if (value.equals("+") || value.equals("-") || value.equals("*") || value.equals("/") ||
            value.equals("SETQ") || value.equals("DEFUN") || value.equals("COND") ||
            value.equals("ATOM") || value.equals("LIST") || value.equals("EQUAL") ||
            value.equals("<") || value.equals(">")) {
            
            if (!operations.contains(value)) {
                operations.add(value);
            }
        }
        
        for (ASTNode child : ast.getChildren()) {
            findOperationsHelper(child, operations);
        }
    }
    
    /**
     * Clase interna para representar funciones de usuario.
     */
    private static class Function {
        private final ASTNode params;
        private final List<ASTNode> body;
        private final ContextualScope parentScope;
        
        public Function(ASTNode params, List<ASTNode> body, ContextualScope parentScope) {
            this.params = params;
            this.body = body;
            this.parentScope = parentScope;
        }
        
        public List<String> getParamNames() {
            List<String> paramNames = new ArrayList<>();
            for (ASTNode param : params.getChildren()) {
                paramNames.add(param.getValue());
            }
            return paramNames;
        }
        
        public List<ASTNode> getBody() {
            return body;
        }
        
        public ContextualScope getParentScope() {
            return parentScope;
        }
    }
}