
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*
This is the most Important part of my project! I have commented some codes which I have tried my best to accomplish some goals.
Though I know I need to delete useless codes when I hand this homework, I want to keep it. It's not only code, but my attempts.
 */


public class MyDraw extends JPanel {
    private ArrayList<MyShape> shapeList;
    private ArrayList<MyShape> deletedShapeList;
    private int shapeType;
    /*
    0 for line, 1 for rectangle, 2 for roundrectangle,3 for oval, 4 for picker,
    5 for brush, 6 for pencil, 7 for spray gun, 8 for erasor, 9 for polygon,
    10 for fill, 11 for  character, 12 for move, 13 for circle
    */
    private MyShape currentShape;               //When I create a shape, i use this currentShape to new different object
    private MouseMonitor ma;                    //Mouse moniter in this panel, which is very important
    private int x1,x2,y1,y2,old_x,old_y;        //x1,y1 for pressed positon; x2,y2 for dragged position; old_x,old_y for polygon
    private boolean flag;                       //To judge whether it is the first point in polygon
    private JButton bt;                         //This is the current Color button in the lower left corner
    private int[][] fillJud;                    //Judge array in BFS
    private int width;                          //Stoke's width
    private Color color;                        //Shapes's color
    private Font font;                          //Word's font
    private String sentences = " ";             //Word
    private int moveJud = 0;                    //judge whether have moved shape or not

    public MyDraw(JButton bt){
        shapeType=0;
        flag = true;
        this.bt = bt;
        width = 1;
        color = Color.black;
        ma=new MouseMonitor();
        this.addMouseListener(ma);
        this.addMouseMotionListener(ma);
        shapeList=new ArrayList<MyShape>();
        deletedShapeList=new ArrayList<MyShape>();
    }

    //Standard set and get method.
    public ArrayList<MyShape> getShapeList() {
        return shapeList;
    }
    public void setType(int type){
        this.shapeType = type;
    }
    public void setColor(Color color){
        this.color = color;
    }
    public void setFont(Font f){
        this.font = f;
    }
    public void setSentences(String s){
        this.sentences = s;
    }
    public void setWidth(int w) {
        this.width = w;
    }

