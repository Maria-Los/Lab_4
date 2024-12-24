package bsu.rfe.java.group7.lab4.Los.A10;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import javax.swing.*;


public class GraphicsDisplay extends JPanel
{
    // Массив данных графика
    private Double[][] graphicsData;

    // Флаг для отображения осей
    private boolean showAxis = true;

    // Флаг для отображения маркеров
    private boolean showMarkers = true;

    // Флаг для отображения модуля функции |f(x)|
    private boolean showAbsFunction = false;

    private double minX; // Минимальное значение X

    private double maxX; // Максимальное значение X

    private double minY; // Минимальное значение Y

    private double maxY; // Максимальное значение Y

    private double scale; // Масштаб для отображения графика


    private final BasicStroke graphicsStroke; // Стиль линии графика

    private final BasicStroke axisStroke; // Стиль линии осей

    private final BasicStroke markerStroke; // Стиль линий маркеров

    private final Font axisFont; // Шрифт для подписей осей

    // Конструктор, инициализирующий параметры отображения
    public GraphicsDisplay()
    {
        setBackground(Color.WHITE); // Устанавливаем белый фон панели

        // Инициализация стилей отображения
        // Жирный пунктир
        graphicsStroke = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[]{10, 10}, 0.0f); // Жирный пунктир

        // Линии осей
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

        // Тонкие линии маркеров
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

        // Шрифт для осей
        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    // Устанавливает данные графика для отображения
    public void showGraphics(Double[][] graphicsData)
    {
        this.graphicsData = graphicsData; // Сохраняем данные графика
        repaint(); // Обновляем изображение
    }

    // Устанавливает флаг отображения осей
    public void setShowAxis(boolean showAxis)
    {
        this.showAxis = showAxis; // Устанавливаем флаг
        repaint(); // Перерисовываем изображение
    }

    // Устанавливает флаг отображения маркеров
    public void setShowMarkers(boolean showMarkers)
    {
        this.showMarkers = showMarkers; // Устанавливаем флаг
        repaint(); // Перерисовываем изображение
    }

    // Устанавливает флаг отображения модуля функции
    public void setShowAbsFunction(boolean showAbsFunction)
    {
        this.showAbsFunction = showAbsFunction; // Устанавливаем флаг
        repaint(); // Перерисовываем изображение
    }

    // Отрисовка графиков, осей и маркеров
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (graphicsData == null || graphicsData.length == 0) return; // Если данных нет, ничего не рисуем

        // Вычисляем минимальные и максимальные значения X и Y
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        minY = graphicsData[0][1];
        maxY = minY;

        // Находим экстремумы Y среди всех точек
        for (int i = 1; i < graphicsData.length; i++)
        {
            if (graphicsData[i][1] < minY) minY = graphicsData[i][1];
            if (graphicsData[i][1] > maxY) maxY = graphicsData[i][1];
        }

        // Вычисляем масштаб графика по оси X и Y
        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);
        scale = Math.min(scaleX, scaleY); // Выбираем минимальный масштаб

        // Корректируем границы, чтобы график помещался на панели
        if (scale == scaleX)
        {
            double yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;
            maxY += yIncrement;
            minY -= yIncrement;
        }

        if (scale == scaleY)
        {
            double xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 2;
            maxX += xIncrement;
            minX -= xIncrement;
        }

        Graphics2D canvas = (Graphics2D) g; // Приводим контекст к Graphics2D для расширенных возможностей

        // Сохраняем исходные параметры графического контекста
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (showAxis)
        {
            paintAxis(canvas); // Рисуем оси, если включено
        }

        paintGraphics(canvas); // Рисуем график функции

        if (showMarkers)
        {
            paintMarkers(canvas);// Рисуем маркеры, если включено
        }

        // Восстанавливаем исходные параметры графического контекста
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    protected void paintGraphics(Graphics2D canvas)
    {
        // Рисуем основной график f(x)
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.BLACK);
        GeneralPath graphics = new GeneralPath();

        for (int i = 0; i < graphicsData.length; i++)
        {
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            if (i > 0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }

        canvas.draw(graphics);

        // Рисуем график модуля функции |f(x)|, если включен соответствующий флаг
        if (showAbsFunction) {
            canvas.setColor(Color.BLUE); // Цвет для модуля функции
            GeneralPath absGraphics = new GeneralPath();
            for (int i = 0; i < graphicsData.length; i++) {
                Point2D.Double point = xyToPoint(graphicsData[i][0], Math.abs(graphicsData[i][1]));
                if (i > 0) {
                    absGraphics.lineTo(point.getX(), point.getY());
                } else {
                    absGraphics.moveTo(point.getX(), point.getY());
                }
            }
            canvas.draw(absGraphics);
        }
    }

    protected void paintMarkers(Graphics2D canvas)
    {
        // Настройка стиля маркеров
        canvas.setStroke(markerStroke);

        for (Double[] point : graphicsData)
        {
            Point2D.Double center = xyToPoint(point[0], point[1]);
            double yValue = point[1];
            int integerPart = (int) Math.floor(yValue);
            int sqrt = (int) Math.sqrt(integerPart);

            // Проверяем, является ли целая часть квадратом целого числа
            if (sqrt * sqrt == integerPart)
            {
                canvas.setColor(Color.GREEN); // Цвет для особых точек
            }
            else
            {
                canvas.setColor(Color.RED);
            }

            // Рисуем треугольник
            GeneralPath triangle = new GeneralPath();
            triangle.moveTo(center.getX(), center.getY() - 5.5);
            triangle.lineTo(center.getX() - 5.5, center.getY() + 5.5);
            triangle.lineTo(center.getX() + 5.5, center.getY() + 5.5);
            triangle.closePath();

            canvas.draw(triangle);  // Контур треугольника
            canvas.fill(triangle); // Заливка треугольника
        }
    }

    protected void paintAxis(Graphics2D canvas)
    {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);

        FontRenderContext context = canvas.getFontRenderContext();

        if (minX <= 0.0 && maxX >= 0.0)
        {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            canvas.drawString("y", (float) labelPos.getX() + 10, (float) (labelPos.getY() - bounds.getY()));
        }

        if (minY <= 0.0 && maxY >= 0.0)
        {
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            canvas.drawString("x", (float) (labelPos.getX() - bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));
        }
    }


    protected Point2D.Double xyToPoint(double x, double y)
    {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX * scale, deltaY * scale);
    }
}