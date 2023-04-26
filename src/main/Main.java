package main;

import gui.MyAppForm;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame j = new JFrame("App");
        j.setContentPane(new MyAppForm().main);
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.pack();
        j.setVisible(true);
    }
}
