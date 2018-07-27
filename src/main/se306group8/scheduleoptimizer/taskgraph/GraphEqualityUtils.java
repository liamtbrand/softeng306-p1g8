package se306group8.scheduleoptimizer.taskgraph;

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
			return object.hashCodeIgnoringChildren();
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
			return object.hashCodeIgnoringParents();
		}
	}

	static <T extends GraphEquality<?>> boolean setsEqualIgnoringParents(Set<? extends T> aSet, Set<? extends T> bSet) {
		Set<IgnoreParentContainer<T>> aTransformed = aSet.stream().map((T a) -> new IgnoreParentContainer<>(a)).collect(Collectors.toSet());
		Set<IgnoreParentContainer<T>> bTransformed = bSet.stream().map((T a) -> new IgnoreParentContainer<>(a)).collect(Collectors.toSet());
		
		return aTransformed.equals(bTransformed);
	}
	
	static <T extends GraphEquality<?>> boolean setsEqualIgnoringChildren(Set<? extends T> aSet, Set<? extends T> bSet) {
		Set<IgnoreChildContainer<T>> aTransformed = aSet.stream().map((T a) -> new IgnoreChildContainer<>(a)).collect(Collectors.toSet());
		Set<IgnoreChildContainer<T>> bTransformed = bSet.stream().map((T a) -> new IgnoreChildContainer<>(a)).collect(Collectors.toSet());
		
		return aTransformed.equals(bTransformed);
	}
	
	static <T extends GraphEquality<?>> int setHashCodeIgnoringParents(Set<? extends T> aSet) {
		return aSet.stream().mapToInt((T a) -> new IgnoreParentContainer<>(a).hashCode()).sum();
	}
	
	static <T extends GraphEquality<?>> int setsHashCodeIgnoringChildren(Set<? extends T> aSet) {
		return aSet.stream().mapToInt((T a) -> new IgnoreChildContainer<>(a).hashCode()).sum();
	}
}
