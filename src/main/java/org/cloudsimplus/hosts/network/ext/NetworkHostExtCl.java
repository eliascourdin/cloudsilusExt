package org.cloudsimplus.hosts.network.ext;

import lombok.Getter;
import lombok.Setter;
import org.cloudsimplus.core.CloudSimTag;
import org.cloudsimplus.hosts.HostSimple;
import org.cloudsimplus.hosts.HostSuitability;
import org.cloudsimplus.network.CloudletPacket;
import org.cloudsimplus.network.HostPacket;
import org.cloudsimplus.network.VmPacket;
import org.cloudsimplus.network.switches.ext.EdgeSwitchExt;
import org.cloudsimplus.resources.HarddriveStorage;
import org.cloudsimplus.resources.Pe;
import org.cloudsimplus.schedulers.cloudlet.network.CloudletTaskScheduler;
import org.cloudsimplus.schedulers.cloudlet.network.CloudletTaskSchedulerSimple;
import org.cloudsimplus.vms.Vm;

import java.util.ArrayList;
import java.util.List;

/**
 * Extensión de {@link HostSimple} para añadir funcionalidades extra.
 * Aquí puedes sobreescribir métodos o agregar nuevas propiedades
 * para tu simulación personalizada.
 */
public class NetworkHostExtCl extends HostSimple {

    @Getter
    @Setter
    private EdgeSwitchExt edgeSwitchExt;

    private final List<CloudletPacket> pktsToSendForLocalVms;

    @Getter
    private long totalDataTransferBytes;

    private final List<CloudletPacket> pktsToSendForExternalVms;

    private final List<CloudletPacket> cloudletPktsReceived;

    public NetworkHostExtCl(long ram, long bw, HarddriveStorage storage, List<Pe> peList) {
        super(ram, bw, storage, peList);
        cloudletPktsReceived = new ArrayList<>();
        pktsToSendForLocalVms = new ArrayList<>();
        pktsToSendForExternalVms = new ArrayList<>();
    }

    @Override
    public double updateProcessing(final double currentTime) {
        final double nextFinishingCloudletTime = super.updateProcessing(currentTime);
        receivePackets();
        sendAllPacketListsOfAllVms();

        return  nextFinishingCloudletTime;
    }


    private void receivePackets() {
        for (final CloudletPacket cloudletPkt : cloudletPktsReceived) {
            receivePacket(cloudletPkt);
        }

        cloudletPktsReceived.clear();
    }

    private void receivePacket(final CloudletPacket cloudletPacket) {
        cloudletPacket.setReceiveTime(getSimulation().clock());
        final Vm destinationVm = cloudletPacket.getDestination().getVm();
        if(getVmList().contains(destinationVm)){
            final CloudletTaskScheduler taskScheduler = getPacketScheduler(destinationVm);
            //taskScheduler.addPacketToListOfPacketsSentFromVm(vmPacket);
            LOGGER.trace(
                "{}: {}: {} received pkt with {} bytes from {} in {} and forwarded it to {} in {}",
                getSimulation().clockStr(), getClass().getSimpleName(), this,
                cloudletPacket.getSize(), cloudletPacket.getSource(), cloudletPacket.getSource().getVm(),
                cloudletPacket.getDestination(), cloudletPacket.getDestination().getVm());
            return;
        }

        LOGGER.warn(
            "{}: {}: Destination {} was not found inside {}",
            getSimulation().clockStr(), getClass(), cloudletPacket.getDestination(), this);
    }



    /**
     * Gets all packet lists from all VMs placed into the Host and sends them all.
     * It checks whether a packet belongs to a local VM or to a VM hosted on another machine.
     */
    private void sendAllPacketListsOfAllVms() {
        getVmList().forEach(this::collectAllPacketsToSendFromVm);
        sendPacketsToLocalVms();
        sendPacketsToExternalVms();
    }

