package se306group8.scheduleoptimizer.taskgraph;

/** This interface defines a contract to be used to define if two elements in a graph are equal ignoring connections in one direction. */
interface GraphEquality<T extends GraphEquality<?>> {
	boolean equalsIgnoringParents(T other);
	boolean equalsIgnoringChildren(T other);
}
