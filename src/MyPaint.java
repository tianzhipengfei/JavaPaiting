

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;

/*
    This is the main frame. I admit I copied plenty of codes from Internet in MyPaint.java. However I added more function and some components to make it stronger
    and make it fit with my whole project. To be honest, I just use some UI components and change the frame of the project from Internet.

    from http://blog.csdn.net/bluesky_usc/article/details/53128460
    I must thanks "BlueSky_USC", whose blog teach me a lot.
 */

public class MyPaint extends JFrame{
    private JButton btt;                        //Current color  button
    private ButtonListener bl;                  //Color button listener
    private RadioButtonListener rbl;            //Function button listener
    private MyDraw md;                          //Canvas
    private JMenuBar bar;
    private JMenu menuFile;
    private JMenu menuOperate;
    private JMenu menuHelp;
    private JMenuItem newFile;
    private JMenuItem openFile;
    private JMenuItem saveFile;
    private JMenuItem saveimgFile;
    private JMenuItem revoke;
    private JMenuItem voke;
    private JMenuItem help;
    private JMenuItem about;
    private JPanel panelCentral;
    private JPanel panelLeft;
    private ButtonGroup bg;
    private LineWidthChooser lineWidthChooser;
    private JPanel panelDown;
    private JPanel panelDownLeft;
    private JPanel panelDownRight;
    private JPanel panelDownChild;

