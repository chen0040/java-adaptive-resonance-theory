package com.github.chen0040.art.falcon.simulation.minefield.gui;

import com.github.chen0040.art.falcon.simulation.minefield.env.MineField;

import javax.swing.*;
import java.awt.*;

/**
 * Created by chen0469 on 10/2/2015 0002.
 */
public class BearingPanel extends JPanel {
    private int   currentBearing;
    private int   targetBearing;
    private double range;
    private int   radius;

    public BearingPanel(MineField m )
    {
        currentBearing = 0;
        targetBearing = 0;
        range = 0;
    }

    public void readBearing(int vehicleId, MineField m )
    {
        currentBearing = m.getCurrentBearing(vehicleId);
        targetBearing = m.getTargetBearing(vehicleId);
        repaint();
    }

    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.setColor( Color.blue );
        radius = Math.min( getHeight() / 2, getWidth() / 4 );
        g.fillOval( getWidth()/4-radius, getHeight()/2-radius, radius*2, radius*2 );
        g.setColor( Color.magenta );
        int cx = getWidth() / 4;
        int cy = getHeight() / 2;

        switch( currentBearing )
        {
            case 0:
                g.drawLine(cx, cy, cx, cy - radius);
                g.drawLine(cx - 1, cy, cx - 1, cy - radius + 1 );
                g.drawLine(cx + 1, cy, cx + 1, cy - radius + 1 );
                break;
            case 1:
                g.drawLine(cx, cy, (int)(cx+radius/Math.sqrt(2.0)), (int)(cy-radius/Math.sqrt(2.0)));
                g.drawLine(cx - 1, cy - 1, (int)(cx+radius/Math.sqrt(2.0)) - 1, (int)(cy-radius/Math.sqrt(2.0)) - 1);
                g.drawLine(cx + 1, cy + 1, (int)(cx+radius/Math.sqrt(2.0)) + 1, (int)(cy-radius/Math.sqrt(2.0)) + 1);
                break;
            case 2:
                g.drawLine(cx, cy, cx+radius, cy);
                g.drawLine(cx, cy - 1, cx+radius-1, cy - 1);
                g.drawLine(cx, cy + 1, cx+radius-1, cy + 1);
                break;
            case 3:
                g.drawLine(cx, cy, (int)(cx+radius/Math.sqrt(2.0)), (int)(cy+radius/Math.sqrt(2.0)));
                g.drawLine(cx + 1, cy - 1, (int)(cx+radius/Math.sqrt(2.0))+1, (int)(cy+radius/Math.sqrt(2.0))-1);
                g.drawLine(cx - 1, cy + 1, (int)(cx+radius/Math.sqrt(2.0))-1, (int)(cy+radius/Math.sqrt(2.0))+1);
                break;
            case 4:
                g.drawLine(cx, cy, cx, cy+radius);
                g.drawLine(cx - 1, cy, cx - 1, cy+radius-1);
                g.drawLine(cx + 1, cy, cx + 1, cy+radius-1);
                break;
            case 5:
                g.drawLine(cx, cy, (int)(cx-radius/Math.sqrt(2.0)), (int)(cy+radius/Math.sqrt(2.0)));
                g.drawLine(cx + 1, cy + 1, (int)(cx-radius/Math.sqrt(2.0))+1, (int)(cy+radius/Math.sqrt(2.0))+1);
                g.drawLine(cx - 1, cy - 1, (int)(cx-radius/Math.sqrt(2.0))-1, (int)(cy+radius/Math.sqrt(2.0))-1);
                break;
            case 6:
                g.drawLine(cx, cy, cx-radius, cy);
                g.drawLine(cx, cy - 1, cx-radius+1, cy - 1);
                g.drawLine(cx, cy + 1, cx-radius+1, cy + 1);
                break;
            case 7:
                g.drawLine(cx, cy, (int)(cx-radius/Math.sqrt(2.0)), (int)(cy-radius/Math.sqrt(2.0)));
                g.drawLine(cx + 1, cy - 1, (int)(cx-radius/Math.sqrt(2.0))+1, (int)(cy-radius/Math.sqrt(2.0))-1);
                g.drawLine(cx - 1, cy + 1, (int)(cx-radius/Math.sqrt(2.0))-1, (int)(cy-radius/Math.sqrt(2.0))+1);
                break;
        }

