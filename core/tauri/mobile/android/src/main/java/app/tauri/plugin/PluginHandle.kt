package app.tauri.plugin

import android.webkit.WebView
import java.lang.reflect.Method

class PluginHandle(val instance: Plugin) {
  private val pluginMethods: HashMap<String, PluginMethodData> = HashMap()
  var loaded = false

  init {
    indexMethods()
  }

  fun load(webView: WebView) {
    instance.load(webView)
    loaded = true
  }

  @Throws(
    InvalidPluginMethodException::class,
    IllegalAccessException::class
  )
  fun invoke(methodName: String, invoke: Invoke) {
    val methodMeta = pluginMethods[methodName]
      ?: throw InvalidPluginMethodException("No method " + methodName + " found for plugin " + instance.javaClass.name)
    methodMeta.method.invoke(instance, invoke)
  }

  private fun indexMethods() {
    val methods: Array<Method> = instance.javaClass.methods
    for (methodReflect in methods) {
      val method: PluginMethod = methodReflect.getAnnotation(PluginMethod::class.java) ?: continue
      val methodMeta = PluginMethodData(methodReflect, method)
      pluginMethods.put(methodReflect.name, methodMeta)
    }
  }
}