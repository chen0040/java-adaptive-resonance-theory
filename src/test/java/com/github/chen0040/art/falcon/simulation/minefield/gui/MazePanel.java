package com.github.chen0040.art.falcon.simulation.minefield.gui;

import com.github.chen0040.art.falcon.simulation.minefield.env.MineField;
import com.github.chen0040.art.falcon.simulation.utils.FileUtils;
import com.github.chen0040.art.falcon.simulation.utils.MazePalette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;

/**
 * Created by chen0469 on 10/2/2015 0002.
 */
public

class MazePanel extends JPanel implements ActionListener {

    final int MAXSTEP = 500;

    private int size = 16;
    private int[][] current;
    private int[] target;
    private int[] bearing;
    private int[] currentBearing;
    private int[] targetBearing;
    private double[] sonar;
    private double range;
    private int[][] mines;
    private int[][][] path;
    private int [] numStep;
    private int agent_num;
    private int maxStep;
    private boolean tracking = false;

    public boolean isTracking(){
        return tracking;
    }

    public void setTracking(boolean tracking){
        this.tracking = tracking;
    }

    /**
     * Background image.
     *
     * @author J.J.
     */
    private BufferedImage bgImage;
    /**
     * Bome image.
     *
     * @author J.J.
     */
    private Image bombIcon;
    /**
     * Tank image.
     *
     * @author J.J.
     */
    private Image [] tankIcon;
    /**
     * Target image.
     *
     * @author J.J.
     */
    private Image targetIcon;
    /**
     * popup menu for bgimage seleciton.
     *
     * @author Jin Jun
     */
    private JPopupMenu popup;

    public MazePanel(MineField m, int numVehicles)
    {
        init_MP( m, numVehicles);
        doRefresh(m, null, 0);
    }

    public void init_MP( MineField m, int numVehicles ) {
        // initiate components first.
        initComponents();

        size = m.getSize();
        this.agent_num = numVehicles;

        int agt;

        numStep = new int[agent_num];
        current = new int[agent_num][];
        currentBearing = new int[agent_num];
        for( agt = 0; agt < agent_num; agt++ )
            current[agt] = new int[2];
        current = m.getCurrentPositions();

        path = new int[MAXSTEP][agent_num][2];

        target = new int[2];
        target = m.getTargetPosition();

        mines = new int[size][size];
        for (int i=0; i<size; i++)
            for (int j=0; j<size; j++)
                mines[i][j] = m.getMine(i, j);

        /**
         * load icons
         */
        loadImageIcons();

        /**
         * load background
         */
        File bgFile = FileUtils.getResourceFile("images/background.jpg");

        if( bgFile.canRead() ) {
            loadBackgroundImage(bgFile);
        } else {
            System.out.println("Cannot find default background image.");
        }
    }

