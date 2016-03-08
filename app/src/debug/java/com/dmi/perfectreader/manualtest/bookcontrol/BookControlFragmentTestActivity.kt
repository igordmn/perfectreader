package com.dmi.perfectreader.manualtest.bookcontrol

import android.os.Bundle
import com.dmi.perfectreader.R
import com.dmi.perfectreader.app.App
import com.dmi.perfectreader.book.BookPresenter
import com.dmi.perfectreader.bookcontrol.BookControlFragment
import com.dmi.perfectreader.bookcontrol.BookControlPresenter
import com.dmi.perfectreader.bookreader.BookReaderPresenter
import com.dmi.perfectreader.setting.AppSettings
import com.dmi.util.base.BaseActivity
import com.dmi.util.layout.HasLayout
import com.dmi.util.log.Log
import com.dmi.util.setting.AbstractSettings
import com.dmi.util.setting.AbstractSettingsApplier
import com.dmi.util.setting.SettingListener
import dagger.ObjectGraph
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@HasLayout(R.layout.activity_container)
class BookControlFragmentTestActivity : BaseActivity() {
    @Inject
    protected lateinit var appSettings: AppSettings

    private val settingsApplier = SettingsApplier()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject(Module())
        settingsApplier.startListen()
        findOrAddChild(TestFragment::class.java, R.id.rootContainer)
    }

    override fun onDestroy() {
        settingsApplier.stopListen()
        super.onDestroy()
    }

    private inner class SettingsApplier : AbstractSettingsApplier() {
        override fun listen() {
            listen(appSettings.format.fontSizePercents)
            listen(appSettings.format.lineHeightPercents)
        }

        private fun <T: Any> listen(setting: AbstractSettings.Setting<T>) {
            listen(setting, listener<T>(setting.name()))
        }

        private fun <T> listener(settingName: String): SettingListener<T> {
            return object: SettingListener<T> {
                override fun onValueSet(value: T) {
                    Log.d("DDD $settingName=$value")
                }
            }
        }
    }

    class TestFragment : BookControlFragment() {
        override fun createObjectGraph(parentGraph: ObjectGraph): ObjectGraph {
            return parentGraph.plus(Module())
        }

        @dagger.Module(addsTo = App.Module::class, injects = arrayOf(TestFragment::class, BookControlPresenter::class))
        inner class Module {
            @Provides
            @Singleton
            fun bookReaderPresenter(): BookReaderPresenter {
                return object : BookReaderPresenter() {
                    override fun toggleMenu() {
                        Log.d("DDD toggleMenu")
                    }

                    override fun exit() {
                        Log.d("DDD exit")
                    }
                }
            }

            @Provides
            @Singleton
            fun bookPresenter(): BookPresenter {
                return object : BookPresenter() {
                    override fun currentPercent(): Double {
                        return 0.0
                    }

                    override fun tap(x: Float, y: Float, tapDiameter: Float, tapHandler: BookPresenter.TapHandler) {
                        Log.d("DDD tap (%.2f, %.2f, %.2f)".format(x, y, tapDiameter))
                        tapHandler.handleTap()
                    }

                    override fun goPercent(percent: Double) {
                        Log.d("DDD goPercent (%.2f)".format(percent))
                    }

                    override fun goNextPage() {
                        Log.d("DDD goNextPage")
                    }

                    override fun goPreviewPage() {
                        Log.d("DDD goPreviewPage")
                    }
                }
            }
        }
    }

    @dagger.Module(addsTo = App.Module::class, injects = arrayOf(BookControlFragmentTestActivity::class))
    inner class Module
}
