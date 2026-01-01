package memory;

import java.util.NoSuchElementException;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        vectors = null;
    }

    public SharedMatrix(double[][] matrix) {
        if(matrix == null)
            throw new NullPointerException("matrix is null");

        vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++)
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
    }

    public void loadRowMajor(double[][] matrix) {
        if(matrix == null)
            throw new NullPointerException("matrix is null");

        acquireAllVectorWriteLocks(vectors);
        SharedVector[] newVectors = new SharedVector[matrix.length];
        for (int i = 0; i < newVectors.length; i++)
            newVectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);

        SharedVector[] oldVectors = vectors;
        vectors = newVectors;
        releaseAllVectorWriteLocks(oldVectors);
    }

    public void loadColumnMajor(double[][] matrix) {
        if (matrix == null)
            throw new NullPointerException("matrix is null");
        acquireAllVectorWriteLocks(vectors);
        SharedVector[] newVectors = new SharedVector[matrix[0].length];
        // vectors = new SharedVector[matrix[0].length];
        for (int col = 0; col < newVectors.length; col++) {
            double[] column = new double[matrix.length];
            for (int row = 0; row < matrix.length ; row++ ){
                column[row] = matrix[row][col];
            }
            newVectors[col] = new SharedVector(column, VectorOrientation.COLUMN_MAJOR);
        }
        SharedVector[] oldVectors = vectors;
        vectors = newVectors;
        releaseAllVectorWriteLocks(oldVectors);

    }

    public double[][] readRowMajor() {
        double[][] matrix;
        if(vectors[0].getOrientation() == VectorOrientation.ROW_MAJOR)
        {
            matrix = new double[vectors.length][];
            int row = 0;
            acquireAllVectorReadLocks(vectors);
            for(SharedVector v : vectors)
            {
                matrix[row] = new double[v.length()];
                for(int col = 0; col < v.length(); col++)
                    matrix[row][col] = v.get(col);
                row++;
            }
            releaseAllVectorReadLocks(vectors);
        }
        else
        {
            matrix = new double[vectors[0].length()][vectors.length];
            int col = 0;
            acquireAllVectorReadLocks(vectors);
            for(SharedVector v : vectors)
            {
                for(int row = 0; row < v.length(); row++)
                {
                    matrix[row][col] = v.get(row);
                }
                col++;
            }
            releaseAllVectorReadLocks(vectors);
        }
        return matrix;
    }


    public SharedVector get(int index) {
        if(length() <= index || index < 0)
            throw new IndexOutOfBoundsException("index out of bounds");

        return vectors[index];
    }

    public int length() {
        if(vectors == null)
            throw new NoSuchElementException();

        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        return vectors[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        if (vecs == null)
            return;

        for(SharedVector v : vecs)
            v.readLock();
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        if (vecs == null)
            return;

        for(SharedVector v : vecs)
            v.readUnlock();
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        if (vecs == null)
            return;

        for(SharedVector v : vecs)
            v.writeLock();
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        if (vecs == null)
            return;
        for(SharedVector v : vecs)
            v.writeUnlock();
    }

    public int getColsCount(){
        if(vectors[0].getOrientation() == VectorOrientation.COLUMN_MAJOR)
            return vectors.length;
        return vectors[0].length();
    }
    public int getRowsCount(){
        if(vectors[0].getOrientation() == VectorOrientation.ROW_MAJOR)
            return vectors.length;
        return vectors[0].length();
    }
}