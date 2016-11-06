import java.io.IOException;
import java.lang.*;
import java.awt.Point;
import java.util.*;
import java.util.Collections;

public class Main{
    public static void main(String[] args) throws IOException{
        System.out.println("hellos worlds");
        //tests_areas();
        //tests_interseccion();
        //tests_buscar();
        tests_insertar();
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
        RTree r10 = new RTree(new Point(5,5), 1, 1, 2, 5, true);
        
        RTree r2nuevo = r1.rectanguloNuevo(r2);
        RTree r3nuevo = r1.rectanguloNuevo(r3);
        RTree r4nuevo = r1.rectanguloNuevo(r4);
        RTree r5nuevo = r1.rectanguloNuevo(r5);
        RTree r6nuevo = r1.rectanguloNuevo(r6);
        RTree r7nuevo = r1.rectanguloNuevo(r7);
        RTree r8nuevo = r1.rectanguloNuevo(r8);
        RTree r9nuevo = r1.rectanguloNuevo(r9);
        RTree r10nuevo = r1.rectanguloNuevo(r10);
        
        RTree r2ok = new RTree(new Point(4,4), 6, 6, 2, 5, true);
        RTree r3ok = new RTree(new Point(5,4), 6, 6, 2, 5, true);
        RTree r4ok = new RTree(new Point(5,4), 7, 5, 2, 5, true);
        RTree r5ok = new RTree(new Point(4,5), 5, 7, 2, 5, true);
        RTree r6ok = new RTree(new Point(4,5), 6, 6, 2, 5, true);
        RTree r7ok = new RTree(new Point(5,5), 6, 6, 2, 5, true);
        RTree r8ok = new RTree(new Point(4,4), 7, 7, 2, 5, true);
        RTree r9ok = new RTree(new Point(5,5), 5, 5, 2, 5, true);
        RTree r10ok = new RTree(new Point(5,5), 5, 5, 2, 5, true);
        
        List<RTree> rsok = new ArrayList<RTree>();
        rsok.add(r2ok);
        rsok.add(r3ok);
        rsok.add(r4ok);
        rsok.add(r5ok);
        rsok.add(r6ok);
        rsok.add(r7ok);
        rsok.add(r8ok);
        rsok.add(r9ok);
        rsok.add(r10ok);
        
        List<RTree> rsnuevos = new ArrayList<RTree>();
        rsok.add(r2nuevo);
        rsok.add(r3nuevo);
        rsok.add(r4nuevo);
        rsok.add(r5nuevo);
        rsok.add(r6nuevo);
        rsok.add(r7nuevo);
        rsok.add(r8nuevo);
        rsok.add(r9nuevo);
        rsok.add(r10nuevo);
        
        for (int i = 0; i < 9; i++){
            System.out.println("Test nuevoRectangulo "+i+" "+rsok.get(i).compareTo(rsnuevos.get(i)));
            if(rsok.get(i).compareTo(rsnuevos.get(i))== false){
                System.out.println("R ok"+rsok.get(i));
                System.out.println("R nuevo"+ rsnuevos.get(i));
            }
        }
        
    }
    
