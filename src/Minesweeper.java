import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;


/*
    TODO:
     - safety net (clear starting area so game isnt stupid)
     - show all bombs when you lose (bugged)
 */

public class Minesweeper {

    static class Coord{
        int x,y;
        Coord(int x, int y){
            this.x = x;
            this.y = y;
        }
        Coord(){
            x = y = 0;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void setX(int x) {
            this.x = x;
        }
    }

    public static final int SIDE_LENGTH = 15;
    public static final int BOMB_COUNT = 50;
    public static final Random rand = new Random();

    private static JFrame frame;
    private static JPanel panel;
    private static JButton[][] field; //whats being displayed
    private static int[][] trueValues; // what the actual number values are, -1 = bomb, -2 is already marked tile
    private static ArrayList<Coord> bombCoords;
    private static boolean isGameInProgress;
    private static boolean safetyNetTriggered;

    public static void init(){

        frame = new JFrame("cuck minesweeper");
        panel =  new JPanel();
        field = new JButton[SIDE_LENGTH][SIDE_LENGTH];
        trueValues = new int[SIDE_LENGTH][SIDE_LENGTH];
        bombCoords = new ArrayList<>();
        isGameInProgress = true;
        safetyNetTriggered = false;
        //initialize window


        //initialize layout
        GridLayout grid = new GridLayout(SIDE_LENGTH,SIDE_LENGTH);
        panel.setLayout(grid);

        //place bombs
        for(int placed = 0; placed < BOMB_COUNT; placed++){
            int row = rand.nextInt(SIDE_LENGTH);
            int col = rand.nextInt(SIDE_LENGTH);
            trueValues[row][col] = -1;
            bombCoords.add(new Coord(row,col));
            //raise the values of all surrounding non-bomb tiles by 1
            for(int i = -1; i <= 1; i++){
                for(int j = -1; j <= 1; j++){
                    if (row + i < 0 || row + i == 15 || col + j < 0 || col + j == 15) continue; //array index will be out of bounds
                    if (trueValues[row + i][col + j] != -1){
                        trueValues[row + i][col + j]++;
                    }
                }
            }
        }

        //give function to all buttons
        for(int row = 0; row < SIDE_LENGTH; row++){
            for(int col = 0; col < SIDE_LENGTH; col++){
                int finalRow = row;
                int finalCol = col;
                field[row][col] = new JButton();
                field[row][col].addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        //placing flags on right click
                        if (e.getButton() == MouseEvent.BUTTON3){
                            if (field[finalRow][finalCol].getText().equals("")){
                                field[finalRow][finalCol].setText("⚑");
                            }
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }
                });

                field[row][col].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!isGameInProgress) return;
                        if (trueValues[finalRow][finalCol] == -2) return;

                        if (!safetyNetTriggered){
                            System.out.println("Hit safe at x :" + finalRow + ", y :" +  finalCol + " Surrounded by " + trueValues[finalRow][finalCol] + " bombs");
                            findFreeSquares(finalRow, finalCol);
                            safetyNetTriggered = true;
                        }
                        //this is a bomb
                        else if (trueValues[finalRow][finalCol] == -1){
                            isGameInProgress = false;
                            System.out.println("Hit bomb at x: " + finalRow + ", y: " +  finalCol);
                            lose();
                            return;
                        }
                        else{
                            field[finalRow][finalCol].setText(String.valueOf(trueValues[finalRow][finalCol]));
                            trueValues[finalRow][finalCol] = -2;
                        }



                        checkForWin();


                    }
                });
                panel.add(field[row][col]);
            }
        }

        //initialize window
        frame.setSize(1000,1000);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(panel);
        frame.pack();
    }

    public static void clearScreen(){
        for(int row = 0; row < SIDE_LENGTH; row++) {
            for (int col = 0; col < SIDE_LENGTH; col++) {
                trueValues[row][col] = 0;
                field[row][col].setText("");
            }
        }
    }

    public static boolean checkForWin(){
        for(int row = 0; row < SIDE_LENGTH; row++) {
            for (int col = 0; col < SIDE_LENGTH; col++) {
                if (trueValues[row][col] < -1){ //this tile isn't a bomb or already marked
                    return false;
                }
            }
        }
        return true;
    }

    public static void lose(){
        System.out.println("You lose!");
        for(Coord c : bombCoords){
            field[c.getX()][c.getY()].setText("B");
        }
    }

    //QOL where we flood fill to find all non bombs nearby if no cleared tiles within a 5x5 radius
    public static void findFreeSquares(int row, int col){
        FFSRecursive(row, col, 0); //steps keeps the fill within a certain radius
    }

    //clears a 3x3 area of squares
    public static void safetyNet(int row, int col){
        for(int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

            }
        }
    }

    private static void FFSRecursive(int row, int col, int steps){
        if (steps > 5) return;
        if (row == -1 || row == 16 || col == -1 || col == 16) return; //array index will be out of bounds
        if (trueValues[row][col] < 0 || trueValues[row][col] > 2) return;
        field[row][col].setText(String.valueOf(trueValues[row][col]));
        trueValues[row][col] = -2;
        FFSRecursive(row + 1, col, steps + 1);
        FFSRecursive(row - 1, col, steps + 1);
        FFSRecursive(row , col + 1, steps + 1);
        FFSRecursive(row + 1, col - 1, steps + 1);

    }

    public static void main(String[] args) {
        init();
    }

}
