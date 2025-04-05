import com.dyma.game.GuessGame;

import java.util.Random;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final Random rondom = new Random();
        final String[] words = "Python Java JavaScript SpriongBoot NodeJs React".split(" ");
        String wordToGuss = words[rondom.nextInt(words.length)];
        var game = new GuessGame(wordToGuss, 10);
        while (true) {
            System.out.println(game);
            System.out.println("Entrez une lettre");
            final var letter = scanner.nextLine();
            if (letter.length() == 1) {
                game.guessLetter(letter.charAt(0));
                if (game.isWin()) {
                    System.out.println(game);
                    System.out.println("Gagné !");
                    System.out.println("Réessaie, oui ou non ");
                    final var playAgain = scanner.nextLine().charAt(0);
                     wordToGuss = words[rondom.nextInt(words.length)];
                    game = new GuessGame(wordToGuss, 10);
                    if (playAgain != 'y') {
                        break;
                    }
                }
                if (game.isLoss()) {
                    System.out.println(game);
                    System.out.println("Pérdu !");
                    System.out.println("Try again yes or No !");
                    final var playAgain = scanner.nextLine().charAt(0);
                    wordToGuss = words[rondom.nextInt(words.length)];
                    game = new GuessGame(wordToGuss, 10);
                    if (playAgain != 'y') {
                        break;
                    }
                }
            } else {
                System.out.println("entrer une seule lettre");
            }

        }
    }
}