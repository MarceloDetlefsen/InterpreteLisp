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
 * File Name: Environment.java
 * Descripción: Clase que representa el entorno de ejecución para el intérprete LISP.
 * 
 * Implementación basada en el diseño del intérprete proporcionado en el diagrama UML.
 */

/**
 * Interfaz que define el comportamiento de un ámbito contextual.
 */
interface ContextualScope {
    /**
     * Establece una variable en el ámbito actual.
     * 
     * @param name El nombre de la variable
     * @param value El valor de la variable
     * @return void
     */
    void setVariable(String name, Object value);
    
    /**
     * Obtiene el valor de una variable buscando en el ámbito actual y en los ámbitos superiores.
     * 
     * @param name El nombre de la variable a buscar
     * @return El valor de la variable o null si no se encuentra
     */
    Object getVariable(String name);
    
    /**
     * Crea un nuevo ámbito que extiende a este ámbito.
     * 
     * @return Un nuevo ámbito con este ámbito como padre
     */
    ContextualScope createSubScope();
    
    /**
     * Revierte los cambios realizados en este ámbito.
     */
    void rollbackState();
}

/**
 * Implementación de un ámbito contextual para el intérprete LISP.
 */
public class Environment implements ContextualScope {
    /**
     * Mapa de variables en este ámbito.
     */
    private Map<String, Object> variables;
    
    /**
     * Ámbito padre, puede ser nulo para el ámbito global.
     */
    private ContextualScope parentScope;
    
    /**
     * Constructor para crear un ámbito global (sin padre).
     */
    public Environment() {
        this.variables = new HashMap<>();
        this.parentScope = null;
    }
    
    /**
     * Constructor para crear un ámbito con un padre específico.
     * 
     * @param parentScope El ámbito padre
     */
    public Environment(ContextualScope parentScope) {
        this.variables = new HashMap<>();
        this.parentScope = parentScope;
    }
    
    @Override
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }
    
    @Override
    public Object getVariable(String name) {
        // Primero buscar en este ámbito
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        
        // Si no se encuentra y hay un ámbito padre, buscar ahí
        if (parentScope != null) {
            return parentScope.getVariable(name);
        }
        
        // No se encontró la variable
        return null;
    }
    
    @Override
    public ContextualScope createSubScope() {
        return new Environment(this);
    }
    
    @Override
    public void rollbackState() {
        // Implementación simple: vaciar las variables de este ámbito
        variables.clear();
    }
    
    /**
     * Inicializa el entorno con funciones y constantes predefinidas de LISP.
     */
    public void initializeBuiltins() {
        // Constantes predefinidas
        setVariable("T", true);
        setVariable("NIL", null);
        
        // Aquí se pueden agregar más funciones predefinidas según sea necesario
    }
    
    /**
     * Establece las variables de ejecución para las funciones del sistema.
     * 
     * @param name El nombre de la función
     * @param implementation La implementación de la función
     */
    public void defineSystemFunction(String name, Object implementation) {
        setVariable(name, implementation);
    }
    
    /**
     * Carga definiciones desde un archivo o entrada de texto.
     * 
     * @param input La entrada que contiene las definiciones
     * @param evaluator El evaluador para procesar las definiciones
     */
    public void loadDefinitions(String input, Evaluator evaluator) {
        // Este método podría implementarse para cargar definiciones predefinidas
        // Por ahora, lo dejamos como un esqueleto
        
        // 1. Tokenizar el input
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.tokenize(input);
        
        // 2. Parsear los tokens a AST
        Parser parser = new Parser(tokens);
        List<ASTNode> definitions = parser.parse();
        
        // 3. Evaluar cada definición
        for (ASTNode def : definitions) {
            evaluator.evaluate(def, this);
        }
    }
}