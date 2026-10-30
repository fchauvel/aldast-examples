class Graph {

    constructor () {
        this._vertices = {}  // Object are hash tables.
    }

    addVertex (vertexId, payload) {
        if (vertexId in this._vertices)
            throw new Error(`Duplicated vertex ${vertexId}.`);
        this._vertices[vertexId] = new Vertex(vertexId, payload);
    }

    removeVertex (vertexId) {
        vertex = this._vertices.vertexId
        if (vertex !== undefined) {
            if (vertex.hasAnyIncidentEdge())
                throw new Error(`Vertex ${vertexId} still has incident edges!`);
            delete this._vertices.vertexId;
        }
    }

    vertexCount () {
        // JS idiom: The length of a hash table/object is the number
        // of keys registerd in an object.
        return Object.keys(this._vertices).length;
    }

    addEdge (sourceId, targetId, isDirected) {
        const source = this._findVertexById(sourceId);
        const target = this._findVertexById(targetId);
        source.addEdgeTo(targetId);
        target.addEdgeFrom(sourceId);
        if (!isDirected)
            this.addEdge(targetId, sourceId, true);
    }

    _findVertexById (vertexId) {
        const vertex = this._vertices[vertexId];
        if (vertex === undefined)
            throw new Error(`Unknown vertex ${vertexId}.`);
        return vertex
    }

    removeEdge (sourceId, targetId, isDirected) {
        const source = this._findVertexById(sourceId);
        const target = this._finddVertexById(targetId);
        source.removeEdgeTo(targetId);
        target.removeEdgeFrom(sourceId);
    }

    edgesFrom (sourceId) {
        return this._findVertexById(sourceId).outgoingEdges();
    }

    edgesTo (targetId) {
        return this._findVertexById(targetId).incomingEdges();
    }

    edgeCount () {
        let sum = 0;
        for (const [key, vertex] of Object.entries(this._vertices)) {
            sum += vertex.incomingEdges().length;
        }
        return sum;
    }

    depthFirstTraversal (entryVertexId, action) {
        let pendings = [entryVertexId];
        let visited = {};
        while (pendings.length > 0) {
            const vertexId = pendings.pop()
            if (!(vertexId in visited)) {
                const vertex = this._findVertexById(vertexId);
                visited[vertexId] = vertex;
                action(vertex);
                for (const edge of vertex.outgoingEdges()) {
                    pendings.push(edge.targetId);
                }
            }
        }
    }

}

class Vertex {

    constructor(vertexId, payload) {
        this.vertexId = vertexId
        this.payload = payload
        this._incidentEdges = [] // Empty array/list
    }

    addEdgeTo (targetId) {
        const edge = new Edge(this.vertexId, targetId);
        this._incidentEdges.push(edge);
    }

    addEdgeFrom (sourceId) {
        const edge = new Edge(sourceId, this.vertexId);
        this._incidentEdges.push(edge);
    }

    removeEdgesFrom (sourceId) {
        this._incidentEdges =
            this._incidentEdges.filter(edge => edge.sourceId !== sourceId);
    }

    removeEdgeTo (targetId) {
        this._incidentEdges =
            this._incidentEdges.filter(edge => edge.targetId !== targetId);
    }

    outgoingEdges () {
        return this._incidentEdges.filter(edge => edge.sourceId === this.vertexId);
    }

    incomingEdges () {
        return this._incidentEdges.filter(edge => edge.targetId === this.vertexId);
    }

    hasAnyIncidentEdge() {
        return this.incidentEdgeCount() > 0;
    }

    incidentEdgeCount () {
        return this._incidentEdges.length;
    }

}

class Edge {

    constructor (sourceId, targetId, weight=0) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.weight = weight;
    }

}

const people = [
    { key: "E", name: "Erik" },
    { key: "D", name: "Denis" },
    { key: "F", name: "Frank" },
    { key: "J", name: "John" },
    { key: "L", name: "Lisa" },
    { key: "M", name: "Mary" },
    { key: "O", name: "Olive" },
    { key: "P", name: "Peter" },
    { key: "T", name: "Thomas" }
];

const graph = new Graph();

for (const eachPerson of people) {
    graph.addVertex(eachPerson.key, eachPerson.name);
}

graph.addEdge("F", "D", false);
graph.addEdge("F", "T", false);
graph.addEdge("F", "L", false);
graph.addEdge("L", "J", false);
graph.addEdge("J", "M", false);
graph.addEdge("M", "P", false);
graph.addEdge("M", "O", false);
graph.addEdge("O", "E", false);
graph.addEdge("O", "D", false);

console.log(`My graph has ${graph.vertexCount()} vertices.`);
console.log(`My graph has ${graph.edgeCount()} edge(s).`);


graph.depthFirstTraversal("F", (vertex) => { console.log(vertex.payload) });
