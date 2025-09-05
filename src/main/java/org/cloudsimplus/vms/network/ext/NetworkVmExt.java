/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.vms.network.ext;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cloudsimplus.brokers.DatacenterBroker;
import org.cloudsimplus.cloudlets.network.NetworkCloudlet;
import org.cloudsimplus.cloudlets.network.ext.NetworkCloudletExt;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.hosts.network.ext.NetworkHostExt;
import org.cloudsimplus.network.VmPacket;
import org.cloudsimplus.network.ext.VmPacketExt;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;


@Accessors @Getter @Setter
public class NetworkVmExt extends VmSimple {
    public static final NetworkVmExt NULL = new NetworkVmExt();


    @NonNull
    private List<NetworkCloudletExt> cloudletList;

    /**
     * List of packets received by the VM.
     */
    @NonNull
    private List<VmPacketExt> receivedPacketList;

    /** Indicates if the VM is free or not. */
    private boolean free;

    /// Creates a NetworkVm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth
    /// and 1024 MEGA of Storage Size.
    ///
    /// To change these values, use the respective setters.
    /// While the Vm [is not created inside a Host][#isCreated()], such values can be changed freely.
    ///
    /// @param id unique ID of the VM
    /// @param mipsCapacity the mips capacity of each Vm [Pe]
    /// @param pesNumber amount of [Pe] (CPU cores)
    public NetworkVmExt(final int id, final long mipsCapacity, final int pesNumber) {
        super(id, mipsCapacity, pesNumber);
        cloudletList = new ArrayList<>();
    }

    /**
     * Creates a VM with no resources.
     */
    private NetworkVmExt(){
        this(-1, 0, 1);
    }

    /// Creates a NetworkVm with 1024 MEGA of RAM, 1000 Megabits/s of Bandwidth and 1024 MEGA of Storage Size.
    /// To change these values, use the respective setters.
    /// While the Vm [is not created inside a Host][#isCreated()], such values can be changed freely.
    ///
    /// It is not defined an `id` for the Vm. The `id` is defined when the Vm is submitted to
    /// a [DatacenterBroker].
    ///
    /// @param mipsCapacity the mips capacity of each Vm [Pe]
    /// @param pesNumber amount of [Pe] (CPU cores)
    public NetworkVmExt(final long mipsCapacity, final int pesNumber) {
        super(mipsCapacity, pesNumber);
        cloudletList = new ArrayList<>();
    }

    @Override
    public NetworkHostExt getHost() {
        if (super.getHost().equals(Host.NULL)){
            return null;
        }
        else{
            return (NetworkHostExt)super.getHost();}
    }

    @Override
    public Vm setHost(final Host host) {
        if(host == Host.NULL)
            return super.setHost(NetworkHostExt.NULL);

        if(host instanceof NetworkHostExt)
            return super.setHost(host);

        throw new IllegalArgumentException("NetworkVmExt can only be run into a NetworkHostExt");
    }
}
