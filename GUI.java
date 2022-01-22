import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame implements ActionListener {
    int ROWS = 5;
    int COLS = 5;

    int[][] boardState = new int[ROWS][COLS];
    JPanel[][] cells = new JPanel[ROWS][COLS];

    JMenuBar menu = new JMenuBar();

    JMenu settings = new JMenu("Options");

    JMenuItem animation = new JMenuItem("Animation");
    JMenuItem quickSolve = new JMenuItem("Quick Solve");
    JMenuItem clearBoard = new JMenuItem("Clear Board");

    Timer timer = new Timer(1, this);

    boolean startAnimation = false, quickSolver = false;

    // set pentamino shapes
    boolean [][] FShape = {{false, true, true}, {true, true, false}, {false, true, false}};
    boolean [][] IShape = {{true}, {true}, {true}, {true}, {true}};
    boolean [][] LShape = {{true, false}, {true, false}, {true, false}, {true, true}};
    boolean [][] NShape = {{false, true}, {false, true}, {true, true}, {true, false}};
    boolean [][] PShape = {{true, true, true},{true, true, false}};
    boolean [][] TShape = {{true, true, true}, {false, true, false}, {false, true, false}};
    boolean [][] UShape = {{true, false, true},{true, true, true}};
    boolean [][] VShape = {{true, false, false}, {true, false, false}, {true, true, true}};
    boolean [][] WShape = {{false, false, true}, {false, true, true}, {true, true, false}};
    boolean [][] XShape = {{false, true, false}, {true, true, true}, {false, true, false}};
    boolean [][] YShape = {{false, true, false, false},{true, true, true, true}};
    boolean [][] ZShape = {{true, true, false}, {false, true, false}, {false, true, true}};

    // create objects with names and dimensions from above
    Piece FPenta = new Piece("FShape", FShape, 1);
    Piece IPenta = new Piece("IShape", IShape, 2);
    Piece LPenta = new Piece("LShape", LShape, 3);
    Piece NPenta = new Piece("NShape", NShape, 4);
    Piece PPenta = new Piece("PShape", PShape, 5);
    Piece TPenta = new Piece("TShape", TShape, 6);
    Piece UPenta = new Piece("UShape", UShape, 7);
    Piece VPenta = new Piece("VShape", VShape, 8);
    Piece WPenta = new Piece("WShape", WShape, 9);
    Piece XPenta = new Piece("XShape", XShape, 10);
    Piece YPenta = new Piece("YShape", YShape, 11);
    Piece ZPenta = new Piece("ZShape", ZShape, 12);

    Piece[] animationArray = {LPenta, VPenta, PPenta, ZPenta, YPenta}; //pieces to be used in animation

    //initialize board & set up GUI
    public GUI(){
        super("Pentomino Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(ROWS,COLS,1,1));
        setSize(500,500);
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLS; j++){
                cells[i][j] = new JPanel();
                add(cells[i][j]);
                cells[i][j].setBackground(Color.GRAY.brighter());
            }
        }
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLS; j++){
                boardState[i][j] = 0;
            }
        }

        setJMenuBar(menu);
        menu.add(settings);

        settings.add(animation);
        settings.add(quickSolve);
        settings.add(clearBoard);

        animation.addActionListener(this);
        quickSolve.addActionListener(this);
        clearBoard.addActionListener(this);
    }
    // variables for animation
    boolean backtrack = false;
    boolean validMove;
    int a = 0;

    @Override
    public void actionPerformed(ActionEvent e) {
        // using an action event with a timer allows to iterate through the algorithm at a slower speed to create an animation
        Object source = e.getSource();
        if(!startAnimation && source == animation && !quickSolver){
            clearBoard(cells, ROWS, COLS, boardState);
            startAnimation = true;
            backtrack = false;
            a = 0;
            timer.start();
        }
        if(startAnimation){
            validMove = false;
            for(int i = animationArray[a].getInsertY(); i < ROWS; i++){
                for(int j = animationArray[a].getInsertX(); j < COLS; j++){
                    if(backtrack){
                        backtrack = false;
                        if(!validMove(animationArray[a], i, j+ 1)){
                            j = 0 ;
                            i++;
                        }
                        else
                            j++;
                    }
                    if(validMove(animationArray[a], i, j) && noOverlap(animationArray[a], i, j, boardState)){
                        validMove = true;
                        insertPiece(i, j, boardState, animationArray[a]);
                        //printBoardState(boardState);
                        //System.out.println("---");
                        colorBoard(boardState, cells);
                        if(isSolved(boardState)){
                            timer.stop();
                            startAnimation = false;
                            resetInsertion(animationArray);
                            resetDimensions(animationArray);
                            //System.out.println("Solved!!");
                        }
                        a++;
                        i = ROWS + 1;
                        j = COLS + 1;
                    }
                }
            }
            if(!validMove){
                animationArray[a].setInsertY(0);
                animationArray[a].setInsertX(0);
                if((animationArray[a].getOrientation() >= 0 && animationArray[a].getOrientation() < 3) && !backtrack){ // add && backtrack == false
                    animationArray[a].rotate();
                }
                else if(animationArray[a].getOrientation() == 3 && !backtrack){ // add && backtrack == false
                    animationArray[a].mirror();
                }
                else if((animationArray[a].getOrientation() > 3 && animationArray[a].getOrientation() < 7) && !backtrack) // add && backtrack == false
                    animationArray[a].rotate();
                else if(animationArray[a].getOrientation() == 7){ // this means we have exhausted all orientations and no valid move
                    backtrack = true;
                    resetOrientation(animationArray[a]);
                    a--;
                    removePiece(boardState, animationArray[a]);
                }
            }
        }
        if(source == quickSolve){
            if(startAnimation){
                System.out.println("Animation already in progress!");
            }

            else{
                clearBoard(cells, ROWS, COLS, boardState);
                colorBoard(boardState, cells);
                quickSolver = true;
                fillAlgorithm(animationArray, boardState);
                colorBoard(boardState, cells);
                quickSolver = false;
            }
        }
        if(source == clearBoard){
            clearBoard(cells, ROWS, COLS, boardState);
            colorBoard(boardState, cells);
        }
    }

