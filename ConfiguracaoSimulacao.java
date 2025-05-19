public class ConfiguracaoSimulacao {
    public static final double[] CAPACIDADES_CAMINHOES_PEQUENOS = {2, 4, 8, 10}; // Fixed capacities
    public static final int CAPACIDADE_CAMINHAO_GRANDE = 20; // Fixed capacity
    public static int TEMPO_VIAGEM_PICO_MIN = 30;
    public static int TEMPO_VIAGEM_PICO_MAX = 60;
    public static int TEMPO_VIAGEM_FORA_PICO_MIN = 15;
    public static int TEMPO_VIAGEM_FORA_PICO_MAX = 30;
    public static int MAX_VIAGENS_DIARIAS = 3;
    public static double[] GERACAO_LIXO_MIN = {20, 20, 20, 20, 20};
    public static double[] GERACAO_LIXO_MAX = {20, 20, 20, 20, 20};
    public static int TEMPO_MAX_ESPERA_CAMINHAO_PEQUENO = 60;
    public static int TOLERANCIA_ESPERA_CAMINHAO_GRANDE = 120;

    public static int NUMERO_ESTACOES_TRANSFERENCIA = 2;
    public static int NUMERO_CAMINHOES_GRANDES_INICIAL = 1;
    public static int NUMERO_CAMINHOES_PEQUENOS = 10; // Nova variável configurável
}