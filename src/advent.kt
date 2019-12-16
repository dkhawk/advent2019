import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import java.io.File
import java.util.concurrent.TimeUnit

fun readFileAsLinesUsingReadLines(fileName: String): List<String> = File(fileName).readLines()

fun main() {
//    Day12.test()
//    rxJavaTesting()
//    Day13.test()
    Day14.test()
//    Day15.test()
//    Day16.test()
}

private fun rxJavaTesting() {
    val source = Observable.just(10, 9, 8, 7, 6, 5, 4, 3, 2, 1).delay(2, TimeUnit.SECONDS)

    var disposable = source.subscribeWith(object : DisposableObserver<Int>() {
        override fun onComplete() {
            println("complete")
        }

        override fun onNext(p0: Int) {
            println("next: $p0")
        }

        override fun onError(p0: Throwable) {
            println("error")
        }
    })

    Thread.sleep(3000)

    if (!disposable.isDisposed) {
        disposable.dispose()
    }
}

class Utils {
    companion object {
        fun sign(value: Int) : Int {
            return when {
                value > 0 -> 1
                value < 0 -> -1
                else -> 0
            }
        }

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
