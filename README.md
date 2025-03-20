# Intérprete Lisp
Este proyecto es un intérprete de LISP desarrollado en Java. Permite evaluar expresiones LISP básicas, incluyendo operaciones aritméticas, definición de funciones, manejo de variables, predicados, condicionales y más. El intérprete sigue las especificaciones del lenguaje LISP tradicional, con algunas adaptaciones y no con todas las funciones incluidas

# 🛠️ Instalación y Ejecución
1. Clonar el repositorio:
    ```bash
    git clone https://github.com/MarceloDetlefsen/InterpreteLisp.git
    cd InterpreteLisp
    ```

2. Compilar el código:
    ```bash
    javac Main.java
    ```

3. Ejecutar el programa.
    ```bash
    java Main
    ```

# 📚 Ejemplos para Funciones del Programa
Estos son algunos ejemplos de expresiones LISP que puedes usar para probar el programa:

## 1. Operaciones aritméticas
    ```bash
    (+ 3 4)
    (- 10 5)
    (* 6 7)
    (/ 20 4)
    (+ 1 2 3 4 5)
    (+ (* 3 4) (- 10 5))
    ```

## 2. Instrucción QUOTE o ‘ (single quote, para interrumpir el proceso de evaluación de expresiones)
    ```bash
    (QUOTE (a b c))
    '(1 2 3)
    (QUOTE (+ 3 4))
    '(+ 3 4)
    ```

## 3. Definición de funciones (DEFUN)
    ```bash
    (SETQ x 10)
    (+ x 5)
    (SETQ y (+ 3 4))
    (+ x y)
    (SETQ z '(a b c))
    z
    ```

## 4. SETQ
    ```bash
    (DEFUN suma (a b) (+ a b))
    (suma 5 7)
    (DEFUN cuadrado (x) (* x x))
    (cuadrado 4)
    ```

## 5. Predicados (ATOM, LIST, EQUAL, <, >)
    ```bash
    (ATOM 'a)
    (ATOM '(a b c))
    (LIST 'a)
    (LIST '(a b c))
    (EQUAL 5 5)
    (EQUAL 'abc 'abc)
    (EQUAL '(1 2) '(1 2))
    (< 3 5)
    (< 5 3)
    (> 10 7)
    (> 7 10)
    ```

## 6. Condicionales (COND)
    ```bash
    (COND ((> 3 2) 'mayor) (T 'menor))
    (COND ((< 3 2) 'menor) (T 'mayor))
    (COND ((< 5 1) 'uno) ((< 5 3) 'dos) (T 'tres))
    ```

## 7. Paso de parámetros. (un parámetro puede ser incluso una función)
    ```bash
    (DEFUN aplicar (f x) (f x))
    (DEFUN cuadrado (x) (* x x))
    (aplicar cuadrado 4)
    ```

    ```bash
    (DEFUN aplicar (f x) (f x))
    (DEFUN doble (x) (* 2 x))
    (aplicar doble 5)
    ```

    ```bash
    (DEFUN componer (f g x) (f (g x)))
    (DEFUN doble (x) (* 2 x))
    (componer doble cuadrado 3)
    ```

## ❌ Pruebas de Errores
    ```bash
    (/ 5 0)
    (suma 1)
    (COND)
    (SETQ)
    (+ 'a 2)
    (DEFUN)
    ```

# 🛠️ Ejemplos de programas completos funcionales con recursividad

## 🌀 Fibonacci
    ```bash
    (DEFUN fibonacci (n)
    (COND
        ((= n 0) 0)
        ((= n 1) 1)
        (T (+ (fibonacci (- n 1)) (fibonacci (- n 2))))
    )
    )
    (fibonacci 5)
    ```

## ❕ Factorial
    ```bash
    (DEFUN factorial (n)
    (COND
        ((= n 0) 1)
        (T (* n (factorial (- n 1))))
    )
    )
    (factorial 5)
    ```

##  🌡️ Conversión de Celsuis a Fahrenheit
    ```bash
    (DEFUN conversion (c)
    (COND
        ((= c 0) 32)
        (T (+ (/ 9 5) (conversion (- c 1))))
    )
    )
    (conversion 25)
    ```

# Autores:
👨‍💻 Marcelo Detlefsen
👨‍💻 Jose Rivera
👨‍💻 Fabián Prado
