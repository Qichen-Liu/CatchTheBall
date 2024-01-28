package project;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Random;
import javax.swing.*;
import javax.sound.sampled.*;


public class CatchTheBall extends JPanel implements ActionListener {
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int BALL_DIAMETER = 30;
    private final int PADDLE_WIDTH = 80;
    private final int PADDLE_HEIGHT = 10;
    private final int paddle_Y = HEIGHT - PADDLE_HEIGHT - 10;
    private int ballX;
    private int ballY;
    private double ballSpeedY = 6.0;
    private double ballSpeedX = 2.0;
    private int paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
    private boolean gameOver = false;
    private Timer timer;
    private int score;
    private static int record;
    private Clip clip;
    
    public CatchTheBall() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
        score = 0;
    }

    public void startGame() {

        Random random = new Random();
        ballX = random.nextInt(WIDTH / 2);
        ballY = random.nextInt(HEIGHT / 2);
        paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
        gameOver = false;
        timer = new Timer(20, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (!gameOver) {
            g.setColor(Color.white);
            g.fillOval(ballX, ballY, BALL_DIAMETER, BALL_DIAMETER);
            g.fillRect(paddleX, paddle_Y, PADDLE_WIDTH, PADDLE_HEIGHT);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Current Score: " + score, 450, 30);
            g.drawString("Highest Score: " + record, 450, 50);

        } else {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("GAME OVER", WIDTH / 2 - 120, HEIGHT / 2 - 50);
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.drawString("Your Final Score: " + score, WIDTH / 2 - 110, HEIGHT / 2);

            if (score > record){
                record = score;
            }
            g.drawString("Highest Score: " + record, WIDTH / 2 - 90, HEIGHT / 2 + 50);

            score = 0;

            JButton restartButton = new JButton("Restart");
            restartButton.setLocation(WIDTH / 2 - 65, HEIGHT / 2 + 80);
            restartButton.setSize(100, 50);
            restartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFrame frame = new JFrame("Catch the Ball");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setContentPane(new CatchTheBall());
                    frame.pack();
                    frame.setVisible(true);
                }
            });
            add(restartButton);
        }
    }

    public void moveBall() {

        ballY += ballSpeedY;
        ballX += ballSpeedX;

        // handle collision
        if ((new Rectangle(ballX, ballY, BALL_DIAMETER, BALL_DIAMETER).intersects(new Rectangle(paddleX, paddle_Y, PADDLE_WIDTH, PADDLE_HEIGHT)))) {
            ballSpeedY = -ballSpeedY;
            
            
            try {
                clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("src/project/bounce.wav"));
                clip.open(inputStream);
                clip.setFramePosition(0);
                clip.start();
                System.out.println("sound!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            

            // pick a random direction
            Random random = new Random();
            if(random.nextInt(2) == 1){
                ballSpeedX = -ballSpeedX;
            }

            score++;

            // speed up if score is a multiple of 2
            if(score > 0 && score % 2 == 0){
                if (ballSpeedY > 0){
                    ballSpeedY++;
                }else {
                    ballSpeedY--;
                }
            }

        } else if (ballY > HEIGHT - BALL_DIAMETER) {
            gameOver = true;
            timer.stop();
        }

        // check if ball hits top of screen
        if (ballY <= 0) {
            ballSpeedY = -ballSpeedY;
        }

        // check if ball hits side walls
        if (ballX <= 0 || ballX + BALL_DIAMETER >= getWidth()) {
            ballSpeedX = -ballSpeedX;
        }
    }

    public void movePaddle(int direction) {
        if (direction == -1) {
            paddleX -= 35;
            if (paddleX < 0) {
                paddleX = 0;
            }
        } else if (direction == 1) {
            paddleX += 35;
            if (paddleX > WIDTH - PADDLE_WIDTH) {
                paddleX = WIDTH - PADDLE_WIDTH;
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        moveBall();
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT) {
                movePaddle(-1);
            } else if (key == KeyEvent.VK_RIGHT) {
                movePaddle(1);
            }
        }
    }

    public static void main(String[] args) {
        int width = 600;
        int height = 600;
        JFrame frame = new JFrame("Catch the Ball Game");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setSize(600, 600);
        panel.setLocation(300, 300);
        panel.setLayout(null);

        JLabel label0 = new JLabel("Rules:");
        label0.setFont(new Font("Arial", Font.PLAIN, 16));
        label0.setSize(600, 50);
        label0.setLocation(110, 170);

        JLabel label1 = new JLabel("You need to catch the ball as many times as you can.");
        label1.setFont(new Font("Arial", Font.PLAIN, 16));
        label1.setSize(600, 50);
        label1.setLocation(110, 200);

        JLabel label2 = new JLabel("The fallen speed will get faster when you earn more scores.");
        label2.setFont(new Font("Arial", Font.PLAIN, 16));
        label2.setSize(600, 50);
        label2.setLocation(110, 230);

        JLabel label3 = new JLabel("Press direction keys on your keyboard to move the paddle.");
        label3.setFont(new Font("Arial", Font.PLAIN, 16));
        label3.setSize(600, 50);
        label3.setLocation(110, 260);

        JLabel label4 = new JLabel("Your highest score will be kept and displayed.");
        label4.setFont(new Font("Arial", Font.PLAIN, 16));
        label4.setSize(600, 50);
        label4.setLocation(110, 290);

        JButton button = new JButton("START GAME");
        button.setBounds(230, 360, 150, 30);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Catch the Ball");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(new CatchTheBall());
                frame.pack();
                frame.setVisible(true);
            }
        });
        panel.add(label0);
        panel.add(label1);
        panel.add(label2);
        panel.add(label3);
        panel.add(label4);
        panel.add(button);
        frame.add(panel);
        frame.setVisible(true);
    }
}
