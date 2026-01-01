package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        if (vector == null)
            throw new IllegalArgumentException("vector cant be null");
        this.vector = vector;
        this.orientation = orientation;
    }

    public double get(int index) {
        if (vector.length <= index || index < 0)
            throw new IllegalArgumentException("index out of bounds");

        readLock();
        double result = vector[index];
        readUnlock();
        return result;
    }

    public int length() {
        return vector.length;
    }

    public VectorOrientation getOrientation() {
        return orientation;
    }

    public void writeLock() {
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        lock.writeLock().unlock();
    }

    public void readLock() {
        lock.readLock().lock();
    }

    public void readUnlock() {
        lock.readLock().unlock();
    }

    public void transpose() {
        writeLock();
        if(orientation == VectorOrientation.ROW_MAJOR)
            orientation = VectorOrientation.COLUMN_MAJOR;
        else
            orientation = VectorOrientation.ROW_MAJOR;
        writeUnlock();
    }

    public void add(SharedVector other) {
        if (length() != other.length())
            throw new IllegalArgumentException("Given vector is not in the correct size");
        writeLock();
        for (int i = 0; i < length(); i++)
            vector[i] = vector[i] + other.get(i);
        writeUnlock();
    }

    public void negate() {
        writeLock();
        for (int i = 0; i < length(); i++)
            vector[i] = vector[i] * -1;
        writeUnlock();
    }

    public double dot(SharedVector other) {
        if (length() != other.length())
            throw new IllegalArgumentException("Given vector is not in the correct size");

        if (orientation == other.orientation)
            throw new IllegalArgumentException("Vectors are not in the correct orientation");

        double output = 0;
        readLock();
        for (int i = 0; i < length(); i++)
            output += vector[i] * other.get(i);
        readUnlock();
        return output;
    }

    public void vecMatMul(SharedMatrix matrix) {
        if(orientation != VectorOrientation.ROW_MAJOR)
            throw new IllegalArgumentException("This vector is not row based");

        if(matrix.getOrientation() != VectorOrientation.COLUMN_MAJOR)
            matrix.loadColumnMajor(matrix.readRowMajor());

        double[] newVector = new double[matrix.length()];
        for(int i =0 ; i < matrix.length(); i++)
            newVector[i] = dot(matrix.get(i));

        writeLock();
        vector = newVector;
        writeUnlock();
    }
}
