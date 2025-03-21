package com.InterpreteLisp;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

/*
 * Universidad del Valle de Guatemala
 * Algoritmos y Estructuras de Datos
 * Ing. Douglas Barrios
 * @author: Marcelo Detlefsen, Jose Rivera, Fabián Prado
 * Creación: 20/03/2025
 * última modificación: 20/03/2025
 * File Name: EvaluatorTest.java
 * Descripción: JUnit Class para comprobar que los metodos de Evaluator.java funcionan correctamente
 * 
 * Código original generado con la asistencia de Claude y Deepseek.
 */

public class EvaluatorTest {
    
    private Evaluator evaluator;
    private ContextualScope scope;
    
    @Before
    public void setUp() {
        evaluator = new Evaluator();
        scope = new Environment();
    }
    
    /**
     * Metodo para crear un ASTNode con valor y sin hijos
    */
    private ASTNode createNode(String value) {
        return new ASTNode(value);
    }
    
    /**
     * Metodo para crear un ASTNode con valor y con hijos
    */
    private ASTNode createNodeWithChildren(String value, List<ASTNode> children) {
        ASTNode node = new ASTNode(value);
        for (ASTNode child : children) {
            node.addChild(child);
        }
        return node;
    }
    
    /**
     * Test de evaluacion de varios tipos de numeros
    */
    @Test
    public void testNumericEvaluation() {
        // Intero
        ASTNode intNode = createNode("42");
        assertEquals(42.0, evaluator.evaluate(intNode, scope));
        
        // Decimal
        ASTNode decimalNode = createNode("3.14");
        assertEquals(3.14, evaluator.evaluate(decimalNode, scope));
        
        // Numero negativo
        ASTNode negativeNode = createNode("-10");
        assertEquals(-10.0, evaluator.evaluate(negativeNode, scope));
    }
    
    /**
     * Test de operaciones aritmeticas basicas
    */
    @Test
    public void testArithmeticOperations() {
        // Suma
        List<ASTNode> addChildren = new ArrayList<>();
        addChildren.add(createNode("5"));
        addChildren.add(createNode("3"));
        ASTNode addNode = createNodeWithChildren("+", addChildren);
        assertEquals(8.0, evaluator.evaluate(addNode, scope));
        
        // Resta
        List<ASTNode> subChildren = new ArrayList<>();
        subChildren.add(createNode("10"));
        subChildren.add(createNode("4"));
        ASTNode subNode = createNodeWithChildren("-", subChildren);
        assertEquals(6.0, evaluator.evaluate(subNode, scope));
        
        // Multiplicacion
        List<ASTNode> mulChildren = new ArrayList<>();
        mulChildren.add(createNode("6"));
        mulChildren.add(createNode("7"));
        ASTNode mulNode = createNodeWithChildren("*", mulChildren);
        assertEquals(42.0, evaluator.evaluate(mulNode, scope));
        
        // Division
        List<ASTNode> divChildren = new ArrayList<>();
        divChildren.add(createNode("20"));
        divChildren.add(createNode("5"));
        ASTNode divNode = createNodeWithChildren("/", divChildren);
        assertEquals(4.0, evaluator.evaluate(divNode, scope));
    }
    
    /**
     * Test Test de operaciones aritmeticas complejas (con nested expressions)
    */
    @Test
    public void testComplexArithmetic() {
        // (+ 2 (* 3 4))
        List<ASTNode> mulChildren = new ArrayList<>();
        mulChildren.add(createNode("3"));
        mulChildren.add(createNode("4"));
        ASTNode mulNode = createNodeWithChildren("*", mulChildren);
        
        List<ASTNode> addChildren = new ArrayList<>();
        addChildren.add(createNode("2"));
        addChildren.add(mulNode);
        ASTNode addNode = createNodeWithChildren("+", addChildren);
        
        assertEquals(14.0, evaluator.evaluate(addNode, scope));
    }
    
