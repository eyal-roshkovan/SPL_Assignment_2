package memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SharedMatrixTest {

    double[][] rowData = {
            {1, 2, 3},
            {4, 5, 6}
    };

    double[][] columnData = {
            {1, 4},
            {2, 5},
            {3, 6}
    };

    SharedMatrix rowMatrix;
    SharedMatrix columnMatrix;
    SharedMatrix emptyMatrix;

    @BeforeEach
    void setUp() {
        rowMatrix = new SharedMatrix(rowData);
        columnMatrix = new SharedMatrix();
        columnMatrix.loadColumnMajor(columnData);
        emptyMatrix = new SharedMatrix();
    }

    @Test
    void loadRowMajor() {
        double[][] newData = {{10, 20}, {30, 40}, {50, 60}};
        rowMatrix.loadRowMajor(newData);

        assertEquals(3, rowMatrix.length());
        assertEquals(VectorOrientation.ROW_MAJOR, rowMatrix.getOrientation());
        assertEquals(10, rowMatrix.get(0).get(0));
        assertEquals(60, rowMatrix.get(2).get(1));
    }

    @Test
    void loadColumnMajor() {
        double[][] newData = {
                {1, 2},
                {3, 4},
                {5, 6}
        };
        columnMatrix.loadColumnMajor(newData);

        assertEquals(2, columnMatrix.length());
        assertEquals(VectorOrientation.COLUMN_MAJOR, columnMatrix.getOrientation());

        assertEquals(1, columnMatrix.get(0).get(0));
        assertEquals(3, columnMatrix.get(0).get(1));
        assertEquals(5, columnMatrix.get(0).get(2));
    }

    @Test
    void readRowMajor() {
        double[][] rowResult = rowMatrix.readRowMajor();
        for (int i = 0; i < rowData.length; i++) {
            assertArrayEquals(rowData[i], rowResult[i]);
        }

        double[][] colResult = columnMatrix.readRowMajor();
        assertEquals(3, colResult.length);
        assertEquals(2, colResult[0].length);
        assertEquals(1, colResult[0][0]);
        assertEquals(2, colResult[1][0]);
    }

    @Test
    void get() {
        SharedVector v0 = rowMatrix.get(0);
        SharedVector v1 = rowMatrix.get(1);

        assertNotNull(v0);
        assertEquals(3, v0.length());
        assertEquals(1, v0.get(0));
        assertEquals(4, v1.get(0));

        assertThrows(IndexOutOfBoundsException.class, () -> rowMatrix.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> rowMatrix.get(-1));
    }

    @Test
    void length() {
        assertEquals(2, rowMatrix.length());
        assertEquals(2, columnMatrix.length());
        assertThrows(IllegalStateException.class, () -> emptyMatrix.length());

    }

    @Test
    void getOrientation() {
        assertEquals(VectorOrientation.ROW_MAJOR, rowMatrix.getOrientation());
        assertEquals(VectorOrientation.COLUMN_MAJOR, columnMatrix.getOrientation());
    }
}