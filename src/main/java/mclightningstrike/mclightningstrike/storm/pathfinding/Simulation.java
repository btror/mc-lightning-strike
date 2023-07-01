package mclightningstrike.mclightningstrike.storm.pathfinding;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Simulation {
    private final int[][][] simulationLightningZone;
    private final int[] simulationStrikeStart;
    private final int[] simulationStrikeTarget;

    private Node[][][] simulationNodeLightningZone;
    private Node simulationNodeStrikeStart;
    private Node simulationNodeStrikeTarget;
    private Node simulationNodeStrikeCurrent;

    private final PriorityQueue<Node> openList;
    private final ArrayList<Node> closedList;

    /**
     * Constructor.
     * <p>
     * Creates a storm simulation.
     * <p>
     * Simulation Integer Mappings:
     * 0 = open space (can be explored in pathfinding)
     * 1 = blocked space (cannot be explored in pathfinding)
     * 2 = visited space (already explored in pathfinding)
     * 3 = final path space (space in the final path)
     * 4 = start space
     * 5 = target space
     */
    public Simulation(
            int[][][] simulationLightningZone,
            int[] simulationStrikeStart,
            int[] simulationStrikeTarget
    ) {
        this.simulationLightningZone = simulationLightningZone;
        this.simulationStrikeStart = simulationStrikeStart;
        this.simulationStrikeTarget = simulationStrikeTarget;

        this.simulationNodeLightningZone = new Node[this.simulationLightningZone.length][this.simulationLightningZone[0].length][this.simulationLightningZone[0][0].length];

        this.openList = new PriorityQueue<>(10, new NodeComparator());
        this.closedList = new ArrayList<>();
    }

    /**
     * Kick off storm simulation pathfinding.
     */
    public void start() {
        simulationNodeStrikeCurrent = new Node(
                simulationStrikeStart[0],
                simulationStrikeStart[1],
                simulationStrikeStart[2],
                0
        );
        simulationNodeStrikeTarget = new Node(
                simulationStrikeTarget[0],
                simulationStrikeTarget[1],
                simulationStrikeTarget[2],
                0
        );

        simulationNodeLightningZone[simulationStrikeStart[0]][simulationStrikeStart[1]][simulationStrikeStart[2]] = simulationNodeStrikeCurrent;
        simulationNodeLightningZone[simulationStrikeTarget[0]][simulationStrikeTarget[1]][simulationStrikeTarget[2]] = simulationNodeStrikeTarget;


        for (int i = 0; i < simulationLightningZone.length; i++) {
            for (int j = 0; j < simulationLightningZone[i].length; j++) {
                for (int k = 0; k < simulationLightningZone[i][j].length; k++) {
                    if (simulationLightningZone[i][j][k] == 0) {
                        Node node = new Node(i, j, k, 0);
                        simulationNodeLightningZone[i][j][k] = node;
                    }
                    if (simulationLightningZone[i][j][k] == 1) {
                        Node node = new Node(i, j, k, 1);
                        simulationNodeLightningZone[i][j][k] = node;
                    }
                }
            }
        }

        int g = calculateG(simulationNodeStrikeCurrent);
        simulationNodeStrikeCurrent.setG(g);

        int h = calculateH(simulationNodeStrikeCurrent);
        simulationNodeStrikeCurrent.setH(h);

        simulationNodeStrikeCurrent.setF();

        simulationNodeStrikeStart = simulationNodeStrikeCurrent;

        openList.add(simulationNodeStrikeCurrent);
    }

    /**
     * Creates the final pathfinding path (supposed to emulate the storm bolt).
     *
     * @return final path found.
     */
    public boolean getPath() {
        boolean pathFound = true;
        while (!openList.isEmpty() && !simulationNodeStrikeCurrent.equals(simulationNodeStrikeTarget)) {
            simulationNodeStrikeCurrent = openList.peek();
            openList.remove(openList.peek());

            if (simulationNodeStrikeCurrent.equals(simulationNodeStrikeTarget)) {
                closedList.add(simulationNodeStrikeCurrent);

                ArrayList<Node> path = generatePath();

                for (int i = path.size() - 1; i > -1; i--) {
                    int row = path.get(i).getRow();
                    int col = path.get(i).getCol();
                    int zNum = path.get(i).getZ();
                    if (simulationLightningZone[row][col][zNum] == 2) { // 2 is available but not explored? (orange)
                        simulationLightningZone[row][col][zNum] = 3; // 3 is explored (blue)
                    }
                }
                break;
            } else {
                try {
                    calculateNeighborValues();
                } catch (NullPointerException e) {
                    System.out.println(e.getMessage());
                }
                simulationLightningZone[simulationNodeStrikeStart.getRow()][simulationNodeStrikeStart.getCol()][simulationNodeStrikeStart.getZ()] = 4;
                simulationLightningZone[simulationNodeStrikeTarget.getRow()][simulationNodeStrikeTarget.getCol()][simulationNodeStrikeTarget.getZ()] = 5;
                try {
                    assert openList.peek() != null;
                } catch (NullPointerException e) {
                    pathFound = false;
                }
                closedList.add(simulationNodeStrikeCurrent);
            }
        }

        if (openList.size() == 0) {
            pathFound = false;
        }
        return pathFound;
    }

    /**
     * Calculate node movement cost.
     *
     * @param node simulation node.
     * @return movement cost.
     */
    private int calculateG(Node node) {
        int row = node.getRow();
        int col = node.getCol();
        int zNum = node.getZ();
        if (row == simulationNodeStrikeCurrent.getRow() && col == simulationNodeStrikeCurrent.getCol() && zNum == simulationNodeStrikeCurrent.getZ()) {
            return 0;
        }

        Node parent = node.getParent();
        if (parent == null) {
            int xDistance;
            if (col > simulationNodeStrikeCurrent.getCol()) {
                xDistance = col - simulationNodeStrikeCurrent.getCol();
            } else {
                xDistance = simulationNodeStrikeCurrent.getCol() - col;
            }

            int yDistance;
            if (row > simulationNodeStrikeCurrent.getRow()) {
                yDistance = row - simulationNodeStrikeCurrent.getRow();
            } else {
                yDistance = simulationNodeStrikeCurrent.getRow() - row;
            }

            int zDistance;
            if (zNum > simulationNodeStrikeCurrent.getZ()) {
                zDistance = zNum - simulationNodeStrikeCurrent.getZ();
            } else {
                zDistance = simulationNodeStrikeCurrent.getZ() - zNum;
            }

            return (xDistance * 10) + (yDistance * 10) + (zDistance * 10);
        }
        return 10 + parent.getG();
    }

    /**
     * Calculate node heuristic value.
     *
     * @param node simulation node.
     * @return heuristic value.
     */
    private int calculateH(Node node) {
        int row = node.getRow();
        int col = node.getCol();
        int zNum = node.getZ();
        int x = 0;
        int y = 0;
        int z = 0;

        while (col < simulationNodeStrikeTarget.getCol() || col > simulationNodeStrikeTarget.getCol()) {
            x += 10;
            if (col < simulationNodeStrikeTarget.getCol()) {
                col++;
            }
            if (col > simulationNodeStrikeTarget.getCol()) {
                col--;
            }
        }

        while (row < simulationNodeStrikeTarget.getRow() || row > simulationNodeStrikeTarget.getRow()) {
            y += 10;
            if (row < simulationNodeStrikeTarget.getRow()) {
                row++;
            }
            if (row > simulationNodeStrikeTarget.getRow()) {
                row--;
            }
        }

        while (zNum < simulationNodeStrikeTarget.getZ() || zNum > simulationNodeStrikeTarget.getZ()) {
            z += 10;
            if (zNum < simulationNodeStrikeTarget.getZ()) {
                zNum++;
            }
            if (zNum > simulationNodeStrikeTarget.getZ()) {
                zNum--;
            }
        }

        return x + y + z;
    }

    /**
     * Calculate neighboring node values.
     */
    private void calculateNeighborValues() {
        int row = simulationNodeStrikeCurrent.getRow();
        int col = simulationNodeStrikeCurrent.getCol();
        int zNum = simulationNodeStrikeCurrent.getZ();

        if (row - 1 > -1 && simulationNodeLightningZone[row - 1][col][zNum].getType() == 0
                && !closedList.contains(simulationNodeLightningZone[row - 1][col][zNum])) {
            Node[][][] grid = simulationNodeLightningZone;
            grid[row - 1][col][zNum].setParent(simulationNodeStrikeCurrent);
            int g = calculateG(grid[row - 1][col][zNum]);
            grid[row - 1][col][zNum].setG(g);
            int h = calculateH(grid[row - 1][col][zNum]);
            grid[row - 1][col][zNum].setH(h);
            grid[row - 1][col][zNum].setF();
            simulationNodeLightningZone = grid;
            openList.add(grid[row - 1][col][zNum]);
            simulationLightningZone[row - 1][col][zNum] = 2;
        }

        if (col + 1 < simulationNodeLightningZone.length && simulationNodeLightningZone[row][col + 1][zNum].getType() == 0
                && !closedList.contains(simulationNodeLightningZone[row][col + 1][zNum])) {
            Node[][][] grid = simulationNodeLightningZone;
            grid[row][col + 1][zNum].setParent(simulationNodeStrikeCurrent);
            int g = calculateG(grid[row][col + 1][zNum]);
            grid[row][col + 1][zNum].setG(g);
            int h = calculateH(grid[row][col + 1][zNum]);
            grid[row][col + 1][zNum].setH(h);
            grid[row][col + 1][zNum].setF();
            simulationNodeLightningZone = grid;
            openList.add(grid[row][col + 1][zNum]);
            simulationLightningZone[row][col + 1][zNum] = 2;
        }

        if (row + 1 < simulationNodeLightningZone.length && simulationNodeLightningZone[row + 1][col][zNum].getType() == 0
                && !closedList.contains(simulationNodeLightningZone[row + 1][col][zNum])) {
            Node[][][] grid = simulationNodeLightningZone;
            grid[row + 1][col][zNum].setParent(simulationNodeStrikeCurrent);
            int g = calculateG(grid[row + 1][col][zNum]);
            grid[row + 1][col][zNum].setG(g);
            int h = calculateH(grid[row + 1][col][zNum]);
            grid[row + 1][col][zNum].setH(h);
            grid[row + 1][col][zNum].setF();
            simulationNodeLightningZone = grid;
            openList.add(grid[row + 1][col][zNum]);
            simulationLightningZone[row + 1][col][zNum] = 2;
        }

        if (col - 1 > -1 && simulationNodeLightningZone[row][col - 1][zNum].getType() == 0
                && !closedList.contains(simulationNodeLightningZone[row][col - 1][zNum])) {
            Node[][][] grid = simulationNodeLightningZone;
            grid[row][col - 1][zNum].setParent(simulationNodeStrikeCurrent);
            int g = calculateG(grid[row][col - 1][zNum]);
            grid[row][col - 1][zNum].setG(g);
            int h = calculateH(grid[row][col - 1][zNum]);
            grid[row][col - 1][zNum].setH(h);
            grid[row][col - 1][zNum].setF();
            simulationNodeLightningZone = grid;
            openList.add(grid[row][col - 1][zNum]);
            simulationLightningZone[row][col - 1][zNum] = 2;
        }

        if (zNum - 1 > -1 && simulationNodeLightningZone[row][col][zNum - 1].getType() == 0
                && !closedList.contains(simulationNodeLightningZone[row][col][zNum - 1])) {
            Node[][][] grid = simulationNodeLightningZone;
            grid[row][col][zNum - 1].setParent(simulationNodeStrikeCurrent);
            int g = calculateG(grid[row][col][zNum - 1]);
            grid[row][col][zNum - 1].setG(g);
            int h = calculateH(grid[row][col][zNum - 1]);
            grid[row][col][zNum - 1].setH(h);
            grid[row][col][zNum - 1].setF();
            simulationNodeLightningZone = grid;
            openList.add(grid[row][col][zNum - 1]);
            simulationLightningZone[row][col][zNum - 1] = 2;
        }

        if (zNum + 1 < simulationNodeLightningZone.length && simulationNodeLightningZone[row][col][zNum + 1].getType() == 0
                && !closedList.contains(simulationNodeLightningZone[row][col][zNum + 1])) {
            Node[][][] grid = simulationNodeLightningZone;
            grid[row][col][zNum + 1].setParent(simulationNodeStrikeCurrent);
            int g = calculateG(grid[row][col][zNum + 1]);
            grid[row][col][zNum + 1].setG(g);
            int h = calculateH(grid[row][col][zNum + 1]);
            grid[row][col][zNum + 1].setH(h);
            grid[row][col][zNum + 1].setF();
            simulationNodeLightningZone = grid;
            openList.add(grid[row][col][zNum + 1]);
            simulationLightningZone[row][col][zNum + 1] = 2;
        }
    }

    /**
     * Generate final pathfinding path.
     *
     * @return final path.
     */
    private ArrayList<Node> generatePath() {
        ArrayList<Node> path = new ArrayList<>();
        Node temp = simulationNodeStrikeCurrent;
        path.add(temp);
        while (temp.getParent() != null) {
            temp = temp.getParent();
            path.add(temp);
        }
        return path;
    }

    /**
     * Get storm simulation.
     *
     * @return storm simulation.
     */
    public int[][][] getSimulationLightningZone() {
        return simulationLightningZone;
    }
}
