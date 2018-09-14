class Race {
	public static void main (String[] args) throws InterruptedException{
		final int[] arr = new int[100];
		Thread one = new Thread() {
			public void run() {
				synchronized (arr){//comment this
					for (int i = 0; i < arr.length * 100000; i++) {
						arr[i % arr.length]--;
					}
				}
			}
		};
		Thread two = new Thread() {
			public void run() {
				synchronized (arr) {//comment this
					for (int i = 0; i < arr.length * 100000; i++) {
						arr[i % arr.length]++;
					}
				}
			}
		};
		one.start();
		two.start();
		one.join();
		two.join();
		for (int i = 0; i < arr.length; i++) {
			System.out.print(arr[i]+" ");
		}
	}
}