    public static void tests_insertar(){
        
        /*
        http://imgur.com/2iScsCU
        Test de overflow
        */
        System.out.println("--------");
        RTree r2 = new RTree(new Point(0,1), 2, 1, 1, 2, true);
        RTree r3 = new RTree(new Point(1,1), 1, 2, 1, 2, true);
        RTree r4 = new RTree(new Point(1,0), 1, 1, 1, 2, true);
        RTree r1 = new RTree(2,r2,r3);
        
        List<RTree> overflow = r1.insertar(r4);
        System.out.println("Test Overflow: "+overflow);
        
        
        System.out.println("--------");
        RTree r2In = new RTree(new Point(0,1), 2, 1, 1, 2, true);
        RTree r3In = new RTree(new Point(1,1), 1, 2, 1, 2, true);
        RTree r4In = new RTree(new Point(1,0), 1, 1, 1, 2, true);
        RTree r1In = new RTree(3,r2,r3);
        
        List<RTree> inserta = r1In.insertar(r4In);
        System.out.println("Test inseta bien "+inserta);
        
        
        /*
        Anterior a este:
        http://imgur.com/nlz212I
        El r2,3,6 estaban un cuadrado a la izquierda que la foto.
        Test de elección a (1) = r7.
        */
        
        System.out.println("--------");
        RTree r2A = new RTree(new Point(1,1), 3, 1, 1, 3, true);
        RTree r3A = new RTree(new Point(3,1), 1, 1, 1, 3, true);
        
        RTree r4A = new RTree(new Point(7,4), 1, 3, 1, 3, true);
        RTree r5A = new RTree(new Point(8,1), 1, 2, 1, 3, true);
        
        RTree r6A = new RTree(new Point(1,1), 3, 3, 1, 3, false);
        RTree r7A = new RTree(new Point(7,1), 4, 3, 1, 3, false);
        
        List<RTree> hijos6A = new ArrayList<RTree>();
        hijos6A.add(r2A);
        hijos6A.add(r3A);
        List<RTree> hijos7A = new ArrayList<RTree>();
        hijos6A.add(r4A);
        hijos6A.add(r5A);
        
        r6A.setHijos(hijos6A, 2);
        r7A.setHijos(hijos7A, 2);
        
        RTree r1A = new RTree(3, r6A, r7A);
        
        RTree r8A = new RTree(new Point(6,2), 2, 1, 1, 3, true);
        
        List<RTree> inserta1A = r1A.insertar(r8A);
        System.out.println("Test de eleccion (1) "+inserta1A);
        
        /*
        http://imgur.com/nlz212I
        Test de aumento de actualizacion del rectangulo. 
        Al aumentar efectivamente el area despues de insertar r8,
        debe insertar en r7 (1), porque su area aumentara menos que r6, pero 
        solo si se actualiza bien.
        */
        
        System.out.println("--------");
        RTree r2B = new RTree(new Point(2,1), 3, 1, 1, 3, true);
        RTree r3B = new RTree(new Point(4,1), 1, 1, 1, 3, true);
        
        RTree r4B = new RTree(new Point(7,4), 1, 3, 1, 3, true);
        RTree r5B = new RTree(new Point(8,1), 1, 2, 1, 3, true);
        
        RTree r6B = new RTree(new Point(2,1), 3, 3, 1, 3, false);
        RTree r7B = new RTree(new Point(7,1), 4, 3, 1, 3, false);
        
        List<RTree> hijos6B = new ArrayList<RTree>();
        hijos6A.add(r2B);
        hijos6A.add(r3B);
        List<RTree> hijos7B = new ArrayList<RTree>();
        hijos6A.add(r4B);
        hijos6A.add(r5B);
        
        r6B.setHijos(hijos6B, 2);
        r7B.setHijos(hijos7B, 2);
        
        RTree r1B = new RTree(3, r6B, r7B);
        
        RTree r8B = new RTree(new Point(9,2), 4, 1, 1, 3, true);
        RTree r9B = new RTree(new Point(5,5), 1, 1, 1, 3, true);
        
         
        if(r7B.getCantidadHijos() != 2){
            System.out.println("fail en cantidad de hijos de 7B, deberían ser 2");
        }
        
        List<RTree> inserta1B = r1B.insertar(r8B);
        
        if(r7B.getCantidadHijos() != 3){
            System.out.println("fail en cantidad de hijos de 7B, deberían ser 3");
        }
        
        System.out.println("Test inserta bien "+inserta1B);
        
        System.out.println("--------");
        List<RTree> inserta2B = r1B.insertar(r9B);
        System.out.println("Test actualiza a menor crecimiento de area (1) "+inserta2B);
    
        /*
        http://imgur.com/uU9AzV4
        Test en que todo es igual, por lo que decide al azar
        */
        
        System.out.println("--------");
        RTree r2C = new RTree(new Point(1,1), 1, 2, 1, 3, true);
        RTree r3C = new RTree(new Point(1,2), 1, 1, 1, 3, true);
        
        RTree r4C = new RTree(new Point(4,1), 2, 1, 1, 3, true);
        RTree r5C = new RTree(new Point(5,1), 1, 1, 1, 3, true);
        
        RTree r6C = new RTree(new Point(1,1), 2, 2, 1, 3, false);
        RTree r7C = new RTree(new Point(4,1), 2, 2, 1, 3, false);
        
        List<RTree> hijos6C = new ArrayList<RTree>();
        hijos6A.add(r2C);
        hijos6A.add(r3C);
        List<RTree> hijos7C = new ArrayList<RTree>();
        hijos6A.add(r4C);
        hijos6A.add(r5C);
        
        r6C.setHijos(hijos6C, 2);
        r7C.setHijos(hijos7C, 2);
        
        RTree r1C = new RTree(3, r6C, r7C);
        
        RTree r8C = new RTree(new Point(3,2), 1, 1, 1, 3, true);
        
        List<RTree> inserta1C = r1C.insertar(r8C);
        System.out.println("Test todo igual, aleatorio "+ inserta1C);
        
        
        /*
        http://imgur.com/pD3Oxae
        Test en que crecimiento es igual, pero no las areas, por lo que escoge
        r6 (0).
        */
        
        
        System.out.println("--------");
        RTree r2D = new RTree(new Point(1,1), 1, 2, 1, 3, true);
        RTree r3D = new RTree(new Point(2,1), 1, 1, 1, 3, true);
        
        RTree r4D = new RTree(new Point(4,2), 1, 3, 1, 3, true);
        RTree r5D = new RTree(new Point(5,1), 1, 1, 1, 3, true);
        
        RTree r6D = new RTree(new Point(1,1), 2, 2, 1, 3, false);
        RTree r7D = new RTree(new Point(4,1), 2, 3, 1, 3, false);
        
        List<RTree> hijos6D = new ArrayList<RTree>();
        hijos6A.add(r2D);
        hijos6A.add(r3D);
        List<RTree> hijos7D = new ArrayList<RTree>();
        hijos6A.add(r4D);
        hijos6A.add(r5D);
        
        r6D.setHijos(hijos6D, 2);
        r7D.setHijos(hijos7D, 2);
        
        RTree r1D = new RTree(3, r6D, r7D);
        
        RTree r8D = new RTree(new Point(3,2), 1, 1, 1, 3, true);
        
        List<RTree> inserta1D = r1D.insertar(r8D);
        System.out.println("Test escoge menor area, (0) "+inserta1D);
        
    }
    
