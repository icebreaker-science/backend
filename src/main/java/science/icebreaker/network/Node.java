package science.icebreaker.network;


public class Node {

    private String name;

    private int weight;


    public Node(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }


    public String getName() {
        return name;
    }


    public Node setName(String name) {
        this.name = name;
        return this;
    }


    public int getWeight() {
        return weight;
    }


    public Node setWeight(int weight) {
        this.weight = weight;
        return this;
    }
}
