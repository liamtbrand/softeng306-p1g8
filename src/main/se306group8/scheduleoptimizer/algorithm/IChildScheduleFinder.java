package se306group8.scheduleoptimizer.algorithm;

import java.util.Collection;

@FunctionalInterface
public interface IChildScheduleFinder {
	Collection<TreeSchedule> getChildSchedules(TreeSchedule schedule);
}