    /**
     * Test de asignacion de valor a una variable
    */
    @Test
    public void testSetq() {
        // (SETQ x 10)
        List<ASTNode> setqChildren = new ArrayList<>();
        setqChildren.add(createNode("x"));
        setqChildren.add(createNode("10"));
        ASTNode setqNode = createNodeWithChildren("SETQ", setqChildren);
        
        assertEquals(10.0, evaluator.evaluate(setqNode, scope));
        assertEquals(10.0, evaluator.evaluate(createNode("x"), scope));
        
        // Test de cambio de valor de una variable
        List<ASTNode> setqChildren2 = new ArrayList<>();
        setqChildren2.add(createNode("x"));
        setqChildren2.add(createNode("20"));
        ASTNode setqNode2 = createNodeWithChildren("SETQ", setqChildren2);
        
        assertEquals(20.0, evaluator.evaluate(setqNode2, scope));
        assertEquals(20.0, evaluator.evaluate(createNode("x"), scope));
    }
    
    /**
     * Test de definicion y ejecucion de funciones del usuario usando DEFUN
    */
    @Test
    public void testDefun() {
        // (DEFUN duplicar (x) (* x 2))
        ASTNode paramNode = createNode("");
        paramNode.addChild(createNode("x"));
        
        List<ASTNode> bodyChildren = new ArrayList<>();
        bodyChildren.add(createNode("x"));
        bodyChildren.add(createNode("2"));
        ASTNode bodyNode = createNodeWithChildren("*", bodyChildren);
        
        List<ASTNode> defunChildren = new ArrayList<>();
        defunChildren.add(createNode("duplicar"));
        defunChildren.add(paramNode);
        defunChildren.add(bodyNode);
        ASTNode defunNode = createNodeWithChildren("DEFUN", defunChildren);
        
        evaluator.evaluate(defunNode, scope);
        
        List<ASTNode> callChildren = new ArrayList<>();
        callChildren.add(createNode("5"));
        ASTNode callNode = createNodeWithChildren("duplicar", callChildren);
        
        assertEquals(10.0, evaluator.evaluate(callNode, scope));
    }
    
    /**
     * Test de una expresion condicion usando COND
    */
    @Test
    public void testCond() {
        // Test de la condicion inicial siendo T
        // (COND 
        //   ((< 5 10) "Si")
        //   (T "No"))
        
        // (< 5 10) "Si" 
        List<ASTNode> compareChildren = new ArrayList<>();
        compareChildren.add(createNode("5"));
        compareChildren.add(createNode("10"));
        ASTNode compareNode = createNodeWithChildren("<", compareChildren);
        
        List<ASTNode> clause1Children = new ArrayList<>();
        clause1Children.add(compareNode);
        clause1Children.add(createNode("Si"));
        ASTNode clause1 = createNodeWithChildren("CLAUSE", clause1Children);
        
        // (T "No") 
        List<ASTNode> clause2Children = new ArrayList<>();
        clause2Children.add(createNode("T"));
        clause2Children.add(createNode("No"));
        ASTNode clause2 = createNodeWithChildren("CLAUSE", clause2Children);
        
        // COND 
        List<ASTNode> condChildren = new ArrayList<>();
        condChildren.add(clause1);
        condChildren.add(clause2);
        ASTNode condNode = createNodeWithChildren("COND", condChildren);
        
        assertEquals("Si", evaluator.evaluate(condNode, scope));
        
        // Test del caso contrario:
        // (COND 
        //   ((> 5 10) "Si")
        //   (T "No"))
        compareChildren = new ArrayList<>();
        compareChildren.add(createNode("5"));
        compareChildren.add(createNode("10"));
        compareNode = createNodeWithChildren(">", compareChildren);
        
        clause1Children = new ArrayList<>();
        clause1Children.add(compareNode);
        clause1Children.add(createNode("Si"));
        clause1 = createNodeWithChildren("CLAUSE", clause1Children);
        
        condChildren = new ArrayList<>();
        condChildren.add(clause1);
        condChildren.add(clause2);
        condNode = createNodeWithChildren("COND", condChildren);
        
        assertEquals("No", evaluator.evaluate(condNode, scope));
    }
    