    //Init Frame
    public void initFrame(){
        JPanel panel = new JPanel();
        btt = new JButton();
        md = new MyDraw(btt);
        bl = new ButtonListener(this.md, btt);
        rbl = new RadioButtonListener(this.md);
        bar= new JMenuBar();
        menuFile = new JMenu("File");
        menuOperate = new JMenu("Edit");
        menuHelp = new JMenu("Help");
        panelCentral = new JPanel();
        panelLeft = new JPanel();
        bg = new ButtonGroup();
        lineWidthChooser = new LineWidthChooser(md);
        panelDown = new JPanel();
        panelDownChild = new JPanel();
        panelDownLeft = new JPanel();
        panelDownRight = new JPanel();

        //Set properties
        this.setSize(600,500);
        this.setDefaultCloseOperation(3);
        this.setTitle("Paint");
        this.setLocationRelativeTo(null);
        panel.setLayout(new BorderLayout());
        this.add(panel);

        //Add menu bar
        panel.add(bar,BorderLayout.NORTH);

        //Add three menu into menu bar
        bar.add(menuFile);
        bar.add(menuOperate);
        bar.add(menuHelp);

        //Add four menu item into the first menu(File)
        newFile= new JMenuItem("New File");
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,Event.CTRL_MASK));         //Add shortcutkeys
        newFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int value=JOptionPane.showConfirmDialog(null, "Do you want to save this image", "Tips", 0);
                if(value==0){
                    saveFile(0);
                }
                if(value==1){
                    md.reset();
                }
            }
        });

        openFile = new JMenuItem("Open Project File");
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,Event.CTRL_MASK));         //Add shortcutkeys
        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int value=JOptionPane.showConfirmDialog(null, "Do you want to save this image", "Tips", 0);
                if(value==0){
                    saveFile(0);
                }
                if(value==1){
                    try {
                        //Show file chooser to let user select file
                        JFileChooser chooser = new JFileChooser();
                        chooser.showOpenDialog(null);
                        File file =chooser.getSelectedFile();
                        //if file doesn't exist, show dialog "NO FILE"
                        if(file==null){
                            JOptionPane.showMessageDialog(null, "No file is selected");
                            md.repaint();
                        }
                        else {
                            //delete all shapes in Shapelist
                            md.getShapeList().clear();
                            md.repaint();
                            FileInputStream fis = new FileInputStream(file);
                            ObjectInputStream ois = new ObjectInputStream(fis);
                            //Get objects from file
                            ArrayList<MyShape> list =(ArrayList<MyShape>)ois.readObject();
                            //Add objects into list
                            for (int i = 0; i <list.size(); i++) {
                                MyShape shape=(MyShape)list.get(i);
                                md.getShapeList().add(shape);
                                //Repaint to show the painting
                                md.repaint();
                            }
                            ois.close();
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        saveFile = new JMenuItem("Save Project File");
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,Event.CTRL_MASK));         //Add shortcutkeys
        saveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile(0);
            }           //0 means save project (Objects)
        });

        saveimgFile = new JMenuItem("Save image file");
        saveimgFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,Event.SHIFT_MASK+Event.CTRL_MASK));         //Add shortcutkeys
        saveimgFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile(1);
            }           //1 means save a picture
        });

        menuFile.add(newFile);
        menuFile.add(openFile);
        menuFile.add(saveFile);
        menuFile.add(saveimgFile);

        //Add two menu item into the second menu(Edit)
        revoke = new JMenuItem("revoke");
        revoke.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,Event.CTRL_MASK));         //Add shortcutkeys
        revoke.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                md.revoke();
            }       //Call method revoke in MyDraw
        });

        voke = new JMenuItem("voke");
        voke.setAccelerator(KeyStroke .getKeyStroke(KeyEvent.VK_Z,Event.SHIFT_MASK+Event.CTRL_MASK));         //Add shortcutkeys
        voke.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                md.voke();
            }       //Call method voke in MyDraw
        });

        menuOperate.add(revoke);
        menuOperate.add(voke);

        //Add two menu item into the third menu(Help)
        help = new JMenuItem("Help");
        help.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,Event.CTRL_MASK));         //Add shortcutkeys
        help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Show help dialog
                JOptionPane.showMessageDialog(null,
                        "Erasor: ...\n" +
                        "Pencil: ...\n" +
                        "Spray: ...\n" +
                        "Brush: ...\n" +
                        "Fill: ...\n" +
                        "Shaoe: ...\n" +
                        "Polygon: ...\n" +
                        "Font: ...\n" +
                        "Line size: ...\n" +
                        "Move: ...\n" +
                        "Picker: ...");
            }
        });

        about = new JMenuItem("About");
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,Event.CTRL_MASK));         //Add shortcutkeys
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Show about dialog
                JOptionPane.showMessageDialog(null, "Developed by Tianzhi Li");
            }
        });

        menuHelp.add(help);
        menuHelp.add(about);


        //Add central JPanel
        panelCentral.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
        panel.setBackground(Color.gray);
        panel.add(panelCentral);
        //Set MyDraw's properties
        md.setBackground(Color.WHITE);
        md.setPreferredSize(new Dimension(500,330));
        //Add MyDraw into central JPanel
        panelCentral.add(md);

        //Add left JPanel (Function RadioButton JPanel)
        panelLeft.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        panelLeft.setBackground(new Color(235,233,238));
        panelLeft.setPreferredSize(new Dimension(100, 0));
        //Add RatioButtons in the central left JPanel

        for(int i=2;i<16;i++){
            //Add four different resources.images to each RadioBuuton
            String p = getClass().getResource("/images/draw"+i+".jpg").getPath();
            String p1 = getClass().getResource("images/draw"+i+"-1"+".jpg").getPath();
            String p2 = getClass().getResource("images/draw"+i+"-2"+".jpg").getPath();
            String p3 = getClass().getResource("images/draw"+i+"-3"+".jpg").getPath();
            ImageIcon img1 = new ImageIcon(p);         //Default image
            ImageIcon img2 = new ImageIcon(p1);    //Image when mouse moves upon button
            ImageIcon img3 = new ImageIcon(p2);    //Image when mouse pressed
            ImageIcon img4 = new ImageIcon(p3);    //Image when selected
            JRadioButton jrb = new JRadioButton();
            jrb.setActionCommand("pic"+i);
            //Set default function is draw Line
            if(i==10){
                jrb.setSelected(true);
            }
            jrb.addActionListener(rbl);
            jrb.setIcon(img1);
            jrb.setRolloverIcon(img2);
            jrb.setPressedIcon(img3);
            jrb.setSelectedIcon(img4);
            jrb.setBorder(null);            //Set no Border, since we added the border in image. :)
            bg.add(jrb);            //add to buttonGroup
            panelLeft.add(jrb);
        };

        //Add line width chooser into left JPanel
        panelLeft.add(new JLabel("line size"));
        panelLeft.add(lineWidthChooser.getBox());

        //Add down JPanel
        panelDown.setBackground(Color.gray);
        panelDown.setLayout(null);
        panelDown.setPreferredSize(new Dimension(0, 80));

        //Add down child JPanel
        panelDownChild.setLayout(null);
        panelDownChild.setBounds(15, 10, 300, 60);
        panelDownChild.setBackground(Color.green);

        //Add down left JPanel (for current color)
        panelDownLeft.setLayout(null);
        panelDownLeft.setBackground(Color.white);
        panelDownLeft.setBounds(0, 0, 60, 60);

        //Add down right JPanel (for choosing color)
        panelDownRight.setBackground(null);
        panelDownRight.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
        panelDownRight.setBounds(60, 0, 240, 60);

        //Add child JPanel into parent JPanel and set position
        panel.add(panelLeft,BorderLayout.WEST);
        panel.add(panelDown, BorderLayout.SOUTH);
        panelDown.add(panelDownChild);
        panelDownChild.add(panelDownLeft);
        panelDownChild.add(panelDownRight);

        //Add Button to show current color
        btt.setBackground(Color.black);
        btt.setBounds(10, 10, 30, 30);
        panelDownLeft.add(btt);

        //Add onSet special effect
        BevelBorder  bb = new BevelBorder(0,Color.gray,Color.white);        //Set border for button
        BevelBorder  bb1 = new BevelBorder(1,Color.gray,Color.white);
        panelDownLeft.setBorder(bb);
        btt.setBorder(bb1);

        //Add right side color
        Color colors[] = {Color.BLUE,Color.red,Color.black,Color.ORANGE,Color.gray, Color.CYAN,Color.GREEN,Color.YELLOW,Color.PINK,Color.magenta,new Color(234,45,78),new Color(67,123,9)};
        for(int i=0;i<12;i++){
            JButton bt = new JButton();
            bt.setBackground(colors[i]);
            bt.setBorder(bb);
            bt.setPreferredSize(new Dimension(40,30));
            //Add button listener for each button
            bt.addActionListener(bl);
            bt.setOpaque(true);
            panelDownRight.add(bt);
        }
        this.setVisible(true);
    }

    //Save file into project file or image file
    public void saveFile(int type){
        //Get file (including name and path)
        JFileChooser chooser = new JFileChooser();
        chooser.showSaveDialog(null);
        File file =chooser.getSelectedFile();

        if(file==null){
            JOptionPane.showMessageDialog(null, "No file is selected");
        }else {

            try {
                if(type==1){
                    //Save as an jpg file
                    File file1 = new File(file.getAbsoluteFile()+".jpg");   //Add ".jpg"
                    md.savePic(file1);
                } else{
                    FileOutputStream fis = new FileOutputStream(file);
                    ObjectOutputStream oos = new ObjectOutputStream(fis);
                    //Add all shapes in list into output stream
                    oos.writeObject(md.getShapeList());
                    oos.close();
                };
                JOptionPane.showMessageDialog(null, "Save sucessfully");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
