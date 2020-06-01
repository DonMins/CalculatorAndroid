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

class MainActivity : AppCompatActivity() {

    var clearText: Boolean = false;
    var eps: Double = 10e-12;
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
        textView.text = textView.text.toString().plus((view as AppCompatButton).text.toString());
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
        val res = parseExpr(textView.text.toString());
        if (res.toDouble().equals(Double.MAX_VALUE)) {
            textView2.text = textView2.text.toString().plus("= ").plus("на ноль делить нельзя");
        } else {
            textView2.text = textView2.text.toString().plus("= ").plus(res.toString().intOrString());
        }
        clearText = true;
    }

    fun clear(view: View) {
        textView.text = "";
    }

    fun deleteLaatSimble(view: View) {
    }

}
