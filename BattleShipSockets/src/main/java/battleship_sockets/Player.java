package battleship_sockets;

import java.util.List;

public class Player {
    private List<Ship> ships;
    private Board board;
    int remainingShips = 0;

    public Player(List<Ship> ships, Board board) {
        this.ships = ships;
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public int numberOfSquaresOfShips(List <Ship> ships){
        int sumOfAllSquares = 0;
        for(Ship ship : ships){
            sumOfAllSquares += ship.getShipType().label;
        }
        return sumOfAllSquares;
    }
    
    public boolean handleShot(int x, int y){
        //por cada Ship en ships
        for(Ship ship: ships){
            //Por cada espacio de Ship
            for(Square square : ship.getFields()){
                //si golpea a una nave
                if(square.getX() == x && square.getY() == y && square.getSquareStatus().equals(SquareStatus.SHIP)){
                    square.setSquareStatus(SquareStatus.HIT);
                    board.getSquare(x, y).setSquareStatus(SquareStatus.HIT);
                    System.out.println("DISTE EN EL BLANCO!!!!");
                    return true;
                }
                //si ya había golpeado en esa celda 
                else if(square.getX() == x && square.getY() == y && square.getSquareStatus().equals(SquareStatus.HIT)){
                    square.setSquareStatus(SquareStatus.HIT);
                    board.getSquare(x, y).setSquareStatus(SquareStatus.HIT);
                    System.out.println("Ya habías dado al blanco aquí");
                    return false;
                }
            }
        }
        board.getSquare(x, y).setSquareStatus(SquareStatus.MISSED);
        System.out.println("FALLASTE!!!!!");
        return false;
    }
    
    
}
