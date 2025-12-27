package memory;

import static org.junit.jupiter.api.Assertions.*;
class SharedVectorTest {
    SharedVector sharedVectorFirstObject;
    SharedVector sharedVectorSecondObject;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        double[] firstVector = {1, 2, 3, 4, 5};
        double[] secondVector = {6, 7, 8, 9, 10};
        sharedVectorFirstObject = new SharedVector(firstVector, VectorOrientation.ROW_MAJOR);
        sharedVectorSecondObject = new SharedVector(secondVector, VectorOrientation.ROW_MAJOR);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void get() {
    }

    @org.junit.jupiter.api.Test
    void length() {
    }

    @org.junit.jupiter.api.Test
    void getOrientation() {
    }

    @org.junit.jupiter.api.Test
    void writeLock() {
    }

    @org.junit.jupiter.api.Test
    void writeUnlock() {
    }

    @org.junit.jupiter.api.Test
    void readLock() {
    }

    @org.junit.jupiter.api.Test
    void readUnlock() {
    }

    @org.junit.jupiter.api.Test
    void transpose() {
    }

    @org.junit.jupiter.api.Test
    void add() {
    }

    @org.junit.jupiter.api.Test
    void negate() {
    }

    @org.junit.jupiter.api.Test
    void dot() {
    }

    @org.junit.jupiter.api.Test
    void vecMatMul() {
    }
}