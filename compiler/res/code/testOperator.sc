

struct Vector {
    operator fun plus(other: Vector) : Vector {
        return other;
    }

    operator fun get(a: Int): Vector {
        return *this;
    }

    operator fun set(a: Int, b: Vector) {
    }
}

fun main() {
    var a : Vector;
    var b : Vector;
    var c : Vector = a + b;
    var d = c[2];
    c[3] = b;
}