public class CaminhaoGrande {
    // Identificador único do caminhão grande
    private final int id;
    // Capacidade máxima de carga em toneladas
    private final double capacidade;
    // Quantidade atual de carga transportada
    private double cargaAtual;
    // Tempo atual de espera em minutos
    private int tempoEsperaAtual;

    // Construtor inicializando o caminhão grande com ID, capacidade e tempo de espera inicial
    public CaminhaoGrande(int id, double capacidade, int tempoEsperaInicial) {
        this.id = id;
        this.capacidade = capacidade;
        this.cargaAtual = 0.0;
        this.tempoEsperaAtual = tempoEsperaInicial; // Inicializa com 0 ou outro valor padrão
    }

    // Adiciona carga ao caminhão, respeitando a capacidade máxima
    public void adicionarCarga(double carga) {
        this.cargaAtual = Math.min(capacidade, cargaAtual + carga);
    }

    // Retorna a carga atual do caminhão
    public double getCargaAtual() {
        return cargaAtual;
    }

    // Retorna o ID do caminhão
    public int getId() {
        return id;
    }

    // Retorna a capacidade máxima do caminhão
    public double getCapacidade() {
        return capacidade;
    }

    // Reseta a carga atual para zero
    public void resetCarga() {
        this.cargaAtual = 0.0;
    }
}
