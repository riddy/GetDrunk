package com.hackathon.getdrunk;

import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

public class LabelPrinter {
	private HashMap<String, String> searchReplacePairs = new HashMap<String, String>();
	private String wkhtmlPath = "C:/mway/projects/meshgateway/WKHTMLTOPDF/wkhtmltopdf.exe";
	private String workingDir = "C:/mway/projects/meshgateway/labels/";
	
	public static final float POINTS_PER_MM = 1 / (10 * 2.54f) * 71;
	private String templateName;
	private boolean rendered;
	
	
	public LabelPrinter() {
		
	}
	
	public void setValue(String key, String value){
		searchReplacePairs.put(key, value);
	}
	
	public void renderDocument(String templateName, float width, float height) throws Exception {
		
		this.templateName = templateName;
		
		
		//Read the template into a String
		String template = FileUtils.readFileToString(new File(workingDir+templateName), StandardCharsets.UTF_8);
		
		//Set working dir in template
		template = template.replaceAll("\\./", workingDir);
		
		//Search and replace all occurences in the template
		for (HashMap.Entry<String, String> entry : searchReplacePairs.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    
		    template = template.replaceAll("%"+key+"%", value);
		}
		
		
		ProcessBuilder ps = new ProcessBuilder(
				wkhtmlPath,
				"--page-height", height+"",
				"--page-width", width+"",
				"--margin-bottom", "0",
				"--margin-top", "0",
				"--margin-left", "0",
				"--margin-right", "0",
				"--disable-smart-shrinking",
				"--grayscale",
				"--dpi", "300",
				"--allow", workingDir,
				"-",
				workingDir+templateName+".pdf");
		ps.directory(new File(workingDir));
		ps.redirectErrorStream(true);
		Process pr = ps.start();
		
		//Pipe our template into the stdin of wkhtmltoPDF
		OutputStream stdin = pr.getOutputStream();
		stdin.write(template.getBytes());
        stdin.flush();
        stdin.close();
		
        //Read the output stream
		BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));

		while(true){
			int c = in.read();
			if(c == -1) break;
			
			System.out.print(Character.toString((char) c));
		}
		
		pr.waitFor();
		in.close();
		
		int exitValue = pr.exitValue();
		if (exitValue != 0) {
	        throw new Exception("Error while rendering");
		}
		
		rendered = true;
	}
	
	public void printDocument(String printerName, String printMediaName) throws Exception{
		
		if(!MasterBridge.ENABLE_PRINT) return;
		
		PDDocument document = null;
		
		if(!rendered) throw new Exception("Document has not been rendered");
		
		//Go through all printers and find the right one
		DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
	    PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
	    PrintService[] printServices = PrintServiceLookup.lookupPrintServices(flavor, patts);
	    System.out.println("Available printers: " + Arrays.asList(printServices));
	    PrintService printService = null;
	    for (PrintService ps : printServices) {
	        if (ps.getName().equals(printerName)) {
	            printService = ps;
	            break;
	        }
	    }
	    if (printService == null) throw new IllegalStateException("Printer not found");
	    
	    //Create a new Print Job
	    PrinterJob job = PrinterJob.getPrinterJob();
	    
	    //Find the correct paper size for this label
	    Media printMedia = null;
	    Media[] medias = (Media[])printService.getSupportedAttributeValues(Media.class, null, null);
	    for (Media media : medias){
	        if (media instanceof MediaSizeName){
	        	System.out.println(media.toString());
	        	if(media.toString().equals(printMediaName)){
	        		printMedia = media;
	        		System.out.println("Using Print Media: " + media.toString());
	        	}
	        }
	    }
	    if(printMedia == null) throw new IllegalStateException("Print Media not found");
	    
	    //Add some printing attributes to our print job
	    PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
	    attributes.add(OrientationRequested.LANDSCAPE);
	    attributes.add(printMedia);

	    try {	    	
		    document = PDDocument.load(new File(workingDir+templateName+".pdf"));
		    
		    while(document.getNumberOfPages() > 1){
		    	document.removePage(1);
		    }
		    
		    job.setPageable(new PDFPageable(document));
	        job.setPrintService(printService);
	        job.print(attributes);
	        
	        document.close();
	        
	    } catch(Exception e){
	    	e.printStackTrace();
	    } finally {
	    	if(document != null) document.close();
	    }
	}
	
	public static void printBeaconLabel(String serialNumber){
		try {
			LabelPrinter beaconLabelPrinter = new LabelPrinter();
	
			beaconLabelPrinter.setValue("SERIAL", serialNumber.toUpperCase());
			
			beaconLabelPrinter.renderDocument("beacon_template.html", 32, 25.4f);
			beaconLabelPrinter.printDocument("Leitz Icon", "SmartBeacon Label");
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void printMeshgatewayLabel(String serialNumber, String modelName){
		try {
			LabelPrinter gwLabelPrinter = new LabelPrinter();
			
			gwLabelPrinter.setValue("URL", "http://meshgw-"+serialNumber.toLowerCase()+".local");
			gwLabelPrinter.setValue("MODEL", modelName);
			gwLabelPrinter.setValue("SERIAL", serialNumber.toUpperCase());
			
			gwLabelPrinter.renderDocument("meshgateway_template.html", 80, 25.4f);
			gwLabelPrinter.printDocument("Leitz Icon", "MeshGateway Label");
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void printGlas(String award){
		try {
			LabelPrinter gwLabelPrinter = new LabelPrinter();

			gwLabelPrinter.setValue("AWARD", award);
						
			gwLabelPrinter.renderDocument("glas.html", 80.4f, 80.4f);
			gwLabelPrinter.printDocument("Leitz Icon", "*Continuous - 3.5\"");
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
