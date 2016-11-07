package tarea1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;

public class MemoryManager{
	
	private int BufferSize;
	private HashMap<Long, Node> elements; // Son los nodos guardados en memoria principal y sus referencias a sus posiciones en disco.
	private HashMap<Long, Boolean> NodeInBUfferWasModified;// Son las posiciones en disco de los nodos en memoria principal y un boolean que indica si éste fue modificado desde que se lleyo de disco
	private LinkedList<Long> priority; // Son las posiciones en disco de los nodos en memoria principal ordenados según la última vez que fueron accesados
	private int numOfElements; //numero de elementos activos en el r-tree
	private long number_of_elements_created;
	public int nro_de_accesos_a_disco;

	public MemoryManager(int bufferSize) throws FileNotFoundException{
		priority = new LinkedList<Long>();
		elements = new HashMap<Long, Node>();
		NodeInBUfferWasModified = new HashMap<Long, Boolean>();
		numOfElements = 0;
		BufferSize = bufferSize;
		this.number_of_elements_created=1;
		this.nro_de_accesos_a_disco=0;
	}
	
	public Node loadNode(long mem_position) throws IOException, ClassNotFoundException{
		if(elements.containsKey(mem_position)){
			priority.remove(mem_position);
			priority.addFirst(mem_position);
			return elements.get(mem_position);
		}
		else{
			if (BufferSize==numOfElements){ // Envía el último elemento de la cola de prioridad a disco.
				long exit = priority.pollLast();
				Node exitNode = elements.get(exit);
				if(NodeInBUfferWasModified.get(exit)){
					writeToFile(exitNode, exit);
				}
				elements.remove(exit);
				NodeInBUfferWasModified.remove(exit);
			}
			Node temp = readFromFile(mem_position);
			priority.addFirst(mem_position);
			NodeInBUfferWasModified.put(mem_position, false);
			elements.put(mem_position, temp);
			return temp;
		}
	}

	public void saveNode(Node node) throws IOException{
		long n = node.getMemoryPosition();
		if(elements.containsKey(n)){
			elements.put(n, node);
			NodeInBUfferWasModified.put(n, true);
			priority.remove(n);
			priority.addFirst(n);
			return;
		}
		else if(numOfElements < BufferSize){
			elements.put(n, node);
			NodeInBUfferWasModified.put(n, true);
			numOfElements++;
			//priority.remove(n);
			priority.addFirst(n);
			return;
		}
		//if(elements.containsKey(n)){
		//	elements.put(n, node);
		//	NodeInBUfferWasModified.put(n, true);
		//	priority.remove(n);
		//	priority.addFirst(n);
		//}
		else{
			long exit = priority.pollLast();
			Node temp = elements.get(exit);
			writeToFile(temp,temp.getMemoryPosition());
			NodeInBUfferWasModified.remove(exit);
			elements.remove(exit);
			elements.put(n, node);
			priority.addFirst(n);
			NodeInBUfferWasModified.put(n, true);
		}
	}
	
	private Node readFromFile(long mem_position) throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(mem_position+".node"));
		Node n = (Node) in.readObject();
		in.close();
		n.memory=this;
		this.nro_de_accesos_a_disco++;
		return n;
	}

	private void writeToFile(Node n, long mem_position) throws IOException {
		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(mem_position+".node"));
		o.writeObject(n);
		o.close();
		this.nro_de_accesos_a_disco++;
	}
	
	public long memoryAssigner(){
		this.number_of_elements_created++;
		return this.number_of_elements_created;
	}

}