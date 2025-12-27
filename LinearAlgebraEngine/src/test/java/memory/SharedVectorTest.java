package memory;

import static org.junit.jupiter.api.Assertions.*;
class SharedVectorTest {
    double[] firstVector = {1, 2, 3, 4, 5};
    double[] secondVector = {6, 7, 8, 9, 10};
    double[] thirdVector = {11, 12, 13, 14, 15};
    double[] fourthVector = {16, 17, 18, 19, 20, 21};

    SharedVector sharedVectorFirstObject;
    SharedVector sharedVectorSecondObject;
    SharedVector sharedVectorThirdObject;
    SharedVector sharedVectorFourthObject;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        sharedVectorFirstObject = new SharedVector(firstVector, VectorOrientation.ROW_MAJOR);
        sharedVectorSecondObject = new SharedVector(secondVector, VectorOrientation.ROW_MAJOR);
        sharedVectorThirdObject = new SharedVector(thirdVector, VectorOrientation.COLUMN_MAJOR);
        sharedVectorFourthObject = new SharedVector(fourthVector, VectorOrientation.ROW_MAJOR);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void get() {
        for (int i = 0; i < firstVector.length; i++)
            assertEquals(firstVector[i], sharedVectorFirstObject.get(i));

        for (int i = 0; i < secondVector.length; i++)
            assertEquals(secondVector[i], sharedVectorSecondObject.get(i));

        for (int i = 0; i < thirdVector.length; i++)
            assertEquals(thirdVector[i], sharedVectorThirdObject.get(i));

        for (int i = 0; i < fourthVector.length; i++)
            assertEquals(fourthVector[i], sharedVectorFourthObject.get(i));
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