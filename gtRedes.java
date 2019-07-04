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
    static Integer packetLength=42;
    public static void main(String args[]) throws Exception{


        clientSocket = new DatagramSocket();

        servidor = args[1];
        porta = Integer.parseInt(args[3]);
        taxa = Integer.parseInt(args[4]);
        taxa=taxa*1000; //transformar em Kbit( de acordo com definição, 1000 é 1Mbit,logo,1 é 1Kbit)
        taxa=taxa/8; //Converte para bytes
        IPAddress = InetAddress.getByName(servidor);


        Integer left = taxa;
        modifier=0;

        //Taxa dividida pelo tamanho do pacote
        rounds = taxa/packetLength;

        //Calcular o valor da modificação de bytes do pacote
        modifier= (taxa-(rounds*packetLength))/rounds;



        timeToSleep=(int)Math.floor(1000/rounds);

        if(taxa!=(rounds*(packetLength+modifier)))
            lastSend=(taxa-(rounds*(packetLength+modifier)));


        System.out.println(modifier);
        System.out.println(rounds);
        System.out.println(lastSend);
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