    /**
     * Load icons for objects in the gameWorld.
     *
     * @author J.J.
     */
    private void loadImageIcons() {

        try {
            bombIcon = new ImageIcon(FileUtils.getResourceFile("images/bomb.png").toURL()).getImage();
            tankIcon = new Image[8];
            for( int i = 0; i<8; i++ ) {
                tankIcon[i] = new ImageIcon(FileUtils.getResourceFile("images/tank" + i + ".png").toURL()).getImage();
            }
            targetIcon = new ImageIcon(FileUtils.getResourceFile("images/target.png").toURL()).getImage();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to initiate components.
     *
     * @author J.J.
     */
    private void initComponents(){
        popup = new JPopupMenu();
        JMenuItem bgMenuItem = new JMenuItem("Change background image");
        bgMenuItem.addActionListener(this);
        popup.add(bgMenuItem);

        this.addMouseListener(new PopupListener());
    }

    /**
     * Method to load background image.
     *
     * @author J.J.
     */
    private void loadBackgroundImage(File file ) {

        try {
            bgImage =  javax.imageio.ImageIO.read(file);

        } catch (Exception e) {
            System.out.println("Load background failed.");
        }
    }

    /**
     * Method to draw background image.
     *
     * @author J.J.
     */
    private void drawBackgroundImage(Graphics g) {
        if( this.bgImage == null ) return;

        float factor = 0;

        g.drawImage(this.bgImage, 0, 0, this.getSize().width, this.getSize().height, this);
//        ((Graphics2D)g).drawImage(bgImage, null,0,0);
    }

    public void doRefresh(MineField m, int[][] pos, int step) {

        current = m.getCurrentPositions();
        for(int k = 0; k < agent_num; k++ )
            currentBearing[k] = m.getCurrentBearing( k );

        target = m.getTargetPosition();
        for (int i=0; i<size; i++)
            for (int j=0; j<size; j++)
                mines[i][j] = m.getMine(i, j);

        setCurrentPath(pos, step);

        repaint();
    }

    private void setCurrentPath(int [][] pos, int step )
            throws ArrayIndexOutOfBoundsException {
        int agt;
        try {
            for( agt = 0; agt < agent_num; agt++ )
            {
                if(pos != null) path[step][agt] = pos[agt];
                numStep[agt] = step;
            }
        }
        catch ( ArrayIndexOutOfBoundsException e ) {
            System.out.println( "path array index out of bound:" + e.getMessage() );
        }
    }

    protected void paintComponent( Graphics g ) {
        int a;
        Color c;

        super.paintComponent(g);
        drawBackgroundImage(g);

        int radius = getWidth()/size/2;
        int radius_path=0;

        /**
         * drawing obstacle
         *
         * documented by J.J.
         */
        g.setColor (Color.red);
        for (int i=0; i<size; i++)
            for (int j=0; j<size; j++)
                if( mines[i][j] == 1 )
                    g.drawImage(bombIcon,
                            (int)((getWidth()/size)*(i+0.5))-radius,
                            (int)((getHeight()/size)*(j+0.5))-radius,
                            radius*2, radius*2, this);

        /**
         * drawing mine
         */
        g.setColor (Color.orange);
        g.drawImage(targetIcon,
                (int)((getWidth()/size)*(target[0]+0.5))-radius,
                (int)((getHeight()/size)*(target[1]+0.5))-radius,
                2*radius, 2*radius, this);

        radius *=0.75;

        for( a = 0; a < agent_num; a++ )
        {
            c = MazePalette.AV_Color[a%MazePalette.AV_Color_Num];
            g.setColor( c );
            /**
             * drawing tank
             */
            if( currentBearing[a]%2 == 0) {
                g.drawImage(tankIcon[currentBearing[a]],
                        (int)((getWidth()/size)*(current[a][0]+0.5))-radius,
                        (int)((getHeight()/size)*(current[a][1]+0.5))-radius,
                        2*radius, 2*radius, this);
            } else {
                g.drawImage(tankIcon[currentBearing[a]],
                        (int)((getWidth()/size)*(current[a][0]+0.5))-radius,
                        (int)((getHeight()/size)*(current[a][1]+0.5))-radius,
                        (int)(2.828*radius), (int)(2.828*radius), this);
            }


// 			for the purpose of tracking the actions of agents
            if( !isTracking() )
                continue;

            for (int i=0; i<numStep[a]; i++)
            {
                radius_path = radius*(i+1)/numStep[a];
                if( ( path[i][a][0] == 0 ) && ( path[i][a][1] == 0 ) )
                {
                    g.setColor( Color.red );
                    radius_path *= 2;
                }
                else
                    g.setColor( c );

                g.fillRect((int)((getWidth()/size)*(path[i][a][0]+0.5))-radius_path/2,
                        (int)((getHeight()/size)*(path[i][a][1]+0.5))-radius_path/2,
                        radius_path, radius_path);

                if( ( path[i+1][a][0] == 0 ) && ( path[i+1][a][1] == 0 ) )
                {
                    g.setColor( Color.red );
                }
                else
                    g.setColor( c );

                if (path[i+1][a][0]!=0 || path[i+1][a][1]!=0)
                    g.drawLine((int)((getWidth()/size)*(path[i][a][0]+0.5)),
                            (int)((getHeight()/size)*(path[i][a][1]+0.5)),
                            (int)((getWidth()/size)*(path[i+1][a][0]+0.5)),
                            (int)((getHeight()/size)*(path[i+1][a][1]+0.5)));
            }
        }
    }

    public void actionPerformed(ActionEvent e){
        JFileChooser fc = new JFileChooser();

        fc.showOpenDialog(this);

        File bgFile = fc.getSelectedFile();

        if( bgFile == null ) return;

        loadBackgroundImage(bgFile);
        repaint();
    }

    class PopupListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                if( popup == null ) return;

                popup.show(e.getComponent(),
                        e.getX(), e.getY());
            }
        }
    }
}
