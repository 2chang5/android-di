package woowacourse.shopping.util.autoDI.dependencyContainer

import woowacourse.shopping.util.autoDI.LifeCycleType
import kotlin.reflect.typeOf

sealed class LifeCycleTypes {
    abstract val value: MutableList<out LifeCycleType<*>>
    inline fun <reified T> searchWithOutQualifier(): T? {
        val type = typeOf<T>()
        return value.find { it.type == type }?.getInstance() as T?
    }

    inline fun <reified T> searchWithQualifier(qualifier: String): T? {
        val type = typeOf<T>()
        return value.find { it.type == type && it.qualifier == qualifier }?.getInstance() as T?
    }

    class Singletons(override val value: MutableList<LifeCycleType.Singleton<*>>) :
        LifeCycleTypes() {
        fun <T : Any> add(qualifier: String? = null, initializeMethod: () -> T) {
            value.add(LifeCycleType.Singleton(qualifier, initializeMethod))
        }
    }

    class Disposables(override val value: MutableList<LifeCycleType.Disposable<*>>) :
        LifeCycleTypes() {
        fun <T : Any> add(qualifier: String? = null, initializeMethod: () -> T) {
            value.add(LifeCycleType.Disposable(qualifier, initializeMethod))
        }
    }
//    class Activities(override val values: MutableList<LifeCycleType<*>>) : LifeCycleTypes()
//    class Fragments(override val values: MutableList<LifeCycleType<*>>) : LifeCycleTypes()
}
