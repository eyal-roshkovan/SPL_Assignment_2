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
        // TODO: resolve computation tree step by step until final matrix is produced
        ComputationNode result = null;
        // TODO: wait for clarification on the "only matrix" input
        computationRoot.associativeNesting();
        ComputationNode newNode = computationRoot.findResolvable();

        while (newNode != null) {
            loadAndCompute(newNode);
            double[][] res = leftMatrix.readRowMajor();
            newNode.resolve(res);

            result = new ComputationNode(res);
            newNode = computationRoot.findResolvable();
        }

    return result;
    }

    public void loadAndCompute(ComputationNode node) {
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
        ComputationNodeType type = node.getNodeType();
        List<Runnable> tasks = null;
        List<ComputationNode> children = node.getChildren();

        double[][] matrix1 = children.get(0).getMatrix();
        double[][] matrix2 = null;

        leftMatrix.loadRowMajor(matrix1);

        if(children.size() == 2)
         matrix2 = children.get(1).getMatrix();

        if (type == ComputationNodeType.MULTIPLY) {
            rightMatrix.loadColumnMajor(matrix2);
            tasks = createMultiplyTasks();
        }
        else if (type == ComputationNodeType.ADD) {
            rightMatrix.loadRowMajor(matrix2);
            tasks = createAddTasks();
        }
        else if (type == ComputationNodeType.NEGATE)
            tasks = createNegateTasks();

        else if (type == ComputationNodeType.TRANSPOSE)
            tasks = createTransposeTasks();

        this.executor.submitAll(tasks);

    }

    public List<Runnable> createAddTasks() {
        // TODO: return tasks that perform row-wise addition
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
        // TODO: return tasks that perform row × matrix multiplication
        int row = leftMatrix.getRowsCount();
        if(row != rightMatrix.getColsCount())
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
        // TODO: return tasks that negate rows
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
        // TODO: return tasks that transpose rows
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
        // TODO: return summary of worker activity
        return executor.getWorkerReport();
    }
}
