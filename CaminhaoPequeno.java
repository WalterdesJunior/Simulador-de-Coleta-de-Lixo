import java.util.Random;

public class CaminhaoPequeno {
    // Identificador único do caminhão pequeno
    private final int id;
    // Capacidade máxima de carga em toneladas
    private final double capacidade;
    // Quantidade atual de carga transportada
    private double cargaAtual;
    // Tempo mínimo de viagem durante horário de pico em minutos
    private final int tempoViagemPicoMin;
    // Tempo máximo de viagem durante horário de pico em minutos
    private final int tempoViagemPicoMax;
    // Tempo mínimo de viagem fora do horário de pico em minutos
    private final int tempoViagemForaPicoMin;
    // Tempo máximo de viagem fora do horário de pico em minutos
    private final int tempoViagemForaPicoMax;
    // Número máximo de viagens permitidas por dia
    private final int maxViagensDiarias;
    // Número de viagens já realizadas
    private int viagensRealizadas;
    // Zona atual onde o caminhão está operando
    private Zona zonaAtual;
    // Indica se o caminhão está em uma estação de transferência
    private boolean naEstacaoTransferencia;
    // Tempo estimado de chegada à estação
    private int tempoChegada;
    // Referência ao simulador para acesso às zonas
    private Simulador simulador;

    // Construtor inicializando o caminhão pequeno com parâmetros
    public CaminhaoPequeno(int id, double capacidade, int tempoViagemPicoMin, int tempoViagemPicoMax,
                           int tempoViagemForaPicoMin, int tempoViagemForaPicoMax, int maxViagensDiarias, Zona zonaInicial) {
        this.id = id;
        this.capacidade = capacidade;
        this.cargaAtual = 0.0;
        this.tempoViagemPicoMin = tempoViagemPicoMin;
        this.tempoViagemPicoMax = tempoViagemPicoMax;
        this.tempoViagemForaPicoMin = tempoViagemForaPicoMin;
        this.tempoViagemForaPicoMax = tempoViagemForaPicoMax;
        this.maxViagensDiarias = maxViagensDiarias;
        this.viagensRealizadas = 0;
        this.zonaAtual = zonaInicial;
        this.naEstacaoTransferencia = false;
        this.tempoChegada = 0;
    }

    // Define a referência ao simulador
    public void setSimulador(Simulador simulador) {
        this.simulador = simulador;
    }

    // Retorna a referência ao simulador
    public Simulador getSimulador() {
        return simulador;
    }

    // Coleta lixo da zona atual, respeitando a capacidade disponível
    public void coletarLixo() {
        if (zonaAtual == null || naEstacaoTransferencia || viagensRealizadas >= maxViagensDiarias) return;
        double lixoColetado = zonaAtual.coletarLixo(capacidade - cargaAtual);
        cargaAtual += lixoColetado;
        System.out.println("Caminhão Pequeno " + id + " coletou " + lixoColetado + " t na zona " + zonaAtual.getNome());
    }

    // Descarrega a carga atual e incrementa o número de viagens
    public double descarregar() {
        double cargaDescarregada = cargaAtual;
        cargaAtual = 0.0;
        viagensRealizadas++;
        return cargaDescarregada;
    }

    // Calcula o tempo de viagem baseado no horário atual
    public int calcularTempoViagem(int tempoAtual) {
        Random rand = new Random();
        boolean horarioPico = (tempoAtual % 1440 >= 360 && tempoAtual % 1440 <= 540) || (tempoAtual % 1440 >= 1020 && tempoAtual % 1440 <= 1200);
        if (horarioPico) {
            return rand.nextInt(tempoViagemPicoMax - tempoViagemPicoMin + 1) + tempoViagemPicoMin;
        } else {
            return rand.nextInt(tempoViagemForaPicoMax - tempoViagemForaPicoMin + 1) + tempoViagemForaPicoMin;
        }
    }

    // Retorna o ID do caminhão
    public int getId() {
        return id;
    }

    // Retorna a capacidade máxima do caminhão
    public double getCapacidade() {
        return capacidade;
    }

    // Retorna a carga atual do caminhão
    public double getCargaAtual() {
        return cargaAtual;
    }

    // Retorna o número de viagens realizadas
    public int getViagensRealizadas() {
        return viagensRealizadas;
    }

    // Retorna a zona atual do caminhão
    public Zona getZonaAtual() {
        return zonaAtual;
    }

    // Define a zona atual do caminhão
    public void setZonaAtual(Zona zona) {
        this.zonaAtual = zona;
    }

    // Verifica se o caminhão está na estação de transferência
    public boolean estaNaEstacaoTransferencia() {
        return naEstacaoTransferencia;
    }

    // Define o estado do caminhão em relação à estação de transferência
    public void setNaEstacaoTransferencia(boolean naEstacao) {
        this.naEstacaoTransferencia = naEstacao;
    }

    // Retorna o tempo de chegada estimado
    public int getTempoChegada() {
        return tempoChegada;
    }

    // Define o tempo de chegada estimado
    public void setTempoChegada(int tempoChegada) {
        this.tempoChegada = tempoChegada;
    }

    // Retorna o número máximo de viagens diárias
    public int getMaxViagensDiarias() { // Novo método adicionado
        return maxViagensDiarias;
    }
}
