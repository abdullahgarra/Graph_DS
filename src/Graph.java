

import java.util.Arrays;
import java.util.Random;

/**
 * This class represents a graph that efficiently maintains the heaviest neighborhood over edge addition and
 * vertex deletion.
 *
 */
public class Graph {
	int [][] heap;		//the max heap
	int size ;			//the size of
	int edges;		//the edges in the graph.
	hashingNodes[] table;	// the hashTable
	int N;			//the length of the node's list;
	boolean empty;		//this boolean will be true iff size=0;
	Hashing thisHash;	//the hash object, which will be using in order to apply all the hashing functions.
	/**					//each graph, has its "thisHash" object,
	 * Initializes the graph on a given set of nodes. The created graph is empty, i.e. it has no edges.
	 * You may assume that the ids of distinct nodes are distinct.
	 *
	 * @param nodes - an array of node objects
	 */
	public Graph(Node [] nodes){
		thisHash=new Hashing();

		N= nodes.length;
		if(N==0) empty = true;
		this.table=thisHash.hashtable(nodes);
		this.heap = new int[nodes.length][2]; //initializing heap where each element in heapArray is a mini-Array - [weight,id]
		size = nodes.length;
		initHeap(nodes,size);


	}


	public static class Hashing {	//the Hashing class;
		public int NInHash;			//the the number of the nodes in the graph.
		public hashingNodes[] tablee;	//the hashing nodes list;
		public static final int p= (int) Math.pow(10, 9) + 9;	//p=10**9+9
		public static final int a = new Random().nextInt(p-1)+1;	// 1<=a<p
		public static final int b = new Random().nextInt(p) ;		//0<=b<p



		public static int hashfunction(int id, int N) { // the universal hashing function : (ax+b)mop p mod n
			//the p is 10**9+1 and we chose n to be the size of the graph+1
			//(the +1 just in case we have an empty graph)

			int toReturn = ((Math.abs(a * id + b)) % p) % (N+1);
			return toReturn;
		}

		public hashingNodes[] hashtable(Node[] nodes) {		//this builds the hashtable, which we will use to find nodes, and
			//and each node points to a list which contains it's neighbors
			boolean toBreak =false;
			int N = nodes.length;
			NInHash=N;
			hashingNodes[] table = new hashingNodes[5 * N];
			int j;
			for (int i = 0; i < N; i++) {

				j = Hashing.hashfunction(nodes[i].getId(), N);
				if(table[j]==null) {
					table[j] = new hashingNodes(nodes[i]);
				}
				else {
					hashingNodes curr = table[j];
					while (curr.next!=null) {
						if(curr.id==nodes[i].id) {
							toBreak=true;
							break;
						}
						curr=curr.next;
					}
					if(toBreak==false) {
						hashingNodes toAdd = new hashingNodes(nodes[i]);
						curr.next = toAdd;
						toAdd.next = null;
					}
					toBreak=false;
				}
			}
			tablee=table;
			return table;

		}
		public void deleteHashNode(hashingNodes node){		//deletion from the chaining list.
			int indexInTable = Hashing.hashfunction(node.sourceNode.id,NInHash);
			hashingNodes start = tablee[indexInTable];
			if(start==node) tablee[indexInTable]=node.next;
			else {
				while (start.next != node) start = start.next;
				start.next = node.next;
			}
		}
	}





	public static class hashingNodes {
		hashingNodes next;
		int id;
		Node sourceNode;
		neighborList neighborhoodList;
		int indexinheap;


		public hashingNodes(Node node) {
			this.id = node.getId();
			this.next = null;
			this.sourceNode=node;
			this.neighborhoodList = new neighborList();

		}
	}




	public void initHeap(Node[] nodes,int n) {	//we initialize the max heap: max heap because we we want the max wight of all the nodes.
		for(int i=0;i<nodes.length;i++) {
			heap[i][0]= nodes[i].getWeight();
			heap[i][1]=nodes[i].getId();
		}
		int start = (n / 2) - 1; // Index of last non-leaf node
		for (int i = start; i >= 0; i--) {
			heapifyDown(heap, n, i);
		}
		for(int i=0;i<heap.length;i++){
			int t = Hashing.hashfunction(heap[i][1],N);
			hashingNodes hashNode = findInChain(table[t],heap[i][1]);
			hashNode.indexinheap=i;
		}

	}

