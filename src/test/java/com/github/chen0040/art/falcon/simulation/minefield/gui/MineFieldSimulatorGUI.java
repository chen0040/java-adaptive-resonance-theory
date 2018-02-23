package com.github.chen0040.art.falcon.simulation.minefield.gui;

import com.github.chen0040.art.falcon.FalconConfig;
import com.github.chen0040.art.falcon.simulation.minefield.*;
import com.github.chen0040.art.falcon.simulation.minefield.agents.FalconNavAgent;
import com.github.chen0040.art.falcon.simulation.minefield.env.MineField;
import com.github.chen0040.art.falcon.simulation.utils.FileUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Created by chen0469 on 10/2/2015 0002.
 */
public class MineFieldSimulatorGUI extends JFrame {
    private MineFieldSimulator simulator;

    private SonarPanel p_sonar;
    private SonarPanel p_avsonar;
    private BearingPanel p_bearing;

    private TitledBorder sonar_title;
    private TitledBorder av_sonar_title;
    private TitledBorder bearing_title;

    private MazePanel p_field;

    private JLabel label1;
    private JLabel label2;

    private JComboBox ddAgentSelection;

    private int selectedAgentId = 0;

    public MineFieldSimulatorGUI(MineFieldSimulatorConfig config, FalconConfig falconConfig){
        simulator = new MineFieldSimulatorR(config, falconConfig);
        initialize(simulator.getMineField());
    }

    public void initialize (MineField maze)
    {
        Container container = getContentPane();
        container.removeAll();

        container.setLayout( new GridLayout ( 1, 2, 0, 0 ) );

        p_sonar = new SonarPanel(true, Color.green, maze);
        JPanel p_sonarPane = new JPanel();
        p_sonarPane.setLayout(new BorderLayout());
        sonar_title = new TitledBorder("Sonar Signal Input");
        p_sonarPane.setBorder(sonar_title);
        p_sonarPane.add(p_sonar, BorderLayout.CENTER);

        p_avsonar = new SonarPanel(false, Color.yellow, maze);
        JPanel p_avsonarPane = new JPanel();
        p_avsonarPane.setLayout(new BorderLayout());
        av_sonar_title = new TitledBorder("AV Sonar Signal");
        p_avsonarPane.setBorder(av_sonar_title);
        p_avsonarPane.add(p_avsonar, BorderLayout.CENTER);

        p_bearing = new BearingPanel(maze );
        JPanel p_bearingPane = new JPanel();
        p_bearingPane.setLayout(new BorderLayout(50, 50));
        bearing_title = new TitledBorder("Current Bearing + Target Bearing" );
        p_bearingPane.setBorder(bearing_title);
        p_bearingPane.add(p_bearing, BorderLayout.CENTER);

        JPanel p_sense = new JPanel();
        p_sense.setLayout(new GridLayout(3 * 1, 1, 0, 0));

        p_sense.add(p_sonarPane);
        p_sense.add(p_avsonarPane);
        p_sense.add(p_bearingPane);

        p_field = new MazePanel(  maze, simulator.getNumAgents() );

        JPanel p_fieldmsg = new JPanel();
        p_fieldmsg.setLayout(new BorderLayout());
        p_fieldmsg.setBorder(new TitledBorder("Minefield (View from the Top)"));
        p_fieldmsg.add(p_field, BorderLayout.CENTER);
        //p_fieldmsg.add(p_msg,BorderLayout.SOUTH);

        label1 = new JLabel("");
        label2 = new JLabel("");

        label1.setIcon(getIcon("images/right.gif"));
        label2.setIcon(getIcon("images/right.gif"));

        JPanel p_left = new JPanel();
        p_left.setLayout(new BorderLayout());
        p_left.add(p_fieldmsg, BorderLayout.CENTER);
        p_left.add(label1, BorderLayout.SOUTH);

        JPanel p_control = new JPanel();
        p_control.setLayout(new BorderLayout());
        p_control.add(createDropdown_AgentSelection(), BorderLayout.NORTH);
        p_control.add(p_sense, BorderLayout.CENTER);
        p_control.add(label2,BorderLayout.SOUTH);


        container.add(p_left);
        container.add(p_control);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        createMenuBar();

        setSize(900, 450);
        setTitle("Minefield Navigation Simulator");
        setVisible(true);
    }

