import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//COMPILAÇÃO:
//1:javac -d . gtRedes.java
//2:jar cvmf MANIFEST.MF gtRedes.jar gtRedes.class
//3:java -jar gtRedes.jar -i ipDestino -p portaDestino -r velKbps


//Nome:Eduardo Osielski Schonhofen
//Disciplina:Redes de Computadores
//Gerator de Tráfego(IPERF)
//Teste Realizado:1000,10000, 7535
//Verificação:Tamanho do pacote enviado no wireshark, total de pacotes enviados  com o IO Graphs, Filtro:udp.dstport == PORT and udp and !icmp

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
    static Integer packetLength=42;
    public static void main(String args[]) throws Exception{


        clientSocket = new DatagramSocket();

        servidor = args[1];
        porta = Integer.parseInt(args[3]);
        taxa = Integer.parseInt(args[5]);
        taxa=taxa*1000; //transformar em Kbit( de acordo com definição, 1000 é 1Mbit,logo,1 é 1Kbit)
        taxa=taxa/8; //Converte para bytes
        IPAddress = InetAddress.getByName(servidor);


        Integer left = taxa;
        modifier=0;

        //Taxa dividida pelo tamanho do pacote
        rounds = taxa/packetLength;



        //Calcular o valor da modificação de bytes do pacote
	//Evitar ter mais de 40 rounds para não ocorrer Sleeps imprecisos(abaixo de 25ms)	
	//Incrementa modifier de bytes do pacote enquanto o número de rounds não for menor igual que 40,divide a taxa pelo tamanho de pacote mais modificador
	while(rounds>40)
	{
	modifier++;
	rounds=taxa/(packetLength+modifier);
	}
	//Distribui um vresto restante entre todos os pacotes enviados no segundo.
	modifier+= (taxa-(rounds*(packetLength+modifier)))/rounds;

	//Calculo tempo de sleep
        timeToSleep=(int)Math.floor(1000/rounds);

	//Verifica se há algum valor restando para enviar,se sim,enviar no último pacote do round.
	lastSend=0;
        if(taxa!=(rounds*(packetLength+modifier)))
            lastSend=(taxa-(rounds*(packetLength+modifier)));


        System.out.println(modifier);
        System.out.println(rounds);
        System.out.println(lastSend);
	System.out.println(timeToSleep);
        Send();


    }

    static void Send() throws Exception
    {
        byte[] sendData = new byte[modifier+lastSend];
        DatagramPacket sendPacket = new DatagramPacket(sendData,0, IPAddress, porta);
        do {

            for(Integer i=1;i<rounds-1;i++)
            {
                sendPacket.setLength(modifier);
                clientSocket.send(sendPacket);
                Thread.sleep(timeToSleep);
            }
            sendPacket.setLength((modifier+lastSend));
            clientSocket.send(sendPacket);
            Thread.sleep(timeToSleep);
            System.out.println("Enviei a sequencia");

        }
        while(true);
    }

}
