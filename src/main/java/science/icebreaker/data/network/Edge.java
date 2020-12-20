package science.icebreaker.data.network;

public class Edge {

    private int node1;

    private int node2;

    private int weight;


    public Edge(
            int node1,
            int node2,
            int weight
    ) {
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
    }


    public int getNode1() {
        return node1;
    }


    public Edge setNode1(int node1) {
        this.node1 = node1;
        return this;
    }


    public int getNode2() {
        return node2;
    }


    public Edge setNode2(int node2) {
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
}
