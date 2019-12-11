import java.io.File
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.max
import kotlin.math.min


/*
class Day3 {
    companion object {
        val sampleInput = """
            Sample input
        """.trimIndent()

//        val sampleWires = listOf("R8,U5,L5,D3", "U7,R6,D4,L4")

//        val sampleWires = listOf(
//        "R75,D30,R83,U83,L12,D49,R71,U7,L72",
//        "U62,R66,U55,R34,D71,R55,D58,R83"
//        )

        val sampleWires = listOf("R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51",
            "U98,R91,D20,R16,D67,R40,U7,R15,U6,R7")

        val input = File("/Users/dkhawk/Downloads/2019/input-3.txt").readText().split("\n")

        fun test() {
            Day3().run {
                part1()
            }
        }
    }

    private fun part1() {
        val input = input
        val world = World()
        val wire = parseWire(input[0])

        world.addWire(wire)

        val wire2 = parseWire(input[1])
        world.addWire(wire2)

        val closest = world.findClosestIntersection()
        println(closest)
        closest?.let {
            val sum = world.grid[it]?.sumBy { steps -> steps.numberOfSteps }
            println("Sum: $sum")
            // For part1
            // val distance = abs(it.x) + abs(it.y)
            // println("Dist: $distance")
        }
    }

    data class BoundingBox(val xMin: Int, val yMin: Int, val xMax: Int, val yMax: Int, val width: Int, val height: Int)

    class BoundingBoxBuilder() {
        var xMin = Int.MAX_VALUE
        var xMax = Int.MIN_VALUE
        var yMin = Int.MAX_VALUE
        var yMax = Int.MIN_VALUE

        fun add(x: Int, y: Int): BoundingBoxBuilder {
            xMin = min(xMin, x)
            xMax = max(xMax, x)
            yMin = min(yMin, y)
            yMax = max(yMax, y)
            return this
        }

        fun build(border: Int = 0): BoundingBox {
            xMin -= border
            xMax += border + 1
            yMin -= border
            yMax += border + 1
            val width = xMax - xMin
            val height = yMax - yMin

            return BoundingBox(xMin, yMin, xMax, yMax, width, height)
        }
    }

    private fun plotWire(wire: Wire) {
        val xMin = wire.locations.minBy(Location::x)?.x ?: Int.MAX_VALUE
        val xMax = wire.locations.maxBy(Location::x)?.x ?: Int.MIN_VALUE
        val yMin = wire.locations.minBy(Location::y)?.y ?: Int.MAX_VALUE
        val yMax = wire.locations.maxBy(Location::y)?.y ?: Int.MIN_VALUE
        val bb = BoundingBox(xMin, yMin, xMax, yMax, xMax - xMin, yMax - yMin)
        val grid = ArrayList<String>(bb.width * bb.height)
        repeat((bb.width + 1) * (bb.height + 1)) { index ->
            grid.add("   .   ")
        }
        wire.locations.forEachIndexed { index, location ->
            val index = (location.y - bb.yMin) * bb.width + (location.x - bb.xMin)
            grid[index] = " %05d ".format(index + 1)
        }
        grid.toList().windowed(bb.width, bb.width) {
            println(it.joinToString(""))
        }
    }

    data class WireSteps(val wireId: Int, val numberOfSteps: Int)

    class World {
        val grid = HashMap<Location, ArrayList<WireSteps>>()
        val wires = HashMap<Int, Wire>()

        fun addWire(wire: Wire) {
            wires[wire.id] = wire
            wire.locations.forEachIndexed { index, location ->
                grid.getOrPut(location) { ArrayList() }.add(WireSteps(wire.id, index + 1))
            }
        }

        fun findClosestIntersection(): Location? {
            grid.remove(Location(0, 0))

            val sums = HashMap<Location, Int>()

            grid.filterValues {
                if (it.size < 1) {
                    false
                } else {
                    val wireIds = HashSet<Int>()
                    it.forEach { step ->
                        wireIds.add(step.wireId)
                    }
                    wireIds.size > 1
                }
            }.map { sums[it.key] = it.value.sumBy(WireSteps::numberOfSteps) }

            return sums.minBy { it.value }?.key
        }
    }

    class WireBuilder() {
        companion object {
            var wireId = 0
        }
        val nodes = ArrayList<Node>()
        data class Node(val direction: Char, val distance: Int)

        fun add(direction: Char, distance: Int) : WireBuilder {
            nodes.add(Node(direction, distance))
            return this
        }

        fun build() : Wire {
            var location = Location(0, 0)
            val locations = ArrayList<Location>()
            nodes.forEach { node->
                var x2 = location.x
                var y2 = location.y
                val x1 = location.x
                val y1 = location.y

                // Note: the -/+ 1's to skip the first location (which is either 0,0 or the end of previous node)
                when(node.direction) {
                    'U' -> { y2 -= node.distance; for( y in (y1-1) downTo y2) locations.add(Location(x1, y)) }
                    'D' -> { y2 += node.distance; for( y in (y1+1)..y2) locations.add(Location(x1, y)) }
                    'L' -> { x2 -= node.distance; for( x in (x1-1) downTo x2) locations.add(Location(x, y1)) }
                    'R' -> { x2 += node.distance; for( x in (x1+1)..x2) locations.add(Location(x, y1)) }
                    else -> throw RuntimeException("Illegal direction ${node.direction}")
                }
                location = Location(x2, y2)
            }

            return Wire(wireId++, locations)
        }
    }

    data class Wire(val id: Int, val locations: List<Location>)

    data class Location(val x: Int, val y: Int)

    private fun parseWire(wireString: String): Wire {
        val wireBuilder = WireBuilder()
        wireString.split(",").map { part ->
            val direction = part[0]
            val distance = part.substring(1).toInt()
            wireBuilder.add(direction, distance)
        }

        return wireBuilder.build()
    }
}

class Day4 {
    companion object {
        val minPin = 153517
        //        val maxPin = minPin + 10
        val maxPin = 630395

        fun test() {
            val d = Day4()
            d.part1()
        }
    }

    private fun part1() {
//        val digits = 123456.toString().map { it.toInt() - '0'.toInt() }
//        println(digitsNeverDecrease(digits))
//        println(conseqSame(digits))

        var count = 0

        for (p in minPin..maxPin) {
            val digits = p.toString().map { it.toInt() - '0'.toInt() }
//            println(digits.joinToString())
            val noDecrease = digitsNeverDecrease(digits)
            val conseq = conseqSame(digits)
            if (noDecrease && conseq) {
                count++
            }
        }

        println(count)
//        { pin:Int ->
//
//        }
    }

    private fun conseqSame(d: List<Int>): Boolean {
        var answer = false

        if ((d[0] == d[1] && (d[1] != d[2]))
            || ((d[0] != d[1]) && (d[1] == d[2] && (d[2] != d[3])))
            || ((d[1] != d[2]) && (d[2] == d[3] && (d[3] != d[4])))
            || ((d[2] != d[3]) && (d[3] == d[4] && (d[4] != d[5])))
            || ((d[3] != d[4]) && (d[4] == d[5]))) {
            answer = true
        }

//        d.windowed(2, 1) {
//            if (it.last() == it.first()) {
//                answer = true
//            }
//        }
        return answer
    }

    private fun digitsNeverDecrease(digits: List<Int>): Boolean {
        var answer = true
        digits.windowed(2, 1) {
            if (it.last() < it.first()) {
                answer = false
            }
        }
        return answer
    }

    /*
    However, they do remember a few key facts about the password:

It is a six-digit number.
The value is within the range given in your puzzle input.
Two adjacent digits are the same (like 22 in 122345).
Going from left to right, the digits never decrease; they only ever increase or stay the same (like 111123 or 135679).
Other than the range rule, the following are true:

111111 meets these criteria (double 11, never decreases).
223450 does not meet these criteria (decreasing pair of digits 50).
123789 does not meet these criteria (no double).
How many different passwords within the range given in your puzzle input meet these criteria?

Your puzzle input is 153517-630395.


     */
}
*/


