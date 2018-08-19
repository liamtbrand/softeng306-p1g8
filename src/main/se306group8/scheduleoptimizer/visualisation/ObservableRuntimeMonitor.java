package se306group8.scheduleoptimizer.visualisation;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.chart.XYChart.Data;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitor;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;


/**
 * An observable implementation of the RuntimeMonitor interface, 
 * It is used to pass information to the visualisation.
 */
public class ObservableRuntimeMonitor implements RuntimeMonitor, Observable {

	
	private volatile boolean finished;
	private volatile TreeSchedule bestSchedule;
	private volatile Queue<String> messages;

	private volatile long schedulesExplored;
	private volatile long schedulesInArray;
	private volatile int scheduleInArrayStorageSize;
	private volatile long schedulesInQueue;
	private volatile int scheduleInQueueStorageSize;
	private volatile long schedulesOnDisk;
	private volatile int scheduleOnDiskStorageSize;
	
	private volatile long startTime;
	private volatile long finishTime;
	private volatile int upperBound = Integer.MAX_VALUE;
	
	private volatile Schedule finishedSolution;
	
	//Set in the start method
	private volatile boolean started;
	private volatile String algorithmName;
	private volatile int numberOfProcessors;
	private volatile int coresToUseForExecution;
	
	//Used for the histogram
	private volatile int[] histogramData = new int[0];
	private volatile int slots = 0;
	private volatile int granularity = 0;
	
	private volatile String graphName = "";
	
	private final List<InvalidationListener> listeners;
	private volatile boolean interupted = false;
	private volatile int lowerBound = 0;
	
	public ObservableRuntimeMonitor() {

		started = false;
		finished = false;
		bestSchedule = null;
		messages = new LinkedBlockingQueue<>();

		schedulesExplored = 0;
		schedulesInArray = 0;
		scheduleInArrayStorageSize = 0;
		schedulesInQueue = 0;
		scheduleInQueueStorageSize = 0;
		schedulesOnDisk = 0;
		scheduleOnDiskStorageSize = 0;

		algorithmName = "Unnamed";
		coresToUseForExecution = 1;
		
		numberOfProcessors = 0;

		listeners = new ArrayList<>();
	}

	private void invalidateListeners() {
		Platform.runLater(() -> {
			for (InvalidationListener listener : listeners) {
				listener.invalidated(this);
			}
		});
	}

	@Override
	public void updateBestSchedule(TreeSchedule optimalSchedule) {
		bestSchedule = optimalSchedule;
	}

	@Override
	public void start(String name, String graphName, int numberOfProcessors, int coresToUseForExecution) {
		started = true;
		algorithmName = name;
		this.graphName = graphName;
		this.numberOfProcessors = numberOfProcessors;
		this.coresToUseForExecution = coresToUseForExecution;
		this.startTime = System.currentTimeMillis();
		
		invalidateListeners();
	}

	@Override
	public void finish(Schedule solution) {
		finished = true;
		finishedSolution = solution;
		finishTime = System.currentTimeMillis();
		invalidateListeners();
	}

	/** Returns the time taken in ms */
	public long timeTaken() {
		return finishTime - startTime;
	}
	
	@Override
	public void logMessage(String message) {
		LocalDateTime now = LocalDateTime.now();
		messages.add("["+new SimpleDateFormat("HH:mm:ss").format(new Date())+"]: "+message);
		invalidateListeners();
	}

	@Override
	public void setSchedulesExplored(long number) {
		schedulesExplored = number;
	}

	public long getSchedulesExplored() {
		return schedulesExplored;
	}

	@Override
	public void setSchedulesInArray(long number) {
		schedulesInArray = number;
	}

	public long getSchedulesInArray() {
		return schedulesInArray;
	}

	@Override
	public void setScheduleInArrayStorageSize(int bytes) {
		scheduleInArrayStorageSize = bytes;
	}

	public int getScheduleInArrayStorageSize() {
		return scheduleInArrayStorageSize;
	}

	@Override
	public void setSchedulesInQueue(long number) {
		schedulesInQueue = number;
	}

	public long getSchedulesInQueue() {
		return schedulesInQueue;
	}

	@Override
	public void setScheduleInQueueStorageSize(int bytes) {
		scheduleInQueueStorageSize = bytes;
	}

	public int getScheduleInQueueStorageSize() {
		return scheduleInQueueStorageSize;
	}

	@Override
	public void setSchedulesOnDisk(long number) {
		schedulesOnDisk = number;
	}

	public long getSchedulesOnDisk() {
		return schedulesOnDisk;
	}
	
	@Override
	public void setScheduleOnDiskStorageSize(int bytes) {
		scheduleOnDiskStorageSize = bytes;
	}

	public int getScheduleOnDiskStorageSize() {
		return scheduleOnDiskStorageSize;
	}

	public boolean hasStarted() {
		return started;
	}

	public boolean hasFinished() {
		return finished;
	}

	public String nextMessage() {
		return messages.poll();
	}

	public TreeSchedule getBestSchedule() {
		return bestSchedule;
	}
	
	public int getNumberOfProcessors() {
		return numberOfProcessors;
	}
	
	public void setProcessorsToScheduleOn(int processors) {
		numberOfProcessors = processors;
	}

	public int getCoresToUseForExecution() {
		return coresToUseForExecution;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		listeners.add(listener);
	}

	@Override
	public void setScheduleDistribution(int[] histogramData, int limit) {
		this.histogramData = histogramData;
		slots = limit;
	}
	
	@Override
	public void removeListener(InvalidationListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void interuptAlgorithm() {
		interupted  = true;
	}
	
	@Override
	public boolean isInterupted() {
		return interupted;
	}
	
	public Collection<Data<String, Number>> getHistogramData() {
		Collection<Data<String, Number>> col = new ArrayList<>();
		
		boolean added = false;
		for(int i = 0; i < slots; i++) {
			if(added || histogramData[i] != 0) {
				added = true;
				col.add(new Data<>(getName(i), histogramData[i]));
			}
		}
		
		return col;
	}

	@Override
	public void setBucketSize(int granularity) {
		this.granularity = granularity;
	}
	
	@Override
	public void setUpperBound(int bound) {
		upperBound = bound;
	}
	
	private String getName(int i) {

		if(granularity == 1) {
			return Integer.toString(i);
		} else {
			return Integer.toString(i * granularity) + " - " + Integer.toString((i + 1) * granularity - 1);
		}

	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public Schedule getFinishedSolution() {
		return finishedSolution;
	}

	public int getUpperBound() {
		return upperBound;
	}

	public String getGraphName() {
		return graphName;
	}

	public int getLowerBound() {
		return lowerBound;
	}
	
	@Override
	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}
}
