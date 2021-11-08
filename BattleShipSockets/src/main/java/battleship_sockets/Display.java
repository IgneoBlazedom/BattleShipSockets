package battleship_sockets;


public class Display {
   
    public Display(){        
        
    }
    
    public void printMenu(){
        System.out.println("El juego está iniciando.............");
        System.out.println("\n");
    }
    
    public void printMessages(String message){
        System.out.println(message);
    }
    
    public void printMainMenuOptions(){
        System.out.println("Presione: \n"
                + "\t 0 - Jugar \n"
                + "\t 1 - Ver instrucciones\n"
                + "\t 2 - Salir del juego");
    }
    
    public void printExitMessage(){
        System.out.println("Gracias por jugar!!");
    }
    
    public void gameRules(){
        System.out.println("NO DISPONIBLE POR EL MOMENTO");
    }
    
    public void printBoard(Board board){
        System.out.print("    ");
        for (int i = 0; i < board.getSizeX(); i++) {
            System.out.print("  "+i+"  "); //columnas
        }
        System.out.println();
        for (int row = 0; row < board.getSizeX(); row++) {
            if(row < 10){
                System.out.print(row+"   ");
            } else {
                System.out.print(row);
            }
            for (int col = 0; col < board.getSizeY(); col++) {
                switch(board.getSquare(row, col).getCharacter()){
                    case 'O':
                        System.out.print("| ? |");
                    break;
                    case 'H':
                        System.out.print("| X |");
                    break;
                    case 'S':
                        System.out.print("| S |");
                    break;
                    case 'M':
                        System.out.print("| M |");
                    break;
                    case 'E':
                        System.out.print("| E |");
                    break;
                }                    
            }
            System.out.println();
        }
        System.out.println();
    }
        
}