/*
//            continue

//            when(opcode) {
//                1 -> {
//                    val p1 = data[pc + 1]
//                    val p2 = data[pc + 2]
//                    val p3 = data[pc + 3]
//                    val answer = getValue(data, pmode1, p1) + getValue(data, pmode2, p2)
//                    storeValue(data, pmode3, p3, answer)
//                    pc += 4
//                }
//                2 -> {
//                    val p1 = data[pc + 1]
//                    val p2 = data[pc + 2]
//                    val p3 = data[pc + 3]
//                    val pv1 = getValue(data, pmode1, p1)
//                    val pv2 =  getValue(data, pmode2, p2)
//                    val answer = pv1 * pv2
//                    storeValue(data, pmode3, p3, answer)
//                    pc += 4
//                }
//                3 -> {
//                    val p1 = data[pc + 1]
//                    storeValue(data, pmode1, p1, readInput())
//                    pc += 2
//                }
//                4 -> {
//                    val p1 = data[pc + 1]
//                    writeOutput(getValue(data, pmode1, p1))
//                    pc += 2
//                }
//                5 -> {
//                    val p1 = data[pc + 1]
//                    val p2 = data[pc + 2]
//                    val pv1 = getValue(data, pmode1, p1)
//                    val pv2 =  getValue(data, pmode2, p2)
//                    if (pv1 != 0) {
//                        pc = pv2
//                    } else {
//                        pc += 3
//                    }
//                }
//                6 -> {
//                    val p1 = data[pc + 1]
//                    val p2 = data[pc + 2]
//                    val pv1 = getValue(data, pmode1, p1)
//                    val pv2 =  getValue(data, pmode2, p2)
//                    if (pv1 == 0) {
//                        pc = pv2
//                    } else {
//                        pc += 3
//                    }
//                }
//                7 -> {
//                    val p1 = data[pc + 1]
//                    val p2 = data[pc + 2]
//                    val p3 = data[pc + 3]
//                    val pv1 = getValue(data, pmode1, p1)
//                    val pv2 =  getValue(data, pmode2, p2)
//                    var value = if (pv1 < pv2) 1 else 0
//                    storeValue(data, pmode3, p3, value)
//                    pc += 4
//                }
//                8 -> {
//                    val p1 = data[pc + 1]
//                    val p2 = data[pc + 2]
//                    val p3 = data[pc + 3]
//                    val pv1 = getValue(data, pmode1, p1)
//                    val pv2 =  getValue(data, pmode2, p2)
//                    var value = if (pv1 == pv2) 1 else 0
//                    storeValue(data, pmode3, p3, value)
//                    pc += 4
//                }
//                99 -> {
//                    return
//                }
//                else -> {
//                    throw RuntimeException("Illegal op code at $pc: ${data[pc]}")
//                }
//            }
*/

