package se306group8.scheduleoptimizer.algorithm;

import java.util.List;

@FunctionalInterface
public interface ChildScheduleFinder {
	List<TreeSchedule> getChildSchedules(TreeSchedule schedule);
}
