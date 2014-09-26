import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Server {
	public static ArrayList<User> users = new ArrayList<User>();
	public static Contest contest = new Contest("Contest1", 60*60000);
//	static{
//		contest.problems.put("Problem 1", new Problem("Contest1/Problem 1", new String[]{"j1.1.in", "j1.2.in", "j1.3.in", "j1.4.in", "j1.5.in", "j1.6.in", "j1.7.in", "j1.8.in", "j1.9.in", "j1.10.in"}, new String[]{"j1.1.out", "j1.2.out", "j1.3.out", "j1.4.out", "j1.5.out", "j1.6.out", "j1.7.out", "j1.8.out", "j1.9.out", "j1.10.out"}));
//		contest.problems.put("Problem 2", new Problem("Contest1/Problem 2", new String[]{"j2.1.in", "j2.2.in", "j2.3.in", "j2.4.in", "j2.5.in", "j2.6.in", "j2.7.in", "j2.8.in", "j2.9.in", "j2.10.in"}, new String[]{"j2.1.out", "j2.2.out", "j2.3.out", "j2.4.out", "j2.5.out", "j2.6.out", "j2.7.out", "j2.8.out", "j2.9.out", "j2.10.out"}));
//		contest.problems.put("Problem 3", new Problem("Contest1/Problem 3", new String[]{"j3.1.in"}, new String[]{"j3.1.out"}));
//	}
	public static boolean contest_in_progress = true;
	public static class ServerThread extends Thread {
		@Override
		public void run(){
			try{
				InetAddress host_address = InetAddress.getLocalHost();
				ServerSocket server_socket = new ServerSocket(63400, 0, host_address);
				System.out.println(host_address);
				long end_time = contest.length+System.currentTimeMillis();
				while(contest_in_progress){
					if(System.currentTimeMillis() > end_time){
						contest_in_progress = false;
					}
					for(int i = 0; i < users.size(); i++)
						if(!users.get(i).connected)
							users.remove(i--);
					User new_user = new User(server_socket.accept());
					users.add(new_user);
					try{
						Thread.sleep(200);
					}catch(InterruptedException e){
						
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
class Frame extends JFrame{
	Frame(){
		super("Computer Science Club Contest");
		add(new Standings_Panel());
	}
}
//class Button_Panel extends JPanel{
//	Button_Panel(){
//		setPreferredSize(new Dimension(256, HEIGHT));
//		
//	}
//}
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
		
		JTable leaderboard = new JTable();
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
//		
//		JPanel header = new JPanel();
//		header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
//		JLabel title = new JLabel("Computer Science Club!", JLabel.CENTER);
//		title.setFont(getFont().deriveFont(42.0f));
//		header.add(title);
//		
////		JTable leaderboard = new JTable(new String[][] {{}}, new String[] {"Name", "Problem 1", "Problem 2", "Problem 3"});
////		JScrollPane scrollPane = new JScrollPane(leaderboard);
////		leaderboard.setFillsViewportHeight(true);
////		add(leaderboard);
////		add(scrollPane);
//		
//		JButton start_contest_button = new JButton("Start Contest");
//		start_contest_button.addMouseListener(new MouseListener(){
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
//				Server.contest_in_progress = true;
//				Server.ServerThread thread = new Server.ServerThread();
//				thread.start();
//			}
//			@Override
//			public void mouseEntered(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//			}
//			@Override
//			public void mouseExited(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			@Override
//			public void mousePressed(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
//		add(start_contest_button);
//		//this.setBackground(Color.ORANGE);
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
			try {
				InputStream socket_input = socket.getInputStream();
				OutputStream socket_output = socket.getOutputStream();
				
//				BufferedReader br = new BufferedReader(new InputStreamReader(socket_input));
//				String name = br.readLine();
//				Problem problem = Server.contest.problems.get(br.readLine());
//				byte[] file_array = new byte [Integer.parseInt(br.readLine())];
//				String file_name = br.readLine();
				
				String name = readLine(socket_input);
				String probName = readLine(socket_input);
				Problem problem = Server.contest.problems.get(probName);
				String fileName = readLine(socket_input);
				byte[] fileArray = new byte[Integer.parseInt(readLine(socket_input))];
				
				socket_input.read(fileArray, 0, fileArray.length);
				
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
				bos.write(fileArray, 0, fileArray.length);
				bos.close();
				
				Process compile = Runtime.getRuntime().exec("javac "+fileName);
				compile.waitFor();
				
			    int passed = 0;
			    for(int caseNo = 0; caseNo < problem.input_file_names.length; caseNo++){
					Process execute =  Runtime.getRuntime().exec("java "+fileName.substring(0, fileName.lastIndexOf('.')));
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
					    if(working)passed++;
					    in.close();
					    fin.close();
				    }else{
				    	if(check_p3(in, fin)) passed++;
				    }
				    in = new BufferedReader(new InputStreamReader(execute.getErrorStream()));
				    line = in.readLine();
				    if (line != null) {
					    socket_output.write(("Case #" + (caseNo + 1) + ": ").getBytes());
					    do {
					        socket_output.write((line + '\n').getBytes());
					    } while ((line = in.readLine()) != null);
				    }
				    execute.waitFor();
				    
			    }
			    Calendar now = Calendar.getInstance();
			    socket_output.write((passed + "/" + problem.input_file_names.length).getBytes());
			    System.out.printf("%d:%02d%02d: %s got %d/%d on %s%n", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), name, passed, problem.input_file_names.length, probName);
			    socket_output.write((byte)'\u0004');
			    //socket.getOutputStream();passed+"/"+problem.input_file_names.length;
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	long length;
	HashMap<String, Problem> problems;
	Contest(String path, long length){
		this.path = path;
		this.length = length;
		problems = new HashMap<String, Problem>();
	}
	Contest(String filename) {
		BufferedReader br;
		try
		{
			br = new BufferedReader(new FileReader(filename));
			length = Long.parseLong(br.readLine());
			String name = br.readLine();
			String probPath = br.readLine();
			String[] inFiles = new String[Integer.parseInt(br.readLine())];
			String[] outFiles = new String[inFiles.length];
			for (int i = 0; i < inFiles.length; i++) {
				String file = br.readLine();
				inFiles[i] = file + ".in";
				outFiles[i] = file + ".out";
			}
			problems.put(name, new Problem(probPath, inFiles, outFiles));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
class Problem{
	String path;
	String[] input_file_names;
	String[] output_file_names;
	Problem(String path, String[] input_file_names, String[] output_file_names){
		this.path = path;
		this.input_file_names = input_file_names;
		this.output_file_names = output_file_names;
	}
}