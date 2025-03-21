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
 * File Name: ParserTest.java
 * Descripción: JUnit Class para comprobar que los metodos de Parser.java funcionan correctamente
 * 
 * Código original generado con la asistencia de Claude y Deepseek.
 */
public class ParserTest {
    
    private Lexer lexer;
    
    @Before
    public void setUp() {
        lexer = new Lexer();
    }


    /**
     * Test de parseo de una expresion simple
    */
    @Test
    public void testParseSimpleExpression() {
        // (+ 2 3)
        String input = "(+ 2 3)";
        List<Token> tokens = lexer.tokenize(input);
        Parser parser = new Parser(tokens);
        
        List<ASTNode> astNodes = parser.parse();
        
        assertEquals(1, astNodes.size());
        
        ASTNode rootNode = astNodes.get(0);
        assertEquals("+", rootNode.getValue());
        
        assertEquals(2, rootNode.getChildren().size());
        
        assertEquals("2", rootNode.getChildren().get(0).getValue());
        assertEquals("3", rootNode.getChildren().get(1).getValue());
    }

    /**
     * Test de parseo de una expresion compleja
    */
    @Test
    public void testParseComplexExpression() {
        // (+ 2 (* 3 4))
        String input = "(+ 2 (* 3 4))";
        List<Token> tokens = lexer.tokenize(input);
        Parser parser = new Parser(tokens);
        
        List<ASTNode> astNodes = parser.parse();
        assertEquals(1, astNodes.size());
        
        ASTNode rootNode = astNodes.get(0);
        assertEquals("+", rootNode.getValue());
        assertEquals(2, rootNode.getChildren().size());
        
        assertEquals("2", rootNode.getChildren().get(0).getValue());
        
        ASTNode mulNode = rootNode.getChildren().get(1);
        assertEquals("*", mulNode.getValue());
        assertEquals(2, mulNode.getChildren().size());
        assertEquals("3", mulNode.getChildren().get(0).getValue());
        assertEquals("4", mulNode.getChildren().get(1).getValue());
    }
    
    /**
     * Test de parseo de una expresion compleja
    */
    @Test
    public void testParseQuote() {
        // (QUOTE a)
        String input = "(QUOTE a)";
        List<Token> tokens = lexer.tokenize(input);
        Parser parser = new Parser(tokens);
        
        List<ASTNode> astNodes = parser.parse();
        assertEquals(1, astNodes.size());
        
        ASTNode quoteNode = astNodes.get(0);
        assertEquals("QUOTE", quoteNode.getValue());
        assertEquals(1, quoteNode.getChildren().size());
        assertEquals("a", quoteNode.getChildren().get(0).getValue());
        
        // (QUOTE (a b c))
        input = "(QUOTE (a b c))";
        tokens = lexer.tokenize(input);
        parser = new Parser(tokens);
        
        astNodes = parser.parse();
        quoteNode = astNodes.get(0);
        assertEquals("QUOTE", quoteNode.getValue());
        assertEquals(1, quoteNode.getChildren().size());
        
        ASTNode listNode = quoteNode.getChildren().get(0);
        assertEquals("a", listNode.getValue());

        assertEquals(2, listNode.getChildren().size());

        assertEquals("b", listNode.getChildren().get(0).getValue());
        assertEquals("c", listNode.getChildren().get(1).getValue());
        
        // Test de cambio entre ' a QUOTE
        input = "'(a b c)";
        tokens = lexer.tokenize(input);
        parser = new Parser(tokens);
        
        astNodes = parser.parse();
        quoteNode = astNodes.get(0);
        assertEquals("QUOTE", quoteNode.getValue());
    }
    
