import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class TetrisGame extends JPanel {
    private final int TILE_SIZE = 30;  // 1マスのサイズ
    private final int WIDTH = 10, HEIGHT = 20;  // フィールドサイズ
    private int[][] field = new int[HEIGHT][WIDTH]; // 10×20のフィールド
    private int[][] currentTetromino;  // 現在のテトリミノ
    private int currentX = 4, currentY = 0;  // ブロックの位置
    private Timer timer;  // 自動落下用タイマー

    public TetrisGame() {
        // 最初のブロックを生成
        generateNewTetromino();
        
        // タイマーを設定（500msごとに落下）
        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveDown();
                repaint();
            }
        });
        timer.start();

        // キーイベントリスナーの追加
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT) {
                    moveLeft();
                } else if (key == KeyEvent.VK_RIGHT) {
                    moveRight();
                } else if (key == KeyEvent.VK_DOWN) {
                    moveDown();
                } else if (key == KeyEvent.VK_UP) {
                    rotate();  // 回転処理
                }
                repaint();  // 再描画
            }
        });
        setFocusable(true);  // フォーカスを取得してキー入力を受け取れるようにする
    }
    // 新しいテトリミノを生成
    private void generateNewTetromino() {
        Random rand = new Random();
    int type = rand.nextInt(5); // 0〜4のランダムな数値を生成（5種類のテトリミノ）

    switch (type) {
        case 0: // 正方形
            currentTetromino = new int[][] {
                {1, 1},
                {1, 1}
            };
            break;
        case 1: // L字
            currentTetromino = new int[][] {
                {0, 1},
                {0, 1},
                {1, 1}
            };
            break;
        case 2: // 逆L字
            currentTetromino = new int[][] {
                {1, 0},
                {1, 0},
                {1, 1}
            };
            break;
        case 3: // T字
            currentTetromino = new int[][] {
                {1, 1, 1},
                {0, 1, 0}
            };
            break;
        case 4: // I字
            currentTetromino = new int[][] {
                {1},
                {1},
                {1},
                {1}
            };
            break;
    }

    currentX = 4;
    currentY = 0;

    if (!canMove(currentTetromino, currentX, currentY)) {
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over!");
        System.exit(0);
    }

    drawTetromino();
}

     // 左移動
    private void moveLeft() {
        if (currentX > 0) {
            clearTetromino();  // 現在のテトリミノを消す
            currentX--;
            drawTetromino();  // 新しい位置にテトリミノを描く
        }
    }

    // 右移動
    private void moveRight() {
        if (currentX < WIDTH - currentTetromino[0].length) {
            clearTetromino();
            currentX++;
            drawTetromino();
        }
    }

    // 下移動
    private void moveDown() {
        if (canMove(currentTetromino, currentX, currentY + 1)) {
            clearTetromino();
            currentY++;
            drawTetromino();
        } else {
          // これ以上下に移動できない → フィールドに固定
            fixTetromino();
            generateNewTetromino();
        }
    }

    private void fixTetromino() {
        for (int y = 0; y < currentTetromino.length; y++) {
            for (int x = 0; x < currentTetromino[0].length; x++) {
                if (currentTetromino[y][x] != 0) {
                    field[currentY + y][currentX + x] = 1; // フィールドに固定
                }
            }
        }
    }
    

    // 回転処理
    private void rotate() {
        int[][] rotated = rotateTetromino(currentTetromino);
        if (canMove(rotated, currentX, currentY)) {
            clearTetromino();
            currentTetromino = rotated;  // 回転後のテトリミノを設定
            drawTetromino();
        }
    }

    // テトリミノを回転させる
    private int[][] rotateTetromino(int[][] tetromino) {
        int size = tetromino.length;
        int[][] rotated = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                rotated[j][size - 1 - i] = tetromino[i][j];
            }
        }
        return rotated;
    }

    private boolean canMove(int[][] tetromino, int offsetX, int offsetY) {
        for (int y = 0; y < tetromino.length; y++) {
            for (int x = 0; x < tetromino[0].length; x++) {
                if (tetromino[y][x] != 0) {
                    int newX = offsetX + x;
                    int newY = offsetY + y;
                    
                    // フィールドの範囲外 or 他のブロックに衝突したら移動できない
                    if (newX < 0 || newX >= WIDTH || newY < 0 || newY >= HEIGHT || field[newY][newX] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    

    // 現在のテトリミノをフィールドに描画
    private void drawTetromino() {
        for (int y = 0; y < currentTetromino.length; y++) {
            for (int x = 0; x < currentTetromino[0].length; x++) {
                if (currentTetromino[y][x] != 0) {
                    field[currentY + y][currentX + x] = 1;
                }
            }
        }
    }

    // テトリミノをフィールドから消去
    private void clearTetromino() {
        for (int y = 0; y < currentTetromino.length; y++) {
            for (int x = 0; x < currentTetromino[0].length; x++) {
                if (currentTetromino[y][x] != 0) {
                    field[currentY + y][currentX + x] = 0;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (field[y][x] == 1) {
                    g.setColor(Color.BLUE);  // ブロックの色
                    g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
                g.setColor(Color.GRAY);  // マス目の枠
                g.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        TetrisGame game = new TetrisGame();
        frame.add(game);
        frame.setSize(320, 640);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}