package com.angrypig.autodi

import android.content.Context
import androidx.lifecycle.ViewModel
import com.angrypig.autodi.autoDIContainer.AutoDIModuleContainer
import com.angrypig.autodi.autoDIModule.AutoDIModule
import com.angrypig.autodi.autoDIModule.autoDIModule
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object AutoDI {
    operator fun invoke(init: AutoDI.() -> Unit) {
        this.init()
    }

    fun registerApplicationContext(applicationContext: Context) {
        AutoDIModuleContainer.registerApplicationContext(applicationContext)
    }

    fun registerModule(autoDIModule: AutoDIModule) {
        AutoDIModuleContainer.registerModule(autoDIModule)
    }

    fun overrideModule(qualifier: String, autoDIModule: AutoDIModule) {
        AutoDIModuleContainer.overrideModule(qualifier, autoDIModule)
    }

    inline fun <reified T : Any> overrideSingleLifeCycleType(
        qualifier: String,
        noinline initializeMethod: () -> T,
    ) {
        val kType = typeOf<T>()
        publishedOverrideSingleLifeCycleType(kType, qualifier, initializeMethod)
    }

    @PublishedApi
    internal fun <T : Any> publishedOverrideSingleLifeCycleType(
        kType: KType,
        qualifier: String,
        initializeMethod: () -> T,
    ) {
        AutoDIModuleContainer.overrideSingleLifeCycleType<T>(kType, qualifier, initializeMethod)
    }

    inline fun <reified VM : ViewModel> overrideSingleViewModel(
        noinline initializeMethod: () -> VM,
    ) {
        val kType = typeOf<VM>()
        publishedOverrideViewModelBundle(kType, initializeMethod)
    }

    @PublishedApi
    internal fun <VM : ViewModel> publishedOverrideViewModelBundle(
        kType: KType,
        initializeMethod: () -> VM,
    ) {
        AutoDIModuleContainer.overrideSingleViewModelBundle<VM>(kType, initializeMethod)
    }

    inline fun <reified T : Any> inject(qualifier: String? = null): T {
        val kType = typeOf<T>()
        return publishedSearchLifeCycleType(kType, qualifier)
    }

    @PublishedApi
    internal fun <T : Any> publishedSearchLifeCycleType(kType: KType, qualifier: String?): T =
        AutoDIModuleContainer.searchLifeCycleType<T>(kType, qualifier).getInstance()

    fun injectApplicationContext(): Context = AutoDIModuleContainer.getApplicationContext()

    fun clearModuleContainer() {
        AutoDIModuleContainer.clear()
    }
}
