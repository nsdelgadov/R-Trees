package tarea1;

import java.util.ArrayList;
import java.util.Arrays;

public class Root extends Node {

	private static final long serialVersionUID = 6645370223161368179L;

	public Root(int m, int M, long mem_position, Rectangle r, MemoryManager memoryManager) throws Exception {
		super(m, M, mem_position, new ArrayList<Rectangle>(Arrays.asList(r)), new ArrayList<Long>(Arrays.asList((long)0)), true, memoryManager, true);//invoca un construction protected que permite saltarse la restriccion
	}

}