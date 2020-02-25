package com.reconova.face_reco_demo.task;

abstract public class  WorkThread extends Thread {
	
	boolean exit = false;
	TaskQueue storage;
	
	public abstract void taskWork(Object taskData);
	
	public void exitThread(boolean exit) {
		this.exit = exit;
	}
	
	public void run() {
		try {
			while(!exit) {
				Object taskData = storage.consume();
				taskWork(taskData);
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	public WorkThread(TaskQueue storage) {
		this.storage = storage;			
	}

}
