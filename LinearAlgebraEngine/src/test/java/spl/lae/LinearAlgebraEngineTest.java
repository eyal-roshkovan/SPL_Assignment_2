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
        ComputationNode nodeA = new ComputationNode(new double[][]{{1, 2}, {3, 4}});
        ComputationNode nodeB = new ComputationNode(new double[][]{{5, 6}, {7, 8}});

        ComputationNode addNode = new ComputationNode("+", List.of(nodeA, nodeB));

        ComputationNode resultNode = engine.run(addNode);

        assertArrayEquals(new double[]{6.0, 8.0}, resultNode.getMatrix()[0]);
        assertArrayEquals(new double[]{10.0, 12.0}, resultNode.getMatrix()[1]);
    }

    @Test
    void testNegationAndTranspose() {
        ComputationNode nodeA = new ComputationNode(new double[][]{{1.0, -2.0}});

        ComputationNode negateNode = new ComputationNode("-", List.of(nodeA));

        ComputationNode transposeNode = new ComputationNode("T", List.of(negateNode));

        ComputationNode resultNode = engine.run(transposeNode);

        assertNotNull(resultNode.getMatrix());
    }

    @Test
    void testAssociativeNestingIntegration() {
        ComputationNode a = new ComputationNode(new double[][]{{1, 1}});
        ComputationNode b = new ComputationNode(new double[][]{{2, 2}});
        ComputationNode c = new ComputationNode(new double[][]{{3, 3}});

        List<ComputationNode> children = new ArrayList<>(List.of(a, b, c));
        ComputationNode root = new ComputationNode("+", children);

        ComputationNode resultNode = engine.run(root);

        assertEquals(6.0, resultNode.getMatrix()[0][0]);
        assertEquals(6.0, resultNode.getMatrix()[0][1]);
    }

    @Test
    void testDeepTreeResolution() {
        ComputationNode a = new ComputationNode(new double[][]{{1, 2}, {3, 4}});
        ComputationNode b = new ComputationNode(new double[][]{{1, 0}, {0, 1}});
        ComputationNode c = new ComputationNode(new double[][]{{1, 1}, {1, 1}});
        ComputationNode d = new ComputationNode(new double[][]{{2, 2}, {2, 2}});

        ComputationNode mul1 = new ComputationNode("*", List.of(a, b));
        ComputationNode mul2 = new ComputationNode("*", List.of(c, d));
        ComputationNode root = new ComputationNode("+", List.of(mul1, mul2));

        ComputationNode resultNode = engine.run(root);

        double[][] res = resultNode.getMatrix();
        assertEquals(5.0, res[0][0]);
        assertEquals(8.0, res[1][1]);
    }

    @Test
    void testMatrixOnlyInput() {
        double[][] data = {{1, 2}, {3, 4}};
        ComputationNode matrixNode = new ComputationNode(data);

        ComputationNode result = engine.run(matrixNode);

        assertSame(matrixNode, result, "Should return the same node if already a matrix");
        assertArrayEquals(data[0], result.getMatrix()[0]);
    }
}