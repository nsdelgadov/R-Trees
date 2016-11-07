import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;

//MemoryManager.java//
class MemoryManager{
	
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
//MemoryManager.java//


//Node.java//
class Node implements Serializable{

	private static final long serialVersionUID = -6518502939478845465L;
	private int m;
	private int M;
	private ArrayList<Rectangle> content;
	private ArrayList<Long> sons;
	private long myMemoryPosition;
	private boolean isLeaf;
	private boolean isRoot;
	transient MemoryManager memory;
	private Rectangle mbr;

	public Node(int m, int M, long mem_position, ArrayList<Rectangle> r,ArrayList<Long> n,boolean is_leaf, MemoryManager memoryManager) throws Exception{
		this(m,M,mem_position,r,n,is_leaf,memoryManager,false);
	}
	
	protected Node(int m, int M, long mem_position, ArrayList<Rectangle> r, ArrayList<Long> n,boolean is_leaf, MemoryManager memoryManager, boolean is_root) throws Exception{
		// El nodo deberá tener entre m y M rectangulos, si no lanza excpetion (Customizar exception?) con excepcion de la raiz
		// Además, el M deberá optimizarse según el tamaño del paginado (Memory page, Usualmente 4KiB para las arquitecturas comunes)
		if(r.size()>M || (!is_root && r.size()<m)){throw new Exception();}
		else{
			this.m=m;
			this.M=M;
			this.myMemoryPosition=mem_position;
			this.content=r;
			this.isLeaf=is_leaf;
			this.sons=n;
			this.memory=memoryManager;
			this.isRoot=is_root;
			this.memory.saveNode(this);
		}
	}

	public byte[] serialize() throws IOException{
		ByteArrayOutputStream ans = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(ans);
		out.writeObject(this);
		byte[] res = ans.toByteArray();
		ans.close();
		out.close();
		return res;
	}
	
	static public Node deserialize(byte[] n) throws IOException, ClassNotFoundException{
		Node e;
        ByteArrayInputStream stringIn = new ByteArrayInputStream(n);
        ObjectInputStream in = new ObjectInputStream(stringIn);
        e = (Node) in.readObject();
        in.close();
        stringIn.close();
		return e;
	}
	
	public ArrayList<Rectangle> getRectangles(){
		return content;
	}
	
	public long getMemoryPosition(){
		return this.myMemoryPosition;
	}
	
	public List<Rectangle> buscar(Rectangle r) throws ClassNotFoundException, IOException{
		
        List<Rectangle> encontrados = new ArrayList<Rectangle>();
        if(this.isLeaf){ //todos son hojas
            for(Rectangle hijo : this.content){
                if(hijo.intersects(r))
                    encontrados.add(hijo);
            }
            return encontrados;
        }
        else{
        	int idx = 0;
            for(Rectangle hijo : this.content){
                if(hijo.intersects(r))
                    encontrados.addAll(memory.loadNode(this.sons.get(idx)).buscar(r));
                idx++;
            }
            return encontrados;
        }
	}
	
