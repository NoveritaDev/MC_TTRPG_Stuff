package me.noverita.plugin.AutoTravel;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import org.bukkit.Location;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class HorseAutoTravel {
    private final MutableValueGraph<Location, Double> graph;
    private final Map<String, Location> namedNodes;
    private static HorseAutoTravel instance;

    public static HorseAutoTravel getInstance() {
        if (instance == null) {
            instance = new HorseAutoTravel();
        }
        return instance;
    }

    private HorseAutoTravel() {
        graph = ValueGraphBuilder.undirected().build();
        namedNodes = new HashMap<>();
    }

    public Set<Location> getNodes() {
        return graph.nodes();
    }

    public void addNode(Location b) {
        graph.addNode(b);
    }

    public void addNamedNode(Location loc, String name) {
        graph.addNode(loc);
        namedNodes.put(name, loc);
    }

    public  void addEdge(Location a, Location b) {
        graph.putEdgeValue(a, b, a.distance(b));
    }

    private double heuristic(Location a, Location b) {
        return a.distance(b);
    }

    // The player will likely not be on an existing node, so we need to bring them on to the graph.
    protected List<Location> playerGetPath(Location playerLocation, String target) {
        Location startNode = null;
        for (Location node: graph.nodes()) {
            if (node.distance(playerLocation) < 3) {
                startNode = node;
                break;
            }
        }
        Location targetNode = namedNodes.get(target);

        if (startNode != null && targetNode != null) {
            return getPath(startNode, targetNode);
        } else {
            return null;
        }
    }

    public Set<EndpointPair<Location>> getEdges() {
        return graph.edges();
    }

    private List<Location> getPath(Location start, Location target) {
        TreeSet<LocationWrapper> queue = new TreeSet<>();
        Map<Location, LocationWrapper> nodeWrappers = new HashMap<>();
        Set<Location> shortestPathFound = new HashSet<>();

        LocationWrapper source = new LocationWrapper(start, null, 0, heuristic(start, target));
        nodeWrappers.put(start, source);
        queue.add(source);

        while (!queue.isEmpty()) {
            LocationWrapper nodeWrapper = queue.pollFirst();
            Location node = nodeWrapper.getNode();
            shortestPathFound.add(node);

            if (node.equals(target)) {
                return buildPath(nodeWrapper);
            }

            Set<Location> neighbors = graph.adjacentNodes(node);
            for (Location neighbor : neighbors) {
                if (shortestPathFound.contains(neighbor)) {
                    continue;
                }

                double cost = graph.edgeValue(node, neighbor).orElseThrow(IllegalStateException::new);
                double totalCostFromStart = nodeWrapper.getTotalCostFromStart() + cost;

                LocationWrapper neighborWrapper = nodeWrappers.get(neighbor);
                if (neighborWrapper == null) {
                    neighborWrapper = new LocationWrapper(neighbor, nodeWrapper, totalCostFromStart, heuristic(neighbor, target));
                    nodeWrappers.put(neighbor, neighborWrapper);
                    queue.add(neighborWrapper);
                } else if (totalCostFromStart < neighborWrapper.getTotalCostFromStart()) {
                    queue.remove(neighborWrapper);

                    neighborWrapper.setTotalCostFromStart(totalCostFromStart);
                    neighborWrapper.setPredecessor(nodeWrapper);

                    queue.add(neighborWrapper);
                }
            }
        }
        return null;
    }

    private static List<Location> buildPath(LocationWrapper nodeWrapper) {
        List<Location> path = new ArrayList<>();
        while (nodeWrapper != null) {
            path.add(nodeWrapper.getNode());
            nodeWrapper = nodeWrapper.getPredecessor();
        }
        Collections.reverse(path);
        return path;
    }

    public static class LocationWrapper implements Comparable<LocationWrapper> {
        private final Location node;
        private LocationWrapper predecessor;
        private double totalCostFromStart;
        private final double minimumRemainingCostToTarget;
        private double costSum;

        public LocationWrapper(Location node, LocationWrapper predecessor, double totalCostFromStart, double minimumRemainingCostToTarget) {
            this.node = node;
            this.predecessor = predecessor;
            this.totalCostFromStart = totalCostFromStart;
            this.minimumRemainingCostToTarget = minimumRemainingCostToTarget;
            calculateCostSum();
        }

        private void calculateCostSum() {
            this.costSum = this.totalCostFromStart + this.minimumRemainingCostToTarget;
        }

        // getter for node
        // getters and setters for predecessor
        public void setTotalCostFromStart(double totalCostFromStart) {
            this.totalCostFromStart = totalCostFromStart;
            calculateCostSum();
        }

        @Override
        public int compareTo(LocationWrapper o) {
            int compare = Double.compare(this.costSum, o.costSum);
            if (compare == 0) {
                compare = node.getBlockX() - o.node.getBlockX();
            }
            return compare;
        }

        private Location getNode() {
            return node;
        }

        private double getTotalCostFromStart() {
            return totalCostFromStart;
        }

        private void setPredecessor(LocationWrapper predecessor) {
            this.predecessor = predecessor;
        }

        private LocationWrapper getPredecessor() {
            return predecessor;
        }
    }
}
