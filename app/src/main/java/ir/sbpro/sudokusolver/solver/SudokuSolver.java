package ir.sbpro.sudokusolver.solver;

import ir.sbpro.sudokusolver.models.SudoNode;
import ir.sbpro.sudokusolver.models.SudokuResult;

public class SudokuSolver {
    public static SudokuResult forwardCheckingSolve(SudoNode sudoku){
        long startTime = System.nanoTime();
        SudoNode result = SudokuSolutions.forwardChecking(sudoku);
        long endTime = System.nanoTime();
        long runtime = endTime - startTime;
        return new SudokuResult(result, runtime, SudokuSolutions.createdNodes);
    }
    
    public static SudokuResult forwardCheckingMRVSolve(SudoNode sudoku){
        long startTime = System.nanoTime();
        SudoNode result = SudokuSolutions.forwardCheckingWithMVR(sudoku);
        long endTime = System.nanoTime();
        long runtime = endTime - startTime;
        return new SudokuResult(result, runtime, SudokuSolutions.createdNodes);
    }
    
    public static SudokuResult minimumConflictsSolve(SudoNode sudoku){
        long startTime = System.nanoTime();
        SudoNode result = SudokuSolutions.minimumConflicts(sudoku);
        long endTime = System.nanoTime();
        long runtime = endTime - startTime;
        return new SudokuResult(result, runtime, SudokuSolutions.createdNodes);
    }
}
