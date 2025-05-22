public class ConfiguracaoSimulacao {
    // Array com as capacidades fixas dos caminhões pequenos em toneladas
    public static final double[] CAPACIDADES_CAMINHOES_PEQUENOS = {2, 4, 8, 10};
    // Capacidade fixa dos caminhões grandes em toneladas
    public static final int CAPACIDADE_CAMINHAO_GRANDE = 20;
    // Tempo mínimo de viagem durante horário de pico em minutos
    public static int TEMPO_VIAGEM_PICO_MIN = 30;
    // Tempo máximo de viagem durante horário de pico em minutos
    public static int TEMPO_VIAGEM_PICO_MAX = 60;
    // Tempo mínimo de viagem fora do horário de pico em minutos
    public static int TEMPO_VIAGEM_FORA_PICO_MIN = 15;
    // Tempo máximo de viagem fora do horário de pico em minutos
    public static int TEMPO_VIAGEM_FORA_PICO_MAX = 30;
    // Número máximo de viagens permitidas por dia
    public static int MAX_VIAGENS_DIARIAS = 3;
    // Array com a geração mínima de lixo por zona em toneladas por dia
    public static double[] GERACAO_LIXO_MIN = {20, 20, 20, 20, 20};
    // Array com a geração máxima de lixo por zona em toneladas por dia
    public static double[] GERACAO_LIXO_MAX = {20, 20, 20, 20, 20};
    // Tempo máximo de espera permitido para caminhões pequenos em minutos
    public static int TEMPO_MAX_ESPERA_CAMINHAO_PEQUENO = 60;
    // Tolerância de espera para caminhões grandes em minutos
    public static int TOLERANCIA_ESPERA_CAMINHAO_GRANDE = 120;
    // Número de estações de transferência
    public static int NUMERO_ESTACOES_TRANSFERENCIA = 2;
    // Número inicial de caminhões grandes
    public static int NUMERO_CAMINHOES_GRANDES_INICIAL = 1;
    // Número inicial de caminhões pequenos
    public static int NUMERO_CAMINHOES_PEQUENOS = 10;
}
