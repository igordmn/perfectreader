package com.dmi.util.xml

import com.dmi.test.shouldBe
import com.dmi.util.lang.required
import org.junit.Test
import java.nio.charset.Charset

class XmlDescTest {
    class Employee : ElementDesc() {
        val name: String? by attribute("name")
        val nick: String? by attribute("nick")
        val fullName: String? by element("full-name")
        val fullNick: String? by element("full-nick")
    }

    class Employees : ElementDesc() {
        val list: List<Employee> by elements("employee", ::Employee)
    }

    class City : ElementDesc() {
        val name: String? by attribute("name")
        val fullName: String? by element("full-name")
        val children: List<City> by elements("city", ::City)
    }

    abstract class Inline : ElementDesc() {
        val children: List<XMLDesc> by nodes("b" to ::B, "i" to ::I)
    }

    class B : Inline() {
        val very: String? by attribute("very")
    }

    class I : Inline()

    class Body : Inline()

    class Root : ElementDesc() {
        val employees: Employees by element("employees", ::Employees).required()
        val cities: List<City> by elements("city", ::City)
        val body: Body by element("body", ::Body).required()
    }

    @Test
    fun `test complex xml`() {
        val root = javaClass.getResourceAsStream("/com/dmi/util/io/complex.xml").reader(Charset.forName("windows-1251")).use {
            parseDesc(it, "root", ::Root)
        }

        root.employees.list.apply {
            size shouldBe 2
            this[0].apply {
                name shouldBe "Олег"
                nick shouldBe "x"
                fullName shouldBe "Олег Пупкин"
                fullNick shouldBe "xxx"
            }
            this[1].apply {
                name shouldBe "Jack"
                nick shouldBe null
                fullName shouldBe null
                fullNick shouldBe null
            }
        }

        root.cities.apply {
            size shouldBe 2
            this[0].apply {
                name shouldBe "Albagrama"
                fullName shouldBe null
                children.size shouldBe 1
                children[0].apply {
                    name shouldBe null
                    fullName shouldBe "Bendrol"
                    children.size shouldBe 0
                }
            }
            this[1].apply {
                name shouldBe "New Albagrama"
                fullName shouldBe null
                children.size shouldBe 0
            }
        }

        root.body.apply {
            children.size shouldBe 3
            index shouldBe 63
            children[0].apply {
                this as Text
                index shouldBe 64
                data shouldBe "\n        Very "
            }
            children[1].apply {
                this as B
                index shouldBe 65
                very shouldBe "true"
                children.size shouldBe 5
                children[0].apply {
                    this as Text
                    index shouldBe 66
                    data shouldBe " bold "
                }
                children[1].apply {
                    this as I
                    index shouldBe 67
                    children.size shouldBe 1
                    children[0].apply {
                        this as Text
                        data shouldBe " italic "
                    }
                }
                children[2].apply {
                    this as Text
                    index shouldBe 70
                    data shouldBe " text "
                }
                children[3].apply {
                    this as B
                    index shouldBe 71
                    very shouldBe null
                    children.size shouldBe 1
                    children[0].apply {
                        this as Text
                        data shouldBe "!"
                    }
                }
                children[4].apply {
                    index shouldBe 74
                    this as Text
                    data shouldBe " "
                }
            }
            children[2].apply {
                this as Text
                index shouldBe 76
                data shouldBe "\n        ...\n    "
            }
        }
    }
}