    /**
     * Gets the packets from the local packets buffer and sends them to VMs inside this host.
     */
    private void sendPacketsToLocalVms() {
        for (final CloudletPacket cloudletPkt : pktsToSendForLocalVms) {
            cloudletPkt.setSendTime(cloudletPkt.getReceiveTime());
            cloudletPkt.setReceiveTime(getSimulation().clock());
            final Vm destinationVm = cloudletPkt.getDestination().getVm();
            // insert the packet in the received list
            //getPacketScheduler(destinationVm).addPacketToListOfPacketsSentFromVm(cloudletPkt);
        }

        if (!pktsToSendForLocalVms.isEmpty()) {
            for (final Vm vm : getVmList()) {
                vm.updateProcessing(getVmScheduler().getAllocatedMips(vm));
            }
        }

        pktsToSendForLocalVms.clear();
    }

    /**
     * Sends packets from the local packets buffer to VMs outside this host.
     */
    private void sendPacketsToExternalVms() {
        for (final CloudletPacket pkt : pktsToSendForExternalVms) {
           // final double delay = edgeSwitchExt.downlinkTransferDelay(pkt, pktsToSendForExternalVms.size());
            totalDataTransferBytes += pkt.getSize();

            //getSimulation().send(
                //getDatacenter(), getEdgeSwitchExt(),
                //delay, CloudSimTag.NETWORK_EVENT_UP, pkt);
        }

        pktsToSendForExternalVms.clear();
    }

    private CloudletTaskScheduler getPacketScheduler(final Vm vm) {
        return vm.getCloudletScheduler().getTaskScheduler();
    }

    /**
     * {@inheritDoc}
     *
     * <p><b>It also creates and sets a {@link CloudletTaskScheduler} for each
     * Vm that doesn't have one already.</b></p>
     * @param vm {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public HostSuitability createVm(final Vm vm) {
        final HostSuitability suitability = super.createVm(vm);
        setPacketScheduler(vm);
        return suitability;
    }

    private void setPacketScheduler(final Vm vm) {
        final var scheduler = vm.getCloudletScheduler();
        if(!scheduler.isThereTaskScheduler()){
            scheduler.setTaskScheduler(new CloudletTaskSchedulerSimple());
        }
    }

    /**
     * Collects all packets inside a specific packet list from a Vm, so that those packets can be sent further on.
     *
     * @param sourceVm the VM from where the packets will be sent
     */
    private void collectAllPacketsToSendFromVm(final Vm sourceVm) {
        //final CloudletTaskScheduler taskScheduler = getVmPacketScheduler(sourceVm);
       // for (final VmPacket vmPkt : taskScheduler.getVmPacketsToSend()) {
           // collectPacketToSendFromVm(vmPkt);
        }

        //taskScheduler.clearVmPacketsToSend();
    //}

    /**
     * Collects a specific packet from a given Vm to join with other packets to be sent.
     *
     * @param vmPkt a packet to be sent from a Vm to another one
     //* @see #collectAllPacketsToSendFromVm(Vm)
     */
   // private void collectPacketToSendFromVm(final VmPacket vmPkt) {
       // final var hostPkt = new HostPacket(this, vmPkt);
       // final var receiverVm = vmPkt.getDestination();

        // If the VM is inside this Host, the packet doesn't travel through the network
        //final var pktsToSendList = getVmList().contains(receiverVm) ? pktsToSendForLocalVms : pktsToSendForExternalVms;
       // pktsToSendList.add(hostPkt);
    //}

    /**
     * Adds a packet to the list of received packets
     * to further submit them to the respective target VMs and Cloudlets.
     *
     * @param hostPacket received network packet
     */
    //public void addReceivedNetworkPacket(final HostPacket hostPacket){
       // hostPktsReceived.add(hostPacket);
    //}

   // public void connectToSwitch(EdgeSwitchExt edgeSwitchExt) {
     //   this.edgeSwitchExt = edgeSwitchExt;
    //}
}





