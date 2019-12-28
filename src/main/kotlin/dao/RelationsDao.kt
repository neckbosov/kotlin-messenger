package dao

interface RelationsDao<X, Y> {
    suspend fun add(key: X, value: Y): Boolean
    suspend fun remove(key: X, value: Y): Boolean
    suspend fun select(key: X): List<Y>
    suspend fun contains(key: X, value: Y): Boolean
}