import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class blood
{

	public static void main(String[] args) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		int[] donors = new int[8];
		for (int i = 0; i < 8; i++)
		{
			donors[i] = Integer.parseInt(st.nextToken());
		}
		
		st = new StringTokenizer(br.readLine());
		
		int[] patients = new int[8];
		for (int i = 0; i < 8; i++)
		{
			patients[i] = Integer.parseInt(st.nextToken());
		}
		
		int saved = 0;
		
		for (int i = 0; i < 8; i++)
		{
			for (int j = 7; j >= 0; j--)
			{
				if (compatible(i, j))
				{
					int donated = Math.min(patients[i], donors[j]);
					donors[j] -= donated;
					patients[i] -= donated;
					saved += donated;
				}
			}
		}
		
		System.out.println(saved);
	}
	
	public static boolean compatible(int r, int d)
	{
		if (r % 2 == 0 && d % 2 != 0)
		{
			return false;
		}
		if (r == 6 || r == 7)
		{
			return true;
		}
		if (d == 0 || d == 1)
		{
			return true;
		}
		if ((d == 2 || d == 3) && (r == 2 || r == 3))
		{
			return true;
		}
		if ((d == 4 || d == 5) && (r == 4 || r == 5))
		{
			return true;
		}
		return false;
	}
}