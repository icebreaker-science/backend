package science.icebreaker.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import science.icebreaker.data.network.Edge;
import science.icebreaker.data.network.Node;
import science.icebreaker.dao.entity.Paper;
import science.icebreaker.service.NetworkService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/network")
public class NetworkController {

    private final NetworkService service;


    public NetworkController(NetworkService service) {
        this.service = service;
    }


    @GetMapping("")
    @ApiOperation(
        "Returns the number of nodes - just for fun."
        + "This endpoint can change any time and should not be actually used.")
    public int getNumberOfNodes() {
        return service.countNodes();
    }


    @GetMapping("/node")
    @ApiOperation("Returns all nodes")
    public List<Node> getNodes() {
        return service.getAllNodes();
    }


    @GetMapping("/graph/ego")
    @ApiOperation("Returns the edges of the ego graph of the selected node")
    public List<Edge> getEgoGraph(
            @ApiParam(value = "Name of a node", example = "raman spectroscopy")
            @RequestParam("node") String node
    ) {
        return service.getEgoGraph(node);
    }


    @GetMapping("/graph/shortest_path")
    @ApiOperation("Returns the edges of a graph containing the selected nodes and the shortest paths between all pairs")
    public List<Edge> getShortestPathGraph(
            @ApiParam(value = "A comma-separated list of node names",
                    example = "raman spectroscopy, microplastics, ocean")
            @RequestParam("nodes") String nodes
    ) {
        return service.getShortestPathGraph(Arrays.asList(nodes.split(",")));
    }


    @GetMapping("/paper")
    @ApiOperation("Returns information about papers")
    public List<Paper> getPapers(
            @ApiParam(value = "A comma-separated list of ids")
            @RequestParam("ids") String ids
    ) {
        List<Integer> idList = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        return service.getPapers(idList);
    }

}
