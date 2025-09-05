/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets.network.ext;

import lombok.NonNull;
import lombok.experimental.Accessors;
import org.cloudsimplus.cloudlets.CloudletSimple;
import org.cloudsimplus.cloudlets.network.CloudletExecutionTask;
import org.cloudsimplus.cloudlets.network.CloudletTask;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.network.NetworkVm;
import org.cloudsimplus.vms.network.ext.NetworkVmExt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Accessors
public class NetworkCloudletExt extends CloudletSimple {

    /**
     * The index of the active running task or -1 if no task has started yet.
     */
    private int currentTaskNum;

    /** @see #getTasks() */
    private final List<CloudletTaskExt> tasks;

    /**
     * Creates a NetworkCloudlet with no priority, no file size and output size equal to 1.
     * The length of the Cloudlet is determined by the sum of its {@link #getTasks() tasks} length.
     *
     * @param pesNumber the number of {@link Pe}s this Cloudlet requires
     */
    public NetworkCloudletExt(final int pesNumber) {
        this(-1, pesNumber);
    }

    /**
     * Creates a NetworkCloudlet with no priority, no file size and output size equal to 1.
     * The length of the Cloudlet is determined by the sum of its {@link #getTasks() tasks} length.
     *
     * @param id        the unique ID of this cloudlet
     * @param pesNumber the number of {@link Pe}s this Cloudlet requires
     */
    public NetworkCloudletExt(final int id, final int pesNumber) {
        super(id, -1, pesNumber);
        this.currentTaskNum = -1;
        this.tasks = new ArrayList<>();
    }

    public double getNumberOfTasks() {
        return tasks.size();
    }

    /**
     * @return a read-only list of Cloudlet's tasks.
     */
    public List<CloudletTaskExt> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Checks if some Cloudlet Task has started yet.
     *
     * @return true if some task has started, false otherwise
     */
    public boolean isTasksStarted() {
        return currentTaskNum > -1;
    }

    /**
     * Change the current task to the next one to start executing it,
     * if the current task is finished.
     *
     * @param nextTaskStartTime the time that the next task will start
     * @return true if the current task is finished and the next one has started;
     * false if current task is not finished or there aren't any more tasks to be executed.
     */
    public boolean startNextTaskIfCurrentIsFinished(final double nextTaskStartTime){
        return
            getNextTaskIfCurrentIfFinished()
                .map(task -> startTask(task, nextTaskStartTime))
                .isPresent();
    }

    private static CloudletTaskExt startTask(final CloudletTaskExt task, double time) {
        task.setStartTime(time);
        return task;
    }

    /**
     * @return an {@link Optional} containing the current task
     * or {@link Optional#empty()} if there is no current task yet.
     */
    public Optional<CloudletTaskExt> getCurrentTask() {
        if (currentTaskNum < 0 || currentTaskNum >= tasks.size()) {
            return Optional.empty();
        }

        return Optional.of(tasks.get(currentTaskNum));
    }

    /**
     * Gets an {@link Optional} containing the next task in the list if the current task is finished.
     *
     * @return the next task if the current one is finished;
     *         otherwise {@link Optional#empty()} if the current task is already the last one,
     *         or it is not finished yet.
     */
    private Optional<CloudletTaskExt> getNextTaskIfCurrentIfFinished(){
        if(getCurrentTask().filter(CloudletTaskExt::isActive).isPresent()) {
            return Optional.empty();
        }

        if(this.currentTaskNum <= tasks.size()-1) {
            this.currentTaskNum++;
        }

        return getCurrentTask();
    }

    @Override
    public boolean isFinished() {
        final boolean allTasksFinished = tasks == null || tasks.stream().allMatch(CloudletTaskExt::isFinished);
        return super.isFinished() && allTasksFinished;
    }


    @Override
    public long getLength() {
        return getTasks().stream()
            .filter(CloudletTaskExt::isExecutionTask)
            .map(task -> (CloudletExecutionTaskExt)task)
            .mapToLong(CloudletExecutionTaskExt::getLength)
            .sum();
    }

    /**
     * Adds a task to the {@link #getTasks() task list}
     * and links the task to this NetworkCloudlet.
     *
     * @param task Task to be added
     * @return this NetworkCloudlet instance
     */
    public NetworkCloudletExt addTask(@NonNull final CloudletTaskExt task) {
        task.setCloudlet(this);
        tasks.add(task);
        return this;
    }

    @Override
    public NetworkVmExt getVm() {
        return (NetworkVmExt)super.getVm();
    }

    @Override
    public NetworkCloudletExt setVm(final Vm vm) {
        if(vm == Vm.NULL) {
            setVm(NetworkVmExt.NULL);
            return this;
        }

        if(vm instanceof NetworkVmExt) {
            super.setVm(vm);
            return this;
        }

        throw new IllegalArgumentException("NetworkCloudlet can just be executed by a NetworkVm");
    }
}