/*
class Day6 {
    companion object {
        fun test() {
//            Day6().part1(testInput)
            Day6().part1(File("/Users/dkhawk/Downloads/2019/input-6.txt").readText())
//            Day6().part1(testInput2)
        }

        val testInput = """
            COM)B
            B)C
            C)D
            D)E
            E)F
            B)G
            G)H
            D)I
            E)J
            J)K
            K)L
        """.trimIndent()

        val testInput2 = """
            COM)B
            B)C
            C)D
            D)E
            E)F
            B)G
            G)H
            D)I
            E)J
            J)K
            K)L
            K)YOU
            I)SAN
        """.trimIndent()
    }

    class Node(val value: String, var totalOrbits:Int = 0) {
        val children = ArrayList<Node>()
    }

    private fun part1(input: String) {
        val allChildren = HashSet<String>()

        val orbitMap = HashMap<String, ArrayList<String>>()

        input.splitToSequence('\n').forEach { pair ->
            val child = pair.substringAfter(')')
            val parent = pair.substringBefore(')')
            orbitMap.getOrPut(parent) { ArrayList<String>() }.add(child)
            allChildren.add(child)
        }

//        orbitMap.forEach { (parent, children) ->
//            println("$parent: ${children.joinToString()}")
//        }

//        println()

//        println("${allChildren.joinToString()}")
        // Orphans

        val allParents = orbitMap.keys.toSet()

        val orphans = allParents - allChildren

//        println()

//        println(orphans.joinToString())

        if (orphans.size > 1) {
            throw RuntimeException("This can only work if there is a single root node")
        }

        val root = Node(orphans.first())
        addChildren(root, orbitMap, 0)

//        printTree(root)

//        println()
//        println(countAllOrbits(root))
        val youPath = ArrayDeque<String>(100)
        findNode(root, "YOU", youPath)
        println(youPath.joinToString(" => "))

        val santaPath = ArrayDeque<String>(100)
        findNode(root, "SAN", santaPath)
        println(santaPath.joinToString(" => "))

        var common = ""
        val yi = youPath.iterator()
        val si = santaPath.iterator()

        var count = 1
        while (yi.hasNext() && si.hasNext()) {
            val c = yi.next()
            if (c == si.next()) {
                common = c
                count++
            }
        }

        println("Common $common ($count)")
        val answer = (youPath.size - count) + (santaPath.size - count)
        println("Answer $answer")
    }

    private fun findNode(node: Node, target: String, path: ArrayDeque<String>): Boolean {
        if (node.value == target) {
            path.push(node.value)
            return true
        }

        node.children.forEach { child ->
            if (findNode(child, target, path)) {
                path.push(node.value)
                return true
            }
        }
        return false
    }

    private fun countAllOrbits(node: Node) : Int {
        return node.totalOrbits + node.children.sumBy { countAllOrbits(it) }
    }

    private fun printTree(node: Node, parents: String = "") {
        val result = parents + if (parents.isBlank()) node.value else " => ${node.value}"
        if (node.children.isEmpty()) {
            println(result)
        } else {
            node.children.forEach {child ->
                printTree(child, result)
            }
        }
    }

    private fun addChildren(node: Node, orbitMap: HashMap<String, ArrayList<String>>, depth: Int) {
        orbitMap[node.value]?.let { children ->
            children.forEach {child ->
                val childNode = Node(child, depth + 1)
                addChildren(childNode, orbitMap, depth + 1)
                node.children.add(childNode)
            }
        }
    }
}
*/

