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

// Interfaz que define el comportamiento de un ámbito contextual.
interface ContextualScope {

    void setVariable(String name, Object value);
    Object getVariable(String name);
    ContextualScope createSubScope();
    void rollbackState();
}

// Implementación de un ámbito contextual para el intérprete LISP.
public class Environment implements ContextualScope {

    private Map<String, Object> variables;
    private ContextualScope parentScope;
    public Environment() {
        this.variables = new HashMap<>();
        this.parentScope = null;
    }
    
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
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        
        if (parentScope != null) {
            return parentScope.getVariable(name);
        }

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
    
    public void initializeBuiltins() {
        // Constantes predefinidas
        setVariable("T", true);
        setVariable("NIL", null);
        // Aquí se pueden agregar más funciones predefinidas según sea necesario
    }
    
    public void defineSystemFunction(String name, Object implementation) {
        setVariable(name, implementation);
    }
    

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