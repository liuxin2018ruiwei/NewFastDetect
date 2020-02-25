package com.reconova.face_reco_demo.task;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaskQueue {

	private SingleStack stack = new SingleStack();

	final Lock lock = new ReentrantLock();

	final Condition notEmpty = lock.newCondition();

	private boolean lockQueue = false;

	public Object lockQueue() {
		lockQueue = true;
		return stack.get();
	}

	public void unlockQueue() {
		lockQueue = false;
	}

	public void addTaskunLock(Object data) {
		try {
			produce(data);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void addTask(Object data) {
		try {
			if (lockQueue)
				return;
			produce(data);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void produce(Object data) throws InterruptedException {
		lock.lock();
		try {
			stack.add(data);
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}

	public Object consume() throws InterruptedException {
		lock.lock();
		Object data = null;
		try {
			while (stack.isEmpty())
				notEmpty.await(1, TimeUnit.SECONDS);
			data = stack.get();
		} finally {
			lock.unlock();
		}
		return data;
	}
}
