package com.InterpreteLisp;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Universidad del Valle de Guatemala
 * Algoritmos y Estructuras de Datos
 * Ing. Douglas Barrios
 * @author: Marcelo Detlefsen, Jose Rivera, Fabián Prado
 * Creación: 20/03/2025
 * última modificación: 20/03/2025
 * File Name: MainTest.java
 * Descripción: JUnit Class para comprobar que los métodos de Main.java funcionan correctamente
 */
public class MainTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }
    
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }
    
    /**
     * Test basico para main con una expresion simple
     */
    @Test
    public void testMainWithSimpleExpression() {
        String input = "(+ 2 3)\n\nexit\n\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        
        Main.main(new String[]{});
        
        String output = outContent.toString();
        assertTrue(output.contains("Resultado: 5"));
    }
    
    /**
     * Test para multiples expresiones 
     */
    @Test
    public void testMainWithMultipleExpressions() {
        String input = "(+ 2 3) (- 10 5)\n\nexit\n\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        
        Main.main(new String[]{});
        
        String output = outContent.toString();
        assertTrue(output.contains("Resultado: 5"));
        assertTrue(output.contains("Resultado: 5"));
    }
    
    /**
     * Test del manejo de expresiones invalidas
     */
    @Test
    public void testMainWithInvalidExpression() {
        String input = "(+ 2 3) esto_no_es_lisp (- 10 5)\n\nexit\n\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        
        Main.main(new String[]{});
        
        String output = outContent.toString();
        assertTrue(output.contains("no es una expresión LISP válida"));
        assertTrue(output.contains("Resultado: 5")); 
    }
    
    /**
     * Test de definicion y ejecucion de la funcion de usario Fibonacci
     */
    @Test
    public void testMainDefunFibonacci() {
        String input = "(DEFUN FIBONACCI (N) " +
              "(COND ((= N 0) 1) " +
              "((= N 1) 1) " +
              "(T (+ (FIBONACCI (- N 1)) (FIBONACCI (- N 2)))))))\n\n" +
              "(FIBONACCI 4)\n\nexit\n\n";

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Main.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Resultado: 5")); 

        assertTrue(output.contains("FIBONACCI"));
    }
}