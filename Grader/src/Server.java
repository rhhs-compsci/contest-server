import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
public class Server {
	public static ArrayList<User> users = new ArrayList<User>();
	public static Contest contest = new Contest("Contest3", 1412284273947L);
	static{
		
		contest.problems.put("bear", new Problem("Contest1/Problem 1", "Problem 1", 10, new String[]{"j1.1.in", "j1.2.in", "j1.3.in", "j1.4.in", "j1.5.in", "j1.6.in", "j1.7.in", "j1.8.in", "j1.9.in", "j1.10.in"}, new String[]{"j1.1.out", "j1.2.out", "j1.3.out", "j1.4.out", "j1.5.out", "j1.6.out", "j1.7.out", "j1.8.out", "j1.9.out", "j1.10.out"}));
		contest.problems.put("rsa", new Problem("Contest1/Problem 2", "Problem 2", 20, new String[]{"j2.1.in", "j2.2.in", "j2.3.in", "j2.4.in", "j2.5.in", "j2.6.in", "j2.7.in", "j2.8.in", "j2.9.in", "j2.10.in"}, new String[]{"j2.1.out", "j2.2.out", "j2.3.out", "j2.4.out", "j2.5.out", "j2.6.out", "j2.7.out", "j2.8.out", "j2.9.out", "j2.10.out"}));
		contest.problems.put("four-word", new Problem("Contest1/Problem 3", "Problem 3", 50, new String[]{"j3.1.in"}, new String[]{"j3.1.out"}));
		contest.problems.put("cold", new Problem("Contest2/p1", "It's Cold Here!", 10, new String[]{"p1.1.in", "p1.2.in", "p1.3.in", "p1.4.in", "p1.5.in"}, new String[]{"p1.1.out", "p1.2.out", "p1.3.out", "p1.4.out", "p1.5.out"}));
		contest.problems.put("icon", new Problem("Contest2/p2", "Icon Scaling", 20, new String[]{"p2.1.in", "p2.2.in", "p2.3.in", "p2.4.in", "p2.5.in"}, new String[]{"p2.1.out", "p2.2.out", "p2.3.out", "p2.4.out", "p2.5.out"}));
		contest.problems.put("friends", new Problem("Contest2/p3", "Friends", 50, new String[]{"p3.1.in", "p3.2.in", "p3.3.in", "p3.4.in", "p3.5.in"}, new String[]{"p3.1.out", "p3.2.out", "p3.3.out", "p3.4.out", "p3.5.out"}));

	}
	public static boolean contest_in_progress = true;
	public static class ServerThread extends Thread {
		@Override
		public void run(){
			try{
				InetAddress host_address = InetAddress.getLocalHost();
				ServerSocket server_socket = new ServerSocket(63400, 0, host_address);
				System.out.println(host_address);
				//long end_time = contest.length+System.currentTimeMillis();
				while(contest_in_progress){
					if(System.currentTimeMillis() > contest.end_time){
						contest_in_progress = false;
					}
					try{
						for(int i = 0; i < users.size(); i++)
							if(!users.get(i).connected)
								users.remove(i--);
						User new_user = new User(server_socket.accept());
						users.add(new_user);
						try{
							Thread.sleep(200);
						}catch(InterruptedException e){
						}
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("Contest Over");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	public static void main(String[] args){
		Frame frame = new Frame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
class User {
	boolean connected;
	String name; 
	Socket socket;
	User_Thread thread;
	User(Socket socket){
		this.socket = socket;
		connected = true;
		thread = new User_Thread();
		thread.start();
	}
	public void purge(){
		connected = false;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	class User_Thread extends Thread{
		public void run(){
			long submission_time = System.currentTimeMillis();
			InputStream socket_input;
			try {
				socket_input = socket.getInputStream();
			OutputStream socket_output = socket.getOutputStream();
			int score = 0;
			try {
				
//				BufferedReader br = new BufferedReader(new InputStreamReader(socket_input));
//				String name = br.readLine();
//				Problem problem = Server.contest.problems.get(br.readLine());
//				byte[] file_array = new byte [Integer.parseInt(br.readLine())];
//				String file_name = br.readLine();
				
				String name = readLine(socket_input).replace(" ", "_");
				if(name.indexOf('/')!=-1||name.indexOf('\\')!=-1){
					socket_output.write(("Invalid Team Name\n").getBytes());
					return;
				}
				Problem problem = Server.contest.problems.get(readLine(socket_input));
				String fileName = readLine(socket_input);
				byte[] fileArray = new byte[Integer.parseInt(readLine(socket_input))];
				
				socket_input.read(fileArray, 0, fileArray.length);
				new File("Submissions/"+name).mkdirs();
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("Submissions/"+name+"/"+fileName));
				bos.write(fileArray, 0, fileArray.length);
				bos.close();
				
				Process compile = Runtime.getRuntime().exec("javac Submissions/"+name+"/"+fileName);
				compile.waitFor();
				
			    int passed = 0;
			    boolean[] results = new boolean[problem.input_file_names.length];
			    String[] responses = new String[problem.input_file_names.length];
			    for(int caseNo = 0; caseNo < problem.input_file_names.length; caseNo++){
					Process execute =  Runtime.getRuntime().exec("java -classpath Submissions/"+name+" "+fileName.substring(0, fileName.lastIndexOf('.')));
					String line = null;
					PrintWriter out = new PrintWriter(new OutputStreamWriter(execute.getOutputStream()));
				    BufferedReader fin = new BufferedReader(new FileReader(problem.path+"/"+problem.input_file_names[caseNo]));
				    while ((line = fin.readLine()) != null)
				        out.println(line);
				    fin.close();
				    out.close();
				    BufferedReader in = new BufferedReader(new InputStreamReader(execute.getInputStream()));
				    fin = new BufferedReader(new FileReader(problem.path+"/"+problem.output_file_names[caseNo]));
				    if(!problem.path.equals("Problem 3")){
				    	boolean working = true;
					    while ((line = fin.readLine()) != null && working) {
					        if(!line.equals(in.readLine())){
					        	working = false;
					        }
					    }
					    if(results[caseNo] = working)
					    	passed++;
					    in.close();
					    fin.close();
				    }else{
				    	if(results[caseNo] = check_p3(in, fin))
				    		passed++;
				    }
				    in = new BufferedReader(new InputStreamReader(execute.getErrorStream()));
				    line = in.readLine();
				    responses[caseNo] = ("Case #" + (caseNo + 1) + ": ");
				    if (line != null) {
					    do {
					        responses[caseNo] += (line + '\n');
					    } while ((line = in.readLine()) != null);
				    }else
				    	responses[caseNo]+=(((results[caseNo]==true) ? "Correct" : "Incorrect") + "\n");
				    socket_output.write(responses[caseNo].getBytes());
				    execute.waitFor();
			    }
			    score = (int) (problem.weight*passed+(Server.contest.end_time - submission_time)/300000);
			    socket_output.write((passed + "/" + problem.input_file_names.length + "\n").getBytes());
			    int old_score = 0;
			    BufferedReader file_reader = null;
			    try{
			    	file_reader = new BufferedReader(new FileReader("Submissions/"+name+"/"+problem.name+".txt"));
			    	old_score = Integer.parseInt(file_reader.readLine());	    
			    }catch(FileNotFoundException e){}
			    if(file_reader!=null)file_reader.close();
			    if(score>old_score){
				    PrintWriter file_writer = new PrintWriter("Submissions/"+name+"/"+problem.name+".txt");
				    file_writer.println(score);
				    for(int i = 0; i < problem.input_file_names.length; i++)
				    	file_writer.println(responses[i]);
				    file_writer.close();
			    }
			    System.out.println(name+": "+problem.name+" "+passed + "/" + problem.input_file_names.length+" "+score);
			} catch (Exception e) {
				e.printStackTrace();
				socket_output.write(("There was an exception\n").getBytes());
			}
			socket_output.write(("Your score for this problem is: " +score+"\n").getBytes());
		    socket_output.write((byte)'\u0004');
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			purge();
		}
	}
	public boolean check_p3(BufferedReader in, BufferedReader fin){
		try{
			ArrayList<Quple> groups = new ArrayList<Quple>();
			String line;
			while ((line = fin.readLine()) != null) {
				groups.add(new Quple(line));
			}
			fin.close();
			boolean working = true;
			while ((line = in.readLine()) != null && working) {
				for(Quple group: groups)
					if(!group.check(line))
						working = false;
			}
			for(Quple group: groups)
				if(!group.matched)
					working = false;
			in.close();
			return working;
		}catch(Exception e){
			return false;
		}
	}
	public String readLine(InputStream is) {
		String ans = "";
		while (true) {
			char next = 0;
			try {
				next = (char)is.read();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			if (next == '\n') break;
			ans += next;
		}
		return ans;
	}
}
class Quple{
	String[] e;
	boolean matched;
	Quple(String str){
		e = str.split(", ");
	}
	boolean check(String s) throws Exception{
		String[] f = s.split(", ");
		boolean b = true;
		for(int a = 0; b&&a < 4; a++){
			b = false;
			for(int c = 0; !b&&c < 4; c++){
				if(e[a].equals(f[c]))
					b = true;
			}
		}
		if(b) matched = true;
		return b;
	}
}
class Contest{
	String path;
	long end_time;
	HashMap<String, Problem> problems;
	Contest(String path, long end_time){
		this.path = path;
		this.end_time = end_time;
		problems = new HashMap<String, Problem>();
	}
}
class Problem{
	String path;
	String name;
	String[] input_file_names;
	String[] output_file_names;
	int weight;
	Problem(String path, String name, int weight, String[] input_file_names, String[] output_file_names){
		this.path = path;
		this.name = name;
		this.input_file_names = input_file_names;
		this.output_file_names = output_file_names;
		this.weight = weight;
		Server.contest.problems.put(name, this);
	}
}
class Frame extends JFrame{
	Frame(){
		super("Computer Science Club Contest");
		add(new Standings_Panel());
	}
}
class Standings_Panel extends JPanel {
	static final int WIDTH = 1024, HEIGHT = 576;
	Standings_Panel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		setBounds(100, 100, 1024, 576);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		add(contentPane);
		
		JPanel headerPanel = new JPanel();
		contentPane.add(headerPanel, BorderLayout.NORTH);
		headerPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel title = new JLabel("Computer Science Club");
		title.setFont(new Font("Dialog", Font.PLAIN, 42));
		headerPanel.add(title, BorderLayout.CENTER);
		
		JLabel ipAddress = new JLabel();
		try {
			ipAddress.setText("IP Address: " + InetAddress.getLocalHost().getHostAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ipAddress.setFont(new Font("Dialog", Font.PLAIN, 20));
		headerPanel.add(ipAddress, BorderLayout.SOUTH);
		
		JPanel bodyPanel = new JPanel();
		contentPane.add(bodyPanel, BorderLayout.CENTER);
		
		JTable leaderboard = new JTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		bodyPanel.add(leaderboard);
		
		JPanel buttonPanel = new JPanel();
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		
		JButton startContest = new JButton("Start Contest");
		startContest.setFont(UIManager.getFont("CheckBoxMenuItem.acceleratorFont"));
		startContest.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				Server.contest_in_progress = true;
				Server.ServerThread thread = new Server.ServerThread();
				thread.start();
			}

			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		buttonPanel.add(startContest);
	}
}