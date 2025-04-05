import com.dyma.game.Player;
import com.dyma.game.TicTacToe;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
    final Scanner scanner = new Scanner(System.in);
    var game = new TicTacToe();
    var player = Player.FIRST;
    while (true){
        System.out.println(game);
        System.out.println("Please enter a number [1-9]:");
        final int playerInput = scanner.nextInt();
        game.processInput(player,playerInput);
       if(game.checkWin()){
           System.out.println(game);

           System.out.println("Player" + player+ " à gagnè la partie");
           break;
       }
        player = nexPlayer(player);
    }
    }

    private static Player nexPlayer(Player player) {
        return player.equals(Player.FIRST) ? Player.SECOND : Player.FIRST;
    }
}