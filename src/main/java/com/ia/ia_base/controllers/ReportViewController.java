package com.ia.ia_base.controllers;

public class ReportViewController extends BaseController{
    public void openGenerateReport(){
        openNewWindow("views/internal_views/report-generate-view.fxml","Generate New Report");
    }
}
