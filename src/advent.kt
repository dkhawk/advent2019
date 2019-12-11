import java.io.File

fun readFileAsLinesUsingReadLines(fileName: String): List<String> = File(fileName).readLines()

fun main() {
    Day11.test()
}

class Utils {
    companion object {
        const val NO_COLOR = "\u001B[0m"
    }

    enum class COLORS(bright: Int, color: Int) {
        BLACK(0, 30),
        RED(0, 31),
        GREEN(0, 32),
        BROWN(0, 33),
        BLUE(0, 34),
        MAGENTA(0, 35),
        CYAN(0, 36),
        LT_GRAY(0, 37),
        GRAY(1, 30),
        LT_RED(1, 31),
        LT_GREEN(1, 32),
        YELLOW(1, 33),
        LT_BLUE(1, 34),
        LT_MAGENTA(1, 35),
        LT_CYAN(1, 36),
        WHITE(1, 37);

        val value = "\u001B[%d;%dm".format(bright, color)

        override fun toString(): String {
            return value
        }
    }
}

