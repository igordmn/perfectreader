package com.dmi.util.scope

import com.dmi.test.shouldBe
import com.dmi.util.scope.Scope.Companion.onchange
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Test

class ScopeTest {
    private val context = newSingleThreadContext("test")

    @Test
    fun `on change variable`() {
        val obj1 = object {
            val scope = Scope()

            var v1: Int by scope.value(1)
        }

        var v1 = 0
        var changes = 0

        val subscription = onchange {
            v1 = obj1.v1
        }.subscribe {
            changes++
        }

        v1 shouldBe 1
        changes shouldBe 0

        obj1.v1++
        v1 shouldBe 1
        changes shouldBe 1

        obj1.v1++
        v1 shouldBe 1
        changes shouldBe 2

        subscription.dispose()
        obj1.v1++
        v1 shouldBe 1
        changes shouldBe 2
    }

    @Test
    fun `on change two variables`() {
        val obj1 = object {
            val scope = Scope()

            var v1: Int by scope.value(1)
            var v2: Int by scope.value(2)
        }

        var v1 = 0
        var v2 = 0
        var changes = 0

        val subscription = onchange {
            v1 = obj1.v1
            v2 = obj1.v2
        }.subscribe {
            changes++
        }

        v1 shouldBe 1
        v2 shouldBe 2
        changes shouldBe 0

        obj1.v1++
        changes shouldBe 1

        obj1.v2++
        changes shouldBe 2

        subscription.dispose()
        obj1.v1++
        obj1.v2++
        changes shouldBe 2
    }

    @Test
    fun `on change computed`() {
        val obj1 = object {
            val scope = Scope()

            var v1: Int by scope.value(1)
            var v2: Int by scope.value(2)
        }

        val obj2 = object {
            val scope = Scope()

            val v3: Int by scope.cached { obj1.v1 + obj1.v2 }
        }

        var v3 = 0
        var changes = 0

        val subscription = onchange {
            v3 = obj2.v3
        }.subscribe {
            changes++
        }

        obj2.v3 shouldBe 3
        v3 shouldBe 3
        changes shouldBe 0

        obj1.v1++
        obj2.v3 shouldBe 4
        v3 shouldBe 3
        changes shouldBe 1

        obj1.v2++
        obj2.v3 shouldBe 5
        v3 shouldBe 3
        changes shouldBe 2

        subscription.dispose()
        obj1.v1++
        obj1.v2++
        changes shouldBe 2
    }

    @Test
    fun `on change complex computed`() {
        val obj1 = object {
            val scope = Scope()

            var v1: Int by scope.value(1)
            var v2: Int by scope.value(2)
            val v3: Int by scope.cached { v1 + v2 }
        }

        val obj2 = object {
            val scope = Scope()

            val v4: Int by scope.cached { obj1.v2 + obj1.v3 }  // v1 + 2 * v2
        }

        val obj3 = object {
            val scope = Scope()

            val v5: Int by scope.cached { obj1.v3 + obj2.v4 }  // 2 * v1 + 3 * v2
        }

        var v4 = 0
        var v5 = 0
        var changes = 0

        val subscription = onchange {
            v4 = obj2.v4
            v5 = obj3.v5
        }.subscribe {
            changes++
        }

        obj2.v4 shouldBe 5
        obj3.v5 shouldBe 8
        v4 shouldBe 5
        v5 shouldBe 8
        changes shouldBe 0

        obj1.v1++
        obj2.v4 shouldBe 6
        obj3.v5 shouldBe 10
        v4 shouldBe 5
        v5 shouldBe 8
        changes shouldBe 2

        obj1.v2++
        obj2.v4 shouldBe 8
        obj3.v5 shouldBe 13
        v4 shouldBe 5
        v5 shouldBe 8
        changes shouldBe 4

        subscription.dispose()
        obj1.v1++
        obj1.v2++
        changes shouldBe 4
    }

    @Test
    fun `on change computed with 'if'`() {
        val obj1 = object {
            val scope = Scope()

            var v1: Boolean by scope.value(false)
            var v2: Int by scope.value(1)
            var v3: Int by scope.value(20)
            val v4: Int by scope.cached { v2 + 1 }
            val v5: Int by scope.cached { v3 - 1 }
            val v6: Int by scope.cached { if (v1) v4 else v5 }
        }

        var v6 = 0
        var changes = 0

        val subscription = onchange {
            v6 = obj1.v6
        }.subscribe {
            changes++
        }

        obj1.v6 shouldBe 19
        v6 shouldBe 19
        changes shouldBe 0

        obj1.v1 = true
        obj1.v6 shouldBe 2
        changes shouldBe 1

        obj1.v1 = false
        obj1.v6 shouldBe 19
        changes shouldBe 2

        obj1.v2 = 0
        obj1.v6 shouldBe 19
        changes shouldBe 2

        obj1.v1 = true
        obj1.v6 shouldBe 1
        changes shouldBe 3

        obj1.v3 = 21
        obj1.v6 shouldBe 1
        changes shouldBe 3

        obj1.v2 = -1
        obj1.v6 shouldBe 0
        changes shouldBe 4

        subscription.dispose()
        obj1.v1 = true
        obj1.v2++
        obj1.v3++
        changes shouldBe 4
    }

