import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class SimplePaint extends JFrame {
    private JMenuBar menuBar;
    private JMenu menuItemCircle, menuItemSquare, menuItemEraser, menuItemClear, menuItemColor, menuItemLine, menuItemDottedLine;
    private JPanel drawPanel;
    private Color currentColor = Color.BLACK;
    private String selectedShape = "Circle";
    private int eraserSize = 20;
    private BufferedImage canvas, tempImage;
    private float initialX, initialY;
    private Graphics2D graphics2D, tempGraphics2D;
    private final String IMAGES_PATH = "src/images/";
    private boolean eraserMode = false;
    private float[][] patterns;
    private int currentPattern = 0;

    private double rotationAngle = 0; // Ángulo de rotación actual
    private boolean isRotating = false; // Indica si estamos en modo rotación

    public SimplePaint() {
        setTitle("Pain't");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initialX = 0;
        initialY = 0;
        patterns = new float[][]{{5, 5}, {10, 10}, {20, 20}};
        canvas = new BufferedImage(800, 500, BufferedImage.TYPE_INT_ARGB);
        tempImage = new BufferedImage(800, 500, BufferedImage.TYPE_INT_ARGB);
        drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(canvas, 0, 0, null);
                g.drawImage(tempImage, 0, 0, null);
            }
        };
        drawPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialX = e.getX();
                initialY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Al soltar el mouse, fusionar la imagen temporal con la principal
                graphics2D = (Graphics2D) canvas.getGraphics();
                graphics2D.drawImage(tempImage, 0, 0, null); // Copia la figura final al canvas principal
                clearTempImage(); // Limpiar la imagen temporal después de soltar
                drawPanel.repaint();
            }
        });

        drawPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (eraserMode) {
                    drawShape((int) initialX, (int) initialY, e.getX(), e.getY());
                    drawFinalShape(e.getX(), e.getY());
                    return;
                }
                clearTempImage();
                drawShape((int) initialX, (int) initialY, e.getX(), e.getY());
                drawPanel.repaint();
            }
        });

        // Crear menú y agregar ítems
        menuBar = new JMenuBar();
        menuBar.setSize(800, 30);


        // Crear ítems del menú con íconos
        menuItemCircle = createMenu("Círculo", IMAGES_PATH + "circulo.png");
        menuItemSquare = createMenu("Cuadrado", IMAGES_PATH + "cuadrado.png");
        menuItemEraser = createMenu("Borrador", IMAGES_PATH + "eraser.png");
        menuItemClear = createMenu("Limpiar", IMAGES_PATH + "trash.png");
        menuItemLine = createMenu("Linea", IMAGES_PATH + "linea.png");
        menuItemDottedLine = createMenu("Linea", IMAGES_PATH + "linea-discontinua.png");
        menuItemColor = createMenu("Color", IMAGES_PATH + "paleta-de-color.png");

        // Agregar eventos a los ítems del menú
        menuItemCircle.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleShapeMousePressed("Circle");
            }

        });
        menuItemSquare.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleShapeMousePressed("Square");
            }
        });
        menuItemLine.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleShapeMousePressed("Line");
            }
        });
        menuItemDottedLine.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleShapeMousePressed("DottedLine");
            }
        });
        menuItemEraser.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                eraserMode = !eraserMode;
                if (eraserMode) {
                    currentColor = Color.WHITE;
                } else {
                    currentColor = Color.BLACK;
                }
                setCursor(new Cursor(eraserMode ? Cursor.DEFAULT_CURSOR : Cursor.CROSSHAIR_CURSOR));
            }
        });
        menuItemColor.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                chooseColor();
            }
        });

        menuItemClear.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clearCanvas();
            }
        });
        menuBar.add(menuItemCircle);
        menuBar.add(menuItemSquare);
        menuBar.add(menuItemLine);
        menuBar.add(menuItemDottedLine);
        menuBar.add(menuItemEraser);
        menuBar.add(menuItemClear);
        menuBar.add(menuItemColor);


        // Establecer la barra de menú
        setJMenuBar(menuBar);

        // Añadir panel de dibujo al marco
        add(drawPanel, BorderLayout.CENTER);

        // Añadir el KeyListener para la rotación
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (eraserMode) {
                    if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_ADD || e.getKeyCode() == 521)) {
                        eraserSize += 10;
                    } else if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_SUBTRACT || e.getKeyCode() == 45)) {
                        eraserSize = Math.max(5, eraserSize - 10);
                    }
                }

                // Tecla R para rotar
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    rotationAngle += Math.PI / 4; // Rota 45 grados (π/4 radianes)
                    isRotating = true;
                    drawPanel.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    isRotating = false;
                }
            }
        });

    }


    private void handleShapeMousePressed(String shape) {
        selectedShape = shape;
        eraserMode = false;
        if (currentColor == Color.WHITE)
            currentColor = Color.BLACK;
        if (Objects.equals(shape, "Circle") || Objects.equals(shape, "Square")) {
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        if (Objects.equals(shape, "DottedLine")) {
            currentPattern = JOptionPane.showOptionDialog(this, "Escoge un patrón", "Patron de linea",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"5, 5", "10, 10", "20, 20"}, 0);
        }
        System.out.println(shape);
    }

    private JMenu createMenu(String text, String iconPath) {
        JMenu menu = new JMenu(text);
        ImageIcon icon = new ImageIcon(iconPath);
        menu.setIcon(icon);
        return menu;
    }

    // Modificar el método drawShape para incluir la rotación
    private void drawShape(int startX, int startY, int endX, int endY) {
        tempGraphics2D = (Graphics2D) tempImage.getGraphics();
        tempGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (eraserMode) {
            tempGraphics2D.setColor(Color.WHITE);
            tempGraphics2D.fillRect(endX - eraserSize / 2, endY - eraserSize / 2, eraserSize, eraserSize);
            return;
        }

        // Guardar la transformación original
        AffineTransform originalTransform = tempGraphics2D.getTransform();

        tempGraphics2D.setColor(currentColor);
        int max = Math.max(Math.abs(endX - startX), Math.abs(endY - startY));
        int minX = Math.min(startX, endX);
        int minY = Math.min(startY, endY);

        // Aplicar la rotación alrededor del centro de la figura
        if (isRotating || rotationAngle != 0) {
            int centerX = minX + max / 2;
            int centerY = minY + max / 2;
            tempGraphics2D.rotate(rotationAngle, centerX, centerY);
        }

        // Dibujar la figura
        if (selectedShape.equals("Circle")) {
            tempGraphics2D.drawOval(minX, minY, max, max);
        } else if (selectedShape.equals("Square")) {
            tempGraphics2D.drawRect(minX, minY, max, max);
        } else if (selectedShape.equals("Line")) {
            tempGraphics2D.drawLine(startX, startY, endX, endY);
        } else if (selectedShape.equals("DottedLine")) {
            tempGraphics2D.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, patterns[currentPattern], 0));
            tempGraphics2D.drawLine(startX, startY, endX, endY);
        }

        // Restaurar la transformación original
        tempGraphics2D.setTransform(originalTransform);
    }


    // Limpiar el lienzo
    private void clearCanvas() {
        Graphics2D g2d = (Graphics2D) canvas.getGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawPanel.repaint();
    }

    // Modificar el método drawFinalShape para mantener la rotación
    private void drawFinalShape(int x, int y) {
        graphics2D = (Graphics2D) canvas.getGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Aplicar la misma rotación a la figura final
        if (rotationAngle != 0) {
            AffineTransform originalTransform = graphics2D.getTransform();
            int centerX = (int) (initialX + x) / 2;
            int centerY = (int) (initialY + y) / 2;
            graphics2D.rotate(rotationAngle, centerX, centerY);
            graphics2D.drawImage(tempImage, 0, 0, null);
            graphics2D.setTransform(originalTransform);
        } else {
            graphics2D.drawImage(tempImage, 0, 0, null);
        }

        // Resetear el ángulo de rotación después de dibujar la figura final
        rotationAngle = 0;
        tempImage = new BufferedImage(800, 500, BufferedImage.TYPE_INT_ARGB);
        drawPanel.repaint();
    }

    // Limpiar la imagen temporal
    private void clearTempImage() {
        Graphics2D g2d = (Graphics2D) tempImage.getGraphics();
        g2d.setComposite(AlphaComposite.Clear); // Usar transparencia para limpiar
        g2d.fillRect(0, 0, tempImage.getWidth(), tempImage.getHeight());
        g2d.setComposite(AlphaComposite.SrcOver); // Restaurar el modo de mezcla normal
    }

    // Seleccionar color
    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Elige un color", currentColor);
        if (newColor != null) {
            currentColor = newColor;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimplePaint paintApp = new SimplePaint();
            paintApp.setVisible(true);
        });
    }
}
