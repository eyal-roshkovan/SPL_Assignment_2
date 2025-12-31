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
        rowMatrix = new SharedMatrix(rowData); // Should default to row-major logic
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
        // loading 3x2 column data should result in 2 vectors of length 3
        double[][] newData = {
                {1, 2},
                {3, 4},
                {5, 6}
        };
        columnMatrix.loadColumnMajor(newData);

        assertEquals(2, columnMatrix.length());
        assertEquals(VectorOrientation.COLUMN_MAJOR, columnMatrix.getOrientation());

        // First vector should be first column: [1, 3, 5]
        assertEquals(1, columnMatrix.get(0).get(0));
        assertEquals(3, columnMatrix.get(0).get(1));
        assertEquals(5, columnMatrix.get(0).get(2));
    }

    @Test
    void readRowMajor() {
        // Test row-major internal storage
        double[][] rowResult = rowMatrix.readRowMajor();
        for (int i = 0; i < rowData.length; i++) {
            assertArrayEquals(rowData[i], rowResult[i]);
        }

        // Test column-major internal storage (should be converted back to row-major)
        // If columnData was loaded as Column Major, readRowMajor should return the original matrix shape
        double[][] colResult = columnMatrix.readRowMajor();
        assertEquals(3, colResult.length); // 2 rows
        assertEquals(2, colResult[0].length); // 3 columns
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
        assertEquals(3, columnMatrix.length());
        assertEquals(0, emptyMatrix.length());
    }

    @Test
    void getOrientation() {
        assertEquals(VectorOrientation.ROW_MAJOR, rowMatrix.getOrientation());
        assertEquals(VectorOrientation.COLUMN_MAJOR, columnMatrix.getOrientation());
    }
}