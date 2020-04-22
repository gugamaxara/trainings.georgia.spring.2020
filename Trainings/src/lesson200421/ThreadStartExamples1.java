package lesson200421;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.Utils;

public class ThreadStartExamples1 {

	public static void main(String[] args) {
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		while(true) {
			executor.execute(() -> {
				System.out.println("hi there");
				Utils.pause(1000);
			});
		}
		
		
//		System.out.println(Thread.currentThread());
//
//		Thread.currentThread().setName("MyThread");
//		Thread.currentThread().setPriority(7);
//		System.out.println(Thread.currentThread());
//
//		new Thread(() -> System.out.println(Thread.currentThread()))
//				.start();
//
//		new Thread(ThreadStartExamples1::cycle).start();
	}

//	private static void cycle() {
//		while (true) {
//			System.out.println(" hi there");
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}

}
