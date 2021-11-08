package battleship_sockets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Server {
    
    public void gameLogic() throws Exception{
        int maxlength = 1500;
        DatagramSocket socket = new DatagramSocket(2020);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        
        System.out.println("Server is running.");
        Scanner keyboard = new Scanner(System.in);
        
        byte[] buffer = new byte[maxlength];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String message = new String(buffer).trim();
        System.out.println("Recieved: "+message);
        
        InetAddress senders_address = packet.getAddress();
        int senders_port = packet.getPort();
        System.out.println("SENDING MESSAGE: ");
        String[] names = {"uno","CONEXIÓN CONFIRMADA","tres"};
        //-----------------------        
//        ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(contentStream);
        oos.writeObject(names);
        buffer = baos.toByteArray();
        packet = new DatagramPacket(buffer, buffer.length, senders_address, senders_port);
        socket.send(packet);
        baos.flush();
        oos.flush();
        System.out.println("Sent: "+names[1]);
        
        ///ACTUAL GAME STARTS HEEEEERE
        List<Ship> shipsPlayer1 = new ArrayList<>();
        List<Ship> shipsPlayer2 = new ArrayList<>();
        List<Board> boards;
        
        int nShips = 3;
        Input gameHandler = new Input();
        boards = gameHandler.getBoards();
        Board boardP1 = boards.get(0);
        Board boardP2 = boards.get(1);
        
        
        System.out.println("ESPERANDO TURNO DE JUGADOR.................");        
        buffer = new byte[maxlength];
        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
        List<int[]> ShipandCoordinates = (List<int[]>)ois.readObject();
        senders_address = packet.getAddress();
        senders_port = packet.getPort();        
        System.out.println("Respuesta recibida---------------------------");
        for(int[] coords : ShipandCoordinates){
            System.out.println(coords[0]+":"+coords[1]+":"+coords[2]);
        }  
              
        //Llenando tablero del jugador:
        for (int i = 0; i < nShips; i++) {
            int row = ShipandCoordinates.get(i)[0];
            int col = ShipandCoordinates.get(i)[1];
            int shipType = ShipandCoordinates.get(i)[2];
            Ship ship1 = gameHandler.createShip(0, row, col, shipType);
            //Crear barco, mientras el lugar indicado sea falso, vuelve a preguntar donde crearlo
            while(ship1.isPlacementOk(ship1, shipsPlayer1, boardP1) == false){
                ship1 = gameHandler.createShip(0, row, col, shipType);
            }
            shipsPlayer1.add(ship1);
        }
        
        System.out.println("------------- turno del servidor-----------");
        //row, col, shipType
        ShipandCoordinates = new ArrayList<>();
//        ShipandCoordinates.add(new int[] {6,6,1}); ///PRUEBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//        ShipandCoordinates.add(new int[] {4,4,1});    
        for(int i = 0; i < nShips; i++){
            ShipandCoordinates.add(new int[] {new Random().nextInt(10), new Random().nextInt(10), new Random().nextInt(4)+1});
        }
        //Llenar tablero del Servidor
        for (int i = 0; i < nShips; i++) {
            int serv_row = ShipandCoordinates.get(i)[0];
            int serv_col = ShipandCoordinates.get(i)[1];
            int serv_shipType = ShipandCoordinates.get(i)[2];
            System.out.println("Fila seleccionada para barco "+(i+1)+": "+serv_row);
            System.out.println("Columna seleccionada para barco "+(i+1)+": "+serv_col);
            System.out.println("Tipo de barco seleccionado"+(i+1)+": "+serv_shipType);
            Ship ship1 = gameHandler.createShip(1, serv_row, serv_col, serv_shipType);
            //Crear barco, mientras el lugar indicado sea falso, vuelve a preguntar donde crearlo
            while(ship1.isPlacementOk(ship1, shipsPlayer2, boardP2) == false){
                ship1 = gameHandler.createShip(1, serv_row, serv_col, serv_shipType);
            }
            shipsPlayer2.add(ship1);
        }
        baos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(baos);
        oos.writeObject(ShipandCoordinates);
        buffer = baos.toByteArray();
        packet = new DatagramPacket(buffer, buffer.length, senders_address, senders_port);
        socket.send(packet);
        oos.flush();
        baos.flush();        
        System.out.println("-------Posicionamiento de barco enviado--------");
        
        Player player1 = new Player(shipsPlayer1, boardP2);
        Player player2 = new Player(shipsPlayer2, boardP1);
        boolean gameOn = true;
        Display display = new Display();
        System.out.println("<<<<<<<<<<<<<<<<<<<<<Tablero JUGADOR>>>>>>>>>>>>>>>>>");
        display.printBoard(boardP1);
        int numberOfShipsPlayer1 = player1.numberOfSquaresOfShips(shipsPlayer1);
        int numberOfShipsPlayer2 = player2.numberOfSquaresOfShips(shipsPlayer2);
        
        while(gameOn){
            System.out.println("Esperando tiro de Cliente*********************");
            buffer = new byte[maxlength];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            ois = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
            int[] ShootCoordinates = (int[])ois.readObject();
            senders_address = packet.getAddress();
            senders_port = packet.getPort();  
            System.out.println("Disparo recibido en las coordenadas: ["+ShootCoordinates[0]+":"+ShootCoordinates[1]+"]");
            int row = ShootCoordinates[0];
            int col = ShootCoordinates[1];            
 
            if(player2.handleShot(ShootCoordinates[0], ShootCoordinates[1])){
                display.printBoard(player2.getBoard());
                numberOfShipsPlayer2--;
            } else {
                display.printBoard(player2.getBoard());
            }
            if(numberOfShipsPlayer2 == 0){
                display.printBoard(player2.getBoard());
                System.out.println("Jugador gana!!!");
                break;
            }
            
            //TIRO DE SERVIDOR
            System.out.println("<<<<<<TIRO DEL SERVIDOR>>>>>>>");
            int serv_row = new Random().nextInt(10);
            int serv_col = new Random().nextInt(10);
            ShootCoordinates = gameHandler.shoot(1,serv_row, serv_col);
            
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(ShootCoordinates);
            buffer = baos.toByteArray();
            packet = new DatagramPacket(buffer, buffer.length, senders_address, senders_port);
            socket.send(packet);
            oos.flush();
            baos.flush();
            System.out.println("Tiro enviado al Jugador con las coordenadas: ["+ShootCoordinates[0]+":"+ShootCoordinates[1]+"]");            
            if (player1.handleShot(ShootCoordinates[0], ShootCoordinates[1])) {
                display.printBoard(player1.getBoard());
                numberOfShipsPlayer1--;
            }else{
                display.printBoard(player1.getBoard());
            }
            if(numberOfShipsPlayer1 == 0){
                display.printBoard(player1.getBoard());
                System.out.println("Servidor gana!!!");
                break;
            }
            
        }
        
        
        oos.close();
    }

    public Server() {
        try {
            gameLogic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public static void main(String[] args) {
        new Server();
        
    }
    
}
