package com.lands.valuation;

public interface ValuationModel {
    // Namespaces
    public static final String NAMESPACE_VALUATION_MODEL = "http://www.mols.com/model/mols/1.0";
    public static final String NAMESPACE_CONTENT_MODEL = "http://www.alfresco.org/model/content/1.0";
    
    // types
    public static final String TYPE_VALUATION = "valuation_report";
    public static final String TYPE_REPORT_VOLUME = "report_volume";
    public static final String TYPE_REPORT_HOLDER = "reports_holder";
    public static final String TYPE_PROJECT_REPORT = "project_report";
    public static final String TYPE_PROJECT_REPORT_FOLDER = "infrast_reports_holder";

    // aspects 
    public static final String ASPECT_INSTRUCTION_LETTER_ASPECT = "instruction_letter_details";
    public static final String ASPECT_REPORT_DETAILS = "report_details";
    public static final String ASPECT_REPORT_HOLDER_DETAILS = "report_holder_details";
    public static final String ASPECT_REPORT_VOLUME = "reports_vol";

    // properties
    public static final String PROP_VOLUME_NO = "vol_number";
    public static final String PROP_REFERENCE_NUMBER = "holder_reference_no";
    public static final String PROP_PROJECT_VOLUME = "volume";
    public static final String PROP_APPROVAL_DATE = "report_approval_date";
    public static final String PROP_SUBJECT = "report_subject";
    public static final String PROP_AGENCY = "report_agency";
    public static final String PROP_CM_NAME = "name";
    public static final String PROP_CLOSE_DATE = "close_date";
    public static final String PROP_OPEN_DATE = "open_date";
    public static final String PROP_PROJECT_NAME = "project_name";

    // extra
    public static final String EXTRA_DIVIDER = "-";


}