import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.net.MulticastSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
class Chat
{
  static class Worker extends Thread
  {
    public void run()
    {
     // En un ciclo infinito se recibirán los mensajes enviados al grupo 
     // 230.0.0.0 a través del puerto 50000 y se desplegarán en la pantalla.
        try{
            InetAddress grupo = InetAddress.getByName("230.0.0.0");
            MulticastSocket socket = new MulticastSocket(50000);
            socket.joinGroup(grupo);
        
        while(true){
            byte[] buffer = recibe_mensaje(socket,40);
            ByteBuffer b = ByteBuffer.wrap(buffer);
            int longitud = b.getInt();
            
            byte[] a = recibe_mensaje(socket,longitud+1);
            System.out.print(new String(a,"UTF-8")+" escribe:");

            buffer = recibe_mensaje(socket,40);           
            b = ByteBuffer.wrap(buffer);
            longitud = b.getInt();

            a = recibe_mensaje(socket,longitud+1);
            System.out.println(new String(a,"UTF-8"));
        }
        }catch(Exception e){}     
        
        

    }
  }
    static byte[] recibe_mensaje(MulticastSocket socket,int longitud) throws IOException{
    
        byte[] buffer = new byte[longitud];
        DatagramPacket paquete = new DatagramPacket(buffer,buffer.length);
        socket.receive(paquete);
        return buffer;
    }



    static void envia_mensaje(byte[] buffer,String ip,int puerto) throws IOException{

        DatagramSocket socket = new DatagramSocket();
        InetAddress grupo = InetAddress.getByName(ip);
        DatagramPacket paquete = new DatagramPacket(buffer,buffer.length,grupo,puerto);
        socket.send(paquete);
        socket.close();
    
    }

  public static void main(String[] args) throws Exception
  {
    Worker w = new Worker();
    w.start();

    String nombre = args[0];
    String cadena = "";
    Scanner reader = new Scanner(System.in);
    BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Saludos "+nombre);
    System.out.println("Enter para enviar");
    ByteBuffer bint = ByteBuffer.allocate(40); 

    //InetAddress grupo = InetAddress.getByName("230.0.0.0");
    MulticastSocket socket = new MulticastSocket(50000);
    //socket.joinGroup(grupo);

    // En un ciclo infinito se leerá los mensajes del teclado y se enviarán
    // al grupo 230.0.0.0 a través del puerto 50000.
    while(true){
        
       
        cadena = reader.nextLine();

        //envia longitud nombre y limpia el buffer
        int longitudNombre = nombre.length();
        bint.putInt(longitudNombre);
        envia_mensaje(bint.array(),"230.0.0.0",50000);
        bint.rewind();               
        //Envia el nombre
        envia_mensaje(nombre.getBytes(),"230.0.0.0",50000);

        int longitud = cadena.length();               
        bint.putInt(longitud);

        //envia longitud cadena                       
        envia_mensaje(bint.array(),"230.0.0.0",50000);
        //Envia la cadena de texto
        envia_mensaje(cadena.getBytes(),"230.0.0.0",50000);
        //limpia el buffer
        bint.rewind();
       // System.out.println("Enviado");
    }
  }


}
