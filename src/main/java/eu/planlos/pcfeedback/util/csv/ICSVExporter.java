package eu.planlos.pcfeedback.util.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract interface ICSVExporter {

	static final Logger log = LoggerFactory.getLogger(ICSVExporter.class);

	abstract List<Object> createRecord(Object object) throws InvalidObjectException;
	abstract String[] getHeader();
	
	public default void writeCSV(List<?> objectList, PrintWriter writer) {
		
		CSVFormat csvFile = createHeader();
		CSVPrinter csvPrinter = null;

		try {
			csvPrinter = new CSVPrinter(new BufferedWriter(writer), csvFile);
			
			for (Object object : objectList) {
				
				List<Object> record = createRecord(object);
				
				csvPrinter.printRecord(record);
				csvPrinter.flush();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
				writer.close();
				csvPrinter.close();
			} catch (IOException e) {
				log.error("Error while flushing/closing fileWriter/csvPrinter !!!");
			}
		}
	}

	private CSVFormat createHeader() {
		return CSVFormat.EXCEL.withHeader(getHeader()).withAutoFlush(true).withDelimiter(';');
	}
}