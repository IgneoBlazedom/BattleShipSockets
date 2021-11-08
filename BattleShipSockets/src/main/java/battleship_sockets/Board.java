package battleship_sockets;
//hola
public class Board {
    private int sizeX;
    private int sizeY;
    Square matrix[][];

    public Board(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        fillBoard(sizeX, sizeY);
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }
    
    public Square getSquare(int x, int y){
        return matrix[x][y];
    }
    
    public Square[][] fillBoard(int x, int y){
        matrix = new Square[sizeX][sizeY];
        for(int row = 0; row < x; row++){
            for(int col = 0; col < y; col++){
                matrix[row][col] = new Square(row, col, SquareStatus.OCEAN);
            }
        }
        return matrix;
    }
    
    public void buildShip(Square square, Ship ship){
        int x,y;
        switch(ship.getShipType().label){
            case 1: //Carrier(tamaño 1)
                square.setSquareStatus(SquareStatus.SHIP);
                ship.add(square);
            break;
            case 2: //Cruiser & Battleship(tamaño 2)
                square.setSquareStatus(SquareStatus.SHIP);
                ship.add(square);
                x = square.getX();
                y = square.getY();
                ship.add(new Square(x, y+1, SquareStatus.SHIP));
            break;
            case 3: //Destroyer(tamaño 3)
                square.setSquareStatus(SquareStatus.SHIP);
                ship.add(square);
                x = square.getX();
                y = square.getY();
                ship.add(new Square(x, y+1, SquareStatus.SHIP));
                ship.add(new Square(x, y+2, SquareStatus.SHIP));
            break;
            case 4: //Submarine (tamaño 4)
                square.setSquareStatus(SquareStatus.SHIP);
                ship.add(square);
                x = square.getX();
                y = square.getY();
                ship.add(new Square(x, y+1, SquareStatus.SHIP));
                ship.add(new Square(x, y+2, SquareStatus.SHIP));
                ship.add(new Square(x, y+3, SquareStatus.SHIP));
            break;
        }
    }
}
