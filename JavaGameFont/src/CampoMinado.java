import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CampoMinado extends JFrame {


    private static int SIZE;
    private static int MINES;
    private JButton[][] buttons;
    private boolean[][] mines;
    private int[][] neighbors;
    private boolean[][] flagged;

    private final JLabel timeLabel;
    private Timer timer;
    private double elapsedTime;
    private boolean isTimerRunning;


    static {
        // Lê o tamanho do tabuleiro e a quantidade de minas a partir de um arquivo txt
        try {
            File lerDados = new File("game.txt");
            BufferedReader reader = new BufferedReader(new FileReader(lerDados));
            SIZE = Integer.parseInt(reader.readLine().trim());
            MINES = Integer.parseInt(reader.readLine().trim());
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CampoMinado() {
        setTitle("Campo Minado");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel para o contador de tempo
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        timeLabel = new JLabel("Time: 0");
        topPanel.add(timeLabel);
        add(topPanel, BorderLayout.NORTH);

        // Painel para o tabuleiro do jogo
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(SIZE, SIZE));
        add(boardPanel, BorderLayout.CENTER);

        initializeBoard(boardPanel);
    }

    private void initializeBoard(JPanel boardPanel) {
        buttons = new JButton[SIZE][SIZE];
        mines = new boolean[SIZE][SIZE];
        neighbors = new int[SIZE][SIZE];
        flagged = new boolean[SIZE][SIZE];
        isTimerRunning = false;

        // Inicializar botões
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setMargin(new Insets(0, 0, 0, 0));
                buttons[row][col].addActionListener(new ButtonClickListener(row, col)); //Adiciona para abrir os quadrados
                buttons[row][col].addMouseListener(new FlagListener(row, col)); // Adiciona para marcar as bombas
                boardPanel.add(buttons[row][col]); //Adiciona o quadrado para clicar nas bombas
            }
        }

        // Distribuir minas aleatoriamente
        distribuiMinasAleatoriamente();
    }

    private void distribuiMinasAleatoriamente() {
        mines = new boolean[SIZE][SIZE];
        neighbors = new int[SIZE][SIZE];
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


    private void initializeTimer() {
        timer = new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime++;
                timeLabel.setText("Time: " + String.format("%.2f", elapsedTime/1000));
            }
        });
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
            // Inicia o tempo quando começa o jogo
            if (!isTimerRunning) {
                initializeTimer();
                timer.start();
                isTimerRunning = true;
            }

            if (flagged[row][col]) {
                return; // Não fazer nada se a célula estiver marcada
            }

            // Faz com que a primeira caixa aberta seja 0 e não tenha bomba
            if ((mines[row][col] || neighbors[row][col] != 0) && countOpenedSquares() == 0) {
                do {
                    distribuiMinasAleatoriamente();
                } while (mines[row][col] || neighbors[row][col] != 0);
            }

            if (mines[row][col]) { //Jogador pisou em uma mina
                buttons[row][col].setText("Mine");
                buttons[row][col].setBackground(Color.RED);
                timer.stop();
                JOptionPane.showMessageDialog(null, "Game Over!");
                try {
                    showScoreboard(); //Mostra a scoreboard
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);
            } else { //Jogador acertou o quadrado
                if (neighbors[row][col] == 0) {
                    buttons[row][col].setText("");
                } else {
                    buttons[row][col].setText(String.valueOf(neighbors[row][col]));
                }
                buttons[row][col].setEnabled(false);

                // Abre as minas em volta se o valor da mina for 0
                openNeighbourMine(row, col);
                // Verifica se o jogador colocou todas as minas no lugar certo para testar vitória
                if (countOpenedSquares() >= SIZE * SIZE - MINES) { //Jogador venceu o jogo
                    timer.stop();
                    JOptionPane.showMessageDialog(null, "Você ganhou! " + elapsedTime/1000 + " segundos");
                    String nomeJogador = JOptionPane.showInputDialog("Digite seu nome:");

                    //Exporta para um txt
                    try {
                        File fileWrite = new File("resultados.txt");
                        BufferedWriter writer = new BufferedWriter(new FileWriter(fileWrite, StandardCharsets.UTF_8, true));
                        writer.write("\n");
                        writer.write("Mina = " + MINES + " SIZE = " + SIZE + "\n");
                        writer.write(nomeJogador + ": " + elapsedTime / 1000 + "\n");
                        writer.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    try {
                        showScoreboard();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }



                    System.exit(0);
                }
            }
        }

        public void showScoreboard() throws IOException {

            File fileRead = new File("resultados.txt");
            BufferedReader reader = new BufferedReader(new FileReader(fileRead));
            String line;
            StringBuilder scoresheet;
            scoresheet = new StringBuilder();
            while((line = reader.readLine()) != null){
                scoresheet.append(line);
                scoresheet.append('\n');
            }
            JOptionPane.showMessageDialog(null, scoresheet.toString());
            reader.close();


        }

        public void openNeighbourMine(int row, int col) {
            // Create a set to track visited cells
            Set<String> visited = new HashSet<>();
            openNeighbourMine(row, col, visited);
        }

        private void openNeighbourMine(int row, int col, Set<String> visited) {
            // Abre as minas em volta se o valor da mina for 0
            if (neighbors[row][col] == 0) {
                try {
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int newRow = row + i;
                            int newCol = col + j;
                            // Verifica se os valores existem dentro do tabuleiro
                            if (newRow >= 0 && newRow < buttons.length && newCol >= 0 && newCol < buttons[0].length) {
                                String cellKey = newRow + "," + newCol;
                                if (!visited.contains(cellKey)) {
                                    if (neighbors[newRow][newCol] == 0) {
                                        buttons[newRow][newCol].setText("");
                                    } else {
                                        buttons[newRow][newCol].setText(String.valueOf(neighbors[newRow][newCol]));
                                    }
                                    buttons[newRow][newCol].setEnabled(false);
                                    visited.add(cellKey); // Coloca a célula como vista
                                    openNeighbourMine(newRow, newCol, visited);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Coda uma saída de erro caso o código não funcione
                }
            }
        }

        public int countOpenedSquares() {
            int openedCount = 0;
            for (JButton[] button : buttons) {
                for (JButton jButton : button) {
                    if (!jButton.isEnabled()) {
                        openedCount++;
                    }
                }
            }
            return openedCount;
        }
    }

    private class FlagListener extends MouseAdapter {
        private final int row;
        private final int col;

        public FlagListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e) && buttons[row][col].isEnabled()) {
                if (!flagged[row][col]) {
                    buttons[row][col].setText("F");
                    flagged[row][col] = true;
                } else {
                    buttons[row][col].setText("");
                    flagged[row][col] = false;
                }
            }
        }
    }
}