	public List<Integer> linear_split() throws Exception{
		int index = 0;
        //el indice del que tiene el x derecho más a la izquierda
        int index_min_der = -1;
        //el indice del que tiene el x izquierdo más a la derecha
        int index_max_izq = -1;
        //el indice del que tiene el y sup más a abajo
        int index_min_sup = -1;
        //el indice del que tiene el y sup más arriba
        int index_max_inf = -1;
        
        //valores anteriores
        float min_der = -1;
        float max_izq = -1;
        float min_sup = -1;
        float max_inf = -1;
        
        for(Rectangle hijo : this.content){
            if(hijo == null){
                System.out.println("ALGO MALO OCURRIO ACAAAAAAAAAAAAAAA");
                break;
            }
            if(index == 0){ 
                //Primera iteracion, tengo uno, por lo que es minimo y
                // maximo en todo
                index_min_der = 0;
                index_max_izq = 0;
                index_min_sup = 0;
                index_max_izq = 0;
                
                min_der = hijo.getXf();
                max_izq = hijo.getXi();
                min_sup = hijo.getYf();
                max_inf = hijo.getYi();
            }
            else{
                if(min_der > hijo.getXf()){
                    index_min_der = index;
                    min_der = hijo.getXf();
                }
                
                if(max_izq < hijo.getXi()){
                    index_max_izq = index;
                    max_izq = hijo.getXi();
                }
                
                if(min_sup > hijo.getYf()){
                    index_min_sup = index;
                    min_sup = hijo.getYf();
                }
                
                if(max_inf < hijo.getYi()){
                    index_max_inf = index;
                    max_inf = hijo.getYi();
                } 
            }
            index++;
        }
        List<Integer> rtrees_mas_lejos = new ArrayList<Integer>();
        this.generateMBR();
        
        /*
        System.out.println("Alto:"+this.getAlto());
        System.out.println("Ancho:"+this.getAncho());
        
        System.out.println("index_min_der:"+index_min_der);
        System.out.println("index_min_sup:"+index_min_sup);
        System.out.println("index_max_inf:"+index_max_inf);
        System.out.println("index_max_izq:"+index_max_izq);
        */
        if((min_der - max_izq)/this.getAncho() >= (min_sup - max_inf)/this.getAlto()){
            rtrees_mas_lejos.add(index_min_der);
            rtrees_mas_lejos.add(index_max_izq);
            /*
            System.out.println("this.sons.get(min_der)"+index_min_der);
            System.out.println("this.sons.get(max_izq)"+index_max_izq);
            */
            return rtrees_mas_lejos;
        }
        else{
            rtrees_mas_lejos.add(index_min_sup);
            rtrees_mas_lejos.add(index_max_inf);
            return rtrees_mas_lejos;
        }
	}
	
	public List<Integer> quadratic_split() throws Exception{
		Rectangle hijo1=this.content.get(0);
		Rectangle hijo2=this.content.get(1);
        float area_libre_candidato = Rectangle.calculateMBR(hijo1,hijo2).area()-hijo1.area()-hijo2.area()+hijo1.calculateIntersectionArea(hijo2);
        
        int indice_hijo1 = 0;
        int indice_hijo2 = 1;
        
        for (int i = 0; i < this.sons.size()-1; i++){
            hijo1 = this.content.get(i);
            for (int j = i+1; j < this.sons.size(); j++){
                hijo2 = this.content.get(j);
                float area_libre_calculada = Rectangle.calculateMBR(hijo1,hijo2).area()-hijo1.area()-hijo2.area()+hijo1.calculateIntersectionArea(hijo2);
                if(area_libre_calculada > area_libre_candidato){
                    area_libre_candidato = area_libre_calculada;
                    indice_hijo1 = i;
                    indice_hijo2 = j;
                }
            }
        }
        List<Integer> indices = new ArrayList<Integer>();
        indices.add(indice_hijo1);
        indices.add(indice_hijo2);
		//TODO retorna un arraylist con los dos rectangulos elegidos segun este procedimiento
		return indices;
	}

