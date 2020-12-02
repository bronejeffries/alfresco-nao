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
import java.util.Date;
import java.text.SimpleDateFormat;

public class CreateVolumeFolderName implements NodeServicePolicies.OnCreateNodePolicy,NodeServicePolicies.OnUpdatePropertiesPolicy {
    
    // Dependencies
    private NodeService nodeService;
    private PolicyComponent policyComponent;

    // Behaviours
    private Behaviour onCreateNode, onUpdateNodeProperties;

    private Logger logger = Logger.getLogger(CreateVolumeFolderName.class);

    public void init() {

        if (logger.isDebugEnabled()) logger.debug("Initializing create folder name behaviors");
        // Create behaviours
        this.onCreateNode = new JavaBehaviour(this, "onCreateNode", NotificationFrequency.TRANSACTION_COMMIT);
        this.onUpdateNodeProperties = new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT);

        // Bind behaviours to node policies
        this.policyComponent.bindClassBehaviour(
            QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
            QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.TYPE_REPORT_VOLUME),
            this.onCreateNode
        );

        this.policyComponent.bindClassBehaviour(
            QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
            QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.TYPE_REPORT_VOLUME),
            this.onUpdateNodeProperties
        );

        // this.policyComponent.bindClassBehaviour(
        //     QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
        //     QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.TYPE_PROJECT_REPORT),
        //     this.onCreateNode
        // );

        // this.policyComponent.bindClassBehaviour(
        //     QName.createQName(NamespaceService.ALFRESCO_URI, "onUpdateProperties"),
        //     QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.TYPE_PROJECT_REPORT),
        //     this.onUpdateNodeProperties
        // );


    }

    public void onCreateNode(ChildAssociationRef childAssocRef) {
        if (logger.isDebugEnabled()) logger.debug("Inside onCreateNode");
        
        createNameForNode(childAssocRef.getChildRef());
    }

    public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after)
    {
        createNameForNode(nodeRef);
    }

    public void createNameForNode(NodeRef nodeRef){

        if(nodeService.exists(nodeRef)){
            
            String close_date  = alfrescoformatDatetoString((Date)nodeService.getProperty(nodeRef,QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.PROP_CLOSE_DATE)));
            String open_date  = alfrescoformatDatetoString((Date)nodeService.getProperty(nodeRef,QName.createQName(ValuationModel.NAMESPACE_VALUATION_MODEL, ValuationModel.PROP_OPEN_DATE)));
            String folder_name = open_date + ValuationModel.EXTRA_DIVIDER + close_date;
            nodeService.setProperty(
                            nodeRef,
                            QName.createQName(
                                ValuationModel.NAMESPACE_CONTENT_MODEL,
                                ValuationModel.PROP_CM_NAME),
                                folder_name);
            if (logger.isDebugEnabled()) logger.debug("Property set");  
            return;

        }else{
            return;
        }
    }

    public static String alfrescoformatDatetoString(Date date){

        String formatedDate = null;
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E dd MMMMM yyyy");
        
        try {
            
            /*make date object out of the string.
            result has no time*/
            formatedDate = simpleDateFormat.format(date);

        } catch (Exception e) {

            //TODO: handle exception
            // System.out.println(e.getMessage());
            // e.printtrace();
            formatedDate = "";
        }

        return formatedDate;
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