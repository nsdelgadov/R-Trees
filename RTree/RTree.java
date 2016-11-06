package RTree;

import java.io.IOException;
import java.lang.*;
import java.awt.Point;
import java.util.*;
import java.util.Collections;

class RTree{
    private Point base;
    private int alto, ancho;
    private int cant_hijos, min_hijos, max_hijos;
    private boolean es_hoja; //tipo = True -> Hoja, tipo = False -> MBR;
    private List<RTree> hijos;
    
    public RTree(int max_hijos, RTree r1, RTree r2){
        this.cant_hijos = 2;
        this.min_hijos = 2;
        this.max_hijos = max_hijos;
        this.es_hoja = false;
        this.hijos = new ArrayList<RTree>();
        this.hijos.add(r1);
        this.hijos.add(r2);
    }
    
    public RTree(Point p, int alto, int ancho, int min_hijos, int max_hijos, boolean es_hoja){
        this.base = p;
        this.alto = alto;
        this.ancho = ancho;
        this.cant_hijos = 0;
        this.min_hijos = min_hijos;
        this.max_hijos = max_hijos;
        this.es_hoja = es_hoja;
        this.hijos = new ArrayList<RTree>();
    }
    
    
    public RTree(RTree r){
        this.base = r.getBase();
        this.alto = r.getAlto();
        this.ancho = r.getAncho();
        this.cant_hijos = 1;
        this.min_hijos = r.getMinHijos();
        this.max_hijos = r.getMaxHijos();
        this.es_hoja = false;
        this.hijos = new ArrayList<RTree>();
        this.hijos.add(r);
    }
    
    //retorna el MBR de un RTree si se le anexa otro RTree
    public RTree rectanguloNuevo(RTree rtree){
        
        Point base1 = this.base;
        int x1 = base1.x;
        int y1 = base1.y;
        int ancho1 = this.ancho;
        int alto1 = this.alto;
        
        Point base2 = rtree.getBase();
        int x2 = base2.x;
        int y2 = base2.y;
        int ancho2 = rtree.getAncho();
        int alto2 = rtree.getAlto();
        
        int ancho_nuevo = ancho1;
        int alto_nuevo = alto1;
        Point punto_nuevo = new Point(base1);
        
        // eje x primero
        if (x1 <= x2){
            if ((x2 + ancho2) > (x1 + ancho1)){
                ancho_nuevo = x2 - x1 + ancho2;
            }
        }else{
            if ((x2 + ancho2) > (x1 + ancho1)){
                ancho_nuevo = ancho2;
            }else{
                ancho_nuevo = x1 - x2 + ancho1;
            }
            punto_nuevo.x = x2;
        }
        
        // eje y primero
        if (y1 <= y2){
            if ((y2 + alto2) > (y1 + alto1)){
                alto_nuevo = y2 - y1 + alto2;
            }else{
                alto_nuevo = alto1;
            }
        }else{
            if ((y2 + alto2) > (y1 + alto1)){
                alto_nuevo = alto2;
            }else{
                alto_nuevo = y1 - y2 + alto1;
            }
            punto_nuevo.y = y2;
        }
        
        
        return new RTree(punto_nuevo, alto_nuevo, ancho_nuevo, this.min_hijos, this.max_hijos, true);
    }
    
    // retorna el area del RTree
    public int areaActual(){
        return this.ancho*this.alto;
    }
    
    public int getAlto(){
        return this.alto;
    }
    
    public void setAlto(int alto){
        this.alto = alto;
    }
    
    public int getAncho(){
        return this.ancho;
    }
    
    public void setAncho(int ancho){
        this.ancho = ancho;
    }
    
    public Point getBase(){
        return this.base;
    }
    
    public void setBase(Point base){
        this.base = base;
    }
    
    public boolean isHoja(){
        return this.es_hoja;
    }
    
    public int getCantidadHijos(){
        return this.cant_hijos;
    }
    
    public int getMinHijos(){
        return this.min_hijos;
    }
    
    public int getMaxHijos(){
        return this.max_hijos;
    }
    
    public List<RTree> getHijos(){
        return this.hijos;
    }
    