	protected Long insert(Rectangle r, String strategy) throws Exception {
		//Retorna direccion de memoria, si retorna algo distinto de null es poruqe hubo overflow y el long corresponde a la direccion del nuevo hijo creado para que se guarde.
		if(this.isLeaf){
			//estamos en una hoja
			this.content.add(r);
			this.sons.add(null);
		}
		else{
			//no es hoja, tenemos que decidir por donde bajar
			List<Integer> candidatos = new ArrayList<Integer>(); // candidatos donde se encuentra el Rectangulo que menos tiene que crecer, contiene indices de content de los rectangulos candidatos.
			
			//busco los Rectangle que crecen menos para elegir por donde bajar
			float delta_area = -1; //area en la que aumenta el mejor rectangulo probado
			int index = 0;
            for(Rectangle hijo : this.content){
                if(hijo == null){
                    System.out.println("Esto no debería pasar D:");
                    break;
                }
                float delta_area_rectangulo_actual = Rectangle.calculateMBR(new ArrayList<Rectangle>(Arrays.asList(r,hijo))).area() - hijo.area();
                if(delta_area == -1){ // caso base, siempre es el area menor
                    candidatos.add(index);
                    delta_area = delta_area_rectangulo_actual;
                }
                else if(delta_area_rectangulo_actual == delta_area){ // caso es que el area es igual al actual menor area
                    candidatos.add(index);
                }
                else if(delta_area_rectangulo_actual < delta_area){ // caso en que el area es menor al actual menor area
                    candidatos = new ArrayList<Integer>();
                    candidatos.add(index);
                    delta_area = delta_area_rectangulo_actual;
                }
                index++;
            }
            // Tenemos determinados los candidatos a menor expansión de área. Si el caso es único, abrimos ese rectángulo y hacemos insert en él, sino debemos escoger entre los candidatos aquellos con menor area
            if(candidatos.size() > 1){ // Si el caso no es único (cosa muy dificil por tratarse de floats iguales) buscamos cual de ellos tiene menor área total
            	float min_area_total_mejor=-1;
            	List<Integer> candidatos_v2 = new ArrayList<Integer>();
            	for(Integer candidato_index : candidatos){
            		Rectangle hijo = content.get(candidato_index);
            		
            		float area = hijo.area();
            		if(min_area_total_mejor==-1){ // caso base, siempre es el area menor
            			min_area_total_mejor=area;
            		}
            		if(area == min_area_total_mejor){ // si el area actual es igual al area guardada previamente, agrego un nuevo candidato
            			candidatos_v2.add(candidato_index);
            		}
            		if(area < min_area_total_mejor){ // si el area actual es menor al area guardada, elimino los cadidatos y pongo al nuevo
            			min_area_total_mejor = area;
            			candidatos_v2 = new ArrayList<Integer>();
            			candidatos_v2.add(candidato_index);
            		}
            	}
            	candidatos = candidatos_v2;
            }
            // Si se tiene empate de areas, se escoge al azar :p 
            if(candidatos.size() > 1){
            	//random
            	Random generator = new Random();
				int random_i = generator.nextInt(candidatos.size()-1);
            	candidatos = new ArrayList<Integer>();
                candidatos.add(random_i);
            }
			// finalmente, insertamos en el rectángulo que elegimos
			Node bestSon = memory.loadNode(this.sons.get(candidatos.get(0)));
            Long newSon = bestSon.insert(r,strategy);
            // inpedendiente de la existencia de un nuevo hijo, debemos recalcular el MBR del hijo que acabamos de mandar a insertar un rectangle
            this.content.set(candidatos.get(0) , Rectangle.calculateMBR(memory.loadNode(this.sons.get(candidatos.get(0))).getRectangles()));
            // en newSon tenemos null en caso de que no haya habido un split en el hijo y una direción de memoria en caso de que si ocurriese
            if(newSon!=null){
				this.content.add(Rectangle.calculateMBR(memory.loadNode(newSon).getRectangles()));
				this.sons.add(newSon);
            }
		}
		// si no hay overflow, retorno null
		if(this.content.size()<=this.M){
			memory.saveNode(this);
			return null;
		}
		else{
			// Si hay overflow, creo las dos listas de direcciones de memoria y rectangulos que resultarán del split
			ArrayList<Long> sons1 = new ArrayList<Long>();
			ArrayList<Long> sons2 = new ArrayList<Long>();
			ArrayList<Rectangle> content1 = new ArrayList<Rectangle>();
			ArrayList<Rectangle> content2 = new ArrayList<Rectangle>();
			
			// escogemos los dos rectangulos iniciales según la estrategia que hayamos decidido
			List<Integer> ind_rectangulos_de_cabecera = new ArrayList<Integer>();
			if(strategy=="linear_split"){ind_rectangulos_de_cabecera=this.linear_split();}
			else if(strategy=="quadratic_split"){ind_rectangulos_de_cabecera=this.quadratic_split();}
			else{throw new Exception("Estrategia Incorrecta");}
			// coloco cada rectangulo en listas distintas
			
			/*
			System.out.println("Cabecera 1 "+ind_rectangulos_de_cabecera.get(0));
			System.out.println("Cabecera 2: "+ind_rectangulos_de_cabecera.get(1));
			*/
			
			int aux1 = ind_rectangulos_de_cabecera.get(0).intValue();
			int aux2 = ind_rectangulos_de_cabecera.get(1).intValue();
			
			sons1.add(this.sons.get(aux1));
			sons2.add(this.sons.get(aux2));
			
			content1.add(this.content.get(aux1));
			content2.add(this.content.get(aux2));
			/*
			System.out.println("Aux1: "+aux1);
			System.out.println("Aux2: "+aux2);
			
			System.out.println("Tamaño 2.1: "+this.sons.size());
			System.out.println("Tamaño 2.2: "+this.content.size());
			*/
			this.sons.remove(sons1.get(0));
			this.sons.remove(sons2.get(0));
			
			this.content.remove(content1.get(0));
			this.content.remove(content2.get(0));
			
			/*
			System.out.println("Tamaño 3.1: "+this.sons.size());
			System.out.println("Tamaño 3.2: "+this.content.size());
			*/
			// Relleno las listas: se toma cada rectangulo restante (en orden aleatorio) y se agrega al nodo cuyo MBR experimente el menor incremento en area; luego se recomputa el MBR del nodo. Debe tener cuidado para asegurar que los dos nuevos nodos terminan con al menos m MBRs
			int max=this.content.size();
			Random generator = new Random();
			while(max>0){
				// elige un rectangulo al azar del nodo y lo saco junto con su referencia en memoria
				int i=generator.nextInt(this.sons.size());
				Long son = this.sons.remove(i);
				Rectangle rect = this.content.remove(i);
				// si la suma de una de mis listas más los restantes es igual o menor al minimo tamaño aceptable, estoy obligado a ponerla en esa lista 
				if(sons1.size()+sons.size()<=this.m-1){
					sons1.add(son);
					content1.add(rect);
				}
				// lo mismo para la 2da lista
				else if(sons2.size()+sons.size()<=this.m-1){
					sons2.add(son);
					content2.add(rect);
				}
				// si no, hay que calcular a que nueva colección de rectangulos hay que agregar el actual. Esto se hace calculando la diferencia entre el mbr de la lista actual y el mbr de la lista incluyendo el rectangulo a agregar. Luego, nos decantamos por la diferencia más pequeña
				else{
					if(Rectangle.calculateMBR(rect,content1).area()-Rectangle.calculateMBR(content1).area()<Rectangle.calculateMBR(rect,content2).area()-Rectangle.calculateMBR(content2).area()){
						sons1.add(son);
						content1.add(rect);
					}
					else{
						sons2.add(son);
						content2.add(rect);
					}
				}
				max--;
			}
			// terminé de separar la lista de rectangulos :D
			
			// TODO Falta caso de is_root==True, donde hay que crear dos hijos, hacer que content y sons contengan sólo a ambos hijos, guardar los 3 nodos y retornal null
			if(this.isRoot == true){
				Long newBrother1_mem_position = this.memory.memoryAssigner();
				Long newBrother2_mem_position = this.memory.memoryAssigner();
				
				Node newBrother1 = new Node(m, M, newBrother1_mem_position, content1, sons1, this.isLeaf, this.memory);
				Node newBrother2 = new Node(m, M, newBrother2_mem_position, content2, sons2, this.isLeaf, this.memory);
				
				this.content = new ArrayList<Rectangle>();
				this.content.add(Rectangle.calculateMBR(content1));
				this.content.add(Rectangle.calculateMBR(content2));
				
				this.sons = new ArrayList<Long>();
				this.sons.add(newBrother1_mem_position);
				this.sons.add(newBrother2_mem_position);
				
				memory.saveNode(this);
				memory.saveNode(newBrother1);
				memory.saveNode(newBrother2);
				return null;
			}
			
			//reemplazo las listas del nodo actual por la primera de las dos listas
			this.content=content1;
			this.sons=sons1;
			//creo un nuevo nodo hermano del actual con la segunda lista de direcciones de memoria y rectangulos
			Long newBrother_memPosition = this.memory.memoryAssigner();
			Node newBrother = new Node(m,M,newBrother_memPosition,content2,sons2,this.isLeaf,this.memory);
			//finalmente, guardamos el nodo actual y el nuevo hermano y retornamos la direccion de memoria del nuevo hermano
			memory.saveNode(this);
			memory.saveNode(newBrother);
			return newBrother_memPosition;
		}
			
	}
	
