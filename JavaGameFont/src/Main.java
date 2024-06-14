import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws CampoMinadoException, JogadorException{

        // Cria o arquivo txt com as configurações do game, se estiver vazio
        try {
            File fileWrite = new File("game.txt");

            // Verifica se o arquivo existe e se está vazio
            if (!fileWrite.exists() || fileWrite.length() == 0) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileWrite));
                writer.write("10\n10");
                writer.close();
                System.out.println("Dados escritos no arquivo.");
            } else {
                System.out.println("O arquivo não está vazio. Nenhum dado foi escrito.");
            }
        } catch (IOException ex) {
            throw new CampoMinadoException("Erro ao manipular arquivo", ex);
        } catch (NumberFormatException  ex) {
            throw new JogadorException("Erro ao manipular arquivo", ex);
        }

        // Inicia a interface gráfica do jogo
        SwingUtilities.invokeLater(() -> {
            CampoMinado frame = new CampoMinado();
            frame.setVisible(true);
        });
    }
}