    public static void tests_interseccion(){
        RTree r1 = new RTree(new Point(1,1), 3, 3, 1, 2, true);
        
        RTree r2 = new RTree(new Point(0,3), 2, 2, 1, 2, true);
        RTree r3 = new RTree(new Point(3,3), 2, 2, 1, 2, true);
        RTree r4 = new RTree(new Point(0,0), 2, 2, 1, 2, true);
        RTree r5 = new RTree(new Point(3,0), 2, 2, 1, 2, true);
        
        RTree r6 = new RTree(new Point(2,0), 5, 1, 1, 2, true);
        RTree r7 = new RTree(new Point(0,2), 1, 5, 1, 2, true);
        
        RTree r8 = new RTree(new Point(0,4), 1, 1, 1, 2, true);
        RTree r9 = new RTree(new Point(4,4), 1, 1, 1, 2, true);
        RTree r10 = new RTree(new Point(4,0), 1, 1, 1, 2, true);
        RTree r11 = new RTree(new Point(0,0), 1, 1, 1, 2, true);
        
        RTree r12 = new RTree(new Point(0,2), 1, 1, 1, 2, true);
        RTree r13 = new RTree(new Point(2,4), 1, 1, 1, 2, true);
        RTree r14 = new RTree(new Point(4,2), 1, 1, 1, 2, true);
        RTree r15 = new RTree(new Point(2,0), 1, 1, 1, 2, true);
        
        RTree r16 = new RTree(new Point(3,3), 1, 1, 1, 2, true);
        RTree r17 = new RTree(new Point(1,1), 3, 3, 1, 2, true);
        RTree r18 = new RTree(new Point(1,0), 5, 4, 1, 2, true);
        List<RTree> rsTrue = new ArrayList<RTree>();
        rsTrue.add(r2);
        rsTrue.add(r3);
        rsTrue.add(r4);
        rsTrue.add(r5);
        rsTrue.add(r6);
        rsTrue.add(r7);
        rsTrue.add(r8);
        rsTrue.add(r9);
        rsTrue.add(r10);
        rsTrue.add(r11);
        rsTrue.add(r12);
        rsTrue.add(r13);
        rsTrue.add(r14);
        rsTrue.add(r15);
        rsTrue.add(r16);
        rsTrue.add(r17);
        rsTrue.add(r18);
        
        for (int i = 0; i < rsTrue.size(); i++){
            String test;
            if(rsTrue.get(i).seIntersectaCon(r1)){
                test = " ok";
            }else{
                test = " fail";
            }
            System.out.println("TestTrue "+i+test);
        }
        
        boolean t1 = r2.seIntersectaCon(r3);
        boolean t2 = r2.seIntersectaCon(r4);
        boolean t3 = r2.seIntersectaCon(r5);
        
        
        boolean t4 = r7.seIntersectaCon(r8);
        boolean t5 = r7.seIntersectaCon(r9);
        boolean t6 = r7.seIntersectaCon(r13);
        
        boolean t7 = r12.seIntersectaCon(r14);
        boolean t8 = r12.seIntersectaCon(r11);
        boolean t9 = r12.seIntersectaCon(r3);
        Boolean[] tests = {t1, t2, t3, t4, t5, t6, t7, t8, t9};
        
        for (int i = 0; i < tests.length; i++){
            String test;
            if(tests[i]){
                test = " fail";
            }else{
                test = " ok";
            }
            System.out.println("TestFalse "+i+test);
        }
        
        
        
    }
    
