import java.util.Random;

public class Simulador {
    private final ListaEncadeada<Zona> zonas;
    private final ListaEncadeada<CaminhaoPequeno> caminhoesPequenos;
    private final ListaEncadeada<EstacaoTransferencia> estacoesTransferencia;
    private final ListaEncadeada<Double> lixoParaAterro;
    private final ListaEncadeada<Integer> temposDeEspera;
    private int tempoSimulacao;
    private int caminhoesGrandesUsados;
    private boolean visualizacaoIniciada = false;
    private volatile boolean simulacaoAtiva = false;
    private volatile boolean simulacaoConcluida = false;
    private double lixoGeradoTotal;
    private double lixoAcumulado;

    public Simulador() {
        zonas = new ListaEncadeada<>();
        caminhoesPequenos = new ListaEncadeada<>();
        estacoesTransferencia = new ListaEncadeada<>();
        lixoParaAterro = new ListaEncadeada<>();
        temposDeEspera = new ListaEncadeada<>();
        tempoSimulacao = 0;
        caminhoesGrandesUsados = ConfiguracaoSimulacao.NUMERO_CAMINHOES_GRANDES_INICIAL;
        lixoGeradoTotal = 0.0;
        lixoAcumulado = 0.0;
    }

    public void inicializar() {
        synchronized (zonas) {
            String[] nomesZonas = {"Sul", "Norte", "Centro", "Leste", "Sudeste"};
            zonas.limpar();
            for (int i = 0; i < nomesZonas.length; i++) {
                zonas.adicionar(new Zona(nomesZonas[i], i));
            }
        }

        synchronized (caminhoesPequenos) {
            Random rand = new Random();
            caminhoesPequenos.limpar();
            int numeroCaminhoes = Math.min(ConfiguracaoSimulacao.NUMERO_CAMINHOES_PEQUENOS, 10); // Limita a 10
            for (int i = 0; i < numeroCaminhoes; i++) {
                double capacidade = ConfiguracaoSimulacao.CAPACIDADES_CAMINHOES_PEQUENOS[rand.nextInt(4)];
                Zona zonaInicial;
                synchronized (zonas) {
                    zonaInicial = zonas.obter(rand.nextInt(zonas.tamanho()));
                }
                caminhoesPequenos.adicionar(new CaminhaoPequeno(
                        i + 1, capacidade,
                        ConfiguracaoSimulacao.TEMPO_VIAGEM_PICO_MIN, ConfiguracaoSimulacao.TEMPO_VIAGEM_PICO_MAX,
                        ConfiguracaoSimulacao.TEMPO_VIAGEM_FORA_PICO_MIN, ConfiguracaoSimulacao.TEMPO_VIAGEM_FORA_PICO_MAX,
                        ConfiguracaoSimulacao.MAX_VIAGENS_DIARIAS, zonaInicial
                ));
            }
        }

        synchronized (estacoesTransferencia) {
            estacoesTransferencia.limpar();
            for (int i = 0; i < ConfiguracaoSimulacao.NUMERO_ESTACOES_TRANSFERENCIA; i++) {
                estacoesTransferencia.adicionar(new EstacaoTransferencia(
                        i + 1, ConfiguracaoSimulacao.TEMPO_MAX_ESPERA_CAMINHAO_PEQUENO,
                        ConfiguracaoSimulacao.CAPACIDADE_CAMINHAO_GRANDE, ConfiguracaoSimulacao.TOLERANCIA_ESPERA_CAMINHAO_GRANDE
                ));
            }
        }

        lixoGeradoTotal = 0.0;
        lixoAcumulado = 0.0;
        caminhoesGrandesUsados = ConfiguracaoSimulacao.NUMERO_CAMINHOES_GRANDES_INICIAL;
        simulacaoConcluida = false;
    }

    public void setSimulacaoAtiva(boolean ativa) {
        this.simulacaoAtiva = ativa;
    }

    public boolean isSimulacaoConcluida() {
        return simulacaoConcluida;
    }