    /**
     * Test de QUOTE que regresa una expresion sin evaluarla
    */
    @Test
    public void testQuote() {
        // (QUOTE x)
        List<ASTNode> quoteChildren = new ArrayList<>();
        quoteChildren.add(createNode("x"));
        ASTNode quoteNode = createNodeWithChildren("QUOTE", quoteChildren);
        
        Object result = evaluator.evaluate(quoteNode, scope);
        assertTrue(result instanceof ASTNode);
        assertEquals("x", ((ASTNode)result).getValue());
    }
    
    
    /**
     * Test de funcion EQUAL para comparar valores
    */
    @Test
    public void testEqual() {
        // (EQUAL 5 5)
        List<ASTNode> equalNumChildren = new ArrayList<>();
        equalNumChildren.add(createNode("5"));
        equalNumChildren.add(createNode("5"));
        ASTNode equalNumNode = createNodeWithChildren("EQUAL", equalNumChildren);
        
        assertTrue((Boolean)evaluator.evaluate(equalNumNode, scope));
        
        // (EQUAL 5 6)
        List<ASTNode> notEqualNumChildren = new ArrayList<>();
        notEqualNumChildren.add(createNode("5"));
        notEqualNumChildren.add(createNode("6"));
        ASTNode notEqualNumNode = createNodeWithChildren("EQUAL", notEqualNumChildren);
        
        assertFalse((Boolean)evaluator.evaluate(notEqualNumNode, scope));
        
        // Test con simbolos (EQUAL 'a 'a)
        // Crear ambos 'a como (QUOTE a)
        List<ASTNode> quoteA1Children = new ArrayList<>();
        quoteA1Children.add(createNode("a"));
        ASTNode quoteA1 = createNodeWithChildren("QUOTE", quoteA1Children);
        
        List<ASTNode> quoteA2Children = new ArrayList<>();
        quoteA2Children.add(createNode("a"));
        ASTNode quoteA2 = createNodeWithChildren("QUOTE", quoteA2Children);
        
        List<ASTNode> equalSymChildren = new ArrayList<>();
        equalSymChildren.add(quoteA1);
        equalSymChildren.add(quoteA2);
        ASTNode equalSymNode = createNodeWithChildren("EQUAL", equalSymChildren);
        
        assertTrue((Boolean)evaluator.evaluate(equalSymNode, scope));
    }
    
    /**
     * Test de operadores de comparacion  (< >)
    */
    @Test
    public void testComparisons() {
        // (< 5 10)
        List<ASTNode> ltChildren = new ArrayList<>();
        ltChildren.add(createNode("5"));
        ltChildren.add(createNode("10"));
        ASTNode ltNode = createNodeWithChildren("<", ltChildren);
        
        assertTrue((Boolean)evaluator.evaluate(ltNode, scope));
        
        // (> 15 10)
        List<ASTNode> gtChildren = new ArrayList<>();
        gtChildren.add(createNode("15"));
        gtChildren.add(createNode("10"));
        ASTNode gtNode = createNodeWithChildren(">", gtChildren);
        
        assertTrue((Boolean)evaluator.evaluate(gtNode, scope));
    }
    