    public static void tests_buscar(){
        RTree r2B = new RTree(new Point(2,1), 3, 1, 1, 3, true);
        RTree r3B = new RTree(new Point(4,1), 1, 1, 1, 3, true);
        
        RTree r4B = new RTree(new Point(7,4), 1, 3, 1, 3, true);
        RTree r5B = new RTree(new Point(8,1), 1, 2, 1, 3, true);
        
        RTree r6B = new RTree(new Point(2,1), 3, 3, 1, 3, false);
        RTree r7B = new RTree(new Point(7,1), 4, 3, 1, 3, false);
        
        List<RTree> hijos6B = new ArrayList<RTree>();
        hijos6B.add(r2B);
        hijos6B.add(r3B);
        List<RTree> hijos7B = new ArrayList<RTree>();
        hijos6B.add(r4B);
        hijos6B.add(r5B);
        
        r6B.setHijos(hijos6B, 2);
        r7B.setHijos(hijos7B, 2);
        
        RTree r1B = new RTree(3, r6B, r7B);
        
        RTree r8B = new RTree(new Point(9,2), 4, 1, 1, 3, true);
        
        List<RTree> inserta1B = r1B.insertar(r8B);
        
        RTree rTest1True = new RTree(new Point(6,1), 2, 5, 1, 3, true);
        RTree rTest2True = new RTree(new Point(5,4), 2, 2, 1, 3, true);
        RTree rTest3True = new RTree(new Point(4,1), 1, 5, 1, 3, true);
        RTree rTest4True = new RTree(new Point(5,1), 1, 3, 1, 3, true);
        RTree rTest5True = new RTree(new Point(2,1), 5, 9, 1, 3, true);
        RTree rTest6True = new RTree(new Point(3,1), 1, 1, 1, 3, true);
        
        
        RTree rTest1Null = new RTree(new Point(5,5), 1, 1, 1, 3, true);
        RTree rTest2Null = new RTree(new Point(4,3), 1, 2, 1, 3, true);
        
        List<RTree> test1 = r1B.buscar(rTest1True);
        List<RTree> test2 = r1B.buscar(rTest2True);
        List<RTree> test3 = r1B.buscar(rTest3True);
        List<RTree> test4 = r1B.buscar(rTest4True);
        List<RTree> test5 = r1B.buscar(rTest5True);
        List<RTree> test6 = r1B.buscar(rTest6True);
        
        List<RTree> test7 = r1B.buscar(rTest1Null);
        List<RTree> test8 = r1B.buscar(rTest2Null);
        
        
        System.out.println("----Test de Buscar----");
        System.out.println("--------");
        System.out.println("Test 1: "+test1);
        System.out.println("--------");
        System.out.println("Test 2: "+test2);
        System.out.println("--------");
        System.out.println("Test 3: "+test3);
        System.out.println("--------");
        System.out.println("Test 4: "+test4);
        System.out.println("--------");
        System.out.println("Test 5: "+test5);
        System.out.println("--------");
        System.out.println("Test 6: "+test6);
        System.out.println("--------");
        System.out.println("Test 7: "+test7);
        System.out.println("--------");
        System.out.println("Test 8: "+test8);
        System.out.println("--------");
    }
}

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
            double indice_d_random = Math.floor(Math.random() * index_min_areas.size());
            int indice_random = (int)indice_d_random;
            if(index_min_areas.size() > 1){
                
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
    
