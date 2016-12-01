import java.io.IOException;

public class Tester {
	
	int FIELD1, FIELD2;
	String FIELD3;
	double FIELD4;

	public static int happy(int n, int q) {
		int status = 1;
		int num = 3;
		n = 10;
		if (n >= 1) {
			System.out.println("First " + n + " happy numbers are :-");
			System.out.println(2);
		}
		int count = 2;
		while(count <=n) {
			int j = 2;
			while(j <= Math.sqrt(num)) {
				if ( num%j == 0 ) {
					status = 0;
					break;
				}
				j++;
			}
			if (status != 0){
				System.out.println(num);
				count++;
			}
			status = 1;
			num++;
		}
		
		// New Code for Testing
		int t = happy (2, 3);
		Tester tester = new Tester();
		try {
			tester.hellofunc();
		} catch (NegativeArraySizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t = t * t;
		
		
		return 0;
	}
	
	void hellofunc () throws IOException, NegativeArraySizeException {
		System.out.print ("Hello");
		int var = 2*9;
	}
}
