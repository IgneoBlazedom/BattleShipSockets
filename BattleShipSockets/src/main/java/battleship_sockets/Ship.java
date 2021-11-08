package battleship_sockets;

import java.util.List;

public class Ship {
    private List<Square> NewShip; //Lista de cuadros donde se encuentra
    private ShipType shipType;

    public Ship(List<Square> NewShip, ShipType shipType) {
        this.NewShip = NewShip;
        this.shipType = shipType;
    }

    public List<Square> getFields() {
        return NewShip;
    }

    public ShipType getShipType() {
        return shipType;
    }
    
    public void add(Square square){
        NewShip.add(square);
    }
    
    public boolean isPlacementOk(Ship ship1, List<Ship> ships, Board board){
        int count = 0;
        //Iteración por cada espacio que ocupa Ship1
        for(int i = 0; i < ship1.getFields().size(); i++){
            //Ship se encuentra fuera de los límites del Board -> count++
            if (ship1.getFields().get(i).getY() > board.getSizeY() || 
                ship1.getFields().get(i).getX() > board.getSizeX())
                  count++;           
            //Por cada Ship del arreglo ships...
            for (int k = 0; k < ships.size(); k++) {
                //Si Ship se desea poner en un espacio que esté ocupado por otra Ship --> count ++
                for (int z = 0; z < ships.get(k).getFields().size(); z++) {
                    if(ship1.getFields().get(i).getX() == ships.get(k).getFields().get(z).getX()&&
                       ship1.getFields().get(i).getY() == ships.get(k).getFields().get(z).getY())
                        count++;
                }
            }            
        }//for
        if(count == 0)
            return true;
        else
            return false;
    }
    
}
