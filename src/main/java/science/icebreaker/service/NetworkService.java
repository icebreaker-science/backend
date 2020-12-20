package science.icebreaker.service;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import science.icebreaker.dao.entity.Paper;
import science.icebreaker.dao.repository.PaperRepository;
import science.icebreaker.data.network.CategoryNode;
import science.icebreaker.data.network.Edge;
import science.icebreaker.data.network.KeywordEdge;
import science.icebreaker.data.network.KeywordNode;

import java.util.List;
import java.util.stream.Collectors;

import static org.neo4j.driver.Values.parameters;


@Service
public class NetworkService implements AutoCloseable {

    /**
     * TODO Find a better solution to limit the size of the results.
     *
     * The number of the returned edges per request is limited since the browser
     * is not able to process to large graphs.
     * It is not defined which edges will be ignored. For example, it might happen
     * that the ego graph endpoint returns
     * a graph where not all nodes are connected to the ego node.
     */
    private static final int MAX_RETURNED_EDGES = 10000;

    private final Driver driver;

    private final PaperRepository paperRepository;


    public NetworkService(
            @Value("${icebreaker.neo4j.host}") String host,
            @Value("${icebreaker.neo4j.port}") String port,
            @Value("${icebreaker.neo4j.username}") String username,
            @Value("${icebreaker.neo4j.password}") String password,
            PaperRepository paperRepository
    ) {
        this.paperRepository = paperRepository;
        driver = GraphDatabase.driver(
            "bolt://" + host + ":" + port, AuthTokens.basic(username, password));
    }


    public int countNodes() {
        try (Session session = driver.session()) {
            Result result = session.run("match (n: Topic) return count(n)");
            return result.single().get(0).asInt();
        }
    }


    public List<KeywordNode> getAllKeywordNodes() {
        try (Session session = driver.session()) {
            Result result = session.run("MATCH (n: Topic) - [e] - (m :Category) "
                    + "RETURN ID(n) AS id, n.name AS name, n.weight AS weight, collect(m.name) AS categories "
                    + "UNION "
                    + "MATCH (n: Topic)  "
                    + "WHERE NOT (n) - [:IS_CATEGORY] -() "
                    + "RETURN ID(n) AS id, n.name AS name, n.weight AS weight, [\"others\"] AS categories;");
            return result.stream()
                    .map((record) ->
                    new KeywordNode(record.get("id").asInt(),
                            record.get("name").asString(),
                            record.get("weight").asInt(),
                            record.get("categories").asList(Values.ofString()).toArray(new String[0])))
                    .collect(Collectors.toList());
        }
    }

    public List<CategoryNode> getAllCategoryNodes() {
        try (Session session = driver.session()) {
            Result result = session.run("MATCH (n: Category) - [:IS_CATEGORY] - (m:Topic)"
                    + "RETURN ID(n) AS id, n.name AS name, n.rank AS rank, count(m) AS weight");
            return result.stream()
                    .map((record) ->
                            new CategoryNode(record.get("id").asInt(),
                                    record.get("name").asString(),
                                    record.get("rank").asInt(),
                                    record.get("weight").asInt()))
                    .collect(Collectors.toList());
        }
    }


    public List<Edge> getEgoGraph(String nodeName) {
        try (Session session = driver.session()) {
            Result result = session.run("match (n:Topic)-[e]-(m: Topic) "
                            + "where n.name = $name "
                            + "with collect(m.name) as ms, n "
                            + "match (m1: Topic)-[e]-(m2: Topic) "
                            + "where (m1.name in ms or m1.name = n.name) "
                            + "and (m2.name in ms or m2.name = n.name) "
                            + "and m1.name < m2.name "
                            + "return ID(m1) AS node1, ID(m2) as node2, e.weight, e.normalizedWeight, e.references "
                            + "limit " + MAX_RETURNED_EDGES,
                    parameters("name", nodeName));
            return result.stream()
                    .map((record) -> new KeywordEdge(
                            record.get("node1").asInt(),
                            record.get("node2").asInt(),
                            record.get("e.weight").asInt(),
                            record.get("e.normalizedWeight").asDouble(),
                            record.get("e.references").asString()
                    ))
                    .collect(Collectors.toList());
        }
    }


    public List<Edge> getShortestPathGraph(List<String> nodes) {
        try (Session session = driver.session()) {
            Result result = session.run("match (n:Topic), (m:Topic), "
                            + "p = allShortestPaths((n)-[*..]-(m)) "
                            + "where n.name in $nodes and m.name in $nodes and n.name < m.name "
                            + "unwind nodes(p) as tmp "
                            + "with collect(distinct(tmp)) as nodes_on_path "
                            + "match (n:Topic)-[e]-(m:Topic) "
                            + "where n in nodes_on_path and m in nodes_on_path "
                            + "and n.name < m.name "
                            + "return ID(n) AS node1, ID(m) as node2, e.weight, e.normalizedWeight, e.references "
                            + "limit " + MAX_RETURNED_EDGES,
                    parameters("nodes", nodes));
            return result.stream()
                    .map((record) -> new KeywordEdge(
                            record.get("node1").asInt(),
                            record.get("node2").asInt(),
                            record.get("e.weight").asInt(),
                            record.get("e.normalizedWeight").asDouble(),
                            record.get("e.references").asString()
                    ))
                    .collect(Collectors.toList());
        }
    }

    public List<Edge> getCategoryGraph() {
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (n:Category) - [:IS_CATEGORY] - (t:Topic) - [:IS_CATEGORY] - (m:Category) "
                            + "WHERE ID(n) < ID(m) "
                            + "RETURN ID(n) AS node1, ID(m) AS node2, count(t) AS weight");
            return result.stream()
                    .map((record) -> new Edge(
                            record.get("node1").asInt(),
                            record.get("node2").asInt(),
                            record.get("weight").asInt()
                    ))
                    .collect(Collectors.toList());
        }
    }

    public List<Edge> getCategoryGraph(String category) {
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (c:Category) - [:IS_CATEGORY] - (n:Topic) - [r:RELATED_TO] - (m:Topic) "
                            + "- [:IS_CATEGORY] - (c:Category) "
                            + "WHERE ID(n) < ID(m) AND c.name = $category "
                            + "RETURN ID(n) AS node1, ID(m) AS node2, r.weight AS weight",
                    parameters("category", category));
            return result.stream()
                    .map((record) -> new Edge(
                            record.get("node1").asInt(),
                            record.get("node2").asInt(),
                            record.get("weight").asInt()
                    ))
                    .collect(Collectors.toList());
        }
    }


    public List<Paper> getPapers(List<Integer> ids) {
        return paperRepository.findAllByIds(ids);
    }


    @Override
    public void close() throws Exception {
        driver.close();
    }
}