    public void executarSimulacao(int duracao) {
        if (!visualizacaoIniciada) {
            VisualizacaoSimulacaoSwing.iniciar(this);
            visualizacaoIniciada = true;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        final int duracaoFinal = duracao;
        Thread simulacaoThread = new Thread(() -> {
            executarSimulacaoInterna(duracaoFinal);
        });
        simulacaoThread.start();
    }

    private void executarSimulacaoInterna(int duracao) {
        while (tempoSimulacao < duracao) {
            if (!simulacaoAtiva) {
                try {
                    Thread.sleep(100);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (tempoSimulacao % 60 == 0) {
                System.out.println("Tempo: " + tempoSimulacao + " minutos (" + (tempoSimulacao / 60) + " horas)");
            }

            synchronized (zonas) {
                for (int i = 0; i < zonas.tamanho(); i++) {
                    Zona zona = zonas.obter(i);
                    if (zona != null) {
                        double lixoAntes = zona.getLixoAcumulado();
                        zona.gerarLixo(1);
                        double lixoDepois = zona.getLixoAcumulado();
                        lixoGeradoTotal += (lixoDepois - lixoAntes);
                        if (tempoSimulacao % 60 == 0) {
                            System.out.println("Zona " + zona.getNome() + ": " + String.format("%.2f", zona.getLixoAcumulado()) + " t acumuladas");
                        }
                    }
                }
            }

            synchronized (caminhoesPequenos) {
                for (int i = 0; i < caminhoesPequenos.tamanho(); i++) {
                    CaminhaoPequeno caminhao = caminhoesPequenos.obter(i);
                    if (caminhao != null && !caminhao.estaNaEstacaoTransferencia()) {
                        caminhao.coletarLixo();
                        if (caminhao.getCargaAtual() >= caminhao.getCapacidade() * 0.8) {
                            EstacaoTransferencia estacao;
                            synchronized (estacoesTransferencia) {
                                estacao = estacoesTransferencia.obter(new Random().nextInt(estacoesTransferencia.tamanho()));
                            }
                            if (estacao != null) {
                                int tempoViagem = caminhao.calcularTempoViagem(tempoSimulacao);
                                estacao.adicionarCaminhaoPequeno(caminhao, tempoSimulacao + tempoViagem);
                                System.out.println("Caminhão Pequeno " + caminhao.getId() + " a caminho da Estação " + estacao.getId() + " (chegada em " + (tempoSimulacao + tempoViagem) + " min)");
                            }
                        }
                    }
                }
            }

            synchronized (estacoesTransferencia) {
                for (int i = 0; i < estacoesTransferencia.tamanho(); i++) {
                    EstacaoTransferencia estacao = estacoesTransferencia.obter(i);
                    if (estacao != null) {
                        if (estacao.processar(tempoSimulacao, lixoParaAterro)) {
                            caminhoesGrandesUsados++;
                            System.out.println("Novo Caminhão Grande na Estação " + estacao.getId() + ". Total: " + caminhoesGrandesUsados);
                        }
                        for (int j = 0; j < estacao.getTemposEspera().tamanho(); j++) {
                            Integer tempo = estacao.getTemposEspera().obter(j);
                            if (tempo != null) {
                                synchronized (temposDeEspera) {
                                    temposDeEspera.adicionar(tempo);
                                }
                            }
                        }
                    }
                }
            }

            tempoSimulacao++;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (tempoSimulacao >= duracao) {
                System.out.println("Simulação concluída no tempo: " + tempoSimulacao + " minutos");
                simulacaoConcluida = true;
                break;
            }
        }
        atualizarLixoAcumulado();
        exibirEstatisticas();
    }

    private void atualizarLixoAcumulado() {
        lixoAcumulado = 0.0;
        synchronized (zonas) {
            for (int i = 0; i < zonas.tamanho(); i++) {
                Zona zona = zonas.obter(i);
                if (zona != null) {
                    lixoAcumulado += zona.getLixoAcumulado();
                }
            }
        }
        synchronized (estacoesTransferencia) {
            for (int i = 0; i < estacoesTransferencia.tamanho(); i++) {
                EstacaoTransferencia estacao = estacoesTransferencia.obter(i);
                if (estacao != null) {
                    lixoAcumulado += estacao.getLixoArmazenado();
                }
            }
        }
    }

    public int calcularMinimoCaminhoesGrandes(int duracao) {
        int caminhoesNecessarios = 1;
        double lixoColetado;

        do {
            ConfiguracaoSimulacao.NUMERO_CAMINHOES_GRANDES_INICIAL = caminhoesNecessarios;
            inicializar();
            simulacaoAtiva = true;
            executarSimulacaoInterna(duracao);

            lixoColetado = getLixoTotalColetado();

            if (lixoColetado < lixoGeradoTotal) {
                caminhoesNecessarios++;
                System.out.println("Lixo não atendido com " + (caminhoesNecessarios - 1) + " caminhões grandes. Tentando com " + caminhoesNecessarios + "...");
            }
        } while (lixoColetado < lixoGeradoTotal);

        System.out.println("Número mínimo de caminhões grandes necessários: " + caminhoesNecessarios);
        return caminhoesNecessarios;
    }

    public void exibirEstatisticas() {
        double lixoTotal = getLixoTotalColetado();
        double tempoMedioEspera = getTempoMedioEspera();

        System.out.println("Estatísticas Finais:");
        System.out.println("Lixo Total Gerado: " + String.format("%.2f", lixoGeradoTotal) + " t");
        System.out.println("Lixo Total Coletado: " + String.format("%.2f", lixoTotal) + " t");
        System.out.println("Lixo Acumulado (não coletado): " + String.format("%.2f", lixoAcumulado) + " t");
        System.out.println("Tempo Médio de Espera: " + String.format("%.2f", tempoMedioEspera) + " min");
        System.out.println("Caminhões Grandes Usados: " + caminhoesGrandesUsados);
    }

    public double getTempoMedioEspera() {
        synchronized (temposDeEspera) {
            if (temposDeEspera == null || temposDeEspera.estaVazia()) {
                return 0.0;
            }

            int soma = 0;
            int count = 0;
            for (int i = 0; i < temposDeEspera.tamanho(); i++) {
                Integer tempo = temposDeEspera.obter(i);
                if (tempo != null) {
                    soma += tempo;
                    count++;
                }
            }

            return count > 0 ? (double) soma / count : 0.0;
        }
    }

    public double getLixoTotalColetado() {
        synchronized (lixoParaAterro) {
            if (lixoParaAterro == null) {
                return 0.0;
            }

            double total = 0.0;
            for (int i = 0; i < lixoParaAterro.tamanho(); i++) {
                Double lixo = lixoParaAterro.obter(i);
                if (lixo != null) {
                    total += lixo;
                }
            }

            return total;
        }
    }

    public double getLixoGeradoTotal() {
        return lixoGeradoTotal;
    }

    public double getLixoAcumulado() {
        return lixoAcumulado;
    }

    public int getCaminhoesGrandesNecessarios() {
        return caminhoesGrandesUsados;
    }

    public int getTempoSimulacao() {
        return tempoSimulacao;
    }

    public ListaEncadeada<Zona> getZonas() {
        return zonas;
    }

    public ListaEncadeada<CaminhaoPequeno> getCaminhoesPequenos() {
        return caminhoesPequenos;
    }

    public ListaEncadeada<EstacaoTransferencia> getEstacoesTransferencia() {
        return estacoesTransferencia;
    }

    public EstacaoTransferencia getEstacaoDoCaminhao(CaminhaoPequeno caminhao) {
        if (caminhao == null) return null;
        synchronized (estacoesTransferencia) {
            for (int i = 0; i < estacoesTransferencia.tamanho(); i++) {
                EstacaoTransferencia estacao = estacoesTransferencia.obter(i);
                if (estacao == null) continue;
                Fila<CaminhaoPequeno> fila = estacao.getFilaCaminhoesPequenos();
                if (fila != null) {
                    int tamanhoFila = fila.tamanho();
                    Fila<CaminhaoPequeno> filaTemp = new Fila<>();
                    for (int j = 0; j < tamanhoFila; j++) {
                        CaminhaoPequeno c = fila.desenfileirar();
                        filaTemp.enfileirar(c);
                        if (c != null && c.equals(caminhao)) {
                            while (!filaTemp.estaVazia()) fila.enfileirar(filaTemp.desenfileirar());
                            return estacao;
                        }
                    }
                    while (!filaTemp.estaVazia()) fila.enfileirar(filaTemp.desenfileirar());
                }
            }
        }
        return null;
    }
}