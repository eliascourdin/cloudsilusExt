/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.cloudlets.network.ext;

import lombok.Getter;
import lombok.Setter;
import org.cloudsimplus.resources.Pe;


public class CloudletExecutionTaskExt extends CloudletTaskExt {

    /** the execution length of the task (in MI). */
    @Getter @Setter
    private long length;

    /** The length of this CloudletTask that has been executed so far (in MI). */
    @Getter
    private long totalExecutedLength;

    /**
     * Creates a new task.
     * @param id id to assign to the task
     * @param executionLength the execution length of the task (in MI)
     */
    public CloudletExecutionTaskExt(final int id, final long executionLength) {
        super(id);
        this.length = executionLength;
    }

    /**
     * Sets a given number of MI to the {@link #getTotalExecutedLength() total MI executed so far} by the cloudlet.
     *
     * @param partialFinishedMI the partial executed length of this Cloudlet (in MI)
     * @return true if the MI value is valid (greater than zero), false otherwise
     */
    public boolean process(final long partialFinishedMI) {
        if(partialFinishedMI <= 0) {
            return false;
        }

        final long maxLengthToAdd = Math.min(partialFinishedMI, length - totalExecutedLength);
        this.totalExecutedLength += maxLengthToAdd;
        setFinished(this.totalExecutedLength == length);
        return true;
    }
}
