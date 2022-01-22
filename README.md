# Pentomino Solver
This project uses a backtracking algorithm to cover an mxn board with pentominoes. Pentominoes are 5-square polygons on a flat plane. By default, the program uses a 5x5 board with 5 pieces. Larger boards with more pieces take much longer to solve since this algorithm tries every possible move with every piece.  Different pieces and board sizes can be hard-coded. This can be done by changing the values for ROWS and COLS and by changing the pieces within the animation array. If there is a solution to fully cover the board, this algorithm will find it.

Options:
1. Animate the algorithm adding pieces to the board
2. "Quick Solve" skips the animation for a quicker result