	public void generateMBR(){
		this.mbr = Rectangle.calculateMBR(content);
	}
	
	public float getAncho(){
		return this.mbr.getXf() - this.mbr.getXi();
	}
	
	public float getAlto(){
		return this.mbr.getYf() - this.mbr.getYi();
	}
	
}
//Node.java//

//Rectangle.java//
class Rectangle implements Serializable{

	private static final long serialVersionUID = 7084549190449318695L;
	private float x_i;
	private float x_f;
	private float y_i;
	private float y_f;
	
	public Rectangle(float xi, float xf,float yi,float yf) {
		if(xi>xf){
			this.x_f=xi;
			this.x_i=xf;
		}
		else{
			this.x_i=xi;
			this.x_f=xf;
		}
		if(yi>yf){
			this.y_f=yi;
			this.y_i=yf;
		}	
		else{
			this.y_i=yi;
			this.y_f=yf;
		}
	}
	public float getAlto(){
		return this.y_f - this.y_i;
	}
	
	public float getAncho(){
		return this.x_f - this.x_i;
	}
	
	public float getXi(){
		return this.x_i;
	}
	
	public float getXf(){
		return this.x_f;
	}
	
	public float getYi(){
		return this.y_i;
	}
	
	public float getYf(){
		return this.y_f;
	}
	
