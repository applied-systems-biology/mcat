package org.hkijena.mcat.api.datainterfaces;

import java.util.Arrays;
import java.util.List;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.api.dataslots.ClusterAbundanceDataSlot;

public class MCATClusterAbundanceDataInterface implements MCATDataInterface  {
	private ClusterAbundanceDataSlot clusterAbundance = new ClusterAbundanceDataSlot("cluster-abundance");
	
	public MCATClusterAbundanceDataInterface() {

	}

	public MCATClusterAbundanceDataInterface(MCATClusterAbundanceDataInterface other) {
		this.clusterAbundance = new ClusterAbundanceDataSlot(other.getClusterAbundance());
	}

	public ClusterAbundanceDataSlot getClusterAbundance() {
		return clusterAbundance;
	}

	@Override
	public List<MCATDataSlot<?>> getSlots() {
		return Arrays.asList(clusterAbundance);
	}
}
