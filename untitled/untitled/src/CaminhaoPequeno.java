import java.util.Random;

public class CaminhaoPequeno {
    private final int id;
    private final double capacidade;
    private double cargaAtual;
    private final int tempoViagemPicoMin;
    private final int tempoViagemPicoMax;
    private final int tempoViagemForaPicoMin;
    private final int tempoViagemForaPicoMax;
    private final int maxViagensDiarias;
    private int viagensRealizadas;
    private Zona zonaAtual;
    private final Zona zonaFixa;
    private boolean naEstacaoTransferencia;
    private int tempoChegada;
    private Simulador simulador;

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
        this.zonaFixa = zonaInicial;
        this.naEstacaoTransferencia = false;
        this.tempoChegada = 0;
    }

    public void setSimulador(Simulador simulador) {
        this.simulador = simulador;
    }

    public Simulador getSimulador() {
        return simulador;
    }

    public void coletarLixo() {
        if (zonaFixa == null || naEstacaoTransferencia) return;
        double lixoColetado = zonaFixa.coletarLixo(capacidade - cargaAtual);
        cargaAtual += lixoColetado;
        System.out.println("Caminhão Pequeno " + id + " coletou " + lixoColetado + " t na zona " + zonaFixa.getNome());
    }

    public double descarregar() {
        double cargaDescarregada = cargaAtual;
        cargaAtual = 0.0;
        viagensRealizadas++;
        return cargaDescarregada;
    }

    public int calcularTempoViagem(int tempoAtual) {
        Random rand = new Random();
        boolean horarioPico = (tempoAtual % 1440 >= 360 && tempoAtual % 1440 <= 540) || (tempoAtual % 1440 >= 1020 && tempoAtual % 1440 <= 1200);
        if (horarioPico) {
            return rand.nextInt(tempoViagemPicoMax - tempoViagemPicoMin + 1) + tempoViagemPicoMin;
        } else {
            return rand.nextInt(tempoViagemForaPicoMax - tempoViagemForaPicoMin + 1) + tempoViagemForaPicoMin;
        }
    }

    public int getId() {
        return id;
    }

    public double getCapacidade() {
        return capacidade;
    }

    public double getCargaAtual() {
        return cargaAtual;
    }

    public int getViagensRealizadas() {
        return viagensRealizadas;
    }

    public Zona getZonaAtual() {
        return zonaAtual;
    }

    public void setZonaAtual(Zona zona) {
        this.zonaAtual = zona;
    }

    public boolean estaNaEstacaoTransferencia() {
        return naEstacaoTransferencia;
    }

    public void setNaEstacaoTransferencia(boolean naEstacao) {
        this.naEstacaoTransferencia = naEstacao;
    }

    public int getTempoChegada() {
        return tempoChegada;
    }

    public void setTempoChegada(int tempoChegada) {
        this.tempoChegada = tempoChegada;
    }

    public int getMaxViagensDiarias() {
        return maxViagensDiarias;
    }

    public Zona getZonaFixa() {
        return zonaFixa;
    }
} 