    /**
     * Test de funciones complejas con recursividad 
    */
    @Test
    public void testRecursiveFunction() {
        // Factorial: (DEFUN FACTORIAL (n) (COND ((= n 0) 1) (T (* n (FACTORIAL (- n 1))))))
        
        ASTNode paramNode = createNode("");
        paramNode.addChild(createNode("n"));
        
        // ((= n 0) 1)
        List<ASTNode> eqChildren = new ArrayList<>();
        eqChildren.add(createNode("n"));
        eqChildren.add(createNode("0"));
        ASTNode eqNode = createNodeWithChildren("=", eqChildren);
        
        List<ASTNode> baseClauseChildren = new ArrayList<>();
        baseClauseChildren.add(eqNode);
        baseClauseChildren.add(createNode("1"));
        ASTNode baseClause = createNodeWithChildren("CLAUSE", baseClauseChildren);
        
        // (T (* n (FACTORIAL (- n 1))))
        List<ASTNode> subChildren = new ArrayList<>();
        subChildren.add(createNode("n"));
        subChildren.add(createNode("1"));
        ASTNode subNode = createNodeWithChildren("-", subChildren);
        
        List<ASTNode> factRecurChildren = new ArrayList<>();
        factRecurChildren.add(subNode);
        ASTNode factRecurNode = createNodeWithChildren("FACTORIAL", factRecurChildren);
        
        List<ASTNode> mulRecurChildren = new ArrayList<>();
        mulRecurChildren.add(createNode("n"));
        mulRecurChildren.add(factRecurNode);
        ASTNode mulRecurNode = createNodeWithChildren("*", mulRecurChildren);
        
        List<ASTNode> recurClauseChildren = new ArrayList<>();
        recurClauseChildren.add(createNode("T"));
        recurClauseChildren.add(mulRecurNode);
        ASTNode recurClause = createNodeWithChildren("CLAUSE", recurClauseChildren);
        
        // Expresion condicional COND 
        List<ASTNode> condChildren = new ArrayList<>();
        condChildren.add(baseClause);
        condChildren.add(recurClause);
        ASTNode condNode = createNodeWithChildren("COND", condChildren);
        
        // Expresion DEFUN 
        List<ASTNode> defunChildren = new ArrayList<>();
        defunChildren.add(createNode("FACTORIAL"));
        defunChildren.add(paramNode);
        defunChildren.add(condNode);
        ASTNode defunNode = createNodeWithChildren("DEFUN", defunChildren);
        
        evaluator.evaluate(defunNode, scope);
        
        // Test de FACTORIAL(5)
        List<ASTNode> callChildren = new ArrayList<>();
        callChildren.add(createNode("5"));
        ASTNode callNode = createNodeWithChildren("FACTORIAL", callChildren);
        
        assertEquals(120.0, evaluator.evaluate(callNode, scope));
    }
    
    
    /**
     * Test relacionado a casos con error
    */
    @Test(expected = RuntimeException.class)
    public void testDivisionByZero() {
        List<ASTNode> divChildren = new ArrayList<>();
        divChildren.add(createNode("10"));
        divChildren.add(createNode("0"));
        ASTNode divNode = createNodeWithChildren("/", divChildren);
        
        evaluator.evaluate(divNode, scope);
    }
    
    @Test(expected = RuntimeException.class)
    public void testUndefinedFunction() {
        List<ASTNode> callChildren = new ArrayList<>();
        callChildren.add(createNode("5"));
        ASTNode callNode = createNodeWithChildren("nonExistentFunction", callChildren);
        
        evaluator.evaluate(callNode, scope);
    }
    
    @Test(expected = RuntimeException.class)
    public void testInvalidQuote() {
        // QUOTE with too many arguments
        List<ASTNode> quoteChildren = new ArrayList<>();
        quoteChildren.add(createNode("a"));
        quoteChildren.add(createNode("b"));
        ASTNode quoteNode = createNodeWithChildren("QUOTE", quoteChildren);
        
        evaluator.evaluate(quoteNode, scope);
    }
    
    /**
     * Test del metodo executeLayer que maneja la evaluacion de diferente layers 
    */
    @Test
    public void testExecuteLayer() {
        // Definimos una variable en un layer especifico
        List<ASTNode> setqChildren = new ArrayList<>();
        setqChildren.add(createNode("x"));
        setqChildren.add(createNode("10"));
        ASTNode setqNode = createNodeWithChildren("SETQ", setqChildren);
        
        evaluator.executeLayer(setqNode, 1);
        
        // Uso de la variable en ese layer: (+ x 5)
        List<ASTNode> addChildren = new ArrayList<>();
        addChildren.add(createNode("x"));
        addChildren.add(createNode("5"));
        ASTNode addNode = createNodeWithChildren("+", addChildren);
        
        // Evaluar en otro layer donde no deberia existir
        try {
            evaluator.executeLayer(addNode, 2);
            fail("Should have thrown RuntimeException for undefined variable");
        } catch (RuntimeException e) { }
    }
 }