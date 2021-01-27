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

public class ReferenceAttachBehaviour implements NodeServicePolicies.OnUpdatePropertiesPolicy{


    // Dependencies
    private NodeService nodeService;
    private PolicyComponent policyComponent;

    // Behaviours
    private Behaviour onUpdateNodeProperties;

    // Logger
    private Logger logger = Logger.getLogger(ReferenceAttachBehaviour.class);

    public void init() {

        if (logger.isDebugEnabled()) logger.debug("Initializing volume attach behaviors");
        System.out.println("Initializing reference attach behaviors");
        // Create behaviours
        this.onUpdateNodeProperties = new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT);

        // Bind behaviours to node policies
        this.policyComponent.bindClassBehaviour(
            QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
            QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.TYPE_VALUATION),
            this.onUpdateNodeProperties
        );

        // this.policyComponent.bindClassBehaviour(
        //     QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
        //     QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.TYPE_PROJECT_REPORT),
        //     this.onUpdateNodeProperties
        // );

        this.policyComponent.bindClassBehaviour(
            QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
            QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.TYPE_REPORT_VOLUME),
            this.onUpdateNodeProperties
        );

    }

    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after)
    {
        System.out.println("Inside onUpdateProperties ");
        if (logger.isDebugEnabled()) logger.debug("Inside onUpdateProperties ");

        NodeRef parent = getParentNodeRef(nodeRef);

        if(HasHolderReference(parent)){
            // implementation for this condition
            if (logger.isDebugEnabled()) logger.debug("parent has  reference");
            System.out.println("parent has reference");
        }else{
            if (logger.isDebugEnabled()) logger.debug("parent has no reference");
            System.out.println("parent has no reference");
            return;
        }

        // implemented here
        QName refNo_qname = QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.PROP_REFERENCE_NUMBER);
        Serializable child_ref_number = after.get(refNo_qname);
        String child_ref = ""; 
        if(child_ref_number!=null){
            child_ref = child_ref_number.toString();
        }

        if(child_ref!=getParentRefNumber(parent)){

            // implementation of this condition
            if (logger.isDebugEnabled()) logger.debug("parent ref no is not the same as child");
            System.out.println("parent ref no is not the same as child");

        }else{
            return;
        }

        // here
        nodeService.setProperty(
                            nodeRef,
                            refNo_qname,
                                getParentRefNumber(parent));

    }

    private NodeRef getParentNodeRef(NodeRef nodeRef){
        ChildAssociationRef childAssociationRef = nodeService.getPrimaryParent(nodeRef);
        NodeRef parent = childAssociationRef.getParentRef();
        return parent;
    }

    private boolean HasHolderReference(NodeRef parentRef){
        return nodeService.hasAspect(parentRef, QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.ASPECT_REPORT_HOLDER_DETAILS));
    }

    private String getParentRefNumber(NodeRef nodeRef){
        String parent_ref_number = nodeService.getProperty(nodeRef,QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.PROP_REFERENCE_NUMBER)).toString();
        return parent_ref_number;
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