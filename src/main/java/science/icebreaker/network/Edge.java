package science.icebreaker.network;

public class Edge {

    private String node1;

    private String node2;

    private int weight;

    private double normalizedWeight;

    private String references;


    public Edge(String node1, String node2, int weight, double normalizedWeight, String references) {
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
        this.normalizedWeight = normalizedWeight;
        this.references = references;
    }


    public String getNode1() {
        return node1;
    }


    public Edge setNode1(String node1) {
        this.node1 = node1;
        return this;
    }


    public String getNode2() {
        return node2;
    }


    public Edge setNode2(String node2) {
        this.node2 = node2;
        return this;
    }


    public int getWeight() {
        return weight;
    }


    public Edge setWeight(int weight) {
        this.weight = weight;
        return this;
    }


    public double getNormalizedWeight() {
        return normalizedWeight;
    }


    public Edge setNormalizedWeight(double normalizedWeight) {
        this.normalizedWeight = normalizedWeight;
        return this;
    }


    public String getReferences() {
        return references;
    }


    public Edge setReferences(String references) {
        this.references = references;
        return this;
    }
}
