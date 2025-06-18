public class Principal {
    public static void main(String[] args) {
        Simulador simulador = new Simulador();
        simulador.inicializar();
        simulador.executarSimulacao(1440); // Simula 1 dia (1440 minutos)
    }
} 