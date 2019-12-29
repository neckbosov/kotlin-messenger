package dao

typealias Id = Long

interface ObjectDao<T> {
    suspend fun getById(elemId: Id): T?
    suspend fun deleteById(elemId: Id)
    suspend fun size(): Int
}