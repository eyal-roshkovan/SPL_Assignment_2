package memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @BeforeEach
    void setUp() {
        sharedVectorFirstObject = new SharedVector(copyArray(firstVector), VectorOrientation.ROW_MAJOR);
        sharedVectorSecondObject = new SharedVector(copyArray(secondVector), VectorOrientation.ROW_MAJOR);
        sharedVectorThirdObject = new SharedVector(copyArray(thirdVector), VectorOrientation.COLUMN_MAJOR);
        sharedVectorFourthObject = new SharedVector(copyArray(fourthVector), VectorOrientation.ROW_MAJOR);
    }


    double[] copyArray(double[] array) {
        double[] copyArray = new double[array.length];
        System.arraycopy(array, 0, copyArray, 0, array.length);
        return copyArray;
    }

    @Test
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

    @Test
    void length() {
        assertEquals(firstVector.length, sharedVectorFirstObject.length());
        assertEquals(secondVector.length, sharedVectorSecondObject.length());
        assertEquals(thirdVector.length, sharedVectorThirdObject.length());
        assertEquals(fourthVector.length, sharedVectorFourthObject.length());
    }

    @Test
    void getOrientation() {
        assertEquals(VectorOrientation.ROW_MAJOR, sharedVectorFirstObject.getOrientation());
        assertEquals(VectorOrientation.ROW_MAJOR, sharedVectorSecondObject.getOrientation());
        assertEquals(VectorOrientation.COLUMN_MAJOR, sharedVectorThirdObject.getOrientation());
        assertEquals(VectorOrientation.ROW_MAJOR, sharedVectorFourthObject.getOrientation());
    }

    @Test
    void writeLock() {
    }

    @Test
    void writeUnlock() {
    }

    @Test
    void readLock() {
    }

    @Test
    void readUnlock() {
    }

    @Test
    void transpose() {
        sharedVectorFirstObject.transpose();
        sharedVectorSecondObject.transpose();
        sharedVectorThirdObject.transpose();
        sharedVectorFourthObject.transpose();

        assertEquals(VectorOrientation.COLUMN_MAJOR, sharedVectorFirstObject.getOrientation());
        assertEquals(VectorOrientation.COLUMN_MAJOR, sharedVectorSecondObject.getOrientation());
        assertEquals(VectorOrientation.ROW_MAJOR, sharedVectorThirdObject.getOrientation());
        assertEquals(VectorOrientation.COLUMN_MAJOR, sharedVectorFourthObject.getOrientation());
    }

    @Test
    void add() {
        assertDoesNotThrow(() -> sharedVectorFirstObject.add(sharedVectorSecondObject));
        for (int i = 0; i < firstVector.length; i++)
            assertEquals(firstVector[i] + secondVector[i], sharedVectorFirstObject.get(i));

        assertThrows(IllegalArgumentException.class, () -> sharedVectorFirstObject.add(sharedVectorThirdObject)); // not same orientation

        assertThrows(IllegalArgumentException.class, () -> sharedVectorFirstObject.add(sharedVectorFourthObject)); // not same length
    }

    @Test
    void negate() {
        assertDoesNotThrow(() -> sharedVectorFirstObject.negate());
        assertDoesNotThrow(() -> sharedVectorSecondObject.negate());
        assertDoesNotThrow(() -> sharedVectorThirdObject.negate());
        assertDoesNotThrow(() -> sharedVectorFourthObject.negate());

        for (int i = 0; i < firstVector.length; i++)
            assertEquals(-firstVector[i], sharedVectorFirstObject.get(i));


        for (int i = 0; i < secondVector.length; i++)
            assertEquals(-secondVector[i], sharedVectorSecondObject.get(i));


        for (int i = 0; i < thirdVector.length; i++)
            assertEquals(-thirdVector[i], sharedVectorThirdObject.get(i));


        for (int i = 0; i < fourthVector.length; i++)
            assertEquals(-fourthVector[i], sharedVectorFourthObject.get(i));

    }

    @Test
    void dot() {
        double firstMulRes = 0;
        double secondMulRes = 0;
        double thirdMulRes = 0;

        for (int i = 0; i < firstVector.length; i++)
            firstMulRes += firstVector[i] * thirdVector[i];

        for (int i = 0; i < secondVector.length; i++)
            secondMulRes += secondVector[i] * thirdVector[i];

        for (int i = 0; i < secondVector.length ;i++)
            thirdMulRes += secondVector[i] * firstVector[i];

        assertThrows(IllegalArgumentException.class, () -> sharedVectorFirstObject.dot(sharedVectorFourthObject));
        assertThrows(IllegalArgumentException.class, () -> sharedVectorSecondObject.dot(sharedVectorFourthObject));

        assertEquals(firstMulRes, sharedVectorThirdObject.dot(sharedVectorFirstObject));
        assertEquals(firstMulRes, sharedVectorFirstObject.dot(sharedVectorThirdObject));
        assertEquals(secondMulRes, sharedVectorSecondObject.dot(sharedVectorThirdObject));
        assertEquals(secondMulRes, sharedVectorThirdObject.dot(sharedVectorSecondObject));
        assertEquals(thirdMulRes, sharedVectorFirstObject.dot(sharedVectorSecondObject));
        assertEquals(thirdMulRes, sharedVectorSecondObject.dot(sharedVectorFirstObject));
    }

    @Test
    void vecMatMul() {
        // First Test Case
        double[][] firstMulMatrix = {{1, 6}, {2, 7}, {3, 8}, {4, 9}, {5, 10}};
        SharedMatrix firstMatrix = new SharedMatrix(firstMulMatrix);
        assertDoesNotThrow(() -> sharedVectorFirstObject.vecMatMul(firstMatrix));
        assertEquals(2, sharedVectorFirstObject.length());
        assertEquals(VectorOrientation.ROW_MAJOR, sharedVectorFirstObject.getOrientation());
        assertEquals(55, sharedVectorFirstObject.get(0));
        assertEquals(130, sharedVectorFirstObject.get(1));

        // Second Test Case
        double[][] secondMulMatrix = {{1, 6}, {2, 7}, {3, 8}, {4, 9}};
        SharedMatrix secondMatrix = new SharedMatrix(secondMulMatrix);
        assertThrows(IllegalArgumentException.class, () -> sharedVectorSecondObject.vecMatMul(secondMatrix)); // dimensions are not compatible

    }

    @Test
    void vecMatMulDimensionMismatch() {
        // Vector is 1x5
        double[][] matrixData = {
                {1, 2},
                {3, 4},
                {5, 6}
        };
        SharedMatrix matrix = new SharedMatrix(matrixData);

        assertThrows(IllegalArgumentException.class, () -> {
            sharedVectorFirstObject.vecMatMul(matrix);
        }, "Should throw exception when vector columns != matrix rows");
    }

    @Test
    void vecMatMulWrongOrientation() {
        // sharedVectorThirdObject is COLUMN_MAJOR (5x1)
        double[][] matrixData = {{1, 1}, {1, 1}, {1, 1}, {1, 1}, {1, 1}}; // 5x2
        SharedMatrix matrix = new SharedMatrix(matrixData);

        assertThrows(IllegalArgumentException.class, () -> {
            sharedVectorThirdObject.vecMatMul(matrix);
        }, "Cannot multiply a COLUMN_MAJOR vector by a matrix (v*A)");
    }

    @Test
    void vecMatMulIdentity() {
        double[][] identityData = {
                {1, 0, 0, 0, 0},
                {0, 1, 0, 0, 0},
                {0, 0, 1, 0, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 0, 0, 1}
        };
        SharedMatrix identity = new SharedMatrix(identityData);

        sharedVectorFirstObject.vecMatMul(identity);

        assertEquals(5, sharedVectorFirstObject.length());
        for (int i = 0; i < firstVector.length; i++)
            assertEquals(firstVector[i], sharedVectorFirstObject.get(i));
    }

    @Test
    void vecMatMulToScalar() {
        double[][] columnMatrix = {{1}, {1}, {1}, {1}, {1}};
        SharedMatrix matrix = new SharedMatrix(columnMatrix);

        sharedVectorFirstObject.vecMatMul(matrix);

        assertEquals(1, sharedVectorFirstObject.length());
        assertEquals(15.0, sharedVectorFirstObject.get(0));
    }

}