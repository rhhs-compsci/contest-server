import java.io.BufferedReader;
import java.io.InputStreamReader;

public class daddy
{

	public static void main(String[] args) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		int n = Integer.parseInt(br.readLine());
		
		int h1 = Math.min(5, n);
		int h2 = n - h1;
		
		int ways = 0;
		while (h1 >= h2)
		{
			ways++;
			h1--;
			h2++;
		}
		
		System.out.println(ways);
	}
}