package work.tangthinker.realmail.cache

interface CacheService {

    fun <T> saveCache(cacheName: String, payload: T)

    fun <T> getCache(cacheName: String, cacheClass: Class<T>) : T

    fun isCacheExists(cacheName: String) : Boolean

    fun clearCache(cacheName: String)
}