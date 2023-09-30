package club.pulsive.impl.util.panel;

import javax.swing.*;
import java.awt.*;

public class LoadClientPane {

    public LoadClientPane(){
        JFrame jFrame = new JFrame();
        JPanel jPanel = new JPanel();
        JButton jButton = new JButton("Pulsive");
        JButton azura = new JButton("Azura");

        jPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 60));
        jPanel.setLayout(new GridLayout(0, 1));
        jPanel.add(new JLabel("What client would you like to choose?"));
        jPanel.add(jButton);
        jPanel.add(azura);


        jFrame.add(jPanel, BorderLayout.CENTER);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setTitle("Which client would you lick to choose?");
        jFrame.pack();
        jFrame.setVisible(true);
    }

}
