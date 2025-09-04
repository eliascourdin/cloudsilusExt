/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */
package org.cloudsimplus.datacenters.network.ext;

import org.cloudsimplus.allocationpolicies.VmAllocationPolicy;
import org.cloudsimplus.allocationpolicies.VmAllocationPolicySimple;
import org.cloudsimplus.core.CloudSimPlus;
import org.cloudsimplus.core.Simulation;
import org.cloudsimplus.datacenters.Datacenter;
import org.cloudsimplus.datacenters.DatacenterSimple;
import org.cloudsimplus.hosts.Host;
import org.cloudsimplus.hosts.network.NetworkHost;
import org.cloudsimplus.hosts.network.ext.NetworkHostExt;
import org.cloudsimplus.network.switches.EdgeSwitch;
import org.cloudsimplus.network.switches.Switch;
import org.cloudsimplus.network.switches.ext.SwitchExt;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.vms.Vm;
import org.cloudsimplus.vms.network.NetworkVm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;


public class NetworkDatacenterExt extends DatacenterSimple {


    private final List<SwitchExt> switchMap;


    public NetworkDatacenterExt(
        final Simulation simulation,
        final List<? extends NetworkHostExt> hostList,
        final VmAllocationPolicy vmAllocationPolicy)
    {
        this(simulation, hostList);
        setVmAllocationPolicy(vmAllocationPolicy);
    }


    public NetworkDatacenterExt(
        final Simulation simulation,
        final List<? extends NetworkHostExt> hostList)
    {
        super(simulation, hostList);
        switchMap = new ArrayList<>();
    }


    public List<SwitchExt> getEdgeSwitch() {
        return switchMap.stream()
            .filter(swt -> swt.getLevel() == EdgeSwitch.LEVEL)
            .collect(toList());
    }


    public void addSwitch(final SwitchExt swt){
        switchMap.add(swt);
    }


    // TODO This method and attribute must be renamed to switchList
    public List<SwitchExt> getSwitchMap() {
        return Collections.unmodifiableList(switchMap);
    }

    @Override
    public List<NetworkHostExt> getHostList() {
        return super.getHostList();
    }

}
