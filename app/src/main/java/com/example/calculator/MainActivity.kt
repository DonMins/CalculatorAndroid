package com.example.calculator

import android.os.Build
import android.os.Bundle
import android.text.TextUtils.split
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.lang.Math.abs
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {

    var clearText: Boolean = false;
    var eps: Double = 10e-12;
    val operatorsList: List<String> = listOf("+", "-", "×", "÷");
    val operatorsListWithDot: List<String> = listOf("+", "-", "×", "÷",".");
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun String.splitIntoGroups(): List<String> {
        val matchResults = Regex(
            """x|\+|-|\*|/|\(|\)|\d+\.\d+|\d+| +?|.+?"""
        ).findAll(this)
        val groups = matchResults.map { it.value }
            .filter { it.isNotBlank() }
            .toList()
        return groups
    }

    fun Expression.calculate(x: Double): Double = when (this) {
        Expression.Variable -> x
        is Expression.Constant -> value
        is Expression.Binary -> {
            val left = left.calculate(x)
            val right = right.calculate(x)
            when (op) {
                Expression.Operation.PLUS -> left + right
                Expression.Operation.MINUS -> left - right
                Expression.Operation.MUL -> left * right
                Expression.Operation.DIV -> {
                    if (abs(right) > eps) {
                        left / right
                    } else {
                        Double.MAX_VALUE
                    }
                }
            }
        }
        is Expression.Negate -> -arg.calculate(x)
    } as Double

    fun parseExpr(string: String): Double {
        val list = string.splitIntoGroups()
        val expr = Parser(list).parse()
        return expr.calculate(1.0)
    }

    fun number(view: View) {
        if (clearText == true) {
            clearText = false;
            textView.text = "";
            textView2.text = "";
        }
        val simbol = (view as AppCompatButton).text.toString();
        if (textView.text.isEmpty() && operatorsListWithDot.contains(simbol) && simbol != "-") {
            textView.text = textView.text
        } else {
            if ((simbol == ".") && ((textView.text.last().toString()) == ".")) {
                textView.text = textView.text
            } else {
                if (operatorsList.contains(simbol) && (!textView.text.isEmpty()) && (operatorsList.contains(
                        textView.text.last().toString()
                    ))
                ) {
                    textView.text =
                        textView.text.substring(0, textView.text.lastIndex).plus(simbol);
                } else {
                    textView.text = textView.text.toString().plus(simbol);
                }
            }
        }
    }

    fun String.intOrString(): Any {
        val v = this.split(".")
        if (v.size == 2) {
            if (v[1].equals("0")) {
                return v[0];
            } else {
                return this;
            }
        }
        return this;
    }

    fun equalss(view: View) {
        textView2.text = "";
        val res = parseExpr(textView.text.toString());
        if (res.toDouble().equals(Double.MAX_VALUE)) {
            textView2.text = textView2.text.toString().plus("= ").plus("на ноль делить нельзя");
        } else {
            textView2.text = textView2.text.toString().plus("= ").plus(BigDecimal(res).setScale(8,BigDecimal.ROUND_HALF_UP).toDouble().toString().intOrString());
        }
        clearText = true;
    }

    fun clear(view: View) {
        textView.text = "";
        textView2.text = "";
    }

    fun deleteLaatSimble(view: View) {
        if ((textView.text.lastIndex >= 0) && (textView2.text.isEmpty()))  {
            textView.text = textView.text.substring(0, textView.text.lastIndex)
        }
    }

}
