import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.PrintStream;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dakota on 7/10/2016.
 */
public class Gui extends JFrame {

    public Gui(StatusListener listener) {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.setTitle("Twitter Bot Controls");

        JLabel runtime = new JLabel();
        final long[] startTime = {System.currentTimeMillis()};

        final JPanel base = new JPanel(new BorderLayout(3, 3));
        final JPanel statusLog = new JPanel(new FlowLayout());
        final JPanel botControls = new JPanel();
        botControls.setLayout(new BoxLayout(botControls, BoxLayout.Y_AXIS));

        // Status Log
        JTextArea txtConsole = new JTextArea(10, 50);
        txtConsole.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        txtConsole.setBackground(new Color(0, 0, 170));
        txtConsole.setForeground(Color.yellow);
        DefaultCaret caret = (DefaultCaret) txtConsole.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(txtConsole, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        PrintStream consoleOutput = new PrintStream(new TextAreaOutputStream(txtConsole));
        System.setOut(consoleOutput);

        statusLog.add(scrollPane);

        // Bot Controls

        JButton btnStart = new JButton("Start");
        JButton btnPause = new JButton("Pause");
        JButton btnClearConsole = new JButton("Clear Console");

        final boolean[] streamPaused = {false};
        final boolean[] streamStopped = {false};
        TwitterStream stream = new TwitterStreamFactory().getInstance();

        btnStart.addActionListener(e -> {
            Date date = new Date();
            if (!streamStopped[0]) {
                streamStopped[0] = false;
                stream.addListener(listener);


                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runtime.setText(getRuntime(startTime[0]));
                        botControls.revalidate();
                        botControls.repaint();
                    }
                }, 1000, 1000);

                btnStart.setText("Start");
                startTime[0] = System.currentTimeMillis();

                System.out.println("[" + date + "]Bot started...");
                stream.sample();
            } else {
                streamStopped[0] = true;
                stream.clearListeners();
                stream.cleanUp();
                stream.shutdown();
                btnStart.setText("Stop");

                System.out.println("[" + date + "]Bot stopped...");
            }

            botControls.revalidate();
            botControls.repaint();
        });

        btnPause.addActionListener(e -> {
            Date date = new Date();
            stream.clearListeners();
            if (streamPaused[0]) {
                streamPaused[0] = false;
                btnPause.setText("Pause");

                stream.addListener(listener);
                System.out.println("[" + date + "]Resuming bot...");
            } else {
                streamPaused[0] = true;
                btnPause.setText("Resume");

                System.out.println("[" + date + "]Pausing bot...");
            }

            botControls.revalidate();
            botControls.repaint();
        });

        btnClearConsole.addActionListener(e -> txtConsole.setText(""));

        botControls.add(runtime);
        botControls.add(btnStart);
        botControls.add(btnPause);
        botControls.add(btnClearConsole);

        base.add(statusLog, BorderLayout.PAGE_END);
        base.add(botControls, BorderLayout.LINE_START);
        this.add(base);
        this.pack();
    }

    private String getRuntime(long startTime) {
        long runtimeMills = System.currentTimeMillis() - startTime;

        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(runtimeMills),
                TimeUnit.MILLISECONDS.toMinutes(runtimeMills) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(runtimeMills)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(runtimeMills) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(runtimeMills)));
    }
}
