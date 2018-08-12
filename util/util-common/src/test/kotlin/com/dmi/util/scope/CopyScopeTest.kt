package com.dmi.util.scope

import com.dmi.test.shouldBe
import com.dmi.util.scope.Scope.Companion.onchange
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Test
import java.util.concurrent.CountDownLatch

class CopyScopeTest {
    private val writeContext = newSingleThreadContext("test")
    private val copyContext = newSingleThreadContext("test")

    @Test(timeout = 1000)
    fun `read variables in copy context`() {
        runBlocking(writeContext) {
            val obj = object {
                val scope = Scope()

                var v1: Int by scope.value(1)
                var v2: Int by scope.value(2)
            }

            val copy = object {
                val scope = CopyScope(copyContext, writeContext)

                val v3 by scope.computed { obj.v1 + 1 }
                val v4 by scope.computed { obj.v2 + 1 }
            }

            val read = CountDownLatch(1)
            val wrote = CountDownLatch(1)

            var jobException: Throwable? = null
            val job = launch(copyContext, onCompletion = { jobException = it }) {
                val readCopy = object {
                    val scope = Scope()
                    val v5 by scope.cached { copy.v3 }
                    val v6 by scope.cached { copy.v4 }
                }

                readCopy.v5 shouldBe 2
                readCopy.v6 shouldBe 3
                copy.v3 shouldBe 2
                copy.v4 shouldBe 3

                read.countDown()
                wrote.await()

                readCopy.v5 shouldBe 2
                readCopy.v6 shouldBe 3
                copy.v3 shouldBe 2
                copy.v4 shouldBe 3

                Thread.sleep(100)

                readCopy.v5 shouldBe 2
                readCopy.v6 shouldBe 3
                copy.v3 shouldBe 2
                copy.v4 shouldBe 3
                yield()

                readCopy.v5 shouldBe 3
                readCopy.v6 shouldBe 4
                copy.v3 shouldBe 3
                copy.v4 shouldBe 4
            }

            read.await()

            obj.v1 = 2
            obj.v2 = 3

            yield()

            wrote.countDown()

            job.join()
            jobException?.let {
                throw it
            }
        }
    }

    @Test(timeout = 1000)
    fun `subscribe variables in copy context`() {
        runBlocking(writeContext) {
            val obj = object {
                val scope = Scope()

                var v1: Int by scope.value(1)
                var v2: Int by scope.value(2)
            }

            val copy = object {
                val scope = CopyScope(copyContext, writeContext)

                val v3 by scope.computed { obj.v1 + 1 }
                val v4 by scope.computed { obj.v2 + 1 }
            }

            val read = CountDownLatch(1)

            var jobException: Throwable? = null
            val job = launch(copyContext, onCompletion = { jobException = it }) {
                val readCopy = object {
                    val scope = Scope()
                    val v5 by scope.cached { copy.v3 }
                    val v6 by scope.cached { copy.v4 }
                }

                readCopy.v5 shouldBe 2
                readCopy.v6 shouldBe 3
                copy.v3 shouldBe 2
                copy.v4 shouldBe 3

                val onchange = onchange {
                    readCopy.v5
                    readCopy.v6
                }
                read.countDown()

                onchange.wait()

                readCopy.v5 shouldBe 3
                readCopy.v6 shouldBe 4
                copy.v3 shouldBe 3
                copy.v4 shouldBe 4
            }

            read.await()

            obj.v1 = 2
            obj.v2 = 3

            job.join()
            jobException?.let {
                throw it
            }
        }
    }

    @Test(timeout = 10000)
    fun `test many fast writes and reads`() {
        runBlocking(writeContext) {
            val obj = object {
                val scope = Scope()

                var v1: Int by scope.value(1)
                var v2: Int by scope.value(2)
            }

            val copy = object {
                val scope = CopyScope(copyContext, writeContext)

                val v3 by scope.computed { obj.v1 + 1 }
                val v4 by scope.computed { obj.v2 + 1 }
            }

            val ready = CountDownLatch(2)

            var jobException: Throwable? = null
            val job = launch(copyContext, onCompletion = { jobException = it }) {
                val readCopy = object {
                    val scope = Scope()
                    val v5 by scope.cached { copy.v3 }
                    val v6 by scope.cached { copy.v4 }
                }

                ready.countDown()
                ready.await()

                repeat(100000) {
                    (readCopy.v6 - readCopy.v5) shouldBe 1
                    (copy.v4 - copy.v3) shouldBe 1
                    yield()
                }
            }

            ready.countDown()
            ready.await()

            repeat(100000) {
                obj.v1++
                obj.v2++
                yield()
            }

            job.join()
            jobException?.let {
                throw it
            }
        }
    }
}