import java.util.Random;

public class Zona {
    private String nome;
    private int id;
    private double lixoAcumulado;
    private double geracaoLixoMin;
    private double geracaoLixoMax;

    public Zona(String nome, int id) {
        this.nome = nome;
        this.id = id;
        this.lixoAcumulado = 0.0;
        this.geracaoLixoMin = ConfiguracaoSimulacao.GERACAO_LIXO_MIN[id];
        this.geracaoLixoMax = ConfiguracaoSimulacao.GERACAO_LIXO_MAX[id];
    }

    public void gerarLixo(int duracaoMinutos) {
        Random rand = new Random();
        double taxaMediaPorMinuto = (geracaoLixoMax + geracaoLixoMin) / (2 * 1440); // Média por minuto em um dia
        double variacao = rand.nextDouble() * (geracaoLixoMax - geracaoLixoMin) / 1440;
        double lixoGerado = (taxaMediaPorMinuto + variacao) * duracaoMinutos;
        lixoAcumulado += lixoGerado;
        if (lixoGerado > 0) {
            System.out.println("Zona " + nome + " gerou " + String.format("%.2f", lixoGerado) + " t de lixo.");
        }
    }

    public double coletarLixo(double capacidade) {
        double lixoColetado = Math.min(lixoAcumulado, capacidade);
        lixoAcumulado -= lixoColetado;
        return lixoColetado;
    }

    public String getNome() {
        return nome;
    }

    public double getLixoAcumulado() {
        return lixoAcumulado;
    }

    public int getId() {
        return id;
    }
}