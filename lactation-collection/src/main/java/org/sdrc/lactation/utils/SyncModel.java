package org.sdrc.lactation.utils;

import java.util.List;

import org.sdrc.lactation.domain.LactationUser;
import org.sdrc.lactation.domain.Patient;

/**
 * 
 * @author Naseem Akhtar (naseem@sdrc.co.in) on 9th February 2018 19:10. This
 *         model will be used to receive data from the mobile.
 * @author Ratikanta
 */

public class SyncModel {

	private List<LactationUser> users;
	private List<Patient> patients;
	private List<BFExpressionModel> bfExpressions;
	private List<FeedExpressionModel> feedExpressions;
	private List<BFSPModel> bfsps;
	private List<BFPDModel> bfpds;

	public List<LactationUser> getUsers() {
		return users;
	}

	public void setUsers(List<LactationUser> users) {
		this.users = users;
	}

	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public List<BFExpressionModel> getBfExpressions() {
		return bfExpressions;
	}

	public void setBfExpressions(List<BFExpressionModel> bfExpressions) {
		this.bfExpressions = bfExpressions;
	}

	public List<FeedExpressionModel> getFeedExpressions() {
		return feedExpressions;
	}

	public void setFeedExpressions(List<FeedExpressionModel> feedExpressions) {
		this.feedExpressions = feedExpressions;
	}

	public List<BFSPModel> getBfsps() {
		return bfsps;
	}

	public void setBfsps(List<BFSPModel> bfsps) {
		this.bfsps = bfsps;
	}

	public List<BFPDModel> getBfpds() {
		return bfpds;
	}

	public void setBfpds(List<BFPDModel> bfpds) {
		this.bfpds = bfpds;
	}

}
