package com.reconova.face_reco_demo.task;

public class SingleStack {
	
	private static Object[] signStack = new Object[1]; 
	int size;
	
	public static void main(String[] args) {
		SingleStack stack = new SingleStack();
		stack.add("a");
		stack.add("b");
		stack.add("c");
		System.out.println(stack.get());
	}
	
	public SingleStack() {
		clear();
	}
	
	public boolean isEmpty() {
		return size == 0 ? true : false;
	}
	
	public void clear() {
		signStack[0] = null;	
		size = 0;
	}
	
	public void add(Object data) {
		signStack[0] = data;
		size = 1;
	}
	
	public Object get() {
		Object x = signStack[0];
		size = 0;
		return x;
	}
}
