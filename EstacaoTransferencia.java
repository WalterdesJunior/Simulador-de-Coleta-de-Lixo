import java.util.Random;

public class EstacaoTransferencia {
    private final int id;
    private final int tempoMaxEsperaCaminhaoPequeno;
    private final int capacidadeCaminhaoGrande;
    private final int toleranciaEsperaCaminhaoGrande;
    private CaminhaoGrande caminhaoGrandeAtual;
    private Fila<CaminhaoPequeno> filaCaminhoesPequenos;
    private ListaEncadeada<Integer> temposEspera;
    private double lixoArmazenado;
    private int tempoEsperaAtualGrande;
    private boolean precisaNovoCaminhaoGrande;

    public EstacaoTransferencia(int id, int tempoMaxEsperaCaminhaoPequeno, int capacidadeCaminhaoGrande, int toleranciaEsperaCaminhaoGrande) {
        this.id = id;
        this.tempoMaxEsperaCaminhaoPequeno = tempoMaxEsperaCaminhaoPequeno;
        this.capacidadeCaminhaoGrande = capacidadeCaminhaoGrande;
        this.toleranciaEsperaCaminhaoGrande = toleranciaEsperaCaminhaoGrande;
        this.caminhaoGrandeAtual = new CaminhaoGrande(id, capacidadeCaminhaoGrande, 0);
        this.filaCaminhoesPequenos = new Fila<>();
        this.temposEspera = new ListaEncadeada<>();
        this.lixoArmazenado = 0.0;
        this.tempoEsperaAtualGrande = 0;
        this.precisaNovoCaminhaoGrande = false;
    }

    public void adicionarCaminhaoPequeno(CaminhaoPequeno caminhao, int tempoChegada) {
        caminhao.setTempoChegada(tempoChegada);
        filaCaminhoesPequenos.enfileirar(caminhao);
    }

    public boolean processar(int tempoAtual, ListaEncadeada<Double> lixoParaAterro) {
        boolean novoCaminhaoGrandeNecessario = false;

        // Verifica se o caminhão grande precisa partir
        if (caminhaoGrandeAtual != null) {
            if (tempoEsperaAtualGrande > toleranciaEsperaCaminhaoGrande) {
                if (caminhaoGrandeAtual.getCargaAtual() > 0) {
                    lixoParaAterro.adicionar(caminhaoGrandeAtual.getCargaAtual());
                    System.out.println("Caminhão Grande " + caminhaoGrandeAtual.getId() + " partiu para o aterro com " + caminhaoGrandeAtual.getCargaAtual() + " t");
                    caminhaoGrandeAtual = new CaminhaoGrande(id, capacidadeCaminhaoGrande, 0);
                    tempoEsperaAtualGrande = 0;
                    novoCaminhaoGrandeNecessario = true;
                }
            } else if (caminhaoGrandeAtual.getCargaAtual() >= capacidadeCaminhaoGrande) {
                lixoParaAterro.adicionar(caminhaoGrandeAtual.getCargaAtual());
                System.out.println("Caminhão Grande " + caminhaoGrandeAtual.getId() + " está cheio e partiu para o aterro com " + caminhaoGrandeAtual.getCargaAtual() + " t");
                caminhaoGrandeAtual = new CaminhaoGrande(id + 1, capacidadeCaminhaoGrande, 0);
                tempoEsperaAtualGrande = 0;
                novoCaminhaoGrandeNecessario = true;
            }
        }

        // Processa a fila de caminhões pequenos
        if (!filaCaminhoesPequenos.estaVazia() && caminhaoGrandeAtual != null) {
            CaminhaoPequeno caminhao = filaCaminhoesPequenos.frente(); // Agora deve funcionar com o método frente()
            if (caminhao == null) return false;

            int tempoEspera = tempoAtual - caminhao.getTempoChegada();
            if (tempoEspera >= 0) {
                temposEspera.adicionar(tempoEspera);
                caminhao = filaCaminhoesPequenos.desenfileirar();

                double lixoDescarregado = caminhao.descarregar();
                lixoArmazenado += lixoDescarregado;
                double lixoParaCaminhaoGrande = Math.min(lixoArmazenado, capacidadeCaminhaoGrande - caminhaoGrandeAtual.getCargaAtual());
                caminhaoGrandeAtual.adicionarCarga(lixoParaCaminhaoGrande);
                lixoArmazenado -= lixoParaCaminhaoGrande;

                if (tempoEspera > tempoMaxEsperaCaminhaoPequeno) {
                    precisaNovoCaminhaoGrande = true;
                    System.out.println("Tempo de espera excedido na Estação " + id + ". Novo caminhão grande será necessário.");
                }

                Random rand = new Random();
                Zona novaZona;
                synchronized (caminhao.getSimulador().getZonas()) {
                    novaZona = caminhao.getSimulador().getZonas().obter(rand.nextInt(caminhao.getSimulador().getZonas().tamanho()));
                }
                caminhao.setZonaAtual(novaZona);
                caminhao.setNaEstacaoTransferencia(false);
            }
        }

        // Incrementa o tempo de espera do caminhão grande se ele não estiver cheio
        if (caminhaoGrandeAtual != null && caminhaoGrandeAtual.getCargaAtual() < capacidadeCaminhaoGrande) {
            tempoEsperaAtualGrande++;
        }

        // Retorna true se um novo caminhão grande foi necessário
        if (precisaNovoCaminhaoGrande) {
            precisaNovoCaminhaoGrande = false;
            return true;
        }
        return novoCaminhaoGrandeNecessario;
    }

    public int getId() {
        return id;
    }

    public Fila<CaminhaoPequeno> getFilaCaminhoesPequenos() {
        return filaCaminhoesPequenos;
    }

    public ListaEncadeada<Integer> getTemposEspera() {
        return temposEspera;
    }

    public double getLixoArmazenado() {
        return lixoArmazenado;
    }

    public CaminhaoGrande getCaminhaoGrandeAtual() {
        return caminhaoGrandeAtual;
    }

    public double getTempoMedioEspera() {
        if (temposEspera == null || temposEspera.estaVazia()) return 0.0;
        double soma = 0.0;
        for (int i = 0; i < temposEspera.tamanho(); i++) {
            Integer tempo = temposEspera.obter(i);
            if (tempo != null) soma += tempo;
        }
        return soma / temposEspera.tamanho();
    }
}