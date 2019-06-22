/* algorytm 2-aproksymacyjny rozwiązujący problem komiwojażera
    w grafie pełnym z wagami >=0
    dodatkowo wagi spełniają nierówność trójkąta

    wejście - graf G podany za pomocą (symetrycznej) macierzy sąsiedztwa
    wyjście - lista z kolejnymi wierzchołkami G, tworzącymi szukany cykl Hamiltona

*/
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

    val pq = PriorityQueue<Pair<Int,Int>>(ComparePairs)//kolejka priorytetowa przechowujaca pary postaci <numerWezla, odlegloscOdDrzewa>
    pq.add(Pair(0, 0))

    while(pq.isNotEmpty()){
        val currentNode = pq.poll() //usun minimum z kolejki

        val currentNodeNum = currentNode.first
        distances[currentNodeNum] = 0

        val currentNodeNeighbors = G[currentNodeNum]
        for(neighborNodeNum in 1..currentNodeNeighbors.size){
            if(neighborNodeNum-1 == currentNodeNum) continue //nie interesuje nas biezacy wezel
            if(distances[neighborNodeNum-1] == 0) continue //nie interesuja nas juz odwiedzone wezly

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
    //pomocnicza funkcja tworzaca liste sasiedztwa dla drzewa stworzonego przez algorytm prima

    //rodzicem wezla o numerze i jest wezel o numerze parents[i]
    val neighborsLists = mutableListOf<MutableList<Int>>()
    for(i in 1..parents.size){
        neighborsLists.add(mutableListOf())
    }

    for(i in 1..parents.size){
        val parentNodeNum = parents[i-1]
        if(parentNodeNum !in neighborsLists[i-1])
            neighborsLists[i-1].add(parentNodeNum) //rodzic jest sasiadem swojego dziecka
        if((i-1) !in neighborsLists[parentNodeNum])
            neighborsLists[parentNodeNum].add(i-1) //dziecko rodzica jest jego sasiadem
    }
    return neighborsLists
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

fun printTSPCycle(G : Array<IntArray>){
    println(findHamilton(findEulerCycle(createNeighbourListAfterPrim(prim(G)))).toString())
}

fun main(args: Array<String>) {
    val G1 : Array<IntArray> = arrayOf(
        intArrayOf(0,1,8,3),
        intArrayOf(1,0,2,5),
        intArrayOf(8,2,0,4),
        intArrayOf(3,5,4,0)
    )

    printTSPCycle(G1)

    val G2 : Array<IntArray> = arrayOf(
        intArrayOf(0,3,2,10,15),
        intArrayOf(3,0,1,7,9),
        intArrayOf(2,1,0,5,6),
        intArrayOf(10,7,5,0,4),
        intArrayOf(15,9,6,4,0)
    )

    printTSPCycle(G2)
}


