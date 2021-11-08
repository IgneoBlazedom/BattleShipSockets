package battleship_sockets;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Input {
    private Scanner scanner = new Scanner(System.in);
    private List<Board> boards = new ArrayList<>();
    int choice;
    List<Integer> CoordinatesAndShipType = new ArrayList<>();
    
    public List<Board> getBoards(){
        generateBoard(10,10);
        return boards;
    }
    
    public void generateBoard(){
        System.out.println("Seleccione el ancho: ");
        int x = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Seleccione la altura: ");
        int y = scanner.nextInt();        
        Board board1 = new Board(x,y);
        Board board2 = new Board(x,y);
        boards.add(board1);
        boards.add(board2);
    }
    
    public void generateBoard(int x, int y){      
        Board board1 = new Board(x,y);
        Board board2 = new Board(x,y);
        boards.add(board1);
        boards.add(board2);
    }
    
    public int getIntegerMenuOption(){
        choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }
    
    //Client
    private List<Integer> AskCoordinatesForShipType(){
        this.CoordinatesAndShipType = new ArrayList<>();
        System.out.println("seleccione fila: ");
        int row = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Seleccione columna: ");
        int col = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Seleccione el barco: \n"+"1. CARRIER \n"+"2. CRUISER \n"
            +"3. BATTLESHIP\n"+"4. DESTROYER \n"+"5. SUBMARINE \n");
        int shipType = scanner.nextInt();
        CoordinatesAndShipType.add(row);
        CoordinatesAndShipType.add(col);
        CoordinatesAndShipType.add(shipType);
        return CoordinatesAndShipType;
    }
    
    //cliente
    public Ship createShip(int player){
        int GamePlayer = player + 1;
        Square shipPart;
        Ship ship;
        System.out.println("Jugador " + GamePlayer + " posicione su barco: ");
        CoordinatesAndShipType = AskCoordinatesForShipType();
        int row = CoordinatesAndShipType.get(0);
        int col = CoordinatesAndShipType.get(1);
        int shipType = CoordinatesAndShipType.get(2);
        shipPart = new Square(row, col, SquareStatus.SHIP);
        ship = new Ship(new ArrayList<>(), ShipType.values()[shipType - 1]);
        boards.get(player).buildShip(shipPart, ship);
        return ship;
    }
    
    //Servidor
    public Ship createShip(int player, int row, int col, int shipType){
        int GamePlayer = player + 1;
        Square shipPart;
        Ship ship;
        System.out.println("Jugador " + GamePlayer + " posicionando su barco.... ");
        shipPart = new Square(row, col, SquareStatus.SHIP);
        ship = new Ship(new ArrayList<>(), ShipType.values()[shipType - 1]);
        boards.get(player).buildShip(shipPart, ship);
        return ship;
    }
    
    //Cliente
    public int[] shoot(int player){
        int GamePlayer = player + 1;
        System.out.println("Jugador " + GamePlayer + " dispara");
        System.out.println("Seleccione fila: ");
        int row = scanner.nextInt();
        System.out.println("Seleccione columna: ");
        int col = scanner.nextInt();
        return new int[]{row,col};
    }
    
    //Servidor
    public int[] shoot(int player, int row, int col){
        int GamePlayer = player + 1;
        System.out.println("Jugador " + GamePlayer + " dispara");
        return new int[]{row,col};
    }
}