	public boolean intersects(Rectangle r) { //Falta verificar caso borde donde rectangulos comparten su borde exluyente, se intersectan?
		if(((r.x_f>this.x_i && r.x_f<this.x_f) || (this.y_f>r.y_i && this.y_f<r.y_f)) && ((r.y_f>this.y_i && r.y_f<this.y_f) || (this.y_f>r.y_i && this.y_f<r.y_f))){return true;}
		else{return false;}
	}

	public float area(){
		return (this.x_f-this.x_i)*(this.y_f-this.y_i);
	}
	
	public float perimeter(){
		return 2*(x_f+y_f-x_i-y_i);
	}
	
	public float calculateIntersectionArea(Rectangle r){ // calculateIntersection o calculateIntersectionArea??? float o Rectangle???
		if(!this.intersects(r)){return 0;}
		else{
			float dx = 0;
			float dy = 0;
			if(this.x_f>r.x_i && r.x_i>this.x_i){
				if(r.x_f>this.x_f){dx = this.x_f - r.x_i;}
				else{dx = r.x_f -r.x_i;}
			}
			if(this.x_f>r.x_i && r.x_i<this.x_i){
				if(r.x_f>this.x_f){dx = this.x_f - this.x_i;}
				else{dx = this.x_i - r.x_f;}
			}
			if(this.y_f>r.y_i && r.y_i>this.y_i){
				if(r.y_f>this.y_f){dy = this.y_f - r.y_i;}
				else{dy = r.y_f -r.y_i;}
			}
			if(this.y_f>r.y_i && r.y_i<this.y_i){
				if(r.y_f>this.y_f){dy = this.y_f - this.y_i;}
				else{dy = this.y_i - r.y_f;}
			}
			return dx*dy;
		}
	}

	public static float calculateOverlap(Rectangle r,List<Rectangle> rects){
		float overlap = 0;
		for(Rectangle rec : rects){
			overlap += r.calculateIntersectionArea(rec);
		}
		return overlap;
	}
	
	public static Rectangle calculateMBR(List<Rectangle> r){
		Rectangle ans = new Rectangle(r.get(0).x_i,r.get(0).x_f,r.get(0).y_i,r.get(0).y_f);
		for(int i=0;i<r.size();i++){
			if (r.get(i).x_i<ans.x_i){ans.x_i=r.get(i).x_i;}
			if (r.get(i).x_f>ans.x_f){ans.x_f=r.get(i).x_f;}
			if (r.get(i).y_i<ans.y_i){ans.y_i=r.get(i).y_i;}
			if (r.get(i).y_f>ans.y_f){ans.y_f=r.get(i).y_f;}
		}
		return ans;
	}
	