    private JPanel createDropdown_AgentSelection(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JLabel label = new JLabel("Selected Agent:");
        label.setIcon(getIcon("images/right.gif"));
        panel.add(label);

        ddAgentSelection = new JComboBox();
        ddAgentSelection.addItem(selectedAgentId);
        panel.add(ddAgentSelection);
        ddAgentSelection.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                MineFieldSimulatorGUI.this.selectedAgentId = (Integer)e.getItem();
                notifyAgentSelectionChanged();
            }
        });

        return panel;
    }

    private void createMenuBar(){
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);

        JMenuItem miStartSimulation = new JMenuItem("Start Simulation");
        menuFile.add(miStartSimulation);
        miStartSimulation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runSims();
            }
        });

        JMenuItem miStartSimulationSilent = new JMenuItem("Start Simulation (No GUI)");
        menuFile.add(miStartSimulationSilent);
        miStartSimulationSilent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runSilentSims();
            }
        });

        JMenuItem miStopSimulation = new JMenuItem("Stop Simulation");
        menuFile.add(miStopSimulation);
        miStopSimulation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simulator.stop();
            }
        });

        JMenu menuView = new JMenu("View");
        menuBar.add(menuView);

        JCheckBoxMenuItem miShowTrack = new JCheckBoxMenuItem("Show Track");
        menuView.add(miShowTrack);
        miShowTrack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem checkButton = (JCheckBoxMenuItem) e.getSource();
                p_field.setTracking(checkButton.isSelected());
            }
        });

        JMenu menuEdit = new JMenu("Edit");
        menuBar.add(menuEdit);

        JMenu menuSize = createMenu_Size();
        menuEdit.add(menuSize);

        JMenu menuAgent = new JMenu("Agent");
        menuBar.add(menuAgent);

        JMenu menuSignals = createMenu_Signals();
        menuAgent.add(menuSignals);

        JMenu menuAI = createMenu_FALCON();
        menuAgent.add(menuAI);

        JMenu menuFalconConfig = createMenu_FalconConfig();
        menuAgent.add(menuFalconConfig);

        JMenu menuMaxSteps = createMenu_MaxSteps();
        menuAgent.add(menuMaxSteps);

        JMenu menuSim = new JMenu("Simulation");
        menuBar.add(menuSim);

        JMenu menuSimInterval = createMenu_SimulationInterval();
        menuSim.add(menuSimInterval);

        JMenu menuSimAgentNum = createMenu_SimulationAgentNum();
        menuEdit.add(menuSimAgentNum);

        JMenu menuTarget = createMenu_SimulationTarget();
        menuEdit.add(menuTarget);

        JMenu menuMines = createMenu_Mines();
        menuEdit.add(menuMines);

        JMenu menuMaxTrial = createMenu_MaxTrials();
        menuSim.add(menuMaxTrial);

        JMenu menuRuns = createMenu_Runs();
        menuSim.add(menuRuns);
        
        this.setJMenuBar(menuBar);

    }

    private JMenu createMenu_Mines(){
        JMenu menu = new JMenu("Mines");
        ButtonGroup buttonGroup = new ButtonGroup();

        int[] options = new int[] { 10, 20, 30, 40 };
        for(Integer numMines : options){
            JMenuItem mi1 = createMenuItem_Mines(numMines);
            buttonGroup.add(mi1);
            menu.add(mi1);
        }

        return menu;
    }

    private JMenuItem createMenuItem_Mines(final int numMines){
        JRadioButtonMenuItem mi1 = new JRadioButtonMenuItem(""+numMines);
        mi1.setSelected(simulator.getConfig().getNumMines()==numMines);

        mi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) e.getSource();
                if (radioButton.isSelected()) {
                    simulator.getConfig().setNumMines(numMines);
                    notifyMineFieldChanged();
                }
            }
        });

        return mi1;
    }

    private JMenu createMenu_Runs(){
        JMenu menu = new JMenu("Runs");
        ButtonGroup buttonGroup = new ButtonGroup();

        int[] options = new int[] { 1, 5, 10, 30 };
        for(Integer runs : options){
            JMenuItem mi1 = createMenuItem_Runs(runs);
            buttonGroup.add(mi1);
            menu.add(mi1);
        }

        return menu;
    }

    private JMenuItem createMenuItem_Runs(final int runs){
        JRadioButtonMenuItem mi1 = new JRadioButtonMenuItem(""+runs);
        mi1.setSelected(simulator.getConfig().getNumRuns() == runs);

        mi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) e.getSource();
                if (radioButton.isSelected()) {
                    simulator.getConfig().setNumRuns(runs);
                }
            }
        });
        return mi1;
    }

    private JMenu createMenu_FalconConfig(){
        JMenu menu = new JMenu("Learn");

        JCheckBoxMenuItem mi1 = new JCheckBoxMenuItem("Immediate Reward");
        mi1.setSelected(simulator.getConfig().isImmediateRewardProvided());
        mi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem checkButton = (JCheckBoxMenuItem)e.getSource();
                simulator.getConfig().setImmediateRewardProvided(checkButton.isSelected());
            }
        });
        menu.add(mi1);

        JCheckBoxMenuItem mi2 = new JCheckBoxMenuItem("Bounded Q");
        mi2.setSelected(simulator.getFalconConfig().isBounded);
        mi2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem checkButton = (JCheckBoxMenuItem) e.getSource();
                simulator.getFalconConfig().isBounded = (checkButton.isSelected());
            }
        });
        menu.add(mi2);

        return menu;
    }

    private JMenu createMenu_FALCON(){
        JMenu menu = new JMenu("FALCON");
        ButtonGroup buttonGroup = new ButtonGroup();

        JRadioButtonMenuItem mi1 = new JRadioButtonMenuItem("R-FALCON");
        mi1.setSelected(simulator instanceof MineFieldSimulatorR);
        buttonGroup.add(mi1);
        menu.add(mi1);
        mi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) e.getSource();
                if (radioButton.isSelected()) {
                    simulator = new MineFieldSimulatorR(simulator.getConfig(), simulator.getFalconConfig());
                }
            }
        });

        JRadioButtonMenuItem mi2 = new JRadioButtonMenuItem("Q-FALCON");
        mi2.setSelected(simulator instanceof MineFieldSimulatorQ);
        buttonGroup.add(mi2);
        menu.add(mi2);
        mi2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) e.getSource();
                if (radioButton.isSelected()) {
                    simulator = new MineFieldSimulatorQ(simulator.getConfig(), simulator.getFalconConfig());
                }
            }
        });

        JRadioButtonMenuItem mi3 = new JRadioButtonMenuItem("Q-FALCON(lambda)");
        mi3.setSelected(simulator instanceof MineFieldSimulatorQLambda);
        menu.add(mi3);
        buttonGroup.add(mi3);
        mi3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) e.getSource();
                if (radioButton.isSelected()) {
                    simulator = new MineFieldSimulatorQLambda(simulator.getConfig(), simulator.getFalconConfig());
                }
            }
        });

        JRadioButtonMenuItem mi4 = new JRadioButtonMenuItem("SARSA-FALCON");
        mi4.setSelected(simulator instanceof MineFieldSimulatorSarsa);
        menu.add(mi4);
        buttonGroup.add(mi4);
        mi4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) e.getSource();
                if (radioButton.isSelected()) {
                    simulator = new MineFieldSimulatorSarsa(simulator.getConfig(), simulator.getFalconConfig());
                }
            }
        });

        JRadioButtonMenuItem mi5 = new JRadioButtonMenuItem("SARSA-FALCON(lambda)");
        mi5.setSelected(simulator instanceof MineFieldSimulatorSarsaLambda);
        menu.add(mi5);
        buttonGroup.add(mi5);
        mi5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) e.getSource();
                if (radioButton.isSelected()) {
                    simulator = new MineFieldSimulatorSarsaLambda(simulator.getConfig(), simulator.getFalconConfig());
                }
            }
        });

        return menu;
    }

    private JMenu createMenu_MaxSteps(){
        JMenu menu = new JMenu("Max Steps");
        ButtonGroup buttonGroup = new ButtonGroup();

        int[] options = new int[] { 30, 60, 90, 120 };
        for(Integer maxSteps : options){
            JMenuItem mi1 = createMenuItem_MaxSteps(maxSteps);
            buttonGroup.add(mi1);
            menu.add(mi1);
        }

        return menu;
    }

    private JMenuItem createMenuItem_MaxSteps(final int maxSteps){
        JRadioButtonMenuItem mi1 = new JRadioButtonMenuItem(""+maxSteps);
        mi1.setSelected(simulator.getConfig().getMaxStep()==maxSteps);

        mi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) e.getSource();
                if (radioButton.isSelected()) {
                    simulator.getConfig().setMaxStep(maxSteps);
                }
            }
        });
        return mi1;
    }

    private JMenu createMenu_MaxTrials(){
        JMenu menu = new JMenu("Max Trials");
        ButtonGroup buttonGroup = new ButtonGroup();

        int[] options = new int[] { 1000, 2000, 3000, 4000 };
        for(Integer maxTrials : options){
            JMenuItem mi1 = createMenuItem_MaxTrials(maxTrials);
            buttonGroup.add(mi1);
            menu.add(mi1);
        }

        return menu;
    }

    private JMenuItem createMenuItem_MaxTrials(final int maxTrials){
        JRadioButtonMenuItem mi1 = new JRadioButtonMenuItem(""+maxTrials);
        mi1.setSelected(simulator.getConfig().getMaxTrial() == maxTrials);

        mi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) e.getSource();
                if (radioButton.isSelected()) {
                    simulator.getConfig().setMaxTrial(maxTrials);
                }
            }
        });

        return mi1;
    }

    private JMenu createMenu_SimulationTarget(){
        JMenu menuTarget = new JMenu("Target");

        JCheckBoxMenuItem miMovingTarget = new JCheckBoxMenuItem("Moving");
        miMovingTarget.setSelected(simulator.getConfig().targetMoving);
        miMovingTarget.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem checkButton = (JCheckBoxMenuItem) e.getSource();
                simulator.getConfig().targetMoving = checkButton.isSelected();
            }
        });
        menuTarget.add(miMovingTarget);

        return menuTarget;
    }

    SwingWorker worker = null;
    private void runSilentSims(){
        if(worker != null && !worker.isDone()){
            JOptionPane dlg = new JOptionPane(JOptionPane.WARNING_MESSAGE);
            dlg.setMessage("Worker is running, please wait or cancel");
            dlg.setVisible(true);
            return;
        }

         worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                simulator.runSims();
                return null;
            }

             @Override
             protected void done() {
                 super.done();
                 JOptionPane dlg =new JOptionPane(JOptionPane.INFORMATION_MESSAGE);
                 dlg.setMessage("Done!");
                 dlg.setVisible(true);
             }
         };
        worker.execute();
    }

    private JMenu createMenu_Signals(){
        JMenu menuSignals = new JMenu("AV");

        JCheckBoxMenuItem miIncludeSonar = new JCheckBoxMenuItem("Include Sonar");
        miIncludeSonar.setSelected(simulator.getConfig().numSonarInput > 0);
        menuSignals.add(miIncludeSonar);
        miIncludeSonar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem checkButton = (JCheckBoxMenuItem) e.getSource();
                if (checkButton.isSelected()) {
                    simulator.getConfig().numSonarInput = 10;
                } else {
                    simulator.getConfig().numSonarInput = 0;
                }
                notifyNumStateChanged();
            }
        });

        JCheckBoxMenuItem miIncludeAVSonar = new JCheckBoxMenuItem("Include AV Sonar");
        miIncludeAVSonar.setSelected(simulator.getConfig().numAVSonarInput > 0);
        menuSignals.add(miIncludeAVSonar);
        miIncludeAVSonar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem checkButton = (JCheckBoxMenuItem) e.getSource();
                if (checkButton.isSelected()) {
                    simulator.getConfig().numSonarInput = 10;
                } else {
                    simulator.getConfig().numSonarInput = 0;
                }
                notifyNumStateChanged();
            }
        });

        JCheckBoxMenuItem miIncludeBearing = new JCheckBoxMenuItem("Include Bearing");
        miIncludeBearing.setSelected(simulator.getConfig().numBearingInput > 0);
        menuSignals.add(miIncludeBearing);
        miIncludeBearing.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem checkButton = (JCheckBoxMenuItem) e.getSource();
                if (checkButton.isSelected()) {
                    simulator.getConfig().numBearingInput = 8;
                } else {
                    simulator.getConfig().numBearingInput = 0;
                }
                notifyNumStateChanged();
            }
        });

        JCheckBoxMenuItem miIncludeRange = new JCheckBoxMenuItem("Include Range");
        miIncludeRange.setSelected(simulator.getConfig().numRangeInput > 0);
        menuSignals.add(miIncludeRange);
        miIncludeRange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem checkButton = (JCheckBoxMenuItem) e.getSource();
                if (checkButton.isSelected()) {
                    simulator.getConfig().numRangeInput = 2;
                } else {
                    simulator.getConfig().numRangeInput = 0;
                }
                notifyNumStateChanged();
            }
        });

        return menuSignals;
    }

    private HashMap<String, ImageIcon> icons = new HashMap<String, ImageIcon>();

    private ImageIcon getIcon(String filename){
        if(icons.containsKey(filename)){
            return icons.get(filename);
        } else {
            URL url = null;
            try {
                url = FileUtils.getResourceFile(filename).toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            ImageIcon icon = new ImageIcon(url);
            icons.put(filename, icon);
            return icon;
        }
    }

    private JMenu createMenu_Size(){
        JMenu menuSize = new JMenu("Mine Field");
        ButtonGroup bgSize = new ButtonGroup();

        int[] options = new int[] { 16, 32, 64, 128, 256 };
        for(Integer size : options){
            JMenuItem mi1 = createMenuItem_Size(size);
            bgSize.add(mi1);
            menuSize.add(mi1);
        }

        return menuSize;
    }

    private JMenuItem createMenuItem_Size(final int size){
        JRadioButtonMenuItem mi1 = new JRadioButtonMenuItem(size+" x "+size);
        mi1.setSelected(simulator.getConfig().getMineFieldSize() == size);

        mi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) e.getSource();
                if (radioButton.isSelected()) {
                    simulator.getConfig().setMineFieldSize(size);
                }
                notifyMineFieldChanged();
            }
        });
        return mi1;
    }

    private JMenu createMenu_SimulationAgentNum(){
        JMenu menu = new JMenu("Vehicles");
        ButtonGroup buttonGroup = new ButtonGroup();

        int[] options = new int[] { 1, 2, 5, 10, 20};

        for(Integer numAgents : options){
            JMenuItem mi1 = createMenuItem_SimulationAgentNum(numAgents);
            buttonGroup.add(mi1);
            menu.add(mi1);
        }

        return menu;
    }

    private JMenuItem createMenuItem_SimulationAgentNum(final int agentNum){
        JRadioButtonMenuItem mi1 = new JRadioButtonMenuItem(""+agentNum);
        mi1.setSelected(simulator.getConfig().getNumAgents()==agentNum);

        mi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) e.getSource();
                if (radioButton.isSelected()) {
                    simulator.getConfig().setNumAgents(agentNum);
                    notifyNumAgentChanged();
                }
            }
        });

        return mi1;
    }

    private JMenu createMenu_SimulationInterval(){
        JMenu menuInterval = new JMenu("Interval");
        ButtonGroup buttonGroup = new ButtonGroup();

        int[] options = new int[] { 10, 20, 50, 100, 200, 300, 400, 500, 600, 700 };

        for(Integer interval : options){
            JMenuItem mi1 = createMenuItem_SimulationInterval(interval);
            buttonGroup.add(mi1);
            menuInterval.add(mi1);
        }

        return menuInterval;
    }

    private JMenuItem createMenuItem_SimulationInterval(final int interval){
        JRadioButtonMenuItem mi1 = new JRadioButtonMenuItem(""+interval);
        mi1.setSelected(simulator.getConfig().getUiInterval() == interval);

        mi1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JRadioButtonMenuItem radioButton = (JRadioButtonMenuItem) e.getSource();
                if (radioButton.isSelected()) {
                    simulator.getConfig().setUiInterval(interval);
                }
            }
        });

        return mi1;
    }

    private void notifyNumStateChanged(){
        simulator.getFalconConfig().numState = simulator.getConfig().numState();
    }

    private void notifyMineFieldChanged(){
        MineFieldSimulatorConfig config = simulator.getConfig();
        MineField mineField = simulator.getMineField();
        mineField.refreshMaze(config.getMineFieldSize(), config.getNumMines(), config.getNumAgents());
        p_field.init_MP(mineField, config.getNumAgents());
        p_field.repaint();
    }

    private void notifyNumAgentChanged(){
        MineFieldSimulatorConfig config = simulator.getConfig();
        MineField mineField = simulator.getMineField();

        ddAgentSelection.removeAllItems();

        for(int agentId = 0; agentId < config.getNumAgents(); ++agentId){
            ddAgentSelection.addItem(agentId);
        }

        mineField.refreshMaze(config.getMineFieldSize(), config.getNumMines(), config.getNumAgents());
        p_field.init_MP(mineField, config.getNumAgents());
        p_field.repaint();
    }

    private void notifyAgentSelectionChanged(){
        MineField mineField = simulator.getMineField();
        p_sonar.readSonar(selectedAgentId, mineField);
        p_avsonar.readSonar(selectedAgentId, mineField);
        p_bearing.readBearing(selectedAgentId, mineField);
    }

    public void runSims(){
        Thread thread = new Thread(new Runnable() {
            public void run() {
                simulator.runSims(new Consumer<MineFieldSimulatorProgress>(){
                    public void accept(MineFieldSimulatorProgress progress) {
                        MineField mineField = progress.getMineField();
                        int step = progress.getStep();
                        int[][] pos = mineField.getCurrentPositions();

                        p_field.doRefresh(mineField, pos, step);

                        String message = simulator.getMessage();
                        if(message != null && !message.equals("")) {
                            label2.setText(message);
                        }

                        p_sonar.readSonar(selectedAgentId, mineField);
                        p_avsonar.readSonar(selectedAgentId, mineField);
                        p_bearing.readBearing(selectedAgentId, mineField);

                        label1.setText("Run: "+progress.getRun()+" Trial: "+progress.getTrial()+" Step: "+step);
                    }
                });
            }
        });

        thread.start();
    }

    public static void main(String[] args){
        // below is settings from RFalcon
        MineFieldSimulatorConfig config = new MineFieldSimulatorConfig();
        config.setImmediateRewardProvided(true);
        config.setNumAgents(1);

        FalconConfig falconConfig = new FalconConfig();
        falconConfig.numAction = FalconNavAgent.numAction;
        falconConfig.numState = config.numState();
        falconConfig.numReward = 2;

        MineFieldSimulatorGUI gui = new MineFieldSimulatorGUI(config, falconConfig);
    }
}
