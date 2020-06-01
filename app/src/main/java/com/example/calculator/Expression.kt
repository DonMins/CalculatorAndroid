package com.example.calculator

sealed class Expression {
    object Variable : Expression()
    class Constant(val value: Double) : Expression()
    enum class Operation {
        PLUS,
        MINUS,
        MUL,
        DIV;
    }
    class Binary(
        val left: Expression,
        val op: Operation,
        val right: Expression
    ) : Expression()
    class Negate(val arg: Expression) : Expression()
}