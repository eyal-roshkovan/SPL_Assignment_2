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

        vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++)
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
    }

    public void loadColumnMajor(double[][] matrix) {
        if (matrix == null)
            throw new NullPointerException("matrix is null");

        vectors = new SharedVector[matrix[0].length];
        for (int col = 0; col < vectors.length; col++) {
            double[] column = new double[matrix.length];
            for (int row = 0; row < matrix.length ; row++ ){
                column[row] = matrix[row][col];
            }
            vectors[col] = new SharedVector(column, VectorOrientation.COLUMN_MAJOR);
        }
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
                    matrix[row][col] = v.get(col);
                }
                col++;
            }
            releaseAllVectorReadLocks(vectors);
        }
        return matrix;
    }


    public SharedVector get(int index) {
        // TODO: return vector at index
        if(length() <= index || index < 0)
            throw new IndexOutOfBoundsException("index out of bounds");

        return vectors[index];
    }

    public int length() {
        if(vectors != null)
            throw new NoSuchElementException();

        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        return vectors[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
        for(SharedVector v : vecs)
            v.readLock();
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
        for(SharedVector v : vecs)
            v.readUnlock();
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
        for(SharedVector v : vecs)
            v.writeLock();
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
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
