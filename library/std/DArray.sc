//@#include "DArray.h"
/**
* Dynamic Array
*/
extern noncopyable struct DArray$<T> {
    fun data(): raw* T;

    fun size(): Int;

    fun add(d: T) mut;

    operator fun get(i: Int): ref* T;

    operator fun set(i: Int, d: T) mut;

    fun resize(size: Int);
    fun reserve(capacity: Int);
    fun removeAt(i: Int);

}
