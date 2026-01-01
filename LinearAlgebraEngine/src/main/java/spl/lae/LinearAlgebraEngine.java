package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.ArrayList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        ComputationNode result = null;
        try {
            if(computationRoot.getNodeType() == ComputationNodeType.MATRIX)
                return computationRoot;

            computationRoot.associativeNesting();
            ComputationNode newNode = computationRoot.findResolvable();
            while (newNode != null) {
                loadAndCompute(newNode);
                double[][] res = leftMatrix.readRowMajor();
                newNode.resolve(res);

                result = new ComputationNode(res);
                newNode = computationRoot.findResolvable();
            }
            System.out.println(getWorkerReport());
        }
        finally {
            try {
                executor.shutdown();
            }
            catch (InterruptedException e) {
            }
        }
        return result;
    }

    public void loadAndCompute(ComputationNode node) {
        ComputationNodeType type = node.getNodeType();
        List<Runnable> tasks = null;
        List<ComputationNode> children = node.getChildren();

        double[][] matrix1 = children.get(0).getMatrix();
        double[][] matrix2 = null;

        leftMatrix.loadRowMajor(matrix1);

        if(children.size() == 2)
            matrix2 = children.get(1).getMatrix();

        if (type == ComputationNodeType.MULTIPLY && children.size() == 2) {
            rightMatrix.loadColumnMajor(matrix2);
            tasks = createMultiplyTasks();
        }
        else if (type == ComputationNodeType.ADD && children.size() == 2) {
            rightMatrix.loadRowMajor(matrix2);
            tasks = createAddTasks();
        }
        else if (type == ComputationNodeType.NEGATE && children.size() == 1)
            tasks = createNegateTasks();

        else if (type == ComputationNodeType.TRANSPOSE && children.size() == 1)
            tasks = createTransposeTasks();
        else
            throw new IllegalArgumentException("Incorrect number of operands");

        executor.submitAll(tasks);

    }

    public List<Runnable> createAddTasks() {
        int row = leftMatrix.getRowsCount();
        int col = leftMatrix.getColsCount();
        if(row != rightMatrix.getRowsCount() || col != rightMatrix.getColsCount())
            throw new IllegalArgumentException("The dimensions are not compatible");

        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < row ; i++) {
            final int rowIndex = i;

            tasks.add(() -> {
                SharedVector v1 = leftMatrix.get(rowIndex);
                SharedVector v2 = rightMatrix.get(rowIndex);

                v1.add(v2);
            });
        }
        return tasks;

    }

    public List<Runnable> createMultiplyTasks() {
        int row = leftMatrix.getRowsCount();
        if(leftMatrix.getColsCount() != rightMatrix.getRowsCount())
            throw new IllegalArgumentException("The dimensions are not compatible");

        List<Runnable> tasks = new ArrayList<>();
        for(int i = 0; i< row; i++) {
            final int rowIndex = i;

            tasks.add(() -> {
                SharedVector v1 = leftMatrix.get(rowIndex);
                v1.vecMatMul(rightMatrix);
            });
        }
        return tasks;
    }

    public List<Runnable> createNegateTasks() {
        int row = leftMatrix.getRowsCount();
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < row ; i++) {
           final int rowIndex = i;

            tasks.add(() -> {
                SharedVector v1 = leftMatrix.get(rowIndex);
                v1.negate();
            });
        }
        return tasks;
    }

    public List<Runnable> createTransposeTasks() {
        int row = leftMatrix.getRowsCount();
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < row ; i++) {
            final int rowIndex = i;

            tasks.add(() -> {
                SharedVector v1 = leftMatrix.get(rowIndex);
                v1.transpose();
            });
        }
        return tasks;
    }

    public String getWorkerReport() {
        return executor.getWorkerReport();
    }
}