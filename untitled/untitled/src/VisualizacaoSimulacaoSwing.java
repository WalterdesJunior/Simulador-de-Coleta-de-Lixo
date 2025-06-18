import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.awt.event.ActionListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.JFreeChart;

public class VisualizacaoSimulacaoSwing extends JFrame {
    private static VisualizacaoSimulacaoSwing instancia = null;
    private Simulador simulador;
    private Map<Integer, Point> posicoesCaminhoesPequenos;
    private Map<Integer, Point> posicoesCaminhoesGrandes;
    private Thread animacaoThread;
    private volatile boolean simulacaoAtiva;
    private volatile boolean simulacaoIniciada;
    private JLabel lblTempoMedioEspera, lblLixoTotalColetado, lblCaminhoesGrandesNecessarios, lblLixoAcumulado;
    private JLabel[] lblZonaInfo = new JLabel[5];
    private JLabel[] lblCaminhaoInfo = new JLabel[10];
    private JLabel[] lblEstacaoInfo = new JLabel[5];

    private JTextField txtTempoPicoMin, txtTempoPicoMax, txtTempoForaPicoMin, txtTempoForaPicoMax;
    private JTextField txtMaxViagensDiarias;
    private JTextField[] txtGeracaoLixoMin = new JTextField[5];
    private JTextField[] txtGeracaoLixoMax = new JTextField[5];
    private JTextField txtTempoMaxEsperaPequeno;
    private JTextField txtToleranciaEsperaGrande;
    private JTextField txtNumeroCaminhoesGrandesInicial;
    private JTextField txtNumeroCaminhoesPequenos;
    private JPanel painelMapa;
    private JPanel painelVisualizacaoCaminhoes;
    private JButton btnIniciarSimulacao;
    private JButton btnPausarSimulacao;
    private JButton btnReiniciarSimulacao;
    private JButton btnGerarRelatorio;
    private java.util.List<Double> lixoColetadoPorTempo = new java.util.ArrayList<>();

    private VisualizacaoSimulacaoSwing(Simulador sim) {
        this.simulador = sim;
        this.posicoesCaminhoesPequenos = new HashMap<>();
        this.posicoesCaminhoesGrandes = new HashMap<>();
        this.simulacaoAtiva = true;
        this.simulacaoIniciada = false;
        inicializarPosicoes();
        mostrarTelaConfiguracao();
    }

    private void inicializarPosicoes() {
        if (simulador == null) return;
        if (simulador.getCaminhoesPequenos() != null) {
            for (int i = 0; i < simulador.getCaminhoesPequenos().tamanho(); i++) {
                CaminhaoPequeno caminhao = simulador.getCaminhoesPequenos().obter(i);
                if (caminhao != null && caminhao.getZonaFixa() != null) {
                    posicoesCaminhoesPequenos.put(caminhao.getId(), new Point(getPosicaoZona(caminhao.getZonaFixa())));
                }
            }
        }
        if (simulador.getEstacoesTransferencia() != null) {
            for (int i = 0; i < simulador.getEstacoesTransferencia().tamanho(); i++) {
                EstacaoTransferencia estacao = simulador.getEstacoesTransferencia().obter(i);
                if (estacao != null && estacao.getCaminhaoGrandeAtual() != null) {
                    posicoesCaminhoesGrandes.put(estacao.getCaminhaoGrandeAtual().getId(),
                            new Point(getPosicaoEstacao(estacao)));
                }
            }
        }
    }

    private Point getPosicaoZona(Zona zona) {
        if (zona == null) return new Point(0, 0);
        switch (zona.getNome()) {
            case "Sul": return new Point(300, 600);
            case "Norte": return new Point(700, 150);
            case "Centro": return new Point(500, 350);
            case "Leste": return new Point(850, 450);
            case "Sudeste": return new Point(400, 500);
            default: return new Point(0, 0);
        }
    }

    private Point getPosicaoEstacao(EstacaoTransferencia estacao) {
        if (estacao == null) return new Point(0, 0);
        int baseX = 350 + (estacao.getId() - 1) * 300;
        return new Point(baseX, 250);
    }