	public void heapifyDown(int[][] heap, int size, int index) {	//heapifyDown in the heap.
		if(size<=1 || index==size-1){}
		else {
			int largest = index;    // Initialize current node as largest
			int left = 2 * index + 1;   // position of left child in heap = 2*index + 1
			int right = 2 * index + 2;   // position of right child in heap = 2*index + 2

			if (left < size && heap[left][0] > heap[largest][0])  // If left child is larger than root
				largest = left;

			if (right < size && heap[right][0] > heap[largest][0]) // If right child is larger than largest so far
				largest = right;

			if (largest != index) {// If largest is not root swap it
				int[] swap = heap[index];
				heap[index] = heap[largest];
				heap[largest] = swap;


				int j = Hashing.hashfunction(heap[index][1], N);
				int k = Hashing.hashfunction(heap[largest][1], N);
				//update indexes in heap
				int tmp = findInChain(table[j], heap[index][1]).indexinheap;///////////////////////////////////////////
				findInChain(table[j], heap[index][1]).indexinheap = findInChain(table[k], heap[largest][1]).indexinheap;
				findInChain(table[k], heap[largest][1]).indexinheap = tmp;
				heapifyDown(heap, size, largest); // Recursively heapify the sub-tree
			}
		}

	}

	public void heapifyUp(int[][] heap , int i){	//heapifying up: same as down.
		if(size==1 || i==0){}
		else {

			int largest = i;
			int parent ;
			if (i % 2 == 1) {
				parent = (i - 1) / 2;
			}
			else parent = i / 2 - 1;


			if (parent >= 0 && heap[parent][0] >= heap[i][0]){
				largest = parent;
			}

			if (largest ==i) {// If largest is not root swap it
				largest=parent;
				int[] swap = heap[i];
				heap[i] = heap[largest];
				heap[largest] = swap;
				int j = Hashing.hashfunction(heap[i][1], N);
				int k = Hashing.hashfunction(heap[largest][1], N);
				//update indexes in heap
				int tmp = findInChain(table[j], heap[i][1]).indexinheap;
				findInChain(table[j], heap[i][1]).indexinheap = findInChain(table[k], heap[largest][1]).indexinheap;
				findInChain(table[k], heap[largest][1]).indexinheap = tmp;
				heapifyUp(heap, largest);
			}
		}

	}



	public hashingNodes findInChain(hashingNodes firstNodeInChain, int id) { //id is the goal's id
		//we find a node in the chaining list: in the hash table.
		while( firstNodeInChain!=null && firstNodeInChain.id!=id ) firstNodeInChain = firstNodeInChain.next;
		return firstNodeInChain;
	}

	public hashingNodes findInTable(int id){ //this will return the first node in the chaining list.
		int indexInTable = Hashing.hashfunction(id,N);
		return findInChain(table[indexInTable],id);
	}

	public void heapIncrease(int amount,int i){ //i is the index in heap
		heap[i][0]=heap[i][0]+amount;
		heapifyUp(heap,i);
	}

	public void heapDecrease(int amount, int i) {// i is indexInHeap
		heap[i][0]=heap[i][0]-amount;
		heapifyDown(heap,size,i);
	}


	private void delete_max(int [][] heap){	//deleting the maximum in the heap, and updating the next max.
		if(size==1) {
			heap[0][0]=-1;
			heap[0][1]=-1;
		}
		else {
			heap[0][0] = heap[size - 1][0];
			heap[0][1] = heap[size - 1][1];
			heap[size - 1][0] = -1;
			heap[size - 1][1] = -1;
			hashingNodes x = findInTable(heap[0][1]);
			x.indexinheap = 0;
			heapifyDown(heap, size, 0);
		}
	}




	/**
	 * This method returns the node in the graph with the maximum neighborhood weight.
	 * Note: nodes that have been removed from the graph using deleteNode are no longer in the graph.
	 * @return a Node object representing the correct node. If there is no node in the graph, returns 'null'.
	 */
	public Node maxNeighborhoodWeight(){
		if(empty) return null;
		int max_id = heap[0][1];
		int indexInHash = Hashing.hashfunction(max_id,N);
		return findInChain(table[indexInHash],max_id).sourceNode;
	}

	/**
	 * given a node id of a node in the graph, this method returns the neighborhood weight of that node.
	 *
	 * @param node_id - an id of a node.
	 * @return the neighborhood weight of the node of id 'node_id' if such a node exists in the graph.
	 * Otherwise, the function returns -1.
	 */
	public int getNeighborhoodWeight(int node_id){	//returns the weight of a node with a specific id.
		if(empty) return -1;
		int indexInTable = Hashing.hashfunction(node_id,N);
		hashingNodes location = findInChain(table[indexInTable],node_id);
		if(location==null) return -1;
		//if(heap[location.indexinheap][1]!=node_id) return -1;
		return heap[findInChain(table[indexInTable],node_id).indexinheap][0];
	}