// this function inserts a piece with a given row, col coordinate, the insertion point of the piece is top left coordinate
    public void insertPiece(int insertROW, int insertCOL, int[][] state, Piece piece){
        //insertion point will be bottom left of piece
        if (validMove(piece, insertROW, insertCOL) && noOverlap(piece, insertROW, insertCOL, state)){
            boolean[][] penta = piece.getDimensions();
            for(int i = 0; i < penta.length; i++){
                for(int j = 0; j < penta[i].length; j++){
                    if(penta[i][j]){
                        state[i + (insertROW - (penta.length - 1))][insertCOL + j] = piece.getPieceId();
                    }
                }
            }
            piece.setInsertX(insertCOL);
            piece.setInsertY(insertROW);
        }
    }

    public void colorBoard(int[][] state, JPanel[][] board){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                if (state[i][j] == 1){
                    board[i][j].setBackground(Color.RED);
                }
                else if (state[i][j] == 2){
                    board[i][j].setBackground(Color.ORANGE);
                }
                else if (state[i][j] == 3){
                    board[i][j].setBackground(Color.YELLOW);
                }
                else if (state[i][j] == 4){
                    board[i][j].setBackground(Color.GREEN);
                }
                else if (state[i][j] == 5){
                    board[i][j].setBackground(Color.BLUE);
                }
                else if (state[i][j] == 6){
                    board[i][j].setBackground(Color.PINK);
                }
                else if (state[i][j] == 7){
                    board[i][j].setBackground(Color.CYAN);
                }
                else if (state[i][j] == 8){
                    board[i][j].setBackground(Color.MAGENTA);
                }
                else if (state[i][j] == 9){
                    board[i][j].setBackground(Color.BLACK);
                }
                else if (state[i][j] == 10){
                    board[i][j].setBackground(Color.BLUE.brighter());
                }
                else if (state[i][j] == 11){
                    board[i][j].setBackground(Color.GRAY.darker());
                }
                else if (state[i][j] == 12){
                    board[i][j].setBackground(Color.CYAN.darker());
                }
                else if (state[i][j] == 13){
                    board[i][j].setBackground(Color.YELLOW);
                }
                else
                    board[i][j].setBackground(Color.gray.brighter());
            }
        }
    }

    public void clearBoard(JPanel[][] board, int sizeROW, int sizeCOL, int[][] state){
        for(int i = 0; i < sizeROW; i++){
            for(int j = 0; j < sizeCOL; j++){
                board[i][j].setBackground(Color.GRAY.brighter());
                state[i][j] = 0;
            }
        }
    }

    public void printBoardState(int[][] state){ //prints the 2D char array that keeps track of board state
        for(int i = 0; i < state.length; i++){
            for(int j = 0; j < state[i].length; j++){
                System.out.print(state[i][j]);
            }
            System.out.println();
        }
    }
    public void removePiece(int [][] state, Piece piece){
        //insertion point will always be bottom left of piece
        if (validMove(piece, piece.getInsertY(), piece.getInsertX())){
            boolean[][] penta = piece.getDimensions();
            for(int i = 0; i < penta.length; i++){
                for(int j = 0; j < penta[i].length; j++){
                    if(penta[i][j]){
                        state[i + (piece.getInsertY() - (penta.length - 1))][piece.getInsertX() + j] = 0;
                    }
                }
            }
        }
    }