    private void mostrarTelaConfiguracao() {
        JFrame telaConfig = new JFrame("Configuração da Simulação");
        telaConfig.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        telaConfig.getContentPane().setBackground(new Color(245, 245, 245));
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblPicoMin = new JLabel("Tempo Pico Mínimo (min):");
        lblPicoMin.setFont(labelFont);
        telaConfig.add(lblPicoMin, gbc);
        gbc.gridx = 1;
        txtTempoPicoMin = new JTextField(String.valueOf(ConfiguracaoSimulacao.TEMPO_VIAGEM_PICO_MIN), 5);
        txtTempoPicoMin.setFont(labelFont);
        telaConfig.add(txtTempoPicoMin, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblPicoMax = new JLabel("Tempo Pico Máximo (min):");
        lblPicoMax.setFont(labelFont);
        telaConfig.add(lblPicoMax, gbc);
        gbc.gridx = 1;
        txtTempoPicoMax = new JTextField(String.valueOf(ConfiguracaoSimulacao.TEMPO_VIAGEM_PICO_MAX), 5);
        txtTempoPicoMax.setFont(labelFont);
        telaConfig.add(txtTempoPicoMax, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblForaPicoMin = new JLabel("Tempo Fora Pico Mínimo (min):");
        lblForaPicoMin.setFont(labelFont);
        telaConfig.add(lblForaPicoMin, gbc);
        gbc.gridx = 1;
        txtTempoForaPicoMin = new JTextField(String.valueOf(ConfiguracaoSimulacao.TEMPO_VIAGEM_FORA_PICO_MIN), 5);
        txtTempoForaPicoMin.setFont(labelFont);
        telaConfig.add(txtTempoForaPicoMin, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblForaPicoMax = new JLabel("Tempo Fora Pico Máximo (min):");
        lblForaPicoMax.setFont(labelFont);
        telaConfig.add(lblForaPicoMax, gbc);
        gbc.gridx = 1;
        txtTempoForaPicoMax = new JTextField(String.valueOf(ConfiguracaoSimulacao.TEMPO_VIAGEM_FORA_PICO_MAX), 5);
        txtTempoForaPicoMax.setFont(labelFont);
        telaConfig.add(txtTempoForaPicoMax, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblMaxViagens = new JLabel("Máx. Viagens Diárias:");
        lblMaxViagens.setFont(labelFont);
        telaConfig.add(lblMaxViagens, gbc);
        gbc.gridx = 1;
        txtMaxViagensDiarias = new JTextField(String.valueOf(ConfiguracaoSimulacao.MAX_VIAGENS_DIARIAS), 5);
        txtMaxViagensDiarias.setFont(labelFont);
        telaConfig.add(txtMaxViagensDiarias, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblGeracaoMin = new JLabel("Geração Lixo Mínimo por Zona (t/dia):");
        lblGeracaoMin.setFont(labelFont);
        telaConfig.add(lblGeracaoMin, gbc);
        gbc.gridx = 1;
        JPanel painelGeracaoMin = new JPanel(new GridLayout(1, 5, 5, 5));
        String[] zonas = {"Sul", "Norte", "Centro", "Leste", "Sudeste"};
        for (int i = 0; i < 5; i++) {
            JPanel zonaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel lblZona = new JLabel(zonas[i] + ": ");
            lblZona.setFont(labelFont);
            zonaPanel.add(lblZona);
            txtGeracaoLixoMin[i] = new JTextField(String.valueOf(ConfiguracaoSimulacao.GERACAO_LIXO_MIN[i]), 5);
            txtGeracaoLixoMin[i].setFont(labelFont);
            zonaPanel.add(txtGeracaoLixoMin[i]);
            painelGeracaoMin.add(zonaPanel);
        }
        telaConfig.add(painelGeracaoMin, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblGeracaoMax = new JLabel("Geração Lixo Máximo por Zona (t/dia):");
        lblGeracaoMax.setFont(labelFont);
        telaConfig.add(lblGeracaoMax, gbc);
        gbc.gridx = 1;
        JPanel painelGeracaoMax = new JPanel(new GridLayout(1, 5, 5, 5));
        for (int i = 0; i < 5; i++) {
            JPanel zonaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel lblZona = new JLabel(zonas[i] + ": ");
            lblZona.setFont(labelFont);
            zonaPanel.add(lblZona);
            txtGeracaoLixoMax[i] = new JTextField(String.valueOf(ConfiguracaoSimulacao.GERACAO_LIXO_MAX[i]), 5);
            txtGeracaoLixoMax[i].setFont(labelFont);
            zonaPanel.add(txtGeracaoLixoMax[i]);
            painelGeracaoMax.add(zonaPanel);
        }
        telaConfig.add(painelGeracaoMax, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblEsperaPequeno = new JLabel("Tempo Máx. Espera Pequenos (min):");
        lblEsperaPequeno.setFont(labelFont);
        telaConfig.add(lblEsperaPequeno, gbc);
        gbc.gridx = 1;
        txtTempoMaxEsperaPequeno = new JTextField(String.valueOf(ConfiguracaoSimulacao.TEMPO_MAX_ESPERA_CAMINHAO_PEQUENO), 5);
        txtTempoMaxEsperaPequeno.setFont(labelFont);
        telaConfig.add(txtTempoMaxEsperaPequeno, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblToleranciaGrande = new JLabel("Tolerância Espera Grande (min):");
        lblToleranciaGrande.setFont(labelFont);
        telaConfig.add(lblToleranciaGrande, gbc);
        gbc.gridx = 1;
        txtToleranciaEsperaGrande = new JTextField(String.valueOf(ConfiguracaoSimulacao.TOLERANCIA_ESPERA_CAMINHAO_GRANDE), 5);
        txtToleranciaEsperaGrande.setFont(labelFont);
        telaConfig.add(txtToleranciaEsperaGrande, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblNumeroCaminhoesGrandes = new JLabel("Número Inicial de Caminhões Grandes:");
        lblNumeroCaminhoesGrandes.setFont(labelFont);
        telaConfig.add(lblNumeroCaminhoesGrandes, gbc);
        gbc.gridx = 1;
        txtNumeroCaminhoesGrandesInicial = new JTextField(String.valueOf(ConfiguracaoSimulacao.NUMERO_CAMINHOES_GRANDES_INICIAL), 5);
        txtNumeroCaminhoesGrandesInicial.setFont(labelFont);
        telaConfig.add(txtNumeroCaminhoesGrandesInicial, gbc);

        gbc.gridx = 0; gbc.gridy++;
        JLabel lblNumeroCaminhoesPequenos = new JLabel("Número de Caminhões Pequenos (1-10):");
        lblNumeroCaminhoesPequenos.setFont(labelFont);
        telaConfig.add(lblNumeroCaminhoesPequenos, gbc);
        gbc.gridx = 1;
        txtNumeroCaminhoesPequenos = new JTextField(String.valueOf(ConfiguracaoSimulacao.NUMERO_CAMINHOES_PEQUENOS), 5);
        txtNumeroCaminhoesPequenos.setFont(labelFont);
        telaConfig.add(txtNumeroCaminhoesPequenos, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JButton btnIniciar = new JButton("Iniciar Simulação");
        btnIniciar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIniciar.setBackground(new Color(46, 204, 113));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFocusPainted(false);
        btnIniciar.addActionListener(e -> {
            try {
                int tempoPicoMin = Integer.parseInt(txtTempoPicoMin.getText());
                int tempoPicoMax = Integer.parseInt(txtTempoPicoMax.getText());
                int tempoForaPicoMin = Integer.parseInt(txtTempoForaPicoMin.getText());
                int tempoForaPicoMax = Integer.parseInt(txtTempoForaPicoMax.getText());
                int maxViagens = Integer.parseInt(txtMaxViagensDiarias.getText());
                double[] geracaoMin = new double[5];
                double[] geracaoMax = new double[5];
                for (int i = 0; i < 5; i++) {
                    geracaoMin[i] = Double.parseDouble(txtGeracaoLixoMin[i].getText());
                    geracaoMax[i] = Double.parseDouble(txtGeracaoLixoMax[i].getText());
                }
                int tempoMaxEsperaPequeno = Integer.parseInt(txtTempoMaxEsperaPequeno.getText());
                int toleranciaGrande = Integer.parseInt(txtToleranciaEsperaGrande.getText());
                int numeroCaminhoesGrandesInicial = Integer.parseInt(txtNumeroCaminhoesGrandesInicial.getText());
                int numeroCaminhoesPequenos = Integer.parseInt(txtNumeroCaminhoesPequenos.getText());

                if (numeroCaminhoesPequenos < 1 || numeroCaminhoesPequenos > 10) {
                    JOptionPane.showMessageDialog(telaConfig, "O número de caminhões pequenos deve estar entre 1 e 10!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ConfiguracaoSimulacao.TEMPO_VIAGEM_PICO_MIN = tempoPicoMin;
                ConfiguracaoSimulacao.TEMPO_VIAGEM_PICO_MAX = tempoPicoMax;
                ConfiguracaoSimulacao.TEMPO_VIAGEM_FORA_PICO_MIN = tempoForaPicoMin;
                ConfiguracaoSimulacao.TEMPO_VIAGEM_FORA_PICO_MAX = tempoForaPicoMax;
                ConfiguracaoSimulacao.MAX_VIAGENS_DIARIAS = maxViagens;
                ConfiguracaoSimulacao.GERACAO_LIXO_MIN = geracaoMin;
                ConfiguracaoSimulacao.GERACAO_LIXO_MAX = geracaoMax;
                ConfiguracaoSimulacao.TEMPO_MAX_ESPERA_CAMINHAO_PEQUENO = tempoMaxEsperaPequeno;
                ConfiguracaoSimulacao.TOLERANCIA_ESPERA_CAMINHAO_GRANDE = toleranciaGrande;
                ConfiguracaoSimulacao.NUMERO_CAMINHOES_GRANDES_INICIAL = numeroCaminhoesGrandesInicial;
                ConfiguracaoSimulacao.NUMERO_CAMINHOES_PEQUENOS = numeroCaminhoesPequenos;

                simulador.inicializar();
                simulador.setSimulacaoAtiva(false);
                inicializarInterface();
                telaConfig.dispose();
                iniciarThreadAnimacao();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(telaConfig, "Por favor, insira valores numéricos válidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        telaConfig.add(btnIniciar, gbc);

        telaConfig.pack();
        telaConfig.setLocationRelativeTo(null);
        telaConfig.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        telaConfig.setVisible(true);
    }

    private void iniciarThreadAnimacao() {
        if (animacaoThread == null || !animacaoThread.isAlive()) {
            simulacaoAtiva = true;
            animacaoThread = new Thread(() -> {
                while (simulacaoAtiva) {
                    try {
                        Thread.sleep(150);
                        SwingUtilities.invokeLater(() -> {
                            if (simulacaoIniciada) {
                                atualizarPosicoesCaminhoes();
                            }
                            atualizarInterface();
                        });
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            animacaoThread.setDaemon(true);
            animacaoThread.start();
        }
    }

    private void inicializarInterface() {
        SwingUtilities.invokeLater(() -> {
            setTitle("Simulação de Coleta de Lixo - Teresina");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout(10, 10));
            setSize(1280, 720);
            setLocationRelativeTo(null);
            getContentPane().setBackground(new Color(240, 240, 245));

            JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
            painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            painelPrincipal.setBackground(new Color(240, 240, 245));

            JPanel painelEstatisticas = new JPanel(new GridLayout(1, 4, 20, 0));
            painelEstatisticas.setBackground(new Color(34, 45, 65));
            painelEstatisticas.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            lblTempoMedioEspera = new JLabel("0.0 min");
            lblLixoTotalColetado = new JLabel("0.0 t");
            lblCaminhoesGrandesNecessarios = new JLabel("0");
            lblLixoAcumulado = new JLabel("0.0 t");

            Font estatisticaFont = new Font("Segoe UI", Font.BOLD, 18);
            Font tituloFont = new Font("Segoe UI", Font.PLAIN, 12);

            painelEstatisticas.add(criarPainelEstatistica("Tempo Médio de Espera", lblTempoMedioEspera, estatisticaFont, tituloFont));
            painelEstatisticas.add(criarPainelEstatistica("Lixo Total Coletado", lblLixoTotalColetado, estatisticaFont, tituloFont));
            painelEstatisticas.add(criarPainelEstatistica("Caminhões Grandes", lblCaminhoesGrandesNecessarios, estatisticaFont, tituloFont));
            painelEstatisticas.add(criarPainelEstatistica("Lixo Acumulado", lblLixoAcumulado, estatisticaFont, tituloFont));

            JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            painelControles.setBackground(new Color(34, 45, 65));

            Font botaoFont = new Font("Segoe UI", Font.BOLD, 12);
            btnIniciarSimulacao = new JButton("Iniciar");
            btnIniciarSimulacao.setFont(botaoFont);
            btnIniciarSimulacao.setBackground(new Color(46, 204, 113));
            btnIniciarSimulacao.setForeground(Color.WHITE);
            btnIniciarSimulacao.setFocusPainted(false);
            btnIniciarSimulacao.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            btnIniciarSimulacao.addActionListener(e -> {
                simulador.setSimulacaoAtiva(true);
                simulador.executarSimulacao(1440);
                simulacaoIniciada = true;
                btnIniciarSimulacao.setEnabled(false);
                btnPausarSimulacao.setEnabled(true);
            });

            btnPausarSimulacao = new JButton("Pausar");
            btnPausarSimulacao.setFont(botaoFont);
            btnPausarSimulacao.setBackground(new Color(231, 76, 60));
            btnPausarSimulacao.setForeground(Color.WHITE);
            btnPausarSimulacao.setFocusPainted(false);
            btnPausarSimulacao.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            btnPausarSimulacao.setEnabled(false);
            btnPausarSimulacao.addActionListener(e -> {
                simulacaoIniciada = !simulacaoIniciada;
                simulador.setSimulacaoAtiva(simulacaoIniciada);
                btnPausarSimulacao.setText(simulacaoIniciada ? "Pausar" : "Continuar");
                btnPausarSimulacao.setBackground(simulacaoIniciada ? new Color(231, 76, 60) : new Color(52, 152, 219));
            });

            btnReiniciarSimulacao = new JButton("Reiniciar");
            btnReiniciarSimulacao.setFont(botaoFont);
            btnReiniciarSimulacao.setBackground(new Color(52, 152, 219));
            btnReiniciarSimulacao.setForeground(Color.WHITE);
            btnReiniciarSimulacao.setFocusPainted(false);
            btnReiniciarSimulacao.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            btnReiniciarSimulacao.addActionListener(e -> {
                simulacaoIniciada = false;
                simulador.setSimulacaoAtiva(false);
                simulador.inicializar();
                inicializarPosicoes();
                btnIniciarSimulacao.setEnabled(true);
                btnPausarSimulacao.setEnabled(false);
                btnPausarSimulacao.setText("Pausar");
                btnPausarSimulacao.setBackground(new Color(231, 76, 60));
                btnGerarRelatorio.setEnabled(false);
            });

            btnGerarRelatorio = new JButton("Gerar Relatório");
            btnGerarRelatorio.setFont(botaoFont);
            btnGerarRelatorio.setBackground(new Color(153, 102, 255));
            btnGerarRelatorio.setForeground(Color.WHITE);
            btnGerarRelatorio.setFocusPainted(false);
            btnGerarRelatorio.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            btnGerarRelatorio.setEnabled(false);
            btnGerarRelatorio.addActionListener(e -> mostrarRelatorio());

            painelControles.add(btnIniciarSimulacao);
            painelControles.add(btnPausarSimulacao);
            painelControles.add(btnReiniciarSimulacao);
            painelControles.add(btnGerarRelatorio);

            JPanel painelSuperior = new JPanel(new BorderLayout());
            painelSuperior.setBackground(new Color(34, 45, 65));
            painelSuperior.add(painelEstatisticas, BorderLayout.CENTER);
            painelSuperior.add(painelControles, BorderLayout.EAST);

            painelMapa = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    GradientPaint bg = new GradientPaint(0, 0, new Color(255, 255, 255), 0, getHeight(), new Color(230, 230, 235));
                    g2d.setPaint(bg);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    if (simulador != null && simulador.getZonas() != null) {
                        synchronized (simulador.getZonas()) {
                            for (int i = 0; i < simulador.getZonas().tamanho(); i++) {
                                Zona zona = simulador.getZonas().obter(i);
                                if (zona == null) continue;
                                Point p = getPosicaoZona(zona);
                                GradientPaint gradient = getGradienteZona(zona);
                                g2d.setPaint(gradient);
                                g2d.fillOval(p.x - 40, p.y - 40, 80, 80);
                                g2d.setColor(new Color(50, 50, 50, 150));
                                g2d.setStroke(new BasicStroke(2));
                                g2d.drawOval(p.x - 40, p.y - 40, 80, 80);

                                g2d.setColor(Color.WHITE);
                                g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                                FontMetrics fm = g2d.getFontMetrics();
                                int textWidth = fm.stringWidth(zona.getNome());
                                g2d.drawString(zona.getNome(), p.x - textWidth / 2, p.y + 5);

                                String lixoInfo = String.format("%.1f t", zona.getLixoAcumulado());
                                textWidth = fm.stringWidth(lixoInfo);
                                g2d.setColor(new Color(0, 0, 0, 180));
                                g2d.fillRoundRect(p.x - textWidth / 2 - 5, p.y + 15, textWidth + 10, 20, 10, 10);
                                g2d.setColor(Color.WHITE);
                                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                                g2d.drawString(lixoInfo, p.x - textWidth / 2, p.y + 30);
                                String lixoTotalInfo = String.format("Gerado: %.1f t", zona.getLixoGeradoTotal());
                                textWidth = fm.stringWidth(lixoTotalInfo);
                                g2d.setColor(new Color(0, 0, 0, 120));
                                g2d.fillRoundRect(p.x - textWidth / 2 - 5, p.y + 35, textWidth + 10, 18, 10, 10);
                                g2d.setColor(Color.YELLOW);
                                g2d.drawString(lixoTotalInfo, p.x - textWidth / 2, p.y + 48);
                            }
                        }
                    }

                    if (simulador != null && simulador.getEstacoesTransferencia() != null) {
                        synchronized (simulador.getEstacoesTransferencia()) {
                            for (int i = 0; i < simulador.getEstacoesTransferencia().tamanho(); i++) {
                                EstacaoTransferencia estacao = simulador.getEstacoesTransferencia().obter(i);
                                if (estacao == null) continue;
                                Point p = getPosicaoEstacao(estacao);
                                GradientPaint gradient = new GradientPaint(p.x - 30, p.y - 30, new Color(46, 204, 113), p.x + 30, p.y + 30, new Color(39, 174, 96));
                                g2d.setPaint(gradient);
                                int[] xPoints = new int[6];
                                int[] yPoints = new int[6];
                                int radius = 30;
                                for (int j = 0; j < 6; j++) {
                                    xPoints[j] = p.x + (int) (radius * Math.cos(j * 2 * Math.PI / 6));
                                    yPoints[j] = p.y + (int) (radius * Math.sin(j * 2 * Math.PI / 6));
                                }
                                g2d.fillPolygon(xPoints, yPoints, 6);
                                g2d.setColor(new Color(30, 30, 30));
                                g2d.setStroke(new BasicStroke(2));
                                g2d.drawPolygon(xPoints, yPoints, 6);

                                g2d.setColor(Color.WHITE);
                                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                                FontMetrics fm = g2d.getFontMetrics();
                                String estacaoText = "Estação " + estacao.getId();
                                int textWidth = fm.stringWidth(estacaoText);
                                g2d.drawString(estacaoText, p.x - textWidth / 2, p.y + 5);
                            }
                        }
                    }

                    if (simulador != null && simulador.getCaminhoesPequenos() != null) {
                        synchronized (simulador.getCaminhoesPequenos()) {
                            for (int i = 0; i < simulador.getCaminhoesPequenos().tamanho(); i++) {
                                CaminhaoPequeno caminhao = simulador.getCaminhoesPequenos().obter(i);
                                if (caminhao == null) continue;
                                Point posAtual = posicoesCaminhoesPequenos.get(caminhao.getId());
                                if (posAtual != null) {
                                    desenharCaminhaoPequeno(g2d, posAtual, caminhao);
                                }
                            }
                        }
                    }

                    if (simulador != null && simulador.getEstacoesTransferencia() != null) {
                        synchronized (simulador.getEstacoesTransferencia()) {
                            for (int i = 0; i < simulador.getEstacoesTransferencia().tamanho(); i++) {
                                EstacaoTransferencia estacao = simulador.getEstacoesTransferencia().obter(i);
                                if (estacao == null || estacao.getCaminhaoGrandeAtual() == null) continue;
                                CaminhaoGrande caminhao = estacao.getCaminhaoGrandeAtual();
                                Point posAtual = posicoesCaminhoesGrandes.get(caminhao.getId());
                                if (posAtual == null) {
                                    posAtual = new Point(getPosicaoEstacao(estacao));
                                    posicoesCaminhoesGrandes.put(caminhao.getId(), posAtual);
                                }
                                desenharCaminhaoGrande(g2d, posAtual, caminhao);
                            }
                        }
                    }

                    desenharLegenda(g2d);
                }
            };
            painelMapa.setPreferredSize(new Dimension(900, 800));
            painelMapa.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 210), 2, true));

            painelVisualizacaoCaminhoes = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    GradientPaint bg = new GradientPaint(0, 0, new Color(255, 255, 255), 0, getHeight(), new Color(230, 230, 235));
                    g2d.setPaint(bg);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    g2d.setColor(new Color(34, 45, 65));
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
                    g2d.drawString("Visualização de Caminhões", 20, 30);

                    int x = 50, y = 80, espacamentoHorizontal = 200, espacamentoVertical = 200;
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    g2d.drawString("Caminhões Pequenos:", 20, 60);

                    if (simulador != null && simulador.getCaminhoesPequenos() != null) {
                        synchronized (simulador.getCaminhoesPequenos()) {
                            for (int i = 0; i < simulador.getCaminhoesPequenos().tamanho(); i++) {
                                CaminhaoPequeno caminhao = simulador.getCaminhoesPequenos().obter(i);
                                if (caminhao == null) continue;
                                int coluna = i % 3;
                                int linha = i / 3;
                                int posX = x + coluna * espacamentoHorizontal;
                                int posY = y + linha * espacamentoVertical;
                                desenharCaminhaoPequeno(g2d, new Point(posX, posY), caminhao);

                                g2d.setColor(new Color(34, 45, 65));
                                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                                g2d.drawString("Caminhão " + caminhao.getId(), posX - 40, posY + 40);
                                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                                g2d.drawString(String.format("Cap: %.1f t", caminhao.getCapacidade()), posX - 40, posY + 55);
                                g2d.drawString(String.format("Carga: %.1f t", caminhao.getCargaAtual()), posX - 40, posY + 70);
                                String estado = caminhao.estaNaEstacaoTransferencia() ? "Na Estação" : "Em Rota";
                                g2d.drawString(estado, posX - 40, posY + 85);
                            }
                        }
                    }

                    y = y + (simulador.getCaminhoesPequenos().tamanho() / 3 + 1) * espacamentoVertical;
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    g2d.drawString("Caminhões Grandes:", 20, y - 20);
                    int contadorGrandes = 0;
                    if (simulador != null && simulador.getEstacoesTransferencia() != null) {
                        synchronized (simulador.getEstacoesTransferencia()) {
                            for (int i = 0; i < simulador.getEstacoesTransferencia().tamanho(); i++) {
                                EstacaoTransferencia estacao = simulador.getEstacoesTransferencia().obter(i);
                                if (estacao == null || estacao.getCaminhaoGrandeAtual() == null) continue;
                                CaminhaoGrande caminhao = estacao.getCaminhaoGrandeAtual();
                                int coluna = contadorGrandes % 3;
                                int linha = contadorGrandes / 3;
                                int posX = x + coluna * espacamentoHorizontal;
                                int posY = y + linha * espacamentoVertical;
                                desenharCaminhaoGrande(g2d, new Point(posX, posY), caminhao);

                                g2d.setColor(new Color(34, 45, 65));
                                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                                g2d.drawString("Caminhão Grande " + caminhao.getId(), posX - 60, posY + 50);
                                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                                g2d.drawString(String.format("Cap: %.1f t", caminhao.getCapacidade()), posX - 60, posY + 65);
                                g2d.drawString(String.format("Carga: %.1f t", caminhao.getCargaAtual()), posX - 60, posY + 80);
                                g2d.drawString("Estação: " + (i + 1), posX - 60, posY + 95);
                                contadorGrandes++;
                            }
                        }
                    }

                    int alturaTotal = y + (contadorGrandes / 3 + 1) * espacamentoVertical + 50;
                    setPreferredSize(new Dimension(900, alturaTotal));
                    revalidate();
                }
            };
            painelVisualizacaoCaminhoes.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 210), 2, true));

            JTabbedPane painelAbas = new JTabbedPane();
            painelAbas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            painelAbas.setBackground(new Color(240, 240, 245));

            JScrollPane scrollMapa = new JScrollPane(painelMapa);
            scrollMapa.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollMapa.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            JPanel painelZonas = new JPanel(new GridLayout(5, 1, 10, 10));
            painelZonas.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            painelZonas.setBackground(Color.WHITE);
            for (int i = 0; i < 5; i++) {
                JPanel painelZona = new JPanel(new BorderLayout());
                painelZona.setBackground(new Color(245, 245, 245));
                painelZona.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                lblZonaInfo[i] = new JLabel("Zona: - | Geração: 0.0 t/dia | Acumulado: 0.0 t");
                lblZonaInfo[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
                painelZona.add(lblZonaInfo[i], BorderLayout.CENTER);
                painelZonas.add(painelZona);
            }

            JPanel painelCaminhoes = new JPanel(new GridLayout(10, 1, 10, 10));
            painelCaminhoes.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            painelCaminhoes.setBackground(Color.WHITE);
            for (int i = 0; i < 10; i++) {
                JPanel painelCaminhao = new JPanel(new BorderLayout());
                painelCaminhao.setBackground(new Color(245, 245, 245));
                painelCaminhao.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                lblCaminhaoInfo[i] = new JLabel("Caminhão - | Cap: 0.0 t | Carga: 0.0 t | Viagens: 0 | Estado: -");
                lblCaminhaoInfo[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
                painelCaminhao.add(lblCaminhaoInfo[i], BorderLayout.CENTER);
                painelCaminhoes.add(painelCaminhao);
            }

            JPanel painelEstacoes = new JPanel(new GridLayout(5, 1, 10, 10));
            painelEstacoes.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            painelEstacoes.setBackground(Color.WHITE);
            for (int i = 0; i < 5; i++) {
                JPanel painelEstacao = new JPanel(new BorderLayout());
                painelEstacao.setBackground(new Color(245, 245, 245));
                painelEstacao.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                lblEstacaoInfo[i] = new JLabel("Estação - | Fila: 0 | Carga Grande: 0.0 t | Tempo Médio: 0.0 min");
                lblEstacaoInfo[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
                painelEstacao.add(lblEstacaoInfo[i], BorderLayout.CENTER);
                painelEstacoes.add(painelEstacao);
            }

            painelAbas.addTab("Mapa", scrollMapa);
            painelAbas.addTab("Caminhões", new JScrollPane(painelVisualizacaoCaminhoes));
            painelAbas.addTab("Zonas", new JScrollPane(painelZonas));
            painelAbas.addTab("Caminhões Info", new JScrollPane(painelCaminhoes));
            painelAbas.addTab("Estações", new JScrollPane(painelEstacoes));

            painelPrincipal.add(painelSuperior, BorderLayout.NORTH);
            painelPrincipal.add(painelAbas, BorderLayout.CENTER);
            add(painelPrincipal);

            setVisible(true);
        });
    }

    private JPanel criarPainelEstatistica(String titulo, JLabel lblValor, Font estatisticaFont, Font tituloFont) {
        JPanel painel = new JPanel(new BorderLayout(10, 5));
        painel.setOpaque(false);

        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelConteudo.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(new Color(180, 180, 180));
        lblTitulo.setFont(tituloFont);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(estatisticaFont);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelConteudo.add(lblValor);
        painelConteudo.add(lblTitulo);
        painel.add(painelConteudo, BorderLayout.CENTER);
        return painel;
    }

    private GradientPaint getGradienteZona(Zona zona) {
        if (zona == null) return new GradientPaint(0, 0, Color.GRAY, 1, 1, Color.DARK_GRAY);
        Color corInicio, corFim;
        switch (zona.getNome()) {
            case "Sul":
                corInicio = new Color(41, 128, 185);
                corFim = new Color(52, 152, 219);
                break;
            case "Norte":
                corInicio = new Color(155, 89, 182);
                corFim = new Color(142, 68, 173);
                break;
            case "Centro":
                corInicio = new Color(39, 174, 96);
                corFim = new Color(46, 204, 113);
                break;
            case "Leste":
                corInicio = new Color(241, 196, 15);
                corFim = new Color(243, 156, 18);
                break;
            case "Sudeste":
                corInicio = new Color(230, 126, 34);
                corFim = new Color(211, 84, 0);
                break;
            default:
                corInicio = Color.GRAY;
                corFim = Color.DARK_GRAY;
        }
        Point p = getPosicaoZona(zona);
        return new GradientPaint(p.x - 40, p.y - 40, corInicio, p.x + 40, p.y + 40, corFim);
    }

    private void desenharCaminhaoPequeno(Graphics2D g2d, Point pos, CaminhaoPequeno caminhao) {
        if (caminhao == null || pos == null) return;
        g2d.setColor(new Color(52, 152, 219));
        g2d.fillOval(pos.x - 10, pos.y - 10, 20, 20);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(pos.x - 10, pos.y - 10, 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
        String id = String.valueOf(caminhao.getId());
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(id);
        g2d.drawString(id, pos.x - textWidth / 2, pos.y + 4);
    }

    private void desenharCaminhaoGrande(Graphics2D g2d, Point pos, CaminhaoGrande caminhao) {
        if (caminhao == null || pos == null) return;
        g2d.setColor(new Color(46, 204, 113));
        int width = 30, height = 20;
        g2d.fillRect(pos.x - width / 2, pos.y - height / 2, width, height);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(pos.x - width / 2, pos.y - height / 2, width, height);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
        String id = "G" + caminhao.getId();
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(id);
        g2d.drawString(id, pos.x - textWidth / 2, pos.y + 6);
    }

    private void desenharLegenda(Graphics2D g2d) {
        int x = 50, y = 50;
        g2d.setColor(new Color(52, 152, 219));
        g2d.fillOval(x - 10, y - 10, 20, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x - 10, y - 10, 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.drawString("Caminhão Pequeno", x + 20, y + 5);

        x += 150;
        g2d.setColor(new Color(46, 204, 113));
        int width = 30, height = 20;
        g2d.fillRect(x - width / 2, y - height / 2, width, height);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x - width / 2, y - height / 2, width, height);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Caminhão Grande", x + 20, y + 5);
    }

    private void atualizarPosicoesCaminhoes() {
        if (simulador == null) return;
        synchronized (simulador.getCaminhoesPequenos()) {
            for (int i = 0; i < simulador.getCaminhoesPequenos().tamanho(); i++) {
                CaminhaoPequeno caminhao = simulador.getCaminhoesPequenos().obter(i);
                if (caminhao == null) continue;
                Point posAtual = posicoesCaminhoesPequenos.get(caminhao.getId());
                if (posAtual != null) {
                    if (caminhao.estaNaEstacaoTransferencia()) {
                        EstacaoTransferencia estacao = simulador.getEstacaoDoCaminhao(caminhao);
                        if (estacao != null) {
                            posAtual.setLocation(getPosicaoEstacao(estacao));
                        }
                    } else {
                        Zona zona = caminhao.getZonaFixa();
                        if (zona != null) {
                            Point destino = getPosicaoZona(zona);
                            moverCaminhao(posAtual, destino, 2);
                        }
                    }
                }
            }
        }

        synchronized (simulador.getEstacoesTransferencia()) {
            for (int i = 0; i < simulador.getEstacoesTransferencia().tamanho(); i++) {
                EstacaoTransferencia estacao = simulador.getEstacoesTransferencia().obter(i);
                if (estacao == null || estacao.getCaminhaoGrandeAtual() == null) continue;
                CaminhaoGrande caminhao = estacao.getCaminhaoGrandeAtual();
                Point posAtual = posicoesCaminhoesGrandes.get(caminhao.getId());
                if (posAtual == null) {
                    posAtual = new Point(getPosicaoEstacao(estacao));
                    posicoesCaminhoesGrandes.put(caminhao.getId(), posAtual);
                }
            }
        }
    }

    private void moverCaminhao(Point posAtual, Point destino, int velocidade) {
        if (posAtual == null || destino == null) return;
        int dx = destino.x - posAtual.x;
        int dy = destino.y - posAtual.y;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        if (distancia > velocidade) {
            double angulo = Math.atan2(dy, dx);
            posAtual.x += (int) (velocidade * Math.cos(angulo));
            posAtual.y += (int) (velocidade * Math.sin(angulo));
        } else {
            posAtual.setLocation(destino);
        }
    }

    private void atualizarInterface() {
        if (simulador == null) return;
        lblTempoMedioEspera.setText(String.format("%.1f min", simulador.getTempoMedioEspera()));
        lblLixoTotalColetado.setText(String.format("%.1f t", simulador.getLixoTotalColetado()));
        lblCaminhoesGrandesNecessarios.setText(String.valueOf(simulador.getCaminhoesGrandesNecessarios()));
        lblLixoAcumulado.setText(String.format("%.1f t", simulador.getLixoAcumulado()));

        if (simulador.getZonas() != null) {
            synchronized (simulador.getZonas()) {
                for (int i = 0; i < simulador.getZonas().tamanho(); i++) {
                    Zona zona = simulador.getZonas().obter(i);
                    if (zona != null && i < lblZonaInfo.length) {
                        lblZonaInfo[i].setText(String.format("Zona: %s | Geração: %.1f-%.1f t/dia | Acumulado: %.1f t | Gerado Total: %.1f t",
                                zona.getNome(), ConfiguracaoSimulacao.GERACAO_LIXO_MIN[i], ConfiguracaoSimulacao.GERACAO_LIXO_MAX[i], zona.getLixoAcumulado(), zona.getLixoGeradoTotal()));
                    }
                }
            }
        }

        if (simulador.getCaminhoesPequenos() != null) {
            synchronized (simulador.getCaminhoesPequenos()) {
                for (int i = 0; i < 10; i++) {
                    if (i < simulador.getCaminhoesPequenos().tamanho()) {
                        CaminhaoPequeno caminhao = simulador.getCaminhoesPequenos().obter(i);
                        if (caminhao != null && i < lblCaminhaoInfo.length) {
                            String estado = caminhao.estaNaEstacaoTransferencia() ? "Na Estação" : "Em Rota";
                            lblCaminhaoInfo[i].setText(String.format("Caminhão %d | Cap: %.1f t | Carga: %.1f t | Viagens: %d | Estado: %s",
                                    caminhao.getId(), caminhao.getCapacidade(), caminhao.getCargaAtual(), caminhao.getViagensRealizadas(), estado));
                        }
                    } else {
                        lblCaminhaoInfo[i].setText("Caminhão - | Cap: 0.0 t | Carga: 0.0 t | Viagens: 0 | Estado: -");
                    }
                }
            }
        }

        if (simulador.getEstacoesTransferencia() != null) {
            synchronized (simulador.getEstacoesTransferencia()) {
                for (int i = 0; i < simulador.getEstacoesTransferencia().tamanho(); i++) {
                    EstacaoTransferencia estacao = simulador.getEstacoesTransferencia().obter(i);
                    if (estacao != null && i < lblEstacaoInfo.length) {
                        lblEstacaoInfo[i].setText(String.format("Estação %d | Fila: %d | Carga Grande: %.1f t | Tempo Médio: %.1f min",
                                estacao.getId(), estacao.getFilaCaminhoesPequenos().tamanho(), estacao.getCaminhaoGrandeAtual().getCargaAtual(), estacao.getTempoMedioEspera()));
                    }
                }
            }
        }

        if (simulador.isSimulacaoConcluida() && !btnGerarRelatorio.isEnabled()) {
            btnGerarRelatorio.setEnabled(true);
        }

        repaint();
    }

    private void mostrarRelatorio() {
        if (simulador == null || !simulador.isSimulacaoConcluida()) return;
        JDialog dialog = new JDialog(this, "Relatório Final - Simulação de Coleta de Lixo", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(this);

        JTabbedPane abas = new JTabbedPane();

        // Aba 1: Texto do relatório
        JTextArea textoResumo = new JTextArea(20, 60);
        textoResumo.setLineWrap(true);
        textoResumo.setWrapStyleWord(true);
        textoResumo.setEditable(false);
        textoResumo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textoResumo.setBackground(new Color(245, 245, 245));
        textoResumo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        double tempoMedioEspera = simulador.getTempoMedioEspera();
        double lixoTotalColetado = simulador.getLixoTotalColetado();
        double lixoGeradoTotal = simulador.getLixoGeradoTotal();
        double lixoAcumulado = simulador.getLixoAcumulado();
        int caminhoesGrandesUsados = simulador.getCaminhoesGrandesNecessarios();
        StringBuilder resumo = new StringBuilder();
        resumo.append("### Análise da Simulação (Dia: 15/05/2025)\n\n");
        resumo.append("A simulação de coleta de lixo, realizada em Teresina, abrangeu 1440 minutos (24 horas) a partir das 00:00. Abaixo estão os principais resultados:\n\n");
        resumo.append("- **Eficiência da Coleta**: Foi coletado ").append(String.format("%.2f", lixoTotalColetado))
                .append(" toneladas de um total de ").append(String.format("%.2f", lixoGeradoTotal))
                .append(" toneladas geradas, resultando em uma eficiência de ")
                .append(String.format("%.1f", (lixoTotalColetado / lixoGeradoTotal) * 100)).append("%.\n");
        resumo.append("- **Lixo Acumulado**: Permaneceu ").append(String.format("%.2f", lixoAcumulado))
                .append(" toneladas não coletadas, indicando possíveis áreas de melhoria.\n");
        resumo.append("- **Tempo Médio de Espera**: Os caminhões pequenos esperaram em média ")
                .append(String.format("%.2f", tempoMedioEspera)).append(" minutos nas estações de transferência.\n");
        resumo.append("- **Uso de Recursos**: Foram necessários ").append(caminhoesGrandesUsados)
                .append(" caminhões grandes para atender à demanda.\n\n");
        resumo.append("**Observações**: Tempos de espera elevados (>60 min) sugerem possíveis gargalos nas estações de transferência. Recomenda-se ajustar a distribuição de caminhões pequenos ou aumentar a capacidade de processamento das estações.\n");
        textoResumo.setText(resumo.toString());
        JScrollPane scrollResumo = new JScrollPane(textoResumo, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        abas.addTab("Resumo", scrollResumo);

        // Aba 2: Tabela de estatísticas
        JTable tabelaEstatisticas = criarTabelaEstatisticas();
        JScrollPane scrollTabela = new JScrollPane(tabelaEstatisticas);
        abas.addTab("Tabela", scrollTabela);

        // Aba 3: Gráficos
        JPanel painelGraficos = new JPanel(new GridLayout(2, 1, 10, 10));
        painelGraficos.setBorder(BorderFactory.createTitledBorder("Gráficos"));
        painelGraficos.setBackground(Color.WHITE);
        painelGraficos.add(criarGraficoLixoColetado());
        JLabel lblGrafico2 = new JLabel("Gráfico: Tempos de Espera por Estação (Implementar com JFreeChart)");
        lblGrafico2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        painelGraficos.add(lblGrafico2);
        JScrollPane scrollGraficos = new JScrollPane(painelGraficos);
        abas.addTab("Gráficos", scrollGraficos);

        dialog.add(abas, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JTable criarTabelaEstatisticas() {
        String[] colunas = {"Métrica", "Valor"};
        String[][] dados = new String[5][2];
        if (simulador != null) {
            dados[0] = new String[]{"Lixo Gerado Total", String.format("%.2f t", simulador.getLixoGeradoTotal())};
            dados[1] = new String[]{"Lixo Coletado", String.format("%.2f t", simulador.getLixoTotalColetado())};
            dados[2] = new String[]{"Lixo Acumulado", String.format("%.2f t", simulador.getLixoAcumulado())};
            dados[3] = new String[]{"Tempo Médio de Espera", String.format("%.2f min", simulador.getTempoMedioEspera())};
            dados[4] = new String[]{"Caminhões Grandes Usados", String.valueOf(simulador.getCaminhoesGrandesNecessarios())};
        }

        JTable tabela = new JTable(dados, colunas);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setRowHeight(25);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setBackground(Color.WHITE);
        tabela.setGridColor(new Color(200, 200, 210));
        return tabela;
    }

    private JPanel criarGraficoLixoColetado() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < lixoColetadoPorTempo.size(); i++) {
            dataset.addValue(lixoColetadoPorTempo.get(i), "Lixo Coletado", Integer.toString(i));
        }
        JFreeChart chart = ChartFactory.createLineChart(
                "Lixo Coletado ao Longo do Tempo",
                "Tempo (min)",
                "Lixo Coletado (t)",
                dataset
        );
        return new ChartPanel(chart);
    }

    public void registrarLixoColetado(double valor) {
        lixoColetadoPorTempo.add(valor);
    }

    public static void iniciar(Simulador sim) {
        if (instancia == null) {
            instancia = new VisualizacaoSimulacaoSwing(sim);
        }
    }

    public void encerrar() {
        simulacaoAtiva = false;
        if (animacaoThread != null) {
            animacaoThread.interrupt();
        }
        dispose();
    }

    public static void informarLixoColetado(double valor) {
        if (instancia != null) {
            instancia.lixoColetadoPorTempo.add(valor);
        }
    }
} 