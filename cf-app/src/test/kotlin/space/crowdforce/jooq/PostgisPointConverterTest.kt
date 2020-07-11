package space.crowdforce.jooq

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import space.crowdforce.jooq.geo.PGPoint

internal class PostgisPointConverterTest {
    companion object {
        private val converter = PostgisPointConverter()
    }

    @Test
    fun zeroParseValue() {
        val pnt = converter.from("(0, 0)")

        assertThat(pnt.x).isEqualTo(0.0)
        assertThat(pnt.y).isEqualTo(0.0)
    }

    @Test
    fun doubleParseValue() {
        val pnt = converter.from("(13.24, 432.21)")

        assertThat(pnt.x).isEqualTo(13.24)
        assertThat(pnt.y).isEqualTo(432.21)
    }

    @Test
    fun nullValue() {
        val pnt = converter.from(null)

        assertThat(pnt).isNull()
    }

    @Test
    fun sameObject() {
        val pnt = converter.from(PGPoint("(13.24, 432.21)"))

        assertThat(pnt.x).isEqualTo(13.24)
        assertThat(pnt.y).isEqualTo(432.21)
    }
}