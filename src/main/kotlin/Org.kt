package org.example

data class OrgDocument(val lines: List<String>)

val whitespaceChars = anyOf(sequenceOf(' ', '\t').map(::char))
val newLine = anyOf(
    string("\r\n").ignoreResult(), char('\r').ignoreResult(), char('\n').ignoreResult()
)
val blankLine = zeroOrMore(whitespaceChars).chainLeft(newLine)

val orgDocument = completeParser(zeroOrMore(blankLine).map { lines ->
    OrgDocument(lines.map { it.joinToString("") }.toList())
})