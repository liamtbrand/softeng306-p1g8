package se306group8.scheduleoptimizer.taskgraph;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/** This is a static utility class used for testing the equality for elements, possibly ignoring parents or children. */
class GraphEqualityUtils {
	/** Wraps an object, redefining equality to ignore children */
	private static class IgnoreChildContainer<T extends GraphEquality<?>> {
		T object;

		IgnoreChildContainer(T object) {
			this.object = object;
		}

		@Override
		public boolean equals(Object other) {
			if(other == this)
				return true;
			
			if(!(other instanceof IgnoreChildContainer)) {
				return false;
			}

			@SuppressWarnings("unchecked")
			IgnoreChildContainer<? extends GraphEquality<T>> otherContainer = (IgnoreChildContainer<? extends GraphEquality<T>>) other;

			return otherContainer.object.equalsIgnoringChildren(object);
		}

		@Override
		public int hashCode() {
			return object.hashCode();
		}
		
		@Override
		public String toString() {
			return object.toString();
		}
	}

	/** Wraps an object, redefining equality to ignore parents */
	private static class IgnoreParentContainer<T extends GraphEquality<?>> {
		T object;

		IgnoreParentContainer(T object) {
			this.object = object;
		}

		@Override
		public boolean equals(Object other) {
			if(other == this)
				return true;
			
			if(!(other instanceof IgnoreParentContainer)) {
				return false;
			}

			@SuppressWarnings("unchecked")
			IgnoreParentContainer<? extends GraphEquality<T>> otherContainer = (IgnoreParentContainer<? extends GraphEquality<T>>) other;

			return otherContainer.object.equalsIgnoringParents(object);
		}

		@Override
		public int hashCode() {
			return object.hashCode();
		}
		
		@Override
		public String toString() {
			return object.toString();
		}
	}

	/** Checks if two sets are equal ignoring the parents of the elements. */
	static <T extends GraphEquality<?>> boolean setsEqualIgnoringParents(Collection<? extends T> aSet, Collection<? extends T> bSet) {
		if(aSet == bSet)
			return true;
		
		Set<IgnoreParentContainer<T>> aTransformed = aSet.stream().map((T a) -> new IgnoreParentContainer<>(a)).collect(Collectors.toSet());
		Set<IgnoreParentContainer<T>> bTransformed = bSet.stream().map((T a) -> new IgnoreParentContainer<>(a)).collect(Collectors.toSet());
		
		return aTransformed.equals(bTransformed);
	}
	
	/** Checks if two sets are equal ignoring the children of the elements. */
	static <T extends GraphEquality<?>> boolean setsEqualIgnoringChildren(Collection<? extends T> aSet, Collection<? extends T> bSet) {
		if(aSet == bSet)
			return true;
		
		Set<IgnoreChildContainer<T>> aTransformed = aSet.stream().map((T a) -> new IgnoreChildContainer<>(a)).collect(Collectors.toSet());
		Set<IgnoreChildContainer<T>> bTransformed = bSet.stream().map((T a) -> new IgnoreChildContainer<>(a)).collect(Collectors.toSet());
		
		return aTransformed.equals(bTransformed);
	}
}
