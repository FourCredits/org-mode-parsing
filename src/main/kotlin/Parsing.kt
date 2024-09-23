package org.example

fun interface Parser<T> {
    fun parse(input: CharSequence): Result<T>
}

sealed interface Result<T>
data class SuccessfulParse<T>(val value: T, val remaining: CharSequence) : Result<T>
class FailedParse<T> : Result<T>

val anyChar = Parser { input ->
    when (val c = input.getOrNull(0)) {
        null -> FailedParse()
        else -> SuccessfulParse(c, input.subSequence(1, input.length))
    }
}

fun <T> pure(value: T) = Parser { input -> SuccessfulParse(value, input) }

fun <T> fail() = Parser<T> { FailedParse() }

fun <T> Parser<T>.satisfy(predicate: (T) -> Boolean) =
    this.bind { if (predicate(it)) pure(it) else fail() }

fun char(c: Char) = anyChar.satisfy { it == c }

fun string(s: String) = Parser { input ->
    if (input.startsWith(s)) SuccessfulParse(s, input.subSequence(s.length, input.length))
    else FailedParse()
}

fun <T> anyOf(parsers: Sequence<Parser<T>>) = Parser { input ->
    parsers.firstNotNullOfOrNull { parser -> parser.parse(input).takeIf { it is SuccessfulParse }}
        ?: FailedParse()
}

fun <T> Parser<T>.ignoreResult() = map { }

fun <T> anyOf(vararg parsers: Parser<T>) = anyOf(parsers.asSequence())

fun <T1, T2> Parser<T1>.map(mapping: (T1) -> T2) = bind { pure(mapping(it)) }

fun <T1, T2> Parser<T1>.chain(next: Parser<T2>) = this.bind { next }
fun <T1, T2> Parser<T1>.chainLeft(next: Parser<T2>) = this.bind { first -> next.map { first } }

fun <T1, T2> Parser<T1>.bind(f: (T1) -> Parser<T2>) = Parser { input ->
    when (val result = parse(input)) {
        is SuccessfulParse -> f(result.value).parse(result.remaining)
        else -> FailedParse()
    }
}

fun <T> zeroOrMore(parser: Parser<T>) = anyOf(oneOrMore(parser), pure(emptySequence()))

fun <T> oneOrMore(parser: Parser<T>): Parser<Sequence<T>> = parser.bind { first ->
    zeroOrMore(parser).map { rest ->
        sequence {
            yield(first)
            yieldAll(rest)
        }
    }
}

fun <T1, T2, T3> lift(parser1: Parser<T1>, parser2: Parser<T2>, joiner: (T1, T2) -> T3) =
    parser1.bind { value1 -> parser2.map { value2 -> joiner(value1, value2) } }

fun <T> completeParser(parser: Parser<T>) = Parser { input ->
    when (val result = parser.parse(input)) {
        is FailedParse -> result
        is SuccessfulParse -> if (result.remaining.isEmpty()) result else FailedParse()
    }
}