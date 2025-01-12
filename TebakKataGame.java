import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

// Abstract class Game sebagai dasar untuk semua jenis permainan
abstract class Game {
    protected int skor;
    protected int waktuTersisa;
    protected String kataRahasia;
    protected String kategori;
    protected String petunjuk;

    public abstract void startGame();
    public abstract void checkAnswer(String jawaban);
    public abstract String generateHint();
}

// Kelas TebakKataGame yang mewarisi dari Game
public class TebakKataGame extends Game {
    private JFrame frame;
    private JLabel labelJudul, labelKategori, labelPetunjuk, labelHasil, labelSkor, labelTimer;
    private JTextField textFieldInput;
    private JButton buttonMulai, buttonCek, buttonReset;
    private Timer timer;

    public TebakKataGame() {
        frame = new JFrame("🔥 Tebak Kata Game 🔥");
        frame.setSize(700, 500);  // Ukuran jendela tetap kecil
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Menggunakan GridBagLayout untuk semua elemen agar selalu di tengah
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Margin antar elemen

        // Judul
        labelJudul = new JLabel("🔥 Tebak Kata Game 🔥");
        labelJudul.setFont(new Font("Poppins", Font.BOLD, 24));
        labelJudul.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3; // Judul akan menempati 3 kolom
        frame.add(labelJudul, gbc);

        // Kategori dan Timer
        labelKategori = new JLabel("Kategori: 📚 Belum dimulai");
        labelTimer = new JLabel("⏳ Waktu: 30 detik");
        labelKategori.setFont(new Font("Poppins", Font.PLAIN, 16));
        labelTimer.setFont(new Font("Poppins", Font.PLAIN, 16));
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(labelKategori, gbc);

        gbc.gridx = 2; // Timer di kanan
        frame.add(labelTimer, gbc);

        // Petunjuk
        labelPetunjuk = new JLabel("💡 Petunjuk: Tekan 'Mulai' untuk memulai permainan");
        labelPetunjuk.setFont(new Font("Poppins", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        frame.add(labelPetunjuk, gbc);

        // Input
        textFieldInput = new JTextField(15);
        textFieldInput.setFont(new Font("Poppins", Font.PLAIN, 16));
        textFieldInput.setHorizontalAlignment(JTextField.CENTER);
        textFieldInput.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        frame.add(textFieldInput, gbc);

        // Hasil
        labelHasil = new JLabel("");
        labelHasil.setFont(new Font("Poppins", Font.BOLD, 18));
        gbc.gridy = 4;
        frame.add(labelHasil, gbc);

        // Skor
        labelSkor = new JLabel("Skor: -");
        labelSkor.setFont(new Font("Poppins", Font.PLAIN, 16));
        gbc.gridy = 5;
        frame.add(labelSkor, gbc);

        // Tombol
        JPanel panelTombol = new JPanel();
        panelTombol.setLayout(new GridLayout(1, 3, 10, 0));
        buttonMulai = new JButton("🚀 Mulai");
        buttonCek = new JButton("✔ Cek");
        buttonReset = new JButton("🔄 Reset");
        panelTombol.add(buttonMulai);
        panelTombol.add(buttonCek);
        panelTombol.add(buttonReset);

        buttonCek.setEnabled(false);
        buttonReset.setEnabled(false);

        gbc.gridy = 6;
        gbc.gridwidth = 3;
        frame.add(panelTombol, gbc);

        // Tombol Aksi
        buttonMulai.addActionListener(e -> startGame());
        buttonCek.addActionListener(e -> checkAnswer(textFieldInput.getText().trim().toLowerCase()));
        buttonReset.addActionListener(e -> resetGame());

        frame.setVisible(true);
    }

    @Override
    public void startGame() {
        String[][] dataKategori = {
                {"Buah", "apel", "mangga", "pisang", "jeruk", "anggur"},
                {"Negara", "indonesia", "jepang", "kanada", "italia", "mesir"},
                {"Hewan", "harimau", "kucing", "burung", "ikan", "gajah"}
        };

        Random random = new Random();
        int kategoriIndex = random.nextInt(dataKategori.length);
        kategori = dataKategori[kategoriIndex][0];
        kataRahasia = dataKategori[kategoriIndex][random.nextInt(dataKategori[kategoriIndex].length - 1) + 1];
        skor = 100;
        waktuTersisa = 30;

        labelKategori.setText("Kategori: 📚 " + kategori);
        labelPetunjuk.setText("💡 Petunjuk: " + generateHint());
        labelHasil.setText("");
        labelSkor.setText("Skor: " + skor);
        labelTimer.setText("⏳ Waktu: 30 detik");
        textFieldInput.setText("");
        textFieldInput.setEnabled(true);

        buttonMulai.setEnabled(false);
        buttonCek.setEnabled(true);
        buttonReset.setEnabled(true);

        startTimer();
    }

    @Override
    public void checkAnswer(String jawaban) {
        if (jawaban.equals(kataRahasia)) {
            labelHasil.setText("🎉 Benar! Skor Anda: " + skor);
            labelHasil.setForeground(Color.GREEN);
            textFieldInput.setEnabled(false);
            timer.cancel();
        } else {
            skor -= 20;
            if (skor <= 0) {
                labelHasil.setText("💀 Game Over! Kata yang benar: " + kataRahasia);
                labelHasil.setForeground(Color.RED);
                textFieldInput.setEnabled(false);
                timer.cancel();
            } else {
                labelPetunjuk.setText("💡 Petunjuk: " + generateHint());
                labelHasil.setText("❌ Salah! Skor Anda: " + skor);
                labelHasil.setForeground(Color.ORANGE);
                labelSkor.setText("Skor: " + skor);
            }
        }
    }

    @Override
    public String generateHint() {
        char[] hintArray = kataRahasia.toCharArray();
        for (int i = 0; i < hintArray.length; i++) {
            if (Math.random() > 0.5) {
                hintArray[i] = '_';
            }
        }
        return new String(hintArray);
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                waktuTersisa--;
                labelTimer.setText("⏳ Waktu: " + waktuTersisa + " detik");
                if (waktuTersisa <= 0) {
                    timer.cancel();
                    labelHasil.setText("💔 Waktu habis! Anda kalah.");
                    labelHasil.setForeground(Color.RED);
                    textFieldInput.setEnabled(false);
                }
            }
        }, 1000, 1000);
    }

    private void resetGame() {
        if (timer != null) {
            timer.cancel();
        }
        buttonMulai.setEnabled(true);
        buttonCek.setEnabled(false);
        textFieldInput.setEnabled(false);
        buttonReset.setEnabled(false);

        labelKategori.setText("Kategori: 📚 Belum dimulai");
        labelPetunjuk.setText("💡 Petunjuk: Tekan 'Mulai' untuk memulai permainan");
        labelHasil.setText("");
        labelSkor.setText("Skor: -");
        labelTimer.setText("⏳ Waktu: 30 detik");
    }

    public static void main(String[] args) {
        new TebakKataGame();
    }
}
