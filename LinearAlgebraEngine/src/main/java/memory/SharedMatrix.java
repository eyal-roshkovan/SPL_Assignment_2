package memory;

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
        if(matrix == null)
            throw new NullPointerException("matrix is null");

        vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++)
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.COLUMN_MAJOR);
    }

    public double[][] readRowMajor() {
        double[][] matrix = new double[vectors.length][];

        if(vectors[0].getOrientation() == VectorOrientation.ROW_MAJOR)
        {
            int row = 0;
            for(SharedVector v : vectors)
            {
                for(int col = 0; col < v.length(); col++)
                {
                    matrix[row][col] = v.get(col);
                }
                row++;
            }
        }
        else
        {
            int col = 0;
            for(SharedVector v : vectors)
            {
                for(int row = 0; row < v.length(); row++)
                {
                    matrix[row][col] = v.get(col);
                }
                col++;
            }
        }
        return matrix;
    }


    public SharedVector get(int index) {
        // TODO: return vector at index
        if(length() < index || index < 0)
            throw new IndexOutOfBoundsException("index out of bounds");

        return vectors[index];
    }

    public int length() {
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
}