        g.setColor (Color.blue);
        radius = getHeight()/2;
        g.fillOval((getWidth()*3)/4-radius, getHeight()/2-radius, radius*2, radius*2);
        g.setColor (Color.magenta);
        cx = (getWidth()*3)/4;
        cy = getHeight()/2;

        switch (targetBearing) {
            case 0:
                g.drawLine(cx, cy, cx, cy - radius);
                g.drawLine(cx - 1, cy, cx - 1, cy - radius + 1 );
                g.drawLine(cx + 1, cy, cx + 1, cy - radius + 1 );
                break;
            case 1:
                g.drawLine(cx, cy, (int)(cx+radius/Math.sqrt(2.0)), (int)(cy-radius/Math.sqrt(2.0)));
                g.drawLine(cx - 1, cy - 1, (int)(cx+radius/Math.sqrt(2.0)) - 1, (int)(cy-radius/Math.sqrt(2.0)) - 1);
                g.drawLine(cx + 1, cy + 1, (int)(cx+radius/Math.sqrt(2.0)) + 1, (int)(cy-radius/Math.sqrt(2.0)) + 1);
                break;
            case 2:
                g.drawLine(cx, cy, cx+radius, cy);
                g.drawLine(cx, cy - 1, cx+radius-1, cy - 1);
                g.drawLine(cx, cy + 1, cx+radius-1, cy + 1);
                break;
            case 3:
                g.drawLine(cx, cy, (int)(cx+radius/Math.sqrt(2.0)), (int)(cy+radius/Math.sqrt(2.0)));
                g.drawLine(cx + 1, cy - 1, (int)(cx+radius/Math.sqrt(2.0))+1, (int)(cy+radius/Math.sqrt(2.0))-1);
                g.drawLine(cx - 1, cy + 1, (int)(cx+radius/Math.sqrt(2.0))-1, (int)(cy+radius/Math.sqrt(2.0))+1);
                break;
            case 4:
                g.drawLine(cx, cy, cx, cy+radius);
                g.drawLine(cx - 1, cy, cx - 1, cy+radius-1);
                g.drawLine(cx + 1, cy, cx + 1, cy+radius-1);
                break;
            case 5:
                g.drawLine(cx, cy, (int)(cx-radius/Math.sqrt(2.0)), (int)(cy+radius/Math.sqrt(2.0)));
                g.drawLine(cx + 1, cy + 1, (int)(cx-radius/Math.sqrt(2.0))+1, (int)(cy+radius/Math.sqrt(2.0))+1);
                g.drawLine(cx - 1, cy - 1, (int)(cx-radius/Math.sqrt(2.0))-1, (int)(cy+radius/Math.sqrt(2.0))-1);
                break;
            case 6:
                g.drawLine(cx, cy, cx-radius, cy);
                g.drawLine(cx, cy - 1, cx-radius+1, cy - 1);
                g.drawLine(cx, cy + 1, cx-radius+1, cy + 1);
                break;
            case 7:
                g.drawLine(cx, cy, (int)(cx-radius/Math.sqrt(2.0)), (int)(cy-radius/Math.sqrt(2.0)));
                g.drawLine(cx + 1, cy - 1, (int)(cx-radius/Math.sqrt(2.0))+1, (int)(cy-radius/Math.sqrt(2.0))-1);
                g.drawLine(cx - 1, cy + 1, (int)(cx-radius/Math.sqrt(2.0))-1, (int)(cy-radius/Math.sqrt(2.0))+1);
                break;
        }
    }
}

