package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        // TODO: store vector data and its orientation
        if (vector == null)
            throw new IllegalArgumentException("vector cant be null");
        this.vector = vector;
        this.orientation = orientation;

    }

    public double get(int index) {
        if (vector.length <= index || index < 0)
            throw new IllegalArgumentException("index out of bounds");

        return vector[index];
    }

    public int length() {
        // TODO: return vector length
        return vector.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return vector orientation
        return orientation;
    }

    public void writeLock() {
        // TODO: acquire write lock
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        // TODO: release write lock
        lock.writeLock().unlock();
    }

    public void readLock() {
        // TODO: acquire read lock
        lock.readLock().lock();
    }

    public void readUnlock() {
        // TODO: release read lock
        lock.readLock().unlock();
    }

    public void transpose() {
        // TODO: transpose vector
        if(orientation == VectorOrientation.ROW_MAJOR)
            orientation = VectorOrientation.COLUMN_MAJOR;
        else
            orientation = VectorOrientation.ROW_MAJOR;
    }

    public void add(SharedVector other) {
        if (length() != other.length())
            throw new IllegalArgumentException("Given vector is not in the correct size");

        writeLock();
        other.readLock();

        try {
            for (int i = 0; i < length(); i++)
                vector[i] = vector[i] + other.get(i);
        }
        finally {
            writeUnlock();
            other.readUnlock();
        }
    }

    public void negate() {
        writeLock();
        try {
            for (int i = 0; i < length(); i++)
                vector[i] = vector[i] * -1;
        }
        finally {
            writeUnlock();
        }
    }

    public double dot(SharedVector other) {
        // TODO: compute dot product (row · column)
        if (length() != other.length())
            throw new IllegalArgumentException("Given vector is not in the correct size");

        if (orientation != VectorOrientation.ROW_MAJOR || other.getOrientation() != VectorOrientation.COLUMN_MAJOR)
            throw new IllegalArgumentException("Given vector is not column based");

        double output = 0;
        readLock();
        other.readLock();
        try {
            for (int i = 0; i < length(); i++)
                output += vector[i] * other.get(i);
        }
        finally {
            readUnlock();
            other.readUnlock();
        }

        return output;
    }

    public void vecMatMul(SharedMatrix matrix) {
        // TODO: compute row-vector × matrix
        if(orientation != VectorOrientation.ROW_MAJOR)
            throw new IllegalArgumentException("This vector is not row based");

        synchronized (matrix)
        {
            if(matrix.getOrientation() != VectorOrientation.COLUMN_MAJOR)
                matrix.loadColumnMajor(matrix.readRowMajor());
        }

        double[] newVector = new double[matrix.length()];
        for(int i = 0 ; i < matrix.length(); i++)
        {
            SharedVector vector = matrix.get(i);
            vector.readLock();
            try {
                newVector[i] = dot(vector);
            }
            finally {
                vector.readUnlock();
            }
        }
        writeLock();
        try{
            vector = newVector;
        }
        finally {
            writeUnlock();
        }
    }
}
