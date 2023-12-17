package ir.sbpro.sudokusolver.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Scanner;

public class SudoNode {
    private int[][] cells;
    private ArrayList[][] domains;

    public int[][] getCells() { return cells;}
    public ArrayList[][] getDomains() { return domains;}
    
    public SudoNode(int[][] cells, ArrayList[][] domains){
        this.cells = cells;
        this.domains = domains;
    }
    
    public SudoNode(int[][] cells){
        this.cells = cells;
        clean();
    }

    public SudoNode() {
        clear();
    }
    
    public void clear(){
        cells = new int[9][];
        for(int i=0; 9>i; i++) cells[i] = new int[9];
        clean();        
    }
    
    public void clean(){
        domains = new ArrayList[9][];
        for(int i=0; 9>i; i++){
            domains[i] = new ArrayList[9];
            for(int j=0; 9>j; j++){
                domains[i][j] = new ArrayList();
                for(int k=0; 9>k; k++) domains[i][j].add((Object) (k+1));
            }
        }
        
        for(int i=0; 9>i; i++){
            for(int j=0; 9>j; j++){
                chainUpdateDomains(i, j);
            }
        }
    }
    
    private void chainUpdateDomains(int i, int j){
        if(cells[i][j] == 0) return;
        domains[i][j] = null;
        
        for(int k=0; 9>k; k++) if(k!=j && domains[i][k]!=null) domains[i][k].remove((Object) cells[i][j]);
        for(int k=0; 9>k; k++) if(k!=i && domains[k][j]!=null) domains[k][j].remove((Object) cells[i][j]);

        int sr = ((int)(i/3))*3;
        int sc = ((int)(j/3))*3;
        for(int m=0; 3>m; m++){
            for(int n=0; 3>n; n++){
                if(domains[sr+m][sc+n] != null)
                    domains[sr+m][sc+n].remove((Object) cells[i][j]);
            }
        }        
    }
    
    public boolean isValid(){
        for(int i=0; 9>i; i++){
            for(int j=0; 9>j; j++){
                if(isDuplicateCell(i, j, cells[i][j])) return false;
            }
        }
        return true;        
    }
    
    public boolean isSolved(){
        for(int i=0; 9>i; i++){
            for(int j=0; 9>j; j++){
                if(cells[i][j] == 0) return false;
            }
        }
        if(!isValid()) return false;
        return true;
    }
    
    private boolean isDuplicateCell(int i, int j, int value){
        if(value==0) return false;
        for(int k=0; 9>k; k++) if(k!=j && cells[i][k]==value) return true;
        for(int k=0; 9>k; k++) if(k!=i && cells[k][j]==value) return true;
        
        int sr = i/3*3;
        int sc = j/3*3;
        for(int m=0; 3>m; m++){
            for(int n=0; 3>n; n++){
                if((sr+m)!=i && (sc+n)!=j && cells[sr+m][sc+n]==value) return true;
            }
        }
        return false;
    }
    
    public SudoNode getChild(int icell, int jcell, int value){
        int[][] childCells = new int[9][];
        for(int i=0; 9>i; i++) childCells[i] = new int[9];
        for(int i=0; 9>i; i++) for(int j=0; 9>j; j++) childCells[i][j] = cells[i][j];
        childCells[icell][jcell] = value;
        
        ArrayList[][] childDomains = new ArrayList[9][];
        for(int i=0; 9>i; i++){
            childDomains[i] = new ArrayList[9];
            for(int j=0; 9>j; j++){
                if(domains[i][j] == null) childDomains[i][j] = null;
                else childDomains[i][j] = (ArrayList) domains[i][j].clone();
            }
        }
        
        SudoNode childNode = new SudoNode(childCells, childDomains);
        childNode.chainUpdateDomains(icell, jcell);
        return childNode;
    }
    
    public int getNumConflicts(){
        int sumc=0;
        for(int i=0; 9>i; i++){
            for(int j=0; 9>j; j++){
                if(cells[i][j]==0) continue;
                
                for(int k=j+1; 9>k; k++) if(cells[i][k] == cells[i][j]) sumc++;
                for(int k=i+1; 9>k; k++) if(cells[k][j] == cells[i][j]) sumc++;
                
                int maxr = i/3*3 + 3;
                int maxc = j/3*3 + 3;
                for(int m=i; maxr>m; m++){
                    int n;
                    if(m==i) n=j;
                    else n=j/3*3;
                    for(; maxc>n; n++){
                        if(m==i || n==j) continue;
                        if(cells[m][n] == cells[i][j]) sumc++;
                    }
                }
            }
        }
        return sumc;
    }
    
    public static int getNumConflicts(SudoNode node, int i, int j, int value){
        int oldValue = node.getCells()[i][j];
        node.getCells()[i][j] = value;
        int sumc = node.getNumConflicts();
        node.getCells()[i][j] = oldValue;
        return sumc;
    }

    public void logSudoku(){
        for(int i=0; 9>i; i++){
            StringBuilder stringBuilder = new StringBuilder();
            for(int j=0; 9>j; j++){
                if(!stringBuilder.toString().isEmpty()) stringBuilder.append(", ");
                stringBuilder.append(cells[i][j]);
            }
            Log.d("XQQQSudo" + i, "[" + stringBuilder.toString() + "]");
        }
    }

    public void logDomains(){
        for(int i=0; 9>i; i++){
            for(int j=0; 9>j; j++){
                if(domains[i][j] == null){
                    Log.d("XQQQDomain (" + i + ", " + j + ")", "Null");
                    continue;
                }

                StringBuilder stringBuilder = new StringBuilder();
                for(int k=0; domains[i][j].size() > k; k++){
                    if(!stringBuilder.toString().isEmpty()) stringBuilder.append(", ");
                    stringBuilder.append(domains[i][j].get(k));
                }

                Log.d("XQQQDomain (" + i + ", " + j + ")",
                        "[" + stringBuilder.toString() + "]");
            }
        }
    }
}
