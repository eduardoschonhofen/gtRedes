import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


//Nome:Eduardo Osielski Schonhofen
//Disciplina:Redes de Computadores
//Gerator de Tráfego(IPERF)

public class gtRedes {

	static Integer modifier;
	static Integer rounds;
	static Integer porta;
	static Integer taxa;
	static String servidor;
	static InetAddress IPAddress;
	static Integer lastSend;
	static Integer timeToSleep;
	static DatagramSocket clientSocket;
	public static void main(String args[]) throws Exception{
	
 
		clientSocket = new DatagramSocket();
 
		servidor = args[1];
		 porta = Integer.parseInt(args[3]);
		 taxa = Integer.parseInt(args[4]);
		 IPAddress = InetAddress.getByName(servidor);
 
		
		//125 bytes(1kb) - 28 bytes(headers) 
		Integer left = taxa;
		modifier=0;
		rounds = taxa/((modifier+28)*8);
		
		//Calcular número de rounds de disparo de bits.
		while(rounds>1000)
		{
			modifier++;
			rounds=taxa/((modifier+28)*8);
		}
		
		System.out.println(modifier);
		System.out.println(rounds);

		timeToSleep=(int)Math.floor(1000/(Math.ceil(rounds)));
		lastSend = taxa-(rounds*((modifier+28)*8))-(28*8);
		if(lastSend<0) {
		noLastSend();
		}
		else
		{
		withLastSend();
		}
	
	
	}
	
	static void noLastSend() throws Exception
	{
		byte[] sendData = new byte[modifier];
		DatagramPacket sendPacket = new DatagramPacket(sendData,0, IPAddress, porta);
			do {
			
			for(Integer i=1;i<rounds;i++)
			{
			sendPacket.setLength(modifier);
			clientSocket.send(sendPacket);
			Thread.sleep(timeToSleep);
			}
			System.out.println("Enviei a sequencia");
			
		}
		while(true);
	}
	
	static void withLastSend() throws Exception
	{
		byte[] sendData = new byte[modifier];
		DatagramPacket sendPacket = new DatagramPacket(sendData,0, IPAddress, porta);
		do {
			
			for(Integer i=1;i<rounds;i++)
			{
			sendPacket.setLength(modifier);
			clientSocket.send(sendPacket);
			Thread.sleep(timeToSleep);
			}
			sendPacket.setLength(0);
			clientSocket.send(sendPacket);
			System.out.println("Enviei a sequencia");
			
		}
		while(true);
	}
}
