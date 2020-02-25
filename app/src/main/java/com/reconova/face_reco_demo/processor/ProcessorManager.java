package com.reconova.face_reco_demo.processor;

import android.os.AsyncTask;

public class ProcessorManager {
	public boolean isWorking;
	long startTime = 0;
	
	public ProcessorManager() {
		isWorking = false;
	}

	/**
	 * 开始处理事件
	 * 
	 * @param iProcessor
	 */
	public void startProcessor(IProcessor iProcessor) {
		if (!isWorking) {
			isWorking = true;
			startTime = System.currentTimeMillis();
			ProcessorTask task = new ProcessorTask(iProcessor);
			task.execute();
		}
	}

	class ProcessorTask extends AsyncTask<Object, Object, Object> {
		private IProcessor mProcessor;

		public ProcessorTask(IProcessor iProcessor) {
			mProcessor = iProcessor;
		}

		/**
		 * 子线程中处理事件
		 */
		protected Object doInBackground(Object... arg0) {
			return mProcessor.onProcess();
		}

		/**
		 * 完成事件处理，更新UI界面
		 */
		protected void onPostExecute(Object result) {
			mProcessor.onPostExcute(result);
			isWorking = false;
		}
	}
	
	public static interface IProcessor {
		/**
		 * 事件处理
		 * 
		 * @return 处理的结果
		 */
		public Object onProcess();

		/**
		 * 完成事件处理，更新UI主界面
		 * 
		 */
		public void onPostExcute(Object result);
	}
}