    public boolean addHijo(RTree hijo){
        if(this.cant_hijos < this.max_hijos){
            this.hijos.add(hijo);
            this.cant_hijos++;
            return true;
        }
        return false;
    }
    
    public void setHijos(List<RTree> hijos, int cant_hijos){
        this.hijos = hijos;
        this.cant_hijos = cant_hijos;
    }
    
    
    public List<RTree> buscar(RTree rtree){
        List<RTree> encontrados = new ArrayList<RTree>();
        if(this.hijos.get(0).es_hoja){ //todos son hojas
            for(RTree hijo : this.hijos){
                if(hijo == null)
                    break;
                if(hijo.seIntersectaCon(rtree))
                    encontrados.add(hijo);
            }
            return encontrados;
        }
        else{
            List<RTree> todos_rts = new ArrayList<RTree>();
            for(RTree hijo : this.hijos){
                if(hijo == null)
                    break;
                if(hijo.seIntersectaCon(rtree))
                    todos_rts.addAll(hijo.buscar(rtree));
            }
            return todos_rts;
        }
    }
    
    public List<RTree> insertar(RTree rtree){
        if(this.hijos.get(0).es_hoja){ //todos son hojas
            System.out.println("tengo hijos");
            if(this.cant_hijos < this.max_hijos){
                this.hijos.add(rtree);
                this.cant_hijos++;
                System.out.println("AHORA SI INSERTO");
                List<RTree> yo = new ArrayList<RTree>();
                yo.add(this);
                return yo;
            }
            else{
                System.out.println("AHORA SI OVERFLOW");
                //insertar, ojo con el maximo -> mejor trabajar con arraylist.
                return this.linear_split(); //TODO OVERFLOW
                
            }
        }
        else{
            System.out.println("no tengo hijos");
            List<Integer> index_min = new ArrayList<Integer>(); // candidatos donde se encuentra el RTree que menos tiene que crecer
            int area_crece = -1; // area que debe crecer
            int index = 0;
            
            //busco los RTree que crecen menos
            for(RTree hijo : this.hijos){
                if(hijo == null)
                    break;
                if(area_crece == -1){ //es el primer estadar
                    index_min.add(index);
                    area_crece = hijo.rectanguloNuevo(rtree).areaActual() - hijo.areaActual();
                }
                else 
                    if(hijo.rectanguloNuevo(rtree).areaActual() - hijo.areaActual() == area_crece){
                        index_min.add(index);
                    }
                    else {
                        if(hijo.rectanguloNuevo(rtree).areaActual() - hijo.areaActual() < area_crece){
                            index_min = new ArrayList<Integer>();
                            area_crece = hijo.rectanguloNuevo(rtree).areaActual() - hijo.areaActual();
                            index_min.add(index);
                        }
                    }
                index++;
            }
            
            //Busco de los RTree que crecen menos los que tienen menor area
            List<Integer> index_min_areas = new ArrayList<Integer>(); // candidatos donde se encuentra el RTree que menos tiene que crecer
            int min_area = -1;
            for (int min : index_min){
                if (min_area == -1){
                    index_min_areas.add(min);
                    min_area = this.hijos.get(min).areaActual();
                }
                else{
                    if(min_area == this.hijos.get(min).areaActual()){
                        index_min_areas.add(min);
                    }
                    else if(this.hijos.get(min).areaActual() < min_area){
                        index_min_areas = new ArrayList<Integer>();
                        min_area = this.hijos.get(min).areaActual();
                        index_min_areas.add(min);
                    }
                }
            }
            
            if(index_min_areas.size() > 1){
                double indice_d_random = Math.floor(Math.random() * index_min_areas.size());
                int indice_random = (int)indice_d_random;
                //System.out.println("RANDOM en " + indice_random);
                RTree hijo_aux =  this.hijos.get(index_min_areas.get(indice_random));
                RTree rtree_aux = hijo_aux.rectanguloNuevo(rtree);
                
                //Cambio el rectangulo nodo
                this.hijos.get(index_min_areas.get(indice_random)).setBase(rtree_aux.getBase());
                this.hijos.get(index_min_areas.get(indice_random)).setAlto(rtree_aux.getAlto());
                this.hijos.get(index_min_areas.get(indice_random)).setAncho(rtree_aux.getAncho());
                
                
                List<RTree> hijos = this.hijos.get(index_min_areas.get(indice_random)).insertar(rtree);
                if(hijos.size()>1){
                    this.hijos.set(index_min_areas.get(indice_random), hijos.get(0));
                    this.hijos.add(hijos.get(1));
                    this.cant_hijos++;
                    if(this.cant_hijos>this.max_hijos){
                        return this.linear_split();
                    }
                }
                else{
                    this.hijos.set(index_min_areas.get(indice_random), hijos.get(0));
                }
                List<RTree> yo = new ArrayList<RTree>();
                yo.add(this);
                return yo;
            }
            else{
                //System.out.println("Seleccionando " + index_min_areas.get(0));
                
                RTree hijo_aux =  this.hijos.get(index_min_areas.get(0));
                RTree rtree_aux = hijo_aux.rectanguloNuevo(rtree);
                
                //Cambio el rectangulo nodo
                this.hijos.get(index_min_areas.get(0)).setBase(rtree_aux.getBase());
                this.hijos.get(index_min_areas.get(0)).setAlto(rtree_aux.getAlto());
                this.hijos.get(index_min_areas.get(0)).setAncho(rtree_aux.getAncho());
                
                
                
                List<RTree> hijos = this.hijos.get(index_min_areas.get(indice_random)).insertar(rtree);
                if(hijos.size()>1){
                    this.hijos.set(index_min_areas.get(indice_random), hijos.get(0));
                    this.hijos.add(hijos.get(1));
                    this.cant_hijos++;
                    if(this.cant_hijos>this.max_hijos){
                        return this.linear_split();
                    }
                }
                else{
                    this.hijos.set(index_min_areas.get(indice_random), hijos.get(0));
                }
                List<RTree> yo = new ArrayList<RTree>();
                yo.add(this);
                return yo;
                
            }
        }
        
    }
    
    
    public boolean compareTo(RTree rtree){
        int x1 = this.base.x;
        int y1 = this.base.y;
        int ancho1 = this.ancho;
        int alto1 = this.alto;
        int min1 = this.min_hijos;
        int max1 = this.max_hijos;
        boolean tipo1 = this.es_hoja;
        List<RTree> hijos1 = this.hijos;
        
        int x2 = rtree.getBase().x;
        int y2 = rtree.getBase().y;
        int ancho2 = rtree.getAncho();
        int alto2 = rtree.getAlto();
        int min2 = rtree.getMinHijos();
        int max2 = rtree.getMaxHijos();
        boolean tipo2 = rtree.isHoja();
        List<RTree> hijos2 = rtree.getHijos();                                                                                                                                                                                                                                                                                                                   
        
        if ( (x1 == x2) && (y1 == y2) && (ancho1 == ancho2) 
                && (alto1 == alto2) && (min1 == min2) && (max1 == max2)
                && (tipo1 == tipo2) ){
                    return true;
                }else{
                    return false;
                }
    }
    