    //Mouse Monitor is very important
    private class MouseMonitor extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            //When the mouse pressed, we should consider the user will create a new shape. So we should save the current condition and delete the shapes in deletedList
            deletedShapeList.clear();
            //get the mouse position when mouse pressed
            x1=e.getX();
            y1=e.getY();
            //Judge what operation user choose (create rectangle? pick color? fill? etc..)
            /*
                If the current operation is creating a new shape, then we just new an object fot currentShape, and when we draged to change the properties of currentShape.
                If the current operation is move shapes, than we just need to find which has been selected.
             */
            switch (shapeType){
                case 0:
                    currentShape = new MyLine(x1,y1,0,0,color,width);
                    break;
                case 1:
                    currentShape = new MyRectangle(x1,y1,0,0,color,width);
                    break;
                case 2:
                    currentShape = new MyRoundRectangle(x1,y1,0,0,40,40,color,width);
                    break;
                case 3:
                    currentShape = new MyOval(x1,y1,0,0,color,width);
                    break;
                case 5:
                    currentShape = new MyPencil(x1,y1,color,10);
                    break;
                case 6:
                    currentShape = new MyPencil(x1,y1,color,width);
                    break;
                case 7:
                    currentShape = new MySprayGun(x1,y1,color,width);
                    break;
                case 8:
                    currentShape = new MyPencil(x1,y1,Color.white,16);
                    break;
                case 11:
                    String s = sentences;
                    currentShape = new MyWord(x1,y1,s,font,color);
                    shapeList.add(currentShape);
                    repaint();
                    sentences="";
                    break;
                /*
                    If the current operation is to move some shapes, then we should know which shape will be selected.
                    So I find from the last in shapeList, if the shape contains the point mouse clicked, than add this
                    shape into moveShapeList. However, it is not easy as I considered. Since some shapes use two points
                    to draw(like rectangle, oval, etc.), and some shapes is a set of point(like pencil, fill, etc.) As a
                    result, I need to use two different ways to judge whether the point is in the shape or not. What's more,
                    if a shape is upon another shape, what should I do when the user clicked shared zone. My solution is
                    move only one TWO-POINTS shape and as many as possible MULTIPOINTS shape.
                 */
                case 12:
                    moveJud=0;
                    for(int i=shapeList.size()-1 ;i >=0; i--){
                        if(shapeList.get(i) instanceof MyFill){
                            MyFill tempFill = (MyFill) shapeList.get(i);
                            if(tempFill.points.contains(new Point(x1,y1))){
                                currentShape = tempFill;
                                deletedShapeList.add(tempFill);
                                shapeList.remove(i);
                                moveJud=1;
                                break;
                            }
                        } else if(shapeList.get(i) instanceof MySprayGun){
                            MySprayGun tempSG = (MySprayGun) shapeList.get(i);
                            if(tempSG.points.contains(new Point(x1,y1))){
                                currentShape = tempSG;
                                deletedShapeList.add(tempSG);
                                shapeList.remove(i);
                                moveJud=1;
                                break;
                            }
                        }  else if(shapeList.get(i) instanceof MyPencil){
                            MyPencil tempPC = (MyPencil) shapeList.get(i);
                            if(tempPC.points.contains(new Point(x1,y1))){
                                currentShape = tempPC;
                                deletedShapeList.add(tempPC);
                                shapeList.remove(i);
                                moveJud=1;
                                break;
                            }
                        } else{
                            currentShape = (MyShape) shapeList.get(i);
                            if(currentShape.containsPix(x1,y1)){
                                deletedShapeList.add(currentShape);
                                shapeList.remove(i);
                                moveJud=1;
                                break;
                            }
                        }
                    }
                    break;
                case 13:
                    currentShape = new MyCircle(x1,y1,0,0,color,width);
                    break;
            }
        }

        public void mouseMoved(MouseEvent e){
            x1=e.getX();
            y1=e.getY();
            /*
                Drawing word is somewhat different from others. For others, user can know real-time look of the canvas,
                however, if the operation is drawing a word, users cannot know what looks like after adding the word.
                So I add this method in mouse moved.
             */
            if(shapeType==11){
                String s = sentences;
                currentShape = new MyWord(x1,y1,s,font,color);
                repaint();
            }
            //Same for erasor
            else if(shapeType==8){
                currentShape = new MyRectangle(x1-4,y1-4,8,8,Color.white,8);
                repaint();
            }
        }

        public void mouseReleased(MouseEvent e) {
            int x=e.getXOnScreen();
            int y=e.getYOnScreen();
            x2=e.getX();
            y2=e.getY();
            switch (shapeType){
                case 0:
                    currentShape.upgradePoint(x1,y1,x2,y2);
                    repaint();
                    shapeList.add(currentShape);
                    break;
                case 1:
                    currentShape.upgradePoint(Math.min(x2, x1),Math.min(y2, y1), Math.abs(x2-x1),Math.abs(y1-y2));
                    repaint();
                    shapeList.add(currentShape);
                    //currentShape=null;
                    break;
                case 2:
                    currentShape.upgradePoint(Math.min(x2, x1),Math.min(y2, y1), Math.abs(x2-x1),Math.abs(y1-y2));
                    repaint();
                    shapeList.add(currentShape);
                    break;
                case 3:
                    currentShape.upgradePoint(Math.min(x2, x1),Math.min(y2, y1), Math.abs(x2-x1),Math.abs(y1-y2));
                    repaint();
                    shapeList.add(currentShape);
                    break;
                case 4:
                    BufferedImage image1 = giveMeImage();
                    Color bicolor=pickColor(x2,y2,image1);
                    color = bicolor;
                    bt.setBackground(bicolor);
                    break;
                case 5:
                    currentShape.addPoint(x2,y2);
                    repaint();
                    shapeList.add(currentShape);
                    break;
                case 6:
                    currentShape.addPoint(x2,y2);
                    repaint();
                    shapeList.add(currentShape);
                    break;
                case 7:
                    currentShape.addPoint(x2,y2);
                    repaint();
                    shapeList.add(currentShape);
                    break;
                case 8:
                    currentShape.addPoint(x2,y2);
                    repaint();
                    shapeList.add(currentShape);
                    break;
                //For polygon, if this is the first point, we should record the old_x and old_y, else just add the point to MyPolygon class
                case 9:
                    if(flag){
                        old_x = x2;
                        old_y = y2;
                        currentShape = new MyPolygon(x2,y2,color,width);
                        flag=false;
                    }
                    else{
                        currentShape.addPoint(x2,y2);
                    }
                    repaint();
                    break;
                //For the function of filling, I use BFS algorithm to get which point should be paint
                /*
                    This is somewhat difficult for me and very interesting to accomplish this feature. Since before attempting this function,
                    I just use two points or several points(Polygon) to record a shape. However filling a paint should add plenty of points,
                    which seems not very to be very consistent to other class.

                    And the beginning, I made some mistakes. For example, in order to get the color of a position, every time I want to
                    "pick" color, I use robot to make a 1*1(pixel) screenshot and get the (0,0) position. And without a doubt, it's very slow.
                    And then, I change the way that make a full JPanel(MyDraw) screenshot( private BufferdImage giveMeImage() ), and than use
                    this image to pick color. And then use BFS to record points to be painted and add to MyFill.

                 */
                case 10:
                    BufferedImage image = giveMeImage();
                    currentShape = new MyFill(x2, y2, color, 1);
                    fillJud = new int[507][337];
                    Color currentColor = pickColor(x2, y2, image);
                    BFS(x2, y2, currentColor, image);
                    repaint();
                    shapeList.add(currentShape);
                    break;
                case 12:
                    if(moveJud == 1){
                        shapeList.add(currentShape);
                    }
                    break;
                case 13:
                    currentShape.upgradePoint(Math.min(x2, x1),Math.min(y2, y1), Math.max(Math.abs(x2-x1),Math.abs(y1-y2)), Math.max(Math.abs(x2-x1),Math.abs(y1-y2)));
                    repaint();
                    shapeList.add(currentShape);
                    break;
            }
            if(shapeType!=9){
                currentShape = null;
            }
        }

        public void mouseDragged(MouseEvent e) {
            /*
                When the mouse dragged, what I need to do is update the current position and repaint the shape.
             */
            x2=e.getX();
            y2=e.getY();
            if(shapeType == 0){
                currentShape.upgradePoint(x1,y1,x2,y2);
            } else if(shapeType>0&&shapeType<4){
                currentShape.upgradePoint(Math.min(x2, x1), Math.min(y2, y1), Math.abs(x2 - x1), Math.abs(y1 - y2));
                /*
                    I want to have a keyboard listener to listen whether users pressed SHIFT, but it doesn't work.
                 */

//                addKeyListener(new KeyAdapter() {
//                    @Override
//                    public void keyPressed(KeyEvent e) {
//                        if(e.getKeyCode() == KeyEvent.VK_SHIFT){
//                            currentShape.upgradePoint(Math.min(x2, x1),Math.min(y2, y1), Math.min(Math.abs(x2-x1),Math.abs(y1-y2)), Math.min(Math.abs(x2-x1),Math.abs(y1-y2)));
//                        } else {
//                            currentShape.upgradePoint(Math.min(x2, x1), Math.min(y2, y1), Math.abs(x2 - x1), Math.abs(y1 - y2));
//                        }
//                    }
//                });

            } else if(shapeType>4 && shapeType <9){
                currentShape.addPoint(x2,y2);
            } else if(shapeType==12){
                if(moveJud == 1){
                    currentShape.moveWhere(x2-x1,y2-y1);
                    x1=x2;y1=y2;            //Without, the distance will be accumulated.
                }
            } else if(shapeType==13){
                currentShape.upgradePoint(Math.min(x2, x1),Math.min(y2, y1), Math.max(Math.abs(x2-x1),Math.abs(y1-y2)), Math.max(Math.abs(x2-x1),Math.abs(y1-y2)));
            }
            repaint();
        }

        public void mouseClicked(MouseEvent e) {
            /*
                If the number of mouse clicking is 2, than add a new line between the last point and the "oldest" point.
             */
            int count =e.getClickCount();
            if(count==2 && shapeType == 9){
                currentShape.addPoint(old_x,old_y);
                flag=true;
                shapeList.add(currentShape);
                repaint();
                currentShape = null;
            }
        }

    }

    //Renew a paint canvas
    public void reset(){
        shapeList.clear();
        deletedShapeList.clear();
        repaint();
    }

    //Make an image of whole JPanel
    private BufferedImage giveMeImage(){
        Dimension imageSize = this.getSize();
        BufferedImage image = new BufferedImage(imageSize.width,
                imageSize.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        this.paint(g);
        g.dispose();
        return image;
    }

    //Save the canvas as a jpg file
    public void savePic(File f){
        Dimension imageSize = this.getSize();
        BufferedImage image = new BufferedImage(imageSize.width,
                imageSize.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        this.paint(g);
        g.dispose();
        try {
            ImageIO.write(image, "jpg", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Step backward
    public void revoke(){
        if(shapeList.size()>0) {
            //pop a shape from deletedShapeList and push it into shapeList
            deletedShapeList.add(shapeList.get(shapeList.size() - 1));
            shapeList.remove(shapeList.size() - 1);
            repaint();
        }
    }

    //Step forward
    public void voke(){
        if(deletedShapeList.size()>0) {
            //pop a shape from shapeList and push it into deletedShapeList
            shapeList.add(deletedShapeList.get(deletedShapeList.size() - 1));
            deletedShapeList.remove(deletedShapeList.size() - 1);
            repaint();
        }
    }

    //Override paintComponent for repaint
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        //Draw every shapes in shapeList
        for(int i=0; i<shapeList.size(); i++){
            shapeList.get(i).Draw(g);
        }
        //Then draw the current shape
        if(currentShape!=null){
            currentShape.Draw(g);
        }
    }

    //BFS to find the points need to be painted and add them into MyFill
    private void BFS(int x1, int y1, Color c, BufferedImage image){
        Point temppoint;
        ArrayList<Point> sp = new ArrayList<Point>();   //Use this ArrayList as a Queue
        sp.add(new Point(x1,y1));
        fillJud[x1][y1] = 1;
        while(sp.size()!=0){            //While the queue is not empty, then get the front point and find jacent points.
            temppoint = sp.get(0);
            sp.remove(0);
            int tempx=temppoint.x, tempy = temppoint.y;
            if(isInsideImage(tempx+1,tempy) && fillJud[tempx+1][tempy] == 0 && pickColor(tempx+1,tempy,image).equals(c)){
                sp.add(new Point(tempx+1,tempy));
                fillJud[tempx+1][tempy] = 1;
            }
            if(isInsideImage(tempx-1,tempy) && fillJud[tempx-1][tempy] == 0 && pickColor(tempx-1,tempy,image).equals(c)){
                sp.add(new Point(tempx-1,tempy));
                fillJud[tempx-1][tempy] = 1;
            }
            if(isInsideImage(tempx,tempy+1) && fillJud[tempx][tempy+1] == 0 && pickColor(tempx,tempy+1,image).equals(c)){
                sp.add(new Point(tempx,tempy+1));
                fillJud[tempx][tempy+1] = 1;
            }
            if(isInsideImage(tempx,tempy-1) && fillJud[tempx][tempy-1] == 0 && pickColor(tempx,tempy-1,image).equals(c)){
                sp.add(new Point(tempx,tempy-1));
                fillJud[tempx][tempy-1] = 1;
            }
            currentShape.addPoint(tempx, tempy);
        }
    }

    //Get the point(x,y) in image
    private Color pickColor(int x, int y, BufferedImage bi){
        int c=bi.getRGB(x, y);
        Color bicolor=new Color(c);
        return bicolor;
    }

    //To judge whether the point(x,y) is in the JPanel or not
    private boolean isInsideImage(int x, int y){
        if(x>=0 && x<500 && y>=0 && y<330){
            return true;
        } else{
            return false;
        }
    }
}
