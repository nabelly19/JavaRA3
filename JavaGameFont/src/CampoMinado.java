import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CampoMinado extends JFrame {
    private static final int SIZE = 13;
    private static final int MINES = 10;
    private JButton[][] buttons;
    private boolean[][] mines;
    private int[][] neighbors;

    public CampoMinado() {
        setTitle("Campo Minado");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(SIZE, SIZE));
        initializeBoard();
    }

    private void initializeBoard() {
        buttons = new JButton[SIZE][SIZE];
        mines = new boolean[SIZE][SIZE];
        neighbors = new int[SIZE][SIZE];

        // Inicializar botões
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setMargin(new Insets(0, 0, 0, 0));
                buttons[row][col].addActionListener(new ButtonClickListener(row, col));
                add(buttons[row][col]);
            }
        }

        // Distribuir minas aleatoriamente
        Random random = new Random();
        int placedMines = 0;
        while (placedMines < MINES) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            if (!mines[row][col]) {
                mines[row][col] = true;
                placedMines++;
            }
        }

        // Calcular números vizinhos
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (!mines[row][col]) {
                    neighbors[row][col] = countNeighboringMines(row, col);
                }
            }
        }
    }

    private int countNeighboringMines(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int r = row + i;
                int c = col + j;
                if (r >= 0 && r < SIZE && c >= 0 && c < SIZE && mines[r][c]) {
                    count++;
                }
            }
        }
        return count;
    }

    private class ButtonClickListener implements ActionListener {
        private final int row;
        private final int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (mines[row][col]) {
                buttons[row][col].setText("M");
                buttons[row][col].setBackground(Color.RED);
                JOptionPane.showMessageDialog(null, "Game Over!");
                System.exit(0);
            } else {
                buttons[row][col].setText(String.valueOf(neighbors[row][col]));
                buttons[row][col].setEnabled(false);

                //Abre as minas em volta se o valor da mina for 0
                openNeighbourMine(row, col);
                //Verifica se o jogador colocou todas as minas no lugar certo para testar vitória
                if(countOpenedSquares() >= SIZE * SIZE - MINES){
                    JOptionPane.showMessageDialog(null, "Você ganhou");
                    System.exit(0);
                }

            }
        }

        public void openNeighbourMine(int row, int col) {
            // Create a set to track visited cells
            Set<String> visited = new HashSet<>();
            openNeighbourMine(row, col, visited);
        }

        private void openNeighbourMine(int row, int col, Set<String> visited) {
            // Abre as minas em volta se o valor da mina for 0
            if (countNeighboringMines(row, col) == 0) {
                try {
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int newRow = row + i;
                            int newCol = col + j;
                            // Verifica se os valores existem dentro do tabuleiro
                            if (newRow >= 0 && newRow < buttons.length && newCol >= 0 && newCol < buttons[0].length) {
                                String cellKey = newRow + "," + newCol;
                                if (!visited.contains(cellKey)) {
                                    buttons[newRow][newCol].setText(String.valueOf(neighbors[newRow][newCol]));
                                    buttons[newRow][newCol].setEnabled(false);
                                    visited.add(cellKey); // Mark cell as visited
                                    openNeighbourMine(newRow, newCol, visited);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    //Coda uma saída de erro caso ocódigo não funcione
                }
            }
        }

        public int countOpenedSquares() {
            int openedCount = 0;
            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons[i].length; j++) {
                    if (!buttons[i][j].isEnabled()) {
                        openedCount++;
                    }
                }
            }
            return openedCount;
        }
    }
}


