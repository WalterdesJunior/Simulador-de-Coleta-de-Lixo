public class Fila<T> {
    private No<T> inicio;
    private No<T> fim;
    private int tamanho;

    private static class No<T> {
        T dado;
        No<T> proximo;

        No(T dado) {
            this.dado = dado;
            this.proximo = null;
        }
    }

    public Fila() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    public void enfileirar(T elemento) {
        No<T> novoNo = new No<>(elemento);
        if (estaVazia()) {
            inicio = novoNo;
            fim = novoNo;
        } else {
            fim.proximo = novoNo;
            fim = novoNo;
        }
        tamanho++;
    }

    public T desenfileirar() {
        if (estaVazia()) return null;
        T elemento = inicio.dado;
        inicio = inicio.proximo;
        tamanho--;
        if (estaVazia()) fim = null;
        return elemento;
    }

    public T frente() { // Novo método adicionado
        if (estaVazia()) return null;
        return inicio.dado;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    public int tamanho() {
        return tamanho;
    }
}