    /**
     * Test de hijos de una expresion COND
    */
    @Test
    public void testParseCond() {
        // (COND ((< a 5) (+ a 1)) (T a)
        String input = "(COND ((< a 5) (+ a 1)) (T a))";
        List<Token> tokens = lexer.tokenize(input);
        Parser parser = new Parser(tokens);
        
        List<ASTNode> astNodes = parser.parse();
        assertEquals(1, astNodes.size());
        
        ASTNode condNode = astNodes.get(0);
        assertEquals("COND", condNode.getValue());
        
        assertEquals(2, condNode.getChildren().size());
        
        // (< a 5)
        ASTNode clause = condNode.getChildren().get(0);
        assertEquals("CLAUSE", clause.getValue());
        assertEquals(2, clause.getChildren().size());
        
        ASTNode condition = clause.getChildren().get(0);
        assertEquals("<", condition.getValue());
        assertEquals(2, condition.getChildren().size());
        assertEquals("a", condition.getChildren().get(0).getValue());
        assertEquals("5", condition.getChildren().get(1).getValue());
        
        // (+ a 1)
        ASTNode result1 = clause.getChildren().get(1);
        assertEquals("+", result1.getValue());
        assertEquals(2, result1.getChildren().size());
        assertEquals("a", result1.getChildren().get(0).getValue());
        assertEquals("1", result1.getChildren().get(1).getValue());
        
        // (T a)
        ASTNode result2 = condNode.getChildren().get(1);
        assertEquals("CLAUSE", result2.getValue());
        assertEquals(2, result2.getChildren().size());
        assertEquals("T", result2.getChildren().get(0).getValue());
        assertEquals("a", result2.getChildren().get(1).getValue());
    }
    
    /**
     * Test de una funcion del usuario
    */
    @Test
    public void testParseDefun() {
        // DEFUN SUM (a b) (+ a b))
        String input = "(DEFUN SUM (a b) (+ a b))";
        List<Token> tokens = lexer.tokenize(input);
        Parser parser = new Parser(tokens);
        
        List<ASTNode> astNodes = parser.parse();
        assertEquals(1, astNodes.size());
        
        ASTNode defunNode = astNodes.get(0);
        assertEquals("DEFUN", defunNode.getValue());
        assertEquals(3, defunNode.getChildren().size());
        
        // SUM
        assertEquals("SUM", defunNode.getChildren().get(0).getValue());
        
        // (a b)
        ASTNode parameters1 = defunNode.getChildren().get(1);
        ASTNode parameters2 = defunNode.getChildren().get(1).getChildren().get(0);
        assertEquals("a", parameters1.getValue());
        assertEquals("b", parameters2.getValue());
        
        // (+ a b)
        ASTNode fun = defunNode.getChildren().get(2);
        assertEquals("+", fun.getValue());
        assertEquals(2, fun.getChildren().size());
        assertEquals("a", fun.getChildren().get(0).getValue());
        assertEquals("b", fun.getChildren().get(1).getValue());
    }
    
    /**
     * Test de funcion SETQ
    */
    @Test
    public void testParseSetq() {
        // (SETQ x 42)
        String input = "(SETQ x 42)";
        List<Token> tokens = lexer.tokenize(input);
        Parser parser = new Parser(tokens);
        
        List<ASTNode> astNodes = parser.parse();
        assertEquals(1, astNodes.size());
        
        ASTNode setqNode = astNodes.get(0);
        assertEquals("SETQ", setqNode.getValue());
        assertEquals(2, setqNode.getChildren().size());
        
        // Variable 
        assertEquals("x", setqNode.getChildren().get(0).getValue());
        
        // Valor
        assertEquals("42", setqNode.getChildren().get(1).getValue());
    }
    
    /**
     * Test de intento de parseo con una expresion invalida
    */
    @Test
    public void testParseInvalidExpressions() {
        // Falta de parentesis
        String input = "(+ 1 2";
        List<Token> tokens = lexer.tokenize(input);
        Parser parser = new Parser(tokens);
        
        try {
            parser.parse();
        } catch (RuntimeException e) {
            // Funcionamiento esperado
        }
    }
}