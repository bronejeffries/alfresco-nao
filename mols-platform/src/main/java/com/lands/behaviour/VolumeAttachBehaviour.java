package com.lands.behaviour;

import java.util.List;

import java.io.Serializable;
import java.util.Map;

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

public class VolumeAttachBehaviour implements NodeServicePolicies.OnUpdatePropertiesPolicy{

    // Dependencies
    private NodeService nodeService;
    private PolicyComponent policyComponent;

    // Behaviours
    private Behaviour onUpdateNodeProperties;

    // Logger
    private Logger logger = Logger.getLogger(VolumeAttachBehaviour.class);

    public void init() {

        if (logger.isDebugEnabled()) logger.debug("Initializing volume attach behaviors");
        System.out.println("Initializing volume attach behaviors");
        // Create behaviours
        this.onUpdateNodeProperties = new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT);

        // Bind behaviours to node policies
        this.policyComponent.bindClassBehaviour(
            QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
            QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.TYPE_VALUATION),
            this.onUpdateNodeProperties
        );

    }

    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after)
    {

        System.out.println("Inside onUpdateProperties ");
        if (logger.isDebugEnabled()) logger.debug("Inside onUpdateProperties ");

        NodeRef parent = getParentNodeRef(nodeRef);
        if(IsVolumeHolder(parent)){
            // implementation for this condition
            if (logger.isDebugEnabled()) logger.debug("parent is a volume holder");
            System.out.println("parent is a volume holder");
        }else{
            if (logger.isDebugEnabled()) logger.debug("parent not volume holder");
            System.out.println("parent not volume holder");
            return;
        }

        // implemented here
        QName volume_qname = QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.PROP_VOLUME_NO);
        Serializable child_volume_number = after.get(volume_qname);
        Integer child_v_no = null; 
        if(child_volume_number!=null){
            child_v_no = Integer.valueOf(child_volume_number.toString());
        }
        if(child_v_no!=getParentVolumeNumber(parent)){

            // implementation of this condition
            if (logger.isDebugEnabled()) logger.debug("parent volume no is not the same as child");
            System.out.println("parent volume no is not the same as child");

        }else{
            return;
        }

        // here
        nodeService.setProperty(
                            nodeRef,
                            volume_qname,
                                getParentVolumeNumber(parent));
        return;


    }

    private NodeRef getParentNodeRef(NodeRef nodeRef){
        ChildAssociationRef childAssociationRef = nodeService.getPrimaryParent(nodeRef);
        NodeRef parent = childAssociationRef.getParentRef();
        return parent;
    }

    private Integer getParentVolumeNumber(NodeRef nodeRef){
        Integer parent_volume_number = (Integer)nodeService.getProperty(nodeRef,QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.PROP_VOLUME_NO));
        return parent_volume_number;
    }

    private boolean IsVolumeHolder(NodeRef parentRef){
        return nodeService.hasAspect(parentRef, QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.ASPECT_REPORT_VOLUME));
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