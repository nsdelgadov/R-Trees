import java.io.IOException;
import java.lang.System.*;
import java.awt.Point;
import java.util.*;

class Main{
    public static void main(String[] args) throws IOException{
        System.out.println("hellos worlds");
        tests_areas();
    }
    
    public static void tests_areas(){
        RTree r1 = new RTree(new Point(5,5), 5, 5, 2, 5, true);
        
        RTree r2 = new RTree(new Point(4,4), 2, 2, 2, 5, true);
        RTree r3 = new RTree(new Point(9,4), 2, 2, 2, 5, true);
        RTree r4 = new RTree(new Point(7,4), 7, 1, 2, 5, true);
        RTree r5 = new RTree(new Point(4,7), 1, 7, 2, 5, true);
        RTree r6 = new RTree(new Point(4,9), 2, 2, 2, 5, true);
        RTree r7 = new RTree(new Point(9,9), 2, 2, 2, 5, true);
        RTree r8 = new RTree(new Point(4,4), 7, 7, 2, 5, true);
        RTree r9 = new RTree(new Point(6,6), 3, 3, 2, 5, true);
        
        RTree r2nuevo = r1.rectanguloNuevo(r2);
        RTree r3nuevo = r1.rectanguloNuevo(r3);
        RTree r4nuevo = r1.rectanguloNuevo(r4);
        RTree r5nuevo = r1.rectanguloNuevo(r5);
        RTree r6nuevo = r1.rectanguloNuevo(r6);
        RTree r7nuevo = r1.rectanguloNuevo(r7);
        RTree r8nuevo = r1.rectanguloNuevo(r8);
        RTree r9nuevo = r1.rectanguloNuevo(r9);
        
        RTree r2ok = new RTree(new Point(4,4), 6, 6, 2, 5, true);
        RTree r3ok = new RTree(new Point(5,4), 6, 6, 2, 5, true);
        RTree r4ok = new RTree(new Point(5,4), 7, 5, 2, 5, true);
        RTree r5ok = new RTree(new Point(4,5), 5, 7, 2, 5, true);
        RTree r6ok = new RTree(new Point(4,5), 6, 6, 2, 5, true);
        RTree r7ok = new RTree(new Point(5,5), 6, 6, 2, 5, true);
        RTree r8ok = new RTree(new Point(4,4), 7, 7, 2, 5, true);
        RTree r9ok = new RTree(new Point(5,5), 5, 5, 2, 5, true);
        
        RTree[] rsok = {r2ok, r3ok, r4ok, r5ok, r6ok, r7ok, r8ok, r9ok};
        RTree[] rsnuevos = {r2nuevo, r3nuevo, r4nuevo, r5nuevo, r6nuevo, r7nuevo, r8nuevo, r9nuevo};
        for (int i = 0; i < 8; i++){
            System.out.println("R"+i+"son iguales?"+rsok[i].compareTo(rsnuevos[i]));
            if(rsok[i].compareTo(rsnuevos[i])== false){
                System.out.println("R ok"+rsok[i]);
                System.out.println("R nuevo"+ rsnuevos[i]);
            }
        }
        
    }
}

class RTree{
    private Point base;
    private int alto, ancho;
    private int cant_hijos, min_hijos, max_hijos;
    private boolean es_hoja; //tipo = True -> Hoja, tipo = False -> MBR;
    private RTree[] hijos;
    
    public RTree(Point p, int alto, int ancho, int min_hijos, int max_hijos, boolean es_hoja){
        this.base = p;
        this.alto = alto;
        this.ancho = ancho;
        this.min_hijos = min_hijos;
        this.max_hijos = max_hijos;
        this.es_hoja = es_hoja;
        this.hijos = new RTree[max_hijos];
    }
    
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
    
    public int areaActual(){
        return this.ancho*this.alto;
    }
    
    public int getAlto(){
        return this.alto;
    }
    
    public int getAncho(){
        return this.ancho;
    }
    
    public Point getBase(){
        return this.base;
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
    
    public RTree[] getHijos(){
        return this.hijos;
    }
    
    
    public String insertar(RTree rtree){
        if(this.hijos[0].es_hoja) //todos son hojas
            if(this.cant_hijos < max_hijos){
                this.hijos[this.cant_hijos] = rtree;
                this.cant_hijos++;
                return "HICE MI PEGA SOY BKN";
            }
            else
                return "ACA DEBERIA HACER UN OVERFLOW"; //TODO OVERFLOW
        else{
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
                    min_area = this.hijos[min].areaActual();
                }
                else{
                    if(min_area == this.hijos[min].areaActual()){
                        index_min_areas.add(min);
                    }
                    else if(this.hijos[min].areaActual() < min_area){
                        index_min_areas = new ArrayList<Integer>();
                        min_area = this.hijos[min].areaActual();
                        index_min_areas.add(min);
                    }
                }
            }
            
            if(index_min_areas.size() > 1)
                return "WEA RANDOM";
            else
                return "LA PRIMERA WEA";
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
        RTree[] hijos1 = this.hijos;
        
        int x2 = rtree.getBase().x;
        int y2 = rtree.getBase().y;
        int ancho2 = rtree.getAncho();
        int alto2 = rtree.getAlto();
        int min2 = rtree.getMinHijos();
        int max2 = rtree.getMaxHijos();
        boolean tipo2 = rtree.isHoja();
        RTree[] hijos2 = rtree.getHijos();
        
        if ( (x1 == x2) && (y1 == y2) && (ancho1 == ancho2) 
                && (alto1 == alto2) && (min1 == min2) && (max1 == max2)
                && (tipo1 == tipo2) ){
                    return true;
                }else{
                    return false;
                }
    }
    
    public String toString(){
        return "Punto: ("+this.base.x+","+this.base.y+")\n"+"ancho: "+this.ancho
                +"\n alto: "+this.alto+"\n tipo: "+this.es_hoja;
    }
}
    