    public String toString(){
        String hijos = "";
        int i =0;
        for(RTree r : this.hijos){
            hijos += "H["+i+"] = " + r.getBase();
            i++;
        }
        
        return "Punto: ("+this.base.x+","+this.base.y+")\n"+" ancho: "+this.ancho
                +"\n alto: "+this.alto+"\n tipo: "+this.es_hoja
                +"\n cantidad de hijos: "+this.cant_hijos+"\n Hijos:"+hijos;
    }
    
    //true si los RTree se intersectan
    public boolean seIntersectaCon(RTree rtree){
        List<RTree> rts = menorEnY(this, rtree);
        Point p1 = rts.get(0).getBase();
        Point p2 = rts.get(1).getBase();
        int alto1 = rts.get(0).getAlto();
        int ancho1 = rts.get(0).getAncho();
        int ancho2 = rts.get(1).getAncho();
        if((p1.x <= p2.x && p1.x+ancho1 >= p2.x && p1.y + alto1 >= p2.y) || (p1.x >= p2.x && p1.x <= ancho2 + p2.x && p1.y + alto1 >= p2.y)){
            return true;
        }
        return false;
    }
    
    
    public List<RTree> quadratic_split(){
        RTree min1_MBR = this.hijos.get(0);
        RTree min2_MBR = this.hijos.get(1);
        int area_libre = min1_MBR.rectanguloNuevo(min2_MBR).areaActual() - min1_MBR.areaActual() - min2_MBR.areaActual();
        int indice_hijo1 = 0;
        int indice_hijo2 = 1;
        for (int i = 1; i < this.cant_hijos-1; i++){
            RTree primero_MBR = this.hijos.get(i);
            for (int j = i+1; j < this.cant_hijos; j++){
                RTree segundo_MBR = this.hijos.get(j);
                int area_libre_calculada = primero_MBR.rectanguloNuevo(segundo_MBR).areaActual() - primero_MBR.areaActual() - segundo_MBR.areaActual();
                if(area_libre_calculada > area_libre){
                    min1_MBR = primero_MBR;
                    min1_MBR = segundo_MBR;
                    area_libre = area_libre_calculada;
                    indice_hijo1 = i;
                    indice_hijo2 = j;
                }
            }
        }
        
        List<Integer> mbrs_elegidos = new ArrayList<Integer>();
        mbrs_elegidos.add(indice_hijo1);
        mbrs_elegidos.add(indice_hijo2);
        List<Integer> hijos_que_quedan = new ArrayList<Integer>();
        for (int i = 0; i < this.cant_hijos; i++){
            hijos_que_quedan.add(i);
        }
        hijos_que_quedan.remove(mbrs_elegidos.get(0));
        hijos_que_quedan.remove(mbrs_elegidos.get(1));
        //TODO hacer test Integer o int
        long seed = System.nanoTime();
        Collections.shuffle(hijos_que_quedan, new Random(seed));
        //test de si entrega otro o lo hace en el mismo
        
        RTree rtree1 = this.hijos.get(mbrs_elegidos.get(0).intValue());
        RTree rtree2 = this.hijos.get(mbrs_elegidos.get(1).intValue());
        
        RTree r1nuevo = new RTree(rtree1);
        RTree r2nuevo = new RTree(rtree2);
        
        for(int i = 0; i < hijos_que_quedan.size(); i++){
            if( r1nuevo.rectanguloNuevo(this.hijos.get(hijos_que_quedan.get(i))).areaActual() 
                > r2nuevo.rectanguloNuevo(this.hijos.get(hijos_que_quedan.get(i))).areaActual() ){
                    if(r1nuevo.getCantidadHijos() < r1nuevo.max_hijos + 1 - r1nuevo.min_hijos){
                        r1nuevo.insertar(this.hijos.get(hijos_que_quedan.get(i)));
                    }else{
                        r2nuevo.insertar(this.hijos.get(hijos_que_quedan.get(i)));
                    }
                }else{
                    if(r2nuevo.getCantidadHijos() < r2nuevo.max_hijos + 1 - r2nuevo.min_hijos){
                        r2nuevo.insertar(this.hijos.get(hijos_que_quedan.get(i)));
                    }else{
                        r1nuevo.insertar(this.hijos.get(hijos_que_quedan.get(i)));
                    }
                }
        }
        List<RTree> retorno = new ArrayList<RTree>();
        retorno.add(r1nuevo);
        retorno.add(r2nuevo);
        return retorno;
        
    }
    
    
    public List<RTree> linear_split(){
        
        List<Integer> lejanos = this.buscar_lejanos();
        List<Integer> hijos_que_quedan = new ArrayList<Integer>();
        for (int i = 0; i < this.cant_hijos; i++){
            hijos_que_quedan.add(i);
        }
        hijos_que_quedan.remove(lejanos.get(0));
        hijos_que_quedan.remove(lejanos.get(1));
        //TODO hacer test Integer o int
        long seed = System.nanoTime();
        Collections.shuffle(hijos_que_quedan, new Random(seed));
        //test de si entrega otro o lo hace en el mismo
        
        RTree rtree1 = this.hijos.get(lejanos.get(0).intValue());
        RTree rtree2 = this.hijos.get(lejanos.get(1).intValue());
        
        RTree r1nuevo = new RTree(rtree1);
        RTree r2nuevo = new RTree(rtree2);
        
        for(int i = 0; i < hijos_que_quedan.size(); i++){
            if( r1nuevo.rectanguloNuevo(this.hijos.get(hijos_que_quedan.get(i))).areaActual() 
                > r2nuevo.rectanguloNuevo(this.hijos.get(hijos_que_quedan.get(i))).areaActual() ){
                    if(r1nuevo.getCantidadHijos() < r1nuevo.max_hijos + 1 - r1nuevo.min_hijos){
                        r1nuevo.insertar(this.hijos.get(hijos_que_quedan.get(i)));
                    }else{
                        r2nuevo.insertar(this.hijos.get(hijos_que_quedan.get(i)));
                    }
                }else{
                    if(r2nuevo.getCantidadHijos() < r2nuevo.max_hijos + 1 - r2nuevo.min_hijos){
                        r2nuevo.insertar(this.hijos.get(hijos_que_quedan.get(i)));
                    }else{
                        r1nuevo.insertar(this.hijos.get(hijos_que_quedan.get(i)));
                    }
                }
        }
        List<RTree> retorno = new ArrayList<RTree>();
        retorno.add(r1nuevo);
        retorno.add(r2nuevo);
        return retorno;
    }
    
