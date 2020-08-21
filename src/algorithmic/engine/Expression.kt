package algorithmic.engine

// արտահայտությունների ինտերֆեյս
interface Expression

// թվային հաստատուն
data class Numeric constructor(val value: Double) : Expression

// տեքստային հաստատուն
data class Text constructor(val value: String) : Expression

// փոփոխական
data class Variable constructor(val sym: Symbol) : Expression

// ունար գործողություն


// բինար գործողություն

