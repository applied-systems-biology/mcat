package org.hkijena.mcat.api.datainterfaces;

import java.util.Arrays;
import java.util.List;

import org.hkijena.mcat.api.MCATDataInterface;
import org.hkijena.mcat.api.MCATDataSlot;
import org.hkijena.mcat.extension.datatypes.ClusterAbundanceData;

public class MCATClusterAbundanceDataInterface implements MCATDataInterface  {
	private MCATDataSlot clusterAbundance = new MCATDataSlot("cluster-abundance", ClusterAbundanceData.class);
	
	public MCATClusterAbundanceDataInterface() {

	}

	public MCATClusterAbundanceDataInterface(MCATClusterAbundanceDataInterface other) {
		this.clusterAbundance = new MCATDataSlot(other.getClusterAbundance());
	}

	public MCATDataSlot getClusterAbundance() {
		return clusterAbundance;
	}

	@Override
	public List<MCATDataSlot> getSlots() {
		return Arrays.asList(clusterAbundance);
	}
}
