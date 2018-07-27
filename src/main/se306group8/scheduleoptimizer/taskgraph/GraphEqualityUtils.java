package se306group8.scheduleoptimizer.taskgraph;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

class GraphEqualityUtils {
	private static class IgnoreChildContainer<T extends GraphEquality<?>> {
		T object;

		IgnoreChildContainer(T object) {
			this.object = object;
		}

		@Override
		public boolean equals(Object other) {
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

	private static class IgnoreParentContainer<T extends GraphEquality<?>> {
		T object;

		IgnoreParentContainer(T object) {
			this.object = object;
		}

		@Override
		public boolean equals(Object other) {
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

	static <T extends GraphEquality<?>> boolean setsEqualIgnoringParents(Collection<? extends T> aSet, Collection<? extends T> bSet) {
		Set<IgnoreParentContainer<T>> aTransformed = aSet.stream().map((T a) -> new IgnoreParentContainer<>(a)).collect(Collectors.toSet());
		Set<IgnoreParentContainer<T>> bTransformed = bSet.stream().map((T a) -> new IgnoreParentContainer<>(a)).collect(Collectors.toSet());
		
		return aTransformed.equals(bTransformed);
	}
	
	static <T extends GraphEquality<?>> boolean setsEqualIgnoringChildren(Collection<? extends T> aSet, Collection<? extends T> bSet) {
		Set<IgnoreChildContainer<T>> aTransformed = aSet.stream().map((T a) -> new IgnoreChildContainer<>(a)).collect(Collectors.toSet());
		Set<IgnoreChildContainer<T>> bTransformed = bSet.stream().map((T a) -> new IgnoreChildContainer<>(a)).collect(Collectors.toSet());
		
		return aTransformed.equals(bTransformed);
	}
}
