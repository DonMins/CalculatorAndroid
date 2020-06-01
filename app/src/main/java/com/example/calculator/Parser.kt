package com.example.calculator

class Parser(val groups: List<String>) {
    var pos = 0
    fun parse(): Expression {
        val result = parseExpression()
        if (pos < groups.size) throw IllegalStateException()
        return result
    }
    private fun parseExpression(): Expression {
        var left = parseItem()
        while (pos < groups.size) {
            val op = operationMap[groups[pos]]
            if (op == Expression.Operation.PLUS || op == Expression.Operation.MINUS) {
                pos++
                left = Expression.Binary(left, op, parseItem())
            } else return left
        }
        return left
    }
    val operationMap = mapOf("+" to Expression.Operation.PLUS, "-" to Expression.Operation.MINUS,
        "×" to Expression.Operation.MUL, "÷" to Expression.Operation.DIV)

    private fun parseItem(): Expression {
        var left = parseFactor()
        while (pos < groups.size) {
            val op = operationMap[groups[pos]]
            if (op == Expression.Operation.MUL || op == Expression.Operation.DIV) {
                pos++
                left = Expression.Binary(
                    left, op, parseFactor())
            } else return left
        }
        return left
    }

    private fun parseFactor(): Expression =
        if (pos >= groups.size) throw IllegalStateException()
        else {
            val group = groups[pos++]
            when (group) {
                "x" -> Expression.Variable
                "-" -> Expression.Negate(parseFactor())
                "(" -> {
                    val arg = parseExpression()
                    val next = groups[pos++]
                    if (next == ")") arg
                    else throw IllegalStateException()
                }
                else -> Expression.Constant(group.toDouble())
            }
        }

}