    @Test
    fun `stop computing after dispose`() {
        val obj1 = object {
            val scope = Scope()

            var v1: Int by scope.value(1)
        }

        val obj2 = object {
            val scope = Scope()

            val v2: Int by scope.cached { obj1.v1 + 1 }
        }

        obj2.v2 shouldBe 2

        obj2.scope.dispose()
        obj1.v1++
        obj2.v2 shouldBe 2
    }

    @Test
    fun `on change async value without suspend`() = runBlocking(context) {
        val obj1 = object {
            val scope = Scope()
            var v1: Int by scope.value(1)
        }

        val obj2 = object {
            val scope = Scope()
            val v2: Int? by scope.async(context) { obj1.v1 + 1 }
        }

        obj2.v2 shouldBe null

        yield()
        obj2.v2 shouldBe 2

        obj1.v1++
        obj2.v2 shouldBe null

        yield()
        obj2.v2 shouldBe 3

        obj2.scope.dispose()
        obj1.v1++
        yield()
        obj2.v2 shouldBe 3
    }

    @Test
    fun `on change async value with suspend`() = runBlocking(context) {
        val obj1 = object {
            val scope = Scope()
            var v1: Int by scope.value(1)
            var v2: Int by scope.value(2)
        }

        val obj2 = object {
            val scope = Scope()
            val v3: Int? by scope.async(context) {
                val v1 = obj1.v1
                yield()
                val v2 = obj1.v2
                v1 + v2
            }
        }

        obj2.v3 shouldBe null
        yield()
        obj2.v3 shouldBe null
        yield()
        obj2.v3 shouldBe 3

        obj1.v1++
        obj2.v3 shouldBe null
        yield()
        obj2.v3 shouldBe null
        yield()
        obj2.v3 shouldBe 4

        obj1.v2++
        obj2.v3 shouldBe null
        yield()
        obj2.v3 shouldBe null
        yield()
        obj2.v3 shouldBe 5

        obj1.v1++
        obj2.v3 shouldBe null
        yield()
        obj2.v3 shouldBe null
        obj1.v1++
        yield()
        obj2.v3 shouldBe null
        yield()
        obj2.v3 shouldBe 7
    }

    @Test
    fun `nested scopes`() {
        abstract class Obj1(val scope: Scope = Scope()) : Disposable by scope {
            var v: Int by scope.value(1)
            abstract val obj3: Any
        }

        class Obj2(obj11: Obj1, val scope: Scope = Scope()) : Disposable by scope {
            val v1 by scope.cached { obj11.v + 1 }
            val v2 = obj11.v + 1
        }

        class Obj3(obj2: Obj2, val scope: Scope = Scope()) : Disposable by scope {
            val obj2 by scope.disposable(obj2)
            var v1 by scope.value(1)
            val v2 by scope.cached { obj2.v1 + v1 }
            val v3 = v1
        }

        val obj1 = object : Obj1() {
            override val obj3: Obj3 by scope.cachedDisposable {
                Obj3(Obj2(this))
            }
        }

        obj1.v shouldBe 1
        obj1.obj3.obj2.v1 shouldBe 2
        obj1.obj3.obj2.v2 shouldBe 2
        obj1.obj3.v1 shouldBe 1
        obj1.obj3.v2 shouldBe 3
        obj1.obj3.v3 shouldBe 1

        obj1.v++
        obj1.v shouldBe 2
        obj1.obj3.obj2.v1 shouldBe 3
        obj1.obj3.obj2.v2 shouldBe 3
        obj1.obj3.v1 shouldBe 1
        obj1.obj3.v2 shouldBe 4
        obj1.obj3.v3 shouldBe 1

        val oldObj3 = obj1.obj3
        obj1.obj3.v1 = 2
        obj1.obj3 shouldBe oldObj3
        obj1.obj3.v1 shouldBe 2
        obj1.obj3.v2 shouldBe 5
        obj1.obj3.v3 shouldBe 1
    }

    @Test
    fun `simple async`() = runBlocking(context) {
        val scope = Scope()
        val v1 by scope.value(1)
        val v2: Int? by scope.async(context) {
            var x = v1
            yield()
            x += v1
            x
        }

        v1 shouldBe 1
        v2 shouldBe null

        yield()
        v1 shouldBe 1
        v2 shouldBe null

        yield()
        v1 shouldBe 1
        v2 shouldBe 2
    }

    @Test
    fun `nested async computed`() = runBlocking(context) {
        class Nested(val scope: Scope = Scope()) : Disposable by scope {
            var v3 by scope.value(2)
            var x: Int = v3
        }

        val scope1 = Scope()
        val nested: Nested? by scope1.asyncDisposable(context) {
            val nested = Nested()
            yield()
            nested.x = nested.v3 + 1
            nested
        }

        nested shouldBe null

        yield()
        nested shouldBe null

        yield()
        nested!!.v3 shouldBe 2
        nested.x shouldBe 3

        nested.v3 = 3
        nested.v3 shouldBe 3
        nested.x shouldBe 3

        yield()
        nested.v3 shouldBe 3
        nested.x shouldBe 3

        yield()
        nested.v3 shouldBe 3
        nested.x shouldBe 3
    }
}