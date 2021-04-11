package retail;

import javax.swing.JLabel;

/**
 * @author Jacek Karolczak
 */
public class Main {

    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.setVisible(true);
        World world = new World(gui.getMapPanel(), gui.getSickValue(), gui.getVaccinatedValue(), gui.getMaskOnValue(), gui.getClientsValue(), gui.getSuppliersValue(), gui.getLockdownValue());
        gui.initDraw(world);
    }
}