public boolean validMove(Piece piece, int insertRow, int insertCol){
    //check for out of bounds condition
    boolean[][] dims = piece.getDimensions();
    if(insertRow >= ROWS)
        return false;
    if(insertRow - (dims.length - 1) < 0)
        return false;
    if(insertCol + dims[0].length > COLS)
        return false;
    if(insertCol < 0)
        return false;
    else
        return true;
}
public boolean noOverlap(Piece piece, int insertRow, int insertCol, int [][] state){
        boolean[][]dims = piece.getDimensions();
        for(int i = 0; i < dims.length; i++){
            for(int j = 0; j < dims[0].length; j++){
                if(state[i + (insertRow - (dims.length - 1))][insertCol + j] != 0 && dims[i][j]){
                    return false;
                }
            }
        }
        return true;
}

public boolean isSolved(int [][]state){
        for(int i = 0; i < state.length; i++){
            for(int j = 0; j < state[0].length; j++){
                if(state[i][j] == 0)
                    return false;
            }
        }
        return true;
}

public void fillAlgorithm(Piece[] pieces, int[][] state){
    boolean backtrack = false;
    boolean validMove;
    for(int a = 0; a < pieces.length; a++){
        while(pieces[a].getOrientation() < 8){
            validMove = false;
            for(int i = pieces[a].getInsertY(); i < ROWS; i++){
                for(int j = pieces[a].getInsertX(); j < COLS; j++){
                    if(backtrack){
                        backtrack = false;
                        if(!validMove(pieces[a], i, j+ 1)){
                            j = 0 ;
                            i++;
                        }
                        else
                            j++;
                    }
                    if(validMove(pieces[a], i, j) && noOverlap(pieces[a], i, j, state)){
                        validMove = true;
                        insertPiece(i, j, state, pieces[a]);
                        if(isSolved(state)){
                            resetInsertion(animationArray);
                            resetDimensions(animationArray);
                            return;
                        }
                         a++;
                         i = ROWS + 1;
                         j = COLS + 1;
                    }
                }
            }
           if(!validMove){
               pieces[a].setInsertY(0);
               pieces[a].setInsertX(0);
               if((pieces[a].getOrientation() >= 0 && pieces[a].getOrientation() < 3) && !backtrack){ // add && backtrack == false
                   pieces[a].rotate();
               }
               else if(pieces[a].getOrientation() == 3 && !backtrack){ // add && backtrack == false
                   pieces[a].mirror();
               }
               else if((pieces[a].getOrientation() > 3 && pieces[a].getOrientation() < 7) && !backtrack) // add && backtrack == false
                   pieces[a].rotate();
               else if(pieces[a].getOrientation() == 7){ // this means we have exhausted all orientations and no valid move
                   backtrack = true;
                   resetOrientation(pieces[a]);
                   a--;
                   removePiece(boardState, pieces[a]);
               }
           }
        }
    }
}

public  void resetDimensions(Piece[] pieces){
    for(int i = 0; i <pieces.length; i++){
        if(pieces[i].getPieceId() == 1)
            pieces[i].setDimensions(FShape);

        if(pieces[i].getPieceId() == 2)
            pieces[i].setDimensions(IShape);

        if(pieces[i].getPieceId() == 3)
            pieces[i].setDimensions(LShape);

        if(pieces[i].getPieceId() == 4)
            pieces[i].setDimensions(NShape);

        if(pieces[i].getPieceId() == 5)
            pieces[i].setDimensions(PShape);

        if(pieces[i].getPieceId() == 6)
            pieces[i].setDimensions(TShape);

        if(pieces[i].getPieceId() == 7)
            pieces[i].setDimensions(UShape);

        if(pieces[i].getPieceId() == 8)
            pieces[i].setDimensions(VShape);

        if(pieces[i].getPieceId() == 9)
            pieces[i].setDimensions(WShape);

        if(pieces[i].getPieceId() == 10)
            pieces[i].setDimensions(XShape);

        if(pieces[i].getPieceId() == 11)
            pieces[i].setDimensions(YShape);

        if(pieces[i].getPieceId() == 12)
            pieces[i].setDimensions(ZShape);

    }
}

public void resetOrientation(Piece piece){
    piece.rotate();
    piece.mirror();
    if(piece.getPieceId() == 1 || piece.getPieceId() == 2 ||piece.getPieceId() == 3 || piece.getPieceId() == 4
            || piece.getPieceId() == 5 || piece.getPieceId() == 11 || piece.getPieceId() == 12)
        // asymmetrical
        piece.setOrientation(0);
    else if(piece.getPieceId() == 10){
        // symmetrical on all sides
        piece.setOrientation(7);
    }
    else // two lines of symmetry
        piece.setOrientation(4);
}

public void resetInsertion(Piece pieces[]){
        for(int i = 0; i < pieces.length; i++){
            pieces[i].setInsertX(0);
            pieces[i].setInsertY(0);
        }
}

    public static void main(String[] args){ //driver
        GUI window = new GUI();
        window.setVisible(true);
    }
}