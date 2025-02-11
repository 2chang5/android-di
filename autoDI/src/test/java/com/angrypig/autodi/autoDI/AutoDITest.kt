package com.angrypig.autodi.autoDI

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.angrypig.autodi.AutoDI
import com.angrypig.autodi.autoDIModule.autoDIModule
import com.angrypig.autodi.injectViewModel
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

private class TestDependency(var testValue: String)

private interface TestThing {
    val testValue: String
}

private class Test1(override val testValue: String) : TestThing
private class Test2(override val testValue: String) : TestThing
private class Test3(override val testValue: String) : TestThing

private class TestViewModel(var testValue: String) : ViewModel()

private class TestActivity : AppCompatActivity() {
    val viewModel: TestViewModel by injectViewModel()
}

@RunWith(RobolectricTestRunner::class)
class AutoDITest {

    @After
    fun tearDown() {
        clearAutoDIContainer()
    }

    private fun clearAutoDIContainer() {
        AutoDI.clearModuleContainer()
    }

    @Test(expected = IllegalStateException::class)
    fun `clearModuleContainer가 잘 동작한다(autoDIContainer 비우기)`() {
        // given
        val noQualifierModule = autoDIModule {
            singleton { TestDependency("meaningless") }
        }
        AutoDI {
            registerModule(noQualifierModule)
        }

        // when
        AutoDI.clearModuleContainer()

        // then
        // 성공 시나리오시 exception 발생하지않음
        AutoDI.inject<TestDependency>()
    }

    @Test
    fun `qualifier 없는 모듈을 등록한다`() {
        // given
        val noQualifierModule = autoDIModule {
            singleton { TestDependency("meaningless") }
        }

        // when
        AutoDI {
            registerModule(noQualifierModule)
        }

        // then
        // 성공 시나리오시 exception 발생하지않음
        AutoDI.inject<TestDependency>()
    }

    @Test
    fun `모듈을 등록할때 존재하는 qualifier인경우 덮어쓴다`() {
        // given
        val existingString = "existing"
        val existingModule = autoDIModule(existingString) {
            singleton { TestDependency(existingString) }
        }
        val overrideString = "override"
        val overrideModule = autoDIModule(existingString) {
            singleton { TestDependency(overrideString) }
        }
        AutoDI.registerModule(existingModule)

        // when
        AutoDI.registerModule(overrideModule)

        // then
        assertThat(AutoDI.inject<TestDependency>().testValue).isEqualTo(overrideString)
    }

    @Test
    fun `모듈을 override한다`() {
        // given
        val existingString = "existing"
        val existingModule = autoDIModule(existingString) {
            singleton { TestDependency(existingString) }
        }
        val overrideString = "override"
        val overrideModule = autoDIModule(overrideString) {
            singleton { TestDependency(overrideString) }
        }
        AutoDI.registerModule(existingModule)

        // when
        AutoDI.overrideModule(existingString, overrideModule)

        // then
        assertThat(AutoDI.inject<TestDependency>().testValue).isEqualTo(overrideString)
    }

    @Test
    fun `String값 "멧돼지"를 주입받는 Pig 객체를 inject함수를 통해 주입받는다`() {
        // given
        class Pig(val name: String)
        AutoDI {
            registerModule(
                autoDIModule {
                    disposable { Pig(AutoDI.inject("pigName")) }
                    disposable("pigName") { "멧돼지" }
                },
            )
        }

        // when
        val injectedInstance = AutoDI.inject<Pig>()

        // then
        val pigName = "멧돼지"
        assertThat(injectedInstance.name).isEqualTo(pigName)
    }

    @Test
    fun `개별적인 LifeCycleType 을 override한다(singleton 생명주기로 테스트)`() {
        // given
        val firstString = "First"
        val firstModule = autoDIModule {
            singleton<TestThing>(firstString) { Test1(firstString) }
        }
        val extraString = "Extra"
        val extraModule = autoDIModule {
            singleton<TestThing> { Test2(extraString) }
        }
        AutoDI {
            registerModule(firstModule)
            registerModule(extraModule)
        }

        // when
        val overrideString = "override"
        val overrideInitializeMethod = { Test3(overrideString) }
        AutoDI.overrideSingleLifeCycleType<TestThing>(firstString, overrideInitializeMethod)

        // then
        assertThat(AutoDI.inject<TestThing>(firstString).testValue).isEqualTo(overrideString)
    }

    @Test
    fun `개별적인 LifeCycleType 을 override한다(disposable 생명주기로 테스트)`() {
        // given
        val firstString = "First"
        val firstModule = autoDIModule {
            disposable<TestThing>(firstString) { Test1(firstString) }
        }
        val extraString = "Extra"
        val extraModule = autoDIModule {
            disposable<TestThing> { Test2(extraString) }
        }
        AutoDI {
            registerModule(firstModule)
            registerModule(extraModule)
        }

        // when
        val overrideString = "override"
        val overrideInitializeMethod = { Test3(overrideString) }
        AutoDI.overrideSingleLifeCycleType<TestThing>(firstString, overrideInitializeMethod)

        // then
        assertThat(AutoDI.inject<TestThing>(firstString).testValue).isEqualTo(overrideString)
    }

    @Test
    fun `개별적인 viewModel 을 override한다`() {
        // given
        val firstString = "First"
        val firstModule = autoDIModule {
            viewModel { TestViewModel(firstString) }
        }
        val extraString = "Extra"
        val extraModule = autoDIModule {
            disposable<TestThing> { Test2(extraString) }
        }
        AutoDI {
            registerModule(firstModule)
            registerModule(extraModule)
        }

        // when
        val overrideString = "override"
        val overrideInitializeMethod = { TestViewModel(overrideString) }
        AutoDI.overrideSingleViewModel<TestViewModel>(overrideInitializeMethod)

        val activity: TestActivity? = Robolectric
            .buildActivity(TestActivity::class.java)
            .create()
            .get()
        val viewModel = activity?.viewModel

        // then
        assertThat(viewModel?.testValue).isEqualTo(overrideString)
    }
}
