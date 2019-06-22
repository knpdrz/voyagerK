import java.util.*

class ComparePairs{
    companion object : Comparator<Pair<Int, Int>>{
        override fun compare(pair1 : Pair<Int, Int>, pair2 : Pair<Int, Int>) : Int = pair1.second - pair2.second
    }
}

fun prim(G : Array<IntArray>) : IntArray {
    val distances = IntArray(G.size)
    val parents = IntArray(G.size)
    for(i in 1..distances.size){
        distances[i-1] = Int.MAX_VALUE
        parents[i-1] = -1
    }

    val pq = PriorityQueue<Pair<Int,Int>>(ComparePairs)
    pq.add(Pair(0, 0))

    while(pq.isNotEmpty()){
        val currentNode = pq.poll() //delete min from queue

        val currentNodeNum = currentNode.first
        distances[currentNodeNum] = 0

        val currentNodeNeighbors = G[currentNodeNum]
        for(neighborNodeNum in 1..currentNodeNeighbors.size){
            if(neighborNodeNum-1 == currentNodeNum) continue //this is not a neighbour, but currentNode
            if(distances[neighborNodeNum-1] == 0) continue //we have already used this node

            val oldNeighborPair = Pair(neighborNodeNum-1, distances[neighborNodeNum-1])
            pq.remove(oldNeighborPair) //removes pair if it exists

            val distFromCurrentNodeToNeighbor = currentNodeNeighbors[neighborNodeNum-1]
            if(distFromCurrentNodeToNeighbor < distances[neighborNodeNum-1]){
                distances[neighborNodeNum-1] = distFromCurrentNodeToNeighbor
                parents[neighborNodeNum-1] = currentNodeNum
            }

            val neighborPair = Pair(neighborNodeNum-1, distances[neighborNodeNum-1])
            pq.add(neighborPair)
        }
    }
    parents[0] = parents.indexOf(0)
    return parents
}

fun createNeighbourListAfterPrim(parents : IntArray) : List<MutableList<Int>> {
    //parent of node i is parents[i]
    val neighborsLists = mutableListOf<MutableList<Int>>()
    for(i in 1..parents.size){
        neighborsLists.add(mutableListOf())
    }

    for(i in 1..parents.size){
        val parentNodeNum = parents[i-1]
        if(parentNodeNum !in neighborsLists[i-1])
            neighborsLists[i-1].add(parentNodeNum) //parent is neighbour of its child
        if((i-1) !in neighborsLists[parentNodeNum])
            neighborsLists[parentNodeNum].add(i-1) //parent's neighbour is its child
    }
    return neighborsLists
}

fun printListOfLists(listOfLists: List<MutableList<Int>>) {
    for(i in 1..listOfLists.size){
        println(listOfLists[i-1])
    }
}

fun findEulerCycle(neighborLists : List<MutableList<Int>>) : List<Int>{
    val cycle = mutableListOf<Int>()
    dfs(0, mutableSetOf(), neighborLists, cycle)
    return cycle
}

fun dfs(nodeNum : Int, visited : MutableSet<Int>, neighborLists : List<MutableList<Int>>, cycle : MutableList<Int>){
    val neighbors = neighborLists[nodeNum]
    visited.add(nodeNum)
    for(neighbor in neighbors){
        if(neighbor !in visited){
            cycle.add(nodeNum)
            dfs(neighbor, visited, neighborLists, cycle)
        }
    }
    cycle.add(nodeNum)
}

fun findHamilton(cycle : List<Int>) : List<Int> {
    val hamilton = mutableListOf<Int>()
    for(node in cycle){
        if(node !in hamilton)
            hamilton.add(node)
    }
    hamilton.add(hamilton[0])
    return hamilton
}

fun main(args: Array<String>) {
/*    val G : Array<IntArray> = arrayOf(//tablica G musi być symetryczna
        intArrayOf(0,1,8,3),
        intArrayOf(1,0,2,5),
        intArrayOf(8,2,0,4),
        intArrayOf(3,5,4,0)
    )*/

    val G : Array<IntArray> = arrayOf(//tablica G musi być symetryczna
        intArrayOf(0,3,2,10,15),
        intArrayOf(3,0,1,7,9),
        intArrayOf(2,1,0,5,6),
        intArrayOf(10,7,5,0,4),
        intArrayOf(15,9,6,4,0)
    )

    println(findHamilton(findEulerCycle(createNeighbourListAfterPrim(prim(G)))).toString())
}


