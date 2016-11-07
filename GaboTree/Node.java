import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Node implements Serializable{

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
	
	public boolean buscar(Rectangle r) throws ClassNotFoundException, IOException{
		// OJO, Como los vértices son floats, ¿será muy dificil encontrar exactos?
		for(int i=0;i<this.content.size();i++){
			if(this.content.get(i).equals(r) && isLeaf){return true;}
			//System.out.println(isLeaf);
			//System.out.println(this.content.get(i).intersects(r));//+" "+this.sons.get(i)!=null);
			if(this.content.get(i).intersects(r) && this.sons.get(i)!=null && this.memory.loadNode(this.sons.get(i)).buscar(r)){return true;}
		}
		return false;
	}
	
	public List<Long> linear_split() throws Exception{
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
        List<Long> rtrees_mas_lejos = new ArrayList<Long>();
        this.generateMBR();
        if((max_izq - min_der)/this.getAncho() >= (max_inf - min_sup)/this.getAlto()){
            rtrees_mas_lejos.add(this.sons.get(index_min_der));
            rtrees_mas_lejos.add(this.sons.get(index_max_izq));
            return rtrees_mas_lejos;
        }
        else{
            rtrees_mas_lejos.add(this.sons.get(index_min_sup));
            rtrees_mas_lejos.add(this.sons.get(index_max_inf));
            return rtrees_mas_lejos;
        }
	}
	
	public List<Long> quadratic_split() throws Exception{
		//TODO retorna un arraylist con los dos rectangulos elegidos segun este procedimiento
		return null;
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
			List<Long> rectangulos_de_cabecera = new ArrayList<Long>();
			if(strategy=="linear_split"){rectangulos_de_cabecera=this.linear_split();}
			else if(strategy=="quadratic_split"){rectangulos_de_cabecera=this.quadratic_split();}
			else{throw new Exception("Estrategia Incorrecta");}
			// coloco cada rectangulo en listas distintas
			Long aux1 = rectangulos_de_cabecera.get(0);
			Long aux2 = rectangulos_de_cabecera.get(1);
			
			sons1.add(aux1);
			sons2.add(aux2);
			
			Rectangle raux1 = this.content.get(this.sons.indexOf(aux1));
			Rectangle raux2 = this.content.get(this.sons.indexOf(aux2));
			
			content1.add(raux1);
			content2.add(raux2);
			
			this.sons.remove(aux1);
			this.sons.remove(aux2);
			
			this.content.remove(raux1);
			this.content.remove(raux2);
			
			// Relleno las listas: se toma cada rectangulo restante (en orden aleatorio) y se agrega al nodo cuyo MBR experimente el menor incremento en area; luego se recomputa el MBR del nodo. Debe tener cuidado para asegurar que los dos nuevos nodos terminan con al menos m MBRs
			int max=this.content.size();
			Random generator = new Random();
			while(max>0){
				// elige un rectangulo al azar del nodo y lo saco junto con su referencia en memoria
				int i=generator.nextInt(max); 
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