	public static Rectangle calculateMBR(Rectangle rec1, Rectangle rec2){
		List<Rectangle> l = new ArrayList<Rectangle>();
		l.add(rec1);
		l.add(rec2);
		return calculateMBR(l);
	}
	
	public static Rectangle calculateMBR(Rectangle rec, List<Rectangle> r){
		List<Rectangle> l = new ArrayList<Rectangle>(r);
		l.add(rec);
		return calculateMBR(l);
	}
	
	@Override
	public boolean equals(Object o){
		if (o.getClass()!=this.getClass()){
			return false;
		}
		else{
			Rectangle r = (Rectangle)o;
			if (this.x_i==r.x_i && this.x_f==r.x_f && this.y_i==r.y_i && this.y_f==r.y_f){return true;}
		}
		return false;
	}
	
}

class RectangleXComparator implements Comparator<Rectangle> {
	@Override
	public int compare(Rectangle r1, Rectangle r2) {
		return (int)Math.round(r1.getXi() - r2.getXi());
	}
}

class RectangleYComparator implements Comparator<Rectangle> {
	@Override
	public int compare(Rectangle r1, Rectangle r2) {
		return (int)Math.round(r1.getYi() - r2.getYi());
	}
}
//Rectangle.java//

//Root.java//
class Root extends Node {

	private static final long serialVersionUID = 6645370223161368179L;

	public Root(int m, int M, long mem_position, Rectangle r, MemoryManager memoryManager) throws Exception {
		super(m, M, mem_position, new ArrayList<Rectangle>(Arrays.asList(r)), new ArrayList<Long>(Arrays.asList((long)0)), true, memoryManager, true);//invoca un construction protected que permite saltarse la restriccion
	}

}
//Root.java//

public class Main {

	public static void main(String[] args) throws Exception {
		Random generator = new Random();
		float x_i=generator.nextFloat()*499900;
		float y_i=generator.nextFloat()*499900;
		float d_x=(generator.nextFloat()*99.9f)+0.1f;
		float d_i=(generator.nextFloat()*99.9f)+0.1f;
		Rectangle r = new Rectangle(x_i,x_i+d_x,y_i,y_i+d_i);
		MemoryManager memory = new MemoryManager(50); // Memorymanager recibe el tamaño del caché, es decir, la cantidad de nodos que se permite tener en memoria principal
		long memPosition = memory.memoryAssigner();
		Root tree = new Root(60,150,memPosition,r,memory);
		Rectangle rect = null;
		ArrayList<Rectangle> tosearch = new ArrayList<Rectangle>();
		java.util.Date date= new java.util.Date();
		int k = 10000; //numero de inserciones a realizar
		System.out.println("Timestamp con 1       insercion  : "+new Timestamp(date.getTime()));
		for(int i = 0;i<k;i++){
			x_i=generator.nextFloat()*499900;
			y_i=generator.nextFloat()*499900;
			d_x=(generator.nextFloat()*99.9f)+0.1f;
			d_i=(generator.nextFloat()*99.9f)+0.1f;
			rect = new Rectangle(x_i,x_i+d_x,y_i,y_i+d_i);
			if(generator.nextInt(100)<10){tosearch.add(rect);}
			tree.insert(rect,"linear_split");
		}
		date= new java.util.Date();
		System.out.println("Timestamp con "+k+" inserciones: "+new Timestamp(date.getTime())+", Nro de Accesos a disco: "+memory.nro_de_accesos_a_disco);
		System.out.println("Timestamp antes    de la búsqueda: "+new Timestamp(date.getTime()));
		memory.nro_de_accesos_a_disco=0;
		for(Rectangle plas : tosearch){
			tree.buscar(plas);
		}
		System.out.println("Se buscaron "+tosearch.size()+" elementos. Nro de Accesos a Disco por búsqueda: "+memory.nro_de_accesos_a_disco);
		System.out.println("Timestamp despues  de la búsqueda: "+new Timestamp(date.getTime()));
	}

}