import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class SimplePaint extends JFrame {
    private JMenuBar menuBar;
    private JMenu menuItemCircle, menuItemSquare, menuItemEraser, menuItemClear, menuItemColor, menuItemLine, menuItemDottedLine;
    private JPanel drawPanel;
    private Color currentColor = Color.BLACK;
    private String selectedShape = "Circle";
    private BufferedImage canvas, tempImage;
    private float initialX, initialY;
    private Graphics2D graphics2D, tempGraphics2D;
    private final String IMAGES_PATH = "src/images/";
    private boolean eraserMode = false;

    public SimplePaint() {
        setTitle("Simple Paint");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initialX = 0;
        initialY = 0;

        // Crear área de dibujo
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

        // Agregar MouseListener y MouseMotionListener
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
                clearTempImage(); // Limpiar la imagen temporal en cada arrastre
                drawShape((int) initialX, (int) initialY, e.getX(), e.getY()); // Dibujar la figura en tempImage
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

        // Agregar eventos a los ítems del menú
        menuItemCircle.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedShape = "Circle";
                System.out.println("Circulo");
            }

        });
        menuItemSquare.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedShape = "Square";
                System.out.println("Cuadrado");
            }
        });
        menuItemLine.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedShape = "Line";
                System.out.println("Linea");
            }
        });
        menuItemDottedLine.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedShape = "DottedLine";
                System.out.println("Linea discontinua");
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


        // Establecer la barra de menú
        setJMenuBar(menuBar);

        // Añadir panel de dibujo al marco
        add(drawPanel, BorderLayout.CENTER);
    }

    private JMenu createMenu(String text, String iconPath) {
        JMenu menu = new JMenu(text);
        ImageIcon icon = new ImageIcon(iconPath);
        menu.setIcon(icon);
        return menu;
    }

    // Método para dibujar en la imagen temporal
    private void drawShape(int startX, int startY, int endX, int endY) {
        tempGraphics2D = (Graphics2D) tempImage.getGraphics(); // Dibujar en tempImage
        tempGraphics2D.setColor(currentColor);
        int max = Math.max(Math.abs(endX - startX), Math.abs(endY - startY));
        if(eraserMode){
            selectedShape = "";
            tempGraphics2D.setColor(Color.WHITE);
        }
        if (selectedShape.equals("Circle")) {
            int diameter = max;
            tempGraphics2D.drawOval(Math.min(startX, endX), Math.min(startY, endY), diameter, diameter);
        } else if (selectedShape.equals("Square")) {
            int side = max;
            tempGraphics2D.fillRect(Math.min(startX, endX), Math.min(startY, endY), side, side);
        } else if (selectedShape.equals("Line")) {
            tempGraphics2D.drawLine(startX, startY, endX, endY);
        } else if (selectedShape.equals("DottedLine")) {
            tempGraphics2D.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
            tempGraphics2D.drawLine(startX, startY, endX, endY);
        }else{
            tempGraphics2D.fillOval(getX(),getY(), endX, endY);
        }
    }


    // Limpiar el lienzo
    private void clearCanvas() {
        Graphics2D g2d = (Graphics2D) canvas.getGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawPanel.repaint();
    }
    private void drawFinalShape(int x, int y) {
        graphics2D = (Graphics2D) canvas.getGraphics();
        graphics2D.drawImage(tempImage, 0, 0, null);
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
