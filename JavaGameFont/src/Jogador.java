import java.io.Serializable;

public class Jogador implements Serializable {
    private String nome;
    private double score; // Tempo em segundos

    public Jogador(String nome, double score) {
        this.nome = nome;
        this.score = score;
    }

    public String getNome() {
        return nome;
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "Jogador{" +
                "nome='" + nome + '\'' +
                ", score=" + score +
                '}';
    }
}