	/**
	 * This function adds an edge between the two nodes whose ids are specified.
	 * If one of these nodes is not in the graph, the function does nothing.
	 * The two nodes must be distinct; otherwise, the function does nothing.
	 * You may assume that if the two nodes are in the graph, there exists no edge between them prior to the call.
	 *
	 * @param node1_id - the id of the first node.
	 * @param node2_id - the id of the second node.
	 * @return returns 'true' if the function added an edge, otherwise returns 'false'.
	 */
	public boolean addEdge(int node1_id, int node2_id){
		if(empty) return false;

		hashingNodes node1=findInTable(node1_id);
		hashingNodes node2=findInTable(node2_id);

		if(node1==null || node2==null) return false; // indicating that one of the nodes is not in the graph
		else {
			neighborList.neighborNode node1AsNeighbor = new neighborList.neighborNode(node1.sourceNode);
			neighborList.neighborNode node2AsNeighbor = new neighborList.neighborNode(node2.sourceNode);

			node1AsNeighbor.neighbor=node2AsNeighbor;
			node2AsNeighbor.neighbor=node1AsNeighbor;

			node1.neighborhoodList.addNeighborNode(node2AsNeighbor);
			node2.neighborhoodList.addNeighborNode(node1AsNeighbor);

			heapIncrease(node1.sourceNode.weight, node2.indexinheap);
			heapIncrease(node2.sourceNode.weight, node1.indexinheap);


		}
		edges++;

		return true;

	}

	/**
	 * Given the id of a node in the graph, deletes the node of that id from the graph, if it exists.
	 *
	 * @param node_id - the id of the node to delete.
	 * @return returns 'true' if the function deleted a node, otherwise returns 'false'
	 */
	public boolean deleteNode(int node_id){
		if(empty) return false;
		hashingNodes nodeHash = findInTable(node_id);
		if (nodeHash==null) return false;
		int i = nodeHash.indexinheap;
		heapIncrease(heap[0][0]+72,i);
		delete_max(heap);
		edges=edges-nodeHash.neighborhoodList.length;
		neighborList.neighborNode start = nodeHash.neighborhoodList.head;
		int firstTimeInHead=1;

		while( (start !=null ) && (start != nodeHash.neighborhoodList.head || firstTimeInHead==1)){
			hashingNodes x = findInTable(start.sourceNode.getId());

			x.neighborhoodList.deleteNeighborNode(start.neighbor);
			int indexInHeap = x.indexinheap;

			heapDecrease(nodeHash.sourceNode.weight,indexInHeap);
			start=start.next;
			firstTimeInHead=0;
		}
		size--;
		if(size==0) empty=true;
		thisHash.deleteHashNode(nodeHash);

		return true;

	}

	/**
	 * Returns the number of nodes currently in the graph.
	 * @return the number of nodes in the graph.
	 */
	public int getNumNodes(){
		if(empty) return 0;
		return size;
	}

	/**
	 * Returns the number of edges currently in the graph.
	 * @return the number of edges currently in the graph.
	 */
	public int getNumEdges(){
		if(empty) return 0;
		return edges;
	}


	/**
	 * This class represents a node in the graph.
	 */
	public class Node{
		/**
		 * Creates a new node object, given its id and its weight.
		 * @param id - the id of the node.
		 * @param weight - the weight of the node.
		 */
		int id;
		int weight;
		public Node(int id, int weight){
			this.id=id;
			this.weight=weight;
		}

		/**
		 * Returns the id of the node.
		 * @return the id of the node.
		 */
		public int getId(){
			return this.id;
		}

		/**
		 * Returns the weight of the node.
		 * @return the weight of the node.
		 */
		public int getWeight(){
			return this.weight;
		}
	}


	public static class neighborList{ //this is the neightbors class: each node in the chaining list has a pointer to a
		//neighbor list, which contains all the neightbors of a given node.
		//this will be a double linked list.
		neighborNode head;
		neighborNode last;
		int length;

		public neighborList(){	//constructor
			this.head=null;
			this.last=null;
			length=0;
		}


		public void addNeighborNode(neighborNode node){		//adding a node to the neightbors list of the original node.
			if(length==0){
				head = node;
				last = node;
				head.next=last;
				head.prev=last;
				last.next=head;
				last.prev=head;
				length++;
			}
			else{
				node.prev=last;
				last.next=node;
				last=node;
				last.next=head;
				head.prev=last;
				length++;
			}
		}
		public void deleteNeighborNode(neighborNode node){	//deleting a node from the neighbor list of the orignial node
			if(length==0){

			}
			else if (length ==1 ) {
				this.head=null;
				this.last=null;
				length=0;
			}
			else{   //length is not a zero;
				neighborNode soso=node; //soso :P
				neighborNode nextToSoso=node.next;
				neighborNode prevToSoso=node.prev;

				neighborNode currprev=node.prev;
				neighborNode currnext=node.next;
				currprev.next=currnext;
				currnext.prev=currprev;
				if(soso==head){
					this.head=nextToSoso;
					this.last=prevToSoso;
					this.head.prev=last;
				}
				else if(soso==this.last){
					this.last=prevToSoso;
					this.head=nextToSoso;
					this.last.next=head;

				}

				length--;
			}
		}


		public static class neighborNode{	//this is the neighbor nodes in the neighbor list,
			neighborNode next;
			neighborNode prev;
			neighborNode neighbor;
			Node sourceNode;


			public neighborNode(Node node){
				this.sourceNode=node;
			}
		}

	}



}





