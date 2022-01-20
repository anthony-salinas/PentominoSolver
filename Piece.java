public class Piece {
    private String pieceName;
    private boolean[][] dimensions;
    private int pieceId, insertX, insertY, orientation;
    public Piece(String name, boolean[][] dims, int id) {
        pieceName = name;
        dimensions = dims;
        pieceId = id;
        orientation = 0;
    }

    public boolean[][] getDimensions() {
        return dimensions;
    }

    public int getPieceId(){
        return pieceId;
    }

    public void setInsertX(int xInsert) {
        insertX = xInsert;
    }

    public int getInsertX(){
        return insertX;
    }

    public void setInsertY(int yInsert) {
        insertY = yInsert;
    }

    public int getInsertY(){
        return insertY;
    }

    public int getOrientation(){
        return orientation;
    }

    public void setOrientation(int num){
        orientation = num;
    }

    public void rotate(){ // rotates Piece Dimensions 90 degrees
        boolean[][] temp = new boolean[dimensions[0].length][dimensions.length]; // flip rows and cols
        for(int i = 0; i < dimensions[0].length; i++){
            for(int j = 0; j < dimensions.length; j++){
                    temp[i][j] = dimensions[(dimensions.length - 1)  - j][i];
            }
        }
        orientation++;
        dimensions = temp;
    }

    public void mirror(){
        boolean[][] temp = new boolean[dimensions.length][dimensions[0].length];//rows and cols stay the same for mirror
        for(int i = 0; i < dimensions.length; i++){
            for(int j = 0; j < dimensions[i].length; j++){
                temp[i][j] = dimensions[i][(dimensions[0].length - 1) - j];
            }
        }
        orientation++;
        dimensions = temp;
    }
}