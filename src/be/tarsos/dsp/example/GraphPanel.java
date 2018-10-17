package be.tarsos.dsp.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Rodrigo
 */
public class GraphPanel extends JPanel {

    private int width = 800;
    private int heigth = 400;
    private int padding = 25;
    private int labelPadding = 25;
    private Color lineColor = new Color(44, 102, 230, 180);
    private Color pointColor = new Color(100, 100, 100, 180);
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 4;
    private int numberYDivisions = 10;
    private List<Double> scores = new ArrayList();
    
    private float lowerBand = 0;
    private float upperBand = 0;
    
    private float upperSpacing = 0;
    private float lowerSpacing = 0;

    public float getUpperSpacing() {
		return upperSpacing;
	}

	public void setUpperSpacing(float upperSpacing) {
		this.upperSpacing = upperSpacing;
	}

	public float getLowerSpacing() {
		return lowerSpacing;
	}

	public void setLowerSpacing(float lowerSpacing) {
		this.lowerSpacing = lowerSpacing;
	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (scores.size() - 1);
        double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (50 - 1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxScore() - getMinScore());

        List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            int x1 = (int) (i * xScale + padding + labelPadding);
            int y1 = (int) ((getMaxScore() - scores.get(i)) * yScale + padding);
            graphPoints.add(new Point(x1, y1));
        }

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            int y1 = y0;
            if (scores.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = ((int) ((getMinScore() + (getMaxScore() - getMinScore()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // and for x axis
        for (int i = 0; i < 50; i++) {
            if (scores.size() > 1) {
                int x0 = i * (getWidth() - padding * 2 - labelPadding) / (50 - 1) + padding + labelPadding;
                int x1 = x0;
                int y0 = getHeight() - padding - labelPadding;
                int y1 = y0 - pointWidth;
                if ((i % ((int) ((50 / 20.0)) + 1)) == 0) {
                    g2.setColor(gridColor);
                    g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                    g2.setColor(Color.BLACK);
                    String xLabel = i + "";
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(xLabel);
                    g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                }
                g2.drawLine(x0, y0, x1, y1);
            }
        }

        // create x and y axes 
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);
        
        //Draw Upper and Lower Band
        if(lowerBand != 0 || upperBand != 0){
	        float yLow = getHeight() - ((lowerBand * 10 * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
	        float yUp = getHeight() - ((upperBand * 10 * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
	        g2.setStroke(new BasicStroke(3f));
	        g2.setColor(Color.RED);
	        g2.drawLine(padding + labelPadding, Math.round(yLow), getWidth() - padding, Math.round(yLow));
	        g2.setColor(Color.GREEN);
	        g2.drawLine(padding + labelPadding, Math.round(yUp), getWidth() - padding, Math.round(yUp));
	        
	        float ySpacingLow = getHeight() - (((lowerBand + lowerSpacing) * 10 * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
	        float ySpacingUp = getHeight() - (((upperBand - upperSpacing) * 10 * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
	        g2.setStroke(new BasicStroke(3f));
	        g2.setColor(new Color(255, 0, 0, 64));
	        g2.drawLine(padding + labelPadding, Math.round(ySpacingLow), getWidth() - padding, Math.round(ySpacingLow));
	        g2.setColor(new Color(0, 255, 0, 64));
	        g2.drawLine(padding + labelPadding, Math.round(ySpacingUp), getWidth() - padding, Math.round(ySpacingUp));
        }
        
        Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(pointColor);
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x - pointWidth / 2;
            int y = graphPoints.get(i).y - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }

//    @Override
//    public Dimension getPreferredSize() {
//        return new Dimension(width, heigth);
//    }

    private double getMinScore() {
        double minScore = Double.MAX_VALUE;
        for (Double score : scores) {
            minScore = Math.min(minScore, score);
        }
        return 0;
    }

    private double getMaxScore() {
        double maxScore = Double.MIN_VALUE;
        for (Double score : scores) {
            maxScore = Math.max(maxScore, score);
        }
        return 100;
    }

    public void addScore(double score) {
    	scores.add(score);
    	if(scores.size() > 50){
    		scores.remove(0);
    	}
        invalidate();
        this.repaint();
    }

    public List<Double> getScores() {
        return scores;
    }
    
    public float getLowerBand() {
		return lowerBand;
	}

	public void setLowerBand(float lowerBand) {
		this.lowerBand = lowerBand;
        invalidate();
        this.repaint();
	}

	public float getUpperBand() {
		return upperBand;
	}

	public void setUpperBand(float upperBand) {
		this.upperBand = upperBand;
        invalidate();
        this.repaint();
	}

	public static GraphPanel createAndShowGui() {
        List<Double> scores = new ArrayList<>();
        Random random = new Random();
        int maxDataPoints = 40;
        int maxScore = 10;
        for (int i = 0; i < maxDataPoints; i++) {
            scores.add((double) random.nextDouble() * maxScore);
//            scores.add((double) i);
        }
        GraphPanel mainPanel = new GraphPanel();
        mainPanel.setSize(new Dimension(800, 600));
        return mainPanel;
    }
    
}
