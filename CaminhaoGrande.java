public class CaminhaoGrande {
    private final int id;
    private final double capacidade;
    private double cargaAtual;
    private int tempoEsperaAtual;

    public CaminhaoGrande(int id, double capacidade, int tempoEsperaInicial) {
        this.id = id;
        this.capacidade = capacidade;
        this.cargaAtual = 0.0;
        this.tempoEsperaAtual = tempoEsperaInicial; // Inicializa com 0 ou outro valor padrão
    }

    public void adicionarCarga(double carga) {
        this.cargaAtual = Math.min(capacidade, cargaAtual + carga);
    }

    public double getCargaAtual() {
        return cargaAtual;
    }

    public int getId() {
        return id;
    }

    public double getCapacidade() {
        return capacidade;
    }

    public void resetCarga() {
        this.cargaAtual = 0.0;
    }
}
