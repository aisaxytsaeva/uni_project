package com.example.uni_project.core

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DateTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }

        val formatted = buildString {
            if (digits.isNotEmpty()) {
                append(digits.take(2))

                if (digits.length >= 3) {
                    append(".")
                    append(digits.substring(2, minOf(4, digits.length)))
                }

                if (digits.length >= 5) {
                    append(".")
                    append(digits.substring(4, minOf(8, digits.length)))
                }
            }
        }

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val digitsBefore = text.text.take(offset).count { it.isDigit() }
                return when (digitsBefore) {
                    0 -> 0
                    1, 2 -> digitsBefore
                    3, 4 -> digitsBefore + 1
                    else -> digitsBefore + 2
                }.coerceAtMost(formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                var digitCount = 0
                var currentOffset = 0

                for (char in formatted.take(offset)) {
                    if (char.isDigit()) {
                        digitCount++
                    }
                    currentOffset++
                }

                return digitCount
            }
        }

        return TransformedText(AnnotatedString(formatted), mapping)
    }
}