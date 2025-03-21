package com.InterpreteLisp;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;


/*
 * Universidad del Valle de Guatemala
 * Algoritmos y Estructuras de Datos
 * Ing. Douglas Barrios
 * @author: Marcelo Detlefsen, Jose Rivera, Fabián Prado
 * Creación: 20/03/2025
 * última modificación: 20/03/2025
 * File Name: LexerTest.java
 * Descripción: JUnit Class para comprobar que los metodos de Lexer.java funcionan correctamente
 * 
 * Código original generado con la asistencia de Claude y Deepseek.
 */
public class LexerTest {
    
    private Lexer lexer;
    private Environment testEnv;
    
    @Before
    public void setUp() {
        lexer = new Lexer();
        testEnv = new Environment();
        testEnv.initializeBuiltins();
    }
    
    /**
     * Test de la tokenizacion de una expresion
    */
    @Test
    public void testTokenize() {
        // (+ 2 3)
        String input = "(+ 2 3)";
        List<Token> tokens = lexer.tokenize(input);
        
        assertEquals(5, tokens.size());
        
        assertEquals("(", tokens.get(0).getValue());
        assertEquals("+", tokens.get(1).getValue());
        assertEquals("2", tokens.get(2).getValue());
        assertEquals("3", tokens.get(3).getValue());
        assertEquals(")", tokens.get(4).getValue());
        
        // Expresion mas compleja: (DEFUN sum (a b) (+ a b))
        input = "(DEFUN sum (a b) (+ a b))";
        tokens = lexer.tokenize(input);
        
        assertEquals(13, tokens.size());
        
        assertEquals("DEFUN", tokens.get(1).getValue());
        assertEquals("sum", tokens.get(2).getValue());
        assertEquals("(", tokens.get(3).getValue());
        assertEquals("a", tokens.get(4).getValue());
        assertEquals("b", tokens.get(5).getValue());
        assertEquals(")", tokens.get(6).getValue());
        assertEquals("(", tokens.get(7).getValue());
        assertEquals("+", tokens.get(8).getValue());
        assertEquals("a", tokens.get(9).getValue());
        assertEquals("b", tokens.get(10).getValue());
    }
    
    /**
     * Test de la verificacion de balance de una expresion
    */
    @Test
    public void testIsBalanced() {
        // Expresiones balanceadas
        assertTrue(lexer.isBalanced("(+ 2 3)"));
        assertTrue(lexer.isBalanced("(DEFUN sum (a b) (+ a b))"));
        assertTrue(lexer.isBalanced("(COND ((< a b) a) ((> a b) b) (T a))"));
        
        // Expresiones no balanceadas
        assertFalse(lexer.isBalanced("(+ 2 3"));
        assertFalse(lexer.isBalanced("(+ 2 3))"));
        assertFalse(lexer.isBalanced("((+ 2 3)"));
    }

    /**
     * Test de la verificacion si una expresion es valida
    */
    @Test
    public void testIsValidExpression() {
        // Expresiones validas
        List<Token> tokens = lexer.tokenize("(+ 2 3)");
        assertTrue(lexer.isValidExpression(tokens, testEnv));
        
        tokens = lexer.tokenize("(SETQ x 10)");
        assertTrue(lexer.isValidExpression(tokens, testEnv));
        
        tokens = lexer.tokenize("(DEFUN test (x) (+ x 1))");
        assertTrue(lexer.isValidExpression(tokens, testEnv));
        
        tokens = lexer.tokenize("'(a b c)");
        assertTrue(lexer.isValidExpression(tokens, testEnv));
        
        // Expresiones no validas
        tokens = lexer.tokenize("()");
        assertFalse(lexer.isValidExpression(tokens, testEnv));
        
        tokens = lexer.tokenize("+");
        assertFalse(lexer.isValidExpression(tokens, testEnv));
        
        // Test with environment having custom function
        Environment customEnv = new Environment();
        customEnv.setVariable("PRUEBA", "Implementacion");
        tokens = lexer.tokenize("(PRUEBA 1 2)");
        assertTrue(lexer.isValidExpression(tokens, customEnv));
    }
    
    /**
     * Test de metodo splitExpressions
    */
    @Test
    public void testSplitExpressions() {
        // Una sola expresion
        String input = "(+ 2 3)";
        List<String> expressions = lexer.splitExpressions(input);
        assertEquals(1, expressions.size());
        assertEquals("(+ 2 3)", expressions.get(0));
        
        // Multiples expresiones
        input = "(+ 2 3) (- 5 1) (* 2 4)";
        expressions = lexer.splitExpressions(input);
        assertEquals(3, expressions.size());
        assertEquals("(+ 2 3)", expressions.get(0));
        assertEquals("(- 5 1)", expressions.get(1));
        assertEquals("(* 2 4)", expressions.get(2));
        
        // Nested expresiones
        input = "(DEFUN sum (a b) (+ a b)) (sum 2 3)";
        expressions = lexer.splitExpressions(input);
        assertEquals(2, expressions.size());
        assertEquals("(DEFUN sum (a b) (+ a b))", expressions.get(0));
        assertEquals("(sum 2 3)", expressions.get(1));
        
        // Expresiones con usando '
        input = "'(a b c) (QUOTE 1 2 3)";
        expressions = lexer.splitExpressions(input);
        assertEquals(2, expressions.size());
        assertEquals("'(a b c)", expressions.get(0));
        assertEquals("(QUOTE 1 2 3)", expressions.get(1));
    }


    /**
     * Test de caracteres invalidos
    */
    @Test
    public void testFindInvalidContent() {
        // Contenido valido
        String input = "(+ 2 3) (- 5 1)";
        List<String> validExpressions = lexer.splitExpressions(input);
        String invalidContent = lexer.findInvalidContent(input, validExpressions);
        assertTrue(invalidContent.trim().isEmpty());
        
        // Contenido invalido
        input = "(+ 2 3) expresion-invalida (- 5 1)";
        validExpressions = lexer.splitExpressions(input);
        invalidContent = lexer.findInvalidContent(input, validExpressions);
        assertEquals("expresion-invalida", invalidContent.trim());
        
        // Expresion completamente invalida
        input = "Esto no es Lisp";
        validExpressions = lexer.splitExpressions(input);
        invalidContent = lexer.findInvalidContent(input, validExpressions);
        assertEquals("Esto no es Lisp", invalidContent.trim());
    }
}