/*
class Day8 {
    companion object {
        fun test() {
//            val input = "123456789012"
//            val width = 3
//            val height = 2

            val input = File("/Users/dkhawk/Downloads/2019/input-8.txt").readText().trim()
            val width = 25
            val height = 6

//            val input = "0222112222120000"
//            val width = 2
//            val height = 2

//            Day8().part1(input, width, height)
            Day8().part2(input, width, height)
        }
    }

    private fun part2(input: String, width: Int, height: Int) {
        // 0 is black, 1 is white, and 2 is transparent.
        val layerSize = width * height
        val layers = input.windowed(layerSize, layerSize).map { layer -> layer.map { it -> it.toInt() - 48 } }

        val image = CharArray(layerSize)
        image.fill('.')
        for (index in 0 until layerSize) {
            for (layer in 0..layers.lastIndex) {
                when (layers[layer][index]) {
                    0 -> image[index] = ' '
                    1 -> image[index] = '#'
                    else -> {}
                }
                if (image[index] != '.') {
                    break
                }
            }
        }

        image.toList().windowed(width, width) { row ->
            println(row.joinToString(""))
        }
    }

    private fun part1(input: String, width: Int, height: Int) {
        val layerSize = width * height
        val layers = input.windowed(layerSize, layerSize).map { layer -> layer.map { it -> it.toInt() - 48 } }
        layers.forEachIndexed { index, layer ->
            print("$index  ->")
            val zeros = layer.count { it == 0 }
            print("0: $zeros")
            val ones = layer.count { it == 1 }
            print(", 1: $ones")
            val twos = layer.count { it == 2 }
            print(", 2: $twos")

            print(",   ${zeros + ones + twos}")
//            print(it.joinToString())
            println()
        }
        println()
        println()

        // most zeros
        layers.minBy { layer -> layer.count { it == 0 } }?.let { maxZerosLayer ->
//            println(it.joinToString())
            val zeros = maxZerosLayer.count { it == 0 }
            print("0: $zeros")
            val ones = maxZerosLayer.count { it == 1 }
            print(", 1: $ones")
            val twos = maxZerosLayer.count { it == 2 }
            print(", 2: $twos")

            print(",   ${zeros + ones + twos}")

            println(" --->>>  ${ones * twos}")
        }
    }
}
 */