    //funcion aux para LinearSplit, los indices de los hijos mas alejados dado la definición para LinearSplit
    public List<Integer> buscar_lejanos(){
        int index = 0;
        //el indice del que tiene el x derecho más a la izquierda
        int index_min_x = -1;
        int index_max_x = -1;
        int index_min_y = -1;
        int index_max_y = -1;
        int min_x = -1;
        int max_x = -1;
        int min_y = -1;
        int max_y = -1;
        for(RTree hijo : this.hijos){
            if(hijo == null){
                System.out.println("ALGO MALO OCURRIO ACAAAAAAAAAAAAAAA");
                break;
            }
            if(index == 0){ 
                //Primera iteracion, tengo uno, por lo que es minimo y
                // maximo en todo
                index_min_x = 0;
                index_max_x = 0;
                index_min_y = 0;
                index_max_x = 0;
                min_x = hijo.getBase().x + hijo.getAncho();
                max_x = hijo.getBase().x;
                min_y = hijo.getBase().y + hijo.getAlto();
                max_y = hijo.getBase().y;
            }
            else{
                if(min_x > hijo.getBase().x + hijo.getAncho()){
                    index_min_x = index;
                    min_x = hijo.getBase().x + hijo.getAncho();
                }
                
                if(max_x < hijo.getBase().x){
                    index_max_x = index;
                    max_x = hijo.getBase().x;
                }
                
                if(min_y > hijo.getBase().y + hijo.getAlto()){
                    index_min_y = index;
                    min_y = hijo.getBase().y + hijo.getAlto();
                }
                
                if(max_y < hijo.getBase().y){
                    index_max_y = index;
                    max_y = hijo.getBase().y;
                } 
            }
            index++;
        }
        List<Integer> rtrees_mas_lejos = new ArrayList<Integer>();
        if((max_x - min_x)/this.ancho >= (max_y - min_y)/this.alto){
            rtrees_mas_lejos.add(index_min_x);
            rtrees_mas_lejos.add(index_max_x);
            return rtrees_mas_lejos;
        }
        else{
            rtrees_mas_lejos.add(index_min_y);
            rtrees_mas_lejos.add(index_max_y);
            return rtrees_mas_lejos;
        }
    }
    
    // dado 2 RTree retorna rs.get(0) el menor en el eje Y (base.y), rs.get(1) el mayor en Y
    public static List<RTree> menorEnY(RTree rtree1, RTree rtree2){
        Point base1 = rtree1.getBase();
        Point base2 = rtree2.getBase();
        List<RTree> rs = new ArrayList<RTree>();
        if (base1.y <= base2.y){
            rs.add(rtree2);
            rs.add(rtree1);
            return rs;
        }
        else{
            rs.add(rtree2);
            rs.add(rtree1);
            return rs;
        }
    }
}
    
