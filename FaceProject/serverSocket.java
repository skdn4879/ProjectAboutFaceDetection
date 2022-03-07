import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer implements Runnable{
	public static final int ServerPort = 9999;	//Select Port
	public static String command = "";
	@Override
	public void run(){
		try{
			ServerSocket serverSocket = new ServerSocket(ServerPort);	//Create Socket
			System.out.println("Conneting...");
			while(true){	//Waiting for Client Connection
				Socket client = serverSocket.accept();	//Detect Data Transport
				System.out.println("Receiving...");
				try{
					//Receive Client Data
					//Get String from InputStream and Save in 'in' as Bufferstram
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					//Transform Data as String, Saved in 'in' and Save 'str'
					String str = in.readLine();
					System.out.println("Received: " + str);
					
					doSomthing(str);
					
					//Send Strings to Client
					PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
					out.println(str);
				} catch(Exception e){
					System.out.println("Error");
					e.printStackTrace();
				} finally{
					client.close();	//Close Socket
					//System.out.println("Done");
				}
			}
		} catch(Exception e){
			System.out.println("S: Error");
			e.printStackTrace();
		}
	}
	
	public static void doSomthing(String instruction){
		String[] inst = instruction.split(" ");
		String thing = inst[0];
		String doing = inst[1];
		
		switch(thing){
			case "Led1":
				System.out.println("Led1 Selected");
				switch(doing){
					case "ON":
						System.out.println("Instruction: " + thing + " ON");
						command = "/home/pi/Graduation/shellCode/turnLed1On.sh";
						doCommand(command);
						break;
					case "OFF":
						System.out.println("Instruction: " + thing + " OFF");
						command = "/home/pi/Graduation/shellCode/turnLed1Off.sh";
						doCommand(command);
						break;
				}
				break;
			case "Led2":
				System.out.println("Led2 Selected");
				switch(doing){
					case "ON":
						System.out.println("Instruction: " + thing + " ON");
						command = "/home/pi/Graduation/shellCode/turnLed2On.sh";
						doCommand(command);
						break;
					case "OFF":
						System.out.println("Instruction: " + thing + " OFF");
						command = "/home/pi/Graduation/shellCode/turnLed2Off.sh";
						doCommand(command);
						break;
				}
				break;
			case "Door":
				System.out.println("Door Selected");
				switch(doing){
					case "ON":
						System.out.println("Instruction: " + thing + " ON");
						command = "/home/pi/Graduation/shellCode/DoorOpen.sh";
						doCommand(command);
						break;
					case "OFF":
						System.out.println("Instruction: " + thing + " OFF");
						command = "/home/pi/Graduation/shellCode/DoorStop.sh";
						doCommand(command);
						break;
				}
				break;
			case "Circular":
				System.out.println("Circular Selected");
				switch(doing){
					case "ON":
						System.out.println("Instruction: " + thing + " ON");
						command = "/home/pi/Graduation/shellCode/CircularOn.sh";
						doCommand(command);
						break;
					case "OFF":
						System.out.println("Instruction: " + thing + " OFF");
						command = "/home/pi/Graduation/shellCode/CircularOff.sh";
						doCommand(command);
						break;
				}
				break;
			case "All":
				System.out.println("All Device Selected");
				switch(doing){
					case "ON":
						System.out.println("Instruction: " + thing + " Device ON");
						command = "/home/pi/Graduation/shellCode/AllDeviceOn.sh";
						doCommand(command);
						break;
					case "OFF":
						System.out.println("Instruction: " + thing + " Device OFF");
						command = "/home/pi/Graduation/shellCode/AllDeviceOff.sh";
						doCommand(command);
						break;
				}
		}
	}
	
	public static void doCommand(String command){
		ExecCommand execCommand = new ExecCommand(command);
		execCommand.start();
	}

	public static void main(String[] args){
		Thread ServerThread = new Thread(new SocketServer());	//Activate as Thread
		ServerThread.start();	//Start Server
	}
}

public class ExecCommand extends Thread{
	
	private String command;
		
	ExecCommand(){}
	ExecCommand(String command){
		this.command = command;
	}
		
	@Override
	public void run(){
		try{
			Process ps = Runtime.getRuntime().exec(command);
			ps.waitFor();
			ps.destroy();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
