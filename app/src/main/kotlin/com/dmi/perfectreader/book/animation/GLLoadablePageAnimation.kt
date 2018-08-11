package com.dmi.perfectreader.book.animation

import com.dmi.perfectreader.dataAccessAsync
import com.dmi.util.android.opengl.GLTexture
import com.dmi.util.graphic.SizeF
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.io.getChild
import com.dmi.util.io.parseDOM
import rx.Subscription
import rx.lang.kotlin.PublishSubject
import java.net.URI

class GLLoadablePageAnimation(
        private val size: SizeF,
        initialURL: URI,
        private val uriHandler: ProtocolURIHandler
) {
    val onChanged = PublishSubject<Unit>()

    private var loadSubscription: Subscription
    private var uri: URI = initialURL

    private val loadMutex = Object()
    private @Volatile var animationSource: AnimationSource? = null
    private var animation: GLPageAnimation? = null

    private fun load(uri: URI) = dataAccessAsync {
        synchronized(loadMutex) {
            require(animationSource == null)
            animationSource = loadAnimationSource(uriHandler, uri)
            onChanged.onNext(Unit)
        }
    }

    init {
        loadSubscription = load(initialURL)
    }

    fun destroy() {
        loadSubscription.unsubscribe()
    }

    fun refresh(currentURI: URI) {
        if (uri != currentURI) {
            uri = currentURI
            loadNew(currentURI)
        }
        checkAnimationLoad()
    }

    private fun loadNew(currentURI: URI) = synchronized(loadMutex) {
        loadSubscription.unsubscribe()
        animationSource = null
        loadSubscription = load(currentURI)
    }

    private fun checkAnimationLoad() = synchronized(loadMutex) {
        val animationSource = animationSource
        if (animationSource != null) {
            animation = GLPageAnimation(animationSource.vertexShader, animationSource.fragmentShader, size)
            this.animationSource = null
        }
    }

    fun draw(texture: GLTexture, progress: Float): Unit? {
        return animation?.draw(texture, progress)
    }
}

private fun loadAnimationSource(uriHandler: ProtocolURIHandler, uri: URI): AnimationSource {
    val animation = uriHandler.open(uri).use(::parseDOM).documentElement
    require(animation.nodeName == "animation") { "Root tag name should be \"animation\"" }

    val version = animation.getAttribute("version")
    require(version == "1.0") { "Unsupported version" }

    val vertexShader: String = animation.getChild("vertex_shader").textContent
    val fragmentShader: String = animation.getChild("fragment_shader").textContent

    return AnimationSource(vertexShader, fragmentShader)
}

private class AnimationSource(val vertexShader: String, val fragmentShader: String)