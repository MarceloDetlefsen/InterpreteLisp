package com.InterpreteLisp;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


/*
 * Universidad del Valle de Guatemala
 * Algoritmos y Estructuras de Datos
 * Ing. Douglas Barrios
 * @author: Marcelo Detlefsen, Jose Rivera, Fabián Prado
 * Creación: 20/03/2025
 * última modificación: 20/03/2025
 * File Name: EnvironmentTest.java
 * Descripción: JUnit Class para comprobar que los metodos de Environment.java funcionan correctamente
 * 
 * Código original generado con la asistencia de Claude y Deepseek.
 */
public class EnvironmentTest {
    
    private Environment globalEnv;
    private Environment localEnv;
    
    @Before
    public void setUp() {
        globalEnv = new Environment(); 
        globalEnv.initializeBuiltins(); 
        localEnv = new Environment(globalEnv); 
    }
    
    /**
     * Test de los setters y getters de una variable en un mismo environment
    */
    @Test
    public void testSetAndGetVariable() {
        // Test de un entero
        globalEnv.setVariable("x", 10);
        assertEquals(10, globalEnv.getVariable("x"));
        
        // Test de un string
        globalEnv.setVariable("y", "Prueba");
        assertEquals("Prueba", globalEnv.getVariable("y"));
        
        // Test de valor null 
        globalEnv.setVariable("z", null);
        assertNull(globalEnv.getVariable("z"));
    }
    
    /**
     * Test de la herencia del Parent Scope con Scopes locales 
    */
    @Test
    public void testVariableScopeInheritance() {
        globalEnv.setVariable("varGlobal", 100);
        
        assertEquals(100, localEnv.getVariable("varGlobal"));
        
        localEnv.setVariable("varLocal", 200);
        
        assertEquals(200, localEnv.getVariable("varLocal"));
        
        assertNull(globalEnv.getVariable("varLocal"));
        
        globalEnv.setVariable("varAmbos", "global");
        localEnv.setVariable("varAmbos", "local");
        
        assertEquals("local", localEnv.getVariable("varAmbos"));
        assertEquals("global", globalEnv.getVariable("varAmbos"));
    }
    
    /**
     * Test del metodo Rollback y su funcionamiento entre scope local y global
    */
    @Test
    public void testRollbackState() {
        localEnv.setVariable("x", 1);
        localEnv.setVariable("y", 2);
        globalEnv.setVariable("z", 3);
        
        assertEquals(1, localEnv.getVariable("x"));
        assertEquals(2, localEnv.getVariable("y"));
        assertEquals(3, localEnv.getVariable("z"));
        
        localEnv.rollbackState();
        
        assertNull(localEnv.getVariable("x"));
        assertNull(localEnv.getVariable("y"));
        assertEquals(3, localEnv.getVariable("z"));
    }
    
    /**
     * Test de la existencia de T y NIL
    */
    @Test
    public void testInitializeBuiltins() {
        assertEquals(true, globalEnv.getVariable("T"));
        assertNull(globalEnv.getVariable("NIL"));
        
        globalEnv.defineSystemFunction("Prueba", "Implementacion");
        assertEquals("Implementacion", globalEnv.getVariable("Prueba"));
    }
    
    /**
     * Test de una del valor de una variable usando un Evaluator
    */
    @Test
    public void testLoadDefinitions() {
        Evaluator evaluator = new Evaluator();
        
        String input = "(SETQ x 42)";
        
        globalEnv.loadDefinitions(input, evaluator);
        
        assertEquals(42.0, globalEnv.getVariable("x"));
    }

}