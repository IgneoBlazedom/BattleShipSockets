
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
import java.util.Scanner;

public class Client {   
    Display display = new Display();
    
    public void exitGame(){
        display.printExitMessage();
        System.exit(0);
    }
    
    public void gameLogic() throws Exception{
        int maxlength = 1500;
        DatagramSocket socket = new DatagramSocket();        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        Scanner keyboard = new Scanner(System.in);
        
        
        System.out.println("Ingrese un mensaje para comprobar su conexión: ");
        String message = keyboard.nextLine();
        byte[] buffer = message.getBytes();
        
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("localhost"), 2020);
        socket.send(packet);
        System.out.println("Sent "+message);
        
        buffer = new byte[maxlength];
        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        InetAddress senders_address = packet.getAddress();
        int senders_port = packet.getPort();
        //----------------------------------------
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
        String[] names = (String[])ois.readObject();
        message = names[1];
        System.out.println("Received "+message);
        
        ///ACTUAL GAME STARTS HEEEEERE
        List<Ship> shipsPlayer1 = new ArrayList<>();
        List<Ship> shipsPlayer2 = new ArrayList<>();
        List<Board> boards;
        
        int nShips = 3;
        Input gameHandler = new Input();
        boards = gameHandler.getBoards();
        Board boardP1 = boards.get(0);
        Board boardP2 = boards.get(1);
        //Llenar tablero del jugador      
        int row;
        int col;
        int shipType;
        List<int[]> ShipandCoordinates = new ArrayList<>();
        for (int i = 0; i < nShips; i++) {
            System.out.println("seleccione fila del barco : "+(i+1));
            row = keyboard.nextInt();
            keyboard.nextLine();
            System.out.println("Seleccione columna del barco : "+(i+1));
            col = keyboard.nextInt();
            keyboard.nextLine();
            System.out.println("Seleccione el barco: \n"+"1. CARRIER tamaño:1\n"+"2. CRUISER tamaño:2 \n"
                +"3. BATTLESHIP tamaño:2\n"+"4. DESTROYER tamaño:3\n"+"5. SUBMARINE tamaño:4\n");
            shipType = keyboard.nextInt();
            Ship ship1 = gameHandler.createShip(0, row, col, shipType);
            //Crear barco, mientras el lugar indicado sea falso, vuelve a preguntar donde crearlo
            while(ship1.isPlacementOk(ship1, shipsPlayer1, boardP1) == false){
                System.out.println("Barco se sale del tablero o ya hay un barco en una de sus posiciones,"
                        + " intenta en otro lugar");
                System.out.println("seleccione fila del barco : "+(i+1));
                row = keyboard.nextInt();
                keyboard.nextLine();
                System.out.println("Seleccione columna del barco : "+(i+1));
                col = keyboard.nextInt();
                keyboard.nextLine();
                System.out.println("Seleccione el barco: \n"+"1. CARRIER \n"+"2. CRUISER \n"
                    +"3. BATTLESHIP\n"+"4. DESTROYER \n"+"5. SUBMARINE \n");
                shipType = keyboard.nextInt();
                ship1 = gameHandler.createShip(0, row, col, shipType);
            }
            int coords[] = {row,col,shipType};
            ShipandCoordinates.add(coords);
            shipsPlayer1.add(ship1);
        }
        oos.writeObject(ShipandCoordinates);
        buffer = baos.toByteArray();
        packet = new DatagramPacket(buffer, buffer.length, senders_address, senders_port);
        socket.send(packet);        
        baos.flush();
        oos.flush();
        
        System.out.println("Listo para turno de servidor");
        buffer = new byte[maxlength];
        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        ois = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
        ShipandCoordinates = (List<int[]>)ois.readObject();
        senders_address = packet.getAddress();
        senders_port = packet.getPort();        
        System.out.println("Respuesta recibida---------------------------");
        for(int[] coords : ShipandCoordinates){
            System.out.println(coords[0]+":"+coords[1]+":"+coords[2]);
        }  
               
        //Llenar tablero del Servidor
        for (int i = 0; i < nShips; i++) {
            int serv_row = ShipandCoordinates.get(i)[0];
            int serv_col = ShipandCoordinates.get(i)[1];
            int serv_shipType = ShipandCoordinates.get(i)[2];
            Ship ship1 = gameHandler.createShip(1, serv_row, serv_col, serv_shipType);
            //Crear barco, mientras el lugar indicado sea falso, vuelve a preguntar donde crearlo
            while(ship1.isPlacementOk(ship1, shipsPlayer2, boardP2) == false){
                ship1 = gameHandler.createShip(1, serv_row, serv_col, serv_shipType);
            }
            shipsPlayer2.add(ship1);
        }
        
        Player player1 = new Player(shipsPlayer1, boardP2);
        Player player2 = new Player(shipsPlayer2, boardP1);
        boolean gameOn = true;
        Display display = new Display();
        System.out.println("<<<<<<<<<<<<<<<<<<<<<Tablero SERVIDOR>>>>>>>>>>>>>>>>>");
        display.printBoard(boardP2);
        int numberOfShipsPlayer1 = player1.numberOfSquaresOfShips(shipsPlayer1);
        int numberOfShipsPlayer2 = player2.numberOfSquaresOfShips(shipsPlayer2);
        
        while(gameOn){
            System.out.println("<<<<<<TIRO DEL JUGADOR>>>>>>>");
            int[] ShootCoordinates;
            System.out.println("Seleccione fila: ");
            row = keyboard.nextInt();
            System.out.println("Seleccione columna: ");
            col = keyboard.nextInt();
            ShootCoordinates = gameHandler.shoot(0,row, col);
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(ShootCoordinates);
            buffer = baos.toByteArray();
            packet = new DatagramPacket(buffer, buffer.length, senders_address, senders_port);
            socket.send(packet);
            oos.flush();
            baos.flush();
            System.out.println("Tiro enviado al servidor en las coordenadas: ["+ShootCoordinates[0]+":"+ShootCoordinates[1]+"]");
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
            buffer = new byte[maxlength];
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            ois = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
            ShootCoordinates = (int[])ois.readObject();
            senders_address = packet.getAddress();
            senders_port = packet.getPort();
            System.out.println("Disparo recibido en las coordenadas: ["+ShootCoordinates[0]+":"+ShootCoordinates[1]+"]");
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
        

        
        
        ois.close();
        
    }
    
    public Client(){
        Display display = new Display();  
        Scanner scanner = new Scanner(System.in);
        int choice;
        boolean exit = false;
               
        while(!exit){
            display.printMainMenuOptions();
            System.out.println("Seleccione la opción");
            System.out.println();
            choice = scanner.nextInt();
            switch(choice){
                case 0: 
                    display.printMessages("Elegiste jugar");
                    try {
                        gameLogic();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                break;
                case 1: 
                    System.out.flush();
                    display.gameRules();
                break;
                case 2:
                    display.printMessages("Elegiste salir");
                    exitGame();
                break;
            }            
        }        
    }
    
    public static void main(String[] args) {
        new Client();        
    }


    
}
