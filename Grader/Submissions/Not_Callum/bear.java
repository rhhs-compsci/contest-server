import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class bear
{

	public static void main(String[] args) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int[] nums = new int[3];
		nums[0] = Integer.parseInt(br.readLine());
		nums[1] = Integer.parseInt(br.readLine());
		nums[2] = Integer.parseInt(br.readLine());
		Arrays.sort(nums);
		System.out.println(nums[1]);
	}
}