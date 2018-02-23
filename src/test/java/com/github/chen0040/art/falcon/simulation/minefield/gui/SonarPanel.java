package com.github.chen0040.art.falcon.simulation.minefield.gui;

import com.github.chen0040.art.falcon.simulation.minefield.env.MineField;

import javax.swing.*;
import java.awt.*;

/**
 * Created by chen0469 on 10/2/2015 0002.
 */
public class SonarPanel extends JPanel {
    private int bearing;
    private double[] sonar;
    private double range;
    private int numSonar=5;
    private int radius;
    private Color fore_color;
    private boolean sonar_mode;

    public SonarPanel(boolean mode, Color c, MineField m )
    {
        sonar_mode = mode;
        fore_color = c;
        sonar = new double[numSonar];
    }

    public void readSonar(int vehicleId, MineField m ) {

        if( sonar_mode )
            sonar = m.getSonar ( vehicleId );
        else
            sonar = m.getAVSonar ( vehicleId );
        repaint();
    }

    public boolean get_sonar_mode()
    {
        return( sonar_mode );
    }


    protected void paintComponent(Graphics g) {
        int r;

        super.paintComponent(g);

        g.setColor (Color.black);
        for (int i=0; i<numSonar; i++)
            g.drawRect((getWidth()/numSonar)*i, getHeight()/2-getWidth()/numSonar/2, getWidth()/numSonar, getWidth()/numSonar);

        g.setColor( fore_color );
        r = Math.min( getHeight() / 2, getWidth() / ( 2 * numSonar ) );
        for( int i=0; i<numSonar; i++ )
        {
            radius = ( int )( sonar[i] * r );
            g.fillOval((getWidth()/numSonar)*i+getWidth()/(2*numSonar)-radius, getHeight()/2-radius, radius*2, radius*2);
        }
    }
}
