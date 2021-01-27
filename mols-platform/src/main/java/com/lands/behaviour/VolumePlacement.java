package com.lands.behaviour;

import java.util.List;

import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.log4j.Logger;

import com.lands.valuation.ValuationModel;

public class VolumePlacement implements NodeServicePolicies.OnCreateNodePolicy {
    
    // Dependencies
    private NodeService nodeService;
    private PolicyComponent policyComponent;

    // Behaviours
    private Behaviour onCreateNode;

    private Logger logger = Logger.getLogger(VolumePlacement.class);

    public void init() {

        if (logger.isDebugEnabled()) logger.debug("Initializing volume placement behaviors");
        // Create behaviours
        this.onCreateNode = new JavaBehaviour(this, "onCreateNode", NotificationFrequency.EVERY_EVENT);

        // Bind behaviours to node policies
        this.policyComponent.bindClassBehaviour(
            QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
            QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.TYPE_VALUATION),
            this.onCreateNode
        );

    }

    public void onCreateNode(ChildAssociationRef childAssocRef) {
        if (logger.isDebugEnabled()) logger.debug("Inside onCreateNode");
        attachVolumeNumber(childAssocRef);
        attachReferenceNumber(childAssocRef);
    }

    public void attachVolumeNumber(ChildAssociationRef childAssocRef){

        if (logger.isDebugEnabled()) logger.debug("Inside attachVolumeNumber");
        NodeRef parentRef = getParentNoderef(childAssocRef);

        if(nodeService.exists(parentRef)&& IsVolumeHolder(parentRef)){
           int parent_volume_number = (Integer)nodeService.getProperty(parentRef,QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.PROP_VOLUME_NO));
            nodeService.setProperty(
                            childAssocRef.getChildRef(),
                            QName.createQName(
                                ValuationModel.NAMESPACE_VALUATION_MODEL,
                                ValuationModel.PROP_VOLUME_NO),
                                parent_volume_number);
            if (logger.isDebugEnabled()) logger.debug("Property set");  
            return;

        }else{
            return;
        }

    }

    public void attachReferenceNumber(ChildAssociationRef childAssocRef){
        if (logger.isDebugEnabled()) logger.debug("Inside attachReferenceNumber");
        NodeRef parentRef = getParentNoderef(childAssocRef);

        if(nodeService.exists(parentRef)&& HasHolderReference(parentRef)){
           String parent_ref_number = nodeService.getProperty(parentRef,QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.PROP_REFERENCE_NUMBER)).toString();
            nodeService.setProperty(
                            childAssocRef.getChildRef(),
                            QName.createQName(
                                ValuationModel.NAMESPACE_VALUATION_MODEL,
                                ValuationModel.PROP_REFERENCE_NUMBER),
                                parent_ref_number);
            if (logger.isDebugEnabled()) logger.debug("Property set");  
            return;

        }else{
            return;
        }
    }

    public NodeRef getParentNoderef(ChildAssociationRef childAssocRef){
        NodeRef parentRef = childAssocRef.getParentRef();
        return parentRef;
    }

    public boolean IsVolumeHolder(NodeRef parentRef){
        return nodeService.hasAspect(parentRef, QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.ASPECT_REPORT_VOLUME));
    }

    private boolean HasHolderReference(NodeRef parentRef){
        return nodeService.hasAspect(parentRef, QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.ASPECT_REPORT_HOLDER_DETAILS));
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }


	public PolicyComponent getPolicyComponent() {
		return policyComponent;
	}


	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

}