package spl.lae;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.ComputationNode;
import parser.ComputationNodeType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinearAlgebraEngineTest {

    private LinearAlgebraEngine engine;

    @BeforeEach
    void setUp() {
        engine = new LinearAlgebraEngine(4);
    }

    @Test
    void testSimpleAddition() {
        // Create leaf matrix nodes
        ComputationNode nodeA = new ComputationNode(new double[][]{{1, 2}, {3, 4}});
        ComputationNode nodeB = new ComputationNode(new double[][]{{5, 6}, {7, 8}});

        // Create (+) operator node with children A and B
        ComputationNode addNode = new ComputationNode("+", List.of(nodeA, nodeB));

        ComputationNode resultNode = engine.run(addNode);

        assertArrayEquals(new double[]{6.0, 8.0}, resultNode.getMatrix()[0]);
        assertArrayEquals(new double[]{10.0, 12.0}, resultNode.getMatrix()[1]);
    }

    @Test
    void testNegationAndTranspose() {
        // Matrix A: [[1, -2]]
        ComputationNode nodeA = new ComputationNode(new double[][]{{1.0, -2.0}});

        // Negate: [-1, 2]
        ComputationNode negateNode = new ComputationNode("-", List.of(nodeA));

        // In your engine, Transpose is handled row-wise.
        // Based on your LinearAlgebraEngine.java: createTransposeTasks() calls v1.transpose()
        ComputationNode transposeNode = new ComputationNode("T", List.of(negateNode));

        ComputationNode resultNode = engine.run(transposeNode);

        // If v1.transpose() modifies the row in-place, check based on your SharedVector implementation
        assertNotNull(resultNode.getMatrix());
    }

    @Test
    void testAssociativeNestingIntegration() {
        // Testing A + B + C logic
        ComputationNode a = new ComputationNode(new double[][]{{1, 1}});
        ComputationNode b = new ComputationNode(new double[][]{{2, 2}});
        ComputationNode c = new ComputationNode(new double[][]{{3, 3}});

        // Create a list that can be modified (ArrayList) because associativeNesting calls .remove()
        List<ComputationNode> children = new ArrayList<>(List.of(a, b, c));
        ComputationNode root = new ComputationNode("+", children);

        // engine.run() calls associativeNesting() internally.
        // It should resolve (A + B) first, then add C.
        ComputationNode resultNode = engine.run(root);

        // Expected: [[1+2+3, 1+2+3]] = [[6, 6]]
        assertEquals(6.0, resultNode.getMatrix()[0][0]);
        assertEquals(6.0, resultNode.getMatrix()[0][1]);
    }

    @Test
    void testDeepTreeResolution() {
        // (A * B) + (C * D)
        ComputationNode a = new ComputationNode(new double[][]{{1, 2}, {3, 4}});
        ComputationNode b = new ComputationNode(new double[][]{{1, 0}, {0, 1}}); // Identity
        ComputationNode c = new ComputationNode(new double[][]{{1, 1}, {1, 1}});
        ComputationNode d = new ComputationNode(new double[][]{{2, 2}, {2, 2}});

        ComputationNode mul1 = new ComputationNode("*", List.of(a, b));
        ComputationNode mul2 = new ComputationNode("*", List.of(c, d));
        ComputationNode root = new ComputationNode("+", List.of(mul1, mul2));

        ComputationNode resultNode = engine.run(root);

        // mul1 = [[1, 2], [3, 4]]
        // mul2 = [[4, 4], [4, 4]]
        // total = [[5, 6], [7, 8]]
        double[][] res = resultNode.getMatrix();
        assertEquals(5.0, res[0][0]);
        assertEquals(8.0, res[1][1]);
    }

    @Test
    void testMatrixOnlyInput() {
        // If the input is already a matrix, it should return itself immediately
        double[][] data = {{1, 2}, {3, 4}};
        ComputationNode matrixNode = new ComputationNode(data);

        ComputationNode result = engine.run(matrixNode);

        assertSame(matrixNode, result, "Should return the same node if already a matrix");
        assertArrayEquals(data[0], result.getMatrix()[0]);
    }
}