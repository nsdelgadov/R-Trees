package tarea1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Rectangle implements Serializable{

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