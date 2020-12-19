package science.icebreaker.data.network;

public class CategoryNode extends Node {

    private int rank;

    private int weight;

    public CategoryNode(int id, String name, int rank, int weight) {
        super(id, name);
        this.rank = rank;
        this.weight = weight;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
