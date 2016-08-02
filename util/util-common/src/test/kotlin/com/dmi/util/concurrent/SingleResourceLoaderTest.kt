package com.dmi.util.concurrent

import com.dmi.test.shouldEqual
import org.junit.Test
import rx.schedulers.Schedulers
import java.lang.Thread.sleep

class SingleResourceLoaderTest {
    @Test(timeout = 2000)
    fun `load once`() {
        // given
        val loader = SingleResourceLoader<Int>(Schedulers.newThread(), {})

        // when
        loader.schedule {
            sleep(20)
            return@schedule 5
        }

        // expect
        pickResult(loader) shouldEqual null
        sleep(50)
        pickResult(loader) shouldEqual 5
        pickResult(loader) shouldEqual null
    }

    @Test(timeout = 2000)
    fun `load twice`() {
        // given
        val loader = SingleResourceLoader<Int>(Schedulers.newThread(), {})

        // when
        loader.schedule {
            sleep(20)
            return@schedule 5
        }
        loader.schedule {
            sleep(20)
            return@schedule 7
        }

        // expect
        sleep(100)
        pickResult(loader) shouldEqual 5
        pickResult(loader) shouldEqual null
        sleep(50)
        pickResult(loader) shouldEqual 7
        pickResult(loader) shouldEqual null
    }

    @Test(timeout = 2000)
    fun `second load should not be called when load thrice`() {
        // given
        val loader = SingleResourceLoader<Int>(Schedulers.newThread(), {})

        // when
        loader.schedule {
            sleep(20)
            return@schedule 5
        }
        loader.schedule {
            sleep(20)
            return@schedule 7
        }
        loader.schedule {
            sleep(20)
            return@schedule 9
        }

        // expect
        sleep(100)
        pickResult(loader) shouldEqual 5
        pickResult(loader) shouldEqual null
        sleep(50)
        pickResult(loader) shouldEqual 9
        pickResult(loader) shouldEqual null
    }

    @Test(timeout = 2000)
    fun `load second time after first is ready`() {
        // given
        val loader = SingleResourceLoader<Int>(Schedulers.newThread(), {})

        // when
        loader.schedule {
            sleep(20)
            return@schedule 5
        }
        sleep(50)
        loader.schedule {
            sleep(20)
            return@schedule 7
        }

        // expect
        sleep(50)
        pickResult(loader) shouldEqual 5
        pickResult(loader) shouldEqual null
        sleep(50)
        pickResult(loader) shouldEqual 7
        pickResult(loader) shouldEqual null
    }

    @Test(timeout = 2000)
    fun `onReady called when load result is ready`() {
        // given
        val loader = SingleResourceLoader<Int>(Schedulers.newThread(), {})
        var ready = false

        // when
        loader.schedule {
            sleep(50)
            return@schedule 5
        }
        loader.schedule {
            sleep(50)
            return@schedule 7
        }
        loader.schedule {
            sleep(50)
            return@schedule 9
        }
        loader.onReady.subscribe {
            ready = true
        }

        // expect onReady after first loading
        ready shouldEqual false
        sleep(25)
        ready shouldEqual false
        sleep(50)
        ready shouldEqual true

        // expect no more onReady until complete will be called
        ready = false
        sleep(100)
        ready shouldEqual false

        // expect onReady after second loading
        ready = false
        loader.completeIfReady {}
        sleep(25)
        ready shouldEqual false
        sleep(50)
        ready shouldEqual true

        // expect no more onReady
        ready = false
        loader.completeIfReady {}
        sleep(150)
        ready shouldEqual false
    }

    @Test(timeout = 2000)
    fun `onReady not called after destroy`() {
        // given
        val loader = SingleResourceLoader<Int>(Schedulers.newThread(), {})
        var ready = false

        // when
        loader.schedule {
            sleep(20)
            return@schedule 5
        }
        loader.schedule {
            sleep(20)
            return@schedule 7
        }
        loader.onReady.subscribe {
            ready = true
        }
        loader.destroy()

        // expect
        sleep(60)
        ready shouldEqual false
    }

    @Test(timeout = 2000)
    fun `destroy load result after complete`() {
        // given
        var lastDestroyed: Int? = null
        val loader = SingleResourceLoader<Int>(Schedulers.newThread(), {
            lastDestroyed = it
        })

        // when
        loader.schedule {
            sleep(20)
            return@schedule 5
        }
        loader.schedule {
            sleep(20)
            return@schedule 7
        }
        loader.schedule {
            sleep(20)
            return@schedule 9
        }

        // expect first is destroyed
        lastDestroyed shouldEqual null
        sleep(50)
        lastDestroyed shouldEqual null
        loader.completeIfReady {
            lastDestroyed shouldEqual null
        }
        lastDestroyed shouldEqual 5

        // expect third destroyed
        lastDestroyed = null
        sleep(50)
        lastDestroyed shouldEqual null
        loader.completeIfReady {
            lastDestroyed shouldEqual null
        }
        lastDestroyed shouldEqual 9

        // expect no more destroys
        lastDestroyed = null
        sleep(50)
        lastDestroyed shouldEqual null
        loader.completeIfReady {}
        lastDestroyed shouldEqual null
    }

    @Test(timeout = 2000)
    fun `destroy load result on loader destroy`() {
        // given
        var lastDestroyed: Int? = null
        val loader = SingleResourceLoader<Int>(Schedulers.newThread(), {
            lastDestroyed = it
        })

        // when
        loader.schedule {
            sleep(20)
            return@schedule 5
        }
        loader.schedule {
            sleep(20)
            return@schedule 7
        }
        loader.schedule {
            sleep(20)
            return@schedule 9
        }
        loader.destroy()

        // expect first is destroyed after load
        lastDestroyed shouldEqual null
        sleep(50)
        lastDestroyed shouldEqual 5
        lastDestroyed = null
        sleep(150)
        lastDestroyed shouldEqual null
    }

    fun pickResult(loader: SingleResourceLoader<Int>): Int? {
        var result: Int? = null
        loader.completeIfReady {
            result = it
        }
        return result
    }
}