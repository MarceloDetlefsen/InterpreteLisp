# Lexer -> Analizador Léxico para Expresiones de LISP
Esta entrega agrega un analizador sintáctico (Parser) para expresiones escritas en LISP. 
Este convierte los tokens en un árbol de sintaxis abstracta (AST) que representa la estructura de la expresión.

# 🚀 Instalación y Ejecución
1. Clonar el repositorio:
    ```bash
    git clone https://github.com/MarceloDetlefsen/ParserLisp.git
    cd ParserLisp

2. Compilar el código:
    ```bash
    javac Main.java Lexer.java Token.java Parser.java ASTNode.java

3. Ejecutar el programa.
    ```bash
    java Main

# Ejemplos para Probar el Programa
Estos son algunos ejemplos de expresiones LISP que puedes usar para probar el programa:

- `(+ 2 (* V 8))`
- `(QUOTE (+ 5 2))`
- `(- 10 5)`
- `(/ 12 4)`
- `(+ (* 2 3) (- 10 5))`
- `(QUOTE (QUOTE (+ 1 2)))`
- `(IF (> x 3) (+ x 2) (- x 2))`
- `) + 2 3 (` *(Este último es un ejemplo de entrada inválida para probar el manejo de errores)*

# Autores:
👨‍💻 Marcelo Detlefsen
👨‍💻 Jose Rivera
👨‍💻 Fabián Prado
