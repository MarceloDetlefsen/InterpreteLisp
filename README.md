# Intérprete Lisp
Este proyecto es un intérprete de LISP desarrollado en Java. Permite evaluar expresiones LISP básicas, incluyendo operaciones aritméticas, definición de funciones, manejo de variables, predicados, condicionales y más. El intérprete sigue las especificaciones del lenguaje LISP tradicional, con algunas adaptaciones y no con todas las funciones incluidas

## 🎥 Link para acceder al video del funcionamiento
[Ver video del funcionamiento](https://drive.google.com/file/d/1bKxVUoVy75kDowL18tBAPAXez4wCdgh0/view?usp=sharing)

# 🛠️ Instalación y Ejecución
1. Clonar el repositorio:
    ```bash
    git clone https://github.com/MarceloDetlefsen/InterpreteLisp.git
    cd InterpreteLisp
    ```

2. Compilar el intérprete:
    ```bash 
    javac -d out src/main/java/com/InterpreteLisp/*.java
    ```

3. Ejecutar el intérprete.
    ```bash
    cd out
    java com.InterpreteLisp.Main
    ```

# 📚 Ejemplos para Funciones del Programa
Estos son algunos ejemplos de expresiones LISP que puedes usar para probar el programa:

## 1. Operaciones aritméticas
```lisp
(+ 3 4)
(- 10 5)
(* 6 7)
(/ 20 4)
(+ 1 2 3 4 5)
(+ (* 3 4) (- 10 5))
```

## 2. Instrucción QUOTE o '
```lisp
(QUOTE (a b c))
'(1 2 3)
(QUOTE (+ 3 4))
'(+ 3 4)
```

## 3. SETQ 
```lisp
(SETQ x 10)
(+ x 5)
(SETQ y (+ 3 4))
(+ x y)
(SETQ z '(a b c))
z
```

## 4. Definición de funciones (DEFUN)
```lisp
(DEFUN suma (a b) (+ a b))
(suma 5 7)
(DEFUN cuadrado (x) (* x x))
(cuadrado 4)
```

## 5. Predicados (ATOM, LIST, EQUAL, <, >)
```lisp
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
```lisp
(COND ((> 3 2) 'mayor) (T 'menor))
(COND ((< 3 2) 'menor) (T 'mayor))
(COND ((< 5 1) 'uno) ((< 5 3) 'dos) (T 'tres))
```

## 7. Paso de parámetros. (un parámetro puede ser incluso una función)
```lisp
(DEFUN aplicar (f x) (f x))
(DEFUN cuadrado (x) (* x x))
(aplicar cuadrado 4)
```

```lisp
(DEFUN aplicar (f x) (f x))
(DEFUN doble (x) (* 2 x))
(aplicar doble 5)
```

```lisp
(DEFUN componer (f g x) (f (g x)))
(DEFUN doble (x) (* 2 x))
(componer doble cuadrado 3)
```

## ❌ Pruebas de Errores
```lisp
(/ 5 0)
(suma 1)
(COND)
(SETQ)
(+ 'a 2)
(DEFUN)
```

# 🛠️ Ejemplos de programas completos funcionales con recursividad

## 🌀 Fibonacci
Este programa implementa la secuencia de Fibonacci, una serie de números en la que cada número es la suma de los dos anteriores, comenzando con 0 y 1. La secuencia de Fibonacci es una de las secuencias más conocidas en matemáticas y tiene aplicaciones en diversas áreas como la informática, la biología y la teoría de números.

```lisp
(DEFUN fibonacci (n)
(COND
    ((= n 0) 0)
    ((= n 1) 1)
    (T (+ (fibonacci (- n 1)) (fibonacci (- n 2))))
    )
)
(fibonacci 9)
```

## ❕ Factorial
Este programa calcula el factorial de un número, que es el producto de todos los enteros positivos menores o iguales a ese número. El factorial de n (denotado como n!) es una función matemática importante en combinatoria, análisis y álgebra.

```lisp
(DEFUN factorial (n)
(COND
    ((= n 0) 1)
    (T (* n (factorial (- n 1))))
    )
)
(factorial 5)
```

## 🌡️ Conversión de Celsius a Fahrenheit
Este programa convierte una temperatura de Celsius a Fahrenheit utilizando una función recursiva. La fórmula utilizada es la tradicional para la conversión de temperaturas, donde 0 grados Celsius es igual a 32 grados Fahrenheit, y cada incremento de 1 grado Celsius se convierte en un incremento de 1.8 grados Fahrenheit.

```lisp
(DEFUN conversion (c)
(COND
    ((= c 0) 32)
    (T (+ (/ 9 5) (conversion (- c 1))))
    )
)
(conversion 25)
```
## UML de Clases
![image](https://github.com/user-attachments/assets/097f1a2f-00ef-49d2-b83d-6091ceabbeba)
El diagrama de clases muestra las clases utilizadas y como se conectan unas con otras. 
- Clases:  
  - Main
  - Token:
  - Lexer:
  - Parser:
  - ASTNode:
  - Evaluator:
  - Environment:
  - ContextualScope (interface):  
 
  
## UML de Secuencias: 
![image](https://github.com/user-attachments/assets/97eafc55-7952-4ac3-827e-c336da6832fa)
El diagrama de secuencias describe cómo se desarrolla el flujo del programa hasta que llega a una respuesta.  

## UML de Casos de Usos:
![image](https://github.com/user-attachments/assets/43e65f55-6cd8-4380-9b33-99c8a25d9f43)
Muestra el comportamiento esperado del sistema en relación a sus actores y sus clases. 

# Autores:
👨‍💻 Marcelo Detlefsen
👨‍💻 Jose Rivera
👨‍💻 Fabián Prado
